/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.rendering;

import java.util.concurrent.locks.Lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.vertical.VerticalProperties;

import com.enonic.cms.framework.util.GenericConcurrencyLock;
import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.CacheObjectSettings;
import com.enonic.cms.core.CacheSettings;
import com.enonic.cms.core.CachedObject;
import com.enonic.cms.core.RequestParameters;
import com.enonic.cms.core.SitePropertiesService;
import com.enonic.cms.core.SiteURLResolver;
import com.enonic.cms.core.plugin.PluginManager;
import com.enonic.cms.core.portal.PortalInstanceKey;
import com.enonic.cms.core.portal.PortalRenderingException;
import com.enonic.cms.core.portal.Ticket;
import com.enonic.cms.core.portal.WindowNotFoundException;
import com.enonic.cms.core.portal.cache.PageCacheService;
import com.enonic.cms.core.portal.datasource.DataSourceResult;
import com.enonic.cms.core.portal.datasource.DatasourceExecutor;
import com.enonic.cms.core.portal.datasource.DatasourceExecutorContext;
import com.enonic.cms.core.portal.datasource.DatasourceExecutorFactory;
import com.enonic.cms.core.portal.datasource.Datasources;
import com.enonic.cms.core.portal.datasource.DatasourcesType;
import com.enonic.cms.core.portal.instruction.PostProcessInstructionContext;
import com.enonic.cms.core.portal.instruction.PostProcessInstructionExecutor;
import com.enonic.cms.core.portal.instruction.PostProcessInstructionProcessor;
import com.enonic.cms.core.portal.livetrace.InstructionPostProcessingTrace;
import com.enonic.cms.core.portal.livetrace.InstructionPostProcessingTracer;
import com.enonic.cms.core.portal.livetrace.LivePortalTraceService;
import com.enonic.cms.core.portal.livetrace.ViewTransformationTrace;
import com.enonic.cms.core.portal.livetrace.ViewTransformationTracer;
import com.enonic.cms.core.portal.livetrace.WindowRenderingTrace;
import com.enonic.cms.core.portal.livetrace.WindowRenderingTracer;
import com.enonic.cms.core.portal.page.PageRequestFactory;
import com.enonic.cms.core.portal.rendering.portalfunctions.PortalFunctionsContext;
import com.enonic.cms.core.portal.rendering.portalfunctions.PortalFunctionsFactory;
import com.enonic.cms.core.portal.rendering.tracing.PagePortletTraceInfo;
import com.enonic.cms.core.portal.rendering.tracing.RenderTrace;
import com.enonic.cms.core.portal.rendering.tracing.TraceMarkerHelper;
import com.enonic.cms.core.portal.rendering.viewtransformer.PortletXsltViewTransformer;
import com.enonic.cms.core.portal.rendering.viewtransformer.StringTransformationParameter;
import com.enonic.cms.core.portal.rendering.viewtransformer.TemplateParameterTransformationParameter;
import com.enonic.cms.core.portal.rendering.viewtransformer.TransformationParameterOrigin;
import com.enonic.cms.core.portal.rendering.viewtransformer.TransformationParams;
import com.enonic.cms.core.portal.rendering.viewtransformer.ViewTransformationResult;
import com.enonic.cms.core.resource.ResourceFile;
import com.enonic.cms.core.resource.ResourceService;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.service.DataSourceService;
import com.enonic.cms.core.structure.TemplateParameter;
import com.enonic.cms.core.structure.TemplateParameterType;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import com.enonic.cms.core.structure.page.Window;
import com.enonic.cms.core.structure.page.WindowKey;

import com.enonic.cms.core.stylesheet.StylesheetNotFoundException;

/**
 * Apr 17, 2009
 */
public class WindowRenderer
{
    private static final Logger LOG = LoggerFactory.getLogger( WindowRenderer.class );

    private static final String DUMMY_XML = "<?xml version=\"1.0\" encoding=\"utf-8\"?><dummy/>";

    private PageCacheService pageCacheService;

    private DatasourceExecutorFactory dataSourceExecutorFactory;

    private WindowRendererContext context;

    private PortletXsltViewTransformer portletXsltViewTransformer;

    private ResourceService resourceService;

    private VerticalProperties verticalProperties;

    private SitePropertiesService sitePropertiesService;

    private SiteURLResolver siteURLResolver;

    private RequestParameters requestParameters;

    private PostProcessInstructionExecutor postProcessInstructionExecutor;

    private LivePortalTraceService liveTraceService;

    private static GenericConcurrencyLock<WindowCacheKey> concurrencyLock = GenericConcurrencyLock.create();

    private DataSourceService dataSourceService;

    private PluginManager pluginManager;

    /**
     * The window rendering trace for this window rendering.
     */
    private WindowRenderingTrace windowRenderingTrace;

    public WindowRenderer( WindowRendererContext windowRendererContext )
    {
        this.context = windowRendererContext;

        if ( windowRendererContext.getInvocationCache() == null )
        {
            throw new IllegalArgumentException( "Datasource invocation cache not set" );
        }
    }

    public RenderedWindowResult renderWindowInline( final WindowKey windowKey, final RequestParameters extraParams )
    {
        windowRenderingTrace = WindowRenderingTracer.startTracing( liveTraceService );

        try
        {
            if ( !context.isRenderedInline() )
            {
                throw new IllegalStateException(
                    "context is indicating that a render direct is expected, but render window inline was called" );
            }

            final Window window = context.getRegionsInPage().getWindowByKey( windowKey );
            if ( window == null )
            {
                throw new WindowNotFoundException( context.getSite().getKey(), context.getSitePath().getLocalPath(), windowKey );
            }

            WindowRenderingTracer.traceRequestedWindow( windowRenderingTrace, window );

            requestParameters = new RequestParameters( context.getSitePath().getRequestParameters() );
            for ( RequestParameters.Param param : extraParams.getParameters() )
            {
                requestParameters.setParam( param );
            }

            return doRenderWindow( window );
        }
        finally
        {
            WindowRenderingTracer.stopTracing( windowRenderingTrace, liveTraceService );
        }
    }

    public RenderedWindowResult renderWindowDirect( final WindowKey windowKey )
    {
        windowRenderingTrace = WindowRenderingTracer.startTracing( liveTraceService );

        try
        {
            if ( context.isRenderedInline() )
            {
                throw new IllegalStateException( "context is saying render inline, but render window direct was called" );
            }

            final Window window = context.getRegionsInPage().getWindowByKey( windowKey );
            if ( window == null )
            {
                throw new WindowNotFoundException( context.getSite().getKey(), context.getSitePath().getLocalPath(), windowKey );
            }

            WindowRenderingTracer.traceRequestedWindow( windowRenderingTrace, window );

            requestParameters = context.getSitePath().getRequestParameters();
            RenderedWindowResult result = doRenderWindow( window );
            result.setContent( result.getContent().replace( Ticket.getPlaceholder(), context.getTicketId() ) );
            return result;
        }
        finally
        {
            WindowRenderingTracer.stopTracing( windowRenderingTrace, liveTraceService );
        }
    }

    private RenderedWindowResult doRenderWindow( final Window window )
    {
        if ( window == null )
        {
            throw new IllegalArgumentException( "Given window cannot be null" );
        }

        final UserEntity executor = resolveRunAsUser( window );

        WindowRenderingTracer.traceRenderer( windowRenderingTrace, executor );

        final CacheSettings portletCacheSettings = window.getPortlet().getCacheSettings( pageCacheService.getDefaultTimeToLive() );
        enterTrace( window, executor, portletCacheSettings );

        try
        {
            final boolean useCache = resolveUseCache( portletCacheSettings );
            if ( !useCache )
            {
                final RenderedWindowResult windowResult = doExecuteDatasourcesAndTransformView( window, executor );
                WindowRenderingTracer.traceUsedCachedResult( windowRenderingTrace, false );
                return cloneAndExecutePostProcessInstructions( windowResult );
            }

            final WindowCacheKey cacheKey = resolveCacheKey( window, executor.getKey() );
            final RenderedWindowResult windowResult;

            final Lock locker = concurrencyLock.getLock( cacheKey );
            try
            {
                locker.lock();

                // see if window result is in cache
                final CachedObject cachedPortletHolder = pageCacheService.getCachedPortletWindow( cacheKey );
                if ( cachedPortletHolder != null )
                {
                    windowResult = (RenderedWindowResult) cachedPortletHolder.getObject();
                    WindowRenderingTracer.traceUsedCachedResult( windowRenderingTrace, true );
                }
                else
                {
                    // window not in cache, need to render...
                    windowResult = doExecuteDatasourcesAndTransformView( window, executor );
                    WindowRenderingTracer.traceUsedCachedResult( windowRenderingTrace, false );

                    // register the rendered window in the cache
                    if ( windowResult.isErrorFree() )
                    {
                        final CachedObject newCachedPortletHolder = pageCacheService.cachePortletWindow( cacheKey, windowResult,
                                                                                                         CacheObjectSettings.createFrom(
                                                                                                             portletCacheSettings ) );
                        windowResult.setExpirationTimeInCache( newCachedPortletHolder.getExpirationTime() );
                    }
                }
            }
            finally
            {
                locker.unlock();
            }

            return cloneAndExecutePostProcessInstructions( windowResult );
        }
        finally
        {
            exitTrace();
        }
    }

    private RenderedWindowResult cloneAndExecutePostProcessInstructions( final RenderedWindowResult evaluatedPortlet )
    {
        RenderedWindowResult clonedRenderedWindowResult = evaluatedPortlet.clone();

        String content = clonedRenderedWindowResult.getContent();

        String resolvedContent = executePostProcessInstructions( content, clonedRenderedWindowResult.getOutputMethod() );

        clonedRenderedWindowResult.setContent( resolvedContent );

        return clonedRenderedWindowResult;
    }

    private String executePostProcessInstructions( String pageMarkup, String outputMode )
    {
        final InstructionPostProcessingTrace instructionPostProcessingTrace =
            InstructionPostProcessingTracer.startTracingForWindow( liveTraceService );
        try
        {
            PostProcessInstructionContext postProcessInstructionContext = new PostProcessInstructionContext();
            postProcessInstructionContext.setSite( context.getSite() );
            postProcessInstructionContext.setEncodeImageUrlParams( RenderTrace.isTraceOff() );
            postProcessInstructionContext.setHttpRequest( context.getHttpRequest() );
            postProcessInstructionContext.setPreviewContext( context.getPreviewContext() );
            postProcessInstructionContext.setInContextOfWindow( true );

            postProcessInstructionContext.setSiteURLResolverEnableHtmlEscaping( createSiteURLResolver( true ) );
            postProcessInstructionContext.setSiteURLResolverDisableHtmlEscaping( createSiteURLResolver( false ) );

            PostProcessInstructionProcessor postProcessInstructionProcessor =
                new PostProcessInstructionProcessor( postProcessInstructionContext, postProcessInstructionExecutor );

            return postProcessInstructionProcessor.processInstructions( pageMarkup );

        }
        finally
        {
            InstructionPostProcessingTracer.stopTracing( instructionPostProcessingTrace, liveTraceService );
        }
    }


    private SiteURLResolver createSiteURLResolver( boolean escapeHtmlParameterAmps )
    {
        SiteURLResolver siteURLResolver = new SiteURLResolver();
        siteURLResolver.setSitePropertiesService( sitePropertiesService );
        siteURLResolver.setHtmlEscapeParameterAmps( escapeHtmlParameterAmps );

        if ( context.getOverridingSitePropertyCreateUrlAsPath() != null )
        {
            siteURLResolver.setOverridingSitePropertyCreateUrlAsPath( context.getOverridingSitePropertyCreateUrlAsPath() );
        }

        return siteURLResolver;
    }

    private RenderedWindowResult doExecuteDatasourcesAndTransformView( final Window window, final UserEntity exectuor )
    {
        RenderedWindowResult portletResult;
        try
        {
            PageRequestFactory.getPageRequest().setCurrentPortletKey( window.getPortlet().getPortletKey() );
            DataSourceResult dataSourceResult = getDataSourceResult( window, exectuor );

            ViewTransformationResult portletViewTransformation;
            final ViewTransformationTrace trace = ViewTransformationTracer.startTracing( liveTraceService );
            try
            {
                portletViewTransformation = renderWindowView( window, dataSourceResult.getData(), trace );
                if ( window.getPortlet().getBorderKey() != null )
                {
                    portletViewTransformation = renderWindowBorderView( window, portletViewTransformation.getContent() );
                }
            }
            finally
            {
                PortalFunctionsFactory.get().removeContext();
                ViewTransformationTracer.stopTracing( trace, liveTraceService );
            }

            portletResult = new RenderedWindowResult();
            portletResult.setHttpContentType( portletViewTransformation.getHttpContentType() );
            portletResult.setContent( portletViewTransformation.getContent() );
            portletResult.setOutputMethod( portletViewTransformation.getOutputMethod() );
            if ( portletViewTransformation.getOutputEncoding() != null )
            {
                portletResult.setContentEncoding( portletViewTransformation.getOutputEncoding() );
            }
        }
        catch ( Exception e )
        {
            String message =
                "Error occured rendering window \"" + window.getPortlet().getName() + "\" (key " + window.getPortlet().getKey() +
                    ") while handling request to site path: " + context.getSitePath().asString();
            PortletErrorMessageMarkupCreator portletErrorMessageMarkupCreator = new PortletErrorMessageMarkupCreator();
            String errorMarkup = portletErrorMessageMarkupCreator.createMarkup( message, e );
            portletResult = new ErrorRenderPortletResult();
            portletResult.setHttpContentType( "text/html" );
            portletResult.setContent( errorMarkup );
            ErrorRenderPortletResult errorPortletResult = (ErrorRenderPortletResult) portletResult;
            errorPortletResult.setError( e );
            LOG.error( message, e );
        }
        finally
        {
            PageRequestFactory.getPageRequest().setCurrentPortletKey( null );
        }

        PagePortletTraceInfo portletTraceInfo = RenderTrace.getCurrentPageObjectTraceInfo();
        if ( portletTraceInfo != null )
        {
            TraceMarkerHelper.wrapResultWithPortletMarker( portletResult, portletTraceInfo );
        }

        portletResult.stripXHTMLNamespaces();

        return portletResult;
    }

    private ViewTransformationResult renderWindowView( final Window window, final XMLDocument xml, final ViewTransformationTrace trace )
    {
        if ( window.getPortlet().getXmlDataAsJDOMDocument().getRootElement().getChild( "datasources" ) == null )
        {
            throw new PortalRenderingException( "Datasources missing for portlet: " + window.getKey() );
        }

        final SiteURLResolver siteURLResolver = new SiteURLResolver();
        siteURLResolver.setOverridingSitePropertyCreateUrlAsPath( context.getOverridingSitePropertyCreateUrlAsPath() );
        siteURLResolver.setSitePropertiesService( sitePropertiesService );

        final PortalFunctionsContext portalFunctionsContext = new PortalFunctionsContext();
        portalFunctionsContext.setInvocationCache( context.getInvocationCache() );
        portalFunctionsContext.setSitePath( context.getSitePath() );
        portalFunctionsContext.setOriginalSitePath( context.getOriginalSitePath() );
        portalFunctionsContext.setSite( context.getSite() );
        portalFunctionsContext.setMenuItem( context.getMenuItem() );
        portalFunctionsContext.setEncodeURIs( context.isEncodeURIs() );
        portalFunctionsContext.setLocale( context.getLocale() );
        portalFunctionsContext.setPortalInstanceKey( resolvePortalInstanceKey( window ) );
        portalFunctionsContext.setRenderedInline( context.isRenderedInline() );
        portalFunctionsContext.setEncodeImageUrlParams( RenderTrace.isTraceOff() );
        portalFunctionsContext.setSiteURLResolver( siteURLResolver );

        try
        {
            PortalFunctionsFactory.get().setContext( portalFunctionsContext );

            final ResourceFile viewFile = resourceService.getResourceFile( window.getPortlet().getStyleKey() );
            if ( viewFile == null )
            {
                throw new StylesheetNotFoundException( window.getPortlet().getStyleKey() );
            }
            ViewTransformationTracer.traceView( viewFile.getPath(), trace );

            final TransformationParams transformationParams = new TransformationParams();
            for ( TemplateParameter templateParameter : window.getPortlet().getTemplateParameters().values() )
            {
                transformationParams.add(
                    new TemplateParameterTransformationParameter( templateParameter, TransformationParameterOrigin.PORTLET ) );
            }

            return portletXsltViewTransformer.transform( viewFile, transformationParams, xml );
        }
        finally
        {
            PortalFunctionsFactory.get().removeContext();
            ViewTransformationTracer.stopTracing( trace, liveTraceService );
        }
    }

    private ViewTransformationResult renderWindowBorderView( Window window, String contentToBorder )
    {

        TransformationParams viewParameters = new TransformationParams();

        for ( TemplateParameter templateParam : window.getPortlet().getBorderTemplateParameters().values() )
        {
            if ( TemplateParameterType.CONTENT.equals( templateParam.getType() ) )
            {
                viewParameters.add(
                    new StringTransformationParameter( templateParam.getName(), contentToBorder, TransformationParameterOrigin.BORDER ) );
            }
            else
            {
                viewParameters.add( new StringTransformationParameter( templateParam.getName(), templateParam.getValue(),
                                                                       TransformationParameterOrigin.BORDER ) );
            }
        }

        PortalInstanceKey portalInstanceKey = resolvePortalInstanceKey( window );

        PortalFunctionsContext portalFunctionsContext = new PortalFunctionsContext();
        portalFunctionsContext.setEncodeURIs( context.isEncodeURIs() );
        portalFunctionsContext.setInvocationCache( context.getInvocationCache() );
        portalFunctionsContext.setLocale( context.getLocale() );
        portalFunctionsContext.setMenuItem( context.getMenuItem() );
        portalFunctionsContext.setSitePath( context.getSitePath() );
        portalFunctionsContext.setOriginalSitePath( context.getOriginalSitePath() );
        portalFunctionsContext.setPortalInstanceKey( portalInstanceKey );
        portalFunctionsContext.setRenderedInline( context.isRenderedInline() );
        portalFunctionsContext.setEncodeImageUrlParams( RenderTrace.isTraceOff() );
        portalFunctionsContext.setSite( context.getSite() );
        portalFunctionsContext.setSiteURLResolver( resolveSiteURLResolver() );

        PortalFunctionsFactory.get().setContext( portalFunctionsContext );

        ResourceFile viewFile = resourceService.getResourceFile( window.getPortlet().getBorderKey() );
        if ( viewFile == null )
        {
            throw new StylesheetNotFoundException( window.getPortlet().getBorderKey() );
        }

        try
        {
            return portletXsltViewTransformer.transform( viewFile, viewParameters, XMLDocumentFactory.create( DUMMY_XML ) );
        }
        finally
        {
            PortalFunctionsFactory.get().removeContext();
        }
    }

    private DataSourceResult getDataSourceResult( Window window, UserEntity executor )
    {
        Datasources datasources = window.getPortlet().getDatasources();

        PortalInstanceKey portalInstanceKey = resolvePortalInstanceKey( window );

        DatasourceExecutorContext datasourceExecutorContext = new DatasourceExecutorContext();
        datasourceExecutorContext.setContentFromRequest( context.getContentFromRequest() );
        datasourceExecutorContext.setPortletDocument(
            window.getPortlet().getGetDataDocmentChildElementDocumentAsRootElementInItsOwnDocument() );
        datasourceExecutorContext.setInvocationCache( context.getInvocationCache() );
        datasourceExecutorContext.setDatasourcesType( DatasourcesType.PORTLET );
        datasourceExecutorContext.setDefaultResultRootElementName( verticalProperties.getDatasourceDefaultResultRootElement() );
        datasourceExecutorContext.setDeviceClass( context.getDeviceClass() );
        datasourceExecutorContext.setHttpRequest( context.getHttpRequest() );
        datasourceExecutorContext.setLanguage( context.getLanguage() );
        datasourceExecutorContext.setLocale( context.getLocale() );
        datasourceExecutorContext.setMenuItem( context.getMenuItem() );
        datasourceExecutorContext.setOriginalSitePath( context.getOriginalSitePath() );
        datasourceExecutorContext.setPageRequestType( context.getPageRequestType() );
        datasourceExecutorContext.setPageTemplate( null );
        datasourceExecutorContext.setPortalInstanceKey( portalInstanceKey );
        datasourceExecutorContext.setPortletWindowRenderedInline( context.isRenderedInline() );
        datasourceExecutorContext.setPreviewContext( context.getPreviewContext() );
        datasourceExecutorContext.setProcessors( context.getProcessors() );
        datasourceExecutorContext.setProfile( context.getProfile() );
        datasourceExecutorContext.setRequestParameters( this.requestParameters );
        datasourceExecutorContext.setSite( context.getSite() );
        datasourceExecutorContext.setUser( executor );
        datasourceExecutorContext.setVerticalSession( context.getVerticalSession() );
        datasourceExecutorContext.setWindow( window );
        datasourceExecutorContext.setDataSourceService( this.dataSourceService );
        datasourceExecutorContext.setPluginManager( this.pluginManager );

        DatasourceExecutor dataSourceExecutor = dataSourceExecutorFactory.createDatasourceExecutor( datasourceExecutorContext );

        return dataSourceExecutor.getDataSourceResult( datasources );
    }

    private PortalInstanceKey resolvePortalInstanceKey( Window window )
    {
        PortalInstanceKey portalInstanceKey;
        if ( context.getMenuItem() == null )
        {
            //rendering pagetemplate for newsletter - special case
            portalInstanceKey = PortalInstanceKey.createSite( context.getSite().getKey() );
        }
        else
        {
            portalInstanceKey = PortalInstanceKey.createWindow( window.getKey() );
        }

        return portalInstanceKey;
    }

    private void enterTrace( Window window, UserEntity executor, CacheSettings portletCacheSettings )
    {
        PagePortletTraceInfo info = RenderTrace.enterPageObject( window.getKey().getPortletKey() );
        if ( info != null )
        {
            info.setSiteKey( context.getSite().getKey() );
            info.setName( window.getPortlet().getName() );
            info.setCacheable( portletCacheSettings.isEnabled() );
            info.setRunAsUser( executor.getQualifiedName() );
        }
    }

    private void exitTrace()
    {
        RenderTrace.exitPageObject();
    }

    private UserEntity resolveRunAsUser( Window window )
    {
        UserEntity current = context.getRenderer();
        MenuItemEntity menuItem = context.getMenuItem();

        UserEntity resolvedRunAsUser = PortletRunAsUserResolver.resolveRunAsUser( window.getPortlet(), current, menuItem );
        if ( resolvedRunAsUser == null || resolvedRunAsUser.isDeleted() )
        {
            resolvedRunAsUser = current;
        }
        return resolvedRunAsUser;
    }

    private WindowCacheKey resolveCacheKey( Window window, UserKey executorKey )
    {
        WindowCacheKey key = new WindowCacheKey();
        key.setMenuItemKey( context.getMenuItem().getMenuItemKey() );
        key.setUserKey( executorKey.toString() );
        key.setPortletKey( window.getKey().getPortletKey().toInt() );
        key.setDeviceClass( context.getDeviceClass() );
        key.setLocale( context.getLocale() );
        key.setParamsString( this.requestParameters.getAsString( false ) );
        key.setQueryString( context.getOriginalUrl() );
        return key;
    }

    private boolean resolveUseCache( CacheSettings portletCacheSettings )
    {
        if ( RenderTrace.isTraceOn() )
        {
            return false;
        }
        else if ( context.getPreviewContext().isPreviewing() )
        {
            return false;
        }
        else if ( context.forceNoCacheUsage() )
        {
            return false;
        }
        else if ( !pageCacheService.isEnabled() )
        {
            return false;
        }
        else
        {
            return portletCacheSettings.isEnabled();
        }
    }

    private SiteURLResolver resolveSiteURLResolver()
    {
        if ( context.getOverridingSitePropertyCreateUrlAsPath() == null )
        {
            return siteURLResolver;
        }
        else
        {
            SiteURLResolver siteURLResolver = new SiteURLResolver();
            siteURLResolver.setOverridingSitePropertyCreateUrlAsPath( context.getOverridingSitePropertyCreateUrlAsPath() );
            siteURLResolver.setSitePropertiesService( sitePropertiesService );
            return siteURLResolver;
        }
    }

    public void setDataSourceExecutorFactory( DatasourceExecutorFactory value )
    {
        this.dataSourceExecutorFactory = value;
    }

    public void setPortletXsltViewTransformer( PortletXsltViewTransformer value )
    {
        this.portletXsltViewTransformer = value;
    }

    public void setPageCacheService( PageCacheService value )
    {
        this.pageCacheService = value;
    }

    public void setResourceService( ResourceService resourceService )
    {
        this.resourceService = resourceService;
    }

    public void setVerticalProperties( final VerticalProperties value )
    {
        verticalProperties = value;
    }

    public void setSiteURLResolver( SiteURLResolver siteURLResolver )
    {
        this.siteURLResolver = siteURLResolver;
    }

    public void setSitePropertiesService( final SitePropertiesService sitePropertiesService )
    {
        this.sitePropertiesService = sitePropertiesService;
    }

    public void setPostProcessInstructionExecutor( PostProcessInstructionExecutor postProcessInstructionExecutor )
    {
        this.postProcessInstructionExecutor = postProcessInstructionExecutor;
    }

    public void setLiveTraceService( LivePortalTraceService liveTraceService )
    {
        this.liveTraceService = liveTraceService;
    }

    public void setDataSourceService( DataSourceService dataSourceService )
    {
        this.dataSourceService = dataSourceService;
    }

    public void setPluginManager( PluginManager pluginManager )
    {
        this.pluginManager = pluginManager;
    }
}

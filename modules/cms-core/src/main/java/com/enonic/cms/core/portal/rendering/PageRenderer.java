/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.rendering;

import java.util.concurrent.locks.Lock;

import org.jdom.Document;
import org.jdom.Element;
import org.joda.time.DateTime;

import com.enonic.vertical.VerticalProperties;

import com.enonic.cms.framework.util.GenericConcurrencyLock;

import com.enonic.cms.core.CacheObjectSettings;
import com.enonic.cms.core.CacheSettings;
import com.enonic.cms.core.CachedObject;
import com.enonic.cms.core.SitePropertiesService;
import com.enonic.cms.core.SiteURLResolver;
import com.enonic.cms.core.TightestCacheSettingsResolver;
import com.enonic.cms.core.plugin.PluginManager;
import com.enonic.cms.core.portal.InvocationCache;
import com.enonic.cms.core.portal.PortalInstanceKey;
import com.enonic.cms.core.portal.Ticket;
import com.enonic.cms.core.portal.cache.PageCacheService;
import com.enonic.cms.core.portal.datasource.DataSourceResult;
import com.enonic.cms.core.portal.datasource.DatasourceExecutor;
import com.enonic.cms.core.portal.datasource.DatasourceExecutorContext;
import com.enonic.cms.core.portal.datasource.DatasourceExecutorFactory;
import com.enonic.cms.core.portal.datasource.DatasourcesType;
import com.enonic.cms.core.portal.instruction.PostProcessInstructionContext;
import com.enonic.cms.core.portal.instruction.PostProcessInstructionExecutor;
import com.enonic.cms.core.portal.instruction.PostProcessInstructionProcessor;
import com.enonic.cms.core.portal.livetrace.InstructionPostProcessingTrace;
import com.enonic.cms.core.portal.livetrace.InstructionPostProcessingTracer;
import com.enonic.cms.core.portal.livetrace.LivePortalTraceService;
import com.enonic.cms.core.portal.livetrace.PageRenderingTrace;
import com.enonic.cms.core.portal.livetrace.PageRenderingTracer;
import com.enonic.cms.core.portal.livetrace.ViewTransformationTrace;
import com.enonic.cms.core.portal.livetrace.ViewTransformationTracer;
import com.enonic.cms.core.portal.rendering.portalfunctions.PortalFunctionsContext;
import com.enonic.cms.core.portal.rendering.portalfunctions.PortalFunctionsFactory;
import com.enonic.cms.core.portal.rendering.tracing.PageTraceInfo;
import com.enonic.cms.core.portal.rendering.tracing.RenderTrace;
import com.enonic.cms.core.portal.rendering.tracing.TraceMarkerHelper;
import com.enonic.cms.core.portal.rendering.viewtransformer.PageTemplateXsltViewTransformer;
import com.enonic.cms.core.portal.rendering.viewtransformer.RegionTransformationParameter;
import com.enonic.cms.core.portal.rendering.viewtransformer.TemplateParameterTransformationParameter;
import com.enonic.cms.core.portal.rendering.viewtransformer.TransformationParameterOrigin;
import com.enonic.cms.core.portal.rendering.viewtransformer.TransformationParams;
import com.enonic.cms.core.portal.rendering.viewtransformer.ViewTransformationResult;
import com.enonic.cms.core.portal.xtrace.XTraceHelper;
import com.enonic.cms.core.resource.ResourceFile;
import com.enonic.cms.core.resource.ResourceKey;
import com.enonic.cms.core.resource.ResourceService;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.service.DataSourceService;
import com.enonic.cms.core.structure.TemplateParameter;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import com.enonic.cms.core.structure.page.Region;
import com.enonic.cms.core.structure.page.template.PageTemplateEntity;
import com.enonic.cms.core.stylesheet.StylesheetNotFoundException;
import com.enonic.cms.core.time.TimeService;


/**
 * This class must be insantiated for each use.
 */
public class PageRenderer
{

    private TimeService timeService;

    private DatasourceExecutorFactory dataSourceExecutorFactory;

    private ResourceService resourceService;

    private PageTemplateXsltViewTransformer pageTemplateXsltViewTransformer;

    private InvocationCache invocationCache;

    private PageRendererContext context;

    private PostProcessInstructionExecutor postProcessInstructionExecutor;

    private PageCacheService pageCacheService;

    private CacheSettings resolvedMenuItemCacheSettings = null;

    private VerticalProperties verticalProperties;

    private SiteURLResolver siteURLResolver;

    private SitePropertiesService sitePropertiesService;

    private TightestCacheSettingsResolver tightestCacheSettingsResolver;

    private LivePortalTraceService livePortalTraceService;

    private PageRenderingTrace pageRenderingTrace;

    private static GenericConcurrencyLock<PageCacheKey> concurrencyLock = GenericConcurrencyLock.create();

    private DataSourceService dataSourceService;

    private PluginManager pluginManager;

    protected PageRenderer( PageRendererContext pageRendererContext, LivePortalTraceService livePortalTraceService )
    {
        this.context = pageRendererContext;
        this.invocationCache = new InvocationCache( livePortalTraceService );
        this.livePortalTraceService = livePortalTraceService;
    }

    public RenderedPageResult renderPage( PageTemplateEntity pageTemplate )
    {
        pageRenderingTrace = PageRenderingTracer.startTracing( livePortalTraceService );

        try
        {
            if ( pageTemplate == null )
            {
                throw new IllegalArgumentException( "pageTemplate cannot be null" );
            }

            RenderedPageResult renderedPageResult;
            try
            {
                resolveMenuItemCacheSettings( pageTemplate );

                PageRenderingTracer.traceRequester( pageRenderingTrace, context.getRunAsUser() );
                enterPageTrace( context.getRunAsUser(), pageTemplate, resolvedMenuItemCacheSettings );

                renderedPageResult = doRenderPageAndWindows( pageTemplate );
            }
            finally
            {
                exitPageTrace();
            }

            return renderedPageResult;
        }
        finally
        {
            XTraceHelper.setCurrentTrace( pageRenderingTrace );
            PageRenderingTracer.stopTracing( pageRenderingTrace, livePortalTraceService );
        }
    }

    private RenderedPageResult doRenderPageAndWindows( final PageTemplateEntity pageTemplate )
    {
        RenderedPageResult renderedPageResult = doRenderPageTemplate( pageTemplate );

        String renderedPageContentIncludingRenderedWindows =
            executePostProcessInstructions( pageTemplate, renderedPageResult.getContent(), renderedPageResult.getOutputMethod() );

        renderedPageContentIncludingRenderedWindows =
            renderedPageContentIncludingRenderedWindows.replace( Ticket.getPlaceholder(), context.getTicketId() );

        renderedPageResult.setContent( renderedPageContentIncludingRenderedWindows );

        CacheSettings normalizedPageCacheSettings =
            tightestCacheSettingsResolver.resolveTightestCacheSettingsForPage( context.getMenuItem(), context.getRegionsInPage(),
                                                                               pageTemplate );
        DateTime requestTime = context.getRequestTime();
        DateTime expirationTime = requestTime.plusSeconds( normalizedPageCacheSettings.getSpecifiedSecondsToLive() );
        renderedPageResult.setExpirationTime( expirationTime );
        return renderedPageResult;
    }

    private RenderedPageResult doRenderPageTemplate( PageTemplateEntity pageTemplate )
    {
        if ( !useCache() )
        {
            // render request is not cacheable
            final RenderedPageResult renderedPageResult = renderPageTemplateExcludingPortlets( pageTemplate );
            renderedPageResult.setRetrievedFromCache( false );
            PageRenderingTracer.traceUsedCachedResult( pageRenderingTrace, false, false );
            return renderedPageResult;
        }

        PageCacheKey pageCacheKey = resolvePageCacheKey();

        final Lock locker = concurrencyLock.getLock( pageCacheKey );
        try
        {
            PageRenderingTracer.startConcurrencyBlockTimer( pageRenderingTrace );
            locker.lock();
            PageRenderingTracer.stopConcurrencyBlockTimer( pageRenderingTrace );

            CachedObject cachedPageHolder = pageCacheService.getCachedPage( pageCacheKey );
            if ( cachedPageHolder != null )
            {
                // Found the page in cache, return the clone to prevent further rendering of the cached object
                RenderedPageResult cachedPageResult = (RenderedPageResult) cachedPageHolder.getObject();
                PageRenderingTracer.traceUsedCachedResult( pageRenderingTrace, true, true );
                return (RenderedPageResult) cachedPageResult.clone();
            }

            RenderedPageResult renderedPageResultToCache = renderPageTemplateExcludingPortlets( pageTemplate );
            // Ensure to mark the result as retrieved from cache, before we put it in the cache
            renderedPageResultToCache.setRetrievedFromCache( true );
            CacheObjectSettings cacheSettings = CacheObjectSettings.createFrom( resolvedMenuItemCacheSettings );
            CachedObject cachedPage = pageCacheService.cachePage( pageCacheKey, renderedPageResultToCache, cacheSettings );
            renderedPageResultToCache.setExpirationTime( cachedPage.getExpirationTime() );

            // Have to return another instance since we did not retrieve this result from cache
            RenderedPageResult renderedPageResultToReturn = (RenderedPageResult) renderedPageResultToCache.clone();
            renderedPageResultToReturn.setRetrievedFromCache( false );
            PageRenderingTracer.traceUsedCachedResult( pageRenderingTrace, true, false );
            return renderedPageResultToReturn;
        }
        finally
        {
            locker.unlock();
        }
    }

    private RenderedPageResult renderPageTemplateExcludingPortlets( final PageTemplateEntity pageTemplate )
    {
        final DataSourceResult dataSourceResult = executeDataSources( pageTemplate );
        final PortalFunctionsContext portalFunctionsContext = new PortalFunctionsContext();
        portalFunctionsContext.setInvocationCache( invocationCache );
        portalFunctionsContext.setSitePath( context.getSitePath() );
        portalFunctionsContext.setOriginalSitePath( context.getOriginalSitePath() );
        portalFunctionsContext.setSite( context.getSite() );
        portalFunctionsContext.setMenuItem( context.getMenuItem() );
        portalFunctionsContext.setEncodeURIs( context.isEncodeURIs() );
        portalFunctionsContext.setLocale( context.getLocale() );
        portalFunctionsContext.setPortalInstanceKey( resolvePortalInstanceKey() );
        portalFunctionsContext.setRenderedInline( false );
        portalFunctionsContext.setEncodeImageUrlParams( RenderTrace.isTraceOff() );
        portalFunctionsContext.setSiteURLResolver( resolveSiteURLResolver() );
        portalFunctionsContext.setPageRendererContext( context );

        final ViewTransformationResult viewTransformationResult;
        final ViewTransformationTrace trace = ViewTransformationTracer.startTracing( livePortalTraceService );
        try
        {
            final ResourceKey stylesheetKey = pageTemplate.getStyleKey();
            final ResourceFile pageTemplateStylesheet = resourceService.getResourceFile( stylesheetKey );
            if ( pageTemplateStylesheet == null )
            {
                throw new StylesheetNotFoundException( stylesheetKey );
            }
            ViewTransformationTracer.traceView( pageTemplateStylesheet.getPath(), trace );

            final Document model;
            if ( dataSourceResult == null || dataSourceResult.getData() == null )
            {
                model = new Document( new Element( verticalProperties.getDatasourceDefaultResultRootElement() ) );
            }
            else
            {
                model = dataSourceResult.getData().getAsJDOMDocument();
            }

            final TransformationParams transformationParams = new TransformationParams();
            for ( Region region : context.getRegionsInPage().getRegions() )
            {
                if ( transformationParams.notContains( region.getName() ) )
                {
                    transformationParams.add( new RegionTransformationParameter( region ) );
                }
            }

            for ( TemplateParameter templateParam : pageTemplate.getTemplateParameters().values() )
            {
                if ( transformationParams.notContains( templateParam.getName() ) )
                {
                    transformationParams.add(
                        new TemplateParameterTransformationParameter( templateParam, TransformationParameterOrigin.PAGETEMPLATE ) );
                }
            }
            PortalFunctionsFactory.get().setContext( portalFunctionsContext );
            viewTransformationResult = pageTemplateXsltViewTransformer.transform( pageTemplateStylesheet, model, transformationParams );
        }
        finally
        {
            PortalFunctionsFactory.get().removeContext();
            ViewTransformationTracer.stopTracing( trace, livePortalTraceService );
        }

        if ( RenderTrace.isTraceOn() )
        {
            viewTransformationResult.setContent(
                TraceMarkerHelper.writePageMarker( RenderTrace.getCurrentRenderTraceInfo(), viewTransformationResult.getContent(),
                                                   viewTransformationResult.getOutputMethod() ) );
        }

        final RenderedPageResult renderedPageResult = new RenderedPageResult();
        renderedPageResult.setRenderedAt( timeService.getNowAsDateTime() );
        renderedPageResult.setHttpContentType( viewTransformationResult.getHttpContentType() );
        renderedPageResult.setContent( viewTransformationResult.getContent() );
        renderedPageResult.setOutputMethod( viewTransformationResult.getOutputMethod() );
        if ( viewTransformationResult.getOutputEncoding() != null )
        {
            renderedPageResult.setContentEncoding( viewTransformationResult.getOutputEncoding() );
        }
        return renderedPageResult;
    }

    private String executePostProcessInstructions( PageTemplateEntity pageTemplate, String pageMarkup, String outputMode )
    {

        WindowRendererContext windowRenderContext = new WindowRendererContext();
        windowRenderContext.setContentFromRequest( context.getContentFromRequest() );
        windowRenderContext.setOverridingSitePropertyCreateUrlAsPath( context.getOverridingSitePropertyCreateUrlAsPath() );
        windowRenderContext.setDeviceClass( context.getDeviceClass() );
        windowRenderContext.setEncodeURIs( context.isEncodeURIs() );
        windowRenderContext.setForceNoCacheUsage( context.forceNoCacheUsage() );
        windowRenderContext.setHttpRequest( context.getHttpRequest() );
        windowRenderContext.setInvocationCache( invocationCache );
        windowRenderContext.setLanguage( context.getLanguage() );
        windowRenderContext.setLocale( context.getLocale() );
        windowRenderContext.setMenuItem( context.getMenuItem() );
        windowRenderContext.setOriginalSitePath( context.getOriginalSitePath() );
        windowRenderContext.setPageRequestType( context.getPageRequestType() );
        windowRenderContext.setPageTemplate( pageTemplate );
        windowRenderContext.setPreviewContext( context.getPreviewContext() );
        windowRenderContext.setProcessors( context.getProcessors() );
        windowRenderContext.setProfile( context.getProfile() );
        windowRenderContext.setRegionsInPage( context.getRegionsInPage() );
        windowRenderContext.setRenderedInline( true );
        windowRenderContext.setRenderer( context.getRenderer() );
        windowRenderContext.setTicketId( context.getTicketId() );
        windowRenderContext.setSite( context.getSite() );
        windowRenderContext.setSitePath( context.getSitePath() );
        windowRenderContext.setVerticalSession( context.getVerticalSession() );
        windowRenderContext.setOriginalUrl( context.getOriginalUrl() );

        final InstructionPostProcessingTrace instructionPostProcessingTrace =
            InstructionPostProcessingTracer.startTracingForPage( livePortalTraceService );
        try
        {
            PostProcessInstructionContext postProcessInstructionContext = new PostProcessInstructionContext();
            postProcessInstructionContext.setSite( context.getSite() );
            postProcessInstructionContext.setEncodeImageUrlParams( RenderTrace.isTraceOff() );
            postProcessInstructionContext.setPreviewContext( context.getPreviewContext() );
            postProcessInstructionContext.setWindowRendererContext( windowRenderContext );
            postProcessInstructionContext.setHttpRequest( context.getHttpRequest() );
            postProcessInstructionContext.setInContextOfWindow( false );
            postProcessInstructionContext.setSiteURLResolverEnableHtmlEscaping( siteURLResolver );

            postProcessInstructionContext.setSiteURLResolverEnableHtmlEscaping( createSiteURLResolver( true ) );
            postProcessInstructionContext.setSiteURLResolverDisableHtmlEscaping( createSiteURLResolver( false ) );

            PostProcessInstructionProcessor postProcessInstructionProcessor =
                new PostProcessInstructionProcessor( postProcessInstructionContext, postProcessInstructionExecutor );

            return postProcessInstructionProcessor.processInstructions( pageMarkup );

        }
        finally
        {
            InstructionPostProcessingTracer.stopTracing( instructionPostProcessingTrace, livePortalTraceService );
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

    private DataSourceResult executeDataSources( PageTemplateEntity pageTemplate )
    {
        // resolve css key
        ResourceKey cssKey = context.getSite().getDefaultCssKey();
        if ( pageTemplate.getCssKey() != null )
        {
            cssKey = pageTemplate.getCssKey();
        }

        ResourceKey[] cssKeys = null;
        if ( cssKey != null )
        {
            cssKeys = new ResourceKey[]{cssKey};
        }

        PortalInstanceKey portalInstanceKey = resolvePortalInstanceKey();

        DatasourceExecutorContext datasourceExecutorContext = new DatasourceExecutorContext();
        datasourceExecutorContext.setContentFromRequest( context.getContentFromRequest() );
        datasourceExecutorContext.setCssKeys( cssKeys );
        datasourceExecutorContext.setInvocationCache( invocationCache );
        datasourceExecutorContext.setDatasourcesType( DatasourcesType.PAGETEMPLATE );
        datasourceExecutorContext.setDefaultResultRootElementName( verticalProperties.getDatasourceDefaultResultRootElement() );
        datasourceExecutorContext.setDeviceClass( context.getDeviceClass() );
        datasourceExecutorContext.setHttpRequest( context.getHttpRequest() );
        datasourceExecutorContext.setLanguage( context.getLanguage() );
        datasourceExecutorContext.setLocale( context.getLocale() );
        datasourceExecutorContext.setMenuItem( context.getMenuItem() );
        datasourceExecutorContext.setOriginalSitePath( context.getOriginalSitePath() );
        datasourceExecutorContext.setPageRequestType( context.getPageRequestType() );
        datasourceExecutorContext.setPageTemplate( pageTemplate );
        datasourceExecutorContext.setPortalInstanceKey( portalInstanceKey );
        datasourceExecutorContext.setPreviewContext( context.getPreviewContext() );
        datasourceExecutorContext.setProcessors( context.getProcessors() );
        datasourceExecutorContext.setProfile( context.getProfile() );
        datasourceExecutorContext.setRegions( context.getRegionsInPage() );
        datasourceExecutorContext.setSite( context.getSite() );
        datasourceExecutorContext.setSiteProperties( sitePropertiesService.getSiteProperties( context.getSite().getKey() ) );
        datasourceExecutorContext.setRequestParameters( context.getSitePath().getRequestParameters() );
        datasourceExecutorContext.setVerticalSession( context.getVerticalSession() );
        datasourceExecutorContext.setUser( context.getRunAsUser() );
        datasourceExecutorContext.setDataSourceService( this.dataSourceService );
        datasourceExecutorContext.setPluginManager( this.pluginManager );

        DatasourceExecutor datasourceExecutor = dataSourceExecutorFactory.createDatasourceExecutor( datasourceExecutorContext );

        return datasourceExecutor.getDataSourceResult( pageTemplate.getDatasources() );
    }

    private PortalInstanceKey resolvePortalInstanceKey()
    {
        PortalInstanceKey portalInstanceKey;
        if ( context.getMenuItem() == null )
        {
            //rendering pagetemplate for newsletter - special case
            portalInstanceKey = PortalInstanceKey.createSite( context.getSite().getKey() );
        }
        else
        {
            portalInstanceKey = PortalInstanceKey.createPage( context.getMenuItem().getMenuItemKey() );
        }
        return portalInstanceKey;
    }

    private void enterPageTrace( UserEntity runAsUser, PageTemplateEntity pageTemplate, CacheSettings menuItemCacheSettings )
    {
        MenuItemEntity menuItem = context.getMenuItem();
        if ( menuItem == null )
        {
            return;
        }

        PageTraceInfo info = RenderTrace.enterPage( menuItem.getKey() );
        if ( info != null )
        {
            info.setSiteKey( context.getSite().getKey() );
            info.setName( menuItem.getName() );
            info.setDisplayName( menuItem.getDisplayName() );
            info.setPageTemplateName( pageTemplate.getName() );
            info.setCacheable( menuItemCacheSettings.isEnabled() );
            info.setRunAsUser( runAsUser.getQualifiedName() );
        }
    }

    private void exitPageTrace()
    {
        MenuItemEntity menuItem = context.getMenuItem();
        if ( menuItem == null )
        {
            return;
        }

        RenderTrace.exitPage();
    }

    private boolean useCache()
    {
        if ( RenderTrace.isTraceOn() )
        {
            return false;
        }
        else if ( context.getPreviewContext().isPreviewing() )
        {
            return false;
        }
        else if ( !pageCacheService.isEnabled() )
        {
            return false;
        }
        else if ( context.forceNoCacheUsage() || context.getMenuItem() == null )
        {
            return false;
        }
        else
        {
            return resolvedMenuItemCacheSettings.isEnabled();
        }
    }

    private CacheSettings resolveMenuItemCacheSettings( PageTemplateEntity pageTemplate )
    {
        if ( resolvedMenuItemCacheSettings == null )
        {
            resolvedMenuItemCacheSettings = context.getMenuItem().getCacheSettings( pageCacheService.getDefaultTimeToLive(), pageTemplate );
        }

        return resolvedMenuItemCacheSettings;
    }

    private PageCacheKey resolvePageCacheKey()
    {
        PageCacheKey key = new PageCacheKey();
        key.setMenuItemKey( context.getMenuItem().getMenuItemKey() );
        key.setUserKey( context.getRunAsUser().getKey().toString() );
        key.setDeviceClass( context.getDeviceClass() );
        key.setLocale( context.getLocale() );
        key.setQueryString( context.getOriginalUrl() );
        return key;
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

    public void setResourceService( ResourceService value )
    {
        this.resourceService = value;
    }

    public void setPageTemplateXsltViewTransformer( PageTemplateXsltViewTransformer value )
    {
        this.pageTemplateXsltViewTransformer = value;
    }

    public void setPageCacheService( PageCacheService value )
    {
        this.pageCacheService = value;
    }

    public void setVerticalProperties( final VerticalProperties value )
    {
        verticalProperties = value;
    }

    public void setSitePropertiesService( final SitePropertiesService value )
    {
        this.sitePropertiesService = value;
    }

    public void setSiteURLResolver( SiteURLResolver value )
    {
        this.siteURLResolver = value;
    }

    public void setTightestCacheSettingsResolver( TightestCacheSettingsResolver value )
    {
        this.tightestCacheSettingsResolver = value;
    }

    public void setTimeService( TimeService value )
    {
        this.timeService = value;
    }

    public void setPostProcessInstructionExecutor( PostProcessInstructionExecutor postProcessInstructionExecutor )
    {
        this.postProcessInstructionExecutor = postProcessInstructionExecutor;
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


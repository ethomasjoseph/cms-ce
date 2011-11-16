/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import com.enonic.vertical.VerticalProperties;

import com.enonic.cms.core.Attribute;
import com.enonic.cms.core.BadRequestErrorType;
import com.enonic.cms.core.InvalidKeyException;
import com.enonic.cms.core.NotFoundErrorType;
import com.enonic.cms.core.Path;
import com.enonic.cms.core.SiteKey;
import com.enonic.cms.core.SitePath;
import com.enonic.cms.core.SitePathResolver;
import com.enonic.cms.core.SiteRedirectAndForwardHelper;
import com.enonic.cms.core.SiteURLResolver;
import com.enonic.cms.core.StacktraceLoggingUnrequired;
import com.enonic.cms.core.portal.mvc.controller.AttachmentRequestException;
import com.enonic.cms.core.portal.mvc.controller.DefaultRequestException;
import com.enonic.cms.core.portal.mvc.controller.ImageRequestException;
import com.enonic.cms.core.portal.mvc.view.BasicAuthView;
import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import com.enonic.cms.store.dao.MenuItemDao;
import com.enonic.cms.store.dao.SiteDao;

public class PortalExceptionResolver
    implements HandlerExceptionResolver
{

    private static final String ATTRIBUTE_ALREADY_PROCESSING_EXCEPTION = "ALREADY_PROCESSING_EXCEPTION";

    private static final Logger LOG = LoggerFactory.getLogger( PortalExceptionResolver.class );

    private SitePathResolver sitePathResolver;

    private SiteRedirectAndForwardHelper siteRedirectAndForwardHelper;

    private SiteURLResolver siteURLResolver;

    private MenuItemDao menuItemDao;

    @Autowired
    private SiteDao siteDao;

    public void setSitePathResolver( SitePathResolver value )
    {
        this.sitePathResolver = value;
    }

    public void setSiteRedirectAndForwardHelper( SiteRedirectAndForwardHelper value )
    {
        this.siteRedirectAndForwardHelper = value;
    }

    public void setSiteURLResolver( SiteURLResolver value )
    {
        this.siteURLResolver = value;
    }

    public void setMenuItemDao( MenuItemDao menuItemDao )
    {
        this.menuItemDao = menuItemDao;
    }

    public ModelAndView resolveException( HttpServletRequest request, HttpServletResponse response, Object handler,
                                          Exception outerException )
    {

        final Throwable causingExeption;
        if ( isExceptionAnyOfThose( outerException, new Class[]{DefaultRequestException.class, AttachmentRequestException.class,
            ImageRequestException.class} ) )
        {
            // Have to unwrap these exceptions to get the causing exception
            causingExeption = outerException.getCause();
        }
        else
        {
            causingExeption = outerException;
        }

        logException( outerException, causingExeption, request );

        AbstractBaseError error = getError( causingExeption );

        try
        {
            return handleExceptions( request, causingExeption, error );
        }
        catch ( LoginPageNotFoundException e )
        {
            return new ModelAndView( new BasicAuthView() );
        }
        finally
        {
            response.setStatus( error.getStatusCode() );
            request.setAttribute( ATTRIBUTE_ALREADY_PROCESSING_EXCEPTION, ATTRIBUTE_ALREADY_PROCESSING_EXCEPTION );
        }
    }

    private void logException( Throwable outerException, Throwable causingException, HttpServletRequest request )
    {

        if ( isExceptionAnyOfThose( causingException, new Class[]{ResourceNotFoundException.class} ) )
        {
            ResourceNotFoundException resourceNotFoundException = (ResourceNotFoundException) causingException;
            boolean ignore = resourceNotFoundException.endsWithIgnoreCase( "favicon.ico" ) ||
                resourceNotFoundException.endsWithIgnoreCase( "robots.txt" );
            if ( ignore )
            {
                // skipping logging
                return;
            }
        }

        if ( isExceptionAnyOfThose( causingException, new Class[]{PathRequiresAuthenticationException.class} ) )
        {
            // skipping logging
            return;
        }

        final boolean outerExceptionIsPortalRequestException = isExceptionAnyOfThose( outerException,
                                                                                      new Class[]{DefaultRequestException.class,
                                                                                          AttachmentRequestException.class,
                                                                                          ImageRequestException.class} );
        final boolean innerExceptionIsQuietException =
            isExceptionAnyOfThose( causingException, new Class[]{StacktraceLoggingUnrequired.class} );

        if ( outerExceptionIsPortalRequestException && innerExceptionIsQuietException )
        {
            LOG.info( outerException.getMessage() );
        }
        else if ( isExceptionAnyOfThose( causingException, new Class[]{ForbiddenErrorType.class, UnauthorizedErrorType.class} ) )
        {
            LOG.debug( causingException.getMessage() );
        }
        else
        {
            StringBuffer message = new StringBuffer();
            message.append( causingException.getMessage() ).append( "\n" );
            if ( logRequestInfo() )
            {
                message.append( buildRequestInfo( request ) );
            }
            LOG.error( message.toString(), causingException );
        }
    }

    private boolean logRequestInfo()
    {

        try
        {
            VerticalProperties verticalProperties = VerticalProperties.getVerticalProperties();
            return Boolean.valueOf( verticalProperties.getProperty( "cms.render.logRequestInfoOnException" ) );
        }
        catch ( Exception e )
        {
            return false;
        }
    }

    private String buildRequestInfo( HttpServletRequest request )
    {

        StringBuffer s = new StringBuffer();
        s.append( "Request information:\n" );
        s.append( " - cms.originalURL: " ).append( request.getAttribute( Attribute.ORIGINAL_URL ) ).append( "\n" );
        s.append( " - cms.originalSitePath: " ).append( request.getAttribute( Attribute.ORIGINAL_SITEPATH ) ).append( "\n" );
        s.append( " - http.queryString: " ).append( request.getQueryString() ).append( "\n" );
        s.append( " - http.requestURI: " ).append( request.getRequestURI() ).append( "\n" );
        s.append( " - http.remoteAddress: " ).append( request.getRemoteAddr() ).append( "\n" );
        s.append( " - http.remoteHost: " ).append( request.getRemoteHost() ).append( "\n" );
        s.append( " - http.characterEncoding: " ).append( request.getCharacterEncoding() ).append( "\n" );
        s.append( " - http.header.User-Agent: " ).append( request.getHeader( "User-Agent" ) ).append( "\n" );
        s.append( " - http.header.Referer: " ).append( request.getHeader( "Referer" ) ).append( "\n" );
        return s.toString();
    }

    @SuppressWarnings({"ThrowableInstanceNeverThrown"})
    private AbstractBaseError getError( Throwable exception )
    {

        if ( exception instanceof BadRequestErrorType )
        {
            return new ClientError( HttpServletResponse.SC_BAD_REQUEST, exception.getMessage(), exception );
        }
        else if ( exception instanceof NotFoundErrorType )
        {
            if ( exception instanceof ContentNameMismatchException )
            {
                ContentNameMismatchException contentNameMismatchException = (ContentNameMismatchException) exception;

                return new ContentNameMismatchClientError( HttpServletResponse.SC_NOT_FOUND, exception.getMessage(), exception,
                                                           contentNameMismatchException.getContentKey(),
                                                           contentNameMismatchException.getRequestedContentName() );
            }

            return new ClientError( HttpServletResponse.SC_NOT_FOUND, exception.getMessage(), exception );
        }
        else if ( exception instanceof ForbiddenErrorType )
        {
            return new ClientError( HttpServletResponse.SC_FORBIDDEN, exception.getMessage(), exception );
        }
        else if ( exception instanceof UnauthorizedErrorType )
        {
            return new ClientError( HttpServletResponse.SC_UNAUTHORIZED, exception.getMessage(), exception );
        }
        else
        {
            return new ServerError( HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exception.getMessage(), exception );
        }
    }

    private ModelAndView handleExceptions( HttpServletRequest request, Throwable exception, AbstractBaseError error )
    {

        if ( isExceptionAnyOfThose( exception, new Class[]{InvalidKeyException.class} ) &&
            ( (InvalidKeyException) exception ).forClass( SiteKey.class ) )
        {
            return getExceptionPage( request, error );
        }
        else if ( exception instanceof UnauthorizedErrorType )
        {
            UnauthorizedErrorType unauthorizedErrorTypeException = (PathRequiresAuthenticationException) exception;
            return getLoginPage( request, unauthorizedErrorTypeException.getSitePath() );
        }

        if ( request.getAttribute( ATTRIBUTE_ALREADY_PROCESSING_EXCEPTION ) == null )
        {
            try
            {
                ModelAndView errorPage = getErrorPage( request, error );
                if ( errorPage != null )
                {
                    return errorPage;
                }
            }
            catch ( Exception e )
            {
                LOG.error( "Failed to get error page: " + e.getMessage(), e );
                return getExceptionPage( request, error );
            }
        }

        return getExceptionPage( request, error );
    }

    private ModelAndView getErrorPage( HttpServletRequest request, AbstractBaseError error )
    {

        SitePath sitePath = sitePathResolver.resolveSitePath( request );
        boolean siteExists = siteExists( sitePath.getSiteKey() );
        if ( siteExists && hasErrorPage( sitePath.getSiteKey().toInt() ) )
        {
            request.setAttribute( Attribute.ORIGINAL_SITEPATH, sitePath );
            int errorPageKey = getErrorPage( sitePath.getSiteKey().toInt() );
            SitePath errorPagePath = new SitePath( sitePath.getSiteKey(), new Path( resolveMenuItemPath( errorPageKey ) ) );
            final String statusCodeString = String.valueOf( error.getStatusCode() );
            errorPagePath.addParam( "http_status_code", statusCodeString );
            errorPagePath.addParam( "exception_message", error.getMessage() );

            if ( error instanceof ContentNameMismatchClientError )
            {
                ContentNameMismatchClientError contentNameMismatchClientError = (ContentNameMismatchClientError) error;
                errorPagePath.addParam( "content_key", contentNameMismatchClientError.getContentKey().toString() );
            }

            return siteRedirectAndForwardHelper.getForwardModelAndView( request, errorPagePath );
        }
        return null;
    }

    private String resolveMenuItemPath( int menuItemKey )
    {
        MenuItemEntity menuItem = menuItemDao.findByKey( menuItemKey );
        if ( menuItem == null )
        {
            return "";
        }
        return menuItem.getPathAsString();
    }

    private ModelAndView getExceptionPage( HttpServletRequest request, AbstractBaseError e )
    {
        if ( VerticalProperties.getVerticalProperties().doShowDetailedErrorInformation() )
        {
            return createFullExceptionPage( request, e );
        }

        return createMinimalExceptionPage( request, e );
    }

    private ModelAndView createMinimalExceptionPage( HttpServletRequest request, AbstractBaseError e )
    {
        Map<String, Object> model = new HashMap<String, Object>();
        SiteErrorDetails siteErrorDetails = new SiteErrorDetails( request, e.getCause(), e.getStatusCode() );
        model.put( "details", siteErrorDetails );

        return new ModelAndView( "errorPageMinimal", model );
    }

    private ModelAndView createFullExceptionPage( HttpServletRequest request, AbstractBaseError e )
    {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put( "details", new SiteErrorDetails( request, e.getCause(), e.getStatusCode() ) );
        return new ModelAndView( "errorPage", model );
    }

    private ModelAndView getLoginPage( HttpServletRequest request, SitePath unauthPageSitePath )
    {

        SiteKey siteKey = unauthPageSitePath.getSiteKey();
        int menuItemKey = getLoginPage( siteKey.toInt() );

        if ( menuItemKey >= 0 )
        {

            Path loginPageLocalPath = new Path( resolveMenuItemPath( menuItemKey ) );
            SitePath loginPageSitePath = unauthPageSitePath.createNewInSameSite( loginPageLocalPath, unauthPageSitePath.getParams() );
            // remove the id param, because we may have got that from the unauthPageSitePath
            loginPageSitePath.removeParam( "id" );

            // we dont want the error code in the referer, so remove it
            unauthPageSitePath.removeParam( "error_user_login" );
            String referer = siteURLResolver.createUrl( request, unauthPageSitePath, true );
            loginPageSitePath.addParam( "referer", referer );

            return siteRedirectAndForwardHelper.getForwardModelAndView( request, loginPageSitePath );
        }
        else
        {
            throw new LoginPageNotFoundException( siteKey );
        }
    }

    private boolean isExceptionAnyOfThose( Throwable e, Class[] classes )
    {

        for ( Class cls : classes )
        {
            if ( cls.isInstance( e ) )
            {
                return true;
            }
        }
        return false;
    }

    private int getErrorPage( final int siteKey )
    {
        final SiteEntity entity = this.siteDao.findByKey( siteKey );
        if ( ( entity == null ) || ( entity.getErrorPage() == null ) )
        {
            return -1;
        }
        else
        {
            return entity.getErrorPage().getKey();
        }
    }

    private boolean hasErrorPage( final int siteKey )
    {
        return getErrorPage( siteKey ) >= 0;
    }

    private int getLoginPage( final int siteKey )
    {
        final SiteEntity entity = this.siteDao.findByKey( siteKey );
        if ( ( entity == null ) || ( entity.getLoginPage() == null ) )
        {
            return -1;
        }
        else
        {
            return entity.getLoginPage().getKey();
        }
    }

    private boolean siteExists( final SiteKey siteKey )
    {
        final SiteEntity site = this.siteDao.findByKey( siteKey.toInt() );
        return site != null;
    }
}

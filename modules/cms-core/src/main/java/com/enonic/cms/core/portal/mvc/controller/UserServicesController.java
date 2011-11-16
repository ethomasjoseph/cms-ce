/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.mvc.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.enonic.vertical.userservices.UserServicesParameterResolver;

import com.enonic.cms.core.Path;
import com.enonic.cms.core.SitePath;
import com.enonic.cms.core.SitePropertyNames;
import com.enonic.cms.core.portal.InvalidParameterValueException;
import com.enonic.cms.core.portal.InvalidTicketException;
import com.enonic.cms.core.portal.ParameterMissingException;
import com.enonic.cms.core.portal.ticket.TicketValidator;

public class UserServicesController
    extends AbstractSiteController
{
    /**
     * @inheritDoc
     */
    protected ModelAndView handleRequestInternal( HttpServletRequest request, HttpServletResponse response, SitePath sitePath )
        throws Exception
    {

        if ( ticketIsRequired( sitePath ) && !ticketIsValid( request ) )
        {
            throw new InvalidTicketException();
        }

        if ( sitePropertiesService.getPropertyAsBoolean( SitePropertyNames.AUTOLOGIN_HTTP_REMOTE_USER_ENABLED, sitePath.getSiteKey() ) )
        {
            autoLoginService.autologinWithRemoteUser( request );
        }

        // Handle multipart forms
        String handler = UserServicesParameterResolver.resolveHandlerFromSitePath( sitePath );

        if ( handler == null || handler.trim().length() == 0 )
        {
            throw new ParameterMissingException( "handler" );
        }

        String servletPath = "/servlet/";
        StringBuffer servletURL = new StringBuffer( servletPath );

        if ( handler.equals( "content" ) )
        {
            servletURL.append( "com.enonic.vertical.userservices.CustomContentHandlerServlet" );
        }
        else if ( handler.equals( "user" ) )
        {
            servletURL.append( "com.enonic.vertical.userservices.UserHandlerServlet" );
        }
        else if ( handler.equals( "calendar" ) )
        {
            servletURL.append( "com.enonic.vertical.userservices.CalendarHandlerServlet" );
        }
        else if ( handler.equals( "poll" ) )
        {
            servletURL.append( "com.enonic.vertical.userservices.PollHandlerServlet" );
        }
        else if ( handler.equals( "sendmail" ) )
        {
            servletURL.append( "com.enonic.vertical.userservices.SendMailServlet" );
        }
        else if ( handler.equals( "content_sendmail" ) )
        {
            servletURL.append( "com.enonic.vertical.userservices.ContentSendMailServlet" );
        }
        else if ( handler.equals( "session" ) )
        {
            servletURL.append( "com.enonic.vertical.userservices.VerticalSessionHandlerServlet" );
        }
        else if ( handler.equals( "form" ) )
        {
            servletURL.append( "com.enonic.vertical.userservices.FormHandlerServlet" );
        }
        else if ( handler.equals( "portal" ) )
        {
            servletURL.append( "com.enonic.vertical.userservices.PortalHandlerServlet" );
        }

        if ( servletPath.equals( servletURL.toString() ) )
        {
            throw new InvalidParameterValueException( "handler", handler );
        }

        String op = UserServicesParameterResolver.resolveOperationFromSitePath( sitePath );
        SitePath pathToServlet = sitePath.createNewInSameSite( new Path( servletURL.toString() ), sitePath.getParams() );
        pathToServlet.addParam( "_op", op );
        return siteRedirectAndForwardHelper.getForwardModelAndView( request, pathToServlet );
    }

    private boolean ticketIsRequired( SitePath sitePath )
    {
        String handler = UserServicesParameterResolver.resolveHandlerFromSitePath( sitePath );
        String operation = UserServicesParameterResolver.resolveOperationFromSitePath( sitePath );

        if ( "user".equals( handler ) )
        {
            if ( "login".equals( operation ) || "logout".equals( operation ) )
            {
                return false;
            }
        }
        else if ( "portal".equals( handler ) && "forceDeviceClass".equals( operation ) )
        {
            return false;
        }
        else if ( "portal".equals( handler ) && "resetDeviceClass".equals( operation ) )
        {
            return false;
        }
        else if ( "portal".equals( handler ) && "forceLocale".equals( operation ) )
        {
            return false;
        }
        else if ( "portal".equals( handler ) && "resetLocale".equals( operation ) )
        {
            return false;
        }

        return true;
    }

    private boolean ticketIsValid( HttpServletRequest request )
    {
        return TicketValidator.isValid( request );
    }

}

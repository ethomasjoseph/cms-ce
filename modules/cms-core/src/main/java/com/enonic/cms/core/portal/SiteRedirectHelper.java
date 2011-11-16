/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.enonic.cms.framework.util.UrlPathEncoder;

import com.enonic.cms.core.SiteKey;
import com.enonic.cms.core.SitePathResolver;
import com.enonic.cms.core.SiteURLResolver;

public class SiteRedirectHelper
{
    private SitePathResolver sitePathResolver;

    private SiteURLResolver siteURLResolver;

    public void setSitePathResolver( SitePathResolver value )
    {
        this.sitePathResolver = value;
    }

    public void setSiteURLResolver( SiteURLResolver value )
    {
        this.siteURLResolver = value;
    }

    public void sendRedirectWithAbsoluteURL( HttpServletResponse response, String absoluteURL )
    {
        String encodedUrl = UrlPathEncoder.encodeURL( absoluteURL );

        doSendRedirect( response, encodedUrl );
    }

    public void sendRedirectWithPath( HttpServletRequest request, HttpServletResponse response, String path )
    {
        String url = doGetFullPathForRedirect( request, path );

        doSendRedirect( response, url );
    }

    public void sendRedirect( HttpServletRequest request, HttpServletResponse response, String path )
    {
        String url;

        if ( isAbsoluteUrl( path ) )
        {
            url = UrlPathEncoder.encodeURL( path );
        }
        else
        {
            url = doGetFullPathForRedirect( request, path );
        }

        doSendRedirect( response, url );
    }

    private String doGetFullPathForRedirect( HttpServletRequest request, String path )
    {
        SiteKey siteKey = sitePathResolver.resolveSiteKey( request );
        return siteURLResolver.createFullPathForRedirect( request, siteKey, path );
    }

    private boolean isAbsoluteUrl( String path )
    {
        return path.matches( "^[a-z]{3,6}://.+" );
    }

    private void doSendRedirect( HttpServletResponse response, String url )
    {
        final String location = response.encodeRedirectURL( url );

        response.setStatus( HttpServletResponse.SC_MOVED_TEMPORARILY );
        response.setHeader( "Location", location );
    }
}

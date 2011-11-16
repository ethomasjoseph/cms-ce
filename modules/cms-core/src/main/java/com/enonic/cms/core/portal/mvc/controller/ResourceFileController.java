/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.mvc.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.springframework.web.servlet.ModelAndView;

import com.enonic.cms.framework.util.HttpServletUtil;

import com.enonic.cms.core.Path;
import com.enonic.cms.core.SitePath;
import com.enonic.cms.core.SitePropertyNames;
import com.enonic.cms.core.portal.ReservedLocalPaths;
import com.enonic.cms.core.portal.ResourceNotFoundException;
import com.enonic.cms.core.resource.ResourceFile;
import com.enonic.cms.core.resource.ResourceKey;
import com.enonic.cms.core.resource.ResourceKeyResolverForSiteLocalResources;
import com.enonic.cms.core.resource.ResourceService;

public class ResourceFileController
    extends AbstractSiteController
{
    private ResourceService resourceService;

    private static String LOCAL_PREFIX = ReservedLocalPaths.PATH_RESOURCE.toString();

    private static String FORWARD_PREFIX = "/_default";

    public ModelAndView handleRequestInternal( HttpServletRequest request, HttpServletResponse response, SitePath sitePath )
        throws ServletException, IOException
    {

        if ( sitePath.getLocalPath().endsWithSlash() && !sitePath.getLocalPath().contains( "/~" ) )
        {
            return createForwardToMenuItem( request, sitePath );
        }

        String sitePublicHome = null;

        final ResourceKey publicPath = siteDao.findByKey( sitePath.getSiteKey().toInt() ).getPathToPublicResources();
        if ( publicPath != null )
        {
            sitePublicHome = publicPath.toString();
        }

        final ResourceKeyResolverForSiteLocalResources resourceKeyResolverForSiteLocalResources =
            new ResourceKeyResolverForSiteLocalResources( sitePublicHome );

        final ResourceKey resourceKey = resourceKeyResolverForSiteLocalResources.resolveResourceKey( sitePath );
        final ResourceFile resourceFile = resourceService.getResourceFile( resourceKey );

        if ( resourceFile == null && sitePath.getLocalPath().contains( "/~" ) )
        {
            // this is to prevent redirect loop
            throw new ResourceNotFoundException( sitePath.getSiteKey(), sitePath.getLocalPath() );
        }

        if ( resourceFile == null )
        {
            return createForwardToMenuItem( request, sitePath );
        }

        setHttpHeaders( response, sitePath );

        response.setContentType( resourceFile.getMimeType() );
        response.setContentLength( (int) resourceFile.getSize() );

        HttpServletUtil.copyNoCloseOut( resourceFile.getDataAsInputStream(), response.getOutputStream() );

        return null;
    }

    private void setHttpHeaders( final HttpServletResponse response, final SitePath sitePath )
    {
        final DateTime now = new DateTime();
        HttpServletUtil.setDateHeader( response, now.toDate() );

        final boolean cacheHeadersEnabled =
            sitePropertiesService.getPropertyAsBoolean( SitePropertyNames.RESOURCE_CACHE_HEADERS_ENABLED, sitePath.getSiteKey() );
        if ( cacheHeadersEnabled )
        {
            final boolean forceNoCache =
                sitePropertiesService.getPropertyAsBoolean( SitePropertyNames.RESOURCE_CACHE_HEADERS_FORCENOCACHE, sitePath.getSiteKey() );
            if ( forceNoCache )
            {
                HttpServletUtil.setCacheControlNoCache( response );
            }
            else
            {
                Integer siteCacheSettingsMaxAge =
                    sitePropertiesService.getPropertyAsInteger( SitePropertyNames.RESOURCE_CACHE_HEADERS_MAXAGE, sitePath.getSiteKey() );

                enableHttpCacheHeaders( response, sitePath, now, siteCacheSettingsMaxAge, true );

            }
        }
    }

    private ModelAndView createForwardToMenuItem( final HttpServletRequest request, final SitePath sitePath )
    {
        final Path path = new Path( sitePath.getLocalPath().toString().replace( LOCAL_PREFIX, FORWARD_PREFIX ) );
        final SitePath forward = new SitePath( sitePath.getSiteKey(), path, sitePath.getParams() );
        return siteRedirectAndForwardHelper.getForwardModelAndView( request, forward );
    }

    public void setResourceService( ResourceService resourceService )
    {
        this.resourceService = resourceService;
    }
}
package com.enonic.cms.web.portal.handler;

import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;

import com.enonic.cms.core.portal.ResourceNotFoundException;
import com.enonic.cms.core.resource.ResourceFile;
import com.enonic.cms.core.resource.ResourceKey;
import com.enonic.cms.core.resource.ResourceKeyResolverForSiteLocalResources;
import com.enonic.cms.core.resource.ResourceService;

import static com.enonic.cms.core.SitePropertyNames.RESOURCE_CACHE_HEADERS_ENABLED;
import static com.enonic.cms.core.SitePropertyNames.RESOURCE_CACHE_HEADERS_FORCENOCACHE;
import static com.enonic.cms.core.SitePropertyNames.RESOURCE_CACHE_HEADERS_MAXAGE;

final class ResourceWebHandler
    extends AbstractWebHandler
{
    private ResourceService resourceService;

    @Override
    public Response handle()
    {
        final ResourceKey publicPath = getSiteDao().findByKey( getSiteKey().toInt() ).getPathToPublicResources();
        final String sitePublicHome = publicPath != null ? publicPath.toString() : null;

        final ResourceKeyResolverForSiteLocalResources resolver = new ResourceKeyResolverForSiteLocalResources( sitePublicHome );
        final ResourceKey resourceKey = resolver.resolveResourceKey( getSitePath() );
        final ResourceFile resourceFile = this.resourceService.getResourceFile( resourceKey );

        if ( resourceFile == null )
        {
            throw new ResourceNotFoundException( getSiteKey(), getLocalPath() );
        }

        return Response.ok( resourceFile ).type( resourceFile.getMimeType() ).cacheControl( createCacheControl() ).build();
    }

    private CacheControl createCacheControl()
    {
        final boolean cacheHeadersEnabled = getSiteProperty( RESOURCE_CACHE_HEADERS_ENABLED, false );
        if ( cacheHeadersEnabled )
        {
            return null;
        }

        final boolean forceNoCache = getSiteProperty( RESOURCE_CACHE_HEADERS_FORCENOCACHE, false );
        final int maxAge = getSiteProperty( RESOURCE_CACHE_HEADERS_MAXAGE, 0 );
        final CacheControl cacheControl = new CacheControl();

        if ( forceNoCache )
        {
            cacheControl.setNoCache( true );
        }
        else
        {
            setCacheHeaders( cacheControl, maxAge );
        }

        return cacheControl;
    }

    public void setResourceService( final ResourceService resourceService )
    {
        this.resourceService = resourceService;
    }
}

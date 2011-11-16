/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.cache;

import com.enonic.cms.framework.cache.CacheFacade;
import com.enonic.cms.framework.cache.CacheManager;

import com.enonic.cms.core.SiteKey;
import com.enonic.cms.core.SitePropertiesService;
import com.enonic.cms.core.SitePropertyNames;

public class PageCacheServiceFactory
{

    private CacheManager cacheManager;

    private SitePropertiesService sitePropertiesService;

    public void setCacheManager( CacheManager value )
    {
        this.cacheManager = value;
    }

    public void setSitePropertiesService( SitePropertiesService value )
    {
        this.sitePropertiesService = value;
    }

    public PageCacheServiceImpl createPageAndObjectCacheService( SiteKey siteKey )
    {

        CacheFacade cacheFacade = cacheManager.getOrCreateCache( "page" );

        PageCacheServiceImpl cacheService = new PageCacheServiceImpl( siteKey );
        cacheService.setCacheFacade( cacheFacade );

        Integer defaultTimeToLive = sitePropertiesService.getPropertyAsInteger( SitePropertyNames.PAGE_CACHE_TIMETOLIVE, siteKey );
        cacheService.setTimeToLive( defaultTimeToLive );
        return cacheService;
    }
}

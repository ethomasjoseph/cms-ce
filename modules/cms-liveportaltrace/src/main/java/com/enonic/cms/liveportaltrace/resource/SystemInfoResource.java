package com.enonic.cms.liveportaltrace.resource;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.ThreadMXBean;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.framework.cache.CacheFacade;
import com.enonic.cms.framework.cache.CacheManager;

import com.enonic.cms.core.spring.PrototypeScope;


@Path("/liveportaltrace/rest/system")
@Produces("application/json")
@PrototypeScope
@Component
public final class SystemInfoResource
{
    private CacheManager cacheManager;

    @GET
    @Path("counters")
    public Map<String, Object> handleGet()
    {
        final Map<String, Object> model = new HashMap<String, Object>();

        final CacheFacade entityCache = cacheManager.getCache( "entity" );
        model.put( "entityCacheCount", entityCache != null ? entityCache.getCount() : 0 );
        model.put( "entityCacheHitCount", entityCache != null ? entityCache.getHitCount() : 0 );
        model.put( "entityCacheMissCount", entityCache != null ? entityCache.getMissCount() : 0 );

        final CacheFacade pageCache = cacheManager.getCache( "page" );

        model.put( "pageCacheCount", pageCache != null ? pageCache.getCount() : 0 );
        model.put( "pageCacheHitCount", pageCache != null ? pageCache.getHitCount() : 0 );
        model.put( "pageCacheMissCount", pageCache != null ? pageCache.getMissCount() : 0 );

        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
        model.put( "javaHeapMemoryUsageUsed", bytesToMegaBytes( heapMemoryUsage.getUsed() ) );
        model.put( "javaHeapMemoryUsageMax", bytesToMegaBytes( heapMemoryUsage.getMax() ) );

        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        model.put( "javaThreadCount", threadMXBean.getThreadCount() );
        model.put( "javaThreadPeakCount", threadMXBean.getPeakThreadCount() );

        return model;
    }

    private long bytesToMegaBytes( long value )
    {
        return value / ( 1024 * 1024 );
    }

    @Autowired
    public void setCacheManager( CacheManager cacheManager )
    {
        this.cacheManager = cacheManager;
    }
}

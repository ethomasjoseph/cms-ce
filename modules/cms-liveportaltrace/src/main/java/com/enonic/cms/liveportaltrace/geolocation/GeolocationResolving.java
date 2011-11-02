package com.enonic.cms.liveportaltrace.geolocation;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GeolocationResolving
{
    @Autowired
    private GeolocationService geolocationService;

    private final ExecutorService executor;

    private final ConcurrentMap<Long, GeolocationInfo> requestLocationCache;

    private final GeolocationCache cache;

    public GeolocationResolving()
    {
        executor = Executors.newSingleThreadExecutor();
        requestLocationCache = new ConcurrentHashMap<Long, GeolocationInfo>();
        cache = new GeolocationCache();
    }

    public GeolocationInfo resolveIpLocation( String ipAddress, long requestNumber )
    {
        GeolocationInfo requestLocation = requestLocationCache.get( requestNumber );
        if ( requestLocation != null )
        {
            return requestLocation;
        }
        requestLocation = cache.get( ipAddress );
        if ( requestLocation != null )
        {
            requestLocationCache.put( requestNumber, requestLocation );
            return requestLocation;
        }

        ResolverTask task = new ResolverTask( ipAddress, requestNumber, cache, requestLocationCache, geolocationService );
        executor.submit( task );

        return null;
    }
}

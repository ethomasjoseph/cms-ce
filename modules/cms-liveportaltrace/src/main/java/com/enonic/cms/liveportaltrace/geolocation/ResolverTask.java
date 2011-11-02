package com.enonic.cms.liveportaltrace.geolocation;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;

public class ResolverTask
    implements Callable
{
    private final GeolocationService geolocationService;

    private final GeolocationCache cache;

    private final String ipAddress;

    private final long requestNumber;

    private final ConcurrentMap<Long, GeolocationInfo> requestLocationCache;

    public ResolverTask( String ipAddress, long requestNumber, GeolocationCache cache,
                         ConcurrentMap<Long, GeolocationInfo> requestLocationCache, GeolocationService geolocationService )
    {
        this.ipAddress = ipAddress;
        this.requestNumber = requestNumber;
        this.cache = cache;
        this.geolocationService = geolocationService;
        this.requestLocationCache = requestLocationCache;
    }

    public Object call()
        throws Exception
    {
        GeolocationInfo location = geolocationService.findLocation( ipAddress );
        cache.put( location );
        requestLocationCache.put( requestNumber, location );
        return null;
    }

}

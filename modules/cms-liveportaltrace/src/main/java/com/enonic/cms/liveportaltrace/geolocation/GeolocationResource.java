package com.enonic.cms.liveportaltrace.geolocation;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.spring.PrototypeScope;
import com.enonic.cms.portal.livetrace.LivePortalTraceService;
import com.enonic.cms.portal.livetrace.PastPortalRequestTrace;

@Path("/liveportaltrace/rest/locations")
@Produces("application/json")
@PrototypeScope
@Component
public final class GeolocationResource
{

    @Autowired
    private GeolocationService geolocationService;

    @Autowired
    private LivePortalTraceService livePortalTraceService;

    private GeolocationCache cache;

    GeolocationResource()
    {
        cache = new GeolocationCache();
    }

    @GET
    public GeolocationInfoModel getLocationList( @DefaultValue("0") @QueryParam("lastId") final long lastId )
    {
        final GeolocationInfoModel locations = new GeolocationInfoModel();
        long lastIdSent = lastId;

        final List<PastPortalRequestTrace> requests = getLastRequests( lastId );
        for ( PastPortalRequestTrace request : requests )
        {
            GeolocationInfo location = resolveLocation( request );
            if ( location != null )
            {
                locations.addLocation( location );
            }
            lastIdSent = Math.max( lastIdSent, request.getPortalRequestTrace().getRequestNumber() );
        }

        locations.setLastId( lastIdSent );
        return locations;
    }

    private GeolocationInfo resolveLocation( PastPortalRequestTrace request )
    {
        String ipAddress = request.getPortalRequestTrace().getHttpRequestRemoteAddress();
        ipAddress = getRandomIpAddress();
        GeolocationInfo location = cache.get( ipAddress );
        if ( location == null )
        {
            location = geolocationService.findLocation( ipAddress );
            if ( location != null )
            {
                cache.put( location );
            }
        }

        return location;
    }

    private List<PastPortalRequestTrace> getLastRequests( final long lastId )
    {
        // TODO replace this with a listener for portal-trace events
        final List<PastPortalRequestTrace> newRequestsSinceLast = livePortalTraceService.getHistorySince( lastId );
        return newRequestsSinceLast;
    }

    private String getRandomIpAddress()
    {
        final String[] ipAddresses =
            new String[]{"213.225.69.102", "213.179.32.0", "213.184.192.0", "213.187.160.0", "213.188.0.0", "213.188.128.0", // Oslo
                "74.6.22.163", "74.6.22.163",// NY
                "88.15.116.0", "88.15.116.0" // BCN
            };
        int randomIdx = (int) ( Math.random() * ( ipAddresses.length ) );

        return ipAddresses[randomIdx];
    }
}

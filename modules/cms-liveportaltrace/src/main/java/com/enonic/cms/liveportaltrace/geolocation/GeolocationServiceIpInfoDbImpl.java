package com.enonic.cms.liveportaltrace.geolocation;

import java.io.IOException;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Ip to location service implementation.
 * Uses REST API from IPInfoDB.com
 */
@Service
public class GeolocationServiceIpInfoDbImpl
    implements GeolocationService
{
    private static final Logger LOG = LoggerFactory.getLogger( GeolocationServiceIpInfoDbImpl.class );

    private static final String IP_LOCATION_RESOLVER_API_KEY = "35de0d0d05cd482d3b7fe40285c4a8b43e4ec6e1520b0f95af9a6a78bf91e078";

    private Gson gson;

    public GeolocationServiceIpInfoDbImpl()
    {
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public GeolocationInfo findLocation( String ipAddress )
    {
        String url = "http://api.ipinfodb.com/v3/ip-city/?key=" + IP_LOCATION_RESOLVER_API_KEY + "&ip=" + ipAddress + "&format=json";

        HttpClient client = new HttpClient();
        GetMethod method = new GetMethod( url );
        method.getParams().setParameter( HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler( 3, false ) );

        try
        {
            int statusCode = client.executeMethod( method );
            if ( statusCode != HttpStatus.SC_OK )
            {
                LOG.warn( "Unable to resolve ip address, http response: " + method.getStatusLine() );
                return null;
            }
            byte[] responseBody = method.getResponseBody();
            String jsonText = new String( responseBody );
            if ( LOG.isInfoEnabled() )
            {
                LOG.info( "Geolocation response for ip " + ipAddress + ": " + jsonText );
            }

            IpInfoDbResponse response = gson.fromJson( jsonText, IpInfoDbResponse.class );

            GeolocationInfo location = new GeolocationInfo();
            location.setIpAddress( ipAddress );
            location.setLongitude( response.getLongitude() );
            location.setLatitude( response.getLatitude() );
            location.setCity( response.getCityName() );
            location.setCountry( response.getCountryName() );

            return location;
        }
        catch ( HttpException e )
        {
            LOG.warn( "Unable to resolve ip address, http error: " + e.getMessage(), e );
            return null;
        }
        catch ( IOException e )
        {
            LOG.warn( "Unable to resolve ip address, connection error: " + e.getMessage(), e );
            return null;
        }
        finally
        {
            method.releaseConnection();
        }
    }
}

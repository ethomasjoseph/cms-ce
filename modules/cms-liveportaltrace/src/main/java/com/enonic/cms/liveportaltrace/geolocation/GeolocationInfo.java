package com.enonic.cms.liveportaltrace.geolocation;


public class GeolocationInfo
{

    private String ipAddress;

    private double latitude;

    private double longitude;


    GeolocationInfo()
    {

    }

    public String getIpAddress()
    {
        return ipAddress;
    }

    public void setIpAddress( String ipAddress )
    {
        this.ipAddress = ipAddress;
    }

    public double getLatitude()
    {
        return latitude;
    }

    public void setLatitude( double latitude )
    {
        this.latitude = latitude;
    }

    public double getLongitude()
    {
        return longitude;
    }

    public void setLongitude( double longitude )
    {
        this.longitude = longitude;
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder();
        sb.append( "LocationInfo" );
        sb.append( "{ipAddress='" ).append( ipAddress ).append( '\'' );
        sb.append( ", latitude=" ).append( latitude );
        sb.append( ", longitude=" ).append( longitude );
        sb.append( '}' );
        return sb.toString();
    }
}

package com.enonic.cms.liveportaltrace.geolocation;


public class RandomIpAddressGenerator
{
    public String generate()
    {
        int[] ip = generateIpOctets();
        while ( isPrivateAddress( ip ) )
        {
            ip = generateIpOctets();
        }
        return octetsToAddress( ip );
    }

    private String octetsToAddress( int[] octets )
    {
        StringBuilder sb = new StringBuilder();
        for ( int octet : octets )
        {
            if ( sb.length() > 0 )
            {
                sb.append( "." );
            }
            sb.append( octet );
        }
        return sb.toString();
    }

    // unicast public addresses (classes A, B, C)
    private int[] generateIpOctets()
    {
        int[] bytes = new int[4];
        bytes[0] = randomValue( 222 ) + 1;
        bytes[1] = randomValue( 255 );
        bytes[2] = randomValue( 255 );
        bytes[3] = randomValue( 255 );
        return bytes;
    }

    private boolean isPrivateAddress( int[] octets )
    {
        if ( octets[0] == 10 )
        {
            // block 10.0.0.0 - 10.255.255.255
            return true;
        }

        if ( ( octets[0] == 172 ) && ( ( octets[1] >= 16 ) && ( octets[1] <= 31 ) ) )
        {
            // block 172.16.0.0 - 172.31.255.255
            return true;
        }

        if ( ( octets[0] == 192 ) && ( octets[1] == 168 ) )
        {
            // block 192.168.0.0 - 192.168.255.255
            return true;
        }
        return false;
    }

    private int randomValue( int max )
    {
        Double value = Math.random() * max;
        return value.intValue();
    }
//
//    public static void main( String... args )
//    {
//        RandomIpAddressGenerator rnd = new RandomIpAddressGenerator();
//        for (int i = 0; i < 100; i++) {
//        rnd.generate();
//        }
//    }
}

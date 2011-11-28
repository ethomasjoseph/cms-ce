package com.enonic.cms.admin.timezone;

import java.util.Collection;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;


public final class TimezoneModelHelper
{
    public static TimezoneModel toModel(final DateTimeZone entity)
    {
        final TimezoneModel model = new TimezoneModel();
        long currTime = System.currentTimeMillis();
        if (entity != null) {
            model.setId( entity.getID() );
            model.setShortName( entity.getShortName( currTime ) );
            model.setName( entity.getName( currTime ) );
            DateTime now = new DateTime(  );
            DateTime local = now.plus( entity.getOffsetFromLocal( now.getMillis() ) );
            Period offsetPeriod = new Period( now, local );
            model.setOffset( getHoursAsHumanReadable (offsetPeriod) );
        }
        return model;
    }

    public static TimezonesModel toModel(final Collection<DateTimeZone> list)
    {
        final TimezonesModel model = new TimezonesModel();
        model.setTotal(list.size());

        for (final DateTimeZone entity : list) {
            model.addTimezone(toModel(entity));
        }
        
        return model;
    }

    private static String getHoursAsHumanReadable( Period offsetPeriod )
    {
        final StringBuffer s = new StringBuffer();
        if ( offsetPeriod.getMinutes() < 0 )
        {
            s.append( "-" );
        }
        else
        {
            s.append( "+" );
        }

        final int hours = offsetPeriod.getHours();

        if ( hours < 10 && hours > ( -10 ) )
        {
            s.append( "0" );
        }
        s.append( Math.abs( hours ) );
        s.append( ":" );

        final int minutes = offsetPeriod.getMinutes();
        if ( minutes < 10 )
        {
            s.append( "0" );
        }
        s.append( minutes );
        return s.toString();
    }
}

package com.enonic.cms.liveportaltrace.model;


import java.util.Date;

import com.enonic.cms.portal.livetrace.Duration;

public class DurationModel
{
    private Date startTime;

    private Date stopTime;

    private long milliseconds;

    private String humanReadable;

    public DurationModel( Date startTime, Date stopTime, long durationTimeInMilliseconds, String durationTimeAsHRFormat )
    {
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.milliseconds = durationTimeInMilliseconds;
        this.humanReadable = durationTimeAsHRFormat;
    }

    public static DurationModel create( Duration duration )
    {
        return new DurationModel( duration.getStartTime() != null ? duration.getStartTime().toDate() : null,
                                  duration.getStopTime() != null ? duration.getStopTime().toDate() : null,
                                  duration.getExecutionTimeInMilliseconds(), duration.getExecutionTimeAsHRFormat() );
    }

    public Date getStartTime()
    {
        return startTime;
    }

    public Date getStopTime()
    {
        return stopTime;
    }

    public long getMilliseconds()
    {
        return milliseconds;
    }

    public String getHumanReadable()
    {
        return humanReadable;
    }
}

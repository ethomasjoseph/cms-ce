package com.enonic.cms.admin.timezone;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.timezone.TimeZoneService;

@Component
@Path("/admin/data/misc/timezone")
@Produces(MediaType.APPLICATION_JSON)
public final class TimezoneResource
{

    @Autowired
    private TimeZoneService timezoneService;

    @GET
    @Path("list")
    public TimezonesModel getAll()
    {
        final List<DateTimeZone> list = this.timezoneService.getTimeZones();
        return TimezoneModelHelper.toModel(list);
    }

}

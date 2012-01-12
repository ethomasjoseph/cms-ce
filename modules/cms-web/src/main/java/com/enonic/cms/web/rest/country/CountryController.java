package com.enonic.cms.web.rest.country;

import java.util.Collection;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.country.CountryService;

@Component
@Path("country")
@Produces(MediaType.APPLICATION_JSON)
public final class CountryController
{
    private CountryService service;

    @GET
    public Collection<CountryModel> getCountries()
    {
        return CountryModelHelper.toModelList( this.service.getCountries() );
    }

    @Autowired
    public void setCountryService(final CountryService service)
    {
        this.service = service;
    }
}

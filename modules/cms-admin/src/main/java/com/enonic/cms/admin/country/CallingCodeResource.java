package com.enonic.cms.admin.country;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sun.jersey.api.core.InjectParam;

import com.enonic.cms.admin.common.LoadStoreRequest;
import com.enonic.cms.core.country.Country;
import com.enonic.cms.core.country.CountryService;

/**
 * Service for building and return list of calling codes,
 * filtering is performed according to query parameter
 */

@Component
@Path("/admin/data/misc/callingcodes")
@Produces(MediaType.APPLICATION_JSON)
public class CallingCodeResource
{

    @Autowired
    public CountryService countryService;

    @GET
    @Path("list")
    public CallingCodesModel getAll()
    {
        List<CallingCodeModel> list = new ArrayList<CallingCodeModel>();
        for ( Country c : countryService.getCountries() )
        {
            list.add( CallingCodeModelTranslator.toModel( c ) );
        }
        CallingCodesModel codes = new CallingCodesModel();
        codes.setCodes( list );
        codes.setTotal( list.size() );
        return codes;
    }
}

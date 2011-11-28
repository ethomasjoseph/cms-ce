package com.enonic.cms.admin.locale;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sun.jersey.api.core.InjectParam;

import com.enonic.cms.admin.common.LoadStoreRequest;
import com.enonic.cms.core.locale.LocaleService;

@Component
@Path("/admin/data/misc/locale")
@Produces("application/json")
public class LocaleResource
{

    @Autowired
    private LocaleService localeService;

    @GET
    @Path("list")
    public LocalesModel getAll(@InjectParam final LoadStoreRequest req)
    {
        final List<Locale> list = Arrays.asList( this.localeService.getLocales() );
        return LocaleModelHelper.toModel( list );
    }
}

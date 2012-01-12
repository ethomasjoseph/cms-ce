package com.enonic.cms.web.admin;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.springframework.stereotype.Component;

import com.enonic.cms.web.common.AbstractStaticController;

@Component
@Path("/resources")
public final class AdminStaticController
    extends AbstractStaticController
{
    public AdminStaticController()
    {
        setBasePath( "/admin" );
    }

    @GET
    @Path("{path:.+}")
    public Response getResource(@PathParam("path") final String path)
        throws Exception
    {
        return serveResource( path );
    }
}

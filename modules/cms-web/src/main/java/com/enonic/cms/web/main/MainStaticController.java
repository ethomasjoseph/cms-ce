package com.enonic.cms.web.main;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.springframework.stereotype.Component;

import com.enonic.cms.web.common.AbstractStaticController;

@Component
@Path("/resources")
public final class MainStaticController
    extends AbstractStaticController
{
    public MainStaticController()
    {
        setBasePath( "/main" );
    }

    @GET
    @Path("{path:.+}")
    public Response getResource(@PathParam("path") final String path)
        throws Exception
    {
        return serveResource( path );
    }
}

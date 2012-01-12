package com.enonic.cms.web.portal;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.web.portal.handler.WebHandlerFactory;

@Component
@Path("{site:[0-9]+}")
public final class SiteController
{
    private WebHandlerFactory webHandlerFactory;

    @GET
    @Path("_public/{path:.*}")
    public Response serveResource( @Context HttpServletRequest req )
    {
        return this.webHandlerFactory.createResourceHandler( req ).handle();
    }

    @GET
    @Path("_image/{path:.*}")
    public Response serveImageFromRoot( @Context HttpServletRequest req )
    {
        return this.webHandlerFactory.createImageHandler( req ).handle();
    }

    @GET
    @Path("{item:.+}/_image/{path:.*}")
    public Response serveImageFromItem( @Context HttpServletRequest req )
    {
        return this.webHandlerFactory.createImageHandler( req ).handle();
    }

    @GET
    @Path("_attachment/{path:.*}")
    public Response serveAttachmentFromRoot( @Context HttpServletRequest req )
    {
        return this.webHandlerFactory.createAttachmentHandler( req ).handle();
    }

    @GET
    @Path("{item:.+}/_attachment/{path:.*}")
    public Response serveAttachmentFromItem( @Context HttpServletRequest req )
    {
        return this.webHandlerFactory.createAttachmentHandler( req ).handle();
    }

    @GET
    public Response servePageOrWindow( @Context HttpServletRequest req )
    {
        return this.webHandlerFactory.createRenderHandler( req ).handle();
    }

    @GET
    @Path("{path:.*}")
    public Response servePageOrWindowPath( @Context HttpServletRequest req )
    {
        return this.webHandlerFactory.createRenderHandler( req ).handle();
    }

    @Autowired
    public void setWebHandlerFactory( final WebHandlerFactory webHandlerFactory )
    {
        this.webHandlerFactory = webHandlerFactory;
    }
}

package com.enonic.cms.web.portal.handler;

import javax.ws.rs.core.Response;

import com.enonic.cms.core.portal.PortalRequestService;

final class RenderWebHandler
    extends AbstractWebHandler
{
    private PortalRequestService portalRequestService;

    @Override
    public Response handle()
    {
        return Response.ok( "render" ).build();
    }

    public void setPortalRequestService( final PortalRequestService portalRequestService )
    {
        this.portalRequestService = portalRequestService;
    }
}

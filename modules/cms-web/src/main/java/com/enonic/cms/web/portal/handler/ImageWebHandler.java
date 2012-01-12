package com.enonic.cms.web.portal.handler;

import javax.ws.rs.core.Response;

import com.enonic.cms.core.portal.image.ImageService;

final class ImageWebHandler
    extends AbstractWebHandler
{
    private ImageService imageService;

    @Override
    public Response handle()
    {
        return Response.ok( "image" ).build();
    }

    public void setImageService( final ImageService imageService )
    {
        this.imageService = imageService;
    }
}

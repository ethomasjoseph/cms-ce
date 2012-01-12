package com.enonic.cms.web.portal.handler;

import javax.servlet.http.HttpServletRequest;

public interface WebHandlerFactory
{
    public WebHandler createImageHandler( final HttpServletRequest req );

    public WebHandler createResourceHandler( final HttpServletRequest req );

    public WebHandler createRenderHandler( final HttpServletRequest req );

    public WebHandler createAttachmentHandler( final HttpServletRequest req );
}

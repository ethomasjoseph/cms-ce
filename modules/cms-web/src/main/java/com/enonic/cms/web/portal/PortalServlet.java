package com.enonic.cms.web.portal;

import com.enonic.cms.web.common.AbstractRestServlet;
import com.enonic.cms.web.portal.provider.ResourceFileBodyWriter;

public final class PortalServlet
    extends AbstractRestServlet
{
    public PortalServlet()
    {
        addClass( ResourceFileBodyWriter.class );
        addClass( SiteController.class );
    }
}

package com.enonic.cms.web.admin;

import com.enonic.cms.web.common.AbstractRestServlet;

public final class AdminServlet
    extends AbstractRestServlet
{
    public AdminServlet()
    {
        addClass( AdminStaticController.class );
    }
}

package com.enonic.cms.web.main;

import com.enonic.cms.web.common.AbstractRestServlet;

public final class MainServlet
    extends AbstractRestServlet
{
    public MainServlet()
    {
        addClass( WelcomeController.class );
        addClass( MainStaticController.class );
    }
}

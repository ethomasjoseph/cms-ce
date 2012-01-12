package com.enonic.cms.web.rest;

import com.enonic.cms.web.common.AbstractRestServlet;
import com.enonic.cms.web.rest.country.CountryController;

public final class RestServlet
    extends AbstractRestServlet
{
    public RestServlet()
    {
        addClass( CountryController.class );
    }
}

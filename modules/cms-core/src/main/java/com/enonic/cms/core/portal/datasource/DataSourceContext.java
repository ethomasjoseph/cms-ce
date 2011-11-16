/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.datasource;

import com.google.common.base.Preconditions;

import com.enonic.cms.core.SiteKey;
import com.enonic.cms.core.portal.PortalInstanceKey;
import com.enonic.cms.core.preview.PreviewContext;
import com.enonic.cms.core.security.user.UserEntity;

public class DataSourceContext
{
    private SiteKey siteKey = null;

    private PortalInstanceKey portalInstanceKey;

    private UserEntity user;

    private PreviewContext previewContext;

    public DataSourceContext( PreviewContext previewContext )
    {
        Preconditions.checkNotNull( previewContext );

        this.previewContext = previewContext;
    }

    public DataSourceContext()
    {
        this.previewContext = PreviewContext.NO_PREVIEW;
    }

    public void setSiteKey( final SiteKey value )
    {
        siteKey = value;
    }

    public void setPortalInstanceKey( final PortalInstanceKey value )
    {
        portalInstanceKey = value;
    }

    public void setUser( final UserEntity user )
    {
        this.user = user;
    }

    public SiteKey getSiteKey()
    {
        return siteKey;
    }

    public PortalInstanceKey getPortalInstanceKey()
    {
        return portalInstanceKey;
    }

    public UserEntity getUser()
    {
        return user;
    }

    public PreviewContext getPreviewContext()
    {
        return previewContext;
    }
}

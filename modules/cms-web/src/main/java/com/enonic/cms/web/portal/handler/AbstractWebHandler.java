package com.enonic.cms.web.portal.handler;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;

import com.google.common.base.Strings;

import com.enonic.cms.core.Path;
import com.enonic.cms.core.SiteKey;
import com.enonic.cms.core.SitePath;
import com.enonic.cms.core.SitePropertiesService;
import com.enonic.cms.store.dao.SiteDao;

abstract class AbstractWebHandler
    implements WebHandler
{
    private final static int CACHE_FOREVER_SECONDS = 31536000;

    private SitePath sitePath;

    private SitePropertiesService sitePropertiesService;

    private SiteDao siteDao;

    private HttpServletRequest request;

    protected final boolean getSiteProperty( final String propertyKey, final boolean defValue )
    {
        final Boolean value = this.sitePropertiesService.getPropertyAsBoolean( propertyKey, getSiteKey() );
        return value != null ? value : defValue;
    }

    protected final int getSiteProperty( final String propertyKey, final int defValue )
    {
        final Integer value = this.sitePropertiesService.getPropertyAsInteger( propertyKey, getSiteKey() );
        return value != null ? value : defValue;
    }

    public abstract Response handle();

    protected final SiteKey getSiteKey()
    {
        return this.sitePath.getSiteKey();
    }

    protected final SitePath getSitePath()
    {
        return this.sitePath;
    }

    protected final Path getLocalPath()
    {
        return this.sitePath.getLocalPath();
    }

    protected final SiteDao getSiteDao()
    {
        return this.siteDao;
    }

    protected final HttpServletRequest getRequest()
    {
        return request;
    }

    public final void setSitePath( final SitePath sitePath )
    {
        this.sitePath = sitePath;
    }

    public final void setSitePropertiesService( final SitePropertiesService sitePropertiesService )
    {
        this.sitePropertiesService = sitePropertiesService;
    }

    public final void setSiteDao( final SiteDao siteDao )
    {
        this.siteDao = siteDao;
    }

    public final void setRequest( final HttpServletRequest request )
    {
        this.request = request;
    }

    protected final void setCacheHeaders( final CacheControl cacheControl, final int maxAge )
    {
        final boolean cacheForever = hasTimestampParameter();
        cacheControl.setMaxAge( cacheForever ? CACHE_FOREVER_SECONDS : maxAge );
    }

    private boolean hasTimestampParameter()
    {
        final String timestamp = this.sitePath.getParam( "_ts" );
        return !Strings.isNullOrEmpty( timestamp );
    }
}

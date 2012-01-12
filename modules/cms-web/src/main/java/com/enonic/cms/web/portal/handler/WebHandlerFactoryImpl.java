package com.enonic.cms.web.portal.handler;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.Attribute;
import com.enonic.cms.core.SitePath;
import com.enonic.cms.core.SitePathResolver;
import com.enonic.cms.core.SitePropertiesService;
import com.enonic.cms.core.portal.PortalRequestService;
import com.enonic.cms.core.portal.image.ImageService;
import com.enonic.cms.core.resource.ResourceService;
import com.enonic.cms.core.structure.SiteService;
import com.enonic.cms.store.dao.BinaryDataDao;
import com.enonic.cms.store.dao.SiteDao;

@Component
public final class WebHandlerFactoryImpl
    implements WebHandlerFactory
{
    private SiteService siteService;

    private SitePathResolver sitePathResolver;

    private SitePropertiesService sitePropertiesService;

    private SiteDao siteDao;

    private ResourceService resourceService;

    private ImageService imageService;

    private BinaryDataDao binaryDataDao;

    private PortalRequestService portalRequestService;

    public WebHandler createImageHandler( final HttpServletRequest req )
    {
        final ImageWebHandler handler = setupHandler( new ImageWebHandler(), req );
        handler.setImageService( this.imageService );
        return handler;
    }

    public WebHandler createResourceHandler( final HttpServletRequest req )
    {
        final ResourceWebHandler handler = setupHandler( new ResourceWebHandler(), req );
        handler.setResourceService( this.resourceService );
        return handler;
    }

    public WebHandler createRenderHandler( final HttpServletRequest req )
    {
        final RenderWebHandler handler = setupHandler( new RenderWebHandler(), req );
        handler.setPortalRequestService( this.portalRequestService );
        return handler;
    }

    public WebHandler createAttachmentHandler( final HttpServletRequest req )
    {
        final AttachmentWebHandler handler = setupHandler( new AttachmentWebHandler(), req );
        handler.setBinaryDataDao( this.binaryDataDao );
        return handler;
    }

    private <T extends AbstractWebHandler> T setupHandler( final T handler, final HttpServletRequest req )
    {
        handler.setSitePath( createSitePath( req ) );
        handler.setSiteDao( this.siteDao );
        handler.setSitePropertiesService( this.sitePropertiesService );
        handler.setRequest( req );
        return handler;
    }

    private SitePath createSitePath( final HttpServletRequest req )
    {
        // Get check and eventually set original sitePath
        SitePath originalSitePath = (SitePath) req.getAttribute( Attribute.ORIGINAL_SITEPATH );
        if ( originalSitePath == null )
        {
            originalSitePath = this.sitePathResolver.resolveSitePath( req );
            this.siteService.checkSiteExist( originalSitePath.getSiteKey() );
            req.setAttribute( Attribute.ORIGINAL_SITEPATH, originalSitePath );
        }

        // Get and set the current sitePath
        final SitePath currentSitePath = this.sitePathResolver.resolveSitePath( req );
        req.setAttribute( Attribute.CURRENT_SITEPATH, currentSitePath );

        return currentSitePath;
    }

    @Autowired
    public void setSiteService( final SiteService siteService )
    {
        this.siteService = siteService;
    }

    @Autowired
    public void setSitePathResolver( final SitePathResolver sitePathResolver )
    {
        this.sitePathResolver = sitePathResolver;
    }

    @Autowired
    public void setSitePropertiesService( final SitePropertiesService sitePropertiesService )
    {
        this.sitePropertiesService = sitePropertiesService;
    }

    @Autowired
    public void setSiteDao( final SiteDao siteDao )
    {
        this.siteDao = siteDao;
    }

    @Autowired
    public void setResourceService( final ResourceService resourceService )
    {
        this.resourceService = resourceService;
    }

    @Autowired
    public void setImageService( final ImageService imageService )
    {
        this.imageService = imageService;
    }

    @Autowired
    public void setBinaryDataDao( final BinaryDataDao binaryDataDao )
    {
        this.binaryDataDao = binaryDataDao;
    }

    @Autowired
    public void setPortalRequestService( final PortalRequestService portalRequestService )
    {
        this.portalRequestService = portalRequestService;
    }
}

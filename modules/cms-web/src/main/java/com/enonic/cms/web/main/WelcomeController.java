package com.enonic.cms.web.main;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.api.Version;
import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.store.dao.SiteDao;
import com.enonic.cms.upgrade.UpgradeService;
import com.enonic.cms.web.common.freemarker.FreeMarkerModel;

@Path("/")
@Component
public final class WelcomeController
{
    private SiteDao siteDao;

    private UpgradeService upgradeService;

    @Autowired
    public void setSiteDao( final SiteDao siteDao )
    {
        this.siteDao = siteDao;
    }

    @Autowired
    public void setUpgradeService( final UpgradeService upgradeService )
    {
        this.upgradeService = upgradeService;
    }

    private Map<String, Integer> createSiteMap()
    {
        final HashMap<String, Integer> siteMap = new HashMap<String, Integer>();
        for ( final SiteEntity entity : this.siteDao.findAll() )
        {
            siteMap.put( entity.getName(), entity.getKey().toInt() );
        }

        return siteMap;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public FreeMarkerModel getIndex( @Context final UriInfo uriInfo )
    {
        final boolean modelUpgradeNeeded = this.upgradeService.needsUpgrade();
        final boolean softwareUpgradeNeeded = this.upgradeService.needsSoftwareUpgrade();
        final boolean upgradeNeeded = modelUpgradeNeeded || softwareUpgradeNeeded;

        final FreeMarkerModel model = FreeMarkerModel.create( "welcomePage.ftl" );

        model.put( "versionTitle", Version.getTitle() );
        model.put( "versionTitleVersion", Version.getTitleAndVersion() );
        model.put( "versionCopyright", Version.getCopyright() );
        model.put( "baseUrl", createBaseUrl( uriInfo ) );

        if ( !upgradeNeeded )
        {
            model.put( "sites", createSiteMap() );
        }

        model.put( "upgradeNeeded", upgradeNeeded );
        model.put( "modelUpgradeNeeded", modelUpgradeNeeded );
        model.put( "softwareUpgradeNeeded", softwareUpgradeNeeded );
        model.put( "upgradeFrom", this.upgradeService.getCurrentModelNumber() );
        model.put( "upgradeTo", this.upgradeService.getTargetModelNumber() );

        return model;
    }

    private String createBaseUrl( final UriInfo uriInfo )
    {
        final String path = uriInfo.getBaseUri().toString().trim();
        if ( path.endsWith( "/" ) )
        {
            return path.substring( 0, path.length() - 1 ).trim();
        }
        else
        {
            return path;
        }
    }
}

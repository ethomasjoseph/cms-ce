/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.rendering;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.enonic.vertical.VerticalProperties;

import com.enonic.cms.core.time.TimeService;

import com.enonic.cms.core.SitePropertiesService;
import com.enonic.cms.core.SiteURLResolver;
import com.enonic.cms.core.TightestCacheSettingsResolver;
import com.enonic.cms.core.plugin.PluginManager;
import com.enonic.cms.core.portal.cache.SiteCachesService;
import com.enonic.cms.core.portal.datasource.DatasourceExecutorFactory;
import com.enonic.cms.core.portal.instruction.PostProcessInstructionExecutor;
import com.enonic.cms.core.portal.livetrace.LivePortalTraceService;
import com.enonic.cms.core.portal.rendering.viewtransformer.PageTemplateXsltViewTransformer;
import com.enonic.cms.core.preference.PreferenceService;
import com.enonic.cms.core.resource.ResourceService;
import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.service.DataSourceService;

public class PageRendererFactory
{
    @Autowired
    @Qualifier("siteCachesService")
    private SiteCachesService siteCachesService;

    @Autowired
    private PreferenceService preferenceService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private DatasourceExecutorFactory datasourceExecutorFactory;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private PageTemplateXsltViewTransformer pageTemplateXsltViewTransformer;

    @Autowired
    private SitePropertiesService sitePropertiesService;

    @Autowired
    private SiteURLResolver siteURLResolver;

    @Autowired
    private VerticalProperties verticalProperties;

    @Autowired
    private TightestCacheSettingsResolver tightestCacheSettingsResolver;

    @Autowired
    private TimeService timeService;

    @Autowired
    private PostProcessInstructionExecutor postProcessInstructionExecutor;

    @Autowired
    private LivePortalTraceService livePortalTraceService;

    @Autowired
    private DataSourceService dataSourceService;

    @Autowired
    private PluginManager pluginManager;

    public PageRenderer createPageRenderer( PageRendererContext pageRendererContext )
    {
        PageRenderer pageRenderer = new PageRenderer( pageRendererContext, livePortalTraceService );

        pageRenderer.setDataSourceExecutorFactory( datasourceExecutorFactory );
        pageRenderer.setPageTemplateXsltViewTransformer( pageTemplateXsltViewTransformer );
        pageRenderer.setResourceService( resourceService );
        pageRenderer.setPageCacheService( siteCachesService.getPageCacheService( pageRendererContext.getSite().getKey() ) );
        pageRenderer.setVerticalProperties( verticalProperties );
        pageRenderer.setSiteURLResolver( siteURLResolver );
        pageRenderer.setSitePropertiesService( sitePropertiesService );
        pageRenderer.setTightestCacheSettingsResolver( tightestCacheSettingsResolver );
        pageRenderer.setTimeService( timeService );
        pageRenderer.setPostProcessInstructionExecutor( postProcessInstructionExecutor );
        pageRenderer.setDataSourceService( dataSourceService );
        pageRenderer.setPluginManager( pluginManager );

        return pageRenderer;
    }
}

package com.enonic.cms.web.common;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ResourceFilterFactory;
import com.sun.jersey.spi.container.WebApplication;
import com.sun.jersey.spi.container.servlet.ServletContainer;
import com.sun.jersey.spi.container.servlet.WebConfig;

import com.enonic.cms.web.common.freemarker.FreeMarkerProvider;
import com.enonic.cms.web.common.json.JsonProvider;
import com.enonic.cms.web.common.spring.SpringComponentProviderFactory;

public abstract class AbstractRestServlet
    extends ServletContainer
{
    private final List<String> requestFilters;

    private final List<String> responseFilters;

    private final List<String> resourceFilterFactories;

    private final Set<Class<?>> resourceClasses;

    public AbstractRestServlet()
    {
        this.requestFilters = Lists.newArrayList();
        this.responseFilters = Lists.newArrayList();
        this.resourceFilterFactories = Lists.newArrayList();
        this.resourceClasses = Sets.newHashSet();

        addClass( FreeMarkerProvider.class );
        addClass( JsonProvider.class );
    }

    protected final void addClass( final Class<?> clz )
    {
        if ( isResourceClass( clz ) )
        {
            this.resourceClasses.add( clz );
        }

        if ( ContainerRequestFilter.class.isAssignableFrom( clz ) )
        {
            this.requestFilters.add( clz.getName() );
        }

        if ( ContainerResponseFilter.class.isAssignableFrom( clz ) )
        {
            this.responseFilters.add( clz.getName() );
        }

        if ( ResourceFilterFactory.class.isAssignableFrom( clz ) )
        {
            this.resourceFilterFactories.add( clz.getName() );
        }
    }

    private boolean isResourceClass( final Class<?> clz )
    {
        return ResourceConfig.isProviderClass( clz ) || ResourceConfig.isRootResourceClass( clz );
    }

    @Override
    protected void initiate( final ResourceConfig rc, final WebApplication wa )
    {
        rc.getClasses().addAll( this.resourceClasses );

        final Map<String, Object> props = rc.getProperties();
        props.put( ResourceConfig.PROPERTY_CONTAINER_REQUEST_FILTERS, getRequestFilters() );
        props.put( ResourceConfig.PROPERTY_CONTAINER_RESPONSE_FILTERS, getResponseFilters() );
        props.put( ResourceConfig.PROPERTY_RESOURCE_FILTER_FACTORIES, getResourceFilterFactories() );

        final Map<String, Boolean> features = rc.getFeatures();
        features.put( ResourceConfig.FEATURE_DISABLE_WADL, true );
        features.put( ResourceConfig.FEATURE_CANONICALIZE_URI_PATH, true );

        wa.initiate( rc, new SpringComponentProviderFactory( rc, getSpringContext() ) );
    }

    private ConfigurableApplicationContext getSpringContext()
    {
        return (ConfigurableApplicationContext) WebApplicationContextUtils.getRequiredWebApplicationContext( getServletContext() );
    }

    @Override
    protected final ResourceConfig getDefaultResourceConfig( final Map<String, Object> props, final WebConfig webConfig )
    {
        return new DefaultResourceConfig();
    }

    private String getRequestFilters()
    {
        return Joiner.on( "," ).join( this.requestFilters );
    }

    private String getResponseFilters()
    {
        return Joiner.on( "," ).join( this.responseFilters );
    }

    private String getResourceFilterFactories()
    {
        return Joiner.on( "," ).join( this.resourceFilterFactories );
    }
}


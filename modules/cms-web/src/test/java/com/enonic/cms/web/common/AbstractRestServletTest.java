package com.enonic.cms.web.common;

import java.util.List;
import java.util.Set;

import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;

import org.junit.Test;

import static org.junit.Assert.*;

import org.springframework.mock.web.MockServletConfig;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;

import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ResourceFilter;
import com.sun.jersey.spi.container.ResourceFilterFactory;
import com.sun.jersey.spi.container.WebApplication;

import com.enonic.cms.web.common.freemarker.FreeMarkerProvider;
import com.enonic.cms.web.common.json.JsonProvider;

public class AbstractRestServletTest
{
    private class TestServlet
        extends AbstractRestServlet
    {
        protected ResourceConfig config;

        public TestServlet( final Class<?>... list )
        {
            for ( final Class<?> clz : list )
            {
                addClass( clz );
            }
        }

        @Override
        protected void initiate( final ResourceConfig rc, final WebApplication wa )
        {
            super.initiate( rc, wa );
            this.config = rc;
        }
    }

    @Path("/")
    public static class MyResource
    {
    }

    @Provider
    public static class MyProvider
    {
    }

    public static class MyFilter
        implements ContainerRequestFilter, ContainerResponseFilter
    {
        public ContainerRequest filter( final ContainerRequest request )
        {
            return request;
        }

        public ContainerResponse filter( final ContainerRequest request, final ContainerResponse response )
        {
            return response;
        }
    }

    public static class MyFilterFactory
        implements ResourceFilterFactory
    {
        public List<ResourceFilter> create( final AbstractMethod am )
        {
            return null;
        }
    }

    @Test
    public void testInit()
        throws Exception
    {
        final TestServlet servlet = createServlet( MyResource.class, MyProvider.class, MyFilter.class, MyFilterFactory.class );

        final Set<Class<?>> classes = servlet.config.getClasses();
        assertTrue( classes.contains( JsonProvider.class ) );
        assertTrue( classes.contains( FreeMarkerProvider.class ) );
        assertTrue( classes.contains( MyResource.class ) );
        assertTrue( classes.contains( MyProvider.class ) );

        final List requestFilters = servlet.config.getContainerRequestFilters();
        assertEquals( 1, requestFilters.size() );
        assertEquals( MyFilter.class.getName(), requestFilters.get( 0 ) );

        final List responseFilters = servlet.config.getContainerResponseFilters();
        assertEquals( 1, responseFilters.size() );
        assertEquals( MyFilter.class.getName(), responseFilters.get( 0 ) );

        final List filterFactories = servlet.config.getResourceFilterFactories();
        assertEquals( 1, filterFactories.size() );
        assertEquals( MyFilterFactory.class.getName(), filterFactories.get( 0 ) );
    }

    private TestServlet createServlet( final Class<?>... list )
        throws Exception
    {
        final GenericWebApplicationContext springContext = new GenericWebApplicationContext();

        final MockServletContext servletContext = new MockServletContext();
        servletContext.setAttribute( WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, springContext );

        final TestServlet servlet = new TestServlet( list );
        servlet.init( new MockServletConfig( servletContext ) );

        return servlet;
    }
}

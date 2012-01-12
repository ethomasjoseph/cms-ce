package com.enonic.cms.web.common;

import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Response;

import org.springframework.web.context.ServletContextAware;

public abstract class AbstractStaticController
    implements ServletContextAware
{
    private ServletContext servletContext;

    private String basePath;

    public final void setBasePath( final String basePath )
    {
        this.basePath = basePath;
    }

    public final void setServletContext( final ServletContext servletContext )
    {
        this.servletContext = servletContext;
    }

    protected final Response serveResource( final String path )
    {
        final InputStream stream = findResource( path );
        if ( stream == null )
        {
            return null;
        }

        final String mimeType = this.servletContext.getMimeType( path );
        return Response.ok().type( mimeType ).entity( stream ).build();
    }

    private InputStream findResource( final String path )
    {
        final String fullPath = this.basePath + "/" + path;
        return this.servletContext.getResourceAsStream( fullPath );
    }
}

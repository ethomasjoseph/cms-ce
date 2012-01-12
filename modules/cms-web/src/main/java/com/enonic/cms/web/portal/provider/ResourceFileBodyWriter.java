package com.enonic.cms.web.portal.provider;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.springframework.stereotype.Component;

import com.google.common.io.ByteStreams;

import com.enonic.cms.core.resource.ResourceFile;

@Component
@Provider
public final class ResourceFileBodyWriter
    implements MessageBodyWriter<ResourceFile>
{
    public boolean isWriteable( final Class<?> aClass, final Type type, final Annotation[] annotations, final MediaType mediaType )
    {
        return ResourceFile.class.isAssignableFrom( aClass );
    }

    public long getSize( final ResourceFile resourceFile, final Class<?> aClass, final Type type, final Annotation[] annotations,
                         final MediaType mediaType )
    {
        return resourceFile.getSize();
    }

    public void writeTo( final ResourceFile resourceFile, final Class<?> aClass, final Type type, final Annotation[] annotations,
                         final MediaType mediaType, final MultivaluedMap<String, Object> httpHeaders, final OutputStream out )
        throws IOException, WebApplicationException
    {
        ByteStreams.copy( resourceFile.getDataAsInputStream(), out );
    }
}

package com.enonic.cms.web.common.freemarker;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.ServletContext;
import javax.ws.rs.core.MediaType;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;

import static org.junit.Assert.*;

public final class FreeMarkerProviderTest
{
    private FreeMarkerProvider provider;

    @Before
    public void setUp()
        throws Exception
    {
        final ServletContext context = Mockito.mock(ServletContext.class);
        Mockito.when(context.getResource("/WEB-INF/freemarker/dummy.ftl")).thenReturn(getClass().getResource("dummy.ftl"));

        final FreeMarkerConfig config = new FreeMarkerConfig();
        config.setServletContext(context);

        this.provider = new FreeMarkerProvider();
        this.provider.setConfig( config );
    }

    @Test
    public void testGetSize()
    {
        final long size = this.provider.getSize(FreeMarkerModel.create("dummy.ftl"), FreeMarkerModel.class,
                FreeMarkerModel.class, new Annotation[0], MediaType.APPLICATION_JSON_TYPE);
        assertEquals(-1, size);
    }

    @Test
    public void testIsWriteable()
    {
        assertFalse(this.provider.isWriteable(Object.class, Object.class, new Annotation[0],
                MediaType.APPLICATION_JSON_TYPE));

        assertTrue(this.provider.isWriteable(FreeMarkerModel.class, FreeMarkerModel.class, new Annotation[0],
                MediaType.APPLICATION_JSON_TYPE));
    }

    @Test
    public void testWriteTo()
        throws Exception
    {
        assertEquals("Dummy...", writeTo("dummy.ftl"));
    }

    @Test(expected = IOException.class)
    public void testWriteToFailure()
        throws Exception
    {
        writeTo("not-found.ftl");
    }

    private String writeTo(final String view)
        throws Exception
    {
        final FreeMarkerModel model = FreeMarkerModel.create(view);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();

        this.provider.writeTo(model, FreeMarkerModel.class, FreeMarkerModel.class, new Annotation[0],
                MediaType.APPLICATION_JSON_TYPE, null, out);

        return new String(out.toByteArray());
    }
}

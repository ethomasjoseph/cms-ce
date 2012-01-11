package com.enonic.cms.web.common.freemarker;

import org.junit.Test;
import static org.junit.Assert.*;

public final class FreeMarkerModelTest
{
    @Test
    public void testCreate()
        throws Exception
    {
        final FreeMarkerModel model = FreeMarkerModel.create("dummy.ftl");
        assertNotNull(model);
        assertEquals("dummy.ftl", model.getView());
    }

    @Test
    public void testChain()
        throws Exception
    {
        final FreeMarkerModel model = FreeMarkerModel.create("dummy.ftl");
        assertNotNull(model);
        assertSame(model, model.put("key", "value"));
    }

    @Test
    public void testPutParam()
        throws Exception
    {
        final FreeMarkerModel model = FreeMarkerModel.create("dummy.ftl");
        assertNotNull(model);

        assertNotNull(model.getModel());
        assertEquals(0, model.getModel().size());

        model.put("key", "value");

        assertEquals(1, model.getModel().size());
        assertEquals("value", model.getModel().get("key"));
    }
}

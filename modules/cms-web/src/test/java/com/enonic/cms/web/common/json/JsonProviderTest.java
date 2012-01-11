package com.enonic.cms.web.common.json;

import org.junit.Test;
import javax.ws.rs.core.MediaType;
import java.io.ByteArrayOutputStream;
import java.lang.annotation.Annotation;
import java.util.Date;
import static org.junit.Assert.assertEquals;

public final class JsonProviderTest
{
    private final class MyObject
    {
        public Date date;
    }

    @Test
    public void testDateFormat()
        throws Exception
    {
        final MyObject obj = new MyObject();
        obj.date = new Date(0);

        final String strValue = serialize(obj);
        assertEquals("{\"date\":\"1970-01-01 01:00:00\"}", strValue);
    }

    @Test
    public void testNullValue()
        throws Exception
    {
        final MyObject obj = new MyObject();
        obj.date = null;

        final String strValue = serialize(obj);
        assertEquals("{}", strValue);
    }

    private String serialize(final MyObject obj)
        throws Exception
    {
        final JsonProvider provider = new JsonProvider();
        final ByteArrayOutputStream out = new ByteArrayOutputStream();

        provider.writeTo(obj, MyObject.class, MyObject.class, new Annotation[0],
                MediaType.APPLICATION_JSON_TYPE, null, out);

        return new String(out.toByteArray());
    }
}

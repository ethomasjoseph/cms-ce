package com.enonic.cms.web.common.freemarker;

import freemarker.template.Template;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.ServletContext;
import java.util.Locale;
import static org.junit.Assert.assertNotNull;

public final class FreeMarkerConfigTest
{
    @Test
    public void testConstruct()
        throws Exception
    {
        final ServletContext context = Mockito.mock(ServletContext.class);
        Mockito.when(context.getResource("/WEB-INF/freemarker/dummy.ftl")).thenReturn(getClass().getResource("dummy.ftl"));
        
        final FreeMarkerConfig config = new FreeMarkerConfig();
        config.setServletContext(context);
        
        final Template template = config.getTemplate("dummy.ftl", Locale.getDefault());
        assertNotNull(template);
    }
}

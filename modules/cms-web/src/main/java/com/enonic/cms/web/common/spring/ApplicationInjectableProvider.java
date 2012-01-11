package com.enonic.cms.web.common.spring;

import javax.ws.rs.core.Context;
import org.springframework.context.ApplicationContext;
import com.sun.jersey.spi.inject.SingletonTypeInjectableProvider;

final class ApplicationInjectableProvider
    extends SingletonTypeInjectableProvider<Context, ApplicationContext>
{
    public ApplicationInjectableProvider(final ApplicationContext context)
    {
        super(ApplicationContext.class, context);
    }
}

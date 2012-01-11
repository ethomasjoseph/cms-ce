package com.enonic.cms.web.common.spring;

import javax.annotation.Nullable;
import org.springframework.context.ConfigurableApplicationContext;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProvider;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProviderFactory;

public final class SpringComponentProviderFactory
    implements IoCComponentProviderFactory
{
    private final ResourceConfig rc;

    private final ConfigurableApplicationContext springContext;

    public SpringComponentProviderFactory( final ResourceConfig rc, final ConfigurableApplicationContext springContext )
    {
        this.rc = rc;
        this.springContext = springContext;

        registerInjectableProviders();
    }

    private void registerInjectableProviders()
    {
        this.rc.getSingletons().add( new ApplicationInjectableProvider( this.springContext ) );
    }

    public IoCComponentProvider getComponentProvider( final Class<?> clz )
    {
        return getComponentProvider( null, clz );
    }

    public IoCComponentProvider getComponentProvider( @Nullable final ComponentContext context, final Class<?> clz )
    {
        final String beanName = SpringComponentProviderHelper.getBeanName( this.springContext, clz );
        if (beanName == null) {
            return null;
        }

        final ComponentScope scope = SpringComponentProviderHelper.getComponentScope( this.springContext, beanName );
        return new SpringManagedComponentProvider(this.springContext, scope, beanName, clz);
    }
}

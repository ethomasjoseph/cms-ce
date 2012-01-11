package com.enonic.cms.web.common.spring;

import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;

import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.core.spi.component.ioc.IoCManagedComponentProvider;

final class SpringManagedComponentProvider
    implements IoCManagedComponentProvider
{
    private final ApplicationContext context;

    private final ComponentScope scope;

    private final String beanName;

    private final Class<?> clz;

    public SpringManagedComponentProvider( final ApplicationContext context, final ComponentScope scope, final String beanName,
                                           final Class clz )
    {
        this.context = context;
        this.scope = scope;
        this.beanName = beanName;
        this.clz = clz;
    }

    public ComponentScope getScope()
    {
        return this.scope;
    }

    public Object getInjectableInstance( final Object o )
    {
        if ( AopUtils.isAopProxy( o ) )
        {
            final Advised aopResource = (Advised) o;
            
            try
            {
                return aopResource.getTargetSource().getTarget();
            }
            catch ( Exception e )
            {
                throw new RuntimeException( "Could not get target object from proxy.", e );
            }
        }
        else
        {
            return o;
        }
    }

    public Object getInstance()
    {
        return this.context.getBean( this.beanName, this.clz );
    }
}

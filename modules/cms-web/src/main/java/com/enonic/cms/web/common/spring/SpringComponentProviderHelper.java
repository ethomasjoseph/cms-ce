package com.enonic.cms.web.common.spring;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import com.sun.jersey.core.spi.component.ComponentScope;

final class SpringComponentProviderHelper
{
    public static String getBeanName( final ApplicationContext springContext, final Class<?> clz )
    {
        final String[] names = BeanFactoryUtils.beanNamesForTypeIncludingAncestors( springContext, clz );
        if ( names.length == 0 )
        {
            return null;
        }
        else if ( names.length == 1 )
        {
            return names[0];
        }
        else
        {
            throw new RuntimeException( "There are multiple beans configured in spring for the type [" + clz.getName() + "]" );
        }
    }

    private static ComponentScope getComponentScope( final String scope )
    {
        if ( BeanDefinition.SCOPE_SINGLETON.equalsIgnoreCase( scope ) )
        {
            return ComponentScope.Singleton;
        }
        else if ( BeanDefinition.SCOPE_PROTOTYPE.equalsIgnoreCase( scope ) )
        {
            return ComponentScope.PerRequest;
        }
        else if ( "request".equalsIgnoreCase( scope ) )
        {
            return ComponentScope.PerRequest;
        }
        else
        {
            return ComponentScope.Undefined;
        }
    }

    public static ComponentScope getComponentScope( final ConfigurableApplicationContext context, final String beanName )
    {
        final String scope = context.getBeanFactory().getBeanDefinition( beanName ).getScope();
        return getComponentScope( scope );
    }
}

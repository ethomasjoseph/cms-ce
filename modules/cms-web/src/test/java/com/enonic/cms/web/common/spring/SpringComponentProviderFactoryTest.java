package com.enonic.cms.web.common.spring;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.support.GenericApplicationContext;

import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProvider;

public class SpringComponentProviderFactoryTest
{
    public static class TestResourceBean
    {
    }

    private DefaultResourceConfig rc;

    private SpringComponentProviderFactory factory;

    private GenericApplicationContext context;

    @Before
    public void setUp()
    {
        this.rc = new DefaultResourceConfig();
        this.context = new GenericApplicationContext();
        this.factory = new SpringComponentProviderFactory( this.rc, this.context );
    }

    private void registerBean( final String name, final Class<?> clz, final String scope )
    {
        final GenericBeanDefinition def = new GenericBeanDefinition();
        def.setBeanClass( clz );
        def.setScope( scope );

        this.context.registerBeanDefinition( name, def );
    }

    @Test
    public void testInjectableProvider()
    {
        final Set<Object> singletons = this.rc.getSingletons();
        assertEquals( 1, singletons.size() );

        final Object obj = singletons.iterator().next();
        assertNotNull( obj );
        assertEquals( ApplicationInjectableProvider.class, obj.getClass() );
    }

    @Test
    public void testGetComponentProviderNoBean()
    {
        final IoCComponentProvider provider = this.factory.getComponentProvider( TestResourceBean.class );
        assertNull( provider );
    }

    @Test
    public void testGetComponentProviderOneBean()
    {
        registerBean( "bean", TestResourceBean.class, "singleton" );

        final IoCComponentProvider provider = this.factory.getComponentProvider( TestResourceBean.class );
        assertNotNull( provider );

        final Object instance = provider.getInstance();
        assertNotNull( instance );
        assertEquals( TestResourceBean.class, instance.getClass() );
    }

    @Test(expected = RuntimeException.class)
    public void testGetComponentProviderManyBeans()
    {
        registerBean( "bean1", TestResourceBean.class, "singleton" );
        registerBean( "bean2", TestResourceBean.class, "singleton" );

        this.factory.getComponentProvider( TestResourceBean.class );
    }

    @Test
    public void testGetInjectableInstanceNoProxy()
    {
        registerBean( "bean", TestResourceBean.class, "singleton" );

        final IoCComponentProvider provider = this.factory.getComponentProvider( TestResourceBean.class );
        final SpringManagedComponentProvider managedProvider = getManagedProvider( provider );

        final Object input = new Object();
        final Object result = managedProvider.getInjectableInstance( input );
        assertSame( result, input );
    }

    @Test
    public void testGetInjectableInstanceSpringProxy()
    {
        registerBean( "bean", TestResourceBean.class, "singleton" );

        final IoCComponentProvider provider = this.factory.getComponentProvider( TestResourceBean.class );
        final SpringManagedComponentProvider managedProvider = getManagedProvider( provider );

        final Object target = new Object();
        final ProxyFactoryBean proxyFactory = new ProxyFactoryBean();
        proxyFactory.setTarget( target );

        final Object input = proxyFactory.getObject();
        final Object result = managedProvider.getInjectableInstance( input );
        assertSame( result, target );
    }

    @Test
    public void testSingletonScope()
    {
        assertScope( "singleton", ComponentScope.Singleton );
    }

    @Test
    public void testRequestScope()
    {
        assertScope( "request", ComponentScope.PerRequest );
    }

    @Test
    public void testPrototypeScope()
    {
        assertScope( "prototype", ComponentScope.PerRequest );
    }

    @Test
    public void testCustomScope()
    {
        assertScope( "custom", ComponentScope.Undefined );
    }

    public void assertScope( final String scope, final ComponentScope componentScope )
    {
        registerBean( "bean", TestResourceBean.class, scope );

        final IoCComponentProvider provider = this.factory.getComponentProvider( TestResourceBean.class );
        assertNotNull( provider );

        final SpringManagedComponentProvider managedProvider = getManagedProvider( provider );
        assertSame( componentScope, managedProvider.getScope() );
    }

    private SpringManagedComponentProvider getManagedProvider( final IoCComponentProvider provider )
    {
        assertTrue( provider instanceof SpringManagedComponentProvider );
        return (SpringManagedComponentProvider) provider;
    }
}

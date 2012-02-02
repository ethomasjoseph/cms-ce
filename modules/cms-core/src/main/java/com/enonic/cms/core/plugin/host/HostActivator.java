package com.enonic.cms.core.plugin.host;

import org.osgi.framework.BundleContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.enonic.cms.api.client.LocalClient;
import com.enonic.cms.core.plugin.container.OsgiContributor;

@Component
public final class HostActivator
    extends OsgiContributor
{
    private HostServices services;

    public HostActivator()
    {
        super( 0 );
        this.services = new HostServices();
        this.services.register( new PluginEnvironmentImpl() );
    }

    public void start( final BundleContext context )
        throws Exception
    {
        for ( final HostService<?> service : this.services )
        {
            service.register( context );
        }
    }

    @Autowired
    @Qualifier("remoteClient")
    public void setClient(final LocalClient client)
    {
        this.services.register( client );
    }

    public void stop( final BundleContext context )
        throws Exception
    {
        // Do nothing
    }
}

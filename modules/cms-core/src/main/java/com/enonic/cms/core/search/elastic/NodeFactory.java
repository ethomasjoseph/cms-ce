package com.enonic.cms.core.search.elastic;

import java.io.File;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.springframework.beans.factory.FactoryBean;

public final class NodeFactory
    implements FactoryBean<Node>
{
    private Node node;
    private File dataDir;

    @Override
    public Node getObject()
    {
        return this.node;
    }

    @Override
    public Class<?> getObjectType()
    {
        return Node.class;
    }

    @Override
    public boolean isSingleton()
    {
        return true;
    }

    public void setDataDir(final File dataDir)
    {
        this.dataDir = dataDir;
    }

    @PostConstruct
    public void start()
    {
        final Settings settings = ImmutableSettings.settingsBuilder()
                .put("path.logs", new File(this.dataDir, "log").getAbsolutePath())
                .put( "path.data", new File( this.dataDir, "data" ).getAbsolutePath() )
                .build();

        this.node = NodeBuilder.nodeBuilder()
                .client( false )
                .local( true )
                .data( true )
                .settings( settings )
                .build();

        this.node.start();
    }

    @PreDestroy
    public void stop()
    {
        this.node.close();
    }
}

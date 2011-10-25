package com.enonic.cms.core.search.elastic;

import javax.annotation.PostConstruct;

import org.elasticsearch.client.Client;
import org.elasticsearch.node.Node;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

public final class ClientFactory
    implements FactoryBean<Client>
{
    private Node node;
    private Client client;

    @Autowired
    public void setNode(final Node node)
    {
        this.node = node;
    }

    @Override
    public Client getObject()
    {
        return this.client;
    }

    @Override
    public Class<?> getObjectType()
    {
        return Client.class;
    }

    @Override
    public boolean isSingleton()
    {
        return true;
    }

    @PostConstruct
    public void init()
        throws Exception
    {
        this.client = this.node.client();
    }
}

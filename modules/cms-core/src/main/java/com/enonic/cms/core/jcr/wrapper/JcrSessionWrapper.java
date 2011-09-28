/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.jcr.wrapper;

import javax.jcr.ItemNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

class JcrSessionWrapper
    implements JcrSession
{
    private final Session session;

    JcrSessionWrapper( Session session )
    {
        this.session = session;
    }

    @Override
    public JcrNode getRootNode()
    {
        try
        {
            return JcrWrappers.wrap( session.getRootNode() );
        }
        catch ( RepositoryException e )
        {
            throw JcrException.wrap( e );
        }
    }

    @Override
    public void save()
    {
        try
        {
            session.save();
        }
        catch ( RepositoryException e )
        {
            throw JcrException.wrap( e );
        }
    }

    @Override
    public JcrQuery createQuery( String statement )
    {
        return new JcrQueryWrapper( this, statement );
    }

    @Override
    public JcrNode getNodeByIdentifier( String id )
    {
        try
        {
            return JcrWrappers.wrap( session.getNodeByIdentifier( id ) );
        }
        catch ( ItemNotFoundException nfe )
        {
            return null;
        }
        catch ( RepositoryException e )
        {
            throw JcrException.wrap( e );
        }
    }

    Session getWrappedSession()
    {
        return this.session;
    }
}

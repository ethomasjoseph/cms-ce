/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.jcr.wrapper;

import java.util.Set;

import org.joda.time.DateTime;

public interface JcrNode
{
    public String getName();

    public String getNodeType();

    public String getPath();

    public JcrNode getParent();

    public String getIdentifier();

    public JcrNode getNode( String relPath );

    public boolean hasNode( String relPath );

    public JcrNode addNode(String relPath, String primaryNodeTypeName);

    public void remove();

    public void remove( String relPath );

    public boolean hasProperties();

    public Set<String> getPropertyNames();

    public Object getProperty( String name );

    public boolean isMultiValuedProperty( String name );

    public int getPropertyType( String name );

    public String getStringProperty( String name );

    public Boolean getBooleanProperty( String name );

    public Long getLongProperty( String name );

    public DateTime getDateTimeProperty( String name );

    public JcrBinary getBinaryProperty( String name );

    public void setProperty( String name, Object value );

    public void setPropertyReference( String name, JcrNode referencedNode, boolean weak );

    public void addPropertyReference( String name, JcrNode referencedNode, boolean weak );

    public JcrNodeIterator getChildren();

    public JcrNode getChild( String name );

    public boolean hasChildNodes();
}

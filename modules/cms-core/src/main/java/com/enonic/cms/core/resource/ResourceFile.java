/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.resource;

import java.io.InputStream;

import org.jdom.Document;

import com.enonic.cms.framework.xml.XMLDocument;

public interface ResourceFile
    extends ResourceBase
{
    String getMimeType();

    long getSize();

    XMLDocument getDataAsXml();

    String getDataAsString();

    byte[] getDataAsByteArray();

    InputStream getDataAsInputStream();

    void setData( XMLDocument data );

    void setData( Document data );

    void setData( String data );

    void setData( byte[] data );

    void setData( InputStream data );
}

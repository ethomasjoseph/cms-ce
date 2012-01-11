/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.handlers;

import com.enonic.cms.store.dao.BinaryDataDao;
import com.enonic.cms.store.dao.ContentBinaryDataDao;
import com.enonic.cms.store.dao.ContentDao;

import com.enonic.cms.framework.blob.BlobRecord;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentEntity;

import com.enonic.cms.core.content.binary.BinaryData;
import com.enonic.cms.core.content.binary.BinaryDataEntity;
import com.enonic.cms.core.content.binary.ContentBinaryDataEntity;
import org.springframework.beans.factory.annotation.Autowired;

public final class BinaryDataHandler
{
    @Autowired
    private BinaryDataDao binaryDataDao;

    @Autowired
    private ContentBinaryDataDao contentBinaryDataDao;

    @Autowired
    private ContentDao contentDao;

    public BinaryData getBinaryData( int binaryKey )
    {
        ContentBinaryDataEntity entity = contentBinaryDataDao.findByBinaryKey( binaryKey );
        if ( entity == null )
        {
            return null;
        }

        return getBinaryData( entity, -1 );
    }

    private BinaryData getBinaryData( ContentBinaryDataEntity contentBinaryData, long timestamp )
    {
        BinaryData binaryData = new BinaryData();
        binaryData.key = contentBinaryData.getBinaryData().getKey();
        binaryData.contentKey = contentBinaryData.getContentVersion().getContent().getKey().toInt();
        binaryData.setSafeFileName( contentBinaryData.getBinaryData().getName() );
        binaryData.timestamp = contentBinaryData.getBinaryData().getCreatedAt();
        binaryData.anonymousAccess = false;

        if ( binaryData.timestamp.getTime() > timestamp )
        {
            BlobRecord blob = this.binaryDataDao.getBlob( contentBinaryData.getBinaryData() );
            binaryData.data = blob.getAsBytes();
        }

        return binaryData;
    }

    public int getBinaryDataKey( int contentKey, String label )
    {
        ContentEntity content = contentDao.findByKey( new ContentKey( contentKey ) );
        if ( content != null )
        {
            BinaryDataEntity binaryData = content.getMainVersion().getBinaryData( label );
            if ( binaryData != null )
            {
                return binaryData.getKey();
            }
        }
        return -1;
    }
}



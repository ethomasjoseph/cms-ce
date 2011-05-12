/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.binary;

import javax.inject.Inject;

import com.enonic.cms.framework.blob.BlobRecord;
import com.enonic.cms.framework.time.TimeService;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentVersionKey;
import com.enonic.cms.core.content.binary.access.BinaryAccessResolver;
import com.enonic.cms.core.preview.PreviewContext;
import com.enonic.cms.core.preview.PreviewService;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.portal.livetrace.BlobFetchingTrace;
import com.enonic.cms.portal.livetrace.BlobFetchingTracer;
import com.enonic.cms.portal.livetrace.LivePortalTraceService;
import com.enonic.cms.store.dao.BinaryDataDao;
import com.enonic.cms.store.dao.UserDao;

public class BinaryServiceImpl
    implements BinaryService
{

    @Inject
    private BinaryDataDao binaryDataDao;

    @Inject
    private UserDao userDao;

    @Inject
    private BinaryAccessResolver binaryAccessResolver;

    @Inject
    private TimeService timeService;

    @Inject
    private PreviewService previewService;

    @Inject
    private LivePortalTraceService livePortalTraceService;

    public BinaryDataEntity getBinaryDataForPortal( User user, AttachmentRequest attachmentRequest )
    {
        BinaryDataEntity binaryData = binaryDataDao.findByKey( attachmentRequest.getBinaryDataKey() );
        checkAccessibilityForPortal( attachmentRequest, binaryData, userDao.findByKey( user.getKey() ) );
        return binaryData;
    }

    public BinaryDataEntity getBinaryDataForAdmin( User user, BinaryDataKey binaryDataKey )
    {
        BinaryDataEntity binaryData = binaryDataDao.findByKey( binaryDataKey );
        checkAccessibilityForAdmin( binaryDataKey, binaryData, userDao.findByKey( user.getKey() ) );
        return binaryData;
    }

    public BlobRecord fetchBinary( BinaryDataKey binaryDataKey )
    {
        final BlobFetchingTrace blobFetchingTrace = BlobFetchingTracer.startTracing( livePortalTraceService );

        try
        {
            final BlobRecord blob = binaryDataDao.getBlob( binaryDataKey );

            if ( blob == null )
            {
                throw AttachmentNotFoundException.notFound( binaryDataKey );
            }

            BlobFetchingTracer.traceBlob( blobFetchingTrace, blob );

            return blob;
        }
        finally
        {
            BlobFetchingTracer.stopTracing( blobFetchingTrace, livePortalTraceService );
        }
    }

    private void checkAccessibilityForAdmin( BinaryDataKey binaryDataKey, BinaryDataEntity binaryData, UserEntity user )
    {

        if ( binaryData == null )
        {
            throw AttachmentNotFoundException.notFound( binaryDataKey );
        }

        if ( !binaryAccessResolver.hasReadAccess( binaryData, user ) )
        {
            throw AttachmentNotFoundException.notFound( binaryDataKey );
        }
    }

    private void checkAccessibilityForPortal( AttachmentRequest attachmentRequest, BinaryDataEntity binaryData, UserEntity user )
    {
        if ( binaryData == null )
        {
            throw AttachmentNotFoundException.notFound( attachmentRequest.getBinaryDataKey() );
        }

        if ( !binaryAccessResolver.hasReadAccess( binaryData, user ) )
        {
            throw AttachmentNotFoundException.noAccess( attachmentRequest.getBinaryDataKey() );
        }

        if ( previewService.isInPreview() )
        {
            PreviewContext previewContext = previewService.getPreviewContext();
            if ( previewContext.isPreviewingContent() &&
                previewContext.getContentPreviewContext().treatContentAsAvailableEvenIfOffline( attachmentRequest.getContentKey() ) )
            {
                // in preview, content related to the previewed content
                return;
            }
        }

        if ( !binaryAccessResolver.hasReadAndIsAccessibleOnline( binaryData, user, timeService.getNowAsDateTime() ) )
        {
            throw AttachmentNotFoundException.notFound( attachmentRequest.getBinaryDataKey() );
        }
    }

    public BinaryDataKey resolveBinaryDataKey( ContentKey contentKey, String label, ContentVersionKey contentVersionKey )
    {
        BinaryDataEntity binaryData;

        if ( contentVersionKey != null )
        {
            binaryData = binaryDataDao.findByContentVersionKey( contentVersionKey, label );
        }
        else
        {
            binaryData = binaryDataDao.findByContentKey( contentKey, label );
        }

        return binaryData != null ? binaryData.getBinaryDataKey() : null;
    }
}
package com.enonic.cms.web.portal.handler;

import javax.ws.rs.core.Response;
import com.enonic.cms.store.dao.BinaryDataDao;

final class AttachmentWebHandler
    extends AbstractWebHandler
{
    private BinaryDataDao binaryDataDao;

    @Override
    public Response handle()
    {
        return Response.ok( "attachment" ).build();
    }

    public void setBinaryDataDao( final BinaryDataDao binaryDataDao )
    {
        this.binaryDataDao = binaryDataDao;
    }
}

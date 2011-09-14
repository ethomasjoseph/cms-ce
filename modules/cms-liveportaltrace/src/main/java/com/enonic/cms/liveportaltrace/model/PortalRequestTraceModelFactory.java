package com.enonic.cms.liveportaltrace.model;

import java.util.List;

import com.enonic.cms.portal.livetrace.PastPortalRequestTrace;
import com.enonic.cms.portal.livetrace.PortalRequestTrace;

public class PortalRequestTraceModelFactory
{
    public static void paintModel( PortalRequestTraceModel model, PastPortalRequestTrace pastPortalRequestTrace )
    {
        doPaintModel( model, pastPortalRequestTrace );
    }

    public static PortalRequestTraceModel createModel( final PastPortalRequestTrace pastPortalRequestTrace )
    {
        final PortalRequestTraceModel model = new PortalRequestTraceModel();
        doPaintModel( model, pastPortalRequestTrace );
        return model;
    }

    public static PortalRequestTraceListModel createListModel( final List<PastPortalRequestTrace> list )
    {
        PortalRequestTraceListModel model = new PortalRequestTraceListModel();
        for ( PastPortalRequestTrace trace : list )
        {
            model.addRequest( createModel( trace ) );
        }
        model.setTotal( list.size() );
        return model;
    }

    public static PortalRequestTraceListModel createEmptyListModel( int total )
    {
        PortalRequestTraceListModel model = new PortalRequestTraceListModel();
        model.setTotal( total );
        return model;
    }

    private static void doPaintModel( PortalRequestTraceModel model, PastPortalRequestTrace pastPortalRequestTrace )
    {
        final PortalRequestTrace portalRequestTrace = pastPortalRequestTrace.getPortalRequestTrace();

        model.setId( pastPortalRequestTrace.getHistoryRecordNumber() );
        model.setSite( SiteModel.create( portalRequestTrace ) );
        model.setUrl( URLModel.create( portalRequestTrace ) );
        model.setRemoteAddress( portalRequestTrace.getHttpRequestRemoteAddress() );
        model.setUsername( portalRequestTrace.getRequester().toString() );
        model.setRequestType( toPortalRequestType( portalRequestTrace.getType() ) );
        model.setDuration( DurationModel.create( portalRequestTrace.getDuration() ) );
    }

    private static PortalRequestTraceType toPortalRequestType( String type )
    {
        if ( type.equals( "P" ) )
        {
            return PortalRequestTraceType.Page;
        }
        else if ( type.equals( "W" ) )
        {
            return PortalRequestTraceType.Window;
        }
        else if ( type.equals( "A" ) )
        {
            return PortalRequestTraceType.Attachment;
        }
        else if ( type.equals( "I" ) )
        {
            return PortalRequestTraceType.Image;
        }
        else
        {
            return PortalRequestTraceType.Unknown;
        }
    }


}
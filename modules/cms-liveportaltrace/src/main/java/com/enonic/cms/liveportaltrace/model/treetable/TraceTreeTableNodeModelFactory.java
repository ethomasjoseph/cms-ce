package com.enonic.cms.liveportaltrace.model.treetable;



import java.util.List;

import com.enonic.cms.liveportaltrace.model.DurationModel;
import com.enonic.cms.portal.livetrace.AttachmentRequestTrace;
import com.enonic.cms.portal.livetrace.ImageRequestTrace;
import com.enonic.cms.portal.livetrace.PageRenderingTrace;
import com.enonic.cms.portal.livetrace.PastPortalRequestTrace;
import com.enonic.cms.portal.livetrace.PortalRequestTrace;
import com.enonic.cms.portal.livetrace.WindowRenderingTrace;

public class TraceTreeTableNodeModelFactory
{
    public static TraceTreeTableNodeModel createEmpty()
    {
        TraceTreeTableNodeModel rootNode = new TraceTreeTableNodeModel();
        rootNode.setId( "" );
        rootNode.setText( "None" );
        return rootNode;
    }

    public static TraceTreeTableNodeModel create( PastPortalRequestTrace pastPortalRequestTrace )
    {
        PortalRequestTrace portalRequestTrace = pastPortalRequestTrace.getPortalRequestTrace();

        TraceTreeTableNodeModel rootNode = new TraceTreeTableNodeModel();
        rootNode.setId( "" + pastPortalRequestTrace.getHistoryRecordNumber() );
        rootNode.setText( "." );
        rootNode.setExpanded( true );

        TraceTreeTableNodeModel portalTraceNode = new TraceTreeTableNodeModel();
        portalTraceNode.setDuration( DurationModel.create( portalRequestTrace.getDuration() ) );
        //portalTraceNode.setText( "Portal request: " + portalRequestTrace.getSiteLocalUrl() );
        portalTraceNode.setText( "Portal request" );
        portalTraceNode.setExpanded( true );
        rootNode.addChild( portalTraceNode );

        if( portalRequestTrace.hasImageRequestTrace() )
        {
            TraceTreeTableNodeModel imageTraceNode = doCreateImageTrace( portalRequestTrace.getImageRequestTrace() );
            portalTraceNode.addChild( imageTraceNode );
        }
        else if( portalRequestTrace.hasAttachmentRequsetTrace() )
        {
            TraceTreeTableNodeModel imageTraceNode = doCreateAttachmentTrace( portalRequestTrace.getAttachmentRequestTrace() );
            portalTraceNode.addChild( imageTraceNode );
        }
        else if( portalRequestTrace.hasPageRenderingTrace() )
        {
            TraceTreeTableNodeModel imageTraceNode = doCreatePageRenderingTrace( portalRequestTrace.getPageRenderingTrace() );
            portalTraceNode.addChild( imageTraceNode );
        }
        else if( portalRequestTrace.hasWindowRenderingTrace() )
        {
            TraceTreeTableNodeModel imageTraceNode = doCreateWindowRenderingTrace( portalRequestTrace.getWindowRenderingTrace() );
            portalTraceNode.addChild( imageTraceNode );
        }

        return rootNode;
    }

    private static TraceTreeTableNodeModel doCreateImageTrace( ImageRequestTrace trace )
    {
        TraceTreeTableNodeModel imageRequestNode = new TraceTreeTableNodeModel();
        imageRequestNode.setText( "Image request" );
        imageRequestNode.setDuration( DurationModel.create( trace.getDuration() ) );
        imageRequestNode.setLeaf( true );
        imageRequestNode.setUsedCachedResult( String.valueOf( trace.getUsedCachedResult() ) );
        return imageRequestNode;
    }

    private static TraceTreeTableNodeModel doCreateAttachmentTrace( AttachmentRequestTrace trace )
    {
        TraceTreeTableNodeModel attachmentRequestNode = new TraceTreeTableNodeModel();
        attachmentRequestNode.setText( "Attachment request" );
        attachmentRequestNode.setDuration( DurationModel.create( trace.getDuration() ) );
        attachmentRequestNode.setLeaf( true );
        return attachmentRequestNode;
    }

    private static TraceTreeTableNodeModel doCreatePageRenderingTrace( PageRenderingTrace trace )
    {
        TraceTreeTableNodeModel pageRenderingNode = new TraceTreeTableNodeModel();
        pageRenderingNode.setText( "Page rendering: " );
        pageRenderingNode.setDuration( DurationModel.create( trace.getDuration() ) );
        pageRenderingNode.setExpanded( true );
        pageRenderingNode.setUsedCachedResult( String.valueOf( trace.isUsedCachedResult() ) );

        List<WindowRenderingTrace> windowRenderingTraceList = trace.getWindowRenderingTraces();
        for( WindowRenderingTrace windowRenderingTrace : windowRenderingTraceList )
        {
            pageRenderingNode.addChild( doCreateWindowRenderingTrace( windowRenderingTrace ) );
        }

        return pageRenderingNode;
    }

    private static TraceTreeTableNodeModel doCreateWindowRenderingTrace( WindowRenderingTrace trace )
    {
        TraceTreeTableNodeModel windowRenderingNode = new TraceTreeTableNodeModel();
        windowRenderingNode.setText( "Window rendering: " + trace.getPortletName() );
        windowRenderingNode.setDuration( DurationModel.create( trace.getDuration() ) );
        windowRenderingNode.setLeaf( true );
        windowRenderingNode.setUsedCachedResult( String.valueOf( trace.isUsedCachedResult() ) );
        return windowRenderingNode;
    }
}

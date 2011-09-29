package com.enonic.cms.liveportaltrace.model.treetable;


import java.util.List;

import com.enonic.cms.liveportaltrace.model.DurationModel;
import com.enonic.cms.portal.livetrace.AttachmentRequestTrace;
import com.enonic.cms.portal.livetrace.DatasourceExecutionTrace;
import com.enonic.cms.portal.livetrace.Duration;
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

    public static TraceTreeTableNodeModel create( final PastPortalRequestTrace pastPortalRequestTrace )
    {
        final PortalRequestTrace portalRequestTrace = pastPortalRequestTrace.getPortalRequestTrace();

        final TraceTreeTableNodeModel rootNode = new TraceTreeTableNodeModel();
        rootNode.setId( "" + pastPortalRequestTrace.getHistoryRecordNumber() );
        rootNode.setText( "." );
        rootNode.setExpanded( true );

        final TraceTreeTableNodeModel portalTraceNode = new TraceTreeTableNodeModel();
        portalTraceNode.setId( "" + portalRequestTrace.hashCode() );
        portalTraceNode.setDuration( DurationModel.create( portalRequestTrace.getDuration() ) );
        //portalTraceNode.setText( "Portal request: " + portalRequestTrace.getSiteLocalUrl() );
        portalTraceNode.setText( "Portal request" );
        portalTraceNode.setExpanded( true );
        rootNode.addChild( portalTraceNode );

        if ( portalRequestTrace.hasImageRequestTrace() )
        {
            TraceTreeTableNodeModel imageTraceNode = doCreateImageTrace( portalRequestTrace.getImageRequestTrace() );
            portalTraceNode.addChild( imageTraceNode );
        }
        else if ( portalRequestTrace.hasAttachmentRequsetTrace() )
        {
            TraceTreeTableNodeModel imageTraceNode = doCreateAttachmentTrace( portalRequestTrace.getAttachmentRequestTrace() );
            portalTraceNode.addChild( imageTraceNode );
        }
        else if ( portalRequestTrace.hasPageRenderingTrace() )
        {
            TraceTreeTableNodeModel imageTraceNode = doCreatePageRenderingTrace( portalRequestTrace.getPageRenderingTrace() );
            portalTraceNode.addChild( imageTraceNode );
        }
        else if ( portalRequestTrace.hasWindowRenderingTrace() )
        {
            TraceTreeTableNodeModel imageTraceNode = doCreateWindowRenderingTrace( portalRequestTrace.getWindowRenderingTrace() );
            portalTraceNode.addChild( imageTraceNode );
        }

        return rootNode;
    }

    private static TraceTreeTableNodeModel doCreateImageTrace( final ImageRequestTrace trace )
    {
        final TraceTreeTableNodeModel node = new TraceTreeTableNodeModel();
        node.setId( "" + trace.hashCode() );
        node.setText( "Image request" );
        node.setDuration( DurationModel.create( trace.getDuration() ) );
        node.setLeaf( true );
        node.setUsedCachedResult( String.valueOf( trace.getUsedCachedResult() ) );
        return node;
    }

    private static TraceTreeTableNodeModel doCreateAttachmentTrace( final AttachmentRequestTrace trace )
    {
        final TraceTreeTableNodeModel node = new TraceTreeTableNodeModel();
        node.setId( "" + trace.hashCode() );
        node.setText( "Attachment request" );
        node.setDuration( DurationModel.create( trace.getDuration() ) );
        node.setLeaf( true );
        return node;
    }

    private static TraceTreeTableNodeModel doCreatePageRenderingTrace( final PageRenderingTrace trace )
    {
        final List<DatasourceExecutionTrace> datasourceExecutionTraces = trace.getDatasourceExecutionTraces();
        final List<WindowRenderingTrace> windowRenderingTraceList = trace.getWindowRenderingTraces();

        final TraceTreeTableNodeModel node = new TraceTreeTableNodeModel();
        node.setId( "" + trace.hashCode() );
        node.setText( "Page rendering: " );
        node.setDuration( DurationModel.create( trace.getDuration() ) );
        node.setExpanded( true );
        node.setUsedCachedResult( String.valueOf( trace.isUsedCachedResult() ) );
        node.setExecutor( trace.getRenderer().toString() );
        node.setLeaf( false );

        node.addChild( doCreateDatasourceExecutionsNode( datasourceExecutionTraces,
                                                         DatasourceExecutionTrace.resolveDurationOfDatasourceExecutions(
                                                             datasourceExecutionTraces ) ) );

        for ( WindowRenderingTrace windowRenderingTrace : windowRenderingTraceList )
        {
            node.addChild( doCreateWindowRenderingTrace( windowRenderingTrace ) );
        }

        return node;
    }

    private static TraceTreeTableNodeModel doCreateWindowRenderingTrace( final WindowRenderingTrace trace )
    {
        final List<DatasourceExecutionTrace> datasourceExecutionTraces = trace.getDatasourceExecutionTraces();

        final TraceTreeTableNodeModel node = new TraceTreeTableNodeModel();
        node.setId( "" + trace.hashCode() );
        node.setText( "Window rendering: " + trace.getPortletName() );
        node.setDuration( DurationModel.create( trace.getDuration() ) );
        node.setUsedCachedResult( String.valueOf( trace.isUsedCachedResult() ) );
        node.setExecutor( trace.getRenderer().toString() );
        node.setLeaf( false );
        node.addChild( doCreateDatasourceExecutionsNode( datasourceExecutionTraces,
                                                         DatasourceExecutionTrace.resolveDurationOfDatasourceExecutions(
                                                             datasourceExecutionTraces ) ) );
        return node;
    }

    private static TraceTreeTableNodeModel doCreateDatasourceExecutionsNode( final List<DatasourceExecutionTrace> datasourceExecutionTraces,
                                                                             final Duration duration )
    {
        final TraceTreeTableNodeModel node = new TraceTreeTableNodeModel();
        node.setId( "" + datasourceExecutionTraces.hashCode() );
        node.setText( "Datasource executions: " + datasourceExecutionTraces.size() );
        if ( duration != null )
        {
            node.setDuration( DurationModel.create( duration ) );
        }
        node.setLeaf( datasourceExecutionTraces.isEmpty() );
        for ( DatasourceExecutionTrace datasourceExecutionTrace : datasourceExecutionTraces )
        {
            node.addChild( doCreateDatasourceExecutionTrace( datasourceExecutionTrace ) );
        }
        return node;
    }

    private static TraceTreeTableNodeModel doCreateDatasourceExecutionTrace( final DatasourceExecutionTrace trace )
    {
        final TraceTreeTableNodeModel node = new TraceTreeTableNodeModel();
        node.setId( "" + trace.hashCode() );
        node.setText( "Datasource execution: " + trace.getMethodName() );
        node.setDuration( DurationModel.create( trace.getDuration() ) );
        node.setLeaf( true );
        node.setUsedCachedResult( String.valueOf( trace.isCacheUsed() ) );
        return node;
    }
}

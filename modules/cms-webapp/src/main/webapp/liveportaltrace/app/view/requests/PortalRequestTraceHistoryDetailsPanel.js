Ext.define( 'LPT.view.requests.PortalRequestTraceHistoryDetailsPanel', {
    extend: 'Ext.tree.Panel',
    alias : 'widget.portalRequestTraceHistoryDetailsPanel',

    title: 'Details',

    store: 'PortalRequestTraceHistoryDetailsStore',

    rootVisible: false,
    collapsible: false,
    useArrows: true,
    multiSelect: false,
    singleExpand: false,
    lines: true,
    
    columns : [
        {
            xtype: 'treecolumn',
            text: 'Trace',
            flex: 4,
            sortable: false,
            dataIndex: 'text'
        },
        {
            text: 'Used cached result',
            dataIndex: 'usedCachedResult',
            sortable: false,
            flex: 1,
            align: 'right',
            margin: '0 0 0 3'
        },
        {
            text: 'Duration',
            dataIndex: 'duration.humanReadable',
            sortable: false,
            flex: 1,
            align: 'right',
            margin: '0 0 0 3'
        }
    ]

} );

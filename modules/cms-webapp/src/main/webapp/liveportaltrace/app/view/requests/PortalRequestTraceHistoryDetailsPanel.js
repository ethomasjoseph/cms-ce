Ext.define( 'LPT.view.requests.PortalRequestTraceHistoryDetailsPanel', {
    extend: 'Ext.tree.Panel',
    alias : 'widget.portalRequestTraceHistoryDetailsPanel',

    title: 'Details',

    store: 'PortalRequestTraceHistoryDetailsStore',

    //collapsible: false,
    //useArrows: true,
    rootVisible: false,
    //multiSelect: false,
    //singleExpand: false,
    //lines: true,

    displayField: 'text',

    initComponent: function()
    {

        this.columns = [
            {
                xtype: 'treecolumn',
                text: 'Trace',
                flex: 2,
                sortable: false,
                dataIndex: 'text'
            },
            {
                text: 'Used cached result',
                dataIndex: 'usedCachedResult',
                sortable: false,
                align: 'right'
            },
            {
                text: 'Duration',
                dataIndex: 'duration.humanReadable',
                sortable: false,
                align: 'right'
            }
        ];

        this.callParent( arguments );
    }

} );

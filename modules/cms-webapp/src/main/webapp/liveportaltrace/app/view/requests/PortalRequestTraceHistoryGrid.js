Ext.define( 'LPT.view.requests.PortalRequestTraceHistoryGrid', {
    extend: 'Ext.grid.Panel',
    alias : 'widget.portalRequestTraceHistoryGrid',

    layout: 'fit',
    loadMask: true,
    columnLines: true,
    frame: false,
    store: 'PortalRequestTraceHistoryListStore',

    selType: 'rowmodel',

    initComponent: function()
    {
        this.columns = [
            {
                text: '#',
                dataIndex: 'id',
                sortable: true,
                width: 75,
                align: 'right'
            },
            {
                text: 'Type',
                dataIndex: 'requestType',
                sortable: true
            },
            {
                text: 'Site',
                dataIndex: 'site.name',
                sortable: true,
                width: 150
            },
            {
                text: 'URL',
                dataIndex: 'url.siteLocalUrl',
                sortable: true,
                flex: 1
            },
            {
                text: 'User',
                dataIndex: 'username',
                sortable: true
            },
            {
                text: 'Started',
                dataIndex: 'duration.startTime',
                sortable: true,
                xtype: 'datecolumn',
                format: 'Y-m-d H:i:s',
                width: 120
            },
            {
                text: 'Duration',
                dataIndex: 'duration.humanReadable',
                sortable: false,
                align: 'right'
            },
            {
                text: 'Client IP',
                dataIndex: 'remoteAddress',
                sortable: true
            }
        ];

        this.bbar = {
            xtype: 'pagingtoolbar',
            store: this.store,
            displayInfo: true,
            displayMsg: 'Displaying portal requests {0} - {1} of {2}',
            emptyMsg: 'No portal requests to display'
        };

        this.viewConfig = {
            trackOver : true,
            stripeRows: true,
            loadingText: ''
        };

        this.callParent( arguments );
    }
} );

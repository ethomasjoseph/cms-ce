Ext.define( 'App.view.ContentGridPanel', {
    extend: 'Ext.grid.Panel',
    alias : 'widget.contentGridPanel',

    title: 'Content',
    requires: ['App.view.ContentToolbar'],
    layout: 'fit',
    multiSelect: true,
    loadMask: true,
    columnLines: true,
    frame: false,
    border: false,
    store: 'ContentStore',

    initComponent: function()
    {
        this.tbar = {
            xtype: 'contentToolbar'
        };

        this.columns = [
            {
                text: 'Key',
                dataIndex: 'key',
                sortable: true,
                align: 'right',
                flex: 1
            },
            {
                text: 'Name',
                dataIndex: 'name',
                sortable: true,
                flex: 10
            },
            {
                text: 'Modified',
                xtype: 'datecolumn',
                dataIndex: 'timestamp',
                format: 'Y-m-d h:m',
                sortable: true
            }
        ];

        this.viewConfig = {
            trackOver : true,
            stripeRows: true,
            plugins: {
                ptype: 'gridviewdragdrop',
                dragGroup: 'global_activityStreamDragGroup'
            }
        };

        this.callParent( arguments );
    }
} );
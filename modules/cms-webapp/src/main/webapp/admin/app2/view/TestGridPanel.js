Ext.define( 'App.view.TestGridPanel', {
    extend: 'Ext.grid.Panel',
    alias : 'widget.testGridPanel',

    title: 'Tests',
    layout: 'fit',
    loadMask: true,
    columnLines: true,
    frame: false,
    border: false,
    store: 'TestStore',

    initComponent: function()
    {
        this.columns = [
            {
                text: 'Name',
                dataIndex: 'name',
                sortable: true,
                flex: 10
            },
            {
                text: 'Content type no',
                dataIndex: 'key',
                sortable: true,
                align: 'center'
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
            stripeRows: true
        };

        this.callParent( arguments );
    }
} );
Ext.define( 'App.view.GridPanel_2', {
    extend: 'Ext.grid.Panel',
    alias : 'widget.gridPanel2',

    title: 'Module 2',
    requires: ['App.view.Toolbar_3'],
    layout: 'fit',
    loadMask: true,
    columnLines: true,
    frame: false,
    border: false,
    store: 'Module_2_Store',

    initComponent: function()
    {
        this.tbar = {
            xtype: 'toolbar3'
        };

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
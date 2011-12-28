Ext.define( 'App.view.AccountGridPanel', {
    extend: 'Ext.grid.Panel',
    alias : 'widget.accountGridPanel',

    title: 'Accounts',
    layout: 'fit',
    loadMask: true,
    columnLines: true,
    frame: false,
    border: false,
    store: 'AccountStore',

    initComponent: function()
    {
        this.tbar = [
            {
                text:'New',
                handler:function ()
                {
                    alert( 'New' );
                }
            },
            {
                text:'Edit',
                handler:function ()
                {
                    alert( 'Edit' );
                }
            },
            {
                text:'Delete',
                handler:function ()
                {
                    alert( 'Delete' );
                }
            },
        ];


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
            stripeRows: true,
            plugins: {
                ptype: 'gridviewdragdrop',
                dragGroup: 'global_activityStreamDragGroup'
            }
        };

        this.callParent( arguments );
    }
} );
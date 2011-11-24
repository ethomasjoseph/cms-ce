Ext.define( 'App.view.ShowPanel', {
    extend: 'Ext.panel.Panel',
    alias : 'widget.accountShow',

    requires: [
        'App.view.BrowseToolbar',
        'App.view.GridPanel',
        'App.view.DetailPanel'
    ],

    layout: 'border',
    border: false,
    padding: 5,

    initComponent: function()
    {
        this.items = [
            {
                region: 'north',
                xtype: 'browseToolbar'
            },
            {
                region: 'center',
                xtype: 'accountGrid',
                flex: 2
            },
            {
                region: 'south',
                xtype: 'accountDetail',
                flex: 1
            }
        ];

        this.callParent( arguments );
    }

} );

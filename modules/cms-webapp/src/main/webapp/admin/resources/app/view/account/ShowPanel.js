Ext.define( 'Cms.view.account.ShowPanel', {
    extend: 'Ext.panel.Panel',
    alias : 'widget.accountShow',

    requires: [
        'Cms.view.account.BrowseToolbar',
        'Cms.view.account.GridPanel',
        'Cms.view.account.DetailPanel'
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

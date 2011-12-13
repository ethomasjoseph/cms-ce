Ext.define( 'App.view.MainPanel_3', {
    extend: 'Ext.panel.Panel',
    alias : 'widget.mainPanel3',

    requires: [
        'App.view.GridPanel_3',
    ],

    layout: 'border',
    border: false,

    initComponent: function()
    {
        this.items = [
            {
                region: 'west',
                xtype: 'panel',
                padding: '5 0 5 5',
                title: 'Module 3 - Side Panel',
                collapsible: true,
                split: true,
                flex: 1
            },
            {
                region: 'center',
                xtype: 'panel',
                padding: '5 5 5 0',
                flex: 6,
                items: [
                    {
                        xtype: 'gridPanel3'
                    }
                ]
            }
        ];

        this.callParent( arguments );
    }

} );

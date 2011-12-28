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
                border: true,
                title: 'Module 3 - Side Panel',
                collapsible: true,
                split: true,
                flex: 2
            },
            {
                region: 'center',
                xtype: 'panel',
                border: true,
                flex: 7,
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

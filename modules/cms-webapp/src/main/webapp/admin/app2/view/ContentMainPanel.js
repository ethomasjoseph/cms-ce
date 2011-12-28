Ext.define( 'App.view.ContentMainPanel', {
    extend: 'Ext.panel.Panel',
    alias : 'widget.contentMainPanel',

    requires: [
        'App.view.ContentGridPanel',
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
                title: 'Content - Side Panel',
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
                        xtype: 'contentGridPanel'
                    }
                ]
            }
        ];

        this.callParent( arguments );
    }

} );

Ext.define( 'App.view.TestMainPanel', {
    extend: 'Ext.panel.Panel',
    alias : 'widget.testMainPanel',
    title: 'Tests',
    border: false,
    bodyPadding: 10,
    layout: 'vbox',
    defaults: {
        margin: '0 0 5 0'
    },
    items: [
        {
            xtype: 'button',
            text: 'Create New Window',
            action: 'createNewWindow',

        },
        {
            xtype: 'button',
            text: 'Go to Content',
            action: 'loadContent'
        }
    ],

    initComponent: function()
    {
        this.callParent( arguments );
    }

} );

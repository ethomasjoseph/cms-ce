Ext.define( 'App.view.TestMainPanel', {
    extend: 'Ext.panel.Panel',
    alias : 'widget.testMainPanel',
    title: 'Tests',
    border: false,
    bodyPadding: 10,
    items: [
        {
            xtype: 'button',
            text: 'Create New Window',
            action: 'createNewWindow',
            margin: '0 5 0 0'
        },
        {
            xtype: 'button',
            text: 'Content',
            action: 'loadContent'
        }
    ],

    initComponent: function()
    {
        this.callParent( arguments );
    }

} );

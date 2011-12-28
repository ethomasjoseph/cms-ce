Ext.define( 'App.view.Panel_1', {
    extend: 'Ext.panel.Panel',
    alias : 'widget.panel1',
    title: 'Module 1',
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
            text: 'Module 3',
            action: 'loadModule3'
        }
    ],

    initComponent: function()
    {
        this.callParent( arguments );
    }

} );

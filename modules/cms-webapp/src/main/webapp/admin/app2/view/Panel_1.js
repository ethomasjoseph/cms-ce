Ext.define( 'App.view.Panel_1', {
    extend: 'Ext.panel.Panel',
    alias : 'widget.panel1',
    title: 'Module 1',
    bodyPadding: 10,
    items: [
        {
            xtype: 'button',
            text: 'Create New Window',
            action: 'createNewWindow'
        }
    ],

    initComponent: function()
    {
        this.callParent( arguments );
    }

} );

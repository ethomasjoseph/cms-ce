Ext.define( 'App.controller.Module_1_Controller', {
    extend: 'Ext.app.Controller',

    stores: ['Module_1_Store'],
    models: ['Module_1_Model'],
    views: ['Panel_1'],

    windowStartX: 150,
    windowStartY: 50
    ,

    init: function()
    {
        this.control(
            {
                '*[action=createNewWindow]': {
                    click: this.createNewWindow
                }
            }
        );
    },

    createNewWindow: function(button, event) {
        var self = this;
        var store = self.getStore('Module_1_Store');
        var randomIndex = Math.floor(Math.random() * (store.getCount()-1));
        var title = store.getAt(randomIndex).data.name;
        var panel = Ext.ComponentQuery.query('.panel1')[0];
        console.log(panel);

        Ext.create('Ext.window.Window', {
            title: title,
            x: self.windowStartX,
            y: self.windowStartY,
            height: 200,
            width: 400,
            layout: 'fit',
            renderTo: panel.getEl(),
            items: {
                xtype: 'grid',
                border: false,
                columns: [{header: 'Field Name'}],
                store: Ext.create('Ext.data.ArrayStore', {})
            }
        }).show(button);

        self.windowStartX += 20;
        self.windowStartY += 20;
    }

} );
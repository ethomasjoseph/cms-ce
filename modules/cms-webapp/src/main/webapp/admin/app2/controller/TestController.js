Ext.define( 'App.controller.TestController', {
    extend: 'App.controller.AdminBaseController',

    stores: ['TestStore'],
    models: ['TestModel'],
    views: ['TestMainPanel'],

    windowStartX: 150,
    windowStartY: 50,

    init: function()
    {
        this.control(
            {
                '*[action=createNewWindow]': {
                    click: this.createNewWindow
                },
                '*[action=loadContent]': {
                    click: function() {
                        this.loadModule('ContentController');
                    }
                }
            }
        );
    },

    createNewWindow: function(button, event) {
        var self = this;
        var store = self.getStore('TestStore');
        var randomIndex = Math.floor(Math.random() * (store.getCount()-1));
        var title = store.getAt(randomIndex).data.name;
        var panel = Ext.ComponentQuery.query('.testMainPanel')[0];
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
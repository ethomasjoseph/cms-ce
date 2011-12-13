Ext.define( 'App.controller.LauncherController', {
    extend: 'Ext.app.Controller',

    init: function()
    {
        this.control(
            {
                '*[action=loadModule-1]': {
                    click: function(button, event) {
                        this.loadModule('Module_1_Controller', button, event);
                    }
                },
                '*[action=loadModule-2]': {
                    click: function(button, event) {
                        this.loadModule('Module_2_Controller', button, event);
                    }
                },
                '*[action=loadModule-3]': {
                    click: function(button, event) {
                        this.loadModule('Module_3_Controller', button, event);
                    }
                }
            }
        );

        this.loadMask = new Ext.LoadMask(Ext.getBody(), {msg:"Please wait..."});
    },

    loadModule: function(controllerName, element, event)
    {
        this.loadMask.show();

        var controller = this.getController(controllerName);
        var view = this.getView(controller.views[0]).create();
        var options = { single: true };
        var args = [];

        view.mon(view, 'render', function() {
            console.log('executing init on Controller "' + this.id + '", passing: ', args);

            controller.init.apply(controller, args);
            this.loadMask.hide();
        }, this, options);

        view.mon(view, 'deactivate', function(view) {
            console.log('removing controller "' + this.id + '" and destroying controller "' + view.id + '"');

            view.destroy();
            Ext.destroy(this.application.controllers.remove(this));
        }, this, options);

        this.getApplicationPanel().getLayout().setActiveItem(view);
    },

    getApplicationPanel: function()
    {
        return Ext.ComponentQuery.query('#application-panel')[0];
    }

} );

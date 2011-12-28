Ext.define( 'App.controller.AdminController', {
    extend: 'Ext.app.Controller',

    init: function()
    {
        /**/
    },

    viewCache: {},

    loadModule: function ( controllerName )
    {
        this.showLoadMask();

        var controller = this.application.controllers.get( controllerName );
        var view = null;

        if ( controller === undefined )
        {
            console.log( 'controller "' + controllerName + '" does not exist. Instantiate controller and render the main view' );

            controller = this.getController( controllerName );
            view = this.getView( controller.views[0] ).create();
            var options = { single:true };
            var args = [];

            this.viewCache[controllerName] = view;
            view.mon( view, 'render', function ()
            {
                controller.init.apply( controller, args );
            }, this, options );
        }
        else
        {
            console.log( 'controller "' + controllerName + '" exist use cache' );

            view = this.viewCache[controllerName];
        }

        this.getModulePanel().getLayout().setActiveItem( view );

        this.hideLoadMask();
    },

    showLoadMask: function()
    {
        if ( !this.loadMask )
        {
            this.loadMask = new Ext.LoadMask( Ext.getBody(), {msg:"Please wait..."} );
        }
        this.loadMask.show();
    },

    hideLoadMask:function ()
    {
        if ( this.loadMask )
        {
            this.loadMask.hide();
        }
    },

    getModulePanel: function()
    {
        return Ext.ComponentQuery.query('#module-panel')[0];
    },

    getActivityStreamPanel: function()
    {
        return Ext.ComponentQuery.query('#activity-stream')[0];
    }


} );

Ext.define( 'App.controller.NotifyUserController', {
    extend: 'Ext.app.Controller',

    stores: [],
    models: [],
    views: [ 'NotifyUserWindow'],

    init: function()
    {
        this.control( {
            'notifyUserWindow *[action=send]':{
                click: this.doSend
            },
            'notifyUserWindow *[action=close]': {
                click: this.closeWindow
            }
        } );
        this.application.on({
            'showNotifyUserWindow': this.showWindow,
            scope: this
        });
        this.application.on({
            'closeNotifyUserWindow': this.closeWindow,
            scope: this
        });
    },

    showWindow: function( model ) {
        this.getNotifyUserWindow().doShow( model );
    },

    closeWindow: function() {
        this.getNotifyUserWindow().close();
    },

    doSend: function( btn, evt ) {
        var form = btn.up('notifyUserWindow').down('form');
        form.submit();
        this.closeWindow();
    },

    getNotifyUserWindow: function()
    {
        var win = Ext.ComponentQuery.query( 'notifyUserWindow' )[0];
        if ( !win ) {
            win = Ext.createWidget( 'notifyUserWindow' );
        }
        return win;
    }

} );


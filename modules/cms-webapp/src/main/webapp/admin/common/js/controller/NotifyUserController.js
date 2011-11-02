Ext.define( 'Common.controller.NotifyUserController', {
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
            'notifier.show': this.showWindow,
            scope: this
        });
        this.application.on({
            'notifier.close': this.closeWindow,
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
        var form = btn.up('notifyUserWindow').down('form').getForm();
        if( form.isValid() ) {
            form.submit();
            this.closeWindow();
        } else {
            form.markInvalid();
        }
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


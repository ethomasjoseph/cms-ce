Ext.define( 'App.util.AccountKeyMap', {
    extend: 'Ext.util.KeyMap',

    constructor: function( actionHandlers )
    {
        var me = this;
        var document = Ext.getDoc();
        me.callParent( [document, [
            {
                key: "n",
                ctrl: true,
                defaultEventAction: 'stopEvent',
                fn: actionHandlers.newMegaMenu
            },
            {
                key: "o",
                ctrl: true,
                defaultEventAction: 'stopEvent',
                fn: actionHandlers.openItem
            },
            {
                key: "e",
                ctrl: true,
                defaultEventAction: 'stopEvent',
                fn: actionHandlers.editItem
            },
            {
                key: "s",
                ctrl: true,
                defaultEventAction: 'stopEvent',
                fn: actionHandlers.saveItem
            },
            {
                key: "j",
                ctrl: true,
                defaultEventAction: 'stopEvent',
                fn: actionHandlers.prevStep
            },
            {
                key: "k",
                ctrl: true,
                defaultEventAction: 'stopEvent',
                fn: actionHandlers.nextStep
            }
        ]] );
    }

} );
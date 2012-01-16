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
                shift: false,
                alt: false,
                defaultEventAction: 'stopEvent',
                fn: actionHandlers.newMegaMenu
            },
            {
                key: "o",
                ctrl: true,
                shift: false,
                alt: false,
                defaultEventAction: 'stopEvent',
                fn: actionHandlers.openItem
            },
            {
                key: "e",
                ctrl: true,
                shift: false,
                alt: false,
                defaultEventAction: 'stopEvent',
                fn: actionHandlers.editItem
            },
            {
                key: "s",
                ctrl: true,
                shift: false,
                alt: false,
                defaultEventAction: 'stopEvent',
                fn: actionHandlers.saveItem
            },
            {
                key: "j",
                ctrl: true,
                shift: false,
                alt: false,
                defaultEventAction: 'stopEvent',
                fn: actionHandlers.prevStep
            },
            {
                key: "k",
                ctrl: true,
                shift: false,
                alt: false,
                defaultEventAction: 'stopEvent',
                fn: actionHandlers.nextStep
            },
            {
                key: Ext.EventObject.DELETE,
                ctrl: false,
                shift: false,
                alt: false,
                fn: actionHandlers.deleteItem
            }
        ]] );
    }

} );
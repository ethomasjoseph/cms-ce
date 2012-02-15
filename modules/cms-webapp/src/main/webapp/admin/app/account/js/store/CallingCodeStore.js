Ext.define( 'App.store.CallingCodeStore', {
    extend: 'Ext.data.Store',

    model: 'Cms.model.account.CallingCodeModel',

    autoLoad: true,

    remoteFilter: false,

    proxy: {
        type: 'ajax',
        url: 'data/misc/callingcodes/list',
        reader: {
            type: 'json',
            root: 'codes'
        }
    }
} );
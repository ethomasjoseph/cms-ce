Ext.define( 'App.store.AccountStore', {
    extend: 'Ext.data.Store',

    model: 'App.model.AccountModel',

    pageSize: 50,
    remoteSort: true,
    //buffered: true,
    autoLoad: true,

    proxy: {
        type: 'ajax',
        url: 'data/account/search',
        simpleSortMode: true,
        reader: {
            type: 'json',
            root: 'results.accounts',
            totalProperty : 'results.total'
        }
    }
} );
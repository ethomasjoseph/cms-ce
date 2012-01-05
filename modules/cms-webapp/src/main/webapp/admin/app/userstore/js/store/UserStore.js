Ext.define( 'App.store.UserStore', {
    extend: 'Ext.data.Store',

    model: 'App.model.AccountModel',

    pageSize: 100,
    autoLoad: true,
    remoteSort: true,

    proxy: {
        type: 'ajax',
        url: 'data/user/list',
        simpleSortMode: true,
        reader: {
            type: 'json',
            root: 'users',
            totalProperty : 'total'
        }
    }
} );

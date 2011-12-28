Ext.define( 'App.store.AccountStore', {
    extend: 'Ext.data.Store',

    model: 'App.model.AccountModel',

    pageSize: 100,
    autoLoad: true,
    remoteSort: true,

    sorters: [
        {
            property : 'name',
            direction: 'ASC'
        }
    ],

    proxy: {
        type: 'ajax',
        url: 'app2/data/Account.json',
        reader: {
            type: 'json',
            root: 'contentTypes',
            totalProperty : 'total'
        }
    }
} );
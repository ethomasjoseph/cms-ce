Ext.define( 'App.store.TestStore', {
    extend: 'Ext.data.Store',

    model: 'App.model.TestModel',

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
        url: 'app2/data/Test.json',
        reader: {
            type: 'json',
            root: 'contentTypes',
            totalProperty : 'total'
        }
    }
} );
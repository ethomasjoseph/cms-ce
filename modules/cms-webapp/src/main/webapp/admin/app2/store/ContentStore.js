Ext.define( 'App.store.ContentStore', {
    extend: 'Ext.data.Store',

    model: 'App.model.ContentModel',

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
        url: 'app2/data/Content.json',
        reader: {
            type: 'json',
            root: 'contentTypes',
            totalProperty : 'total'
        }
    }
} );
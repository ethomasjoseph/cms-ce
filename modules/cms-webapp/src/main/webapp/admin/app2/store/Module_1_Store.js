Ext.define( 'App.store.Module_1_Store', {
    extend: 'Ext.data.Store',

    model: 'App.model.Module_1_Model',

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
        url: 'app2/data/ContentTypes_1.json',
        reader: {
            type: 'json',
            root: 'contentTypes',
            totalProperty : 'total'
        }
    }
} );
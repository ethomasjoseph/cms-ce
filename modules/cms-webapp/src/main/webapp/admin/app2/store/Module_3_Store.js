Ext.define( 'App.store.Module_3_Store', {
    extend: 'Ext.data.Store',

    model: 'App.model.Module_3_Model',

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
        url: 'app2/data/ContentTypes_3.json',
        reader: {
            type: 'json',
            root: 'contentTypes',
            totalProperty : 'total'
        }
    }
} );
Ext.define( 'App.store.Module_2_Store', {
    extend: 'Ext.data.Store',

    model: 'App.model.Module_2_Model',

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
        url: 'app2/data/ContentTypes_2.json',
        reader: {
            type: 'json',
            root: 'contentTypes',
            totalProperty : 'total'
        }
    }
} );
Ext.define('App.store.UserstoreConnectorStore', {
    extend: 'Ext.data.Store',
    model: 'App.model.UserstoreConnectorModel',

    autoLoad: true,

    proxy: {
        type: 'ajax',
        url: 'data/userstore/connectors',
        simpleSortMode: true,
        reader: {
            type: 'json',
            root: 'userStoreConnectors'
        }
    }
});

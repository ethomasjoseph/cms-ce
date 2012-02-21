Ext.define( 'App.store.SummaryStore', {
    extend: 'Ext.data.TreeStore',

    model: 'App.model.SummaryModel',
    proxy: {
        type: 'ajax',
        //the store will get the content from the .json file
        url: 'summary.json'
    },
    folderSort: false,
    autoLoad: true

} );
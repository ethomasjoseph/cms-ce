Ext.define('App.store.ActivityStreamStore', {
    extend: 'Ext.data.Store',
    model: 'App.model.ActivityStreamModel',

    autoLoad: false,

    proxy: {
        type: 'ajax',
        url: 'app/data/ActivityStream.json',
        reader: {
            type: 'json',
            root: 'activitystreams'
        }
    }
});

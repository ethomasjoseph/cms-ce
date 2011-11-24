Ext.define('App.store.TimezoneStore', {
    extend: 'Ext.data.Store',

    model: 'App.model.TimezoneModel',

    pageSize: 50,
    autoLoad: true,

    proxy: {
        type: 'ajax',
        url: 'data/misc/timezone/list',
        simpleSortMode: true,
        reader: {
            type: 'json',
            root: 'timezones',
            totalProperty : 'total'
        }
    }
});
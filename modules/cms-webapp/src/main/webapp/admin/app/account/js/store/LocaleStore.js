Ext.define('App.store.LocaleStore', {
    extend: 'Ext.data.Store',

    model: 'App.model.LocaleModel',

    pageSize: 50,
    autoLoad: true,

    proxy: {
        type: 'ajax',
        url: 'data/misc/locale/list',
        simpleSortMode: true,
        reader: {
            type: 'json',
            root: 'locales',
            totalProperty : 'total'
        }
    }
});
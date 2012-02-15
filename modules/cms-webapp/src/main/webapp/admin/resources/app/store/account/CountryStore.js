Ext.define('Cms.store.account.CountryStore', {
    extend: 'Ext.data.Store',

    model: 'Cms.model.account.CountryModel',

    //pageSize: 100,
    autoLoad: true,

    proxy: {
        type: 'ajax',
        url: 'data/misc/country/list',
        //url: 'app/data/Countries.json',
        reader: {
            type: 'json',
            root: 'countries',
            totalProperty : 'total'
        }
    }
});
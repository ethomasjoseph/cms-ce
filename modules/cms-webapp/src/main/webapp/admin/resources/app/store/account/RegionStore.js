Ext.define('Cms.store.account.RegionStore', {
    extend: 'Ext.data.Store',

    model: 'Cms.model.account.RegionModel',

    //pageSize: 100,
    autoLoad: false,

    proxy: {
        type: 'ajax',
        url: 'data/misc/region/list',
        //url: 'app/data/Regions.json',
        reader: {
            type: 'json',
            root: 'regions',
            totalProperty : 'total'
        }
    }
});

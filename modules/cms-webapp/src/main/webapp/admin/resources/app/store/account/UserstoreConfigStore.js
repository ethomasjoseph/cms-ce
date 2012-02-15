Ext.define('Cms.store.account.UserstoreConfigStore', {
    extend: 'Ext.data.Store',
    model: 'Cms.model.account.UserstoreConfigModel',
    pageSize: 100,
    autoLoad: true,

    sorters: [
        {
            sorterFn: function(a, b) {
                var nameA= a.get('name').toLowerCase();
                var nameB = b.get('name').toLowerCase();
                if (nameA < nameB)
                {
                    return -1;
                }
                if (nameA > nameB)
                {
                    return 1;
                }
                return 0;
            }
        }
    ],

    proxy: {
        type: 'ajax',
        url: 'data/userstore/list',
        simpleSortMode: true,
        reader: {
            type: 'json',
            root: 'userStoreConfigs'
        }
    }
});

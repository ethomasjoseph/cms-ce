Ext.define('App.store.GroupStore', {
    extend: 'Ext.data.Store',

    model: 'Cms.model.account.GroupModel',

    remoteFilter: true,
    //autoLoad: true,

    proxy: {
        type: 'ajax',
        url: 'data/group/list',
        reader: {
            type: 'json',
            root: 'accounts'
        }
    }
});
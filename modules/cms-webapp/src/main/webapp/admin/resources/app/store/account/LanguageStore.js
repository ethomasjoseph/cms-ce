Ext.define('Cms.store.account.LanguageStore', {
    extend: 'Ext.data.Store',

    model: 'Cms.model.account.LanguageModel',

    pageSize: 100,
    autoLoad: true,

    sorters: [
        {
            property : 'languageCode',
            direction: 'ASC'
        }
    ],

    proxy: {
        type: 'ajax',
        url: 'app/account/js/data/Languages.json',
        reader: {
            type: 'json',
            root: 'languages',
            totalProperty : 'total'
        }
    }
});


Ext.define('Admin.model.account.CountryModel', {
    extend: 'Ext.data.Model',

    idField: 'code',

    fields: [
        'code',
        'englishName',
        'localName',
        'regionsEnglishName',
        'regionsLocalName',
        'callingCode'
    ]
});

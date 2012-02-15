Ext.define( 'Cms.model.account.AccountModel', {
    extend: 'Ext.data.Model',

    fields: [
        'key', 'name', 'email', 'qualifiedName', 'displayName', 'userStore', 'lastModified',
        'hasPhoto', 'type', 'builtIn', 'isEditable'
    ],

    idProperty: 'key'
} );

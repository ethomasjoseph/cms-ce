Ext.define( 'App.model.AccountModel', {
    extend: 'Ext.data.Model',

    fields: [
        'key', 'name', 'email', 'qualifiedName', 'displayName', 'userStore', 'lastModified', 'hasPhoto', 'type', 'builtIn'
    ],

    idProperty: 'key'
} );

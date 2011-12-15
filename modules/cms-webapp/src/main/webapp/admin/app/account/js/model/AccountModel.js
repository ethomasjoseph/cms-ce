Ext.define( 'App.model.AccountModel', {
    extend: 'Ext.data.Model',

    fields: [
        'key', 'name', 'email', 'qualifiedName', 'displayName', 'userStore',
        {name: 'isUser', type: 'boolean'},
        {name: 'lastModified'/*, type: 'date', dateFormat: 'Y-m-d H:i:s'*/}
    ],

    idProperty: 'key'
} );

Ext.define( 'Cms.model.account.GroupModel', {
    extend: 'Ext.data.Model',

    fields: [
        'key', 'name', 'userStore', 'type'
    ],

    idProperty: 'key'
} );

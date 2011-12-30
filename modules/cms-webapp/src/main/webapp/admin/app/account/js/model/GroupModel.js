Ext.define( 'App.model.GroupModel', {
    extend: 'Ext.data.Model',

    fields: [
        'key', 'name', 'userStore', 'type'
    ],

    idProperty: 'key'
} );

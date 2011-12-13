Ext.define('App.model.Module_3_Model', {
    extend: 'Ext.data.Model',

    fields: [
        'key', 'name', 'description', 'data',
        {name: 'timestamp', type: 'date', dateFormat: 'Y-m-d H:i:s'}
    ],

    idProperty: 'key'
});

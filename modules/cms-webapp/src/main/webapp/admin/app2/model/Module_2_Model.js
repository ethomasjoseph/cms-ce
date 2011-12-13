Ext.define('App.model.Module_2_Model', {
    extend: 'Ext.data.Model',

    fields: [
        'key', 'name', 'description', 'data',
        {name: 'timestamp', type: 'date', dateFormat: 'Y-m-d H:i:s'}
    ],

    idProperty: 'key'
});

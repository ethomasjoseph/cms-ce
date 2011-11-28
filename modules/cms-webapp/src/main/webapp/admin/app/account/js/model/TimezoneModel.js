Ext.define('App.model.TimezoneModel', {
    extend: 'Ext.data.Model',

    fields: [
        'id',
        'shortName',
        'name',
        'offset',
        {
            name: 'idAndOffset',
            convert: function(value, record) {
                return record.get('id') + ' (' + record.get('offset') + ')';
            }
        }
    ]
});

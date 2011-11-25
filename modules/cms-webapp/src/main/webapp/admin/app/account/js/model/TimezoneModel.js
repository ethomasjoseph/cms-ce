Ext.define('App.model.TimezoneModel', {
    extend: 'Ext.data.Model',

    fields: [
        'id',
        'shortName',
        'name',
        'offset',
        {
            name: 'nameAndOffset',
            convert: function(value, record) {
                return record.get('name') + ' (' + record.get('offset') + ')';
            }
        }
    ]
});

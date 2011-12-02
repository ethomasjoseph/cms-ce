Ext.define('App.model.TimezoneModel', {
    extend: 'Ext.data.Model',

    fields: [
        'id',
        'humanizedId',
        'shortName',
        'name',
        'offset',
        {
            name: 'humanizedIdAndOffset',
            convert: function(value, record) {
                return record.get('humanizedId') + ' (' + record.get('offset') + ')';
            }
        }
    ]
});

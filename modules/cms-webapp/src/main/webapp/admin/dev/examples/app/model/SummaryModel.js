Ext.define('App.model.SummaryModel', {
    extend: 'Ext.data.Model',

    fields:[
        {name: 'field', type: 'string'},
        {name: 'value', type: 'string'},
        {name: 'changeType', type: 'string'}
    ]
});

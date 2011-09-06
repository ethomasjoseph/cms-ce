Ext.define( 'LPT.model.TraceTreeTableNodeModel', {
    extend: 'Ext.data.Model',

    fields: [
        {name: 'text', type: 'string'},
        {name: 'usedCachedResult', type: 'string'},
        {name: 'duration.humanReadable', type: 'string'}
    ]
} );

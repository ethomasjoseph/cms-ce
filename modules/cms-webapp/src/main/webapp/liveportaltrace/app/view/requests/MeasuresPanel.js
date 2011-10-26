Ext.define( 'LPT.view.requests.MeasuresPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.measuresPanel',

    requires: [
        'Ext.chart.*', 'Ext.chart.axis.Gauge', 'Ext.chart.series.*',
        'LPT.view.requests.GaugePanel', 'LPT.view.requests.MemoryGaugePanel',
        'LPT.view.requests.PageCacheGaugePanel','LPT.view.requests.EntityCacheGaugePanel'
    ],

    id: 'measuresPanel',
    title: 'Application Counters',
    split: true,
    collapsible: true,

    layout: {
        type: 'vbox',
        align : 'stretch',
        pack  : 'start'
    },

    bodyPadding: '20 0 0',

    defaults: {
        margins: '0 0 0 0'
    },

    initComponent: function() {

        this.items = [
            {
                xtype: 'requestsGaugePanel',
                padding: 5
            },
            {
                xtype: 'pageCacheGaugePanel',
                padding: 5
            },
            {
                xtype: 'entityCacheGaugePanel',
                padding: 5
            },
            {
                xtype: 'threadsGaugePanel',
                padding: 5
            },
            {
                xtype: 'memoryGaugePanel',
                padding: 5
            }
        ];

        this.callParent(arguments);
    }

} );

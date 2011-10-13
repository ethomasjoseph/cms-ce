Ext.define( 'LPT.view.requests.GaugePanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.requestsPerformancePanel',

    requires: [
        'Ext.chart.*', 'Ext.chart.axis.Gauge', 'Ext.chart.series.*'
    ],

    id: 'requestsPerformancePanel',
    title: 'Requests per second',
    split: true,
    collapsible: true,

    layout: 'fit',

    bodyPadding: '20 0 0',

    defaults: {
        margins: '0 0 0 0'
    },

    gaugeColor: '#82B525',
    gaugeStore: null,
    firstUpdate: true,

    initComponent: function() {

        this.gaugeStore = Ext.create('Ext.data.JsonStore', {
            fields: ['name', 'data']
        });

        this.items = {
            xtype: 'chart',
            flex: 1,
            insetPadding: 30,
            animate: {
                easing: 'elasticIn',
                duration: 1000
            },
            store: this.gaugeStore,
            axes: [
                {
                    type: 'gauge',
                    position: 'gauge',
                    minimum: 0,
                    maximum: 10,
                    steps: 10,
                    margin: 7
                }
            ],
            series: [
                {
                    type: 'gauge',
                    field: 'data',
                    donut: 70,
                    colorSet: [this.gaugeColor, '#ddd']
                }
            ]
        };

        this.callParent(arguments);
    },

    updateData: function( data ) {
        if (this.firstUpdate) {
            // skip first call (initial loading of data in grid)
            this.firstUpdate = false;
            return;
        }

        if ( data ) {
            if (data.length > 0) {
                var value = data[0].data;
                var maxValue = this.items.get(0).axes.get(0).maximum;
                if (value >= maxValue) {
                    maxValue = value * 1.1; // Increase 10% over the current value
                    maxValue = Math.round(maxValue / 10) * 10; // round it to a multiple of 10

                    // set new chart maximum
                    this.items.get(0).axes.get(0).maximum = maxValue;
                }
            }

            this.gaugeStore.loadData( data );
        }
    }

} );

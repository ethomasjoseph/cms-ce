Ext.define( 'LPT.view.requests.ThreadsGaugePanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.threadsGaugePanel',

    requires: [
        'Ext.chart.*', 'Ext.chart.axis.Gauge', 'Ext.chart.series.*'
    ],

    id: 'threadsGaugePanel',
    title: 'Threads',
    collapsible: true,
    titleCollapse: true,
//    collapsed: true,

    layout: 'fit',
    defaults: {
        margins: '0 0 0 0'
    },

    gaugeColor: '#82B525',
    gaugeStore: null,

    initComponent: function() {

        this.gaugeStore = Ext.create('Ext.data.JsonStore', {
            fields: ['name', 'data']
        });

        this.items = [
            {
                id: 'headerLabelThreads',
                tpl: new Ext.Template('<div class="center">{text}</div>'),
                border: false,
                bodyPadding: 5,
                height: 20
            },
            {
                xtype: 'chart',
                flex: 1,
                insetPadding: 30,
                animate: {
                    easing: 'elasticIn',
                    duration: 1000
                },
                height: 150,
                width: 210,
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
            }
        ];

        this.callParent(arguments);
    },

    updateData: function( data, maxValue ) {
        if ( data ) {
            // set new chart maximum
            this.items.get(1).axes.get(0).maximum = maxValue;
            if (data.length > 0) {
                var value = data[0].data;
                Ext.getCmp('headerLabelThreads').update({text: 'Thread count: ' + value});
            }

            this.gaugeStore.loadData( data );
        }
    }

} );

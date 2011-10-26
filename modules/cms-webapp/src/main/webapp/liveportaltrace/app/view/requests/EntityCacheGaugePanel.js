Ext.define( 'LPT.view.requests.EntityCacheGaugePanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.entityCacheGaugePanel',

    requires: [
        'Ext.chart.*', 'Ext.chart.axis.Gauge', 'Ext.chart.series.*'
    ],

    id: 'entityCacheGaugePanel',
    title: 'Entity Cache',
    collapsible: true,
    titleCollapse: true,

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
                id: 'headerLabel',
                tpl: new Ext.Template('<div class="center">{text}</div>'),
                border: false,
                bodyPadding: 5,
                height: 20
            },
            {
                xtype: 'chart',
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
                        maximum: 100,
                        steps: 5,
                        margin: 7,
                        label: {
                            renderer: function(v) {
                                return v+'%';
                            }
                        }

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

    updateData: function( data ) {
        if ( data ) {
            if (data.length > 0) {
                var value = Math.round(data[0].data);
                Ext.getCmp('headerLabel').update({text: 'Hits vs misses: ' + value + '% hits' });
            }
            this.gaugeStore.loadData( data );
        }
    }

} );

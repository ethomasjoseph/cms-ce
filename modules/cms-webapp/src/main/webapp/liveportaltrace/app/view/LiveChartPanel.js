Ext.define( 'LPT.view.LiveChartPanel', {
    extend: 'Ext.panel.Panel',

    alias : 'widget.liveChartPanel',

    layout: 'fit',
    height: '100%',
    width: '100%',

    baseTime: null,
    valueList: [],
    historyTimeMillis: 5*60*1000, // 5 minutes

    items: [
        {
            xtype: 'chart',
            theme: 'Blue',
            animate: true,
            store: new Ext.data.ArrayStore({
                fields: [{name:'timestamp', type: 'int'},  'requests'],
                data: []
            }),
            id: 'chartCmp',
            axes: [
                {
                    type: 'Numeric',
                    minimum: 0,
                    maximum: 100,
                    position: 'left',
                    fields: ['requests'],
                    title: 'Number of requests',
                    grid: {
                        odd: {
                            fill: '#f8f8f8',
                            stroke: '#ddd',
                            'stroke-width': 0.5
                        }
                    }
                },
                {
                    type: 'Numeric',
                    position: 'bottom',
                    fields: ['timestamp'],
                    title: 'Time',
                    minimum: 0,
                    maximum: this.historyTimeMillis,
                    majorTickSteps: 10,
                    label: {
                        renderer: function(value) {
                            var initialTime = Ext.getCmp('liveChartPanel').baseTime;
                            var date = new Date(value + initialTime);
                            return Ext.Date.format(date, "H:i:s");
                        }
                    }
                }
            ],
            series: [
                {
                    type: 'line',
                    smooth: true,
                    fill : true,
                    shadow: true,
                    style: {
                        opacity: 0.3
                    },

                    axis: ['left', 'bottom'],
                    xField: 'timestamp',
                    yField: 'requests',
                    label: {
                        display: 'none',
                        field: 'requests',
                        renderer: function( v )
                        {
                            return v >> 0;
                        },
                        'text-anchor': 'middle'
                    },
                    markerConfig: {
                        type: 'circle',
                        size: 2,
                        radius: 2,
                        'stroke-width': 0
                    }
                }
            ]
        }
    ],

    updateData: function( data ) {
        if ( data && (data.length > 0)) {

            try {
                var now = new Date().getTime();
                if (!this.baseTime) {
                    this.baseTime = now;
                    data[0].data = 0; // initial value 0
                }

                var value = data[0].data;
                var point = {timestamp: (now - this.baseTime), requests: value};
                var store = Ext.getCmp('chartCmp').store;

                // shift values to the left before adding new one
                while ((this.valueList.length > 1) && (point.timestamp > (this.historyTimeMillis) ) ) {
                    this.valueList = this.valueList.slice(1);

                    var firstPoint = this.valueList[0];
                    var diff = firstPoint.timestamp;

                    this.baseTime = this.baseTime + diff;

                    for (var i = 0; i < this.valueList.length; i ++) {
                        this.valueList[i].timestamp -= diff;
                    }
                    point.timestamp = (now - this.baseTime);
                }
                this.valueList.push(point);

                store.loadData(this.valueList.slice(0));
            } catch (e) {
                console.log(e);
            }
        }
    }

} );

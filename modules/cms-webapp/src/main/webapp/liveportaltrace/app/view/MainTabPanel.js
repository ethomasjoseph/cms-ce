Ext.define( 'LPT.view.MainTabPanel', {
    extend: 'Ext.tab.Panel',
    alias : 'widget.mainTabPanel',

    requires: [
        'Ext.tab.*',
        'LPT.view.requests.PerformancePanel',
        'LPT.view.requests.InfoPanel',
        'LPT.view.GeoMapPanel',
        'LPT.view.LiveChartPanel'
    ],
    layout: 'fit',
    activeTab: 0,

    initComponent: function() {
        this.items = [
            {
                region: 'center',
                xtype: 'portalRequestTraceHistoryPanel',
                title: 'Completed Requests',
                closable: false
            },
//            {
//                region: 'center',
//                xtype: 'cmsInfoPanel',
//                title: 'Status Info',
//                closable: false
//            },
            {
                region: 'center',
                xtype: 'geomap',
                id: 'geomapPanel',
                title: 'Location Map',
                closable: false
            },
            {
                region: 'center',
                xtype: 'liveChartPanel',
                id: 'liveChartPanel',
                title: 'Live Traffic',
                closable: false
            }
            /*,
            {
                region: 'center',
                xtype: 'performancePanel',
                title: 'Performance Measures',
                closable: false
            }*/
        ];

        this.callParent(arguments);

        if (window.location.hash === '#map') {
            this.setActiveTab(1);
        }
    }
});

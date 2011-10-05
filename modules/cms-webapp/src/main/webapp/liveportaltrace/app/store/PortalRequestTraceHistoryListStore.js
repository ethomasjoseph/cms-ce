Ext.define('LPT.store.PortalRequestTraceHistoryListStore', {
    extend: 'Ext.data.Store',

    model: 'LPT.model.PortalRequestTraceModel',

    pageSize: 100,
    autoLoad: false,

    proxy: {
        type: 'ajax',
        url: '/liveportaltrace/rest/portal-request-trace-history/list',
        reader: {
            type: 'json',
            root: 'requests',
            totalProperty : 'total'
        },
        startParam: 'lastId'
    },

    sorters: [{
        property : 'id',
        direction: 'DESC'
       }
    ],

    lastRequestId: 0
});

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

//    listeners:{
//        load: function(store, records, successful, operation) {
//            var i, req;
//            if (records) {
//                // keep last request-id
//                for (i = 0; i < records.length; i ++) {
//                    req = records[i].data;
//                    if (req.id > store.lastRequestId) {
//                        store.lastRequestId = req.id;
//                    }
//                }
//            }
//        },
//
//        beforeload: function(store, operation) {
//            // ask for requests starting after the last request-id
//            operation.start = store.lastRequestId;
//        }
//    }

});

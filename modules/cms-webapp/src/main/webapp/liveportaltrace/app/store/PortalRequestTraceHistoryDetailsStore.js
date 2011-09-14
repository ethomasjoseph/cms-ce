Ext.define('LPT.store.PortalRequestTraceHistoryDetailsStore', {
    extend: 'Ext.data.TreeStore',

    storeId: 'PortalRequestTraceHistoryDetailsStore',

    nodeParam: 'id',

    folderSort: false,
    defaultRootId: '-1',

    proxy: {
        type: 'ajax',
        url: '/liveportaltrace/rest/portal-request-trace-history/detail',
        reader: {
            type: 'json'
        }
    },
    fields: [
        {name: 'text', type: 'string'},
        {name: 'usedCachedResult', type: 'string'},
        {name: 'duration.humanReadable', type: 'auto'}
    ]

} );

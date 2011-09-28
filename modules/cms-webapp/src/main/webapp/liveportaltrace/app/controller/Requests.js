var REQUESTS_REFRESH_TIME = 1000;

Ext.define('LPT.controller.Requests', {
    extend: 'Ext.app.Controller',

    stores: [
        'PortalRequestTraceHistoryListStore',
        'PortalRequestTraceHistoryDetailsStore' ,
        'GeolocationStore',
        'StatusInfo'
    ],
    models: [
        'PortalRequestTraceModel',
        'GeolocationModel',
        'StatusInfo'
    ],
    views: [
        'requests.PortalRequestTraceHistoryGrid',
        'requests.FilterPanel',
        'requests.PortalRequestTraceHistoryPanel',
        'requests.PortalRequestTraceHistoryDetailsPanel',
        'requests.ContextMenu'
    ],

    refs: [
        {ref: 'portalRequestTraceHistoryGrid', selector: 'portalRequestTraceHistoryGrid'},
        {ref: 'mainTabPanel', selector: 'mainTabPanel'},
        {ref: 'portalRequestTraceHistoryDetailsPanel', selector: 'portalRequestTraceHistoryDetailsPanel', xtype: 'portalRequestTraceHistoryDetailsPanel'},
        {ref: 'requestContextMenu', selector: 'requestContextMenu', autoCreate: true, xtype: 'requestContextMenu'}
    ],

    init: function() {
        this.control({
            'portalRequestTraceHistoryGrid': {
                itemdblclick: this.showRequestInfo,
                itemcontextmenu: this.popupMenu
            },
            'portalRequestTraceHistoryGrid > tableview': {
            },
            '*[action=details]': {
                click: this.onContextMenuDetails
            }
        });

        this.startAutoRefreshTimer();
    },

    showRequestInfo: function( view, modelItem, htmlEl, idx, e )
    {
        var req = modelItem;
        if ( req )
        {
            this.showRequestDetails(req);
        }
    },

    getRequestTitleForTab: function(reqData) {
//        var title = reqData.id +' - ';
//        var p = reqData.url.lastIndexOf('/');
//        var url = (p > 0)? '...'+reqData.url.substr(p) : reqData.url;
//        title += url;
//        return title;
        return reqData.url;
    },

    popupMenu: function(view, rec, node, index, e) {
        e.stopEvent();
        this.getRequestContextMenu().showAt(e.getXY());
        return false;
    },

    onContextMenuDetails: function () {
        var req = this.getPortalRequestTraceHistoryGrid().getSelectionModel().selected.get(0);
        this.showRequestDetails(req);
    },

    showRequestDetails: function (selectedRequest) {

        var detailsTab = this.getPortalRequestTraceHistoryDetailsPanel();

        detailsTab.setVisible(true);
        detailsTab.setTitle( "Details of Request #" + selectedRequest.data.id );

        detailsTab.store.setRootNode( {id: selectedRequest.data.id});

        //var path = "Portal request: " + selectedRequest.data["url.siteLocalUrl"];
        var path = "/Portal request";

        // TODO: detailsTab.store.selectPath( path, "text" );
    },

    startAutoRefreshTimer: function () {
        var controller = this;
        setTimeout( function() {
            controller.loadPortalRequests();
        }, REQUESTS_REFRESH_TIME);
    },

    loadPortalRequests: function () {
        var controller = this;
        var store = this.getPortalRequestTraceHistoryGrid().getView().getStore();
        Ext.Ajax.request( {
            url: '/liveportaltrace/rest/portal-request-trace-history/list',
            method: 'GET',
            params: {lastId: store.lastRequestId},
            success: function( response ) {
                var requestJson = Ext.JSON.decode( response.responseText );
                var requestObject, r, requestId, requestArray;

                requestJson = requestJson && requestJson.requests;
                if (requestJson && (requestJson.length > 0)) {
                    // add new requests to grid panel
                    requestArray = [];
                    for (r = 0; r < requestJson.length; r++) {
                        requestObject = controller.requestJsonToModel(requestJson[r]);
                        requestId = requestObject.get('id');

                        if (requestId > store.lastRequestId) {
                            store.lastRequestId = requestId;
                        }
                        requestArray.push(requestObject);
                    }
                    store.insert(0, requestArray);
                }

                controller.startAutoRefreshTimer(); // restart timer
            },
            failure: function() {
                controller.startAutoRefreshTimer(); // restart timer
            }
        });
    },

    requestJsonToModel: function(requestJson) {
        var requestObject = Ext.create('LPT.model.PortalRequestTraceModel', requestJson);
        requestObject.set('url.originalURL', requestJson.url.originalURL);
        requestObject.set('url.siteLocalUrl', requestJson.url.siteLocalUrl);
        requestObject.set('url.internalURL', requestJson.url.internalURL);
        requestObject.set('site.key', requestJson.site.key);
        requestObject.set('site.name', requestJson.site.name);
        requestObject.set('duration.startTime', requestJson.duration.startTime);
        requestObject.set('duration.milliseconds', requestJson.duration.milliseconds);
        requestObject.set('duration.humanReadable', requestJson.duration.humanReadable);
        requestObject.commit(true);
        return requestObject;
    }

});

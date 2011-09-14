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
        this.autoRefreshIntervalId = setInterval(
            function() {
                // find selected item before refreshing
                var grid = controller.getPortalRequestTraceHistoryGrid();
                var idx = grid.getSelectionModel().getSelection();
                var selected = idx.length > 0;

                controller.getPortalRequestTraceHistoryGrid().getView().getStore().load();

                if (selected) {
                    // restore selected item
                    grid.getSelectionModel().select(idx[0].index, false, false);
                    controller.getRequestTable().getView().refresh();
                }
            },
            3000);
    },

    stopAutoRefreshTimer: function () {
        clearInterval(this.autoRefreshIntervalId);
    },

    autoRefreshIntervalId: null

});

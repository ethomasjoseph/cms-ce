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
        'requests.ContextMenu',
        'requests.GaugePanel',
        'requests.ThreadsGaugePanel'
    ],

    refs: [
        {ref: 'portalRequestTraceHistoryGrid', selector: 'portalRequestTraceHistoryGrid'},
        {ref: 'filterPanel', selector: 'requestsFilterPanel'},
        {ref: 'mainTabPanel', selector: 'mainTabPanel'},
        {ref: 'portalRequestTraceHistoryDetailsPanel', selector: 'portalRequestTraceHistoryDetailsPanel', xtype: 'portalRequestTraceHistoryDetailsPanel'},
        {ref: 'requestContextMenu', selector: 'requestContextMenu', autoCreate: true, xtype: 'requestContextMenu'},
        {ref: 'requestsGaugePanel', selector: 'requestsGaugePanel', xtype: 'requestsGaugePanel'},
        {ref: 'memoryGaugePanel', selector: 'memoryGaugePanel', xtype: 'memoryGaugePanel'},
        {ref: 'entityCacheGaugePanel', selector: 'entityCacheGaugePanel', xtype: 'entityCacheGaugePanel'},
        {ref: 'pageCacheGaugePanel', selector: 'pageCacheGaugePanel', xtype: 'pageCacheGaugePanel'},
        {ref: 'threadsGaugePanel', selector: 'threadsGaugePanel', xtype: 'threadsGaugePanel'},
        {ref: 'liveChartPanel', selector: 'liveChartPanel', xtype: 'liveChartPanel'}
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
            },
            '#filterIncludePageRequestsCheckbox': {
                change: this.applyFilterForCompletedRequests
            },
            '#filterIncludeWindowRequestsCheckbox': {
                change: this.applyFilterForCompletedRequests
            }
            ,
            '#filterIncludeImageRequestsCheckbox': {
                change: this.applyFilterForCompletedRequests
            }
            ,
            '#filterIncludeAttachmentRequestsCheckbox': {
                change: this.applyFilterForCompletedRequests
            },
            'requestsFilterPanel': {
                render: this.requestsFilterPanelRender
            },
            '*[action=filtersite]': {
                change: this.applyFilterForCompletedRequests
            },
            '*[action=stopAutoRefresh]': {
                click: this.onStopAutoRefresh
            },
            '*[action=startAutoRefresh]': {
                click: this.onStartAutoRefresh
            }
        });

        this.startAutoRefreshTimer();
        this.startSystemCountersTimer();
    },

    totalFacetStatistics: new FacetStatistics(),

    siteNames: {},
    
    autoRefreshOn: true,

    searchFilterTypingTimer: null,

    requestsFilterPanelRender: function()
    {
        var filterTextField = Ext.getCmp( 'filterSiteText' );
        filterTextField.addListener( 'keypress', this.searchFilterKeyPress, this );
    },

    showRequestInfo: function( view, modelItem, htmlEl, idx, e )
    {
        var req = modelItem;
        if ( req )
        {
            this.showRequestDetails(req);
        }
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
        if (!this.autoRefreshOn) {
            this.startAutoRefreshTimer(); // restart timer
            return;
        }
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
                    try  {
                        store.insert(0, requestArray);
                    } catch (e) {
                        console.log(e);
                    }
                    controller.showRequestsPerSecond(requestArray.length);

                    // collecting statistics
                    Ext.Array.each( requestArray, function( item )
                    {
                        var trace = item.data;
                        if ( trace.requestType === "Page" )
                        {
                            controller.totalFacetStatistics.numberOfPageRequests++;
                        } else if ( trace.requestType === "Window" )
                        {
                            controller.totalFacetStatistics.numberOfWindowRequests++;
                        } else if ( trace.requestType === "Image" )
                        {
                            controller.totalFacetStatistics.numberOfImageRequests++;
                        } else if ( trace.requestType === "Attachment" )
                        {
                            controller.totalFacetStatistics.numberOfAttachmentRequests++;
                        }

                        var siteName = trace.site.name;
                        var siteKey = siteName.replace( / /g, "_" );
                        var siteInfo = controller.totalFacetStatistics.siteStatsByKey[siteKey];
                        if( siteInfo == null )
                        {
                            controller.totalFacetStatistics.siteStatsByKey[siteKey] = { name: siteName, count: 1 };
                        }
                        else
                        {
                            siteInfo.count = siteInfo.count + 1;
                        }

                    }, this );

                    // update gui with collected statistics
                    controller.getFilterPanel().updateFacetStatistics( controller.totalFacetStatistics );
                } else {
                    controller.showRequestsPerSecond(0);
                }

                controller.startAutoRefreshTimer(); // restart timer
            },
            failure: function() {
                controller.startAutoRefreshTimer(); // restart timer
            }
        });
    },

    applyFilterForCompletedRequests: function()
    {
        console.log('applyFilterForCompletedRequests');
        var store = Ext.data.StoreManager.lookup( 'PortalRequestTraceHistoryListStore' );

        var filterIncludePageRequestsCheckbox = Ext.getCmp( 'filterIncludePageRequestsCheckbox' );
        var filterIncludeWindowRequestsCheckbox = Ext.getCmp( 'filterIncludeWindowRequestsCheckbox' );
        var filterIncludeAttachmentRequestsCheckbox = Ext.getCmp( 'filterIncludeAttachmentRequestsCheckbox' );
        var filterIncludeImageRequestsCheckbox = Ext.getCmp( 'filterIncludeImageRequestsCheckbox' );
        var filterSite = Ext.getCmp( 'filterSiteText' );

        var me = this;
        var requestTypeFilter = new Ext.util.Filter( {
                                         filterFn: function( item )
                                         {
                                             var dontAccept = false;
                                             if ( !filterIncludePageRequestsCheckbox.getValue() )
                                             {
                                                 dontAccept = item.data.requestType === 'Page';
                                             }
                                             if ( dontAccept )
                                             {
                                                 return false;
                                             }

                                             if ( !filterIncludeWindowRequestsCheckbox.getValue() )
                                             {
                                                 dontAccept = item.data.requestType === 'Window';
                                             }
                                             if ( dontAccept )
                                             {
                                                 return false;
                                             }

                                             if ( !filterIncludeAttachmentRequestsCheckbox.getValue() )
                                             {
                                                 dontAccept = item.data.requestType === 'Attachment';
                                             }
                                             if ( dontAccept )
                                             {
                                                 return false;
                                             }

                                             if ( !filterIncludeImageRequestsCheckbox.getValue() )
                                             {
                                                 dontAccept = item.data.requestType === 'Image';
                                             }
                                             if ( dontAccept )
                                             {
                                                 return false;
                                             }

                                             if ( filterSite.getValue().length > 0 )
                                             {
                                                 if (me.filterSearch(item.data, filterSite.getValue()))
                                                 {
                                                     return false;
                                                 }
                                             }

                                             return true;
                                         }
                                     } );

        store.clearFilter();
        store.filter( requestTypeFilter );

        this.getFilterPanel().updateFacetStatistics( this.countFacets() );
    },

    filterSearch: function(reqData, searchText)
    {
        searchText = searchText.toLowerCase();
        if (searchText.substring(0, 1) === '/') {
            var url = reqData.url.siteLocalUrl.toLowerCase();
            return url.substring(0, searchText.length) !== searchText;
        } else {
            var siteName = reqData.site.name.toLowerCase();
            return siteName.substring(0, searchText.length) !== searchText;
        }
    },

    countFacets: function()
    {
        var store = this.getPortalRequestTraceHistoryGrid().getView().getStore();
        var facetStatistics = new FacetStatistics();
        Ext.Array.each( store.data.items, function(item) {

            var trace = item.data;
            if( trace.requestType === "Page" )
            {
                facetStatistics.numberOfPageRequests++;
            }
            else if( trace.requestType === "Window" )
            {
                facetStatistics.numberOfWindowRequests++;
            }
            else if( trace.requestType === "Image" )
            {
                facetStatistics.numberOfImageRequests++;
            }
            else if( trace.requestType === "Attachment" )
            {
                facetStatistics.numberOfAttachmentRequests++;
            }
        }, this);

        return facetStatistics;
    },

    onStopAutoRefresh: function() {
        var stopButton = Ext.getCmp( 'stopAutoRefreshButton' );
        var startButton = Ext.getCmp( 'startAutoRefreshButton' );
        stopButton.hide();
        startButton.show();
        this.autoRefreshOn = false;
    },

    onStartAutoRefresh: function() {
        var stopButton = Ext.getCmp( 'stopAutoRefreshButton' );
        var startButton = Ext.getCmp( 'startAutoRefreshButton' );
        startButton.hide();
        stopButton.show();
        this.autoRefreshOn = true;
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
    },

    startSystemCountersTimer: function () {
        var controller = this;
        setTimeout( function() {
            controller.loadSystemCounters();
        }, REQUESTS_REFRESH_TIME);
    },

    loadSystemCounters: function () {
        if (!this.autoRefreshOn) {
            this.startSystemCountersTimer(); // restart timer
            return;
        }
        var controller = this;
        Ext.Ajax.request( {
            url: '/liveportaltrace/rest/system/counters',
            method: 'GET',
            success: function( response ) {
                var systemInfo = Ext.JSON.decode( response.responseText );

                controller.showMemoryUsage(systemInfo.javaHeapMemoryUsageUsed, systemInfo.javaHeapMemoryUsageMax);
                controller.showThreadCount(systemInfo.javaThreadCount, systemInfo.javaThreadPeakCount);

                var entityCacheTotal = systemInfo.entityCacheHitCount + systemInfo.entityCacheMissCount;
                controller.showEntityCacheUsage(systemInfo.entityCacheHitCount, entityCacheTotal);

                var pageCacheTotal = systemInfo.pageCacheHitCount + systemInfo.pageCacheMissCount;
                controller.showPageCacheUsage(systemInfo.pageCacheHitCount, pageCacheTotal);

                controller.startSystemCountersTimer(); // restart timer
            },
            failure: function() {
                controller.startSystemCountersTimer(); // restart timer
            }
        });
    },

    showRequestsPerSecond: function (value) {
        var requestsGaugePanel = this.getRequestsGaugePanel();
        requestsGaugePanel.updateData( [{
            name: 'requests',
            data: ( value )
        }] );

        var liveChartPanel = this.getLiveChartPanel();
        liveChartPanel.updateData( [{
            name: 'requests',
            data: ( value )
        }] );
    },

    showMemoryUsage: function (value, maxValue) {
        var memoryGaugePanel = this.getMemoryGaugePanel();
        memoryGaugePanel.updateData( [{
            name: 'memory',
            data: ( value )
        }], maxValue );
    },

    showEntityCacheUsage: function (value, maxValue) {
        var valPercent = (maxValue !== 0)? (value / maxValue) * 100 : 0;
        var entityCachePanel = this.getEntityCacheGaugePanel();
        entityCachePanel.updateData( [{
            name: 'entity hits',
            data: ( valPercent )
        }] );
    },

    showPageCacheUsage: function (value, maxValue) {
        var valPercent = (maxValue !== 0)? (value / maxValue) * 100 : 0;
        var pageCachePanel = this.getPageCacheGaugePanel();
        pageCachePanel.updateData( [{
            name: 'page hits',
            data: ( valPercent )
        }] );
    },

    showThreadCount: function (value, maxValue) {
        var threadsGaugePanel = this.getThreadsGaugePanel();
        threadsGaugePanel.updateData( [{
            name: 'thread count',
            data: ( value )
        }], maxValue );
    },

    searchFilterKeyPress: function ()
    {
        if ( this.searchFilterTypingTimer != null )
        {
            window.clearTimeout( this.searchFilterTypingTimer );
            this.searchFilterTypingTimer = null;
        }
        var me = this;
        this.searchFilterTypingTimer = window.setTimeout( function ()
                                                          {
                                                              me.applyFilterForCompletedRequests();
                                                          }, 500 );
    }

});

function FacetStatistics() {
    this.numberOfPageRequests =  0;
    this.numberOfWindowRequests = 0;
    this.numberOfImageRequests = 0;
    this.numberOfAttachmentRequests = 0;
    this.siteStatsByKey = {};
};

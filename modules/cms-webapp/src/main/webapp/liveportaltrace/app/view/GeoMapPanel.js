var Places = {
    Oslo: new OpenLayers.LonLat(10.75, 59.91),
    Barcelona: new OpenLayers.LonLat(2.15899, 41.3888),
    NewYork: new OpenLayers.LonLat(-73.9739, 40.7555)
};

Ext.define('LPT.view.GeoMapPanel', {
	extend: 'Ext.panel.Panel',
    requires: [
        'LPT.store.GeolocationStore'
    ],

    alias : 'widget.geomap',

    layout: 'fit',
    height: '100%',
    width: '100%',

	initComponent: function() {
     this.callParent(arguments);
		 this.on('afterrender', this.afterRender, this);
	},

    firstTimeRender: true,
    lastRequestId: 0,

	afterRender: function() {
        if (!this.firstTimeRender) {
            return;
        }
        this.firstTimeRender = false;
        
		var wh = this.ownerCt.getSize();
		Ext.applyIf(this, wh);
		this.callParent(arguments);

		this.map = new OpenLayers.Map(this.body.dom.id);

        // Open Street Map
        this.layer = new OpenLayers.Layer.OSM('OSM Map');
		this.layer.setIsBaseLayer(true);
        this.layer.attribution = '';
//        this.layer.maxExtent = new OpenLayers.Bounds(-20037508.34,-20037508.34,20037508.34,20037508.34);
		this.map.addLayer(this.layer);


        // feature layer
        this.vectorLayer = new OpenLayers.Layer.Vector("Overlay");
        this.map.addLayer(this.vectorLayer);

        // mark points in map
        var pointStyles = new OpenLayers.StyleMap({
            "default": new OpenLayers.Style({
                pointRadius: "${size}", // sized according to size attribute
                fillColor: "#ffcc66",
                strokeColor: "#ff9933",
                strokeWidth: 1,
                graphicZIndex: 1
            })
        });

        // Create a vector layer and give it your style map.
        this.pointsLayer = new OpenLayers.Layer.Vector("Points", {
            styleMap: pointStyles,
            rendererOptions: {zIndexing: true}
        });
        this.map.addLayer(this.pointsLayer);

        this.map.setCenter(Places.Oslo, 3);

        var thisObj = this;
        // start point animation
        setInterval(function() {
                var that = thisObj;
                that.animatePoints();
            },
            100
        );

        // start auto-update timer
        setInterval(function() {
                var that = thisObj;
                that.autoRefreshLocations();
            }, 1000*4
        );
	},

    animatePoints: function () {
        var i = 0;
        var featureItems = this.pointsLayer.features;
        var featuresToBeRemoved = [];
        for (i = 0; i < featureItems.length; i++) {
            if (featureItems[i].attributes.ttl <= 0) {
                featuresToBeRemoved.push(featureItems[i]);
                continue;
            }
            var size = featureItems[i].attributes.size;
            var incrementDirection = featureItems[i].attributes.incr;
            if ((size >= 10) || (size <= 0)) {
                incrementDirection = -incrementDirection;
            }
            size = size + incrementDirection;

            featureItems[i].attributes.size = size;
            featureItems[i].attributes.incr = incrementDirection;
            featureItems[i].attributes.ttl = featureItems[i].attributes.ttl - 1;
        }

        if (featuresToBeRemoved.length > 0) {
            this.pointsLayer.removeFeatures(featuresToBeRemoved);
        }
        this.pointsLayer.redraw();
    },

    autoRefreshLocations: function() {
        var i = 0;
        console.log('Refreshing geolocation data');

        this.loadLocations( this.lastRequestId );
    },

    loadLocations: function(lastReqId) {
        var that = this;
        Ext.Ajax.request( {
            url: '/liveportaltrace/rest/locations',
            method: 'GET',
            params: {lastId: lastReqId},
            success: function( response, opts )
            {
                var resp = Ext.decode(response.responseText);

                that.lastRequestId = resp.lastId;
                var locationList = resp.locationList;

                console.log('Geolocation data refreshed');
                for (i = 0; i < locationList.length; i++) {
                    var location = locationList[i];
                    var lonlat = new OpenLayers.LonLat(location.longitude, location.latitude);
                    that.pointsLayer.addFeatures([that.createFeaturePoint(lonlat)]);
                }

                that.zoomToFeatures();
            },
            failure: function( response, opts )
            {
                console.log('Could not retrieve locations');
            }
        } );

    },

    zoomToFeatures: function() {
        var features = this.pointsLayer.features;
        if (features && features.length > 1) {
            var bounds = features[0].geometry.getBounds().clone();
            for (var i = 1; i < features.length; i++) {
                bounds.extend(features[i].geometry.getBounds());
            }
            this.map.zoomToExtent(bounds,false);
        }
    },

    markFeature: function(place) {
        var feature = new OpenLayers.Feature.Vector(
            this.convertLongigudeLatitudeToPoint(place),
            {some:'data'},
            {externalGraphic: 'OpenLayers/img/marker-blue.png', graphicHeight: 21, graphicWidth: 16}
        );
        this.vectorLayer.addFeatures(feature);
    },

    createFeaturePoint: function(place) {
        var feature = new OpenLayers.Feature.Vector(
            this.convertLongigudeLatitudeToPoint(place),
            {size: 5 + parseInt(5 * Math.random(), 10), incr: 1, ttl: 200}
        );
        return feature;
    },

    convertLongigudeLatitudeToPoint: function(lonLat, projection) {
        projection = projection || "EPSG:4326";
        var proj = new OpenLayers.Projection(projection);
        lonLat.transform(proj, this.map.getProjectionObject());
        var point = new OpenLayers.Geometry.Point(lonLat.lon, lonLat.lat);
        return point;
    }

});

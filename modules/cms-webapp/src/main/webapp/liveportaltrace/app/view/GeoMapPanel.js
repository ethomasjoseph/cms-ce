var Places = {
    Oslo: new OpenLayers.LonLat(10.75, 59.91),
    Barcelona: new OpenLayers.LonLat(2.15899, 41.3888),
    NewYork: new OpenLayers.LonLat(-73.9739, 40.7555)
};

// constants
var MinimumZoomDistance = 10*1000*1000;
var FeaturePointRadius = 8;
var FeaturePointLifeTime = 500;
var FeatureColors = [
    {fill: '#ffcc66', border: '#ff9933'},
    {fill: '#0033cc', border: '#000099'},
    {fill: '#00cc00', border: '#66ff33'}
];

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
        this.on('activate', this.activate, this);
	},

    firstTimeRender: true,
    lastRequestId: 0,
    featureColorIndex: 0,

    activate: function() {
        // set map size to container panel size
        var size = this.getSize();
        if ( (this.map.size.w === size.width) && (this.map.size.h === size.height) ) {
            return;
        }

        this.map.updateSize();
    },

	afterRender: function() {
        var thisObj = this;

        if (!this.firstTimeRender) {
            return;
        }
        this.firstTimeRender = false;
        
		var wh = this.ownerCt.getSize();
		Ext.applyIf(this, wh);
		this.callParent(arguments);

        var panzoombar = new OpenLayers.Control.PanZoomBar();
        panzoombar.zoomBarDrag = function() {
            thisObj.userChangedZoom = true;
        }

		this.map = new OpenLayers.Map(this.body.dom.id, {
                    controls: [
                        panzoombar,
                        new OpenLayers.Control.Navigation(),
                        new OpenLayers.Control.KeyboardDefaults()
                    ],
                    numZoomLevels: 6
        });

        // Open Street Map
        this.layer = new OpenLayers.Layer.OSM('OSM Map');
		this.layer.setIsBaseLayer(true);
        this.layer.attribution = '';
		this.map.addLayer(this.layer);


        // feature layer
        this.vectorLayer = new OpenLayers.Layer.Vector("Overlay");
        this.map.addLayer(this.vectorLayer);

        // mark points in map
        var pointStyles = new OpenLayers.StyleMap({
            "default": new OpenLayers.Style({
                graphicName: 'circle',
                label: '${label}',
                labelAlign: 'tr',
                labelSelect: true,
                fontOpacity: 1,
                fontColor: '#666666',//'${bordercolor}',
                pointRadius: "${size}",
                fillColor: '${bordercolor}',//"${fillcolor}",
                fillOpacity: 0.4,
                strokeColor: '${bordercolor}',
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

        // Ensure that center is not set
        if (!this.map.getCenter()) {
            this.map.setCenter(Places.Oslo, 3);
        }

        // start point animation
        setInterval(function() {
                var that = thisObj;
                that.animatePoints();
            },
            100
        );

        // start auto-update timer
        this.startAutoRefreshTimer();
	},

    startAutoRefreshTimer: function () {
        var me = this;
        setTimeout( function() {
            me.autoRefreshLocations();
        }, 1000);
    },

    animatePoints: function () {
        var i = 0;
        var featureItems = this.pointsLayer.features;
        var featuresToBeRemoved = [];
        var maxSize = FeaturePointRadius * 2;
        for (i = 0; i < featureItems.length; i++) {
            if (featureItems[i].attributes.ttl <= 0) {
                featuresToBeRemoved.push(featureItems[i]);
                continue;
            } else if (featureItems[i].attributes.ttl < (FeaturePointLifeTime - 100)) {
                featureItems[i].attributes.label = '';
            }
            var size = featureItems[i].attributes.size;
            var incrementDirection = featureItems[i].attributes.incr;
            if ((size > maxSize) || (size < 1)) {
//                incrementDirection = -incrementDirection;
                size = 1;
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
        this.loadLocations( this.lastRequestId );
    },

    loadLocations: function(lastReqId) {
        var me = this;
        Ext.Ajax.request( {
            url: '/liveportaltrace/rest/locations',
            method: 'GET',
            params: {lastId: lastReqId},
            success: function( response, opts )
            {
                var resp = Ext.decode(response.responseText);

                me.lastRequestId = resp.lastId;
                var locationList = resp.locations;

                for (i = 0; i < locationList.length; i++) {
                    var location = locationList[i];
                    var lonlat = new OpenLayers.LonLat(location.longitude, location.latitude);
                    me.pointsLayer.addFeatures([me.createFeaturePoint(lonlat, location)]);
                }

                if (! me.userChangedZoom) {
                    me.zoomToFeatures();
                }
                me.pointsLayer.redraw(true);

                me.startAutoRefreshTimer(); // restart timer
            },
            failure: function( response, opts )
            {
                //console.log('Could not retrieve locations');
                me.startAutoRefreshTimer(); // restart timer
            }
        } );

    },

    zoomToFeatures: function() {
        var features = this.pointsLayer.features;
        var bounds;
        if (! features || features.length === 0) {
            return;
        }

        if (features.length === 1) {
            bounds = features[0].geometry.getBounds().clone();
        } else {
            bounds = features[0].geometry.getBounds().clone();
            for (var i = 1; i < features.length; i++) {
                bounds.extend(features[i].geometry.getBounds());
            }
        }
        if (bounds.getWidth() < MinimumZoomDistance) {
            bounds.left = bounds.left - (MinimumZoomDistance/2);
            bounds.right = bounds.right + (MinimumZoomDistance/2);
        }
        if (bounds.getHeight() < MinimumZoomDistance) {
            bounds.top = bounds.top - (MinimumZoomDistance/2);
            bounds.bottom = bounds.bottom + (MinimumZoomDistance/2);
        }

        this.map.zoomToExtent(bounds, false);
    },

    markFeature: function(place) {
        var feature = new OpenLayers.Feature.Vector(
            this.convertLongigudeLatitudeToPoint(place),
            {some:'data'},
            {externalGraphic: 'OpenLayers/img/marker-blue.png', graphicHeight: 21, graphicWidth: 16}
        );
        this.vectorLayer.addFeatures(feature);
    },

    createFeaturePoint: function(place, locationData) {
//        this.featureColorIndex = (this.featureColorIndex + 1) % FeatureColors.length;
        var nextColor = FeatureColors[this.featureColorIndex];
        var direction = Math.random() > 0.5 ? 1 : -1;

        var feature = new OpenLayers.Feature.Vector(
            this.convertLongigudeLatitudeToPoint(place), {
                size: parseInt(FeaturePointRadius*2 * Math.random(), 10),
                fillcolor: nextColor.fill,
                bordercolor: this.getRandomColor(), //nextColor.border,
                incr: 0.5,//direction,
                ttl: FeaturePointLifeTime,
                label: locationData.city + '(' + locationData.country + ')'
            }
        );
        return feature;
    },

    getRandomColor: function() {
        return "#"+((1<<24)*Math.random()|0).toString(16);
    },

    convertLongigudeLatitudeToPoint: function(lonLat, projection) {
        projection = projection || "EPSG:4326";
        var proj = new OpenLayers.Projection(projection);
        lonLat.transform(proj, this.map.getProjectionObject());
        var point = new OpenLayers.Geometry.Point(lonLat.lon, lonLat.lat);
        return point;
    }

});

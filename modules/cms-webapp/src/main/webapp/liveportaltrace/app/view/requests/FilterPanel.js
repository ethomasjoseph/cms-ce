Ext.define( 'LPT.view.requests.FilterPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.requestsFilterPanel',

    title: 'Filter',
    split: true,
    collapsible: true,
    autoHeight: true,
    layout: {
        type: 'vbox',
        padding: 10,
        align: 'stretch'
    },
    border: true,
    bodyPadding: 10,

    defaults: {
        margins: '0 0 0 0'
    },

    items: [
        {
            xtype: 'fieldcontainer',
            layout: 'hbox',

            items: [
                {
                    xtype: 'textfield',
                    name: 'filterSiteText',
                    enableKeyEvents: true,
                    id: 'filterSiteText',
                    flex: 1
                },
                {
                    xtype: 'button',
                    icon: 'resources/images/find.png',
                    action: 'search',
                    margins: '0 0 0 5'
                }
            ]
        },
        {
            xtype: 'panel',
            title: 'Types',
            collapsible: false,
            defaultType: 'textfield',
            defaults: {anchor: '100%'},
            bodyPadding: 5,
            layout: 'anchor',
            items :[
                {
                    id: 'filterIncludePageRequestsCheckbox',
                    xtype: 'checkbox',
                    checked: true,
                    boxLabel: 'Page requests'
                },
                {
                    id: 'filterIncludeWindowRequestsCheckbox',
                    xtype: 'checkbox',
                    checked: true,
                    boxLabel: 'Window requests'
                },
                {
                    id: 'filterIncludeImageRequestsCheckbox',
                    xtype: 'checkbox',
                    checked: true,
                    boxLabel: 'Image requests'
                },
                {
                    id: 'filterIncludeAttachmentRequestsCheckbox',
                    xtype: 'checkbox',
                    checked: true,
                    boxLabel: 'Attachment requests'
                }
            ]
        },
        {
            id: 'filterSiteContainer',
            xtype: 'panel',
            autoHeight: true,
            title: 'Sites',
            bodyPadding: 5,
            layout:{
                type: 'anchor'

            },
            defaults: {anchor: '100%'}

        }
    ],

    initComponent: function()
    {
        this.callParent( arguments );
    },

    updateFacetStatistics: function(facetStatistics)
    {
        Ext.getCmp( "filterIncludePageRequestsCheckbox" ).boxLabelEl.update( "Page requests (" + facetStatistics.numberOfPageRequests + ")" );
        Ext.getCmp( "filterIncludeWindowRequestsCheckbox" ).boxLabelEl.update( "Window requests (" + facetStatistics.numberOfWindowRequests + ")" );
        Ext.getCmp( "filterIncludeImageRequestsCheckbox" ).boxLabelEl.update( "Image requests (" + facetStatistics.numberOfImageRequests + ")" );
        Ext.getCmp( "filterIncludeAttachmentRequestsCheckbox" ).boxLabelEl.update( "Attachment requests (" + facetStatistics.numberOfAttachmentRequests + ")" );

        this.updateFacetSiteStatistics(facetStatistics.siteStatsByKey);
    },

    updateFacetSiteStatistics: function( siteStatsByKey )
    {
        var filterSiteContainer = Ext.getCmp( 'filterSiteContainer' );

        for( var siteKey in siteStatsByKey )
        {
            var siteInfo = siteStatsByKey[siteKey];
            var itemId = "filterSite_" + siteKey;
            var siteFilter = filterSiteContainer.getComponent( itemId );
            var label = siteInfo.name + " (" + siteInfo.count + ")";

            if( siteFilter == null )
            {
                var checkBox = Ext.create( "Ext.form.field.Checkbox", {
                            itemId: itemId,
                            xtype: 'checkbox',
                            checked: true,
                            boxLabel: label,
                            action: 'filtersite'
                        } );

                filterSiteContainer.add( checkBox );
            }
            else
            {
                siteFilter.boxLabelEl.update( label );
            }

        }

        filterSiteContainer.doLayout();
    }



} );

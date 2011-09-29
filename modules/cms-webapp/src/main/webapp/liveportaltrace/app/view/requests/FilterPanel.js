Ext.define( 'LPT.view.requests.FilterPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.requestsFilterPanel',

    title: 'Filter',
    split: true,
    collapsible: true,

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
                    name: 'filter',
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
    }

} );

Ext.define( 'Common.TabPanel', {
    extend: 'Ext.tab.Panel',
    alias: 'widget.cmsTabPanel',
    requires: ['Common.TabCloseMenu'],
    defaults: { closable: true },
    plugins: ['tabCloseMenu'],

    initComponent: function()
    {
        this.callParent( arguments );
    },

    addTab: function( item, index, requestConfig )
    {
        var tabPanel = this;
        var tab = this.getTabById( item.id );
        // Create a new tab if it is not already created
        if ( !tab )
        {
            if (requestConfig)
            {
                tab = this.insert( index || this.items.length, item );
                this.setActiveTab( tab );
                var mask = new Ext.LoadMask( tab, {msg:"Please wait..."} );
                mask.show();
                var createTabFromResponse = requestConfig.createTabFromResponse;
                requestConfig.success = function successCallback(response)
                {
                    var tabContent = createTabFromResponse(response);
                    tab.add(tabContent);
                    mask.hide();
                    // There is a need to call doLayout manually, since it isn't called for background tabs
                    // after content was added
                    tab.on('activate', function(){this.doLayout();}, tab, {single: true});
                }
                Ext.Ajax.request( requestConfig );
            }
            else
            {
                tab = this.insert( index || this.items.length, item );
            }
            if ( tab.closable )
            {
                tab.on({
                    beforeclose: function( tab, options ) {
                        tabPanel.onBeforeCloseTab( tab, options )
                    }
                });
            }
        }

        this.setActiveTab( tab );
        return tab;
    },

    getTabById: function( id )
    {
        return this.getComponent(id);
    },

    onBeforeCloseTab: function( tab, options )
    {
        var tabToActivate = null;
        var activatePreviousTab = tab.isVisible();
        if ( activatePreviousTab )
        {
            var tabIndex = this.items.findIndex( 'id', tab.id );
            tabToActivate = this.items.items[ tabIndex - 1 ];
        }
        else
        {
            tabToActivate = this.getActiveTab();
        }

        this.setActiveTab( tabToActivate );
    },

    removeAllTabs: function()
    {
        var all = this.items.items;
        var last = all[this.getTabCount() -1 ];
        while ( this.getTabCount() > 1 )
        {
            this.remove( last );
            last = this.items.items[this.getTabCount() - 1];
        }
    },

    getTabCount: function()
    {
        return this.items.items.length;
    }

} );

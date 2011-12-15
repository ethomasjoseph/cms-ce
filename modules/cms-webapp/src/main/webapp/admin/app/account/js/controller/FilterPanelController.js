Ext.define( 'App.controller.FilterPanelController', {
    extend: 'Ext.app.Controller',

    stores: [
        'AccountStore',
        'UserstoreConfigStore'
    ],
    models: [
        'AccountModel'
    ],
    views: [],

    init: function()
    {
        this.control( {
                          'accountFilter': {
                              specialkey: this.filterHandleEnterKey,
                              render: this.onFilterPanelRender
                          },
                          'accountFilter button[action=search]': {
                              click: this.searchFilter
                          }
                      } );
        this.getStore( 'UserstoreConfigStore' ).on( 'load', this.initFilterPanelUserStoreOptions, this );
        this.getStore( 'AccountStore' ).on( 'load', this.updateFilterFacets, this );
    },

    onFilterPanelRender: function()
    {
        var filterTextField = Ext.getCmp( 'filter' );
        filterTextField.addListener( 'change', this.searchFilterKeyPress, this );

        this.getFilterUserStoreField().addListener( 'change', function( field, newValue, oldValue, eOpts )
        {
            this.getAccountFilter().updateTitle();
            this.searchFilter( 'userstore' );
        }, this );

        this.getFilterAccountTypeField().addListener( 'change', function( field, newValue, oldValue, eOpts )
        {
            this.getAccountFilter().updateTitle();
            this.searchFilter( 'type' );
        }, this );

        this.getFilterOrganizationField().addListener( 'change', function( field, newValue, oldValue, eOpts )
        {
            this.getAccountFilter().updateTitle();
            this.searchFilter( 'organization' );
        }, this );

        filterTextField.focus( false, 10 );
    },

    searchFilter: function( facetSelected )
    {
        this.setBrowseTabActive();

        var usersStore = this.getStore('AccountStore');
        var textField = this.getFilterTextField();
        var userStoreField = this.getFilterUserStoreField();
        var accountTypeField = this.getFilterAccountTypeField();
        var organizationsField = this.getFilterOrganizationField();

        var values = [];
        Ext.Object.each( organizationsField.getValue(), function( key, val )
        {
            values.push( val );
        } );
        organizationsField = values.join( ',' );

        values = [];
        Ext.Object.each( accountTypeField.getValue(), function( key, val )
        {
            values.push( val );
        } );
        accountTypeField = values.join( ',' );

        values = [];
        Ext.Object.each( userStoreField.getValue(), function( key, val )
        {
            values.push( val );
        } );
        userStoreField = values.join( ',' );

        if ( textField.getValue().length > 0 )
        {
            this.getAccountFilter().updateTitle();
        }

        this.facetSelected = facetSelected ? facetSelected : '';

        var filterQuery = {
            query: textField.getValue(),
            type: accountTypeField,
            userstores: userStoreField,
            organizations: organizationsField
        };

        // save the last filter query
        this.getAccountFilter().lastQuery = filterQuery;

        usersStore.clearFilter();
        usersStore.getProxy().extraParams = filterQuery;

        // move to page 1 when search filter updated
        var pagingToolbar = this.getUserGrid().down( 'pagingtoolbar' );
        // changing to first page triggers usersStore.load()
        pagingToolbar.moveFirst();
    },

    filterHandleEnterKey: function( field, event )
    {
        if ( event.getKey() == event.ENTER )
        {
            this.searchFilter();
        }
    },

    getAccountFilter: function()
    {
        return Ext.ComponentQuery.query( 'accountFilter' )[0];
    },

    getFilterUserStoreField: function()
    {
        return Ext.ComponentQuery.query( 'accountFilter checkboxgroup[itemId=userstoreOptions]' )[0];
    },

    getFilterAccountTypeField: function()
    {
        return Ext.ComponentQuery.query( 'accountFilter checkboxgroup[itemId=accountTypeOptions]' )[0];
    },

    getFilterOrganizationField: function()
    {
        return Ext.ComponentQuery.query( 'accountFilter checkboxgroup[itemId=organizationOptions]' )[0];
    },

    getFilterTextField: function()
    {
        return Ext.ComponentQuery.query( 'accountFilter textfield[name=filter]' )[0];
    },

    initFilterPanelUserStoreOptions: function( store )
    {
        var items = store.data.items;
        var userstores = [];

        for ( var i = 0; i < items.length; i++ )
        {
            var userstoreName = items[i].data.name;
            userstores.push( userstoreName );
        }
        var filterPanel = this.getAccountFilter();
        filterPanel.setUserStores( userstores );
    },

    updateFilterFacets: function( store )
    {
        var data = store.proxy.reader.jsonData;
        var filterPanel = this.getAccountFilter();

        filterPanel.showFacets( data.results.facets, this.facetSelected );
    },

    getCmsTabPanel: function()
    {
        return Ext.ComponentQuery.query( 'cmsTabPanel' )[0];
    },

    getUserGrid: function()
    {
        return Ext.ComponentQuery.query( 'accountGrid' )[0];
    },

    setBrowseTabActive: function()
    {
        var browseTab = this.getCmsTabPanel().getTabById( 'tab-browse' );
        this.getCmsTabPanel().setActiveTab( browseTab );
    },

    searchFilterKeyPress: function ()
    {
        if ( this.searchFilterTypingTimer != null )
        {
            window.clearTimeout( this.searchFilterTypingTimer );
            this.searchFilterTypingTimer = null;
        }
        var controller = this;
        this.searchFilterTypingTimer = window.setTimeout( function ()
                                                          {
                                                              controller.searchFilter();
                                                          }, 500 );
    }

} );

Ext.define( 'App.controller.GridPanelController', {
    extend: 'Ext.app.Controller',

    requires: ['App.util.AccountKeyMap'],
    stores: [
        'AccountStore'
    ],
    models: [
        'AccountModel'
    ],
    views: [
        'BrowseToolbar',
        'FilterPanel',
        'ShowPanel',
        'ContextMenu'
    ],

    init: function()
    {
        this.control(
            {
                'cmsTabPanel': {
                    afterrender: function( tabPanel, eOpts )
                    {
                        this.updateActionItems();
                    }
                },
                'accountGrid': {
                    selectionchange: function(selModel, selected, eOpts)
                    {
                        this.updateDetailsPanel(selModel, selected, eOpts);
                        this.updateActionItems();
                    },
                    itemcontextmenu: this.popupMenu,
                    itemdblclick: this.showAccountPreviewPanel
                },
                'viewport': {
                    afterrender: this.initAccount
                }
            }
        );
    },

    updateDetailsPanel: function(selModel, selected, eOpts)
    {
        var detailPanel = this.getAccountDetailPanel();
        var persistentGridSelectionPlugin = this.getPersistentGridSelectionPlugin();
        var persistentSelection = persistentGridSelectionPlugin.getSelection();
        var persistentSelectionCount = persistentGridSelectionPlugin.getSelectionCount();
        var userStore = this.getStore( 'AccountStore' );
        var pageSize = userStore.pageSize;
        var totalCount = userStore.totalCount;
        var selectionModel = this.getUserGrid().getSelectionModel();
        var selectionModelCount = selectionModel.getCount();
        var showAccountPreviewOnly = persistentSelectionCount === 1;

        if ( persistentSelectionCount === 0 )
        {
            detailPanel.showNoneSelection();
        }
        else if ( showAccountPreviewOnly )
        {
            // need raw to include fields like memberships, not defined in model
            var accountData = persistentSelection[0].raw;
            if ( accountData )
            {
                detailPanel.setCurrentAccount( accountData );
                detailPanel.showAccountPreview( accountData )
            }
        }
        else
        {
            var detailed = true;
            if ( persistentSelectionCount > 10 )
            {
                detailed = false;
            }
            var selectedUsers = [];
            Ext.Array.each( persistentSelection, function( user )
            {
                Ext.Array.include( selectedUsers, user.data );
            } );
            detailPanel.showMultipleSelection( selectedUsers, detailed );
        }

        detailPanel.updateTitle( persistentGridSelectionPlugin );
    },

    updateActionItems: function()
    {
        var components2d = [];
        components2d.push( Ext.ComponentQuery.query( '*[action=edit]' ) );
        components2d.push( Ext.ComponentQuery.query( '*[action=showDeleteWindow]' ) );
        components2d.push( Ext.ComponentQuery.query( '*[action=changePassword]' ) );
        components2d.push( Ext.ComponentQuery.query( '*[action=viewUser]' ) );

        var items = [];
        var selectionCount = this.getPersistentGridSelectionPlugin().getSelectionCount();
        var multipleSelection = selectionCount > 1;
        var disable = selectionCount === 0;

        for ( var i = 0; i < components2d.length; i++ )
        {
            items = components2d[i];
            for ( var j = 0; j < items.length; j++ )
            {
                items[j].setDisabled( disable );
                if ( multipleSelection && items[j].disableOnMultipleSelection )
                {
                    items[j].setDisabled( true );
                }
            }
        }
    },

    popupMenu: function( view, rec, node, index, e )
    {
        e.stopEvent();
        this.getUserContextMenu().showAt( e.getXY() );
        return false;
    },

    showEditUserForm: function( el, e )
    {
        var ctrl = this.getController( 'EditUserPanelController' );
        if ( ctrl )
        {
            var account = this.getAccountDetailPanel().getCurrentAccount();
            ctrl.showEditUserForm( account );
        }
    },

    showAccountPreviewPanel: function( el, e )
    {
        var ctrl = this.getController( 'BrowseToolbarController' );
        if ( ctrl )
        {
            ctrl.showAccountPreviewPanel( el, e );
        }
    },

    initAccount: function()
    {
        var me = this;
        var cmsTabPanel = this.getCmsTabPanel();
        var keyMap = new App.util.AccountKeyMap( {
                                                     newMegaMenu: function()
                                                     {
                                                         var activeTab = cmsTabPanel.getActiveTab();
                                                         if ( activeTab.getId() == "tab-browse" )
                                                         {
                                                             var menu = cmsTabPanel.down( "#newItemMenu" );
                                                             menu.showBy( cmsTabPanel.down( "#newAccountButton" ) );
                                                         }
                                                     },
                                                     openItem: function()
                                                     {
                                                         var activeTab = cmsTabPanel.getActiveTab();
                                                         if ( activeTab.getId() == "tab-browse" )
                                                         {
                                                             me.showAccountPreviewPanel();
                                                         }
                                                     },
                                                     editItem: function()
                                                     {
                                                         var activeTab = cmsTabPanel.getActiveTab();
                                                         if ( activeTab.getId() == "tab-browse" )
                                                         {
                                                             me.showEditUserForm();
                                                         }
                                                     },
                                                     saveItem: function()
                                                     {
                                                         var activeTab = cmsTabPanel.getActiveTab();
                                                         if ( activeTab.isXType( "groupWizardPanel" ) ||
                                                                 activeTab.isXType( "userWizardPanel" ) )
                                                         {
                                                             me.getController( "UserWizardController" ).saveNewUser();
                                                         }
                                                     },
                                                     prevStep: function()
                                                     {
                                                         var activeTab = cmsTabPanel.getActiveTab();
                                                         if ( activeTab.isXType( "groupWizardPanel" ) ||
                                                                 activeTab.isXType( "userWizardPanel" ) )
                                                         {
                                                             me.getController( "UserWizardController" ).wizardPrev();
                                                         }
                                                     },
                                                     nextStep: function()
                                                     {
                                                         var activeTab = cmsTabPanel.getActiveTab();
                                                         if ( activeTab.isXType( "groupWizardPanel" ) ||
                                                                 activeTab.isXType( "userWizardPanel" ) )
                                                         {
                                                             me.getController( "UserWizardController" ).wizardNext();
                                                         }
                                                     },
                                                     deleteItem: function()
                                                     {
                                                         var activeTab = cmsTabPanel.getActiveTab();
                                                         if ( activeTab.getId() == "tab-browse" )
                                                         {
                                                             me.getController( "BrowseToolbarController" ).showDeleteAccountWindow();
                                                         }
                                                     }
                                                 } );
    },

    getPersistentGridSelectionPlugin: function()
    {
        return this.getUserGrid().getPlugin( 'persistentGridSelection' );
    },

    getUserGrid: function()
    {
        return Ext.ComponentQuery.query( 'accountGrid' )[0];
    },

    getCmsTabPanel: function()
    {
        return Ext.ComponentQuery.query( 'cmsTabPanel' )[0];
    },

    getAccountDetailPanel: function()
    {
        return Ext.ComponentQuery.query( 'accountDetail' )[0];
    },

    getUserContextMenu: function()
    {
        var menu = Ext.ComponentQuery.query( 'accountContextMenu' )[0];
        if ( !menu )
        {
            menu = Ext.create( 'widget.accountContextMenu' );
        }
        return menu;
    }

} );

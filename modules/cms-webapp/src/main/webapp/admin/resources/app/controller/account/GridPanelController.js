Ext.define( 'Admin.controller.account.GridPanelController', {
    extend: 'Ext.app.Controller',

    requires: ['Admin.view.account.AccountKeyMap'],
    stores: [
        'Admin.store.account.AccountStore'
    ],
    models: [
        'Admin.model.account.AccountModel'
    ],
    views: [
        'Admin.view.account.BrowseToolbar',
        'Admin.view.account.FilterPanel',
        'Admin.view.account.ShowPanel',
        'Admin.view.account.ContextMenu'
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
                    selectionchange: function()
                    {
                        this.updateDetailsPanel();
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

    updateDetailsPanel: function()
    {
        var detailPanel = this.getAccountDetailPanel();
        var persistentGridSelectionPlugin = this.getPersistentGridSelectionPlugin();
        var persistentSelection = persistentGridSelectionPlugin.getSelection();
        var persistentSelectionCount = persistentGridSelectionPlugin.getSelectionCount();
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
        var actionItems2d = [];
        var editButtons = Ext.ComponentQuery.query( '*[action=edit]' );
        var changePasswordButtons = Ext.ComponentQuery.query( '*[action=changePassword]' );
        actionItems2d.push( editButtons );
        actionItems2d.push( changePasswordButtons );
        actionItems2d.push( Ext.ComponentQuery.query( '*[action=showDeleteWindow]' ) );
        actionItems2d.push( Ext.ComponentQuery.query( '*[action=viewUser]' ) );

        var actionItems = [];
        var selectionCount = this.getPersistentGridSelectionPlugin().getSelectionCount();
        var multipleSelection = selectionCount > 1;
        var disable = selectionCount === 0;

        for ( var i = 0; i < actionItems2d.length; i++ )
        {
            actionItems = actionItems2d[i];
            for ( var j = 0; j < actionItems.length; j++ )
            {
                actionItems[j].setDisabled( disable );
                if ( multipleSelection && actionItems[j].disableOnMultipleSelection )
                {
                    actionItems[j].setDisabled( true );
                }
            }
        }

        var selection = this.getPersistentGridSelectionPlugin().getSelection();
        if ( selectionCount == 1 )
        {
            for ( j = 0; j < editButtons.length; j++ )
            {
                editButtons[j].setDisabled( !selection[0].get( 'isEditable' ) );
            }
            for ( j = 0; j < changePasswordButtons.length; j++ )
            {
                changePasswordButtons[j].setDisabled( selection[0].get( 'type' ) !== 'user' );
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
        var ctrl = this.getController( 'Admin.controller.account.EditUserPanelController' );
        if ( ctrl )
        {
            var account = this.getAccountDetailPanel().getCurrentAccount();
            ctrl.showEditUserForm( account );
        }
    },

    showAccountPreviewPanel: function( el, e )
    {
        var ctrl = this.getController( 'Admin.controller.account.BrowseToolbarController' );
        if ( ctrl )
        {
            ctrl.showAccountPreviewPanel( el, e );
        }
    },

    initAccount: function()
    {
        var me = this;
        var cmsTabPanel = this.getCmsTabPanel();
        var keyMap = Ext.create( 'Admin.view.account.AccountKeyMap', {
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
                                                             me.getController( "Admin.controller.account.UserWizardController" ).saveNewUser();
                                                         }
                                                     },
                                                     prevStep: function()
                                                     {
                                                         var activeTab = cmsTabPanel.getActiveTab();
                                                         if ( activeTab.isXType( "groupWizardPanel" ) ||
                                                                 activeTab.isXType( "userWizardPanel" ) )
                                                         {
                                                             me.getController( "Admin.controller.account.UserWizardController" ).wizardPrev();
                                                         }
                                                     },
                                                     nextStep: function()
                                                     {
                                                         var activeTab = cmsTabPanel.getActiveTab();
                                                         if ( activeTab.isXType( "groupWizardPanel" ) ||
                                                                 activeTab.isXType( "userWizardPanel" ) )
                                                         {
                                                             me.getController( "Admin.controller.account.UserWizardController" ).wizardNext();
                                                         }
                                                     },
                                                     deleteItem: function(keyCode, event)
                                                     {
                                                         var activeTab = cmsTabPanel.getActiveTab();
                                                         if ( activeTab.getId() == "tab-browse" )
                                                         {
                                                             me.getController( "Admin.controller.account.BrowseToolbarController" ).showDeleteAccountWindow();
                                                             event.stopEvent();
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
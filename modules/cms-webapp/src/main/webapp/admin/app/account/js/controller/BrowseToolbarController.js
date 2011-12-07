Ext.define( 'App.controller.BrowseToolbarController', {
    extend: 'Ext.app.Controller',

    stores: [
        'GroupStore',
        'CallingCodeStore'
    ],
    models: [
        'GroupModel',
        'CallingCodeModel'
    ],
    views: [
        'EditUserPanel',
        'ChangePasswordWindow',
        'DeleteAccountWindow',
        'wizard.user.UserWizardPanel',
        'wizard.group.GroupWizardPanel',
        'UserPreviewWindow',
        'ExportAccountsWindow'
    ],

    init: function()
    {
        this.control( {
                          '*[action=newUser]': {
                              click: this.showEditUserForm
                          },
                          '*[action=newGroup]': {
                              click: this.createNewGroupTab
                          },
                          '*[action=showDeleteWindow]': {
                              click: this.showDeleteUserWindow
                          },
                          '*[action=edit]': {
                              click: this.showEditUserForm
                          },
                          '*[action=changePassword]': {
                              click: this.showChangePasswordWindow
                          },
                          '*[action=viewUser]': {
                              click: this.showUserPreviewWindow
                          },
                            '*[action=exportAccounts]': {
                                click: this.showExportAccountsWindow
                            }
                      } );
    },

    createNewGroupTab: function()
    {
        this.getCmsTabPanel().addTab( {
                                          title: 'New Group',
                                          iconCls: 'icon-new-group',
                                          xtype: 'groupWizardPanel'
                                      } );
    },

    showDeleteUserWindow: function()
    {
        var selected = this.getUserGrid().getSelectionModel().selected;
        this.getDeleteAccountWindow().doShow( selected );
    },

    showChangePasswordWindow: function()
    {
        var selected = this.getUserGrid().getSelectionModel().selected.get( 0 );
        this.getUserChangePasswordWindow().doShow( selected );
    },

    showExportAccountsWindow: function()
    {
        var grid = this.getUserGrid();
        var lastQuery = this.getAccountFilter().lastQuery;
        var selected = this.getPersistentGridSelectionPlugin().getSelection();
        var data = {
            selected: selected,
            searched: {
                count: grid.getStore().getTotalCount(),
                lastQuery: lastQuery
            }
        };
        this.getExportAccountsWindow().doShow( { data: data } );
    },

    showEditUserForm: function( el, e )
    {
        var ctrl = this.getController( 'EditUserPanelController' );
        if ( ctrl )
        {
            var previewWindow = Ext.ComponentQuery.query( 'userPreviewWindow' )[0];
            if (previewWindow)
            {
                previewWindow.close();
            }
            ctrl.showEditUserForm( el, e );
        }
    },

    showUserPreviewWindow: function( el, e )
    {
        var selected = this.getUserGrid().getSelectionModel().selected.get( 0 );
        var window = this.getUserPreviewWindow();
        window.doShow(selected.data);
    },

    getAccountFilter: function()
    {
        return Ext.ComponentQuery.query( 'accountFilter' )[0];
    },

    getPersistentGridSelectionPlugin: function()
    {
        return this.getUserGrid().getPlugin( 'persistentGridSelection' );
    },

    getCmsTabPanel: function()
    {
        return Ext.ComponentQuery.query( 'cmsTabPanel' )[0];
    },

    getUserGrid: function()
    {
        return Ext.ComponentQuery.query( 'accountGrid' )[0];
    },

    getUserDeleteWindow: function()
    {
        var win = Ext.ComponentQuery.query( 'userDeleteWindow' )[0];
        if ( !win )
        {
            win = Ext.create( 'widget.userDeleteWindow' );
        }
        return win;
    },

    getUserChangePasswordWindow: function()
    {
        var win = Ext.ComponentQuery.query( 'userChangePasswordWindow' )[0];
        if ( !win )
        {
            win = Ext.create( 'widget.userChangePasswordWindow' );
        }
        return win;
    },

    getExportAccountsWindow: function()
    {
        var win = Ext.ComponentQuery.query( 'exportAccountsWindow' )[0];
        if ( !win )
        {
            win = Ext.create( 'widget.exportAccountsWindow' );
        }
        return win;
    },

    getDeleteAccountWindow: function()
    {
        var win = Ext.ComponentQuery.query( 'deleteAccountWindow' )[0];
        if ( !win )
        {
            win = Ext.create( 'widget.deleteAccountWindow' );
        }
        return win;
    },

    getUserPreviewWindow: function()
    {
        var win = Ext.ComponentQuery.query( 'userPreviewWindow' )[0];
        if ( !win )
        {
            win = Ext.create( 'widget.userPreviewWindow' );
        }
        return win;
    }

} );

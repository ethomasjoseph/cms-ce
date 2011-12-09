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
        'UserPreviewPanel',
        'wizard.user.UserWizardPanel',
        'wizard.group.GroupWizardPanel',
        'UserPreviewPanel',
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
                              click: this.showUserPreviewPanel
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
            ctrl.showEditUserForm( el, e );
        }
    },

    showUserPreviewPanel: function( el, e )
    {
        var me = this;
        var selected = me.getUserGrid().getSelectionModel().selected.get( 0 );
        Ext.Ajax.request( {
                      url: 'data/user/userinfo',
                      method: 'GET',
                      params: {key: selected.get('key')},
                      success: function( response )
                      {
                          var jsonObj = Ext.JSON.decode( response.responseText );
                          me.getCmsTabPanel().addTab( {
                            title: jsonObj.displayName + ' (' + jsonObj.qualifiedName + ')',
                            xtype: 'userPreviewPanel',
                            data: jsonObj
                          } );

                      }
                  } );
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
    }

} );

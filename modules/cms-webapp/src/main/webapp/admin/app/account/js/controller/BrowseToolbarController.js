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
        'ChangePasswordWindow',
        'DeleteAccountWindow',
        'wizard.user.UserWizardPanel',
        'wizard.group.GroupWizardPanel',
        'preview.user.UserPreviewPanel',
        'preview.group.GroupPreviewPanel',
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
                              click: this.showDeleteAccountWindow
                          },
                          '*[action=edit]': {
                              click: this.showEditUserForm
                          },
                          '*[action=changePassword]': {
                              click: this.showChangePasswordWindow
                          },
                          '*[action=viewUser]': {
                              click: this.showAccountPreviewPanel
                          },
                          '*[action=exportAccounts]': {
                              click: this.showExportAccountsWindow
                          }
                      } );
    },

    createNewGroupTab: function()
    {
        var window = Ext.create( 'widget.selectUserStoreWindow', {caller: 'group'} );
        window.show();
    },

    showDeleteAccountWindow: function()
    {
        var persistentSelectionPlugin = this.getPersistentGridSelectionPlugin();
        if ( persistentSelectionPlugin.getSelectionCount() > 0 )
        {
            this.getDeleteAccountWindow().doShow( persistentSelectionPlugin.getSelection() );
        }
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
            var account;
            if ( "newUser" != el.action ) {
                account = this.getAccountDetailPanel().getCurrentAccount();
            }
            ctrl.showEditUserForm( account );
        }
    },

    showAccountPreviewPanel: function( el, e )
    {
        var me = this;
        var selected = me.getAccountDetailPanel().getCurrentAccount();
        if ( selected.type === 'user' )
        {
            Ext.Ajax.request(
                {
                    url: 'data/user/userinfo',
                    method: 'GET',
                    params: {key: selected.key },
                    success: function( response )
                    {
                        var jsonObj = Ext.JSON.decode( response.responseText );
                        me.getCmsTabPanel().addTab(
                            {
                                title: jsonObj.displayName + ' (' +
                                  jsonObj.qualifiedName + ')',
                                id: 'preview-user-' + jsonObj.userStore + '-' + jsonObj.name,
                                xtype: 'userPreviewPanel',
                                data: jsonObj,
                                user: selected
                          }
                      );
                    }
                }
            );
        }
        else
        {
            Ext.Ajax.request(
                {
                    url: 'data/account/groupinfo',
                    method: 'GET',
                    params: {key: selected.key },
                    success: function( response )
                    {
                        var jsonObj = Ext.JSON.decode( response.responseText );

                        me.getCmsTabPanel().addTab(
                            {
                                title: jsonObj.group.displayName,
                                id: 'preview-group-' + selected.key,
                                xtype: 'groupPreviewPanel',
                                data: jsonObj.group,
                                group: selected
                            }
                        );
                    }
                }
            );
        }
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

    getAccountDetailPanel: function()
    {
        return Ext.ComponentQuery.query( 'accountDetail' )[0];
    }

} );

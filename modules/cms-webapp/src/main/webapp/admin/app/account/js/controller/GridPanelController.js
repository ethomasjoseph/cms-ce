Ext.define( 'App.controller.GridPanelController', {
    extend: 'Ext.app.Controller',

    stores: [
        'UserStore'
    ],
    models: [
        'UserModel'
    ],
    views: [
        'BrowseToolbar',
        'FilterPanel',
        'ShowPanel',
        'ContextMenu'
    ],

    init: function()
    {
        this.control( {
            'cmsTabPanel': {
                  afterrender: function( tabPanel, eOpts ) {
                      this.updateActionItems();
                  }
            },
            'accountGrid': {
                  selectionchange: function() {
                      this.updateDetailsPanel();
                      this.updateActionItems();
                  },
                  beforeitemmousedown: this.cancelItemContextClickOnMultipleSelection,
                  itemcontextmenu: this.popupMenu,
                  itemdblclick: this.showEditUserForm
            }
        } );
    },

    updateDetailsPanel: function()
    {
        var detailPanel = this.getAccountDetailPanel();
        var persistentGridSelectionPlugin = this.getPersistentGridSelectionPlugin();
        var persistentSelection = persistentGridSelectionPlugin.getSelection();
        var persistentSelectionCount = persistentGridSelectionPlugin.getSelectionCount();
        var userStore = this.getStore('UserStore');
        var pageSize = userStore.pageSize;
        var totalCount = userStore.totalCount;

        var selectionModel = this.getUserGrid().getSelectionModel();
        var selectionModelCount = selectionModel.getCount();

        // Works because selection model count is 1 even if page has changed.
        var showUserPreviewOnly = ( selectionModelCount === 1 && userStore.currentPage > 1 ) || persistentSelectionCount == 1;

        if ( persistentSelectionCount === 0 )
        {
            detailPanel.showNoneSelection();
        }
        else if ( showUserPreviewOnly )
        {
            var user = selectionModelCount === 1 ? selectionModel.getSelection()[0] : persistentSelection[0];

            if ( user )
            {
                detailPanel.setCurrentUser( user.data );
            }

            detailPanel.showUserPreview( user.data )
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

        var items = [];
        var selectionCount = this.getPersistentGridSelectionPlugin().getSelectionCount();
        var multipleSelection = selectionCount > 1;
        var disable = selectionCount === 0;

        for ( var i = 0; i < components2d.length; i++ )
        {
            items = components2d[i];
            for (var j = 0; j < items.length; j++)
            {
                items[j].setDisabled(disable);
                if ( multipleSelection && items[j].disableOnMultipleSelection )
                {
                    items[j].setDisabled(true);
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
        if ( el.action == 'newUser' )
        {
            var tab = {
                id: Ext.id(null, 'new-user-'),
                title: 'New User',
                iconCls: 'icon-new-user',
                closable: true,
                autoScroll: true,
                layout: 'fit',
                items: [
                    {
                        xtype: 'userWizardPanel'
                    }
                ]
            };
            this.getCmsTabPanel().addTab( tab );
        }
        else
        {
            var accountDetail = this.getAccountDetailPanel();
            var tabPane = this.getCmsTabPanel();
            var currentUser = accountDetail.getCurrentUser();
            Ext.Ajax.request( {
                    url: 'data/user/userinfo',
                    method: 'GET',
                    params: {key: currentUser.key},
                    success: function( response )
                    {
                        var jsonObj = Ext.JSON.decode( response.responseText );
                        var tab = {
                            id: currentUser.userStore + '-' + currentUser.name,
                            title: currentUser.displayName + ' (' + currentUser.qualifiedName + ')',
                            iconCls: 'icon-edit-user',
                            closable: true,
                            autoScroll: true,
                            items: [
                                {
                                                  xtype: 'panel',
                                                  border: false
                                }
                            ]
                        };
                        tabPane.addTab( tab );
                    }
                }
            );

        }
    },

    cancelItemContextClickOnMultipleSelection: function( view, record, item, index, event, eOpts )
    {
        var persistentGridSelection = this.getPersistentGridSelectionPlugin();
        var rightClick = event.button === 2;
        var isSelected = persistentGridSelection.selected[record.internalId];

        var cancel = rightClick && isSelected && persistentGridSelection.getSelectionCount() > 1;
        if ( cancel )
        {
            return false;
        }

        return true;
    },


    getPersistentGridSelectionPlugin: function()
    {
        return this.getUserGrid().getPlugin('persistentGridSelection');
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
            menu = Ext.create('widget.accountContextMenu');
        return menu;
    }

} );

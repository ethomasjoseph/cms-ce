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
        this.control(
            {
                'cmsTabPanel': {
                    afterrender: function(tabPanel, eOpts) {
                        this.createBrowseTab(tabPanel, eOpts);
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
            }
        );
    },

    createBrowseTab: function( tabPanel, eOpts )
    {
        this.getCmsTabPanel().addTab( {
           id: 'tab-browse',
           title: 'Browse',
           closable: false,
           xtype: 'panel',
           layout: 'border',
           dockedItems: [
               {
                   xtype: 'browseToolbar',
                   dock: 'top'
               }],
           items: [
               {
                   region: 'west',
                   width: 225,
                   xtype: 'accountFilter'
               },
               {
                   region: 'center',
                   padding: '5 5 5 0',
                   xtype: 'accountShow'
               }
           ]
        } );

        var accountDetail = this.getAccountDetailPanel();
        accountDetail.updateTitle(this.getPersistentGridSelectionPlugin());
    },

    updateDetailsPanel: function()
    {
        var persistentGridSelection = this.getPersistentGridSelectionPlugin();
        var selection = persistentGridSelection.getSelection();
        var accountDetailPanel = this.getAccountDetailPanel();
        var selectionCount = persistentGridSelection.getSelectionCount();
        var userStore = this.getStore('UserStore');
        var pageSize = userStore.pageSize;
        var totalCount = userStore.totalCount;

        if ( selectionCount == 0 )
        {
            accountDetailPanel.showNoneSelection();
        }
        else
        {
            var user = selection[0];
            if ( user )
            {
                accountDetailPanel.setCurrentUser( user.data );
            }

            var detailed = true;
            if ( selectionCount > 10 )
            {
                detailed = false;
            }
            var selectedUsers = [];
            Ext.Array.each( selection, function( user )
            {
                Ext.Array.include( selectedUsers, user.data );
            } );
            accountDetailPanel.showMultipleSelection( selectedUsers, detailed );
        }

        accountDetailPanel.updateTitle(persistentGridSelection);
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
                iconCls: 'icon-user-add',
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
            Ext.Ajax.request(
                {
                    url: 'data/user/userinfo',
                    method: 'GET',
                    params: {key: currentUser.key},
                    success: function( response )
                    {
                        var jsonObj = Ext.JSON.decode( response.responseText );
                        var tab = {
                            id: currentUser.userStore + '-' + currentUser.name,
                            layout: 'border',
                            title: currentUser.displayName + ' (' + currentUser.qualifiedName + ')',
                            iconCls: 'icon-edit-user',
                            closable: true,
                            autoScroll: true,
                            items: [
                                {
                                    xtype: 'editUserPanel',
                                    region: 'center',
                                    userFields: jsonObj,
                                    currentUser: currentUser
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

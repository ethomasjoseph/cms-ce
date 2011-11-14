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
        'wizard.UserWizardPanel'
    ],

    init: function()
    {
        this.control(
            {
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
                }
            }
        );
    },

    createNewGroupTab: function()
    {
        this.getCmsTabPanel().addTab(
            {
                title: 'New Group',
                html: 'New Group Form',
                iconCls: 'icon-new-group'
            }
        );
    },

    showDeleteUserWindow: function()
    {
        this.getUserDeleteWindow().doShow( this.getPersistentGridSelectionPlugin() );
    },

    showChangePasswordWindow: function()
    {
        var selected =  this.getUserGrid().getSelectionModel().selected.get( 0 );
        this.getUserChangePasswordWindow().doShow( selected );
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
            } );
        }
    },



    getPersistentGridSelectionPlugin: function()
    {
        return this.getUserGrid().getPlugin('persistentGridSelection');
    },

    getCmsTabPanel: function()
    {
        return Ext.ComponentQuery.query( 'cmsTabPanel' )[0];
    },

    getUserGrid: function()
    {
        return Ext.ComponentQuery.query( 'accountGrid' )[0];
    },

    getAccountDetailPanel: function()
    {
        return Ext.ComponentQuery.query( 'accountDetail' )[0];
    },

    getUserDeleteWindow: function()
    {
        var win = Ext.ComponentQuery.query( 'userDeleteWindow' )[0];
        if ( !win )
            win = Ext.create('widget.userDeleteWindow');
        return win;
    },

    getUserChangePasswordWindow: function()
    {
        var win = Ext.ComponentQuery.query( 'userChangePasswordWindow' )[0];
        if ( !win )
            win = Ext.create('widget.userChangePasswordWindow');
        return win;
    }

} );

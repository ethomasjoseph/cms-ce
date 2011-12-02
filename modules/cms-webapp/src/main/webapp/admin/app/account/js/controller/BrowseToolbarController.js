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
        'wizard.UserWizardPanel',
        'UserPreviewWindow'
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
                          }
                      } );
    },

    createNewGroupTab: function()
    {
        this.getCmsTabPanel().addTab( {
                                          title: 'New Group',
                                          html: 'New Group Form',
                                          iconCls: 'icon-new-group'
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

    showEditUserForm: function( el, e )
    {
        var ctrl = this.getController( 'EditUserPanelController' );
        if ( ctrl )
        {
            ctrl.showEditUserForm( el, e );
        }
    },

    showUserPreviewWindow: function( el, e )
    {
        var selected = this.getUserGrid().getSelectionModel().selected.get( 0 );
        var window = Ext.create('widget.userPreviewWindow',{
            modelData: selected.data
        });
        window.show();
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

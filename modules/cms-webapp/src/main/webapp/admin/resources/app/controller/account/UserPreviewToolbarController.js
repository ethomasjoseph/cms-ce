Ext.define( 'Admin.controller.account.UserPreviewToolbarController', {
    extend: 'Ext.app.Controller',

    stores: [],
    models: [],
    views: [],

    init: function()
    {
        this.control( {
            'userPreviewPanel *[action=deleteAccountPreview]': {
                click: this.deleteAccount
            },
            'userPreviewPanel *[action=editUserPreview]': {
                click: this.editUser
            },
            'userPreviewPanel *[action=changePasswordPreview]': {
                click: this.changePassword
            }
        } );
    },

    deleteAccount: function( el, e )
    {
        var ctrl = this.getController( 'Admin.controller.account.BrowseToolbarController' );
        if ( ctrl ) {
            ctrl.showDeleteAccountWindow();
        }
    },

    changePassword: function( el, e )
    {
        var ctrl = this.getController( 'Admin.controller.account.BrowseToolbarController' );
        if ( ctrl ) {
            ctrl.showChangePasswordWindow();
        }
    },

    editUser: function( el, e )
    {
        var ctrl = this.getController( 'Admin.controller.account.EditUserPanelController' );
        if ( ctrl ) {
            var userPreview = this.getCmsTabPanel().getActiveTab();//el.up('userPreviewPanel');
            var index = this.getCmsTabPanel().items.indexOf( userPreview );
            // check if we are inside the account detail view
            // beneath the grid or in separate tab
            var inTab = index >= 0;
            var user;
            if ( inTab ) {
                user = userPreview.user ?
                        userPreview.user :
                        userPreview.down('userPreviewPanel');
            } else {
                index = undefined;
                user = this.getAccountDetailPanel().getCurrentAccount();
            }
            if ( user ) {
                userPreview.close();
                ctrl.showEditUserForm( user, index );
            }
        }
    },

    getCmsTabPanel: function()
    {
        return Ext.ComponentQuery.query( 'cmsTabPanel' )[0];
    },

    getAccountDetailPanel: function()
    {
        return Ext.ComponentQuery.query( 'accountDetail' )[0];
    }

} );


Ext.define( 'App.controller.UserPreviewToolbarController', {
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
        var ctrl = this.getController( 'BrowseToolbarController' );
        if ( ctrl ) {
            ctrl.showDeleteAccountWindow();
        }
    },

    changePassword: function( el, e )
    {
        var ctrl = this.getController( 'BrowseToolbarController' );
        if ( ctrl ) {
            ctrl.showChangePasswordWindow();
        }
    },

    editUser: function( el, e )
    {
        var ctrl = this.getController( 'EditUserPanelController' );
        if ( ctrl ) {
            var userPreview = el.up('userPreviewPanel');
            var index = this.getCmsTabPanel().items.indexOf( userPreview );
            // check if we are inside the account detail view
            // beneath the grid or in separate tab
            var inTab = index >= 0;
            var user;
            if ( inTab ) {
                user = userPreview.user;
            } else {
                index = undefined;
                user = this.getAccountDetailPanel().getCurrentAccount();
            }
            if ( user ) {
                userPreview.el.mask("Loading...");
                ctrl.showEditUserForm( user, function() {
                    userPreview.el.unmask();
                    if ( inTab ) {
                        userPreview.close();
                    }
                }, index );
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

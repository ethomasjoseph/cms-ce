Ext.define( 'Admin.controller.account.GroupPreviewToolbarController', {
    extend: 'Ext.app.Controller',

    stores: [],
    models: [],
    views: [],

    init: function()
    {
        this.control( {
            'groupPreviewPanel *[action=deleteGroupPreview]': {
                click: this.deleteGroup
            },
            'groupPreviewPanel *[action=editGroupPreview]': {
                click: this.editGroup
            }
        } );
    },

    deleteGroup: function( el, e )
    {
        var ctrl = this.getController( 'Admin.controller.account.BrowseToolbarController' );
        if ( ctrl ) {
            ctrl.showDeleteAccountWindow();
        }
    },

    editGroup: function( el, e )
    {
        var ctrl = this.getController( 'Admin.controller.account.EditUserPanelController' );
        if ( ctrl ) {
            var groupPreview = this.getCmsTabPanel().getActiveTab();//el.up('groupPreviewPanel');
            var index = this.getCmsTabPanel().items.indexOf( groupPreview );
            // check if we are inside the account detail view
            // beneath the grid or in separate tab
            var inTab = index >= 0;
            var group;
            if ( inTab ) {
                group = groupPreview.group ?
                        groupPreview.group :
                        groupPreview.down('groupPreviewPanel').group;
            } else {
                index = undefined;
                group = this.getAccountDetailPanel().getCurrentAccount();
            }
            if ( group ) {
                groupPreview.close();
                ctrl.showEditUserForm( group, index );
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


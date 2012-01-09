Ext.define( 'App.controller.GroupPreviewToolbarController', {
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
        var ctrl = this.getController( 'BrowseToolbarController' );
        if ( ctrl ) {
            ctrl.showDeleteAccountWindow();
        }
    },

    editGroup: function( el, e )
    {
        var ctrl = this.getController( 'EditUserPanelController' );
        if ( ctrl ) {
            var groupPreview = el.up('groupPreviewPanel');
            var index = this.getCmsTabPanel().items.indexOf( groupPreview );
            // check if we are inside the account detail view
            // beneath the grid or in separate tab
            var inTab = index >= 0;
            var group;
            if ( inTab ) {
                group = groupPreview.group;
            } else {
                index = undefined;
                group = this.getAccountDetailPanel().getCurrentAccount();
            }
            if ( group ) {
                groupPreview.el.mask("Loading...");
                ctrl.showEditUserForm( group, function() {
                    groupPreview.el.unmask();
                    if ( inTab ) {
                        groupPreview.close();
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


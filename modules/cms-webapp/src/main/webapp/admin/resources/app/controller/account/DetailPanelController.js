Ext.define( 'Cms.controller.account.DetailPanelController', {
    extend: 'Ext.app.Controller',

    stores: [],
    models: [],
    views: [],

    init: function()
    {
        this.control(
            {
                '*[action=deselectItem]': {
                    click: this.deselectItem
                },
                'accountDetail': {
                    afterrender: this.initDetailToolbar
                }
            }
        );
    },

    initDetailToolbar: function()
    {
        var accountDetail = this.getAccountDetailPanel();
        accountDetail.updateTitle( this.getPersistentGridSelectionPlugin() );
        accountDetail.showNoneSelection();
    },

    deselectItem: function( button )
    {
        var selModel = this.getUserGrid().getSelectionModel();
        var userInfoPanel = button.up( 'userDetailButton' );
        if ( userInfoPanel == null )
        {
            userInfoPanel = button.up( 'userShortDetailButton' );
        }

        selModel.deselect( userInfoPanel.getUser() );
    },

    getUserGrid: function()
    {
        return Ext.ComponentQuery.query( 'accountGrid' )[0];
    },

    getAccountDetailPanel: function()
    {
        return Ext.ComponentQuery.query( 'accountDetail' )[0];
    },

    getPersistentGridSelectionPlugin: function()
    {
        return this.getUserGrid().getPlugin( 'persistentGridSelection' );
    }

} );

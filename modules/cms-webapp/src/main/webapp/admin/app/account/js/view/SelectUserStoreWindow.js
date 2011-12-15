Ext.define( 'App.view.SelectUserStoreWindow', {
    extend: 'Common.view.BaseDialogWindow',
    alias: 'widget.selectUserStoreWindow',

    requires: ['App.view.wizard.user.UserStoreListPanel'],

    dialogTitle: 'Select user store',
    dialogInfoTpl: undefined,

    items: [
        {
            xtype: 'userStoreListPanel',
            height: 400
        }
    ],

    initComponent: function()
    {
        this.callParent( arguments );
    }


});
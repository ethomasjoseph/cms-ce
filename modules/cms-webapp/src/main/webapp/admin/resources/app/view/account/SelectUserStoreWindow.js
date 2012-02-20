Ext.define( 'Admin.view.account.SelectUserStoreWindow', {
    extend: 'Admin.view.BaseDialogWindow',
    alias: 'widget.selectUserStoreWindow',

    requires: ['Admin.view.account.wizard.user.UserStoreListPanel'],

    dialogTitle: 'Select user store',
    dialogInfoTpl: undefined,

    /* Caller field defines is this window used for group wizard or user wizard*/
    caller: undefined,
    items: [
        {
            xtype: 'userStoreListPanel'
        }
    ],

    initComponent: function()
    {
        // TODO: Why is caller and items set twice?
        this.items = [
        {
            xtype: 'userStoreListPanel',
            caller: this.caller
        }];
        this.callParent( arguments );
    }

});
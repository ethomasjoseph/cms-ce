Ext.define( 'App.view.SelectUserStoreWindow', {
    extend: 'Common.view.BaseDialogWindow',
    alias: 'widget.selectUserStoreWindow',

    requires: ['App.view.wizard.user.UserStoreListPanel'],

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
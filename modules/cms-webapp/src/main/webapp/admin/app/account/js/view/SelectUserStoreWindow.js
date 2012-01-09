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
            xtype: 'userStoreListPanel',
            height: 400
        }
    ],

    initComponent: function()
    {
        this.items = [
        {
            xtype: 'userStoreListPanel',
            caller: this.caller,
            height: 400
        }];
        this.callParent( arguments );
    }


});
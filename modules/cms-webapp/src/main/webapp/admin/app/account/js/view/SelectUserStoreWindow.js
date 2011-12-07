Ext.define( 'App.view.SelectUserStoreWindow', {
    extend: 'Common.view.BaseDialogWindow',
    alias: 'widget.selectUserStoreWindow',

    requires: ['App.view.wizard.user.UserStoreListPanel'],

    dialogTitle: 'Select user store',
    dialogInfoTpl: undefined,
    layout: 'fit',
    modal: 'true',
    items: [
        {
            xtype: 'userStoreListPanel'
        }
    ],

    initComponent: function()
    {
        this.callParent( arguments );
    }


});
Ext.define( 'App.view.UserPreviewWindow', {
    extend: 'Common.view.BaseDialogWindow',
    alias: 'widget.userPreviewWindow',

    requires: ['App.view.UserPreviewToolbar'],

    dialogTitle: 'User Preview',

    autoWidth: true,

    width: undefined,

    dialogInfoTpl: undefined,

    initComponent: function()
    {
        this.items = [
            {
                tbar: {
                    xtype: 'userPreviewToolbar'
                },
                itemId: 'userPreview',
                tpl: new Ext.XTemplate( Templates.account.userPreview )
        }];
        this.callParent( arguments );
    },

    doShow: function(model)
    {
        this.down('#userPreview').update(model);
        this.show();
    }

} );
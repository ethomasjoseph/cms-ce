Ext.define( 'App.view.UserPreviewWindow', {
    extend: 'Common.view.BaseDialogWindow',
    alias: 'widget.userPreviewWindow',

    dialogTitle: 'User Preview',

    autoWidth: true,

    width: undefined,

    dialogInfoTpl: undefined,

    initComponent: function()
    {
        this.items = [{
            data: this.modelData,
            tpl: new Ext.XTemplate( Templates.account.userPreview )
        }];
        this.callParent( arguments );
    }

} );
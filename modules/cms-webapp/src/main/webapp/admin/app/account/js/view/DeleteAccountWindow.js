Ext.define( 'App.view.DeleteAccountWindow', {
    extend: 'Common.view.BaseDialogWindow',
    alias: 'widget.deleteAccountWindow',

    dialogTitle: 'Delete Account(s)',

    items: [
        {
            margin: '10px 0 10px 105px',
            xtype: 'container',
            defaults: {
                xtype: 'button',
                scale: 'medium',
                margin: '0 10 0 0'
            },
            items: [
                {
                    text: 'Cancel',
                    iconCls: 'icon-cancel',
                    action: 'close',
                    handler: function()
                    {
                        this.up( 'window' ).close();
                    }
                },
                {
                    text: 'Delete',
                    iconCls: 'icon-delete-user-24',
                    itemId: 'deleteAccountButton',
                    action: 'deleteUser'
                }
            ]
        }
    ],

    initComponent: function()
    {

        this.callParent( arguments );
    },

    doShow: function( model )
    {
        if ( model.length == 1 )
        {
            this.callParent( [model.get( 0 )] );
        }
        else
        {
            this.setDialogInfoTpl( Templates.account.deleteManyUsers );
            this.callParent( [
                                 {data: {selectionLength: model.length}}
                             ] );
        }
    }
} )
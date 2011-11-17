Ext.define( 'Common.view.BaseDialogWindow', {
    extend: 'Ext.window.Window',
    alias: 'widget.baseDialogWindow',

    border: false,
    padding: 1,

    draggable: false,
    closable: false,
    width: 500,
    modal: true,
    modelData: undefined,
    autoHeight: true,
    cls: 'cms-dialog-window',
    closeAction: 'hide',
    bodyPadding: 10,
    bodyStyle: 'background: #fff;',

    dialogTitle: 'Base dialog',
    dialogHeaderCfg: {
        itemId: 'dialogHeader',
        xtype: 'container',
        cls: 'dialog-header',
        styleHtmlContent: true,
        tpl: new Ext.XTemplate('<h3>{title}</h3>')
    },
    dialogInfoCfg: {
        itemId: 'dialogInfo',
        cls: 'dialog-info',
        xtype: 'container',
        border: false,
        height: 80,
        styleHtmlContent: true,
        tpl: new Ext.XTemplate(Templates.common.userInfo)
    },

    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'right',
            items: [
                {
                    scale: 'medium',
                    iconAlign: 'top',
                    text: 'Close',
                    action: 'close',
                    iconCls: 'icon-close'
                }
            ]
        }
    ],

    listeners: {
        show: function( cmp ) {
            var header = this.down( '#dialogHeader' );
            if ( header ) {
                header.doLayout();
            }
            var info = this.down( '#dialogInfo' );
            if ( info ) {
                info.doLayout();
            }
            var form = cmp.down( 'form' );
            if ( form ){
                form.doLayout();
            }
        }
    },

    initComponent: function()
    {
        var me = this;
        Ext.Array.insert( this.items, 0, [
            this.dialogHeaderCfg,
            this.dialogInfoCfg
        ]);

        this.callParent( arguments );

        var header = this.down( '#dialogHeader' );
        if ( header ) {
            header.update( {
                title: me.dialogTitle
            } );
        }
    },

    doShow: function( model ) {
        if ( model ) {
            this.modelData = model.data;
            var info = this.down( '#dialogInfo' );
            if ( info ) {
                info.update( this.modelData );
            }
        }
        var form = this.down( 'form' ).getForm();
        if ( form ) {
            form.reset();
        }
        this.show();
    }

} );


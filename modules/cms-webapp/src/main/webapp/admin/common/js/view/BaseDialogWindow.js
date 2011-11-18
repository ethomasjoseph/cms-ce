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
    dialogInfoTpl: Templates.common.userInfo,

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
                    iconCls: 'icon-close',
                    listeners: {
                        click: function( btn, evt ) {
                            btn.up( 'baseDialogWindow' ).close();
                        }
                    }
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
                var firstField = form.down( 'field' );
                if ( firstField ) {
                    firstField.focus();
                }
            }
        }
    },

    initComponent: function()
    {
        var me = this;
        Ext.Array.insert( this.items, 0, [
            {
                itemId: 'dialogHeader',
                xtype: 'container',
                cls: 'dialog-header',
                styleHtmlContent: true,
                html: '<h3>' + me.dialogTitle + '</h3>'
            },
            {
                itemId: 'dialogInfo',
                cls: 'dialog-info',
                xtype: 'container',
                border: false,
                height: 80,
                styleHtmlContent: true,
                tpl: new Ext.XTemplate( me.dialogInfoTpl )
            }
        ]);

        this.callParent( arguments );

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

Ext.define( 'Common.view.NotifyUserWindow', {
    extend: 'Ext.window.Window',
    alias: 'widget.notifyUserWindow',

    border: false,
    padding: 1,

    draggable:false,
    closable:false,
    width: 500,
    modal: true,
    modelData: undefined,
    autoHeight: true,
    cls: 'cms-notify-user-window',
    closeAction: 'hide',
    bodyPadding: 10,
    bodyStyle: 'background: #fff;',

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
            cmp.down( '#userInfo' ).doLayout();
            cmp.down( 'form' ).doLayout();
        }
    },

    initComponent: function()
    {
        this.items = [
            {
                xtype: 'container',
                cls: 'notify-header',
                styleHtmlContent: true,
                html: '<h3>Notify user</h3>'
            },
            {
                itemId: 'userInfo',
                cls: 'notify-user',
                xtype: 'container',
                border: false,
                height: 80,
                styleHtmlContent: true,
                tpl: new Ext.XTemplate(Templates.common.userInfo)
            },
            {
                xtype: 'form',
                method: 'POST',
                autoHeight: true,
                border: false,
                url: 'data/user/notify',
                bodyPadding: '5px 0 0',
                items: [
                    {
                        xtype: 'fieldset',
                        margin: 0,
                        title: 'Message',
                        defaults: {
                            xtype: 'textfield',
                            allowBlank: false,
                            validateOnChange: true
                        },
                        items: [
                            {
                                fieldLabel: 'To',
                                name: 'to',
                                anchor: '100%',
                                allowBlank: false
                            },{
                                fieldLabel: 'Subject',
                                anchor: '100%',
                                name: 'subject'
                            }, {
                                fieldLabel: 'Message',
                                xtype: 'textarea',
                                anchor: '100%',
                                rows: 3,
                                allowBlank: false
                            },
                            {
                                margin: '0 0 10px 105px',
                                formBind: true,
                                xtype: 'button',
                                scale: 'medium',
                                text: 'Send',
                                iconCls: 'icon-btn-tick-24',
                                action: 'send'
                            }
                        ]
                    }
                ]
            }
        ];

        this.callParent( arguments );
    },

    doShow: function( model ) {
        if ( model ) {
            this.modelData = model.data;
            this.down( '#userInfo' ).update( this.modelData );
        }
        this.down( 'form' ).getForm().reset();
        this.show();
    }

} );

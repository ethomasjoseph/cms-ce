Ext.define( 'App.view.NotifyUserWindow', {
    extend: 'Ext.window.Window',
    alias: 'widget.notifyUserWindow',

    title: 'Notify User',
    width: 350,
    modal: true,
    autoHeight: true,
    modelData: undefined,

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
                    iconCls: 'icon-delete'
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
                itemId: 'userInfo',
                bodyPadding: 10,
                styleHtmlContent: true,
                tpl: new Ext.XTemplate(Templates.common.userInfo),
                listeners: {
                    'render': function() {
                        this.update(this.up().modelData);
                    }
                }
            },
            {
                xtype: 'form',
                method: 'POST',
                autoHeight: true,
                border: false,
                url: 'data/user/notify',
                bodyPadding: '10px 0 0',
                items: [
                    {
                        xtype: 'fieldset',
                        title: 'Message',
                        defaults: {
                            xtype: 'textfield',
                            allowBlank: false
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
                                style: 'margin-left: 105px;',
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

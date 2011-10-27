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

    initComponent: function()
    {
        this.items = [
            {
                itemId: 'userInfo',
                margins: '0 0 10px',
                height: 60,
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
                url: 'data/user/notify',
                items: [
                    {
                        xtype: 'fieldset',
                        title: 'Message',
                        defaults: {
                            xtype: 'textfield',
                            anchor: '100%',
                            allowBlank: false
                        },
                        items: [
                            {
                                fieldLabel: 'To',
                                name: 'to',
                                allowBlank: false
                            },{
                                fieldLabel: 'Subject',
                                name: 'subject'
                            }, {
                                fieldLabel: 'Message',
                                xtype: 'textarea',
                                rows: 3,
                                allowBlank: false
                            }
                        ]
                    },
                    {
                        style: 'margin-left: 120px;',
                        xtype: 'button',
                        scale: 'medium',
                        text: 'Send',
                        iconCls: 'icon-btn-tick-24',
                        action: 'send'
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

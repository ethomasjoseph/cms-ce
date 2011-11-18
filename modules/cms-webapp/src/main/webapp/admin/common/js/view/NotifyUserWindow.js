Ext.define( 'Common.view.NotifyUserWindow', {
    extend: 'Common.view.BaseDialogWindow',
    alias: 'widget.notifyUserWindow',

    dialogTitle: 'Notify User',
    items: [
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
        ]

} );

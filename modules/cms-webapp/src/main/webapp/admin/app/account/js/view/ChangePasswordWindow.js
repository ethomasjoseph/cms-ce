Ext.define( 'App.view.ChangePasswordWindow', {
    extend: 'Common.view.BaseDialogWindow',
    alias: 'widget.userChangePasswordWindow',

    dialogTitle: 'Change Password',

    initComponent: function()
    {
        this.items = [
            {
                xtype: 'form',
                id: 'userChangePasswordForm',
                method: 'POST',
                url: 'data/user/changepassword',
                bodyPadding: '10 0',
                bodyCls: 'cms-no-border',
                layout: 'anchor',
                defaults: {
                    xtype: 'textfield',
                    anchor: '100%',
                    inputType: 'password',
                    allowBlank: false,
                    listeners: {
                        change: this.doChange,
                        scope: this
                    }
                },
                items: [{
                    fieldLabel: 'New Password',
                    name: 'cpw_password',
                    itemId: 'cpw_password',
                    allowBlank: false
                },{
                    fieldLabel: 'Confirm Password',
                    name: 'cpw_password2',
                    itemId: 'cpw_password2',
                    submitValue: false,
                    allowBlank: false
                },{
                    margin: '0 0 10px 105px',
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
                            handler: function() {
                                this.up('window').close();
                            }
                        },
                        {
                            text: 'Change Password',
                            iconCls: 'icon-btn-tick-24',
                            itemId: 'changePasswordButton',
                            disabled: true,
                            handler: function() {
                                var form = Ext.getCmp( 'userChangePasswordForm' ).getForm();
                                if ( form.isValid() )
                                {
                                    form.submit();
                                }
                            }
                        }
                    ]
                }]
            }
        ];

        this.callParent( arguments );
    },

    doChange: function( e )
    {
        var password1 = Ext.ComponentQuery.query('#cpw_password')[0];
        var password2 = Ext.ComponentQuery.query('#cpw_password2')[0];
        var changePasswordButton = Ext.ComponentQuery.query('#changePasswordButton')[0];
        var disable = password1.getValue() !== password2.getValue();

        changePasswordButton.setDisabled(disable);
    }

} );

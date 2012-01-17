Ext.define( 'App.view.ChangePasswordWindow', {
    extend: 'Common.view.BaseDialogWindow',
    alias: 'widget.userChangePasswordWindow',

    requires: ['App.view.PasswordMeter'],
    dialogTitle: 'Change Password',

    afterRender: function()
    {
        var password1 = Ext.ComponentQuery.query( '#newPassword' )[0];
        password1.getField().on('change', function() {
            this.enableDisableChangePasswordButton();
        }, this);

        this.enableDisableChangePasswordButton();

        password1.getField().focus(false, 500);

        this.callParent( arguments );
    },

    initComponent: function()
    {
        var me = this;

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
                    allowBlank: false,
                    listeners: {
                        change: this.enableDisableChangePasswordButton,
                        scope: this
                    }
                },
                items: [
                    {
                        fieldLabel: 'New Password',
                        xtype: 'passwordMeter',
                        name: 'newPassword',
                        itemId: 'newPassword',
                        cls: 'cms-glowing-item',
                        passwordInputName: 'newPassword',
                        anchor: '100%',
                        width: 260,
                        allowBlank: false
                    },
                    {
                        fieldLabel: 'Confirm Password',
                        xtype: 'textfield',
                        inputType: 'password',
                        name: 'newPassword2',
                        itemId: 'newPassword2',
                        submitValue: false,
                        width: 365,
                        allowBlank: false
                    },
                    {
                        margin: '0 0 10px 105px',
                        xtype: 'container',
                        defaults: {
                            xtype: 'button',
                            scale: 'medium',
                            margin: '0 10 0 0'
                        },
                        items: [
                            {
                                text: 'Change Password',
                                iconCls: 'icon-btn-tick-24',
                                itemId: 'changePasswordButton',
                                disabled: true,
                                handler: function()
                                {
                                    var parentApp = parent.mainApp;
                                    var form = Ext.getCmp( 'userChangePasswordForm' ).getForm();
                                    var submitOptions = {
                                        params: {
                                            userKey: me.modelData.key
                                        },
                                        success: function( form, action ) {
                                            me.close();
                                            if ( parentApp )
                                            {
                                                parentApp.fireEvent( 'notifier.show', "Password was changed", "Far far away, behind the word mountains, far from the countries Vokalia and Consonantia, there live the blind texts.");
                                            }
                                        },
                                        failure: function( form, action ) {
                                            Ext.Msg.alert('Failed', action.result.msg);
                                        }
                                    };

                                    if ( form.isValid() )
                                    {
                                        form.submit(submitOptions);
                                    }
                                }
                            }
                        ]
                    }
                ]
            }
        ];

        this.callParent( arguments );
    },

    enableDisableChangePasswordButton: function()
    {
        var changePasswordButton = Ext.ComponentQuery.query( '#changePasswordButton' )[0];
        var password1 = Ext.ComponentQuery.query( '#newPassword' )[0];
        var password2 = Ext.ComponentQuery.query( '#newPassword2' )[0];
        var password1Value = password1.getValue();
        var password2Value = password2.getValue();
        var fieldsHasEqualValues = password1Value === password2Value;

        var enable = (password1Value !== '' && password2Value !== '') && fieldsHasEqualValues;

        changePasswordButton.setDisabled( !enable );
    }

} );

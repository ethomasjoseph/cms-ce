Ext.define( 'Cms.view.account.ChangePasswordWindow', {
    extend: 'Cms.view.BaseDialogWindow',
    alias: 'widget.userChangePasswordWindow',

    requires: ['Cms.view.account.PasswordMeter'],
    dialogTitle: 'Change Password',

    afterRender: function()
    {
        this.getPassword1().on( 'change', this.onPasswordChange, this );
        this.getPassword2().on( 'change', this.onPasswordChange, this );
        this.callParent( arguments );
    },

    afterLayout: function ()
    {
        // Need to reset the gui each time the window is displayed
        this.getPassword1().setValue('');
        this.getPassword2().setValue('');
        this.down( '#validationLabel' ).update({text:''});
        this.enableDisableChangePasswordButton();
        this.getPassword1().focus( false, 500 );
    },

    onPasswordChange: function()
    {
        this.enableDisableChangePasswordButton();
        this.checkForPasswordMatch();
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
                    allowBlank: false
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
                        width: 195,
                        allowBlank: false
                    },
                    {
                        xtype: 'container',
                        layout: {
                            type: 'hbox'
                        },
                        items: [
                            {
                                fieldLabel: 'Confirm Password',
                                xtype: 'textfield',
                                inputType: 'password',
                                name: 'newPassword2',
                                itemId: 'newPassword2',
                                submitValue: false,
                                width: 300,
                                allowBlank: false
                            },
                            {
                                itemId: 'validationLabel',
                                tpl: '<div class="validationError">{text}</div> ',
                                data: {text: ''},
                                width: 120,
                                cls: 'cms-validation-label',
                                margin: '0 0 0 5'
                            }
                        ]
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

    testPasswordsAreEmpty: function ( password1value, password2value )
    {
        return password1value.length === 0 || password2value.length === 0;
    },

    testPasswordsAreEqual: function( password1value, password2value )
    {
        var areEqual = password1value === password2value;
        return !this.testPasswordsAreEmpty( password1value, password2value ) && areEqual;
    },

    checkForPasswordMatch: function()
    {
        var password1value = this.getPassword1().getValue();
        var password2value = this.getPassword2().getValue();
        var validationLabel = this.down( '#validationLabel' );

        if ( this.testPasswordsAreEmpty( password1value, password2value ) )
        {
            return;
        }
        if ( !this.testPasswordsAreEqual( password1value, password2value ) )
        {
            validationLabel.update( {text:'Passwords don\'t match'} );
        }
        else
        {
            validationLabel.update( {text:''} );
        }
    },

    enableDisableChangePasswordButton: function()
    {
        var changePasswordButton = Ext.ComponentQuery.query( '#changePasswordButton' )[0];
        var password1value = this.getPassword1().getValue();
        var password2value = this.getPassword2().getValue();
        var enable = this.testPasswordsAreEqual( password1value, password2value );
        changePasswordButton.setDisabled( !enable );
    },

    getPassword1: function()
    {
        return Ext.ComponentQuery.query( '#newPassword' )[0].down('textfield');
    },

    getPassword2: function()
    {
        return Ext.ComponentQuery.query( '#newPassword2' )[0];
    }

} );

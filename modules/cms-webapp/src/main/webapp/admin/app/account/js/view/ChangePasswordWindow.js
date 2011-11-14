Ext.define( 'App.view.ChangePasswordWindow', {
    extend: 'Ext.window.Window',
    alias: 'widget.userChangePasswordWindow',

    title: 'Change Password',
    width: 400,
    plain: true,
    modal: true,

    initComponent: function()
    {
        this.items = [
            {
                id: 'userChangePasswordUserInfo',
                bodyPadding: 10,
                border: false,
                tpl: new Ext.XTemplate(Templates.common.userInfo),
                listeners: {
                    'render': function() {
                        this.update(this.up().modelData);
                    }
                }
            },
            {
                xtype: 'form',
                id: 'userChangePasswordForm',
                method: 'POST',
                url: 'data/user/changepassword',
                bodyPadding: '0 10 10 10',
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
                }]
            }
        ];

        this.buttons = [
            {
                text: 'Cancel',
                handler: function() {
                    this.up('window').close();
                }
            },
            {
                text: 'Change Password',
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
        ];

        this.listeners = {
            afterrender: function() {
                Ext.ComponentQuery.query('#cpw_password')[0].focus();
            }
        },

        this.callParent( arguments );
    },

    doShow: function( model )
    {
        this.modelData = model.data;
        this.show();
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

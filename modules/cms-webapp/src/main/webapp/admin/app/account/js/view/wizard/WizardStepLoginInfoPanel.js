Ext.define( 'App.view.wizard.WizardStepLoginInfoPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.wizardStepLoginInfoPanel',

    requires: [
        'App.view.UserFormField'
    ],

    defaults: {
        padding: '10px 15px'
    },

    items: [
        {
            xtype: 'fieldset',
            title: 'Names',
            defaults: {
                xtype: 'textfield',
                allowBlank: false
            },
            items: [
                {
                    fieldLabel: 'Username',
                    name: 'username',
                    emptyText: 'Name',
                    value: 'Suggested name'
                },
                {
                    fieldLabel: 'E-mail',
                    name: 'email',
                    vtype: 'email',
                    emptyText: 'Email'
                }
            ]
        },
        {
            xtype: 'fieldset',
            title: 'Security',
            defaults: {
                xtype: 'textfield',
                allowBlank: false
            },
            items: [
                {
                    inputType: 'password',
                    fieldLabel: 'Password',
                    name: 'password',
                    emptyText: 'Password'
                }
            ]
        },
        {
            xtype: 'fieldset',
            title: 'Location',
            items: [
                {
                    xtype: 'userFormField',
                    type: 'combo',
                    queryMode: 'local',
                    minChars: 1,
                    emptyText: 'Please select',
                    fieldStore: Ext.data.StoreManager.lookup( 'CountryStore' ),
                    valueField: 'code',
                    displayField: 'englishName'
                }
            ]
        }
    ],

    initComponent: function()
    {
        this.callParent( arguments );
    }

} );

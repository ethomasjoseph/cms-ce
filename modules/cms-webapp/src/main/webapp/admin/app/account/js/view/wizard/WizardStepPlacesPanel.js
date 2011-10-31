Ext.define( 'App.view.wizard.WizardStepPlacesPanel', {
    extend: 'Ext.form.Panel',
    alias : 'widget.wizardStepPlacesPanel',

    border: false,

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
        }
    ],

    initComponent: function()
    {
        this.items = [
            {
                xtype: 'label',
                text: "nice content here"
            }
        ];

        this.callParent( arguments );
    }

} );

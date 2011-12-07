Ext.define( 'App.view.wizard.group.WizardStepGeneralPanel', {
    extend: 'Ext.form.Panel',
    alias : 'widget.wizardStepGeneralPanel',

    clientValidation: true,

    items: [{
        xtype: 'fieldset',
        title: 'General',
        padding: '10px 15px',
        defaults: {
            width: 600
        },
        items: [{
                xtype: 'textfield',
                fieldLabel: 'Name',
                allowBlank: false,
                name: 'name'
            },
            {
                xtype: 'checkbox',
                fieldLabel: 'Restricted enrollment',
                name: 'restrictedEnrollment'
            },
            {
                xtype: 'textarea',
                fieldLabel: 'Description',
                rows: 5,
                name: 'description'
            }
        ]
    }]

} );

Ext.define( 'App.view.wizard.group.WizardStepGeneralPanel', {
    extend: 'Ext.form.Panel',
    alias : 'widget.wizardStepGeneralPanel',


    initComponent: function()
    {
        var me = this;
        me.items = [{
            xtype: 'fieldset',
            title: 'General',
            padding: '10px 15px',
            defaults: {
                width: 600
            },
            items: [
                {
                    xtype: 'textfield',
                    fieldLabel: 'Name <span style="color: red;">*</span>',
                    allowBlank: false,
                    value: me.modelData ? me.modelData.displayName : '',
                    name: 'displayName',
                    itemId: 'displayName',
                    enableKeyEvents: true,
                    listeners: {
                        keyup: function(field, event)
                        {
                            var value = field.getValue();
                            var displayNameLabel = me.up('groupWizardPanel').down('#wizardHeader');
                            if (value.trim() == '')
                            {
                                value = 'Display Name';
                            }
                            displayNameLabel.update(value);
                        }
                    },
                    emptyText: 'Display Name'
                },
                {
                    xtype: 'checkbox',
                    fieldLabel: 'Public group',
                    value: me.modelData ? me.modelData.public : false,
                    name: 'restrictedEnrollment'
                },
                {
                    xtype: 'textarea',
                    fieldLabel: 'Description',
                    allowBlank: true,
                    rows: 5,
                    value: me.modelData ? me.modelData.description : '',
                    name: 'description'
                }
            ]
        }];
        me.callParent( arguments );
    },

    getData: function()
    {
        var restricted = this.down('*[name=restrictedEnrollment]').value;
        var description = this.down('*[name=description]').value;
        var data = {'description': description, 'restricted': restricted};
        return data;
    }
} );

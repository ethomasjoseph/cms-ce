Ext.define( 'App.view.wizard.user.WizardStepUserSummaryPanel', {
    extend: 'Ext.form.Panel',
    alias : 'widget.wizardStepUserSummaryPanel',

    border: false,
    alwaysKeep: true,

    items: [
        {
            xtype: 'component',
            html: 'A nice summary will come here'
        }
    ],

    initComponent: function()
    {
        this.callParent( arguments );
    }

} );
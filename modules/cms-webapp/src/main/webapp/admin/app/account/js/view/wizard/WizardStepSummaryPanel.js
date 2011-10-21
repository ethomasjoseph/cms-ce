Ext.define( 'App.view.wizard.WizardStepSummaryPanel', {
    extend: 'Ext.form.Panel',
    alias : 'widget.wizardStepSummaryPanel',

    border: false,

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
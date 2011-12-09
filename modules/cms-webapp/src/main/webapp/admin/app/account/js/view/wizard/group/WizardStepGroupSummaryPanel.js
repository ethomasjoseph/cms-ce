Ext.define( 'App.view.wizard.group.WizardStepGroupSummaryPanel', {
    extend: 'Ext.form.Panel',
    alias : 'widget.wizardStepGroupSummaryPanel',

    border: false,

    items: [
        {
            xtype: 'component',
            html: 'A nice group summary will come here'
        }
    ],

    initComponent: function()
    {
        this.callParent( arguments );
    }

} );
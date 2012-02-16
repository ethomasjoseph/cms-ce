Ext.define( 'Cms.view.account.wizard.group.WizardStepGroupSummaryPanel', {
    extend: 'Ext.form.Panel',
    alias : 'widget.wizardStepGroupSummaryPanel',

    border: false,
    alwaysKeep: true,

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
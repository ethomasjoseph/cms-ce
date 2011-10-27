Ext.define( 'App.view.wizard.WizardStepPlacesPanel', {
    extend: 'Ext.form.Panel',
    alias : 'widget.wizardStepPlacesPanel',

    requires: [ 'Common.BoxSelect' ],
    border: false,

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

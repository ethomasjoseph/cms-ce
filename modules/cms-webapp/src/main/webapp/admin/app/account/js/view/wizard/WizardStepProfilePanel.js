Ext.define( 'App.view.wizard.WizardStepProfilePanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.wizardStepProfilePanel',

    layout: 'card',

    items:[
        {
            xtype: 'userStoreListPanel'
        },
        {
            itemId: 'userForm',
            xtype: 'editUserFormPanel',
            enableToolbar: false
        }
    ],


    initComponent: function()
    {
        this.callParent( arguments );
    }


} )
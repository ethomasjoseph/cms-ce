Ext.define( 'App.view.wizard.WizardStepProfilePanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.wizardStepProfilePanel',

    layout: 'card',

    items:[
        {
            xtype: 'container',
            items: [
                {
                    xtype: 'container',
                    autoHeight: true,
                    styleHtmlContent: true,
                    html: '<h3>Select a userstore</h3>'
                },
                {
                    xtype: 'userStoreListPanel'
                }
            ]
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


} );
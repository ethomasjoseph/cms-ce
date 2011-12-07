Ext.define( 'App.view.wizard.group.GroupWizardToolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias : 'widget.groupWizardToolbar',

    border: false,

    isNewGroup: true,

    initComponent: function()
    {

        var buttonDefaults = {
            scale: 'medium',
            iconAlign: 'top',
            minWidth: 64
        };

        var saveBtn = {
            xtype: 'buttongroup',
            columns: 1,
            defaults: buttonDefaults,
            items: [
                {
                    text: 'Save',
                    action: 'saveGroup',
                    itemId: 'save',
                    iconCls: 'icon-btn-save-24'
                }
            ]};
        var deleteBtn = {
            xtype: 'buttongroup',
            columns: 1,
            defaults: buttonDefaults,
            items: [
                {
                    text: 'Delete',
                    action: 'deleteGroup',
                    iconCls: 'icon-delete-user-24'
                }
            ]};

        if( this.isNewGroup ) {
            this.items = [ saveBtn ];
        } else {
            this.items = [ saveBtn, deleteBtn ];
        }
        this.callParent( arguments );
    }

} );

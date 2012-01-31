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
                    disabled: true,
                    iconCls: 'icon-save-24'
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
        var finishBtn = {
            xtype: 'buttongroup',
            hidden: true,
            columns: 1,
            itemId: 'finish',
            defaults: buttonDefaults,
            items: [{
                    text: 'Finish',
                    action: 'finishGroup',
                    iconCls: 'icon-ok-24'
                }
            ]};
        if( this.isNew ) {
            this.items = [ saveBtn, '->', finishBtn ];
        } else {
            this.items = [ saveBtn, deleteBtn, '->', finishBtn ];
        }
        this.callParent( arguments );
    }

} );

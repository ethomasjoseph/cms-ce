Ext.define( 'Cms.view.account.preview.group.GroupPreviewToolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias : 'widget.groupPreviewToolbar',


    initComponent: function()
    {

        var buttonDefaults = {
            scale: 'medium',
            iconAlign: 'top',
            minWidth: 64
        };

        var deleteBtn = {
            xtype: 'buttongroup',
            columns: 1,
            defaults: buttonDefaults,
            items: [
                {
                    text: 'Delete',
                    action: 'deleteGroupPreview',
                    iconCls: 'icon-delete-user-24'
                }
            ]};
        var ediUser = {
            xtype: 'buttongroup',
            columns: 1,
            defaults: buttonDefaults,
            items: [
                {
                    text: 'Edit Group',
                    action: 'editGroupPreview',
                    iconCls: 'icon-edit-user-24'
                }
            ]};
        if (this.deletable)
        {
            this.items = [ ediUser, deleteBtn];
        }
        else
        {
            this.items = [ ediUser ];
        }
        this.callParent( arguments );
    }

} );

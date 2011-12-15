Ext.define( 'App.view.preview.group.GroupPreviewToolbar', {
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
                    action: 'showDeleteWindow',
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
                    action: 'edit',
                    iconCls: 'icon-edit-user-24'
                }
            ]};
        this.items = [ ediUser, deleteBtn];
        this.callParent( arguments );
    }

} );

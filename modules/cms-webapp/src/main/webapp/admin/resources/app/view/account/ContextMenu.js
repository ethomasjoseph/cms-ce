Ext.define('Cms.view.account.ContextMenu', {
    extend: 'Ext.menu.Menu',
    alias: 'widget.accountContextMenu',

    items: [
        {
            text: 'Edit',
            iconCls: 'icon-edit',
            action: 'edit',
            disableOnMultipleSelection: false
        },
        {
            text: 'Delete',
            iconCls: 'icon-delete',
            action: 'showDeleteWindow'
        },
        {
            text: 'View',
            iconCls: 'icon-user',
            action: 'viewUser',
            disableOnMultipleSelection: false
        },
        '-',
        {
            text: 'Change Password',
            iconCls: 'icon-change-password',
            action: 'changePassword',
            disableOnMultipleSelection: true
        }
    ]
});


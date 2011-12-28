Ext.define('App.view.ContentToolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.contentToolbar',

    items: [
        {
            text: 'New',
            handler: function() {
                alert('New');
            }
        },
        {
            text: 'Edit',
            handler: function() {
                alert('Edit');
            }
        },
        {
            text: 'Delete',
            handler: function() {
                alert('Delete');
            }
        }
    ],

    initComponent: function() {
        this.callParent(arguments);
    }
});


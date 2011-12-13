Ext.define('App.view.Toolbar_3', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.toolbar3',

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


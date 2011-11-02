Ext.define( 'App.view.NotificationWindow', {
    extend: 'Ext.Component',
    alias: 'widget.notificationWindow',
    width: 370,
    autoHeight: true,
    floating: true,
    autoShow: true,

    tpl: Templates.main.notificationWindow,

    initComponent: function()
    {
        this.callParent( arguments );

        this.update({});

    },

    onRender: function()
    {
        this.callParent( arguments );
    },

    afterRender: function()
    {
        this.callParent( arguments );
        this.getEl().setOpacity(0);
    }

});

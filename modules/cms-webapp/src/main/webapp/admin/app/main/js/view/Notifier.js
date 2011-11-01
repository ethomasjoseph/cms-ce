Ext.define( 'App.view.Notifier', {
    extend: 'Ext.Component',
    alias: 'widget.notifier',
    width: 370,
    autoHeight: true,
    floating: true,
    autoShow: true,

    tpl: Templates.main.notifier,

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

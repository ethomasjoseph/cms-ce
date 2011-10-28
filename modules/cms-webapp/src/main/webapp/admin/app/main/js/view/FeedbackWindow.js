Ext.define( 'App.view.FeedbackWindow', {
    extend: 'Ext.Component',
    alias: 'widget.feedbackWindow',
    width: 370,
    autoHeight: true,
    floating: true,
    autoShow: true,

    tpl: Templates.main.feedbackWindow,

    initComponent: function()
    {
        this.callParent( arguments );
    },

    onRender: function()
    {
        this.callParent( arguments );
    },

    afterRender: function()
    {
        this.callParent( arguments );
    }

});

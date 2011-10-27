Ext.define( 'App.view.FeedbackBox', {
    extend: 'Ext.Component',
    alias: 'widget.feedbackBox',
    width: 380,
    autoHeight: true,
    floating: true,
    autoShow: true,

    tpl: Templates.main.feedbackBox,

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

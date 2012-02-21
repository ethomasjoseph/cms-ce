Ext.define( 'App.controller.SummaryController', {
    extend: 'Ext.app.Controller',
    views: ['SummaryTreeGrid'],

    init: function()
    {
        this.control( {
            'summaryTreeGrid': {
                afterrender: function(grid) {
                    // grid.getStore().load();
                }
            }
        });
    }
});
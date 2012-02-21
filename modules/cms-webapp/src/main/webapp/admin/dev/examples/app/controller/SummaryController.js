Ext.define( 'App.controller.SummaryController', {
    extend: 'Ext.app.Controller',
    views: ['SummaryTreeGrid'],

    init: function()
    {
        this.control( {
            'summaryTreeGrid': {
                afterrender: function(grid) {

                    setTimeout(function() {
                        console.log('store load()');
                        grid.getStore().load();
                    }, 3000);

                    //grid.getStore().load();
                    /*
                    Ext.Ajax.request({
                        url: 'summary.json',
                        success: function(response){
                            var responseText = response.responseText;
                            console.log(Ext.JSON.decode(responseText));
                            console.log(grid.getStore());


                        }
                    });
                    */
                }
            }
        });
    }
});
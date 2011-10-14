Ext.define('App.view.ActivityStreamPanel', {
    extend: 'Ext.panel.Panel',
    alias : 'widget.activityStream',
    title: 'Activity Stream',
    tools:[
        {
            type:'refresh'
        },
        {
            type:'gear'
        }
    ],
    collapsible: true,
    width: 270,
    minWidth: 200,
    maxWidth: 270,
    autoScroll: true,
    bodyCls: 'cms-activity-stream-panel-body',

    initComponent: function()
    {
        this.html = '<div id="cms-activity-stream-speak-out-panel-container"><!-- --></div>' +
                    '<div id="cms-activity-stream-message-container"><!-- --></div>';

        this.callParent(arguments);
    }

});

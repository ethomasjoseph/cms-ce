Ext.define( 'App.controller.LauncherController', {
    extend: 'App.controller.AdminBaseController',

    init: function()
    {
        this.control(
            {
                'viewport': {
                    render: function() {
                        this.setUpActivityStreamDropListener()
                    }
                },
                '*[action=loadTests]': {
                    click: function() {
                        this.loadModule('TestController');
                    }
                },
                '*[action=loadAccounts]': {
                    click: function() {
                        this.loadModule('AccountController');
                    }
                },
                '*[action=loadContent]': {
                    click: function() {
                        this.loadModule('ContentController');
                    }
                }
            }
        );
    },

    setUpActivityStreamDropListener: function()
    {
        var self = this;
        var activityStreamPanel = this.getActivityStreamPanel();
        activityStreamPanel.mon(activityStreamPanel, 'render', function(panel, options) {
            var dropTargetEl = panel.body.dom;
            Ext.create('Ext.dd.DropTarget', dropTargetEl, {
                ddGroup: 'global_activityStreamDragGroup',
                notifyEnter: function(ddSource, e, data) {
                    panel.body.stopAnimation();
                    panel.body.highlight();
                },
                notifyDrop: function(ddSource, e, data) {
                    self.activityStreamOnNotifyDrop(ddSource, e, data)

                    return true;
                }
            });
        }, this);
    },

    activityStreamOnNotifyDrop: function(ddSource, e, data)
    {
        var panel = this.getActivityStreamPanel();
        var records = data.records;

        for (var i = 0; i < records.length; i++)
        {
            panel.body.createChild(
                {
                    tag: 'p',
                    style:'padding:5px 10px',
                    html: 'Bot: @mer: Please review this item: "' + records[i].data.name + '"'
                }
            );
        }
    }

} );

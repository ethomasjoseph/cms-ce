Ext.define( 'App.controller.LauncherController', {
    extend: 'Ext.app.Controller',

    init: function()
    {
        this.control(
            {
                'viewport': {
                    render: function() {
                        this.activityStreamSetupDropListener()
                    }
                },
                '*[action=loadModule-1]': {
                    click: function(button, event) {
                        this.loadModule('Module_1_Controller', button, event);
                    }
                },
                '*[action=loadModule-2]': {
                    click: function(button, event) {
                        this.loadModule('Module_2_Controller', button, event);
                    }
                },
                '*[action=loadModule-3]': {
                    click: function(button, event) {
                        this.loadModule('Module_3_Controller', button, event);
                    }
                }
            }
        );

        this.loadMask = new Ext.LoadMask(Ext.getBody(), {msg:"Please wait..."});
    },

    loadModule: function(controllerName, element, event)
    {
        this.loadMask.show();

        var controller = this.getController(controllerName);
        var view = this.getView(controller.views[0]).create();
        var options = { single: true };
        var args = [];

        view.mon(view, 'render', function() {
            console.log('executing init on Controller "' + this.id + '", passing: ', args);

            controller.init.apply(controller, args);
            this.loadMask.hide();
        }, this, options);

        view.mon(view, 'deactivate', function(view) {
            console.log('removing controller "' + this.id + '" and destroying controller "' + view.id + '"');

            view.destroy();
            Ext.destroy(this.application.controllers.remove(this));
        }, this, options);

        this.getModulePanel().getLayout().setActiveItem(view);
    },

    activityStreamSetupDropListener: function()
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
                    html: 'Bot: @mer: Please review this item: "' + records[0].data.name + '"'
                }
            );
        }
    },

    getModulePanel: function()
    {
        return Ext.ComponentQuery.query('#module-panel')[0];
    },

    getActivityStreamPanel: function()
    {
        return Ext.ComponentQuery.query('#activity-stream')[0];
    }


} );

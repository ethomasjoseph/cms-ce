Ext.define('App.controller.FeedbackWindowController', {
    extend: 'Ext.app.Controller',
    views: ['FeedbackWindow'],

    init: function()
    {
        this.control({
        });

        this.application.on(
            {
                'feedbackWindow.show': this.show,
                scope: this
            }
        );

        this.createWindow();
    },

    show: function(title, message)
    {
        if (self.fadeoutWaiter)
            clearTimeout(self.fadeoutWaiter);

        this.feedbackWindow.center();
        this.feedbackWindow.update(
            {
                messageTitle: title,
                messageText: message
            }
        );

        this.fadeIn();
    },

    hide: function()
    {
        if (self.fadeoutWaiter)
            clearTimeout(self.fadeoutWaiter);

        this.feedbackWindow.stopAnimation();
        this.feedbackWindow.getEl().setOpacity(0);
    },

    fadeIn: function()
    {
        var self = this;

        this.feedbackWindow.animate(
            {
                duration: 500,
                from: {
                    opacity: 0
                },
                to: {
                    opacity: .9
                },
                listeners: {
                    afteranimate: function() {
                        self.fadeoutWaiter = setTimeout(function() {
                            self.fadeOut();
                        }, 3000)
                    },
                    scope: this
                }
            }
        )
    },

    fadeOut: function()
    {
        if (this.fadeoutWaiter)
            clearTimeout(this.fadeoutWaiter);

        this.feedbackWindow.animate(
            {
                duration: 500,
                from: {
                    opacity: .9
                },
                to: {
                    opacity: 0
                },
                listeners: {
                }
            }
        )
    },

    createWindow: function()
    {
        this.feedbackWindow = Ext.create('App.view.FeedbackWindow');
        this.feedbackWindow.update({});
        this.addWindowClickListener();
        this.feedbackWindow.getEl().setOpacity(0);
    },

    // Private
    addWindowClickListener: function()
    {
        this.feedbackWindow.getEl().on('click', function(event, target) {
            if(target.className.indexOf('notify-user-button') > -1)
            {
                Ext.Msg.alert('Comming soon', 'Notify User Window');
            }

            this.hide();
        }, this);
    }

});

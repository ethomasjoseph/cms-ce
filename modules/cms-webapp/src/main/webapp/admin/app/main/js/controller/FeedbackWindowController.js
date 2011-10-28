Ext.define('App.controller.FeedbackWindowController', {
    extend: 'Ext.app.Controller',
    views: ['FeedbackWindow'],

    init: function()
    {
        this.control({
        });

        this.application.on(
            {
                'feedbackWindow.showMessage': this.showMessage,
                scope: this
            }
        );

        this.createWindow();
    },

    showMessage: function(title, message)
    {
        this.feedbackWindow.update(
            {
                messageTitle: title,
                messageText: message
            }
        );

        this.fadeInAndOut();
    },

    hide: function()
    {
        this.feedbackWindow.stopAnimation();
        this.feedbackWindow.getEl().setOpacity(0);
    },

    fadeInAndOut: function()
    {
        var self = this;
        this.feedbackWindow.stopAnimation();

        this.feedbackWindow.center();
        var windowPosition = this.feedbackWindow.getPosition()[1];

        this.feedbackWindow.animate(
            {
                duration: 300,
                from: {
                    opacity: 0,
                    y: windowPosition - 160
                },
                to: {
                    opacity: .9,
                    y: windowPosition
                },
                easing: 'easeIn'
            }
        ).animate(
            {
                duration: 2500,
                from: {
                    opacity: .9
                },
                to: {
                    opacity: .9
                }
            }
        ).animate(
            {
                duration: 500,
                from: {
                    opacity: .9
                },
                to: {
                    opacity: 0
                }
            }
        );
    },

    // Private
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

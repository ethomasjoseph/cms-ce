Ext.define('App.controller.FeedbackWindowController', {
    extend: 'Ext.app.Controller',
    views: ['FeedbackWindow'],

    init: function()
    {
        Ext.create('widget.feedbackWindow');

        this.control({
        });

        this.application.on(
            {
                'feedbackWindow.show': this.show,
                scope: this
            }
        );

        this.addWindowClickListener();
    },

    show: function(title, message)
    {
        this.getFeedbackWindow().update(
            {
                messageTitle: title,
                messageText: message
            }
        );

        this.animateWindow();
    },

    animateWindow: function()
    {
        var self = this;
        var feedbackWindow = this.getFeedbackWindow();

        feedbackWindow.stopAnimation();

        var viewPortHeight = Ext.Element.getViewportHeight();
        var viewPortWidth = Ext.Element.getViewportWidth();
        var windowBox = feedbackWindow.getBox();
        var leftPosition = viewPortWidth - windowBox.width - 5;
        var animateFromPosition = viewPortHeight + windowBox.height;
        var animateToPosition = viewPortHeight - windowBox.height - 5;

        feedbackWindow.setPosition(leftPosition, animateFromPosition);

        feedbackWindow.animate(
            {
                duration: 400,
                from: {
                    opacity: 0,
                    y: animateFromPosition
                },
                to: {
                    opacity: 1,
                    y: animateToPosition
                },
                easing: 'easeIn'
            }
        ).animate(
            {
                duration: 4000,
                from: {
                    opacity: 1
                },
                to: {
                    opacity: 1
                }
            }
        ).animate(
            {
                duration: 500,
                from: {
                    opacity: 1
                },
                to: {
                    opacity: 0
                }
            }
        );
    },

    hide: function()
    {
        var feedbackWindow = this.getFeedbackWindow();
        feedbackWindow.stopAnimation();
        feedbackWindow.getEl().setOpacity(0);
    },

    addWindowClickListener: function()
    {
        var feedbackWindow = this.getFeedbackWindow();
        feedbackWindow.getEl().on('click', function(event, target) {
            if(target.className.indexOf('notify-user-button') > -1)
            {
                Ext.Msg.alert('Comming soon', 'Notify User Window');
            }

            this.hide();
        }, this);
    },

    getFeedbackWindow: function()
    {
        return Ext.ComponentQuery.query('feedbackWindow')[0];
    }

});

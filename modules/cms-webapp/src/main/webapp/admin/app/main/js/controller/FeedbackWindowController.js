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
                //TODO: get real user
                var user = {
                    data: {
                       "username":"mer",
                       "display-name":"Morten Øien Eriksen",
                       "name":"mer",
                       "key":"2AF735F668BB0B75F8AF886C4D304F049460EE43",
                       "displayName":"Morten Øien Eriksen",
                       "lastModified":"2010-03-15 16:00:02",
                       "qualifiedName":"enonic\\mer",
                        "email":"mer@enonic.com"
                    }
                };
                this.application.fireEvent('notifier.show', user );
            }

            this.hide();
        }, this);
    },

    getFeedbackWindow: function()
    {
        return Ext.ComponentQuery.query('feedbackWindow')[0];
    }

});

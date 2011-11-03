Ext.define('App.controller.NotificationWindowController', {
    extend: 'Ext.app.Controller',

    views: ['NotificationWindow'],

    init: function()
    {
        Ext.create('widget.notificationWindow');

        this.control({
        });

        this.application.on(
            {
                'notifier.show': this.show,
                scope: this
            }
        );

        this.addWindowClickListener();
    },

    show: function(title, message)
    {
        this.getNotificationWindow().update(
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
        var notificationWindow = this.getNotificationWindow();

        notificationWindow.stopAnimation();

        var viewPortHeight = Ext.Element.getViewportHeight();
        var viewPortWidth = Ext.Element.getViewportWidth();
        var windowBox = notificationWindow.getBox();
        var leftPosition = viewPortWidth - windowBox.width - 5;
        var animateFromPosition = viewPortHeight + windowBox.height;
        var animateToPosition = viewPortHeight - windowBox.height - 5;

        notificationWindow.setPosition(leftPosition, animateFromPosition);

        notificationWindow.animate(
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
        var notificationWindow = this.getNotificationWindow();
        notificationWindow.stopAnimation();
        notificationWindow.getEl().setOpacity(0);
    },

    addWindowClickListener: function()
    {
        this.getNotificationWindow().getEl().on('click', function(event, target) {
            if(target.className.indexOf('notify-user') > -1)
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
                this.application.fireEvent('showNotifyUserWindow ', user );
            }

            this.hide();
        }, this);
    },

    getNotificationWindow: function()
    {
        return Ext.ComponentQuery.query('notificationWindow')[0];
    }

});

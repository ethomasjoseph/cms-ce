Ext.define('App.controller.NotifierController', {
    extend: 'Ext.app.Controller',
    views: ['Notifier'],

    init: function()
    {
        Ext.create('widget.notifier');

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
        this.getNotifier().update(
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
        var notifier = this.getNotifier();

        notifier.stopAnimation();

        var viewPortHeight = Ext.Element.getViewportHeight();
        var viewPortWidth = Ext.Element.getViewportWidth();
        var windowBox = notifier.getBox();
        var leftPosition = viewPortWidth - windowBox.width - 5;
        var animateFromPosition = viewPortHeight + windowBox.height;
        var animateToPosition = viewPortHeight - windowBox.height - 5;

        notifier.setPosition(leftPosition, animateFromPosition);

        notifier.animate(
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
        var notifier = this.getNotifier();
        notifier.stopAnimation();
        notifier.getEl().setOpacity(0);
    },

    addWindowClickListener: function()
    {
        var notifier = this.getNotifier();
        notifier.getEl().on('click', function(event, target) {
            if(target.className.indexOf('notify-user') > -1)
            {
                Ext.Msg.alert('Comming soon', 'Notify User Window');
            }

            this.hide();
        }, this);
    },

    getNotifier: function()
    {
        return Ext.ComponentQuery.query('notifier')[0];
    }

});

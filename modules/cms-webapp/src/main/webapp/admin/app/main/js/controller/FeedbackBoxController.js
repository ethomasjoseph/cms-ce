Ext.define('App.controller.FeedbackBoxController', {
    extend: 'Ext.app.Controller',
    views: ['FeedbackBox'],

    init: function()
    {
        this.control({
        });

        this.application.on(
            {
                'feedback.show': this.show,
                scope: this
            }
        );

        this.createBoxComponent();
    },

    show: function(title, message)
    {
        if (self.fadeoutWaiter)
            clearTimeout(self.fadeoutWaiter);

        this.feedbackBox.center();
        this.feedbackBox.update(
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

        this.feedbackBox.stopAnimation();
        this.feedbackBox.getEl().setOpacity(0);
    },

    fadeIn: function()
    {
        var self = this;

        this.feedbackBox.animate(
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
        clearTimeout(this.fadeoutWaiter);

        this.feedbackBox.animate(
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

    createBoxComponent: function()
    {
        this.feedbackBox = Ext.create('App.view.FeedbackBox');
        this.feedbackBox.update({});
        this.addBoxClickListener();
        this.feedbackBox.getEl().setOpacity(0);
    },

    // Private
    addBoxClickListener: function()
    {
        this.feedbackBox.getEl().on('click', function(event, target) {
            if(target.className.indexOf('notify-user-button') > -1)
            {
                Ext.Msg.alert('Comming soon', 'Notify User Window');
            }

            this.hide();
        }, this);
    }

});

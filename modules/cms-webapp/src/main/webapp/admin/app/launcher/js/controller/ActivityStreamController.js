Ext.define('App.controller.ActivityStreamController', {
    extend: 'Ext.app.Controller',

    stores: ['ActivityStreamStore'],
    models: ['ActivityStreamModel'],
    views: ['ActivityStreamPanel'],

    init: function() {
        this.control(
        {
            'activityStreamPanel': {
                'afterrender': this.onPanelRender
            }
        });
    },

    onPanelRender: function()
    {
        this.appendSpeakOutPanel();
        this.loadMessages();
    },

    loadMessages: function() {
        var store = this.getStore('ActivityStreamStore');
        store.load(
            {
                scope: this,
                callback: function(records, operation, success) {
                    this.renderMessages();
                }
            }
        );
    },

    renderMessages: function(messages)
    {
        for ( var i = 0; i < messages.length; i++)
        {
            this.appendMessage(messages[i].data);
        }
    },

    appendMessage: function(message)
    {
        var template = new Ext.XTemplate(Templates.launcher.activityStreamItem);
        var messagesContainer = Ext.DomQuery.select( '#cms-activity-stream-message-container' )[0];
        var messageElement = template.append( messagesContainer, message);

        this.postProcessMessage(messageElement);
    },

    postProcessMessage: function(messageElement)
    {
        var dom = Ext.DomQuery;
        var eventManager = Ext.EventManager;
        var favorite = dom.selectNode('.favorite', messageElement);
        var comment = dom.selectNode('.comment', messageElement);
        var more = dom.selectNode('.more', messageElement);

        eventManager.addListener(messageElement, 'mouseenter', function(event, target) {
            favorite.style.visibility = 'visible';
            comment.style.visibility = 'visible';
            more.style.visibility = 'visible';
        }, this);

        eventManager.addListener(messageElement, 'mouseleave', function(event, target) {
            favorite.style.visibility = 'hidden';
            comment.style.visibility = 'hidden';
            more.style.visibility = 'hidden';
        }, this);
    },

    appendSpeakOutPanel: function()
    {
        var template = new Ext.XTemplate(Templates.launcher.speakOutPanel);
        var container = Ext.DomQuery.select( '#cms-activity-stream-speak-out-panel-container' )[0];
        template.append( container, {});

        this.appendSpeakOutTextField();
        this.appendSpeakOutSendButton();
    },

    appendSpeakOutTextField: function()
    {
        return new Ext.form.TextField(
            {
                renderTo: 'activity-stream-speak-out-text-input',
                width: 228
            }
        );
    },

    appendSpeakOutSendButton: function()
    {
        return new Ext.button.Button(
            {
                renderTo: 'activity-stream-speak-out-send-button',
                text: 'Send'
            }
        );
    }

});

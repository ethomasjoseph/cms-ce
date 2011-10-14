Ext.define('App.view.ActivityStreamPanel', {
    extend: 'Ext.panel.Panel',
    alias : 'widget.activityStream',
    title: 'Activity Stream',
    tools:[
        {
            type:'refresh'
        },
        {
            type:'gear'
        }
    ],
    collapsible: true,
    width: 270,
    minWidth: 200,
    maxWidth: 270,
    autoScroll: true,
    html: '<div id="cms-activity-stream-speak-out-box"><!-- --></div><div id="cms-activity-stream-message-container"><!-- --></div>',
    bodyCls: 'cms-activity-stream-panel-body',
    listeners: {
        afterrender: function() {
            this.renderMessages()
        }
    },

    renderMessages: function()
    {
        var message = {
            displayName:'Morten Eriksen',
            photo: 'resources/images/x-user.png',
            location: 'Admin',
            action: 'Created article',
            description: '99 Luftballons',
            prettyDate: '2 minutes ago',
            birthday: false
        };

        var message2 = {
            displayName:'Morten Eriksen',
            photo: 'resources/images/x-user.png',
            location: 'Admin',
            action: 'Created article',
            description: '99 Luftballons gnafl gnafl gnafl gnaflgnafl gnafl gnaflgnafl gnafl gnaflgnafl gnafl gnafl',
            prettyDate: '2 minutes ago',
            birthday: true
        };

        this.appendChatBox();

        this.appendMessage(message);
        this.appendMessage(message2);
        this.appendMessage(message);
        this.appendMessage(message);
        this.appendMessage(message);
        this.appendMessage(message);
        this.appendMessage(message);
        this.appendMessage(message);
        this.appendMessage(message);
    },

    appendMessage: function(message)
    {
        var tpl = new Ext.XTemplate(Templates.launcher.activityStreamItem);
        var messageContainer = Ext.DomQuery.select( '#cms-activity-stream-message-container' )[0];
        var messageElement = tpl.append( messageContainer, message);

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

    appendChatBox: function()
    {
        var tpl = new Ext.XTemplate(Templates.launcher.speakOutPanel);
        var container = Ext.DomQuery.select( '#cms-activity-stream-speak-out-box' )[0];
        var chatBox = tpl.append( container, {});

        this.appendSpeakOutTextField();
        this.appendSpeakOutSendButton();
    },

    appendSpeakOutTextField: function()
    {
        var dom = Ext.DomQuery;
        return new Ext.form.TextField({
            renderTo: dom.selectNode('#activity-stream-speak-out-text-input'),
            width: 228
        });
    },

    appendSpeakOutSendButton: function()
    {
        var dom = Ext.DomQuery;
        return new Ext.button.Button({
            renderTo: dom.selectNode('#activity-stream-speak-out-send-button'),
            text: 'Send'
        });
    },

    initComponent: function()
    {
        this.callParent(arguments);
    }

});

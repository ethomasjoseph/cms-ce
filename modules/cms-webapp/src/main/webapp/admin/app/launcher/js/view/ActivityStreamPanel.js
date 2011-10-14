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
    html: '<div id="cms-activity-stream-message-container"><!-- --></div>',
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
            prettyDate: '2 minutes ago'
        };

        var message2 = {
            displayName:'Morten Eriksen',
            photo: 'resources/images/x-user.png',
            location: 'Admin',
            action: 'Created article',
            description: '99 Luftballons gnafl gnafl gnafl gnaflgnafl gnafl gnaflgnafl gnafl gnaflgnafl gnafl gnafl',
            prettyDate: '2 minutes ago'
        };

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
        var tpl = new Ext.Template(Templates.launcher.activityStreamItem);
        var messageContainer = Ext.DomQuery.select( '#cms-activity-stream-message-container' )[0];
        var messageElement = tpl.append( messageContainer, message);

        this.postProcessMessageElement(messageElement);
    },

    postProcessMessageElement: function(messageElement)
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

    initComponent: function()
    {
        this.callParent(arguments);
    }

});

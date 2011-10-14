Ext.define('App.controller.ActivityStreamController', {
    extend: 'Ext.app.Controller',

    models: ['ActivityStreamModel'],
    stores: ['ActivityStreamStore'],
    views: ['ActivityStreamPanel'],

    init: function() {
        this.control(
        {
            'activityStreamPanel': {
                'afterrender': this.afterPanelRender
            }
        });
    },

    afterPanelRender: function()
    {
        this.appendSpeakOutPanel();
        this.createDataView();
    },

    createDataView: function()
    {
        var store = this.getStore('ActivityStreamStore');
        var template = new Ext.XTemplate(Templates.launcher.activityStream);

        Ext.create('Ext.view.View', {
            store: store,
            tpl: template,
            itemSelector: 'div.cms-activity-stream-message',
            renderTo: 'cms-activity-stream-message-container',
            listeners: {
                'itemmouseenter':  {
                    fn: this.onMessageMouseEnter
                },
                'itemmouseleave':  {
                    fn: this.onMessageMouseLeave
                },
                'itemclick':  {
                    fn: this.onMessageClick
                }
            }
        });
    },

    onMessageMouseEnter: function(view, record, item, index, event, eOpts )
    {
        var dom = Ext.DomQuery;
        var eventManager = Ext.EventManager;
        var favorite = dom.selectNode('.favorite', item);
        var comment = dom.selectNode('.comment', item);
        var more = dom.selectNode('.more', item);

        favorite.style.visibility = 'visible';
        comment.style.visibility = 'visible';
        more.style.visibility = 'visible';
    },

    onMessageMouseLeave: function(view, record, item, index, event, eOpts )
    {
        var dom = Ext.DomQuery;
        var eventManager = Ext.EventManager;
        var favorite = dom.selectNode('.favorite', item);
        var comment = dom.selectNode('.comment', item);
        var more = dom.selectNode('.more', item);

        favorite.style.visibility = 'hidden';
        comment.style.visibility = 'hidden';
        more.style.visibility = 'hidden';
    },

    onMessageClick: function( view, record, item, index, event, eOpts )
    {
        var target = event.target;
        if (target.className.indexOf('favorited') === -1)
        {
            target.className += ' favorited';
        }
        else
        {
            target.className = target.className.replace(/ favorited/, '');
        }
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
        new Ext.form.TextField(
            {
                renderTo: 'activity-stream-speak-out-text-input',
                width: 228
            }
        );
    },

    appendSpeakOutSendButton: function()
    {
        new Ext.button.Button(
            {
                renderTo: 'activity-stream-speak-out-send-button',
                text: 'Send'
            }
        );
    }

});

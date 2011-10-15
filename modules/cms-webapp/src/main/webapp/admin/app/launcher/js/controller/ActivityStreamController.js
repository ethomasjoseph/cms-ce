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
                },
                'itemadd': {
                    fn: function(records, index, node, eOpts) {
                        // TODO: ExtJS view itemadd bug? param node is not the node.
                        // http://www.sencha.com/forum/showthread.php?142914-Ext.view.View-itemadd-event-bug
                    }
                }
            }
        });
    },

    onMessageMouseEnter: function(view, record, item, index, event, eOpts )
    {
        var dom = Ext.DomQuery;
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
        this.appendUrlShortenerButton();
        this.appendSpeakOutSendButton();
    },

    appendSpeakOutTextField: function()
    {
        new Ext.form.TextField(
            {
                itemId: 'speakOutTextField',
                renderTo: 'activity-stream-speak-out-text-input',
                enforceMaxLength: true,
                maxLength: 140,
                width: 228,
                enableKeyEvents: true,
                listeners: {
                    'keyup':  {
                        fn: this.speakOutTextFieldHandleKeyUp,
                        scope: this
                    }
                }
            }
        );
    },

    appendUrlShortenerButton: function()
    {
        new Ext.button.Button(
            {
                renderTo: 'activity-stream-speak-out-url-shortener-button-container',
                iconCls: 'icon-link',
                handler: function() {
                }
            }
        );
    },

    appendSpeakOutSendButton: function()
    {
        var me = this;

        new Ext.button.Button(
            {
                renderTo: 'activity-stream-speak-out-send-button-container',
                text: 'Send',
                handler: function() {
                    me.postMessage(me.getSpeakOutTextField().getValue());
                    me.resetSpeakOutTextField();
                }
            }
        );
    },

    postMessage: function(message)
    {
        if (message.length === 0)
        {
            return;
        }

        var store = this.getStore('ActivityStreamStore');
        store.insert( 0, [
            {
                "displayName":"Pavel Milkevich",
                "photo": "resources/images/pavel.jpeg",
                "location": "Admin",
                "action": "Said",
                "description": message,
                "prettyDate": "just now",
                "birthday": false
            }
        ]);
    },

    updateTextLeftContainer: function()
    {
        var textField = this.getSpeakOutTextField();
        var textLeftContainer = Ext.DomQuery.select( '#activity-stream-speak-out-letters-left-container' )[0];
        var textFieldMaxTextLength = textField.maxLength;
        var textFieldTextLength = textField.getValue().length;

        if (textFieldTextLength <= textFieldMaxTextLength)
        {
            textLeftContainer.innerHTML = String((textFieldMaxTextLength - textFieldTextLength));
        }
    },

    speakOutTextFieldHandleKeyUp: function(textField, event, eOpts) {
        var isEnterKey = event.button === 12;
        if ( isEnterKey )
        {
            this.postMessage(textField.getValue());
            this.resetSpeakOutTextField();
        }

        this.updateTextLeftContainer();
    },

    resetSpeakOutTextField: function()
    {
        var textField = this.getSpeakOutTextField();
        textField.setValue('');
        textField.focus();
    },

    getSpeakOutTextField: function()
    {
        return Ext.ComponentQuery.query( 'textfield[itemId=speakOutTextField]' )[0];
    }



});

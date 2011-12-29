// Bu-huuu! This is not allowed.
var _textForTranslateTest = 'The {0} {1} {2} jumps over the {3} {4}';

Ext.define( 'App.view.TestMainPanel', {
    extend: 'Ext.panel.Panel',
    alias : 'widget.testMainPanel',
    title: 'Tests',
    border: false,
    bodyPadding: 10,
    layout: 'vbox',
    defaults: {
        margin: '0 0 5 0'
    },
    items: [
        {
            xtype: 'button',
            text: 'Create New Window',
            action: 'createNewWindow',

        },
        {
            xtype: 'button',
            text: 'Go to Content',
            action: 'loadContent'
        },
        {
            xtype: 'panel',
            title: 'Translate Tests',
            bodyPadding: 10,
            defaults: {
                xtype: 'component',
                styleHtmlContent: true
            },
            items: [
                {
                    html: translate('You have <strong>{0}</strong> new message{1} in your <strong>{2}</strong>', '5', 's', 'Activity Stream')
                },
                {
                    html: translate(_textForTranslateTest, 'quick', 'brown', 'fox', 'lazy', 'dog')
                },
                {
                    html: translate(_textForTranslateTest, 'fearless', 'black', 'honey badger', 'sleeping', 'lion')
                }
            ]
        }
    ],


    initComponent: function()
    {
        this.callParent( arguments );
    }

} );

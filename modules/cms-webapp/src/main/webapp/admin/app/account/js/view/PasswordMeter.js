Ext.define( 'App.view.PasswordMeter', {
    extend: 'Ext.form.FieldContainer',
    alias: 'widget.passwordMeter',

    requires: ['Ext.ProgressBar'],
    height: 38,
    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    initComponent: function()
    {
        this.items = [{
            xtype: 'textfield',
            inputType: 'password',
            flex: 1
        },
        {
            xtype: 'progressbar',
            flex: 0.5
        }
        ];
        this.callParent( arguments );

        this.down('progressbar').updateProgress(0.5, '', true);
    }
});
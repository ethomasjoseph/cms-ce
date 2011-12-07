Ext.define( 'App.view.UserPreviewPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.userPreviewPanel',

    requires: ['App.view.UserPreviewToolbar'],

    dialogTitle: 'User Preview',

    autoWidth: true,

    tbar: {
        xtype: 'userPreviewToolbar'
    },

    layout: 'fit',
    width: undefined,


    initComponent: function()
    {
        this.items = [
            {
                xtype: 'panel',
                layout: {
                    type: 'hbox',
                    align: 'stretch'
                },
                border: 0,
                items: [
                    {
                        flex: 0.3,
                        tpl: Templates.account.userPreviewPhoto,
                        data: this.data
                    },
                    {
                        flex: 1,
                        xtype: 'panel',
                        layout: {
                            type: 'vbox',
                            align: 'stretch'
                        },
                        items: [
                            {
                                flex: 0.3,
                                tpl: Templates.account.userPreviewHeader,
                                data: this.data
                            },
                            {
                                flex: 1,
                                tpl: Templates.account.userPreviewStub,
                                data: {}
                            }]
                    },
                    {
                        flex: 0.5,
                        tpl: Templates.account.userPreviewCommonInfo,
                        data: {}
                    }]
        }];
        this.callParent( arguments );
    }

} );
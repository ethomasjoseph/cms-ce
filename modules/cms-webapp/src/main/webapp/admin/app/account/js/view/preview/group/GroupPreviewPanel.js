Ext.define( 'App.view.preview.group.GroupPreviewPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.groupPreviewPanel',


    requires: ['App.view.preview.group.GroupPreviewToolbar', 'Common.WizardPanel'],

    autoWidth: true,

    tbar: {
        xtype: 'groupPreviewToolbar'
    },

    cls: 'cms-user-preview-panel',
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
                defaults: {
                    border: 0
                },
                items: [
                    {
                        width: 100,
                        tpl: Templates.account.userPreviewPhoto,
                        data: this.data,
                        margin: 5
                    },
                    {
                        flex: 1,
                        cls: 'center',
                        xtype: 'panel',
                        layout: {
                            type: 'vbox',
                            align: 'stretch'
                        },
                        defaults: {
                            border: 0
                        },
                        items: [
                            {
                                height: 70,
                                tpl: Templates.account.userPreviewHeader,
                                data: this.data
                            },
                            {
                                flex: 1,
                                xtype: 'wizardPanel',
                                presentationMode: true,
                                showControls: false,
                                items: [
                                    {
                                        stepNumber: 1,
                                        stepTitle: "Memberships",
                                        tpl: Templates.account.previewMemberships,
                                        data: this.data
                                    }
                                ]
                            }
                        ]
                    }
                ]
            }
        ];
        this.callParent( arguments );
    }





} );
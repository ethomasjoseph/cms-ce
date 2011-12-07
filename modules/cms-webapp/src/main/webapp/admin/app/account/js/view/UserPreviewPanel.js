Ext.define( 'App.view.UserPreviewPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.userPreviewPanel',


    requires: ['App.view.UserPreviewToolbar', 'Common.WizardPanel'],

    dialogTitle: 'User Preview',

    autoWidth: true,

    tbar: {
        xtype: 'userPreviewToolbar'
    },

    cls: 'cms-user-preview',
    layout: 'fit',
    width: undefined,


    initComponent: function()
    {
        this.fieldSets = [
            {
                title: 'User',
                fields: ['username', 'email']
            },
            {
                title: 'Name',
                fields: [ 'prefix', 'first-name', 'middle-name',
                            'last-name', 'suffix', 'initials', 'nick-name']
            },
            {
                title: 'Personal Information',
                fields: ['personal-id', 'member-id', 'organization', 'birthday',
                        'gender', 'title', 'description', 'html-email', 'homepage']
            },
            {
                title: 'Settings',
                fields: ['timezone', 'locale', 'country', 'global-position']
            },
            {
                title: 'Communication',
                fields: ['phone', 'mobile', 'fax']
            }
        ];
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
                        width: 120,
                        tpl: Templates.account.userPreviewPhoto,
                        data: this.data,
                        cls: 'west'
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
                                showControls: false,
                                items: [
                                    {
                                        stepNumber: 1,
                                        stepTitle: "Activities"
                                    },
                                    {
                                        stepNumber: 2,
                                        stepTitle: "Profile",
                                        tpl: Templates.account.userPreviewStub,
                                        data: {}
                                    },
                                    {
                                        stepNumber: 3,
                                        stepTitle: "Places"
                                    },
                                    {
                                        stepNumber: 4,
                                        stepTitle: "Memberships"
                                    },
                                    {
                                        stepNumber: 5,
                                        stepTitle: "Advanced"
                                    }]
                            }]
                    },
                    {
                        flex: 0.5,
                        cls: 'east',
                        tpl: Templates.account.userPreviewCommonInfo,
                        data: this.data
                    }]
        }];
        this.callParent( arguments );
    }

} );
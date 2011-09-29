Ext.define( 'App.view.wizard.UserWizardPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.userWizardPanel',
    requires: [
        'Common.WizardPanel',
        'App.view.wizard.UserStoreListPanel',
        'App.view.wizard.UserWizardToolbar'
    ],

    layout: {
        type: 'hbox',
        align: 'stretch',
        padding: 10
    },

    defaults: {
        border: false
    },

    tbar: {
        xtype: 'userWizardToolbar'
    },

    items: [
        {
            width: 100,
            items: [
                {
                    xtype: 'image',
                    src: 'resources/images/x-user.png',
                    width: 100,
                    height: 100
                }
            ]
        },
        {
            flex: 1,
            layout: {
                type: 'vbox',
                align: 'stretch',
                padding: '0 10'
            },
            defaults: {
                border: false
            },
            items: [
                {
                    cls: 'cms-new-user-header',
                    styleHtmlContent: true,
                    html: '<h1>New User</h1><h4>User Wizard</h4>'
                },
                {
                    flex: 1,
                    xtype: 'wizardPanel',
                    showControls: false,
                    items: [
                        {
                            stepTitle: 'Userstore',
                            xtype: 'userStoreListPanel'
                        },
                        {
                            stepTitle: "Profile",
                            html: 'Panel 2<br/> Suspendisse massa justo, commodo viverra mollis vel, faucibus cursus nulla.'
                        },
                        {
                            stepTitle: "User",
                            html: 'Panel 3<br/>Quisque non tellus in massa feugiat dictum.'
                        },
                        {
                            stepTitle: "Memberships",
                            html: 'Quisque augue urna, lacinia ac consectetur sed, accumsan sit amet sapien.'
                        },
                        {
                            stepTitle: "Finalize",
                            html: 'Panel 5<br/> Duis vel nibh enim. Mauris vel risus erat, eu tristique elit.'
                        }
                    ]
                }
            ]
        }
    ],

    initComponent: function()
    {
        this.callParent( arguments );
    }

} );

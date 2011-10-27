Ext.define('App.view.AppLauncherToolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.appLauncherToolbar',
    id: 'app-launcher-toolbar',

    requires: ['App.view.LoggedInUserButton'],

    items: [
        {
            xtype: 'tbspacer', width: 5
        },
        // Logo
        {
            xtype: 'component',
            id: 'app-launcher-logo',
            autoEl: {
                tag: 'div'
            }
        },
        {
            xtype: 'tbspacer', width: 5
        },
        '-',
        {
            xtype: 'tbspacer', width: 5
        },
        // Start button
        {
            itemId: 'app-launcher-button',
            cls: 'app-launcher-button',
            xtype: 'button',
            text: 'Dashboard',
            icon: Ext.BLANK_IMAGE_URL, // TODO: (bug fix) Remove icon when Ext is upgraded to >= 4.0.7
            menu: {
                minWidth: 160,
                items:[
                    {
                        id: 0,
                        text: 'Dashboard',
                        cms: {
                            appUrl: 'app-dashboard.html'
                        },
                        icon: 'app/main/images/house.png'
                    },
                    '-',
                    {
                        id: 100,
                        text: 'Accounts',
                        cms: {
                            appUrl: 'app-account.html'
                        },
                        icon: 'app/main/images/group.png'
                    },
                    {
                        id: 200,
                        text: 'Content',
                        cms: {
                            appUrl: 'blank.html'
                        },
                        icon: 'app/main/images/folder_database.png'
                    },
                    {
                        id: 300,
                        text: 'Sites',
                        cms: {
                            appUrl: 'blank.html'
                        },
                        icon: 'app/main/images/world.png'
                    },
                    {
                        id: 500,
                        text: 'Direct Marketing',
                        cms: {
                            appUrl: 'blank.html'
                        },
                        icon: 'app/main/images/chart_curve.png'
                    },
                    {
                        id: 600,
                        text: 'Reports',
                        cms: {
                            appUrl: 'blank.html'
                        },
                        icon: 'app/main/images/report.png'
                    },

                    '-',

                    {
                        id: 800,
                        text: 'System',
                        cms: {
                            appUrl: 'app-system.html'
                        },
                        icon: 'app/main/images/cog.png',
                        menu: {
                            items: [
                                {
                                    id: 810,
                                    text:"Cache",
                                    cms: {
                                        appUrl:"app-systemCache.html"
                                    },
                                    icon: 'app/main/images/drive_web.png'
                                },
                                {
                                    id: 820,
                                    text: "Content Types",
                                    cms: {
                                        appUrl: "app-contentType.html"
                                    },
                                    icon: 'app/main/images/page_world.png'
                                },
                                {
                                    id: 830,
                                    text: 'Live Portal Trace',
                                    cms: {
                                        appUrl: 'blank.html'
                                    },
                                    icon: 'app/main/images/utilities-system-monitor.png'
                                },
                                {
                                    id: 840,
                                    text: 'Repository',
                                    cms: {
                                        appUrl: 'blank.html'
                                    },
                                    icon: 'app/main/images/database.png'
                                },
                                {
                                    id: 850,
                                    text: 'Userstores',
                                    cms: {
                                        appUrl: 'app-userstore.html'
                                    },
                                    icon: 'app/main/images/address-book-blue-icon.png'
                                }
                            ]
                        }
                    }
                ]
            }
        },
        /*{
            xtype: 'button',
            itemId: 'launcher-feedback-test-button',
            text: 'Show FeedbackBox'
        },*/
        '->',
        // Logged in user
        {
            //id: 'launcher-logged-in-user-button',
            xtype: 'loggedInUserButton',
            text: 'Morten Eriksen'
        },

        '-',

        // Settings
        {
            id: 'launcher-settings-button',
            xtype: 'button',
            iconCls: 'icon-settings',
            menu: [
                {
                    text: 'Setting 1'
                },
                {
                    text: 'Setting 2'
                },
                {
                    text: 'Setting 3'
                }
            ]
        }
    ]

});


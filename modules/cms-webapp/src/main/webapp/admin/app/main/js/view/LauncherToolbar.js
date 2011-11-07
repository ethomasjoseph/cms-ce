Ext.define('App.view.LauncherToolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.launcherToolbar',
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
                        icon: 'app/main/images/icons/dashboard.png'
                    },
                    '-',
                    {
                        id: 100,
                        text: 'Accounts',
                        cms: {
                            appUrl: 'app-account.html'
                        },
                        icon: 'app/main/images/icons/accounts.png'
                    },
                    {
                        id: 200,
                        text: 'Content',
                        cms: {
                            appUrl: 'blank.html'
                        },
                        icon: 'app/main/images/icons/content.png'
                    },
                    {
                        id: 300,
                        text: 'Campaigns',
                        cms: {
                            appUrl: 'blank.html'
                        },
                        icon: 'app/main/images/icons/campaigns.png'
                    },
                    {
                        id: 500,
                        text: 'Search',
                        cms: {
                            appUrl: 'blank.html'
                        },
                        icon: 'app/main/images/icons/search.png'
                    },
                    {
                        id: 600,
                        text: 'Segments',
                        cms: {
                            appUrl: 'blank.html'
                        },
                        icon: 'app/main/images/icons/segments.png'
                    },
                    {
                        id: 610,
                        text: 'Commerce',
                        cms: {
                            appUrl: 'blank.html'
                        },
                        icon: 'app/main/images/icons/commerce.png'
                    },
                    {
                        id: 620,
                        text: 'Optimizer',
                        cms: {
                            appUrl: 'blank.html'
                        },
                        icon: ''
                    },
                    {
                        id: 630,
                        text: 'Social',
                        cms: {
                            appUrl: 'blank.html'
                        },
                        icon: 'app/main/images/icons/social.png'
                    },
                    {
                        id: 800,
                        text: 'System',
                        cms: {
                            appUrl: 'app-system.html'
                        },
                        icon: 'app/main/images/icons/system.png',
                        menu: {
                            items: [
                                {
                                    id: 810,
                                    text:"WebDAV",
                                    cms: {
                                        appUrl:"blank.html"
                                    },
                                    icon: 'app/main/images/icons/webdav.png'
                                },
                                {
                                    id: 820,
                                    text: "Applications",
                                    cms: {
                                        appUrl:"blank.html"
                                    },
                                    icon: 'app/main/images/icons/applications.png'
                                },
                                {
                                    id: 830,
                                    text: 'Cluster',
                                    cms: {
                                        appUrl: 'blank.html'
                                    },
                                    icon: 'app/main/images/icons/cluster.png'
                                },
                                {
                                    id: 840,
                                    text: 'Live Trace',
                                    cms: {
                                        appUrl: 'blank.html'
                                    },
                                    icon: 'app/main/images/icons/live_trace.png'
                                },
                                {
                                    id: 850,
                                    text: 'Sites',
                                    cms: {
                                        appUrl: 'blank.html'
                                    },
                                    icon: 'app/main/images/icons/sites.png'
                                },
                                {
                                    id: 860,
                                    text: 'Userstores',
                                    cms: {
                                        appUrl: 'app-userstore.html'
                                    },
                                    icon: 'app/main/images/icons/userstores.png'
                                },
                                {
                                    id: 860,
                                    text: 'Cache',
                                    cms: {
                                        appUrl: 'app-systemCache.html'
                                    },
                                    icon: 'app/main/images/icons/cache.png'
                                },
                                {
                                    id: 870,
                                    text: 'Content Types',
                                    cms: {
                                        appUrl: 'app-contentType.html'
                                    },
                                    icon: 'app/main/images/icons/content_types.png'
                                }
                            ]
                        }
                    }
                ]
            }
        },
        '->',
        // Logged in user
        {
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


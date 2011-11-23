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
                        id: 'app-0',
                        text: 'Dashboard',
                        cms: {
                            appUrl: 'app-dashboard.html'
                        },
                        icon: 'app/main/images/launcher/dashboard.png'
                    },
                    '-',
                    {
                        id: 'app-100',
                        text: 'Accounts',
                        cms: {
                            appUrl: 'app-account.html'
                        },
                        icon: 'app/main/images/launcher/accounts.png'
                    },
                    {
                        id: 'app-200',
                        text: 'Content',
                        cms: {
                            appUrl: 'blank.html'
                        },
                        icon: 'app/main/images/launcher/content.png'
                    },
                    {
                        id: 'app-300',
                        text: 'Campaigns',
                        cms: {
                            appUrl: 'blank.html'
                        },
                        icon: 'app/main/images/launcher/campaigns.png'
                    },
                    {
                        id: 'app-500',
                        text: 'Search',
                        cms: {
                            appUrl: 'blank.html'
                        },
                        icon: 'app/main/images/launcher/search.png'
                    },
                    {
                        id: 'app-600',
                        text: 'Segments',
                        cms: {
                            appUrl: 'blank.html'
                        },
                        icon: 'app/main/images/launcher/segments.png'
                    },
                    {
                        id: 'app-610',
                        text: 'Commerce',
                        cms: {
                            appUrl: 'blank.html'
                        },
                        icon: 'app/main/images/launcher/commerce.png'
                    },
                    {
                        id: 'app-620',
                        text: 'Optimizer',
                        cms: {
                            appUrl: 'blank.html'
                        },
                        icon: 'app/main/images/launcher/optimizer.png'
                    },
                    {
                        id: 'app-630',
                        text: 'Social',
                        cms: {
                            appUrl: 'blank.html'
                        },
                        icon: 'app/main/images/launcher/social.png'
                    },
                    {
                        id: 'app-635',
                        text: 'Chat',
                        cms: {
                            appUrl: 'blank.html'
                        },
                        icon: 'app/main/images/launcher/chat.png'
                    },
                    {
                        id: 'app-700',
                        text: 'Calendar',
                        cms: {
                            appUrl: 'blank.html'
                        },
                        icon: 'app/main/images/launcher/calendar.png'
                    },
                    '-',
                    {
                        id: 'app-800',
                        text: 'System',
                        cms: {
                            appUrl: 'app-system.html'
                        },
                        icon: 'app/main/images/launcher/system.png',
                        menu: {
                            items: [
                                {
                                    id: 'app-810',
                                    text:"WebDAV",
                                    cms: {
                                        appUrl:"blank.html"
                                    },
                                    icon: 'app/main/images/launcher/webdav.png'
                                },
                                {
                                    id: 'app-820',
                                    text: "Applications",
                                    cms: {
                                        appUrl:"blank.html"
                                    },
                                    icon: 'app/main/images/launcher/applications.png'
                                },
                                {
                                    id: 'app-830',
                                    text: 'Cluster',
                                    cms: {
                                        appUrl: 'blank.html'
                                    },
                                    icon: 'app/main/images/launcher/cluster.png'
                                },
                                {
                                    id: 'app-840',
                                    text: 'Live Trace',
                                    cms: {
                                        appUrl: 'blank.html'
                                    },
                                    icon: 'app/main/images/launcher/live_trace.png'
                                },
                                {
                                    id: 'app-850',
                                    text: 'Sites',
                                    cms: {
                                        appUrl: 'blank.html'
                                    },
                                    icon: 'app/main/images/launcher/sites.png'
                                },
                                {
                                    id: 'app-860',
                                    text: 'Userstores',
                                    cms: {
                                        appUrl: 'app-userstore.html'
                                    },
                                    icon: 'app/main/images/launcher/userstores.png'
                                },
                                {
                                    id: 'app-860',
                                    text: 'Cache',
                                    cms: {
                                        appUrl: 'app-systemCache.html'
                                    },
                                    icon: 'app/main/images/launcher/cache.png'
                                },
                                {
                                    id: 'app-870',
                                    text: 'Content Types',
                                    cms: {
                                        appUrl: 'app-contentType.html'
                                    },
                                    icon: 'app/main/images/launcher/content_types.png'
                                }
                            ]
                        }
                    }
                ]
            }
        },
        '->',
        // Application search
        {
            xtype: 'textfield',
            name: 'app-search',
            width: 150
        },
        {
            xtype: 'button',
            icon: 'app/main/images/launcher/magnifying_glass.png'
        },
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


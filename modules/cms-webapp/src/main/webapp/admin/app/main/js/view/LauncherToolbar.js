Ext.define('App.view.LauncherToolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.launcherToolbar',
    id: 'app-launcher-toolbar',

    requires: ['App.view.LoggedInUserButton',
                'Common.view.MegaMenu'],

    height: 42,

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
            scale: 'medium',
            text: 'Dashboard',
            menu: Ext.create( 'Common.view.MegaMenu', {
                maxColumns: 6,
                url: 'app/main/data/megaMenu.json'
            } )
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

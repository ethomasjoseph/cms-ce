Ext.define('App.controller.LauncherToolbarController', {
    extend: 'Ext.app.Controller',

    views: ['LauncherToolbar'],

    requires: [
        'App.LauncherToolbarHelper'
    ],

    init: function()
    {
        this.control(
            {
                'viewport': {
                    afterrender: this.loadDefaultApp
                },
                'launcherToolbar *[id=app-launcher-logo]': {
                    render: this.onLogoRendered
                },
                'launcherToolbar *[itemId=app-launcher-button] menu > menuitem': {
                    click: this.loadApp
                },
                'launcherToolbar *[itemId=launcher-feedback-test-button]': {
                    click: function() {
                        this.application.fireEvent('feedbackWindow.show', 'Feedback from App Launcher', 'App Launcher says Hello World!');
                    }
                }
            }
        );
    },

    loadDefaultApp: function( component, options )
    {
        var defaultApplication = this.getStartMenuButton().menu.items.items[0];
        this.loadApp( defaultApplication, null, null );
    },

    loadApp: function( item, e, options )
    {
        if ( !window.appLoadMask )
        {
            window.appLoadMask = new Ext.LoadMask( Ext.getDom( 'main-viewport-center' ), {msg:"Please wait..."} );
        }

        if ( item.cms.appUrl === '' )
        {
            item.cms.appUrl = 'blank.html'
        }

        if ( !item.icon || item.icon === '' )
        {
            item.icon = Ext.BLANK_IMAGE_URL
        }

        this.showLoadMask();
        this.setDocumentTitle( item.text );
        this.setUrlFragment( item.text );
        this.getIframe().src = item.cms.appUrl;
        this.updateStartButton( item );
    },

    updateStartButton: function( item )
    {
        var startMenuButton = this.getStartMenuButton();
        startMenuButton.setText( item.text );
        startMenuButton.setIcon( item.icon );
    },

    setDocumentTitle: function( title )
    {
        window.document.title = 'Enonic CMS Admin - ' + title;
    },

    setUrlFragment: function( fragmentId )
    {
        window.location.hash = fragmentId;
    },

    getIframe: function()
    {
        return Ext.getDom( 'main-iframe' );
    },

    showAboutWindow: function()
    {
        var aboutWindow = Ext.ComponentQuery.query('#cms-about-window')[0];
        if (aboutWindow)
        {
            aboutWindow.show();
            return;
        }

        Ext.create('Ext.window.Window', {
            itemId: 'cms-about-window',
            modal: true,
            resizable: false,
            title: 'About',
            width: 550,
            height: 300
        }).show();
    },

    showLoadMask: function()
    {
        window.appLoadMask.show();
    },

    onLogoRendered: function( component, options )
    {
        component.el.on( 'click', this.showAboutWindow );
    },

    getStartMenuButton: function()
    {
        return Ext.ComponentQuery.query('launcherToolbar button[itemId=app-launcher-button]')[0];
    }

});

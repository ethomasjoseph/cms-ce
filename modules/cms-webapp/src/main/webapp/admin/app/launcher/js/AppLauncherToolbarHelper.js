Ext.define('App.AppLauncherToolbarHelper', {
     statics: {
         hideLauncherMenus: function() {
             var componentQuery = Ext.ComponentQuery;
             var toolbarMenuButtons = componentQuery.query('appLauncherToolbar button[menu]');
             var loggedInUserButton = componentQuery.query('appLauncherToolbar loggedInUserButton')[0];

             var menu = null;
             for (var i = 0; i < toolbarMenuButtons.length; i++) {
                 menu = toolbarMenuButtons[i].menu;
                 if (menu.isVisible(true)) {
                    menu.hide();
                 }
             }

             loggedInUserButton.toggle(false);
         }

     },

     constructor: function() { }
});
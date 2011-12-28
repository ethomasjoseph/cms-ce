Ext.define( 'App.controller.AccountController', {
    extend: 'App.controller.AdminBaseController',

    stores: ['AccountStore'],
    models: ['AccountModel'],
    views: ['AccountGridPanel'],

    init: function()
    {
    }

} );
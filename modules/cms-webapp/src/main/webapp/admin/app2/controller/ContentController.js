Ext.define( 'App.controller.ContentController', {
    extend: 'App.controller.AdminBaseController',

    stores: ['ContentStore'],
    models: ['ContentModel'],
    views: ['ContentMainPanel'],

    init: function()
    {
    }

} );
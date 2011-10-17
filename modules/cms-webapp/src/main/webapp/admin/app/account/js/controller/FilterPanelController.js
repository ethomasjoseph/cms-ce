Ext.define( 'App.controller.FilterPanelController', {
    extend: 'Ext.app.Controller',

    stores: [
        'UserStore'
    ],
    models: [
        'UserModel'
    ],
    views: [],

    init: function()
    {
        this.control(
            {
                'accountFilter': {
                    specialkey: this.filterHandleEnterKey,
                    render: this.onFilterPanelRender
                },
                'accountFilter button[action=search]': {
                    click: this.searchFilter
                }
            }
        );
    },

    onFilterPanelRender: function()
    {
        Ext.getCmp( 'filter' ).focus( false, 10 );
    },

    searchFilter: function()
    {
        var usersStore = this.getUserStoreStore();
        var textField = this.getFilterTextField();

        usersStore.clearFilter();
        usersStore.load( {params:{query: textField.getValue()}} );
    },

    filterHandleEnterKey: function( field, event )
    {
        console.log(field);
        if ( event.getKey() == event.ENTER )
        {
            this.searchFilter();
        }
    },

    getFilterTextField: function()
    {
        return Ext.ComponentQuery.query( 'accountFilter textfield[name=filter]' )[0];
    }

} );

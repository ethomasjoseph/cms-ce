Ext.define( 'App.view.BrowseToolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias : 'widget.browseToolbar',

    border: false,

    initComponent: function()
    {
        var buttonDefaults = {
            scale: 'medium',
            iconAlign: 'top',
            minWidth: 64
        };

        this.items = [
            {
                xtype: 'buttongroup',
                columns: 1,
                defaults: buttonDefaults,
                items: [
                    {
                        xtype: 'splitbutton',
                        text: ' New',
                        action: 'newUser',
                        iconCls: 'icon-add-24',
                        cls: 'x-btn-as-arrow',
                        menu: Ext.create( 'Common.view.MegaMenu', {
                            maxColumns: 1,
                            tableAttrs: {
                                style: {
                                    width: '100%'
                                }
                            },
                            url: 'app/account/js/data/accountMenu.json'
                        } )
                    }
                ]
            },
            {
                xtype: 'buttongroup',
                columns: 3,
                defaults: buttonDefaults,
                items: [
                    {
                        text: 'Edit',
                        action: 'edit',
                        iconCls: 'icon-edit-user-24',
                        disableOnMultipleSelection: true
                    },
                    {
                        text: 'Delete',
                        action: 'showDeleteWindow',
                        iconCls: 'icon-delete-user-24',
                        disableOnMultipleSelection: false
                    }
                ]
            },
            {
                xtype: 'buttongroup',
                columns: 1,
                defaults: buttonDefaults,
                items: [
                    {
                        text: 'Change Password',
                        action: 'changePassword',
                        iconCls: 'icon-change-password-24',
                        disableOnMultipleSelection: true
                    }
                ]
            },
            {
                xtype: 'buttongroup',
                columns: 1,
                defaults: buttonDefaults,
                items: [
                    {
                        text: 'View',
                        action: 'viewUser',
                        iconCls: 'icon-user-24',
                        disableOnMultipleSelection: true
                    }
                ]
            }
        ];

        this.callParent( arguments );
    }

} );

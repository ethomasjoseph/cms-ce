Ext.define( 'App.view.AddressContainer', {
    extend: 'Ext.form.FieldSet',
    alias: 'widget.addressContainer',

    width: '100%',
    title: 'Address',
    padding: 10,
    cls: 'addresses',

    dropConfig: undefined,

    requires: [
        "App.view.AddressDropTarget",
        "App.view.AddressColumn"
    ],

    initComponent: function()
    {
        var button = {
            xtype: 'button',
            text: 'Add New Address',
            action: 'addNewTab',
            currentUser: this.currentUser
        };
        var addresses =  this.items;
        this.items = [
            {
                xtype: 'addressColumn',
                items: addresses
            },
            button
        ];
        this.callParent( arguments );
        this.addEvents({
            validatedrop: true,
            beforedragover: true,
            dragover: true,
            beforedrop: true,
            drop: true
        });
        this.on("drop", this.doLayout, this);
    },

    initEvents: function ()
    {
        this.callParent();
        this.dd = Ext.create("App.view.AddressDropTarget", this, this.dropConfig);
    },

    beforeDestroy: function () {
        if (this.dd) {
            this.dd.unreg()
        }
        this.callParent();
    }

} );
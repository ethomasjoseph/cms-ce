Ext.define( 'App.view.wizard.UserWizardToolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias : 'widget.userWizardToolbar',

    border: false,

    isNewUser: true,

    initComponent: function()
    {

        var buttonDefaults = {
            scale: 'medium',
            iconAlign: 'top',
            minWidth: 64
        };

        var saveBtn = {
            xtype: 'buttongroup',
            columns: 1,
            defaults: buttonDefaults,
            items: [
                {
                    text: 'Save',
                    action: 'saveNewUser',
                    itemId: 'save',
                    iconCls: 'icon-btn-save-24'
                }
            ]};
        var deleteBtn = {
            xtype: 'buttongroup',
            columns: 1,
            defaults: buttonDefaults,
            items: [
                {
                    text: 'Delete',
                    action: 'showDeleteWindow',
                    iconCls: 'icon-delete-user-24',
                    disabled: this.isNewUser
                }
            ]};
        var changePasswordBtn = {
            xtype: 'buttongroup',
            columns: 1,
            defaults: buttonDefaults,
            items: [
                {
                    text: 'Change Password',
                    action: 'changePassword',
                    iconCls: 'icon-change-password-24',
                    disabled: this.isNewUser
                }
            ]};
        if ( this.isNewUser )
        {
            this.items = [ saveBtn ];
        }
        else
        {
            this.items = [ saveBtn, deleteBtn, changePasswordBtn];
        }
        this.callParent( arguments );
    }

} );

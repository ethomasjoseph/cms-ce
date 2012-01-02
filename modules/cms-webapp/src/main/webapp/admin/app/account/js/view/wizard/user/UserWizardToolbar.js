Ext.define( 'App.view.wizard.user.UserWizardToolbar', {
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
                    disabled: true,
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
                    iconCls: 'icon-delete-user-24'
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
                    iconCls: 'icon-change-password-24'
                }
            ]};
        var finishBtn = {
            xtype: 'buttongroup',
            hidden: true,
            columns: 1,
            itemId: 'finish',
            defaults: buttonDefaults,
            items: [{
                    text: 'Finish',
                    action: 'wizardFinish',
                    iconCls: 'icon-ok-24'
                }
            ]};
        if ( this.isNewUser )
        {
            this.items = [ saveBtn, '->', finishBtn ];
        }
        else
        {
            this.items = [ saveBtn, deleteBtn, changePasswordBtn, '->', finishBtn];
        }
        this.callParent( arguments );
    }

} );

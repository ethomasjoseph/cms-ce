Ext.define('App.view.wizard.user.UserStoreListPanel', {
    extend: 'Ext.view.View',
    alias : 'widget.userStoreListPanel',
    border: false,
    store: 'UserstoreConfigStore',

    initComponent: function() {
        this.tpl = Templates.account.userstoreRadioButton;
        this.itemSelector = 'div.cms-userstore';
        this.callParent(arguments);
    },

    getData: function(){
        if (this.selectedUserStore){
            return {
                userStore: this.selectedUserStore.get('name')
            };
        }else{
            return undefined;
        }
    },

    setData: function(record){
        this.selectedUserStore = record;
    }

});
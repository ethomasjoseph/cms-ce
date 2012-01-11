Ext.define( 'App.view.wizard.group.WizardStepMembersPanel', {
    extend: 'Ext.form.Panel',
    alias : 'widget.wizardStepMembersPanel',

    requires: [ 'Common.BoxSelect' ],
    border: false,

    initComponent: function()
    {
        this.items = [
            {
                xtype: 'fieldset',
                title: 'Members',
                padding: '10px 15px',
                defaults: {
                    width: 600
                },
                items: [
                    {
                        fieldLabel: 'Select members',
                        allowBlank: true,
                        minChars: 1,
                        forceSelection : true,
                        triggerOnClick: true,
                        typeAhead: true,
                        xtype:'boxselect',
                        cls: 'cms-groups-boxselect',
                        resizable: false,
                        name: 'members',
                        itemId: 'members',
                        store: new App.store.AccountStore(),
                        mode: 'local',
                        displayField: 'name',
                        itemClassResolver: function (values)
                        {
                            if (values.type === 'user' && !values.builtIn)
                            {
                                return 'cms-user-item';
                            }
                            if (values.type === 'role' || values.builtIn)
                            {
                                return 'cms-role-item';
                            }
                            else
                            {
                                return 'cms-group-item';
                            }
                        },
                        listConfig: {
                            getInnerTpl: function()
                            {
                                return Templates.common.groupList;
                            }

                        },
                        valueField: 'key',
                        growMin: 75,
                        hideTrigger: true,
                        pinList: false,
                        labelTpl: '<tpl if="type==\'user\'">{displayName} ({qualifiedName})</tpl>' +
                                  '<tpl if="type!=\'user\'">{name} ({userStore})</tpl>'
                    }
                ]
            }
        ];

        this.callParent( arguments );
        
        if (this.modelData && this.modelData.members)
        {
            var members = this.down('#members');
            var memberKeys = [];
            Ext.Array.each(this.modelData.members, function (member)
            {
                Ext.Array.include(memberKeys, member.key);
            });
            members.setValue(memberKeys);
        }
    },

    getData: function()
    {
        var selectBox = this.down( 'comboboxselect' );
        var values = selectBox.valueModels;
        var groupsSelected = [];
        Ext.Array.each( values, function(group) {
            var group = {key :group.data.key, name:group.data.name, userStore:group.data.userStore};
            groupsSelected.push(group);
        });
        var userData = { members: groupsSelected };
        return userData;
    }
} );

Ext.define( 'App.view.wizard.group.WizardStepMembersPanel', {
    extend: 'Ext.form.Panel',
    alias : 'widget.wizardStepMembersPanel',

    requires: [ 'Common.BoxSelect' ],
    border: false,

    initComponent: function()
    {
        var memberKeys = [];
        if (this.modelData && this.modelData.members)
        {
            Ext.Array.each(this.modelData.members, function (member)
            {
                Ext.Array.include(memberKeys, member.key);
            });
        }
        var membersList = {
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
                        value: memberKeys,
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
                    };
        var formItems = []
        if (this.modelData && this.modelData.type === 'role')
        {
            var roleDescription = this.getRoleDescription(this.modelData.name);
            var descriptionItem = {
                xtype: 'displayfield',
                fieldLabel: 'Description',
                value: roleDescription
            }
            formItems = [descriptionItem, membersList];
        }
        else
        {
            formItems = [membersList];
        }
        this.items = [
            {
                xtype: 'fieldset',
                title: 'Members',
                padding: '10px 15px',
                defaults: {
                    width: 600
                },
                items: formItems
            }
        ];

        this.callParent( arguments );
        this.down( '#members' ).getStore().sort('type', 'ASC'); // show group accounts first
        
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
    },

    //TODO: Should be replaced, better move to some kind of service
    getRoleDescription: function(name)
    {
        if (name === 'Contributors')
        {
            return 'Sed at commodo arcu. Integer mattis lorem pharetra ligula dignissim. ';
        }
        if (name === 'Developers')
        {
            return 'Curabitur suscipit condimentum ultrices. Nam dolor sem, suscipit ac faucibus. ';
        }
        if (name === 'Enterprise Administrators')
        {
            return 'Mauris pellentesque diam in ligula pulvinar luctus. Donec ac elit. ';
        }
        if (name === 'Expert Contributors')
        {
            return 'Morbi vulputate purus non neque dignissim eu iaculis sapien auctor. ';
        }
        return '';
    }
} );

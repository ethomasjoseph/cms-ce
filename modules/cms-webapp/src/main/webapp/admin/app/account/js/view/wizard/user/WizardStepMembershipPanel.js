Ext.define( 'App.view.wizard.user.WizardStepMembershipPanel', {
    extend: 'Ext.form.Panel',
    alias : 'widget.wizardStepMembershipPanel',

    requires: [ 'Common.BoxSelect' ],
    border: false,

    initComponent: function()
    {
        this.items = [
            {
                xtype: 'fieldset',
                title: 'Member of',
                padding: '10px 15px',
                items: [
                    {
                        allowBlank:true,
                        minChars: 1,
                        forceSelection : true,
                        triggerOnClick: false,
                        typeAhead: true,
                        xtype:'boxselect',
                        cls: 'cms-groups-boxselect',
                        resizable: false,
                        name: 'memberships',
                        anchor:'100%',
                        store: 'GroupStore',
                        mode: 'local',
                        displayField: 'name',
                        itemClassResolver: function (values)
                        {
                            if (values.type === 'role')
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
                        labelTpl: '{name} ({userStore})',
                        listeners: {
                            afterrender: function( component, eOpts )
                            {
                                // Fix for BoxSelect's missing "focus on click" behaviour.
                                // The element that looks like a text area is not actually a text area but a DIV element containing a borderless textfield for input. Hence the extending of the Combo box.
                                // In order to focus on the component we have to add a click listener to the element for the component and set focus on the buried text field in the callback.
                                // TODO: Make a feature request.
                                var element = Ext.get( component.getEl() );
                                element.on( 'click', function()
                                {
                                    element.child( '* input', true ).focus();
                                }, this, {capture: true} )
                            },
                            scope: this
                        }
                    },
                    {
                        xtype: 'button',
                        action: 'newGroup',
                        iconCls: 'icon-group-add-24',
                        iconAlign: 'left',
                        scale: 'medium',
                        width: 'auto',
                        text: 'New'
                    }
                ]
            }
        ];

        this.callParent( arguments );

        if ( this.groups )
        {
            var selectBox = this.down( 'comboboxselect' );
            var groupKeys = [];
            Ext.Array.each( this.groups, function( group )
            {
                Ext.Array.include( groupKeys, group.key );
            } );
            selectBox.setValue( groupKeys );
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
        var userData = { groups: groupsSelected };
        return userData;
    }

} );
Ext.define( 'App.view.wizard.WizardStepMembershipPanel', {
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
                        forceSelection : false,
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
                        labelTpl: '{name} ({userStore})'
                    }
                ]
            }
        ];

        this.callParent( arguments );
    }

} );

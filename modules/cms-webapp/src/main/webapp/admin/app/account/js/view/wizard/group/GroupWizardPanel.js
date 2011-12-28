Ext.define( 'App.view.wizard.group.GroupWizardPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.groupWizardPanel',
    requires: [
        'Common.WizardPanel',
        'App.view.wizard.group.GroupWizardToolbar',
        'App.view.wizard.group.WizardStepGeneralPanel',
        'App.view.wizard.group.WizardStepMembersPanel',
        'App.view.wizard.group.WizardStepGroupSummaryPanel'
    ],

    layout: 'column',

    border: 0,
    autoScroll: true,

    defaults: {
        border: false
    },

    initComponent: function()
    {
        var me = this;
        var isNew = this.modelData == undefined;
        var displayNameValue = 'Group name';

        var groupWizardHeader = Ext.create( 'Ext.form.Panel', {
            xtype: 'form',
            itemId: 'wizardHeader',
            cls: 'cms-wizard-header-container',
            border: false,
            items: {
                xtype: 'textfield',
                cls: 'cms-display-name',
                anchor: '100%',
                height: 36,
                allowBlank: false,
                value: displayNameValue
            }
        } );

        var groupWizardToolbar = Ext.createByAlias( 'widget.groupWizardToolbar', {
            xtype: 'groupWizardToolbar',
            isNew: isNew
        } );

        me.tbar = groupWizardToolbar;
        me.items = [
            {
                width: 138,
                padding: '5 5 5 5',
                border: false,
                items: [
                    {
                        xtype: 'container',
                        plain: true,
                        width: 128,
                        height: 128,
                        cls: 'icon-group-128'
                    }
                ]
            },
            {
                columnWidth: 1,
                padding: '8 10 10 0',
                defaults: {
                    border: false
                },
                items: [
                    groupWizardHeader,
                    {
                        xtype: 'wizardPanel',
                        showControls: true,
                        validateItems: [
                            groupWizardHeader
                        ],
                        isNew: isNew,
                        items: [
                            {
                                stepNumber: 1,
                                stepTitle: "General",
                                xtype: 'wizardStepGeneralPanel'
                            },
                            {
                                stepNumber: 2,
                                stepTitle: "Members",
                                xtype: 'wizardStepMembersPanel'
                            },
                            {
                                stepNumber: 3,
                                stepTitle: "Summary",
                                xtype: 'wizardStepGroupSummaryPanel'
                            }
                        ]
                    }
                ]
            }
        ];

        this.callParent( arguments );

    }

} );

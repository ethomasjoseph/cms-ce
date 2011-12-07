Ext.define( 'App.view.wizard.group.GroupWizardPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.groupWizardPanel',
    requires: [
        'Common.WizardPanel',
        'App.view.wizard.group.GroupWizardToolbar',
        'App.view.wizard.group.WizardStepGeneralPanel',
        'App.view.wizard.group.WizardStepMembersPanel',
        'App.view.wizard.group.WizardStepGroupSummaryPanel',
        'Common.fileupload.PhotoUploadButton'
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

        me.tbar = {
            xtype: 'groupWizardToolbar',
            isNew: isNew
        };
        me.items = [
            {
                width: 138,
                padding: '5 5 5 5',
                border: false,
                items: [
                    {
                        xtype: 'image',
                        border: false,
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
                    {
                        xtype: 'form',
                        itemId: 'wizardHeader',
                        cls: 'cms-wizard-header-container',
                        onValidityChange: function( valid ) {
                            console.log( 'onvaliditychange = ' + valid );
                            if ( this.owner && this.owner.rendered ) {
                                var form = this.owner.up( 'groupWizardPanel' ).down( 'wizardPanel' ).getLayout().getActiveItem().getForm();
                                if( form ) {
                                    form.onValidityChange( valid && form.isValid() );
                                }
                            }
                        },
                        items: [{
                            xtype: 'textfield',
                            cls: 'cms-display-name',
                            anchor: '100%',
                            height: 46,
                            allowBlank: false,
                            value: displayNameValue
                        }]
                    },
                    {
                        xtype: 'wizardPanel',
                        showControls: true,
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

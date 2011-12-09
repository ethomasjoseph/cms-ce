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
                    {
                        xtype: 'form',
                        itemId: 'wizardHeader',
                        cls: 'cms-wizard-header-container',
                        listeners: {
                            // add validity change event to update validity of the active form
                            validitychange: function( headerForm, valid, opts ) {
                                var activeForm = me.getActiveItemForm();
                                if( activeForm ) {
                                    activeForm.onValidityChange( valid && activeForm.isValid() );
                                }
                            }
                        },
                        items: [{
                            xtype: 'textfield',
                            cls: 'cms-display-name',
                            anchor: '100%',
                            height: 36,
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

        // add validity change event on each form to check the validity of the header as well
        var wizardPanel = me.down( 'wizardPanel' );
        for (var i = 0; i < wizardPanel.items.items.length; i++) {
            var item = wizardPanel.items.items[i];
            var itemForm = Ext.isFunction( item.getForm ) ? item.getForm() : undefined;
            if ( itemForm ) {
                itemForm.on( 'validitychange', function( activeForm, valid, opts ) {
                    var headerForm = me.getHeaderForm();
                    if( headerForm && activeForm.owner.isVisible() ) {
                        activeForm.onValidityChange( valid && headerForm.isValid() );
                    }
                } );
            }
        }

    },

    getHeaderForm: function() {
        return this.down( '#wizardHeader' ).getForm();
    },

    getActiveItemForm: function() {
        return this.down( 'wizardPanel' ).getLayout().getActiveItem().getForm();
    }

} );

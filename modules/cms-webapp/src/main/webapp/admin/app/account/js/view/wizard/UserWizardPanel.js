Ext.define( 'App.view.wizard.UserWizardPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.userWizardPanel',
    requires: [
        'Common.WizardPanel',
        'App.view.wizard.UserStoreListPanel',
        'App.view.wizard.UserWizardToolbar',
        'App.view.EditUserFormPanel',
        'App.view.wizard.WizardStepLoginInfoPanel',
        'App.view.wizard.WizardStepMembershipPanel',
        'App.view.wizard.WizardStepSummaryPanel',
        'Common.fileupload.PhotoUploadButton'
    ],

    layout: 'column',

    border: 0,
    autoScroll: true,

    defaults: {
        border: false
    },

    tbar: {
        xtype: 'userWizardToolbar'
    },

    initComponent: function()
    {
        var me = this;


        me.items = [
            {
                width: 120,
                padding: 10,
                items: [
                    {
                        xtype: 'photoUploadButton',
                        width: 100,
                        height: 100,
                        url: '/admin/data/user/photo',
                        progressBarHeight: 6
                    }
                ]
            },
            {
                columnWidth: 1,
                padding: '10 10 10 0',
                defaults: {
                    border: false
                },
                items: [
                    {
                        xtype: 'panel',
                        cls: 'cms-new-user-header',
                        styleHtmlContent: true,
                        listeners: {
                            afterrender: {
                                fn: function() {
                                    var me = this;
                                    me.getEl().addListener('click', function(event, target, eOpts) {
                                       me.toggleDisplayNameField(event, target);
                                    });
                                },
                                scope: this
                            }
                        },
                        html: Templates.account.newUserPanelHeader
                    },
                    {
                        xtype: 'wizardPanel',
                        showControls: false,
                        items: [
                            {
                                stepNumber: 1,
                                stepTitle: 'Userstore',
                                xtype: 'userStoreListPanel'
                            },
                            {
                                stepNumber: 2,
                                stepTitle: "Profile",
                                itemId: 'userForm',
                                xtype: 'editUserFormPanel',
                                enableToolbar: false
                            },
                            {
                                stepNumber: 3,
                                stepTitle: "Login",
                                xtype: 'wizardStepLoginInfoPanel'
                            },
                            {
                                stepNumber: 4,
                                stepTitle: "Memberships",
                                xtype: 'wizardStepMembershipPanel'
                            },
                            {
                                stepNumber: 5,
                                stepTitle: "Summary",
                                xtype: 'wizardStepSummaryPanel'
                            }
                        ]
                    }
                ]
            }
        ];

        this.callParent( arguments );
    },

    toggleDisplayNameField: function(event, target)
    {
        var clickedElement = new Ext.Element(target);
        var parentToClickedElementIsHeader = clickedElement.findParent('.cms-wizard-header');
        var displayNameField = Ext.DomQuery.select('input.cms-display-name', this.getEl().dom)[0];
        var displayNameFieldElement = new Ext.Element(displayNameField);

        if (parentToClickedElementIsHeader)
        {
            displayNameFieldElement.dom.removeAttribute('readonly');
            displayNameFieldElement.addCls('cms-edited-field');
        }
        else
        {
            displayNameFieldElement.set({readonly: true});
            displayNameFieldElement.removeCls('cms-edited-field');
        }
    },

    resizeFileUpload: function( file )
    {
        file.el.down( 'input[type=file]' ).setStyle( {
            width: file.getWidth(),
            height: file.getHeight()
        } );
    },

    setFileUploadDisabled: function( disable ) {
        //this.uploadForm.setDisabled( disable );
    }

});

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
        'Common.fileupload.PhotoUploadButton',
        'App.view.wizard.WizardStepProfilePanel'
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
                width: 121,
                padding: '5 5 5 5',
                items: [
                    {
                        xtype: 'photoUploadButton',
                        width: 111,
                        height: 111,
                        url: 'data/user/photo',
                        progressBarHeight: 6
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
                        xtype: 'panel',
                        styleHtmlContent: true,
                        listeners: {
                            afterrender: {
                                fn: function()
                                {
                                    var me = this;
                                    me.getEl().addListener( 'click', function( event, target, eOpts ){
                                        me.toggleDisplayNameField( event, target );
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
                                stepTitle: "Profile",
                                xtype: 'wizardStepProfilePanel',
                                enableToolbar: false
                            },
                            {
                                stepNumber: 2,
                                stepTitle: "User",
                                itemId: "userPanel",
                                xtype: 'editUserFormPanel',
                                includedFields: ['username', 'email', 'password', 'repeat-password', 'photo',
                                    'country', 'locale', 'timezone', 'global-position'],
                                enableToolbar: false
                            },
                            {
                                stepNumber: 3,
                                stepTitle: "Places",
                                itemId: 'placesPanel',
                                xtype: 'editUserFormPanel',
                                includedFields: ['address'],
                                enableToolbar: false
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

    toggleDisplayNameField: function( event, target )
    {
        var clickedElement = new Ext.Element( target );
        var parentToClickedElementIsHeader = clickedElement.findParent( '.cms-wizard-header' );
        var displayNameField = Ext.DomQuery.select( 'input.cms-display-name', this.getEl().dom )[0];
        var displayNameFieldElement = new Ext.Element( displayNameField );

        if ( parentToClickedElementIsHeader )
        {
            displayNameFieldElement.dom.removeAttribute( 'readonly' );
            displayNameFieldElement.addCls( 'cms-edited-field' );
        }
        else
        {
            displayNameFieldElement.set( {readonly: true} );
            var value = Ext.String.trim( displayNameFieldElement.getValue() );
            if ( value === '' || value === 'Display Name' )
            {
                displayNameFieldElement.removeCls( 'cms-edited-field' );
            }
        }
    },

    updateQualifiedNameHeader: function( userStoreName )
    {
        Ext.get( 'q-userstore' ).dom.innerHTML = userStoreName + '\\';
        Ext.get( 'q-username' ).dom.innerHTML = '';
    },

    resizeFileUpload: function( file )
    {
        file.el.down( 'input[type=file]' ).setStyle({
             width: file.getWidth(),
             height: file.getHeight()
        });
    },

    setFileUploadDisabled: function( disable )
    {
        //TODO: disable image upload
        //this.uploadForm.setDisabled( disable );
    }

} );

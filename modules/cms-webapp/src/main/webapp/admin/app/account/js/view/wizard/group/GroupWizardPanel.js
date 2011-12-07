Ext.define( 'App.view.wizard.group.GroupWizardPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.groupWizardPanel',
    requires: [
        'Common.WizardPanel',
        'App.view.wizard.group.GroupWizardToolbar',
        'App.view.wizard.group.WizardStepGroupPanel',
        'App.view.wizard.group.WizardStepMembersPanel',
        'App.view.wizard.user.WizardStepSummaryPanel',
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
        var photoUrl;
        var userGroups = [];
        var displayNameValue = 'Display name';
        if ( me.userFields )
        {
            photoUrl = 'data/user/photo?key=' + me.userFields.key;
            userGroups = me.userFields.groups;
            displayNameValue = me.userFields.displayName;
        }

        me.tbar = {
            xtype: 'userWizardToolbar',
            isNewGroup: this.userFields == undefined
        };
        me.items = [
            {
                width: 121,
                padding: '5 5 5 5',
                items: [
                    {
                        xtype: 'photoUploadButton',
                        width: 111,
                        height: 111,
                        photoUrl: photoUrl,
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
                                    me.getEl().addListener( 'click', function( event, target, eOpts )
                                    {
                                        me.toggleDisplayNameField( event, target );
                                    } );
                                },
                                scope: this
                            }
                        },
                        tpl: Templates.account.newUserPanelHeader,
                        data: {
                            value: displayNameValue,
                            isNewGroup: this.userFields == undefined
                        }
                    },
                    {
                        xtype: 'wizardPanel',
                        showControls: true,
                        items: [
                            {
                                stepNumber: 1,
                                stepTitle: "Group",
                                xtype: 'wizardStepGroupPanel'
                            },
                            {
                                stepNumber: 2,
                                stepTitle: "Memberships",
                                xtype: 'wizardStepMembersPanel'
                            },
                            {
                                stepNumber: 3,
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
        file.el.down( 'input[type=file]' ).setStyle( {
                                                         width: file.getWidth(),
                                                         height: file.getHeight()
                                                     } );
    },

    setFileUploadDisabled: function( disable )
    {
        //TODO: disable image upload
        //this.uploadForm.setDisabled( disable );
    },

    renderUserForms: function( userStore )
    {
        var userForms = this.query( 'editUserFormPanel' );
        Ext.Array.each( userForms, function( userForm )
        {
            userForm.renderUserForm( {userStore: userStore} );
        } );
    }

} );

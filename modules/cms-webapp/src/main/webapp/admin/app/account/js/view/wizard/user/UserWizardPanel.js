Ext.define( 'App.view.wizard.user.UserWizardPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.userWizardPanel',
    requires: [
        'Common.WizardPanel',
        'App.view.wizard.user.UserStoreListPanel',
        'App.view.wizard.user.UserWizardToolbar',
        'App.view.EditUserFormPanel',
        'App.view.wizard.user.WizardStepLoginInfoPanel',
        'App.view.wizard.user.WizardStepMembershipPanel',
        'App.view.wizard.user.WizardStepUserSummaryPanel',
        'Common.fileupload.PhotoUploadButton'
    ],

    layout: 'column',

    border: 0,
    autoScroll: true,

    defaults: {
        border: false
    },

    displayNameAutoGenerate: true,

    initComponent: function()
    {
        var me = this;
        var photoUrl;
        var userGroups = [];
        var displayNameValue = 'Display Name';
        me.headerData = {
            value: displayNameValue,
            userstoreName: me.userstore,
            qUserName: me.qUserName,
            isNewUser: this.userFields == undefined,
            edited: false
        };
        if ( me.userFields )
        {
            photoUrl = me.hasPhoto ? 'data/user/photo?key=' + me.userFields.key : 'resources/icons/256x256/dummy-user.png';
            userGroups = me.userFields.groups;
            displayNameValue = me.userFields.displayName;
        }

        me.tbar = {
            xtype: 'userWizardToolbar',
            isNewUser: this.userFields == undefined
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
                        itemId: 'wizardHeader',
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
                        tpl: new Ext.XTemplate(Templates.account.newUserPanelHeader),
                        data: me.headerData
                    },
                    {
                        xtype: 'wizardPanel',
                        showControls: true,
                        items: [
                            {
                                stepNumber: 1,
                                stepTitle: "Profile",
                                itemId: "profilePanel",
                                xtype: 'editUserFormPanel',
                                userFields: me.userFields,
                                enableToolbar: false
                            },
                            {
                                stepNumber: 2,
                                stepTitle: "User",
                                itemId: "userPanel",
                                xtype: 'editUserFormPanel',
                                userFields: me.userFields,
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
                                userFields: me.userFields,
                                enableToolbar: false
                            },
                            {
                                stepNumber: 4,
                                stepTitle: "Memberships",
                                groups: userGroups,
                                xtype: 'wizardStepMembershipPanel'
                            },
                            {
                                stepNumber: 5,
                                stepTitle: "Summary",
                                xtype: 'wizardStepUserSummaryPanel'
                            }
                        ]
                    }
                ]
            }
        ];

        this.callParent( arguments );

        //Render all user forms
        if ( me.userFields && me.userFields.userStore )
        {
            me.renderUserForms( me.userFields.userStore );
        }
        else
        {
            me.renderUserForms( me.userstore );
        }
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
    },

    updateHeader: function( data )
    {
        Ext.apply(this.headerData, data);
        this.down('#wizardHeader').update(this.headerData);
    }

} );

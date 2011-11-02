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

        var userImage = Ext.create( 'Ext.Img', {
            src: 'resources/images/x-user-photo.png',
            width: 100,
            height: 100
        } );

        var uploadForm = this.uploadForm = Ext.create( 'Ext.form.Panel', {
            fileUpload: true,
            disabled: true,
            width: 100,
            height: 100,
            frame: false,
            border: false,
            style: {
                position: 'absolute',
                top: 0,
                left: 0,
                padding: 0,
                margin: 0,
                opacity: 0
            },
            items: [
                {
                    xtype: 'filefield',
                    name: 'photo',
                    buttonOnly: true,
                    hideLabel: true,
                    width: 100,
                    height: 100,
                    buttonConfig: {
                        width: 100,
                        height: 100
                    },
                    listeners: {
                        afterrender: function( imgUpl )
                        {
                            me.resizeFileUpload( imgUpl );
                        },
                        change: function( imgUpl, path, eOpts )
                        {
                            var form = this.up( 'form' ).getForm();
                            var regex = new RegExp( "\.(jpg|jpeg|gif|png|bmp)$" );
                            var isValid = regex.test( path );
                            if ( isValid )
                            {
                                form.submit( {
                                                 url: 'data/user/photo',
                                                 method: 'POST',
                                                 waitMsg: 'Uploading your photo...',
                                                 success: function( form, action )
                                                 {
                                                     userImage.setSrc( action.result.src );
                                                     me.resizeFileUpload( imgUpl );
                                                 },
                                                 failure: function( form, action )
                                                 {
                                                     Ext.Msg.show( {
                                                                       title: 'Failure',
                                                                       msg: 'File was not uploaded.',
                                                                       minWidth: 200,
                                                                       modal: true,
                                                                       icon: Ext.Msg.INFO,
                                                                       buttons: Ext.Msg.OK
                                                                   } );
                                                 }
                                             } );
                            }
                            else
                            {
                                Ext.Msg.alert( "Incorrect file", "Supported files are jpg, jpeg, png, gif and bmp." );
                            }
                        }
                    }
                }
            ]
        } );

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
                                stepTitle: "Places",
                                itemId: 'placesPanel',
                                xtype: 'editUserFormPanel',
                                includedFields: ['username', 'email', 'password', 'repeat-password', 'photo',
                                    'country', 'locale', 'timezone', 'global-position'],
                                enableToolbar: false
                            },
                            {
                                stepNumber: 3,
                                stepTitle: "Login",
                                itemId: "loginPanel",
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
            displayNameFieldElement.removeCls( 'cms-edited-field' );
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
    }

} );

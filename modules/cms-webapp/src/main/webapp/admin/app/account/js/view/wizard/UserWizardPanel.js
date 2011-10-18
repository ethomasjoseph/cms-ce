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
        'App.view.wizard.WizardStepFinalizePanel'
    ],

    layout: {
        type: 'hbox',
        align: 'stretch',
        padding: 10
    },

    defaults: {
        border: false
    },

    tbar: {
        xtype: 'userWizardToolbar'
    },

    toggleDisplayNameField: function(event, target){
        var className = target.className;
        if (className && className === 'edit-button'){
            var displayNameField = Ext.get('display-name');
            var readonly = displayNameField.getAttribute('readonly');
            if (readonly){
                displayNameField.dom.removeAttribute('readonly');
                displayNameField.dom.focus();
                displayNameField.dom.select();
            }else{
                displayNameField.set({readonly: true});
            }
            displayNameField.addCls('cms-edited-field');
        }
    },

    onMouseOverHeader: function(event, target){
        var editButton = Ext.get('edit-button');
        if (event.type == 'mouseover'){
            editButton.show();
        }
        if (event.type == 'mouseout'){
            editButton.hide();
        }
    },

    resizeFileUpload: function( file ) {
        file.el.down( 'input[type=file]' ).setStyle( {
            width: file.getWidth(),
            height: file.getHeight()
        } );
    },

    setFileUploadDisabled: function( disable ) {
        this.uploadForm.setDisabled( disable );
    },

    initComponent: function()
    {
        var me = this;

        var userImage = Ext.create( 'Ext.Img', {
            src: 'resources/images/x-user-photo.png',
            width: 100,
            height: 100
        } );

        var uploadForm = this.uploadForm = Ext.create('Ext.form.Panel', {
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
            items: [{
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
                    afterrender: function( imgUpl ) {
                        me.resizeFileUpload( imgUpl );
                    },
                    change: function( imgUpl, path, eOpts ) {
                        var form = this.up('form').getForm();
                        var regex = new RegExp("\.(jpg|jpeg|gif|png|bmp)$");
                        var isValid = regex.test( path );
                        if( isValid )
                        {
                            form.submit( {
                                url: 'data/user/photo',
                                method: 'POST',
                                waitMsg: 'Uploading your photo...',
                                success: function( form, action ) {
                                    userImage.setSrc( action.result.src );
                                    me.resizeFileUpload( imgUpl );
                                },
                                failure: function(form, action) {
                                    Ext.Msg.show({
                                        title: 'Failure',
                                        msg: 'File was not uploaded.',
                                        minWidth: 200,
                                        modal: true,
                                        icon: Ext.Msg.INFO,
                                        buttons: Ext.Msg.OK
                                    });
                                }
                            } );
                        }
                        else
                        {
                            Ext.Msg.alert("Incorrect file", "Supported files are jpg, jpeg, png, gif and bmp.");
                        }
                    }
                }
            }]
        });


        me.items = [
            {
                width: 100,
                items: [
                    userImage,
                    uploadForm
                ]
            },
            {
                flex: 1,
                layout: {
                    type: 'vbox',
                    align: 'stretch',
                    padding: '0 10'
                },
                defaults: {
                    border: false
                },
                items: [
                    {
                        xtype: 'panel',
                        cls: 'cms-new-user-header',
                        styleHtmlContent: true,
                        listeners: {
                            click: {
                                element: 'body',
                                fn: me.toggleDisplayNameField
                            },
                            mouseover: {
                                element: 'body',
                                fn: me.onMouseOverHeader
                            },
                            mouseout: {
                                element: 'body',
                                fn: me.onMouseOverHeader
                            }
                        },
                        // TODO: Move to templates
                        html: '<div class="cms-wizard-header">' +
                            '<div class="clearfix">' +
                                '<div class="cms-left">' +
                                    '<input id="display-name" type="text" value="New User" readonly="true" class="cms-display-name"/>' +
                                '</div>' +
                                '<div class="cms-right">' +
                                    '<a href="javascript:;" id="edit-button" class="edit-button"></a>' +
                                '</div>' +
                            '</div>' +
                            '<p class="clearfix">-User Wizard: <span id="q-userstore"></span><span id="q-username"></span></p>' +
                        '</div>'

                    },
                    {
                        flex: 1,
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
                                stepTitle: "Finalize",
                                xtype: 'wizardStepFinalizePanel'
                            }
                        ]
                    }
                ]
            }
        ];
        this.callParent( arguments );
    }

} );

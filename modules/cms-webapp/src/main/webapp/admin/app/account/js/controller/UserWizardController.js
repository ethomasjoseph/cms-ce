Ext.define( 'App.controller.UserWizardController', {
    extend: 'Ext.app.Controller',

    stores: [
        'UserstoreConfigStore',
        'CountryStore',
        'RegionStore'
    ],
    models: [
        'UserstoreConfigModel',
        'CountryModel',
        'RegionModel'
    ],
    views: [],

    EMPTY_DISPLAY_NAME_TEXT: 'Display Name',
    displayNameAutoGenerate: true,

    init: function()
    {
        this.control( {
                          '*[action=saveNewUser]': {
                              click: this.saveNewUser
                          },
                          '*[action=wizardPrev]': {
                              click: this.wizardPrev
                          },
                          '*[action=wizardNext]': {
                              click: this.wizardNext
                          },
                          'wizardPanel': {
                              beforestepchanged: this.validateStep,
                              stepchanged: this.stepChanged,
                              finished: this.wizardFinished
                          },
                          'userStoreListPanel': {
                              itemclick: this.userStoreSelected
                          },
                          'addressPanel textfield[name=label]': {
                              keyup: this.updateTabTitle
                          },
                          'editUserFormPanel': {
                              fieldsloaded: this.userStoreFieldsLoaded
                          },
                          'userWizardPanel': {
                              afterrender: this.bindDisplayNameEvents
                          }
                      } );
    },

    saveNewUser: function()
    {
        var parentApp = parent.mainApp;
        if ( parentApp )
        {
            parentApp.fireEvent( 'notifier.show', "User was saved",
                                 "Something just happened! Li Europan lingues es membres del sam familie. Lor separat existentie es un myth.",
                                 false );
        }
    },

    validateStep: function( wizard, step )
    {
        var data = undefined;
        if ( step.getData )
        {
            data = step.getData();
        }
        if ( data )
        {
            wizard.addData( data );
        }
        return true;
    },

    stepChanged: function( wizard, oldStep, newStep )
    {
        this.getUserWizardPanel().doLayout();
        this.focusFirstField();

        if ( newStep.getXType() == 'wizardStepProfilePanel' )
        {
            // move to 1st step
            this.getUserWizardPanel().setFileUploadDisabled( true );
        }
        // oldStep can be null for first page
        if ( oldStep && oldStep.getXType() == 'userStoreListPanel' )
        {
            // move from 1st step
            this.getUserWizardPanel().setFileUploadDisabled( false );
        }
    },

    wizardFinished: function( wizard, data )
    {
        var tab = wizard.up( 'userWizardPanel' );
        if ( tab )
        {
            tab.close();
        }
        var parentApp = parent.mainApp;
        if ( parentApp )
        {
            parentApp.fireEvent( 'notifier.show', "User was created",
                                 "Something just happened! Li Europan lingues es membres del sam familie. Lor separat existentie es un myth.",
                                 true );
        }
    },

    wizardPrev: function( btn, evt )
    {
        var w = this.getWizardPanel();
        w.prev( btn );
    },

    wizardNext: function( btn, evt )
    {
        var w = this.getWizardPanel();
        w.next( btn );
    },

    userStoreFieldsLoaded: function( target )
    {
        this.getUserWizardPanel().doLayout();
        this.focusFirstField();
        this.bindFormEvents( target );
    },

    userStoreSelected: function( view, record, item )
    {
        view.setData( record );

        var selectedUserStoreElement = new Ext.Element( item );
        var userStoreElements = view.getNodes();
        for ( var i = 0; i < userStoreElements.length; i++ )
        {
            var userStoreElement = new Ext.Element( userStoreElements[i] );
            if ( userStoreElement.id !== selectedUserStoreElement.id )
            {
                userStoreElement.removeCls( 'cms-userstore-active' );
            }
        }

        selectedUserStoreElement.addCls( 'cms-userstore-active' );

        var radioButton = selectedUserStoreElement.down( 'input' );
        radioButton.dom.checked = true;

        var userStoreName = record.get( 'name' );
        var userForms = this.getUserWizardPanel().query( 'editUserFormPanel' );
        Ext.Array.each( userForms, function( userForm )
        {
            var curUser = {userStore: userStoreName};
            userForm.renderUserForm( curUser );
        } );
        this.getUserWizardPanel().updateQualifiedNameHeader( userStoreName );
        var window = view.up( 'window' );
        window.cancelled = false;
        window.close();
    },

    bindFormEvents: function( form )
    {
        var prefix = form.down( '#prefix' );
        var firstName = form.down( '#first-name' );
        var middleName = form.down( '#middle-name' );
        var lastName = form.down( '#last-name' );
        var suffix = form.down( '#suffix' );
        var username = form.down( '#username' );

        if ( prefix )
        {
            prefix.on( 'change', this.profileNameFieldChanged, this );
        }
        if ( firstName )
        {
            firstName.on( 'change', this.profileNameFieldChanged, this );
        }
        if ( middleName )
        {
            middleName.on( 'change', this.profileNameFieldChanged, this );
        }
        if ( lastName )
        {
            lastName.on( 'change', this.profileNameFieldChanged, this );
        }
        if ( suffix )
        {
            suffix.on( 'change', this.profileNameFieldChanged, this );
        }
        if ( username )
        {
            username.on( 'change', this.usernameFieldChanged, this );
        }
    },

    bindDisplayNameEvents: function()
    {
        var displayName = Ext.get( 'cms-display-name' );
        if ( displayName )
        {
            Ext.Element.get( displayName ).on( 'blur', this.displayNameBlur, this );
            Ext.Element.get( displayName ).on( 'focus', this.displayNameFocus, this );
            Ext.Element.get( displayName ).on( 'change', this.displayNameChanged, this );
        }
    },

    hasDefaultDisplayName: function( displayNameInputField )
    {
        var text = Ext.String.trim( displayNameInputField.value );
        return (text === this.EMPTY_DISPLAY_NAME_TEXT);
    },

    setDefaultDisplayName: function( displayNameInputField )
    {
        displayNameInputField.value = this.EMPTY_DISPLAY_NAME_TEXT;
        Ext.Element.get( displayNameInputField ).removeCls( 'cms-edited-field' );
    },

    displayNameFocus: function( event, element )
    {
        if ( this.hasDefaultDisplayName( element ) )
        {
            element.value = '';
        }
    },

    displayNameBlur: function( event, element )
    {
        var text = Ext.String.trim( element.value );
        if ( text === '' )
        {
            var autogeneratedDispName = this.autoGenerateDisplayName();
            if ( autogeneratedDispName === '' )
            {
                this.setDefaultDisplayName( element );
            }
            else
            {
                element.value = autogeneratedDispName;
                Ext.Element.get( element ).addCls( 'cms-edited-field' );
            }
        }
    },

    displayNameChanged: function( event, element )
    {
        if ( element.value === '' )
        {
            this.displayNameAutoGenerate = true;
        }
        else
        {
            this.displayNameAutoGenerate = false;
        }
    },

    autoGenerateDisplayName: function()
    {
        var formPanel = this.getUserWizardPanel().down( 'editUserFormPanel' );
        var prefix = formPanel.down( '#prefix' ) ? Ext.String.trim( formPanel.down( '#prefix' ).getValue() ) : '';
        var firstName = formPanel.down( '#first-name' ) ? Ext.String.trim( formPanel.down( '#first-name' ).getValue() )
                : '';
        var middleName = formPanel.down( '#middle-name' )
                ? Ext.String.trim( formPanel.down( '#middle-name' ).getValue() ) : '';
        var lastName = formPanel.down( '#last-name' ) ? Ext.String.trim( formPanel.down( '#last-name' ).getValue() )
                : '';
        var suffix = formPanel.down( '#suffix' ) ? Ext.String.trim( formPanel.down( '#suffix' ).getValue() ) : '';
        var displayNameValue = prefix + ' ' + firstName + ' ' + middleName + ' ' + lastName + ' ' + suffix;
        return Ext.String.trim( displayNameValue.replace( /  /g, ' ' ) );
    },

    profileNameFieldChanged: function( field )
    {
        if ( !this.displayNameAutoGenerate )
        {
            return;
        }

        var displayNameField = Ext.get( 'cms-display-name' );
        if ( displayNameField )
        {
            var displayNameValue = this.autoGenerateDisplayName();

            if ( displayNameValue !== '' )
            {
                displayNameField.dom.value = displayNameValue;
                displayNameField.addCls( 'cms-edited-field' );
            }
            else
            {
                this.setDefaultDisplayName( displayNameField.dom );
            }
        }
    },

    usernameFieldChanged: function( field )
    {
        Ext.get( 'q-username' ).dom.innerHTML = field.value;
    },

    focusFirstField: function()
    {
        var activeItem = this.getWizardPanel().getLayout().getActiveItem();
        var firstField;
        if ( activeItem && ( firstField = activeItem.down( 'field' ) ) )
        {
            firstField.focus();
        }
    },

    updateTabTitle: function ( field, event )
    {
        var addressPanel = field.up( 'addressPanel' );
        addressPanel.setTitle( field.getValue() );
    },

    getUserWizardPanel: function()
    {
        return Ext.ComponentQuery.query( 'userWizardPanel' )[0];
    },

    getWizardPanel: function()
    {
        return Ext.ComponentQuery.query( 'wizardPanel' )[0];
    }

} );

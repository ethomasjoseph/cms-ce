Ext.define( 'App.controller.UserWizardController', {
    extend: 'Ext.app.Controller',

    stores: [
        'UserstoreConfigStore',
        'CountryStore',
        'RegionStore',
        'TimezoneStore',
        'LocaleStore'
    ],
    models: [
        'UserstoreConfigModel',
        'CountryModel',
        'RegionModel',
        'TimezoneModel',
        'LocaleModel'
    ],
    views: [],

    EMPTY_DISPLAY_NAME_TEXT: 'Display Name',

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
                          'userWizardPanel wizardPanel': {
                              afterrender: this.bindDisplayNameEvents,
                              beforestepchanged: this.validateStep,
                              stepchanged: this.stepChanged,
                              finished: this.wizardFinished,
                              validitychange: this.validityChanged,
                              dirtychange: this.dirtyChanged
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
                          'userFormField': {
                              validitychange: this.userFieldValidityChange
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
        this.focusFirstField();
        var userWizard = wizard.up('userWizardPanel');
        if ( newStep.getXType() === 'wizardStepProfilePanel' )
        {
            // move to 1st step
            userWizard.setFileUploadDisabled( true );
        }
        // oldStep can be null for first page
        if ( oldStep && oldStep.getXType() === 'userStoreListPanel' )
        {
            // move from 1st step
            userWizard.setFileUploadDisabled( false );
        }

        // auto-suggest username
        if ( ( oldStep && oldStep.itemId === 'profilePanel' ) && newStep.itemId === 'userPanel' )
        {
            var formPanel = wizard.down( 'editUserFormPanel' );
            var firstName = formPanel.down( '#first-name' );
            var firstNameValue = firstName ? Ext.String.trim( firstName.getValue() ) : '';
            var lastName = formPanel.down( '#last-name' );
            var lastNameValue = lastName ? Ext.String.trim( lastName.getValue() ) : '';
            var userStoreName = wizard.getData()['userStore'];
            var usernameField = wizard.down('#username')
            if ( firstNameValue || lastNameValue )
            {
                this.autoSuggestUsername(firstNameValue, lastNameValue, userStoreName, usernameField);
            }
        }
    },

    wizardFinished: function( wizard, data )
    {
        data['display-name'] = this.getDisplayNameValue();
        var onUpdateUserSuccess = function( userkey ) {
            var tab = wizard.up( 'userWizardPanel' );
            var isNewUser = true;
            if ( tab )
            {
                isNewUser = tab.isNewUser();
                tab.close();
            }
            var parentApp = parent.mainApp;
            if ( parentApp )
            {
                parentApp.fireEvent( 'notifier.show', "User was created",
                                     "Something just happened! Li Europan lingues es membres del sam familie. Lor separat existentie es un myth.",
                                     {userKey: userkey, newUser: isNewUser, notifyUser: true});
            }
        }
        this.updateUser( data , onUpdateUserSuccess );
    },

    updateUser: function ( userData, onSuccess )
    {
        Ext.Ajax.request( {
              url: 'data/user/update',
              method: 'POST',
              jsonData: userData,
              success: function( response, opts )
              {
                  var serverResponse = Ext.JSON.decode( response.responseText );
                  if ( !serverResponse.success )
                  {
                      Ext.Msg.alert( 'Error', serverResponse.error );
                  }
                  else
                  {
                      onSuccess( serverResponse.userkey );
                  }
              },
              failure: function( response, opts )
              {
                  Ext.Msg.alert( 'Error', 'Unable to update user' );
              }
        } );
    },

    validityChanged: function( wizard, valid )
    {
        var tb = this.getUserWizardToolbar();
        var save = tb.down( '#save' );
        save.setDisabled( !(valid && ( wizard.isWizardDirty || wizard.isNew )) );
    },

    dirtyChanged: function( wizard, dirty )
    {
        var tb = this.getUserWizardToolbar();
        var save = tb.down( '#save' );
        save.setDisabled( !((dirty || wizard.isNew ) && wizard.isWizardValid) );
    },

    wizardPrev: function( btn, evt )
    {
        var w = this.getWizardPanel();
        w.prev( btn );
    },

    wizardNext: function( btn, evt )
    {
        var w = this.getWizardPanel();
        var valid = true
        if ( w.getLayout().getActiveItem().getForm )
        {
            valid = w.getLayout().getActiveItem().getForm().isValid();
        }
        if ( valid )
        {
            w.next( btn );
        }
    },

    userStoreFieldsLoaded: function( target )
    {
        target.up( 'userWizardPanel' ).doLayout();
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
        var tab = {
            id: Ext.id( null, 'new-user-' ),
            title: 'New User',
            iconCls: 'icon-new-user',
            closable: true,
            autoScroll: true,
            userstore: userStoreName,
            xtype: 'userWizardPanel'
        };
        var tabItem = this.getCmsTabPanel().addTab( tab );
        tabItem.down('wizardPanel').addData( {'userStore': userStoreName} );
        var window = view.up( 'window' );
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

    bindDisplayNameEvents: function( wizard )
    {
        var wizardId = wizard.getId();
        var displayName = Ext.query( '#' + wizardId + ' input.cms-display-name' );
        if ( displayName )
        {
            Ext.Element.get( displayName ).on( 'blur', this.displayNameBlur, this, {wizard: wizard} );
            Ext.Element.get( displayName ).on( 'focus', this.displayNameFocus, this );
            Ext.Element.get( displayName ).on( 'change', this.displayNameChanged, this, {wizard: wizard} );
        }
    },

    hasDefaultDisplayName: function( displayNameInputField )
    {
        var text = Ext.String.trim( displayNameInputField.value );
        return (text === this.EMPTY_DISPLAY_NAME_TEXT);
    },

    setDefaultDisplayName: function( wizard )
    {
        this.updateWizardHeader( wizard, {value: this.EMPTY_DISPLAY_NAME_TEXT, edited: false} );
    },

    displayNameFocus: function( event, element )
    {
        if ( this.hasDefaultDisplayName( element ) )
        {
            element.value = '';
        }
    },

    displayNameBlur: function( event, element, opts )
    {
        var text = Ext.String.trim( element.value );
        if ( text === '' )
        {
            var autogeneratedDispName = this.autoGenerateDisplayName( opts.wizard );
            if ( autogeneratedDispName === '' )
            {
                this.setDefaultDisplayName( opts.wizard );
            }
            else
            {
                this.updateWizardHeader( opts.wizard, {value: autogeneratedDispName, edited: true} );
            }
        }
    },

    displayNameChanged: function( event, element, opts )
    {
        if ( element.value === '' )
        {
            opts.wizard.displayNameAutoGenerate = true;
        }
        else
        {
            opts.wizard.displayNameAutoGenerate = false;
        }
    },

    autoGenerateDisplayName: function( wizard )
    {
        var prefix = wizard.down( '#prefix' ) ? Ext.String.trim( wizard.down( '#prefix' ).getValue() ) : '';
        var firstName = wizard.down( '#first-name' ) ? Ext.String.trim( wizard.down( '#first-name' ).getValue() ) : '';
        var middleName = wizard.down( '#middle-name' ) ? Ext.String.trim( wizard.down( '#middle-name' ).getValue() )
                : '';
        var lastName = wizard.down( '#last-name' ) ? Ext.String.trim( wizard.down( '#last-name' ).getValue() ) : '';
        var suffix = wizard.down( '#suffix' ) ? Ext.String.trim( wizard.down( '#suffix' ).getValue() ) : '';
        var displayNameValue = prefix + ' ' + firstName + ' ' + middleName + ' ' + lastName + ' ' + suffix;
        return Ext.String.trim( displayNameValue.replace( /  /g, ' ' ) );
    },

    getDisplayNameValue: function()
    {
        var userWizard = this.getWizardPanel().up('userWizardPanel');
        var wizardPanelId = userWizard.getId();
        var displayNameField = Ext.query( '#' + wizardPanelId + ' input.cms-display-name' )[0];
        var displayName = displayNameField.value;
        return displayName === this.EMPTY_DISPLAY_NAME_TEXT ? '' : displayName;
    },
    
    profileNameFieldChanged: function( field )
    {
        var userWizard = field.up( 'userWizardPanel' );

        if ( !userWizard.displayNameAutoGenerate )
        {
            return;
        }

        var displayNameValue = this.autoGenerateDisplayName( userWizard );

        if ( displayNameValue !== '' )
        {
            this.updateWizardHeader( userWizard, {value: displayNameValue, edited: true} );
        }
        else
        {
            this.updateWizardHeader( userWizard, {value: this.EMPTY_DISPLAY_NAME_TEXT, edited: false} );
        }
    },

    usernameFieldChanged: function( field )
    {
        var userWizard = field.up( 'userWizardPanel' );
        this.updateWizardHeader( userWizard, {qUserName: field.value} );
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

    userFieldValidityChange: function( field, isValid )
    {
        if ( field.fieldname === 'repeat-password' || field.fieldname === 'password' || field.fieldname === 'username' )
        {
            var repeatPassword = field.up( 'fieldset' ).down( '#repeat-password' );
            var passwordMeter = field.up( 'fieldset' ).down( '#password' );
            if ( repeatPassword )
            {
                repeatPassword.validate();
            }
            if ( passwordMeter )
            {
                passwordMeter.validate();
            }
            field.showGreenMark( isValid );
        }

    },

    updateWizardHeader: function ( wizard, data )
    {
        wizard.updateHeader( data );
        this.bindDisplayNameEvents( wizard );
    },

    updateTabTitle: function ( field, event )
    {
        var addressPanel = field.up( 'addressPanel' );
        addressPanel.setTitle( field.getValue() );
    },

    getWizardPanel: function()
    {
        return Ext.ComponentQuery.query( 'wizardPanel' )[0];
    },

    getUserWizardToolbar: function()
    {
        return Ext.ComponentQuery.query( 'userWizardToolbar' )[0];
    },

    getCmsTabPanel: function()
    {
        return Ext.ComponentQuery.query( 'cmsTabPanel' )[0];
    },

    autoSuggestUsername: function( firstName, lastName, userStoreName, usernameField )
    {
        if ( usernameField.getValue() !== '' )
        {
            return;
        }

        Ext.Ajax.request( {
                              url: 'data/account/suggestusername',
                              method: 'GET',
                              params: {
                                  'firstname': firstName,
                                  'lastname': lastName,
                                  'userstore': userStoreName
                              },
                              success: function( response )
                              {
                                  var respObj = Ext.decode( response.responseText, true );
                                  if ( usernameField.getValue() === '' )
                                  {
                                      usernameField.setValue( respObj.username );
                                  }
                              }
                          } );
    }

} );

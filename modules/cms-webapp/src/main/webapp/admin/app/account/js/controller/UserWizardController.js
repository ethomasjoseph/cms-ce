Ext.define( 'App.controller.UserWizardController', {
    extend: 'Ext.app.Controller',

    stores: ['UserstoreConfigStore'],
    models: ['UserstoreConfigModel'],
    views: [],

    init: function()
    {
        this.control( {
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
                          }
                      } );
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

        if ( newStep.getXType() == 'userStoreListPanel' )
        {
            // move to 1st step
            this.getUserWizardPanel().setFileUploadDisabled( true );
        } else if ( (newStep.getXType() == 'editUserFormPanel') && (oldStep.getXType() == 'userStoreListPanel') )
        {
            var prefix = newStep.down( '#prefix' );
            var firstName = newStep.down( '#first-name' );
            var middleName = newStep.down( '#middle-name' );
            var lastName = newStep.down( '#last-name' );
            var suffix = newStep.down( '#suffix' );
            if ( prefix )
            {
                prefix.on( 'keyup', this.textFieldHandleEnterKey );
            }
            if ( firstName )
            {
                firstName.on( 'keyup', this.textFieldHandleEnterKey );
            }
            if ( middleName )
            {
                middleName.on( 'keyup', this.textFieldHandleEnterKey );
            }
            if ( lastName )
            {
                lastName.on( 'keyup', this.textFieldHandleEnterKey );
            }
            if ( suffix )
            {
                suffix.on( 'keyup', this.textFieldHandleEnterKey );
            }
        }
        if ( oldStep.getXType() == 'userStoreListPanel' )
        {
            // move from 1st step
            var userStore = wizard.getData().userStore;
            Ext.get( 'q-userstore' ).dom.innerHTML = userStore + '\\';
            Ext.get( 'q-username' ).dom.innerHTML = 'unnamed';
            this.getUserWizardPanel().setFileUploadDisabled( false );
        }
    },

    wizardFinished: function( wizard, data )
    {
        this.application.fireEvent('notifier.show', "User created", "Something just happened! Li Europan lingues es membres del sam familie. Lor separat existentie es un myth." );
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

    userStoreFieldsLoaded: function()
    {
        this.getUserWizardPanel().doLayout();
        this.focusFirstField();
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
        var userForms = this.getUserWizardPanel().query( 'editUserFormPanel' );
        Ext.Array.each( userForms, function( userForm )
        {
            userForm.currentUser = {userStore: record.get( 'name' )};
        } );
        var profilePanel = this.getUserWizardPanel().down( 'wizardStepProfilePanel' );
        profilePanel.getLayout().next();
    },

    textFieldHandleEnterKey: function( field, event )
    {
        var formPanel = field.up( 'editUserFormPanel' );
        var prefix = formPanel.down( '#prefix' ) ? Ext.String.trim( formPanel.down( '#prefix' ).getValue() ) : '';
        var firstName = formPanel.down( '#first-name' ) ? Ext.String.trim( formPanel.down( '#first-name' ).getValue() )
                : '';
        var middleName = formPanel.down( '#middle-name' )
                ? Ext.String.trim( formPanel.down( '#middle-name' ).getValue() ) : '';
        var lastName = formPanel.down( '#last-name' ) ? Ext.String.trim( formPanel.down( '#last-name' ).getValue() )
                : '';
        var suffix = formPanel.down( '#suffix' ) ? Ext.String.trim( formPanel.down( '#suffix' ).getValue() ) : '';
        var displayName = Ext.get( 'display-name' );
        if ( displayName )
        {
            var displayNameValue = prefix + ' ' + firstName + ' ' + middleName + ' ' + lastName + ' ' + suffix;
            displayName.dom.value = Ext.String.trim( displayNameValue );
            displayName.addCls( 'cms-edited-field' );
        }
    },

    focusFirstField: function() {
        var activeItem = this.getWizardPanel().getLayout().getActiveItem();
        var firstField;
        if ( activeItem && ( firstField = activeItem.down( 'field' ) ) )
            firstField.focus();
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

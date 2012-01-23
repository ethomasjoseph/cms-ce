Ext.define( 'App.controller.GroupWizardController', {
    extend: 'Ext.app.Controller',

    stores: [],
    models: [],
    views: [],

    init:function () {
        this.control({
            'groupWizardPanel *[action=saveGroup]':{
                click:this.saveGroup
            },
            'groupWizardPanel *[action=deleteGroup]':{
                click:this.deleteGroup
            },
            'groupWizardPanel *[action=finishGroup]':{
                click: function( btn, evt, opts ) {
                    this.saveGroup( btn, evt, opts, true );
                }
            },
            'groupWizardPanel textfield#displayName': {
                keyup: function( field, event ) {
                    var value = field.getValue();
                    var displayNameLabel = field.up('groupWizardPanel').down('#wizardHeader');
                    if (value.trim() == '') {
                        value = 'Display Name';
                    }
                    displayNameLabel.update({
                        displayName: value
                    });
                }
            },
            'groupWizardPanel wizardPanel':{
                beforestepchanged:this.validateStep,
                stepchanged:this.stepChanged,
                finished:this.wizardFinished,
                validitychange: this.validityChanged,
                dirtychange: this.dirtyChanged
            }
        });
    },

    saveGroup: function( btn, evt, opts, closeWizard )
    {
        var groupWizard = btn.up( 'groupWizardPanel' );
        var parentApp = parent.mainApp;
        if ( parentApp )
        {
            parentApp.fireEvent( 'notifier.show', "Group was saved",
                                 "Something just happened! Li Europan lingues es membres del sam familie. Lor separat existentie es un myth.",
                                 false );
        }
        if ( closeWizard ) {
            groupWizard.close();
        }
    },

    deleteGroup: function()
    {
        var parentApp = parent.mainApp;
        if ( parentApp )
        {
            parentApp.fireEvent( 'notifier.show', "Group was deleted",
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
        if ( wizard.isNew ) {
            // in case of new wizard check that step is valid before proceeding
            var pb = wizard.getProgressBar();
            pb.setDisabled( !wizard.isStepValid( newStep ) );
        }
    },

    wizardFinished: function( wizard, data )
    {
        data['name'] = this.getDisplayNameValue();
        var tab = this.getGroupWizardPanel();
        var parentApp = parent.mainApp;

        var onUpdateGroupSuccess = function() {
            if ( tab )
            {
                tab.close();
            }
            if ( parentApp )
            {
                parentApp.fireEvent( 'notifier.show', "Group was created",
                                     "Something just happened! Li Europan lingues es membres del sam familie. Lor separat existentie es un myth.",
                                     true );
            }
        };

        this.updateGroup(data, onUpdateGroupSuccess);
    },

    validityChanged: function( wizard, valid )
    {
        // Need to go this way up the hierarchy in case there are multiple wizards
        var tb = wizard.up('groupWizardPanel').down('groupWizardToolbar');
        var pb = wizard.getProgressBar();
        var save = tb.down( '#save' );
        var finish = wizard.down( '#controls #finish' );
        var conditionsMet = valid && ( wizard.isWizardDirty || wizard.isNew );
        save.setDisabled( !conditionsMet);
        finish.setVisible( conditionsMet );
        if ( !wizard.isNew ) {
            pb.setDisabled( !conditionsMet );
        } else {
            pb.setDisabled( !wizard.isStepValid() );
        }
    },

    dirtyChanged: function( wizard, dirty )
    {
        var tb = wizard.up('groupWizardPanel').down('groupWizardToolbar');
        var pb = wizard.getProgressBar();
        var save = tb.down( '#save' );
        var finish = wizard.down( '#controls #finish' );
        var conditionsMet = (dirty || wizard.isNew ) && wizard.isWizardValid;
        save.setDisabled( !conditionsMet );
        finish.setVisible( conditionsMet );
        if ( !wizard.isNew ) {
            pb.setDisabled( !conditionsMet );
        } else {
            pb.setDisabled( !wizard.isStepValid() );
        }
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

    getGroupWizardPanel: function()
    {
        return this.getCmsTabPanel().getActiveTab();
    },

    getCmsTabPanel: function()
    {
        return Ext.ComponentQuery.query( 'cmsTabPanel' )[0];
    },

    getGroupWizardToolbar: function()
    {
        return Ext.ComponentQuery.query( 'groupWizardToolbar' )[0];
    },

    getWizardPanel: function()
    {
        return Ext.ComponentQuery.query( 'wizardPanel' )[0];
    },

    getDisplayNameValue: function()
    {
        var groupWizard = this.getGroupWizardPanel();
        var generalStep = groupWizard.down('wizardStepGeneralPanel');
        var displayNameFieldValue = generalStep ? generalStep.query('#displayName')[0].value : groupWizard.getData().displayName;

        return displayNameFieldValue;
    },

    updateGroup:function ( groupData, onSuccess )
    {
        Ext.Ajax.request( {
                              url: 'data/group/update',
                              method: 'POST',
                              jsonData: groupData,
                              success:function ( response, opts )
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
                              failure:function ( response, opts )
                              {
                                  Ext.Msg.alert( 'Error', 'Unable to update group' );
                              }
                          } );
    }

} );

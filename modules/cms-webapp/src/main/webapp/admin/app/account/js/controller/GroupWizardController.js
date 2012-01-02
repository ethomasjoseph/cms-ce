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

    },

    wizardFinished: function( wizard, data )
    {
        var tab = wizard.up( 'groupWizardPanel' );
        if ( tab )
        {
            tab.close();
        }
        var parentApp = parent.mainApp;
        if ( parentApp )
        {
            parentApp.fireEvent( 'notifier.show', "Group was created",
                                 "Something just happened! Li Europan lingues es membres del sam familie. Lor separat existentie es un myth.",
                                 true );
        }
    },

    validityChanged: function( wizard, valid )
    {
        // Need to go this way up the hierarchy in case there are multiple wizards
        var tb = wizard.up('groupWizardPanel').down('groupWizardToolbar');
        var save = tb.down( '#save' );
        var finish = tb.down( '#finish' );
        var conditionsMet = valid && ( wizard.isWizardDirty || wizard.isNew );
        save.setDisabled( !conditionsMet);
        finish.setVisible( conditionsMet );
    },

    dirtyChanged: function( wizard, dirty )
    {
        var tb = wizard.up('groupWizardPanel').down('groupWizardToolbar');
        var save = tb.down( '#save' );
        var finish = tb.down( '#finish' );
        var conditionsMet = (dirty || wizard.isNew ) && wizard.isWizardValid;
        save.setDisabled( !conditionsMet );
        finish.setVisible( conditionsMet );
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
        return Ext.ComponentQuery.query( 'groupWizardPanel' )[0];
    },

    getGroupWizardToolbar: function()
    {
        return Ext.ComponentQuery.query( 'groupWizardToolbar' )[0];
    },

    getWizardPanel: function()
    {
        return Ext.ComponentQuery.query( 'wizardPanel' )[0];
    }

} );

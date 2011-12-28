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
            'groupWizardPanel wizardPanel':{
                beforestepchanged:this.validateStep,
                stepchanged:this.stepChanged,
                finished:this.wizardFinished,
                validitychange: this.validityChanged,
                dirtychange: this.dirtyChanged
            }
        });
    },

    saveGroup: function()
    {
        var parentApp = parent.mainApp;
        if ( parentApp )
        {
            parentApp.fireEvent( 'notifier.show', "Group was saved",
                                 "Something just happened! Li Europan lingues es membres del sam familie. Lor separat existentie es un myth.",
                                 false );
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
        var tb = this.getGroupWizardToolbar();
        var save = tb.down( '#save' );
        save.setDisabled( !(valid && ( wizard.isWizardDirty || wizard.isNew )) );
    },

    dirtyChanged: function( wizard, dirty )
    {
        var tb = this.getGroupWizardToolbar();
        var save = tb.down( '#save' );
        save.setDisabled( !((dirty || wizard.isNew ) && wizard.isWizardValid) );
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

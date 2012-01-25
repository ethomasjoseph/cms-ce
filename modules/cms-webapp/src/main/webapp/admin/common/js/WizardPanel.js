Ext.define( 'Common.WizardPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.wizardPanel',

    requires: ['Common.WizardLayout'],

    layout: {
        type: 'wizard',
        animation: 'none'
    },
    cls: 'cms-wizard',
    autoHeight: true,
    defaults: {
        border: false,
        frame: false,
        autoHeight: true
    },
    bodyPadding: 10,

    externalControls: undefined,
    showControls: true,
    data: {},
    isNew : true,

    // items common for all steps that shall be valid for step to be valid
    validateItems: undefined,
    // items common for all steps that shall be disabled if step is invalid
    boundItems: undefined,

    // private, for storing wizard validity and dirty state, to be able to fire change event
    isWizardValid: undefined,
    isWizardDirty: undefined,
    // private, for tracking invalid and dirty items
    dirtyItems: undefined,
    invalidItems: undefined,
    presentationMode: false,


    initComponent: function()
    {
        var wizard = this;
        this.data = {};
        this.dirtyItems = [];
        this.invalidItems = [];

        this.cls += this.isNew ? ' cms-wizard-new' : ' cms-wizard-edit';
        
        if ( this.showControls )
        {
            wizard.bbar = {
                xtype: 'container',
                margin: '10 20',
                height: 40,
                itemId: 'controls',
                defaults: {
                    xtype: 'button',
                    scale: 'medium'
                },
                items: [
                    {
                        itemId: 'prev',
                        iconCls: 'icon-left-24',
                        width: 40,
                        margin: '0 0 0 85',
                        hideMode: 'display',
                        handler: function( btn, evt )
                        {
                            wizard.prev();
                        }
                    },
                    {
                        text: 'Next',
                        itemId: 'next',
                        iconAlign: 'right',
                        margin: '0 0 0 10',
                        formBind: true,
                        iconCls: 'icon-right-24',
                        handler: function( btn, evt )
                        {
                            wizard.next();
                        }
                    },
                    {
                        text: 'Finish',
                        itemId: 'finish',
                        margin: '0 0 0 10',
                        iconCls: 'icon-ok-24',
                        hidden: true,
                        handler: function( btn, evt )
                        {
                            wizard.finish();
                        }
                    }
                ]
            };

        }

        this.dockedItems = [{
            xtype: 'panel',
            dock: 'top',
            cls: 'cms-wizard-toolbar',
            disabledCls: 'cms-wizard-toolbar-disabled',
            itemId: 'progressBar',
            listeners: {
                click: {
                    fn: wizard.changeStep,
                    element: 'body',
                    scope: wizard
                }
            },
            styleHtmlContent: true,
            margin: 0,
            tpl: new Ext.XTemplate( Templates.common.wizardPanelSteps, {

                resolveClsName: function(index, total) {
                    var activeIndex = wizard.items.indexOf( wizard.getLayout().getActiveItem() ) + 1;
                    var clsName = '';

                    if (index == 1) {
                        clsName += 'first '
                    }

                    if (index < activeIndex) {
                        clsName += 'previous '
                    }

                    if (index + 1 == activeIndex) {
                        clsName += 'immediate '
                    }

                    if (index == activeIndex) {
                        clsName += 'current '
                    }

                    if (index > activeIndex) {
                        clsName += 'next '
                    }

                    if (index - 1 == activeIndex) {
                        clsName += 'immediate '
                    }

                    if (index == total) {
                        clsName += 'last '
                    }

                    return clsName;
                }
            })
        }];

        this.callParent( arguments );
        this.updateProgress();
        this.addEvents(
                "beforestepchanged",
                "stepchanged",
                "animationstarted",
                "animationfinished",
                'validitychange',
                'dirtychange',
                "finished"
        );
        this.on( {
            animationstarted: this.onAnimationStarted,
            animationfinished: this.onAnimationFinished
        } );

        if( !wizard.boundItems ) {
            wizard.boundItems = [];
        }
        if ( this.showControls ) {
            var controls = this.getDockedComponent('controls');
            this.boundItems.push( controls.down( '#finish' ) );
        }

        if( !wizard.validateItems ) {
            wizard.validateItems = [];
        }

        // bind afterrender events
        this.on( 'afterrender', this.bindItemListeners );
        this.on( 'render', function(cmp)
        {
            cmp.items.each( function(item){
                if (!item.alwaysKeep && item.getForm && (item.getForm().getFields().getCount() == 0))
                {
                    cmp.remove(item);
                }
            });
        });

    },

    bindItemListeners: function( cmp ) {

        for ( var i = 0; i < cmp.validateItems.length; i++ ) {
            var validateItem =  cmp.validateItems[i];
            if( validateItem ) {
                var validateItemForm = Ext.isFunction( validateItem.getForm ) ? validateItem.getForm() : undefined;
                if ( validateItemForm ) {
                    // replace component with its form for convenience
                    Ext.Array.replace( this.validateItems, i, 1, [validateItemForm] );
                    validateItemForm.on( {
                        'validitychange': cmp.handleValidityChange,
                        'dirtychange': cmp.handleDirtyChange,
                        scope: cmp
                    }, this );
                    if( validateItemForm.hasInvalidField() ) {
                        Ext.Array.include( this.invalidItems, validateItemForm );
                    }
                }
            }
        }

        for ( i = 0; i < cmp.items.items.length; i++ ) {
            var item = cmp.items.items[i];
            if (i == 0)
            {
                cmp.onAnimationFinished( item, null );
            }
            this.getBoundItems = this.getWizardBoundItems;

            var itemForm = Ext.isFunction( item.getForm ) ? item.getForm() : undefined;
            if ( itemForm ) {
                itemForm.on( {
                    'validitychange': cmp.handleValidityChange,
                    'dirtychange': cmp.handleDirtyChange,
                    scope: cmp
                } );
                if( itemForm.hasInvalidField() ) {
                    Ext.Array.include( this.invalidItems, itemForm );
                }
            }
        }

    },

    getWizardBoundItems: function() {
        var boundItems = this._boundItems;
        if ( !boundItems && this.owner.rendered ) {
            boundItems = this._boundItems = Ext.create('Ext.util.MixedCollection');
            boundItems.addAll(this.owner.query('[formBind]'));

            var wizard = this.owner.up( 'wizardPanel' );
            boundItems.addAll( wizard.boundItems );
        }
        return boundItems;
    },

    handleValidityChange: function( form, valid, opts ) {

        if ( form.owner.rendered && form.owner.isVisible() ) {

            if( !valid ) {
                Ext.Array.include( this.invalidItems, form );
            } else {
                Ext.Array.remove( this.invalidItems, form );
            }

            var isWizardValid = this.invalidItems.length == 0;
            if( this.isWizardValid != isWizardValid ) {
                // fire the wizard validity change event
                this.isWizardValid = isWizardValid;
                this.fireEvent('validitychange', this, isWizardValid);
            }
        }

    },

    handleDirtyChange: function( form, dirty, opts ) {

        if ( form.owner.rendered && form.owner.isVisible() ) {
            if( dirty ) {
                Ext.Array.include( this.dirtyItems, form );
            } else {
                Ext.Array.remove( this.dirtyItems, form );
            }
            var isWizardDirty = this.dirtyItems.length > 0;
            if( this.isWizardDirty != isWizardDirty ) {
                // fire the wizard dirty change event
                this.isWizardDirty = isWizardDirty;
                this.fireEvent('dirtychange', this, isWizardDirty);
            }
        }

    },


    changeStep: function(event, target)
    {
        var progressBar = this.dockedItems.items[0];
        var isNew = this.isNew;
        var isDisabled = progressBar.isDisabled();

        var li = Ext.fly( target ).up( 'li' );

        // allow click only the next immediate step in new mode
        // or any step in edit mode when valid
        // or any except the last in edit when not valid
        // or all previous steps in any mode
        if ( ( !isDisabled && isNew && li.hasCls('next') && li.hasCls( 'immediate' ) )
                || ( !isDisabled && !isNew )
                || ( isDisabled && !isNew && !li.hasCls( 'last' ) )
                || li.hasCls( 'previous' ) ) {
            var step = Number( li.getAttribute( 'wizardStep' ) );
            this.navigate( step - 1 );
        }
        event.stopEvent();
    },

    next: function( btn )
    {
        return this.navigate( "next", btn );
    },

    prev: function( btn )
    {
        return this.navigate( "prev", btn );
    },

    finish: function() {
        this.fireEvent( "finished", this, this.getData()  );
    },

    getNext: function()
    {
        return this.getLayout().getNext();
    },

    getPrev: function()
    {
        return this.getLayout().getPrev();
    },

    navigate: function( direction, btn )
    {
        var oldStep = this.getLayout().getActiveItem();
        if( btn ) {
            this.externalControls = btn.up( 'toolbar' );
        }
        if ( this.fireEvent( "beforestepchanged", this, oldStep ) != false )
        {
            var newStep;
            switch ( direction ) {
                case "-1":
                case "prev":
                    if ( this.getPrev() ) {
                        newStep = this.getLayout().prev();
                    }
                    break;
                case "+1":
                case "next":
                    if ( this.getNext() ) {
                        newStep = this.getLayout().next();
                    } else {
                        this.finish();
                    }
                    break;
                default:
                    newStep = this.getLayout().setActiveItem( direction );
                    break;
            }
        }
    },

    onAnimationStarted: function( newStep, oldStep )
    {
        if ( this.showControls ) {
            // disable internal controls if shown
            this.updateButtons( this.getDockedComponent('controls'), true );
        }
        if ( this.externalControls ) {
            // try to disable external controls
            this.updateButtons( this.externalControls, true );
        }
    },

    onAnimationFinished: function( newStep, oldStep )
    {
        if ( newStep )
        {
            this.updateProgress( newStep );

            this.fireEvent( "stepchanged", this, oldStep, newStep );
            if ( this.showControls ) {
                // update internal controls if shown
                this.updateButtons( this.getDockedComponent('controls') );
            }
            if ( this.externalControls ) {
                // try to update external controls
                this.updateButtons( this.externalControls );
            }
            return newStep;
        }
    },

    updateProgress: function( newStep )
    {
        var progressBar = this.dockedItems.items[0];
        progressBar.update( this.items.items );
    },

    isStepValid: function( step ) {
        var activeForm = step ? step.getForm() : this.getLayout().getActiveItem().getForm();
        if( activeForm ) {
            var isStepValid = Ext.Array.intersect( this.invalidItems, this.validateItems).length == 0;
            isStepValid = !activeForm.hasInvalidField() && isStepValid;
            return isStepValid;
        }
    },

    updateButtons: function( toolbar, disable )
    {
        if( toolbar ) {
            var prev = toolbar.down( '#prev' ),
                next = toolbar.down( '#next' );
            var hasNext = this.getNext(),
                hasPrev = this.getPrev();
            if( prev ) {
                prev.setDisabled( disable || !hasPrev );
                prev.setVisible( hasPrev );
            }
            if( next ) {
                next.setDisabled( disable || !hasNext);
                next.removeCls('cms-prev-button');
                next.removeCls('cms-button');
                next.addCls(hasPrev ? 'cms-prev-button' : 'cms-button');
            }
        }
    },

    addData: function( newValues )
    {
        Ext.merge( this.data, newValues );
    },

    getData: function()
    {
        var me = this;
        me.items.each(function(item){
            if (item.getData)
            {
                me.addData(item.getData());
            }
        });
        return me.data;
    },

    getProgressBar: function() {
        return this.dockedItems.items[0];
    }

} );

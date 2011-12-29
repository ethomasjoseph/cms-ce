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

        if ( this.showControls )
        {
            Ext.each( this.items, function( item, index, all ) {
                var isFirst = index == 0,
                    isLast = ( index == ( all.length - 1 ) );
                item.bbar = {
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
                            margin: '0 0 0 50',
                            hideMode: 'display',
                            hidden: isFirst,
                            handler: function( btn, evt )
                            {
                                wizard.prev();
                            }
                        },
                        {
                            text: isLast ? 'Finish' : 'Next',
                            itemId: isLast ? 'finish' : 'next',
                            iconAlign: 'right',
                            margin: isFirst ? '0 0 0 100' : '0 0 0 10',
                            formBind: true,
                            iconCls: isLast ? 'icon-ok-24' : 'icon-right-24',
                            handler: function( btn, evt )
                            {
                                wizard.next();
                            }
                        }
                    ]
                };

                item.getBoundItems = wizard.getWizardBoundItems;

            });
        }

        this.dockedItems = [{
            xtype: 'panel',
            dock: 'top',
            cls: 'cms-wizard-toolbar',
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
                isCurrent: function( index ) {
                    return wizard.getLayout().getActiveItem().stepNumber == index;
                },

                isPrevious: function( index ) {
                    return wizard.getLayout().getActiveItem().stepNumber -1 == index;
                },

                isNext: function( index ) {
                    return wizard.getLayout().getActiveItem().stepNumber < index;
                },

                resolveClsName: function(index, total) {
                    var clsName = '';
                    if (this.isPrevious(index)) {
                        clsName = 'previous'
                    }

                    if (this.isCurrent(index)) {
                        clsName = 'current'
                    }

                    if (this.isNext(index)) {
                        clsName = 'next'
                    }

                    var isLast = !this.isCurrent(index) && index == total;
                    if (isLast) {
                        clsName = 'last'
                    }

                    var isLastAndCurrent = this.isCurrent(index) && index == total;
                    if (isLastAndCurrent) {
                        clsName = 'current-last'
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
        wizard.boundItems.push( this.down( '#progressBar' ) );

        if( !wizard.validateItems ) {
            wizard.validateItems = [];
        }

        // bind afterrender events

        this.on( 'afterrender', this.bindItemListeners );

        var firstStep = this.items.getCount() > 0 ? this.items.first() : undefined;
        if( firstStep ) {
            firstStep.on('afterrender', function (item) {
                wizard.updateValidity( item );
                wizard.fireEvent('stepchanged', wizard, null, item);
            })
        }

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

            this.updateValidity();

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
        var isDisabled = progressBar.isDisabled();

        var li = Ext.fly( target ).up( 'li' );

        if ( !isDisabled || ( isDisabled && li.hasCls( 'previous' ) )  ){
            var step = Number( li.getAttribute( 'wizardStep' ) );
            this.navigate( step - 1 );
        }
        event.stopEvent()
    },

    next: function( btn )
    {
        return this.navigate( "next", btn );
    },

    prev: function( btn )
    {
        return this.navigate( "prev", btn );
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
                        this.fireEvent( "finished", this, this.getData()  );
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
            this.updateValidity( newStep );
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

    updateValidity: function( step ) {
        if (!this.presentationMode)
        {
            var activeForm = step ? step.getForm() : this.getLayout().getActiveItem().getForm();
            if( activeForm ) {
                var isStepValid = Ext.Array.intersect( this.invalidItems, this.validateItems).length == 0;
                isStepValid = !activeForm.hasInvalidField() && isStepValid;
                activeForm.onValidityChange( isStepValid );
            }
        }

    },

    updateProgress: function( newStep )
    {
        var progressBar = this.dockedItems.items[0];
        progressBar.update( this.items.items );

        var step = newStep || this.getLayout().getActiveItem();
        var form = step instanceof Ext.form.Panel ? step : step.down( 'form' );
        if ( form ) {
            progressBar.setDisabled( form.getForm().hasInvalidField() );
        }
    },

    updateButtons: function( toolbar, disable )
    {
        if( toolbar ) {
            var prev = toolbar.down( '#prev' ),
                next = toolbar.down( '#next' ),
                finish = toolbar.down( '#finish' ),
                save = toolbar.down( '#save' );
            var hasNext = this.getNext(),
                hasPrev = this.getPrev();
            if( prev ) {
                prev.setDisabled( disable || !hasPrev );
            }
            if( next ) {
                if ( finish ) {
                    next.setDisabled( disable || !hasNext );
                    finish.setDisabled( disable || hasNext )
                } else {
                    next.setText( hasNext ? 'Next' : 'Finish' );
                    next.setDisabled( disable );
                }
            }
            if ( save ) {
                save.setDisabled( disable || !hasNext );
            }
        }
    },

    addData: function( newValues )
    {
        Ext.merge( this.data, newValues );
    },

    getData: function()
    {
        return this.data;
    }

} );

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

    initComponent: function()
    {
        var wizard = this;

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

                item.getBoundItems = function() {
                    var boundItems = this._boundItems;
                    if ( !boundItems && this.owner.rendered ) {
                        boundItems = this._boundItems = Ext.create('Ext.util.MixedCollection');
                        boundItems.addAll(this.owner.query('[formBind]'));
                        // also add a top nav to bound items of a current step
                        if ( wizard.getLayout().getActiveItem() == this.owner ) {
                            boundItems.add( this.owner.up( 'wizardPanel' ).down( '#progressBar' ) );
                        }
                    }
                    return boundItems;
                }
            });
        }

        // Add stepchanged event for the first step
        if ( this.items && this.items.length > 0 ) {
            var ls = this.items[0].listeners || {};
            Ext.apply( ls, {
                afterrender: {
                    fn: function( firstStep ) {
                        wizard.fireEvent('stepchanged', wizard, null, firstStep );
                    }
                }
            });
            this.items[0].listeners = ls;
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
        this.addEvents( "beforestepchanged", "stepchanged", "animationstarted", "animationfinished", "finished" );
        this.on( "animationstarted", this.onAnimationStarted );
        this.on( "animationfinished", this.onAnimationFinished );
        this.updateProgress();
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

        var step = newStep || this.getLayout().getActiveItem();
        var form = step instanceof Ext.form.Panel ? step : step.down( 'form' );
        if ( form ) {
            progressBar.setDisabled( !form.getForm().isValid() );
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
        Ext.apply( this.data, newValues );
    },

    getData: function()
    {
        return this.data;
    }

} );

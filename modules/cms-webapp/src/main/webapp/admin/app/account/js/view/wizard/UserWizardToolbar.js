Ext.define( 'App.view.wizard.UserWizardToolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias : 'widget.userWizardToolbar',

    border: false,
    restrainWidth: 10000,

    initComponent: function()
    {

        var buttonDefaults = {
            scale: 'medium',
            iconAlign: 'top',
            minWidth: 64
        };

        this.items = [
            {
                xtype: 'container',
                itemId: 'restrainer',
                layout: 'hbox',
                items: [
                    {
                        xtype: 'buttongroup',
                        columns: 1,
                        defaults: buttonDefaults,
                        items: [
                            {
                                text: 'Save',
                                itemId: 'save',
                                iconCls: 'icon-btn-save-24'
                            }
                        ]
                    },
                    {
                        xtype: 'tbfill'
                    },
                    {
                        xtype: 'buttongroup',
                        columns: 3,
                        defaults: buttonDefaults,
                        items: [
                            {
                                text: 'Previous',
                                iconCls: 'icon-btn-arrow-left-24',
                                itemId: 'prev',
                                disabled: true,
                                action: 'wizardPrev'
                            },
                            {
                                text: 'Next',
                                iconCls: 'icon-btn-arrow-right-24',
                                itemId: 'next',
                                action: 'wizardNext'
                            },
                            {
                                text: 'Finish',
                                iconCls: 'icon-btn-tick-24',
                                disabled: true,
                                itemId: 'finish',
                                action: 'wizardNext'
                            }
                        ]
                    }

                ]
            }
        ];
        this.callParent( arguments );
        Ext.EventManager.onWindowResize( this.updateRestrainWidth, this );
        this.on( 'afterrender', this.updateRestrainWidth );
    },

    updateRestrainWidth: function ()
    {
        var w = this.getWidth() - this.el.getPadding('lr');
        this.down( '#restrainer' ).setWidth( Ext.Array.min( [ w, this.restrainWidth ] ) );
    }

} );

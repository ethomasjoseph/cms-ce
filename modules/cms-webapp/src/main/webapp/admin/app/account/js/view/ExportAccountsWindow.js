Ext.define( 'App.view.ExportAccountsWindow', {
    extend: 'Common.view.BaseDialogWindow',
    alias: 'widget.exportAccountsWindow',

    dialogTitle: 'Export Accounts',
    dialogInfoTpl: false,

    items: [
        {
            xtype: 'form',
            border: false,
            items:[
                {
                    itemId: 'exportType',
                    xtype      : 'radiogroup',
                    fieldLabel : 'Export',
                    defaults: {
                        name: 'exportType',
                        anchor: '100%'
                    },
                    layout: 'anchor',
                    items: [
                        {
                            boxLabel: 'Selection (<span class="count">0</span>)',
                            inputValue: 'selection',
                            checked: true
                        },
                        {
                            boxLabel: 'Search result (<span class="count">0</span>)',
                            inputValue: 'search'
                        }
                    ]
                },
                {
                    xtype: 'container',
                    margin: '10px 0 10px 105px',
                    defaults: {
                        xtype: 'button',
                        scale: 'medium',
                        margin: '0 10 0 0'
                    },
                    items: [
                        {
                            text: 'Cancel',
                            iconCls: 'icon-cancel-24',
                            action: 'close',
                            handler: function()
                            {
                                this.up( 'window' ).close();
                            }
                        },
                        {
                            text: 'Export',
                            formBind: true,
                            iconCls: 'icon-ok-24',
                            handler: function( btn, evt ) {
                                var win = btn.up( 'window' );

                                var type = win.down( '#exportType' );
                                var url = Ext.urlAppend( '/admin/data/account/export', Ext.urlEncode( type.getValue() ) );

                                win.close();
                                window.open( url, "_blank")

                            }
                        }
                    ]
                }
            ]
        }
    ],

    initComponent: function()
    {

        this.callParent( arguments );
    },

    doShow: function( model )
    {
        this.callParent( arguments );
        if ( model )
        {
            var form = this.down('form');

            var counts = Ext.query( 'span.count', form.el.dom );
            counts[0].innerHTML = model.selected.length;
            counts[1].innerHTML = model.searched.length;
        }
    }

} );
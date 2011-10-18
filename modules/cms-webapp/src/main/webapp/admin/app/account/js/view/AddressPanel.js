Ext.define( 'App.view.AddressPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.addressPanel',

    xtype: 'form',
    draggable: true,
    layout: 'anchor',
    bodyPadding: 10,
    defaults: {
        anchor: '100%'
    },

    closeAction: 'hide',

    padding: 10,

    initComponent: function()
    {
        if ( this.values == null )
        {
            this.values = [];
        }
        this.title = this.values['label'] == null ? '[no title]' : this.values['label'];
        var countryField, regionField;
        if ( this.iso )
        {
            var countryStore = Ext.data.StoreManager.lookup( 'CountryStore' );
            var regionStore = new App.store.RegionStore();
            countryField = {
                xtype: 'combobox',
                store: countryStore,
                fieldLabel: 'Country',
                valueField: 'code',
                displayField: 'englishName',
                queryMode: 'local',
                minChars: 1,
                emptyText: 'Please select',
                name: 'iso-country',
                itemId: 'iso-country',
                value: this.values['iso-country'],
                disabled: this.readonly
            };
            regionField = new Ext.form.field.ComboBox( {
                                                           xtype: 'combobox',
                                                           store: regionStore,
                                                           valueField: 'code',
                                                           displayField: 'englishName',
                                                           queryMode: 'local',
                                                           minChars: 1,
                                                           emptyText: 'Please select',
                                                           fieldLabel: 'Region',
                                                           name: 'iso-region',
                                                           itemId: 'iso-region',
                                                           value: this.values['iso-region'],
                                                           disabled: this.values['iso-region'] == null
                                                       } );
            if ( this.values['iso-country'] && this.values['iso-region'] )
            {
                Ext.apply( regionStore.proxy.extraParams, {
                    'countryCode': this.values['iso-country']
                } );
                regionStore.load({
                    scope: this,
                    callback: function() {
                        regionField.setValue( this.values['iso-region'] );
                    }
                });
            }
        }
        else
        {
            countryField = {
                xtype: 'textfield',
                fieldLabel: 'Country',
                name: 'country',
                itemId: 'address-country',
                value: this.values['country'],
                disabled: this.readonly
            };
            regionField = {
                xtype: 'textfield',
                fieldLabel: 'Region',
                name: 'region',
                itemId: 'address-region',
                value: this.values['region'],
                disabled: this.readonly
            };
        }
        this.items = [
            {
                xtype: 'textfield',
                fieldLabel: 'Label',
                name: 'label',
                itemId: 'address-label',
                enableKeyEvents: true,
                value: this.values['label'],
                bubbleEvents: ['keyup'],
                anchor: '30%',
                disabled: this.readonly
            },
            {
                xtype: 'textfield',
                fieldLabel: 'Street',
                name: 'street',
                itemId: 'address-street',
                value: this.values['street'],
                disabled: this.readonly
            },
            {
                xtype: 'textfield',
                fieldLabel: 'Postal Code',
                name: 'postal-code',
                anchor: '20%',
                itemId: 'address-postal-code',
                value: this.values['postal-code'],
                disabled: this.readonly
            },
            {
                xtype: 'textfield',
                fieldLabel: 'Postal Address',
                name: 'postal-address',
                anchor: '30%',
                itemId: 'address-postal-address',
                value: this.values['postal-address'],
                disabled: this.readonly
            },
            countryField,
            regionField
        ];
        this.listeners = {
            beforeclose: {
                fn: function( panel, opts )
                {
                    panel.setDisabled( true );
                }
            }
        };
        this.callParent( arguments );
    }
} );

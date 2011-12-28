Ext.define( 'App.view.EditUserFormPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.editUserFormPanel',

    requires: [
        'App.view.AddressContainer',
        'App.view.AddressPanel',
        'App.view.UserFormField'
    ],

    statics: {
            fieldLabels: {
                'username': 'User Name',
                'email': 'E-mail',
                'password': 'Password',
                'repeat-password': 'Repeat Password',
                'timezone': 'Timezone',
                /* general fields */
                'prefix': 'Prefix',
                'first-name': 'First Name',
                'middle-name': 'Middle Name',
                'last-name': 'Last Name',
                'suffix': 'Suffix',
                'initials': 'Initials',
                'nick-name': 'Nick Name',
                'personal-id': 'Personal ID',
                'member-id': 'Member ID',
                'organization': 'Organisation',
                'birthday': 'Birthday',
                'gender': 'Gender',
                'title': 'Title',
                'description': 'Descripion',
                'html-email': 'E-mail',
                'home-page': 'Web Page',
                'time-zone': 'Time Zone',
                'locale': 'Locale',
                'country': 'Country',
                'global-position': 'Global Position',
                'phone': 'Phone Number',
                'mobile': 'Mobile Number',
                'fax': 'Fax Number',
                'address': 'Address'
            }
    },

    autoScroll: false,
    autoHeight: true,
    border: false,
    currentUser: undefined,
    defaultUserStoreName: 'default',
    enableToolbar: true,

    store: 'UserstoreConfigStore',

    initComponent: function()
    {
        var me = this;
        me.store = Ext.data.StoreManager.lookup( me.store );
        if ( !this.staticFields )
        {
            this.staticFields = [
                {
                    label: 'Username',
                    type: 'username',
                    required: true,
                    remote: false,
                    readonly: false
                },
                {
                    label: 'Password',
                    type: 'password',
                    required: true,
                    remote: false,
                    readonly: false
                },
                {
                    label: 'Repeat password',
                    type: 'repeat-password',
                    required: true,
                    remote: false,
                    readonly: false
                },
                {
                    label: 'E-mail',
                    type: 'email',
                    required: true,
                    vtype: 'email',
                    remote: false,
                    readonly: false
                },
                {
                    label: 'Display name',
                    type: 'display-name',
                    required: true,
                    remote: false,
                    readonly: false
                },
                {
                    "type":"photo",
                    "readOnly":false,
                    "required":false,
                    "remote":false,
                    "iso":true
                },
                {
                    "type":"country",
                    "readOnly":false,
                    "required":false,
                    "remote":false,
                    "iso":true
                },
                {
                    "type":"locale",
                    "readOnly":false,
                    "required":false,
                    "remote":false,
                    "iso":true
                },
                {
                    "type":"timezone",
                    "readOnly":false,
                    "required":false,
                    "remote":false,
                    "iso":true
                },
                {
                    "type":"global-position",
                    "readOnly":false,
                    "required":false,
                    "remote":false,
                    "iso":true
                }
            ];
        }
        if ( !this.excludedFields )
        {
            this.excludedFields = ["username", "email", "country", "global-position", "locale",
                "address", "photo", "password", "repeat-password", "timezone"];
        }

        if ( this.enableToolbar )
        {
            this.dockedItems = [
                {
                    dock: 'top',
                    xtype: 'toolbar',
                    border: false,
                    padding: 5,
                    items: [
                        {
                            text: 'Save',
                            iconCls: 'icon-save',
                            action: 'saveUser'
                        },
                        {
                            text: 'Cancel',
                            action: 'closeUserForm'
                        },
                        '->',
                        {
                            text: 'Delete',
                            iconCls: 'icon-delete-user',
                            action: 'deleteUser'
                        },
                        {
                            text: 'Change Password',
                            iconCls: 'icon-change-password',
                            action: 'changePassword'
                        }
                    ]
                }
            ];
        }
        this.userFieldSet = {
            'username': this.createTextField,
            'email': this.createTextField
        };
        this.securityFieldSet = {
            'password': this.createPasswordField,
            'repeat-password': this.createPasswordField
        };
        this.nameFieldSet = {
            'prefix': this.createTextField,
            'first-name': this.createTextField,
            'middle-name': this.createTextField,
            'last-name': this.createTextField,
            'suffix': this.createTextField,
            'initials': this.createTextField,
            'nick-name': this.createTextField
        };
        this.detailsFieldSet = {
            'personal-id': this.createTextField,
            'member-id': this.createTextField,
            'organization': this.createTextField,
            'birthday': this.createDateField,
            'gender': this.createComboBoxField,
            'title': this.createTextField,
            'description': this.createTextField,
            'html-email': this.createCheckBoxField,
            'homepage': this.createTextField
        };
        this.locationFieldSet = {
            'timezone': this.createComboBoxField,
            'locale': this.createComboBoxField,
            'country': this.createComboBoxField,
            'global-position': this.createTextField
        };
        this.communicationFieldSet = {
            'phone': this.createAutoCompleteField,
            'mobile': this.createAutoCompleteField,
            'fax': this.createAutoCompleteField
        };
        this.addressFieldSet = {
            'address': function( field )
            {
                if ( me.userFields && me.userFields.userInfo && me.userFields.userInfo.addresses )
                {
                    var addresses = me.userFields.userInfo.addresses;
                    var tabs = [];
                    for ( var index in addresses )
                    {
                        Ext.Array.include( tabs, me.generateAddressPanel( field, index != 0, addresses[index] ) );
                    }
                    return {
                        sourceField: field,
                        xtype: 'addressContainer',
                        items: tabs
                    };
                }
                else
                {
                    var tabItem = me.generateAddressPanel( field );
                    return {
                        sourceField: field,
                        xtype: 'addressContainer',
                        items: [tabItem]
                    };
                }
            }
        };
        this.callParent( arguments );
        this.addEvents( 'fieldsloaded' );
        this.removeAll();
        this.show();
    },

    renderUserForm: function( user )
    {
        var me = this;
        me.currentUser = user;
        var userStoreName = user ? user.userStore : me.defaultUserStoreName;
        var userStore = me.store.findRecord('name', userStoreName).raw;
        me.removeAll();
        me.generateForm( userStore );
        me.doLayout();
    },

    createAutoCompleteField: function ( field )
    {
        var callingCodeStore = Ext.data.StoreManager.lookup( 'CallingCodeStore' );
        var f = {
            xtype: 'userFormField',
            type: 'autocomplete',
            fieldLabel: field.fieldlabel,
            fieldStore: callingCodeStore,
            valueField: 'callingCode',
            displayField: 'callingCode',
            displayConfig:{
                getInnerTpl: function()
                {
                    return '{callingCode} ({englishName})';
                }
            }
        };
        return f;
    },

    createComboBoxField: function ( field )
    {
        var fieldStore;
        var valueField;
        var displayField;
        var displayConfig;
        var listeners = null;

        if ( field.type == 'timezone' )
        {
            fieldStore = Ext.data.StoreManager.lookup( 'TimezoneStore' );
            valueField = 'id';
            displayField = 'humanizedIdAndOffset';
            displayConfig = {
                getInnerTpl: function()
                {
                    return '{humanizedId} ({offset})';
                }
            };
            listeners = {
                beforequery: {
                    fn: function(query)
                    {
                        var queryText = query.query;
                        queryText = queryText.split( '(' )[0];
                        var pattern = new RegExp( queryText, 'gi' );
                        fieldStore.clearFilter();
                        fieldStore.filter( 'humanizedIdAndOffset', pattern );
                        query.combo.expand();

                        var picker = query.combo.getPicker();
                        var firstItem = picker.getNode( 0 );
                        if ( firstItem )
                        {
                            picker.highlightItem( firstItem );
                        }
                        return false;
                    }
                }
            }

        } else if ( field.type == 'locale' )
        {
            fieldStore = Ext.data.StoreManager.lookup( 'LocaleStore' );
            valueField = 'id';
            displayField = 'displayName';
        } else if ( field.type == 'country' )
        {
            fieldStore = Ext.data.StoreManager.lookup( 'CountryStore' );
            valueField = 'code';
            displayField = 'englishName';
        } else if ( field.type == 'region' )
        {
            fieldStore = new App.store.RegionStore();
            valueField = 'code';
            displayField = 'englishName';
        } else if ( field.type == 'locale' )
        {
            fieldStore = Ext.data.StoreManager.lookup( 'LanguageStore' );
            valueField = 'languageCode';
            displayField = 'description';
        } else if ( field.type == 'gender' )
        {
            fieldStore = new Ext.data.Store( {
                                                 fields: ['label', 'value'],
                                                 data: [
                                                     {label: 'Male', value: 'MALE'},
                                                     {label: 'Female', value: 'FEMALE'}
                                                 ]} );
            valueField = 'value';
            displayField = 'label';
        }

        return {
            xtype: 'userFormField',
            type: 'combo',
            queryMode: 'local',
            minChars: 1,
            emptyText: 'Please select',
            fieldStore: fieldStore,
            valueField: valueField,
            displayField: displayField,
            displayConfig: displayConfig,
            listeners: listeners
        };
    },

    createTextField: function( field )
    {
        return {
            xtype: 'userFormField',
            type: 'text'
        };
    },

    createCheckBoxField: function ( field )
    {
        return {
            xtype: 'userFormField',
            type: 'boolean'
        }
    },

    createPhotoField: function( field )
    {
        return {
            xtype: 'userFormField',
            type: 'file'
        };
    },

    createPasswordField: function ( field )
    {
        return {
            xtype: 'userFormField',
            type: 'password'
        };
    },

    createDateField: function( field )
    {
        return {
            xtype: 'userFormField',
            type: 'date'
        };
    },

    generateForm: function( storeConfig, staticFields, excludedFields )
    {
        if ( staticFields )
        {
            this.staticFields = staticFields;
        }
        if ( excludedFields )
        {
            this.excludedFields = excludedFields;
        }
        if ( storeConfig && storeConfig.userFields )
        {
            var fields = Ext.Array.filter( Ext.Array.toArray( storeConfig.userFields ), function ( field )
            {
                for ( index in this.staticFields )
                {
                    if ( this.staticFields[index].type == field.type )
                    {
                        return false;
                    }
                }
                return true;
            }, this );
            fields = Ext.Array.merge( this.staticFields, fields );
            this.add( this.generateFieldSet( 'User', this.userFieldSet, fields ) );
            if (!this.userFields)
            {
                this.add( this.generateFieldSet( 'Security', this.securityFieldSet, fields ) );
            }
            this.add( this.generateFieldSet( 'Name', this.nameFieldSet, fields ) );
            this.add( this.generateFieldSet( 'Personal Information', this.detailsFieldSet, fields ) );
            this.add( this.generateFieldSet( 'Settings', this.locationFieldSet, fields ) );
            this.add( this.generateFieldSet( 'Communication', this.communicationFieldSet, fields ) );
            this.add( this.generateFieldSet( 'Address', this.addressFieldSet, fields ) );
        }
        this.fireEvent( 'fieldsloaded', this, this );
    },

    generateFieldSet: function( title, fieldSet, storeConfig )
    {
        var self = App.view.EditUserFormPanel;
        var me = this;
        var fieldSetItem = {
            defaults: {
                bodyPadding: 10
            },
            xtype: 'fieldset',
            layout: {
                type: 'table',
                columns: 1,
                tableAttrs: {
                    style: {
                        width: '100%'
                    }
                }
            },
            measureWidth: true,
            title: title
        };
        var fieldItems = [];
        Ext.Array.each( storeConfig, function ( item )
        {
            var canBeAdded = true;
            if ( this.includedFields )
            {
                canBeAdded = Ext.Array.contains( this.includedFields, item.type );
            }
            else
            {
                canBeAdded = !Ext.Array.contains( this.excludedFields, item.type );
            }
            if ( fieldSet[item.type] && canBeAdded )
            {
                var fieldValue;
                if ( me.userFields )
                {
                    fieldValue = me.userFields[item.type];
                    if ( (fieldValue == null) && (me.userFields.userInfo != null) )
                    {
                        fieldValue = me.userFields.userInfo[item.type];
                    }
                }
                var baseConfig = {
                    fieldLabel: self.fieldLabels[item.type] || item.type,
                    fieldname: item.type,
                    required: item.required || false,
                    remote: item.remote || false,
                    readonly: item.readOnly || false || (item.type == 'username' && me.userFields),
                    vtype: item.vtype,
                    fieldValue: fieldValue,
                    currentUser: me.currentUser
                };
                var createFunc = fieldSet[item.type];
                var newField = createFunc( item );
                newField = Ext.apply( newField, baseConfig );
                Ext.Array.include( fieldItems, newField );
            }
        }, this );
        if ( title == 'Address' )
        {
            return fieldItems;
        } else if ( fieldItems.length > 0 )
        {
            fieldSetItem.items = fieldItems;
            return fieldSetItem;
        }
        else
        {
            return [];
        }
    },

    generateAddressPanel: function ( field, closable, values )
    {
        var addressPanel = {
            xtype: 'addressPanel',
            values: values,
            closable: closable || false,
            readonly: field.readonly,
            iso: field.iso

        };
        return addressPanel;
    },

    setItemValue: function( itemId, value )
    {
        var field = this.down( '#' + itemId );
        if ( field )
        {
            field.setValue( value );
        }
    },

    getAddresses: function(formValues)
    {
        var addressFields = ['country', 'region', 'street', 'postal-code', 'postal-address', 'label', 'iso-country', 'iso-region'];
        var address;
        var addresses = [];
        if ( Ext.isArray( formValues.label ) )
        {
            // multiple address panels in form
            var numAdr = formValues.label.length;
            for ( var a = 0; a < numAdr; a++ )
            {
                address = {};
                Ext.Array.forEach(addressFields, function (fieldId){
                    if (formValues[fieldId] && formValues[fieldId][a]) {
                        address[fieldId] = formValues[fieldId][a];
                    }
                });
                addresses.push( address );
            }
        }
        else
        {
            // single address panel in form
            address = {};
            Ext.Array.forEach(addressFields, function (fieldId){
                if (formValues[fieldId]) {
                    address[fieldId] = formValues[fieldId];
                }
            });
            addresses.push( address );
        }
        return addresses;
    },

    getData: function()
    {
        var formValues = this.getValues();
        var userData;
        var isPlacesForm = formValues.label;
        if ( isPlacesForm )
        {
            userData = {
                userInfo: {addresses: this.getAddresses(formValues) }
            };
        }
        else
        {
            userData = {
                userInfo:formValues
            };
            if ( formValues['username'] )
            {
                userData['username'] = formValues['username'];
            }
            if ( formValues['email'] )
            {
                userData['email'] = formValues['email'];
            }
        }
        return userData;
    }

} );


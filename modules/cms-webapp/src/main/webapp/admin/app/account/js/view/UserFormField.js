Ext.define( 'App.view.UserFormField', {
    extend: 'Ext.form.FieldContainer',
    alias: 'widget.userFormField',

    requires: ['App.view.PasswordMeter'],

    layout: {
        type: 'hbox'
    },

    width: 600,

    actionName: undefined,

    initComponent: function()
    {
        var me = this;
        this.fieldConfigBuilders = {
            'date': this.createDateConfig,
            'file': this.createFileConfig,
            'combo': this.createComboConfig,
            'autocomplete': this.createAutoCompleteConfig,
            'password': this.createPasswordConfig,
            'text': this.createTextConfig,
            'boolean': this.createCheckBoxConfig
        };
        this.fieldWidth = {
            'initials': 150,
            'birthday': 300,
            'gender': 200,
            'country': 400,
            'timezone': 400,
            'global-position': 200,
            'locale': 300,
            'fax': 300,
            'mobile': 300,
            'phone': 300
        }
        this.items = [];
        var fieldConfig = {
            flex: 1,
            enableKeyEvents: true,
            disabled: this.readonly,
            allowBlank: !this.required,
            name: this.fieldname,
            itemId: this.fieldname,
            action: this.actionName,
            value: this.fieldValue,
            listeners: {
                'validitychange': me.validityChanged
            }
        };
        if ( this.fieldWidth[this.fieldname] )
        {
            this.width = this.fieldWidth[this.fieldname];
        }
        var builderFunction;
        if ( this.type )
        {
            builderFunction = this.fieldConfigBuilders[this.type];
        }
        else
        {
            builderFunction = this.fieldConfigBuilders.text;
        }
        fieldConfig = builderFunction( fieldConfig, this );
        if ( this.remote )
        {
            var remoteIcon = {xtype: 'image', src: 'resources/images/icon_remote_10px.gif'};
            Ext.Array.include( this.items, remoteIcon );
        }
        if ( this.fieldname == 'display-name' )
        {
            fieldConfig.readOnly = true;
            fieldConfig.readOnlyCls = 'cms-display-name-readonly';
            fieldConfig.cls = 'cms-display-name';
            fieldConfig.height = 40;
            var lockButton = {
                xtype: 'button',
                iconCls: 'icon-locked',
                action: 'toggleDisplayNameField',
                currentUser: this.currentUser
            };
            Ext.Array.include( this.items, [fieldConfig, lockButton] );
        }
        else
        {
            Ext.Array.include( this.items, fieldConfig );
        }
        if ( this.required && (this.fieldLabel != undefined) )
        {
            this.fieldLabel += "<span style=\"color:red;\" ext:qtip=\"This field is required\">*</span>";
        }
        var greenLabel = {
            xtype: 'image',
            itemId: 'greenMark',
            src: 'resources/icons/16x16/finish.png',
            height: 16,
            width: 16,
            style: {
                visibility:'hidden'
            },
            setVisibility: function(isVisible){
                if (isVisible)
                {
                    this.el.setStyle({visibility: 'visible'});
                }
                else
                {
                    this.el.setStyle({visibility: 'hidden'});
                }
            }
        }
        Ext.Array.include(this.items, greenLabel);
        this.callParent( arguments );
        this.addEvents('validitychange');
    },

    createCheckBoxConfig: function( fieldConfig )
    {
        var checkBoxConfig = {xtype: 'checkbox',
            checked: fieldConfig.value};
        return Ext.apply( fieldConfig, checkBoxConfig );
    },

    createDateConfig: function( fieldConfig )
    {
        fieldConfig.value = Ext.Date.parse( fieldConfig.value, 'Y-m-d' );
        var dateConfig = {
            xtype: 'datefield',
            format: 'Y-m-d'
        };
        return Ext.apply( fieldConfig, dateConfig );
    },

    createComboConfig: function( fieldConfig, me )
    {
        var comboConfig;
        if ( me.fieldStore && me.fieldStore.getTotalCount() > 0 )
        {
            comboConfig = {
                xtype: 'combobox',
                store: me.fieldStore,
                valueField: me.valueField,
                displayField: me.displayField,
                queryMode: me.queryMode,
                minChars: me.minChars,
                emptyText: me.emptyText,
                listConfig: me.displayConfig
            };
        }
        else
        {
            comboConfig = {xtype: 'textfield'};
        }
        return Ext.apply( fieldConfig, comboConfig );
    },

    createAutoCompleteConfig: function( fieldConfig, me )
    {
        var autoCompleteConfig = {
            xtype: 'combobox',
            enableKeyEvents: true,
            store: me.fieldStore,
            triggeredAction: 'all',
            typeAhead: true,
            queryMode: 'local',
            minChars: 0,
            forceSelection: false,
            hideTrigger: true,
            valueField: me.valueField,
            displayField: me.displayField,
            listConfig: me.displayConfig,
            action: 'initValue'
        };
        return Ext.apply( fieldConfig, autoCompleteConfig );
    },

    createPasswordConfig: function( fieldConfig, me )
    {
        var passwordConfig, validator;
        if (fieldConfig.itemId == 'repeat-password')
        {
            validator = me.validatePassword;
        }
        
        if (me.fieldname == 'password')
        {
            passwordConfig = {
                xtype: 'passwordMeter'
            }
        }
        else
        {
            passwordConfig = {
                xtype: 'textfield',
                inputType: 'password',
                validator: validator
            };
        }

        return Ext.apply( fieldConfig, passwordConfig );
    },

    createFileConfig: function( fieldConfig )
    {
        var fileConfig = {xtype: 'filefield'};
        return Ext.apply( fieldConfig, fileConfig );
    },

    createTextConfig: function( fieldConfig )
    {
        var textConfig = {
            xtype: 'textfield',
            enableKeyEvents: true,
            bubbleEvents: ['keyup']
        };
        return Ext.apply( fieldConfig, textConfig );
    },

    validatePassword: function(value)
    {
        var passwordFieldValue = this.up('fieldset').down('#password').getValue();
        if (passwordFieldValue == value )
        {
            return true;
        } else {
            return 'Passwords don\'t match';
        }
    },

    validityChanged: function(field, isValid, opts)
    {
        var parentField = field.up('userFormField');
        parentField.fireEvent('validitychange', parentField, isValid, opts);
    }


} );
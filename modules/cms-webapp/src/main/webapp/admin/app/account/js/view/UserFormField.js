Ext.define( 'App.view.UserFormField', {
    extend: 'Ext.form.FieldContainer',
    alias: 'widget.userFormField',

    layout: {
        type: 'hbox'
    },

    actionName: undefined,
    //anchor: '50%',

    initComponent: function()
    {
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
            'initials': '30%',
            'birthday': '40%',
            'gender': '40%',
            'country': '60%',
            'global-position': '40%',
            'locale': '40%',
            'fax': '60%',
            'mobile': '40%',
            'phone': '60%'
        }
        this.items = [];
        var fieldConfig = {
            flex: 1,
            disabled: this.readonly,
            allowBlank: !this.required,
            name: this.fieldname,
            itemId: this.fieldname,
            action: this.actionName,
            value: this.fieldValue
        };
        Ext.applyIf( this, {
            anchor: this.fieldWidth[this.fieldname]
        } );
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
        if ( this.required && (this.fieldLabel != undefined))
        {
            this.fieldLabel += "<span style=\"color:red;\" ext:qtip=\"This field is required\">*</span>";
        }


        this.callParent( arguments );
    },

    createCheckBoxConfig: function( fieldConfig){
        var checkBoxConfig = {xtype: 'checkbox',
        checked: fieldConfig.value};
        return Ext.apply(fieldConfig, checkBoxConfig);
    },

    createDateConfig: function( fieldConfig )
    {
        fieldConfig.value = Ext.Date.parse( fieldConfig.value, 'Y-m-d' );
        var dateConfig = {
            xtype: 'datefield',
            format: 'Y-m-d'
        };
        return Ext.apply(fieldConfig, dateConfig);
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
                emptyText: me.emptyText
            };
        }
        else
        {
            comboConfig = {xtype: 'textfield'};
        }
        return Ext.apply(fieldConfig, comboConfig);
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
        return Ext.apply(fieldConfig, autoCompleteConfig);
    },

    createPasswordConfig: function( fieldConfig )
    {
        var passwordConfig = {
            xtype: 'textfield',
            inputType: 'password'
        };
        return Ext.apply(fieldConfig, passwordConfig);
    },

    createFileConfig: function( fieldConfig )
    {
        var fileConfig = {xtype: 'filefield'};
        return Ext.apply(fieldConfig, fileConfig);
    },

    createTextConfig: function( fieldConfig )
    {
        var textConfig = {
            xtype: 'textfield',
            enableKeyEvents: true,
            bubbleEvents: ['keyup']
        };
        return Ext.apply(fieldConfig, textConfig);
    }
} );
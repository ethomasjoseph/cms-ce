Ext.define( 'App.view.UserFormField', {
    extend: 'Ext.form.FieldContainer',
    alias: 'widget.userFormField',

    requires: ['App.view.PasswordMeter'],

    layout: {
        type: 'hbox'
    },

    validationResultType: 'none',

    width: 1000,

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
            'phone': 300,
            'password': 250,
            'repeat-password': 250
        }
        this.items = [];
        var fieldConfig = {
            enableKeyEvents: true,
            disabled: this.readonly,
            allowBlank: !this.required,
            vtype: this.vtype,
            name: this.fieldname,
            itemId: this.fieldname,
            action: this.actionName,
            value: this.fieldValue,
            width: 600,
            padding: '0 0 0 20',
            listeners: {
                'validitychange': me.validityChanged
            }
        };
        if ( this.fieldWidth[this.fieldname] )
        {
            fieldConfig.width = this.fieldWidth[this.fieldname];
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
            fieldConfig.cls = 'cms-remote-field';
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
        var validationLabel;
        if (this.validationResultType == 'short')
        {
            validationLabel = {
                itemId: 'validationLabel',
                height: 16,
                width: 16,
                margin: '0 0 0 15',
                cls: 'cms-validation-label',
                tpl: Templates.account.shortValidationResult,
                setVisibility: function( isVisible )
                {
                    if ( isVisible )
                    {
                        this.el.setStyle( {visibility: 'visible'} );
                    }
                    else
                    {
                        this.el.setStyle( {visibility: 'hidden'} );
                    }
                }
            };
            Ext.Array.include( this.items, validationLabel );
        }
        if (this.validationResultType == 'detail')
        {
            validationLabel = {
                itemId: 'validationLabel',
                tpl: '<div class="validationStatus">{text}</div> ',
                data: {text: ''},
                width: 200,
                cls: 'cms-validation-label',
                margin: '0 0 0 15',
                setVisibility: function( isVisible )
                {
                    if ( isVisible )
                    {
                        this.el.setStyle( {visibility: 'visible'} );
                    }
                    else
                    {
                        this.el.setStyle( {visibility: 'hidden'} );
                    }
                }
            };
            Ext.Array.include( this.items, validationLabel );
        }

        this.callParent( arguments );
        this.addEvents( 'validitychange' );
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
                listeners: me.listeners,
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
        var passwordConfig;

        if ( me.fieldname == 'password' )
        {
            me.cls = 'cms-glowing-item';
            passwordConfig = {
                xtype: 'passwordMeter'
            }
        }
        else
        {
            passwordConfig = {
                xtype: 'textfield',
                inputType: 'password',
                validator: me.validatePassword
            };
        }
//        passwordConfig.validator = me.validatePassword;
        return Ext.apply( fieldConfig, passwordConfig );
    },

    createFileConfig: function( fieldConfig )
    {
        var fileConfig = {xtype: 'filefield'};
        return Ext.apply( fieldConfig, fileConfig );
    },

    createTextConfig: function( fieldConfig, me )
    {
        var textConfig = {
            xtype: 'textfield',
            enableKeyEvents: true,
            bubbleEvents: ['keyup']
        };
        if ( me.fieldname === 'username' )
        {
            textConfig.validator = me.validateUserName;
            textConfig.validValue = true;
        } else if ( me.fieldname === 'email' )
        {
            textConfig.validator = me.validateUniqueEmail;
            textConfig.validValue = true;
            textConfig.listeners = {change: me.emailChanged, scope:me};
        }
        return Ext.apply( fieldConfig, textConfig );
    },

    validatePassword: function()
    {
        var passwordField = this.up( 'fieldset' ).down( '#password' );
        var repeatPasswordField = this.up( 'fieldset' ).down( '#repeat-password' );
        if ( passwordField.getValue() == repeatPasswordField.getValue() )
        {
            return true;
        }
        else
        {
            return 'Passwords don\'t match';
        }
    },

    validateUserName: function( value )
    {
        var me = this;
        if ( me.prevValue != value && value !== '' )
        {
            this.prevValue = value;
            var userForm = me.up( 'editUserFormPanel' );
            var userStoreName = userForm.currentUser ? userForm.currentUser.userStore : userForm.defaultUserStoreName;
            Ext.Ajax.request( {
                                  url: 'data/account/userkey',
                                  method: 'GET',
                                  params: {
                                      'userstore': userStoreName,
                                      'username': value
                                  },
                                  success: function( response )
                                  {
                                      var respObj = Ext.decode( response.responseText, true );
                                      if ( respObj.userkey != null )
                                      {
                                          me.validValue = false;
                                      }
                                      else
                                      {
                                          me.validValue = true;
                                      }
                                      me.validate();
                                  }
                              } );
        }
        return me.validValue == true ? me.validValue : "User with this user name already exists";

    },

    emailChanged: function ( field )
    {
        field.pendingServerValidation = true;
    },

    validateUniqueEmail: function( value )
    {
        var me = this;
        if ( (me.prevValue !== value) && (me.pendingServerValidation === true) )
        {
            me.prevValue = value;
            if ( !Ext.data.validations.email( {}, value ) )
            {
                // skip server unique-email validation, invalid email format will be triggered
                return true;
            }

            var userForm = me.up( 'editUserFormPanel' );
            var userWizard = userForm.up('userWizardPanel');
            var currentUserKey = (!userWizard.isNewUser()) ? userWizard.userFields['key'] : null;

            var userStoreName = userForm.currentUser ? userForm.currentUser.userStore : userForm.defaultUserStoreName;
            Ext.Ajax.request( {
                                  url: 'data/account/verifyUniqueEmail',
                                  method: 'GET',
                                  params: {
                                      'userstore': userStoreName,
                                      'email': value
                                  },
                                  success: function( response )
                                  {
                                      var respObj = Ext.decode( response.responseText, true );
                                      if ( respObj.emailInUse )
                                      {
                                          me.validValue = (respObj.userkey === currentUserKey);
                                      }
                                      else
                                      {
                                          me.validValue = true;
                                      }
                                      me.pendingServerValidation = false;
                                      me.validate();
                                  }
                              } );
        }
        return me.validValue || "A user with this email already exists in the userstore";
    },

    validityChanged: function( field, isValid, opts )
    {
        var parentField = field.up( 'userFormField' );
        var validationLabel = parentField.down('#validationLabel');
        if (parentField.validationResultType == 'detail')
        {

            var errors = field.getErrors();
            if (errors.length > 0)
            {
                validationLabel.update({text: errors[0]});
            }
            else
            {
                validationLabel.update({text: ''});
            }
        }
        if (parentField.validationResultType == 'short')
        {
            validationLabel.update({valid: isValid});
        }
        parentField.fireEvent( 'validitychange', parentField, isValid, opts );
    }


} );

Ext.define( 'App.view.PasswordMeter', {
    extend: 'Ext.form.FieldContainer',
    alias: 'widget.passwordMeter',

    requires: ['Ext.ProgressBar'],

    layout: {
        type: 'hbox',
        align: 'stretch'
    },

    height: 28,

    passwordStatuses: {
        0: {
            text: 'Too short',
            color: 'black'
        },
        1: {
            text: 'Weak',
            color: '#7D1D1F'
        },
        2: {
            text: 'Good',
            color: '#7D3D50'
        },
        3: {
            text: 'Strong',
            color: '#7D7750'
        },
        4: {
            text: 'Very Strong',
            color: '#3B8150'
        },
        5: {
            text: 'Very Strong',
            color: '#3B8150'
        }
    },


    updateStatus: function ( field, e, opts )
    {
        var passwordLevel = this.calculatePasswordStrength( field.getValue() );
        var passwordStatus = this.down( '#passwordStatus' );
        var passwordInput = this.down( '#passwordInput' );
        if (passwordLevel == 5)
        {
            passwordInput.addCls( 'cms-password-extra-strong' );
        }
        else
        {
            passwordInput.removeCls( 'cms-password-extra-strong' )
        }
        passwordStatus.update( this.passwordStatuses[passwordLevel] );
    },

    calculatePasswordStrength: function ( pwd )
    {
        var featuresNumber = 0;

        // Calculating feature count
        // Has text and numbers
        if (pwd.match(/\d+/g) && pwd.match(/[A-Za-z]+/g))
        {
            featuresNumber += 1;
        }
        // Has special chars
        if (pwd.match(/[\]\[!"#$%&'()*+,.\/:;<=>?@\^_`{|}~-]+/g))
        {
            featuresNumber += 1;
        }
        // Has at least two "text" and two "number" characters
        if (pwd.match(/\d\d+/g) && pwd.match(/[A-Za-z]+/g))
        {
            featuresNumber += 1;
        }
        // Has both uppercase and lower case text
        if (pwd.match(/[A-Z]+/g) && pwd.match(/[a-z]+/g))
        {
            featuresNumber += 1;
        }
        // Calculating level
        if ((pwd.length >= 12) && (featuresNumber >= 4))
        {
            return 5;
        }
        if ((pwd.length >= 10) && (featuresNumber >= 3))
        {
            return 4;
        }
        if ((pwd.length >= 10) && (featuresNumber >= 2))
        {
            return 3;
        }
        if ((pwd.length >= 8) && (featuresNumber >= 1))
        {
            return 2;
        }
        if ((pwd.length >= 6) && (pwd.match(/\d+/g) || pwd.match(/[A-Za-z]+/g)))
        {
            return 1;
        }

        return 0;
    },


    initComponent: function ()
    {
        var me = this;
        me.items = [
            {
                xtype: 'textfield',
                inputType: 'password',
                itemId: 'passwordInput',
                enableKeyEvents: true,
                allowBlank: this.allowBlank,
                listeners: {
                    keyup: {
                        fn: me.updateStatus,
                        scope: me
                    },
                    'validitychange': me.validityChanged
                },
                width: me.width,
                validator: me.validator
            },
            {
                itemId: 'passwordStatus',
                width: 100,
                tpl: Templates.account.passwordStatus,
                data: me.passwordStatuses[0]
            }
        ];
        // Additional width for status
        me.width += 100;
        me.callParent( arguments );
        me.addEvents( 'validitychange' );
    },

    getValue: function()
    {
        return this.down( 'textfield' ).getValue();
    },

    setValue: function( value )
    {
        this.down( 'textfield' ).setValue( value );
    },

    validityChanged: function( field, isValid, opts )
    {
        var parentField = field.up( 'passwordMeter' );
        parentField.fireEvent( 'validitychange', parentField, isValid, opts );
    },

    validate: function()
    {
        this.down( 'textfield' ).validate();
    }
} );

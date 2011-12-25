if ( !cms ) var cms = {};
if ( !cms.CodeArea ) cms.CodeArea = {};

cms.CodeArea = {
    editors:[],

    create:function ( config )
    {
        var editor = ace.edit( 'cms_codeArea_' + config.id );

        this.setMode( editor, config.mode );

        this.add( editor, config );

        editor.setReadOnly(config.readonly);
    },

    add:function ( aceInstance, config )
    {
        this.editors.push({
            ace: aceInstance,
            id: config.id,
            required: config.required,

            getValue:function ()
            {
                return this.ace.getSession().getValue();
            },

            setValue:function ( value )
            {
                this.ace.getSession().setValue( value );
            },

            save:function ()
            {
                document.getElementById( 'cms_codeArea_textArea_' + config.id ).value = this.getValue();
            }
        });
    },

    setMode:function ( aceInstance, mode )
    {
        var m = mode.toLowerCase();

        try
        {
            if ( m === 'java' )
            {
                var JavaMode = require( "ace/mode/java" ).Mode;
                aceInstance.getSession().setMode( new JavaMode() );
            }
            else if ( m === 'xml' )
            {
                var XmlMode = require( "ace/mode/xml" ).Mode;
                aceInstance.getSession().setMode( new XmlMode() );
            }
            else if ( m === 'html' )
            {
                var HtmlMode = require( "ace/mode/html" ).Mode;
                aceInstance.getSession().setMode( new HtmlMode() );

            }
            else if ( m === 'javascript' )
            {
                var JavaScriptMode = require( "ace/mode/javascript" ).Mode;
                aceInstance.getSession().setMode( new JavaScriptMode() );
            }
            else if ( m === 'css' )
            {
                var CssMode = require( "ace/mode/css" ).Mode;
                aceInstance.getSession().setMode( new CssMode() );
            }
            else if ( m === 'json' )
            {
                var JsonMode = require( "ace/mode/json" ).Mode;
                aceInstance.getSession().setMode( new JsonMode() );
            }
            else
            {
                alert( 'codearea.js\n\nA valid mode is not set in XSL template call. Default to text mode' );
            }
        }
        catch( exception )
        {
            alert( 'codearea.js\n\nSomething went wrong when setting ' + m + '. Most probably the JavaScript is not embedded.\n\nException: ' + exception );
        }

    },

    saveAll:function ()
    {
        for ( var i = 0; i < this.editors.length; i++ )
        {
            this.editors[i].save();
        }
    }

};

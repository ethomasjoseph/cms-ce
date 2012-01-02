Ext.define( 'App.lib.i18n.Phrases', {

    phrasesMap: {},

    NOT_TRANSLATED: '## Not translated ##',

    statics: {

        setMap: function ( phrasesMap )
        {
            this.phrasesMap = phrasesMap;
        },


        getPhrase: function ()
        {
            var args = Array.prototype.slice.call(arguments);
            var key = args[0];

            var phrase = this._findPhrase(key);
            if ( phrase === undefined )
            {
                return this.NOT_TRANSLATED
            }

            return this.formatPhrase(phrase, args.slice(1, args.length));
        },


        formatPhrase: function(phrase, args)
        {
            var formatted = phrase;

            for ( var i = 0; i < args.length; i++ )
            {
                var regExp = new RegExp( '\\{' + (i) + '\\}', 'g' );
                formatted = formatted.replace( regExp, args[i] );
            }

            return formatted;
        },


        _findPhrase: function ( key )
        {
            var depths = key.split('.').reverse();
            var toBottom = depths.length;
            var phrase = this.phrasesMap;

            while (toBottom--) {
                phrase = phrase[depths[toBottom]];
            }

            return phrase;
        }
    }

} );
function translate()
{
    var str = arguments[0];
    for ( var i = 0; i < arguments.length; i++ )
    {
        if ( i === 0 )
        {
            continue;
        }

        var regexp = new RegExp( '\\{' + (i - 1) + '\\}', 'gi' );
        str = str.replace( regexp, arguments[i] );
    }

    return str;
}

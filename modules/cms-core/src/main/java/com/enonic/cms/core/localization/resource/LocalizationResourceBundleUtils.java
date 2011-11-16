/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.localization.resource;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.enonic.cms.core.localization.LocaleParsingException;

/**
 * Created by rmy - Date: Apr 24, 2009
 */
public abstract class LocalizationResourceBundleUtils
{

    // Pattern to parse locale string on format Languagcode[-country][anything]
    private static final String LOCALE_PATTERN = "^(\\w{2})(_(\\w{2}))?(_(\\w{2}))?$";

    private static Matcher match( String inputString, String regexp, int patternOptions )
    {
        Pattern pattern = Pattern.compile( regexp, patternOptions );
        if ( inputString == null )
        {
            inputString = "";
        }

        return pattern.matcher( inputString );
    }

    public static Locale parseLocaleString( String localeAsString )
    {
        localeAsString = localeAsString.replace( '-', '_' );

        Matcher matcher = match( localeAsString, LOCALE_PATTERN, Pattern.CASE_INSENSITIVE );

        String language = "";
        String country = "";
        String variant = "";

        if ( matcher.matches() )
        {
            language = getLanguageFromMatcher( matcher );
            country = getCountryFromMatcher( matcher );
            variant = getVariantFromMatcher( matcher );
        }
        else
        {
            throw new LocaleParsingException( "Could not parse locale string: " + localeAsString + " to valid locale" );
        }

        return new Locale( language, country == null ? "" : country, variant == null ? "" : variant );
    }

    private static String getLanguageFromMatcher( Matcher matcher )
    {
        return matcher.group( 1 );
    }

    private static String getCountryFromMatcher( Matcher matcher )
    {
        return matcher.group( 3 );
    }

    private static String getVariantFromMatcher( Matcher matcher )
    {
        return matcher.group( 5 );
    }

}

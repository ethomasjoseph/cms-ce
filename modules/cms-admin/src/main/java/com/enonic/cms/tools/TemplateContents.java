package com.enonic.cms.tools;

import java.util.List;
import java.util.ListIterator;

import org.apache.commons.lang.StringUtils;

public class TemplateContents
{
    private final String name;

    private final List<String> lines;

    public TemplateContents( String name, List<String> lines )
    {
        this.name = name;
        this.lines = lines;
    }

    private String toJavascriptText( String linePrefix )
    {
        removeEmptyLines();

        final StringBuilder text = new StringBuilder();
        for ( int i = 0; i < lines.size(); i++ )
        {
            final String line = lines.get( i );
            final String lineTrimmed = StringUtils.strip( line );
            final String trimmed = StringUtils.substringBefore( line, lineTrimmed );

            final String textLine = linePrefix + trimmed + "'" + escapeSingleQuotes( lineTrimmed ) + "'";
            text.append( textLine );

            boolean isLastLine = ( i == lines.size() - 1 );
            if ( !isLastLine )
            {
                text.append( " + \r\n" );
            }

        }

        return text.toString();
    }

    private void removeEmptyLines()
    {
        ListIterator<String> ite = lines.listIterator();
        while ( ite.hasNext() )
        {
            final String line = ite.next();
            if ( ( line == null ) || line.trim().isEmpty() )
            {
                ite.remove();
            }
        }
    }

    private String escapeSingleQuotes( String text )
    {
        return text.replace( "'", "\\'" );
    }

    public String getName()
    {
        return name;
    }

    public String getText(){
        return toJavascriptText( "\t\t" );
    }
}

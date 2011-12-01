/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.admin.account;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import static org.junit.Assert.*;


public class CsvBuilderTest
{

    @Test
    public void createSimpleCsv()
        throws IOException
    {
        final CsvBuilder csvBuilder = new CsvBuilder();
        addCsvLines(csvBuilder);
        final String csvContent = csvBuilder.build().trim();

        final String expectedContent = normalizeNewLineChars( readFile( "csv_test.csv" ).trim() );

        assertEquals( expectedContent, csvContent );
    }

    @Test
    public void createCsvTabSeparator()
        throws IOException
    {
        final CsvBuilder csvBuilder = new CsvBuilder().setSeparator( "\t" );
        addCsvLines(csvBuilder);
        final String csvContent = csvBuilder.build().trim();

        final String expectedContent = normalizeNewLineChars( readFile( "csv_test_tab.csv" ).trim() );

        assertEquals( "\t", csvBuilder.getSeparator() );
        
        assertEquals( expectedContent, csvContent );
    }

    @Test
    public void createCsvWithQuotesAndSeparator()
        throws IOException
    {
        final CsvBuilder csvBuilder = new CsvBuilder();
        csvBuilder.addValue( "Type" );
        csvBuilder.addValue( "\"Display Name\"" );
        csvBuilder.addValue( "Local, or global Name" );

        csvBuilder.endOfLine();

        csvBuilder.addValue( "User" );
        csvBuilder.addValue( "\"super\" User" );
        csvBuilder.addValue( "local \"name\"" );

        final String csvContent = csvBuilder.build().trim();

        final String expectedContent = normalizeNewLineChars( readFile( "csv_test_quotes.csv" ).trim() );

        assertEquals( expectedContent, csvContent );
    }

    private void addCsvLines(CsvBuilder csvBuilder) {
        csvBuilder.addValue( "Type" );
        csvBuilder.addValue( "Display Name" );
        csvBuilder.addValue( "Local Name" );

        csvBuilder.endOfLine();

        csvBuilder.addValue( "User" );
        csvBuilder.addValue( "Anonymous User" );
        csvBuilder.addValue( "anonymous" );
    }

    private String normalizeNewLineChars( String text )
    {
        text = text.replaceAll( "\\r\\n", "\n" );
        text = text.replaceAll( "\\r", "\n" );
        return text.replaceAll( "\\n", "\r\n" );
    }

    private String readFile( String filename )
        throws IOException
    {
        InputStream is = getClass().getResourceAsStream( filename );
        StringWriter writer = new StringWriter();
        IOUtils.copy( is, writer );
        return writer.toString();
    }

}

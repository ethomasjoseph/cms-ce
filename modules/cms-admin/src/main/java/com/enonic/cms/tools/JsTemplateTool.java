package com.enonic.cms.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.io.IOUtils;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;


public class JsTemplateTool
{
    private static final String FREEMARKER_TEMPLATE = "Template.ftl";

    private final List<TemplateContents> templates;

    private final Configuration freeMarkerConfig;

    private String templatesNamespace;

    private String templateFilePattern;

    private String outputJsFileName;


    public JsTemplateTool()
    {
        templates = new ArrayList<TemplateContents>();
        templateFilePattern = "*.tpl.html";
        outputJsFileName = "Templates.js";

        freeMarkerConfig = new Configuration();
        freeMarkerConfig.setObjectWrapper( new DefaultObjectWrapper() );
        freeMarkerConfig.setClassForTemplateLoading( this.getClass(), "" );
    }

    public void generateTemplate( final File searchPath )
    {
        templates.clear();
        try
        {
            explore( searchPath );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }

    private void explore( final File directory )
        throws IOException, TemplateException
    {
        if ( !directory.isDirectory() )
        {
            return;
        }
        processTemplates( directory );
    }

    private void processTemplates( File baseDirectory )
        throws IOException, TemplateException
    {
        System.out.println( "Template search path: " + baseDirectory.getAbsolutePath() + "/" + templateFilePattern );

        final Pattern pattern = createPattern( templateFilePattern );
        final File[] templateFiles = listFiles( baseDirectory, pattern );

        for ( File templateFile : templateFiles )
        {
            final Matcher matcher = pattern.matcher( templateFile.getName() );
            matcher.find();
            final String templateName = matcher.group( 1 );
            readTemplate( templateFile, templateName );
        }

        writeOutputTemplate( templatesNamespace, baseDirectory );
    }

    private Pattern createPattern( String pattern )
    {
        String translatedPattern = pattern.replace( ".", "\\." ).replace( "*", "(.*)" );
        return Pattern.compile( translatedPattern );
    }

    private void readTemplate( File templateFile, String templateName )
        throws IOException
    {
        System.out.println( "Processing html template '" + templateFile.getName() + "'" );

        final FileInputStream is = new FileInputStream( templateFile );
        final List<String> templateLines = IOUtils.readLines( is );
        final TemplateContents templateContents = new TemplateContents( templateName, templateLines );

        templates.add( templateContents );
    }

    private File[] listFiles( final File directory, final Pattern pattern )
    {
        return directory.listFiles( new FilenameFilter()
        {
            @Override
            public boolean accept( File dir, String name )
            {
                return pattern.matcher( name ).matches();
            }
        } );
    }

    private void writeOutputTemplate( String templatesNamespace, File baseDirectory )
        throws IOException, TemplateException
    {
        final Template template = freeMarkerConfig.getTemplate( FREEMARKER_TEMPLATE );

        final Map<String, Object> root = new HashMap<String, Object>();
        root.put( "templateNamespace", templatesNamespace );
        root.put( "templateList", templates );
        root.put( "timestamp", new Date() );

        final File templateFile = new File( baseDirectory, outputJsFileName );
        final Writer out = new FileWriter( templateFile );
        template.process( root, out );
        out.flush();

        System.out.println( "Javascript template file generated: " + templateFile.getAbsolutePath() );
    }

    public String getTemplatesNamespace()
    {
        return templatesNamespace;
    }

    public void setTemplatesNamespace( String templatesNamespace )
    {
        this.templatesNamespace = templatesNamespace;
    }

    public String getTemplateFilePattern()
    {
        return templateFilePattern;
    }

    public void setTemplateFilePattern( String templateFilePattern )
    {
        this.templateFilePattern = templateFilePattern;
    }

    public String getOutputJsFileName()
    {
        return outputJsFileName;
    }

    public void setOutputJsFileName( String outputJsFileName )
    {
        this.outputJsFileName = outputJsFileName;
    }


    public static void main( String... args )
    {
        final JsTemplateTool tool = new JsTemplateTool();

        final CommandLineParser parser = new PosixParser();
        final Options options = new Options();
        options.addOption( "ns", "namespace", true, "Template namespace in Javascript output file." );
        options.addOption( "p", "path", true, "Templates directory path." );
        options.addOption( new Option( "help", "Print this message." ) );

        try
        {
            // parse the command line arguments
            final CommandLine line = parser.parse( options, args );

            if ( line.hasOption( "help" ) )
            {
                printHelp( options );
                return;
            }
            final String templateNamespace = line.hasOption( "ns" ) ? line.getOptionValue( "ns" ) : "accounts";
            final String pathStr =
                line.hasOption( "p" ) ? line.getOptionValue( "p" ) : "modules/cms-webapp/src/main/webapp/admin/app/account/js/templates";
            tool.setTemplatesNamespace( templateNamespace );

            final File path = new File( pathStr );

            tool.generateTemplate( path );

        }
        catch ( ParseException exp )
        {
            System.out.println( "Invalid parameters: " + exp.getMessage() );
            printHelp( options );
        }
    }

    private static void printHelp( Options options )
    {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "JsTemplateTool", options );
    }
}

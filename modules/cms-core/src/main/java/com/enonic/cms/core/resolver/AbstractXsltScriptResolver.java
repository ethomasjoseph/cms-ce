/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.resolver;

import javax.xml.transform.URIResolver;

import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.framework.xml.XMLDocument;

import com.enonic.cms.core.resolver.locale.LocaleResolverException;
import com.enonic.cms.core.resource.ResourceFile;
import com.enonic.cms.core.xslt.XsltProcessor;
import com.enonic.cms.core.xslt.XsltProcessorException;
import com.enonic.cms.core.xslt.XsltProcessorManager;
import com.enonic.cms.core.xslt.XsltProcessorManagerAccessor;
import com.enonic.cms.core.xslt.XsltResource;

/**
 * Created by rmy - Date: Apr 29, 2009
 */
public abstract class AbstractXsltScriptResolver
    implements ScriptResolverService
{
    protected final static String RESOLVING_EXCEPTION_MSG = "Failed to resolve value";

    private static final String XSLT_RESOLVER_PROCESSOR_NAME = "xsltResolverProcessor";

    private ResolverInputXMLCreator resolverInputXMLCreator;

    public ScriptResolverResult resolveValue( ResolverContext context, ResourceFile localeResolverScript )
    {
        XMLDocument resolverInput = getResolverInput( context );

        String resolvedValue;
        try
        {
            resolvedValue = resolveWithXsltScript( localeResolverScript.getDataAsXml(), resolverInput );
        }
        catch ( XsltProcessorException e )
        {
            throw new LocaleResolverException( RESOLVING_EXCEPTION_MSG + " using script : " + localeResolverScript.getPath(), e );
        }

        return populateScriptResolverResult( resolvedValue );
    }

    protected abstract ScriptResolverResult populateScriptResolverResult( String resolvedValue );

    protected XsltProcessor createProcessor( String name, XMLDocument xslt, URIResolver uriResolver )
        throws XsltProcessorException
    {
        XsltResource resource = new XsltResource( name, xslt.getAsString() );
        XsltProcessorManager manager = XsltProcessorManagerAccessor.getProcessorManager();
        XsltProcessor processor = manager.createProcessor( resource, uriResolver );
        processor.setOmitXmlDecl( true );
        return processor;
    }

    protected String cleanWhitespaces( String value )
    {
        value = value.replaceAll("(\\n)", "");
        value = value.replaceAll("(\\t)", "");
        value = value.replaceAll("(\\s)", "");
        return value;
    }

    protected String resolveWithXsltScript( XMLDocument xslt, XMLDocument xml )
        throws XsltProcessorException
    {
        URIResolver uriResolver = null;
        XsltProcessor processor = createProcessor( XSLT_RESOLVER_PROCESSOR_NAME, xslt, uriResolver );
        String result = processor.process( xml.getAsJDOMSource() );
        return cleanWhitespaces( result );
    }

    protected XMLDocument getResolverInput( ResolverContext context )
    {
        return resolverInputXMLCreator.buildResolverInputXML( context );
    }

    @Autowired
    public void setResolverInputXMLCreator( ResolverInputXMLCreator resolverInputXMLCreator )
    {
        this.resolverInputXMLCreator = resolverInputXMLCreator;
    }
}



package com.enonic.cms.itest.client;

import com.enonic.cms.api.client.model.GetContentTypeConfigXMLParams;
import com.enonic.cms.core.client.InternalClientImpl;
import com.enonic.cms.core.content.ContentHandlerName;
import com.enonic.cms.core.portal.livetrace.LivePortalTraceService;
import com.enonic.cms.framework.xml.XMLDocumentFactory;
import com.enonic.cms.itest.AbstractSpringTest;
import com.enonic.cms.itest.util.DomainFactory;
import com.enonic.cms.itest.util.DomainFixture;
import com.enonic.cms.store.dao.ContentTypeDao;
import org.jdom.Document;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.enonic.cms.itest.util.AssertTool.*;

public class InternalClientImpl_getContentTypeXmlTest
    extends AbstractSpringTest
{
    private DomainFactory factory;

    @Autowired
    private DomainFixture fixture;

    private InternalClientImpl internalClient;

    @Autowired
    private ContentTypeDao contentTypeDao;

    @Autowired
    private LivePortalTraceService livePortalTraceService;

    @Before
    public void setUp()
    {

        factory = fixture.getFactory();

        // setup
        fixture.initSystemData();
        fixture.save( factory.createContentHandler( "Custom content", ContentHandlerName.CUSTOM.getHandlerClassShortName() ) );

        fixture.save( factory.createContentType( 1002, "document", ContentHandlerName.CUSTOM.getHandlerClassShortName(),
                                                 getDocumentContentTypeXml() ) );

        internalClient = new InternalClientImpl();
        internalClient.setContentTypeDao( contentTypeDao );
        internalClient.setLivePortalTraceService( livePortalTraceService );
    }

    @Test
    public void getContentTypeConfigXMLByKey()
    {
        GetContentTypeConfigXMLParams params = new GetContentTypeConfigXMLParams();
        params.key = 1002;
        Document documentContentType = internalClient.getContentTypeConfigXML( params );

        assertXPathEquals( "/contenttype/config/form/title/@name", documentContentType, "heading" );
        assertXPathExist( "/contenttype/config/form/block/input", documentContentType );
        assertXPathExist( "/contenttype/config/form/block/@name", documentContentType );
        assertXPathNotExist( "/contenttype/config/form/block2/input", documentContentType );

        assertXPathEquals( "/contenttype/config/form/block[1]/input[1]/@name", documentContentType, "heading" );
        assertXPathEquals( "/contenttype/config/form/block[1]/input[2]/@name", documentContentType, "text" );
        assertXPathEquals( "/contenttype/config/form/block[2]/input[1]/@name", documentContentType, "meta-keywords" );
        assertXPathEquals( "/contenttype/config/form/block[2]/input[2]/@name", documentContentType, "meta-description" );

        assertSingleXPathValueEquals( "/contenttype/config/form/block[2]/input[1]/display", documentContentType, "Meta keywords" );
        assertSingleXPathValueEquals( "/contenttype/config/form/block[2]/input[1]/xpath", documentContentType,
                                      "contentdata/meta-keywords" );
        assertSingleXPathValueEquals( "/contenttype/config/form/block[2]/input[1]/help", documentContentType, "Comma separated" );
    }

    @Test
    public void getContentTypeConfigXMLByName()
    {
        GetContentTypeConfigXMLParams params = new GetContentTypeConfigXMLParams();
        params.name = "document";
        Document documentContentType = internalClient.getContentTypeConfigXML( params );

        assertXPathEquals( "/contenttype/config/form/title/@name", documentContentType, "heading" );
        assertXPathExist( "/contenttype/config/form/block/input", documentContentType );
        assertXPathExist( "/contenttype/config/form/block/@name", documentContentType );
        assertXPathNotExist( "/contenttype/config/form/block2/input", documentContentType );

        assertXPathEquals( "/contenttype/config/form/block[1]/input[1]/@name", documentContentType, "heading" );
        assertXPathEquals( "/contenttype/config/form/block[1]/input[2]/@name", documentContentType, "text" );
        assertXPathEquals( "/contenttype/config/form/block[2]/input[1]/@name", documentContentType, "meta-keywords" );
        assertXPathEquals( "/contenttype/config/form/block[2]/input[2]/@name", documentContentType, "meta-description" );

        assertSingleXPathValueEquals( "/contenttype/config/form/block[2]/input[1]/display", documentContentType, "Meta keywords" );
        assertSingleXPathValueEquals( "/contenttype/config/form/block[2]/input[1]/xpath", documentContentType,
                                      "contentdata/meta-keywords" );
        assertSingleXPathValueEquals( "/contenttype/config/form/block[2]/input[1]/help", documentContentType, "Comma separated" );
    }

    private Document getDocumentContentTypeXml()
    {
        final StringBuffer config = new StringBuffer();
        config.append( "<contenttype>" );
        config.append( "  <config>" );
        config.append( "    <form>" );
        config.append( "      <title name=\"heading\"/>" );
        config.append( "      <block name=\"Document\">" );
        config.append( "        <input name=\"heading\" required=\"true\" type=\"text\">" );
        config.append( "          <display>Heading</display>" );
        config.append( "          <xpath>contentdata/heading</xpath>" );
        config.append( "        </input>" );
        config.append( "        <input config=\"document\" mode=\"xhtml\" name=\"text\" type=\"htmlarea\">" );
        config.append( "          <display>Text</display>" );
        config.append( "          <xpath>contentdata/text</xpath>" );
        config.append( "        </input>" );
        config.append( "      </block>" );
        config.append( "      <block name=\"Meta information\">" );
        config.append( "        <input name=\"meta-keywords\" type=\"text\">" );
        config.append( "          <display>Meta keywords</display>" );
        config.append( "          <xpath>contentdata/meta-keywords</xpath>" );
        config.append( "          <help>Comma separated</help>" );
        config.append( "        </input>" );
        config.append( "        <input name=\"meta-description\" type=\"textarea\">" );
        config.append( "          <display>Meta description</display>" );
        config.append( "          <xpath>contentdata/meta-description</xpath>" );
        config.append( "         </input>" );
        config.append( "      </block>" );
        config.append( "    </form>" );
        config.append( "  </config>" );
        config.append( "</contenttype>" );
        return XMLDocumentFactory.create( config.toString() ).getAsJDOMDocument();
    }
}

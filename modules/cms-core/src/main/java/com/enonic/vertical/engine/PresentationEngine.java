/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enonic.esl.util.StringUtil;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.VerticalProperties;
import com.enonic.vertical.engine.handlers.BinaryDataHandler;
import com.enonic.vertical.engine.handlers.CommonHandler;
import com.enonic.vertical.engine.handlers.MenuHandler;
import com.enonic.vertical.engine.handlers.UserHandler;

import com.enonic.cms.core.content.binary.BinaryData;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.store.dao.SiteDao;

import com.enonic.cms.domain.SiteKey;

public class PresentationEngine
    extends BaseEngine
{
    private static final Logger LOG = LoggerFactory.getLogger( PresentationEngine.class.getName() );

    private final static int DEFAULT_CONNECTION_TIMEOUT = 2000;

    private BinaryDataHandler binaryDataHandler;

    private CommonHandler commonHandler;

    private MenuHandler menuHandler;

    private UserHandler userHandler;

    @Autowired
    private SiteDao siteDao;

    public CommonHandler getCommonHandler()
    {
        return commonHandler;
    }

    public BinaryData getBinaryData( User user, int binaryDataKey, long timestamp )
    {

        if ( user == null )
        {
            user = userHandler.getAnonymousUser();
        }

        return binaryDataHandler.getBinaryData( user, binaryDataKey, timestamp );
    }

    private Document getURL( String address, String encoding, int timeoutMs )
    {
        InputStream in = null;
        BufferedReader reader = null;
        Document result;
        try
        {
            URL url = new URL( address );
            URLConnection urlConn = url.openConnection();
            urlConn.setConnectTimeout( timeoutMs > 0 ? timeoutMs : DEFAULT_CONNECTION_TIMEOUT );
            urlConn.setRequestProperty( "User-Agent", VerticalProperties.getVerticalProperties().getDataSourceUserAgent() );
            String userInfo = url.getUserInfo();
            if ( StringUtils.isNotBlank( userInfo ) )
            {
                String userInfoBase64Encoded = new String( Base64.encodeBase64( userInfo.getBytes() ) );
                urlConn.setRequestProperty( "Authorization", "Basic " + userInfoBase64Encoded );
            }
            in = urlConn.getInputStream();

            // encoding == null: XML file
            if ( encoding == null )
            {
                result = XMLTool.domparse( in );
            }
            else
            {
                StringBuffer sb = new StringBuffer( 1024 );
                reader = new BufferedReader( new InputStreamReader( in, encoding ) );
                char[] line = new char[1024];
                int charCount = reader.read( line );
                while ( charCount > 0 )
                {
                    sb.append( line, 0, charCount );
                    charCount = reader.read( line );
                }

                result = XMLTool.createDocument( "urlresult" );
                Element root = result.getDocumentElement();
                XMLTool.createCDATASection( result, root, sb.toString() );
            }
        }
        catch ( SocketTimeoutException ste )
        {
            String message = "Socket timeout when trying to get url: " + address;
            LOG.warn( message);
            result = null;
        }
        catch ( IOException ioe )
        {
            String message = "Failed to get URL: %t";
            LOG.warn( StringUtil.expandString( message, null, ioe ), ioe );
            result = null;
        }
        catch ( RuntimeException re )
        {
            String message = "Failed to get URL: %t";
            LOG.warn( StringUtil.expandString( message, null, re ), re );
            result = null;
        }
        finally
        {
            try
            {
                if ( reader != null )
                {
                    reader.close();
                }
                else if ( in != null )
                {
                    in.close();
                }
            }
            catch ( IOException ioe )
            {
                String message = "Failed to close URL connection: %t";
                LOG.warn( StringUtil.expandString( message, null, ioe ), ioe );
            }
        }

        if ( result == null )
        {
            result = XMLTool.createDocument( "noresult" );
        }

        return result;
    }

    public Document getURLAsXML( String address, int timeout )
    {
        return getURL( address, null, timeout );
    }

    public Document getURLAsText( String address, String encoding, int timeout )
    {
        return getURL( address, encoding, timeout );
    }

    public boolean hasErrorPage( int menuKey )
    {
        return menuHandler.getErrorPage( menuKey ) >= 0;
    }

    public int getErrorPage( int menuKey )
    {
        return menuHandler.getErrorPage( menuKey );
    }

    public int getLoginPage( int menuKey )
    {
        return menuHandler.getLoginPage( menuKey );
    }

    public int getBinaryDataKey( int contentKey, String label )
    {
        return binaryDataHandler.getBinaryDataKey( contentKey, label );
    }

    public void setBinaryDataHandler( BinaryDataHandler binaryDataHandler )
    {
        this.binaryDataHandler = binaryDataHandler;
    }

    public void setCommonHandler( CommonHandler commonHandler )
    {
        this.commonHandler = commonHandler;
    }

    public void setMenuHandler( MenuHandler menuHandler )
    {
        this.menuHandler = menuHandler;
    }

    public void setUserHandler( UserHandler userHandler )
    {
        this.userHandler = userHandler;
    }

    public boolean siteExists( SiteKey siteKey )
    {
        SiteEntity site = siteDao.findByKey( siteKey.toInt() );
        return ( site != null );
    }
}

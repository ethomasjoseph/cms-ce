/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import org.apache.commons.fileupload.FileItem;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.io.FileUtil;
import com.enonic.esl.util.ParamsInTextParser;
import com.enonic.esl.xml.XMLTool;

import com.enonic.cms.framework.util.TIntArrayList;

import com.enonic.cms.core.content.binary.BinaryData;

public abstract class AdminHandlerBaseServlet
{
    public static BinaryData createBinaryData( final FileItem fileItem )
        throws VerticalAdminException
    {
        return createBinaryData( fileItem, null );
    }

    public static BinaryData createBinaryData( final FileItem fileItem, final String label )
        throws VerticalAdminException
    {
        BinaryData binaryData = new BinaryData();
        InputStream fis = null;
        try
        {
            binaryData.fileName = FileUtil.getFileName( fileItem );

            fis = fileItem.getInputStream();
            ByteArrayOutputStream bao = new ByteArrayOutputStream();

            byte[] buf = new byte[1024 * 10];
            int size;
            while ( ( size = fis.read( buf ) ) > 0 )
            {
                bao.write( buf, 0, size );
            }
            binaryData.data = bao.toByteArray();
            binaryData.label = label;
        }
        catch ( IOException e )
        {
            VerticalAdminLogger.errorAdmin( "I/O error: %t", e );
        }
        finally
        {
            try
            {
                if ( fis != null )
                {
                    fis.close();
                }
            }
            catch ( IOException ioe )
            {
                String message = "Failed to close file input stream: %t";
                VerticalAdminLogger.warn( message, ioe );
            }
        }
        return binaryData;
    }

    public static boolean isArrayFormItem( Map formItems, String string )
    {
        if ( !formItems.containsKey( string ) )
        {
            return false;
        }

        if ( formItems.get( string ) == null )
        {
            return false;
        }

        return formItems.get( string ).getClass() == String[].class;
    }

    public static String[] getArrayFormItem( Map formItems, String string )
    {
        if ( !formItems.containsKey( string ) )
        {
            return new String[0];
        }

        Object item = formItems.get( string );

        if ( item == null )
        {
            return new String[0];
        }

        if ( item.getClass() == String[].class )
        {
            return (String[]) item;
        }
        else
        {
            return new String[]{(String) item};
        }
    }

    public static Document buildAccessRightsXML( Element rootElem, String key, ExtendedMap formItems, int accessrightsType )
    {

        // Handle this in calling methods instead
        //if (!formItems.containsKey("updateaccessrights"))
        //    return null;

        Document doc;
        Element elmAccessRights;
        if ( rootElem != null )
        {
            doc = rootElem.getOwnerDocument();
            elmAccessRights = XMLTool.createElement( doc, rootElem, "accessrights" );
        }
        else
        {
            doc = XMLTool.createDocument( "accessrights" );
            elmAccessRights = doc.getDocumentElement();
        }

        if ( key != null )
        {
            elmAccessRights.setAttribute( "key", key );
        }
        if ( accessrightsType != Integer.MIN_VALUE )
        {
            elmAccessRights.setAttribute( "type", String.valueOf( accessrightsType ) );
        }

        for ( Object parameterKey : formItems.keySet() )
        {
            String paramName = (String) parameterKey;
            if ( paramName.startsWith( "accessright[key=" ) )
            {
                String paramValue = formItems.getString( paramName );
                ExtendedMap paramsInName = ParamsInTextParser.parseParamsInText( paramName, "[", "]", ";" );
                ExtendedMap paramsInValue = ParamsInTextParser.parseParamsInText( paramValue, "[", "]", ";" );

                buildAccessRightElement( doc, elmAccessRights, paramsInName.getString( "key" ), paramsInValue );
            }
        }

        return doc;
    }

    protected static Element buildAccessRightElement( Document doc, Element root, String key, ExtendedMap paramsInValue )
    {

        Element element = XMLTool.createElement( doc, root, "accessright" );

        if ( key != null )
        {
            element.setAttribute( "groupkey", key );
        }
        element.setAttribute( "grouptype", paramsInValue.getString( "grouptype", "" ) );
        element.setAttribute( "adminread", paramsInValue.getString( "adminread", "false" ) );
        element.setAttribute( "read", paramsInValue.getString( "read", "false" ) );
        element.setAttribute( "update", paramsInValue.getString( "update", "false" ) );
        element.setAttribute( "delete", paramsInValue.getString( "delete", "false" ) );
        element.setAttribute( "create", paramsInValue.getString( "create", "false" ) );
        element.setAttribute( "publish", paramsInValue.getString( "publish", "false" ) );
        element.setAttribute( "administrate", paramsInValue.getString( "administrate", "false" ) );
        element.setAttribute( "approve", paramsInValue.getString( "approve", "false" ) );
        element.setAttribute( "add", paramsInValue.getString( "add", "false" ) );

        String displayName = paramsInValue.getString( "name", null );
        if ( displayName != null )
        {
            element.setAttribute( "displayname", displayName );
        }

        return element;
    }

    public static int[] getIntArrayFormItem( ExtendedMap formItems, String formKey )
    {
        return getIntArrayFormItems( formItems, new String[]{formKey} );
    }

    public static int[] getIntArrayFormItems( ExtendedMap formItems, String[] formKeys )
    {
        TIntArrayList keys = new TIntArrayList();
        for ( int i = 0; i < formKeys.length; i++ )
        {
            String[] items = getArrayFormItem( formItems, formKeys[i] );
            for ( int j = 0; j < items.length; j++ )
            {
                if ( items[j] != null && items[j].length() > 0 )
                {
                    int value = Integer.parseInt( items[j] );
                    if ( !keys.contains( value ) )
                    {
                        keys.add( value );
                    }
                }
            }
        }
        return keys.toArray();
    }
}

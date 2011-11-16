/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.handlers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Map;
import java.util.Set;

import com.enonic.cms.core.content.UnitEntity;
import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.engine.VerticalCreateException;
import com.enonic.vertical.engine.VerticalEngineLogger;
import com.enonic.vertical.engine.filters.Filter;

import com.enonic.cms.core.CalendarUtil;

public class UnitHandler
    extends BaseHandler
{

    // Unit SQL

    private final static String UNI_TABLE = "tUnit";

    private final static String UNI_SELECT = "SELECT uni_lKey,uni_lan_lKey,lan_sDescription,uni_sName,uni_sDescription," +
        "uni_lSuperKey,uni_bDeleted,uni_dteTimestamp,cat_lKey,cat_sName" + " FROM tUnit" + " JOIN tLanguage ON uni_lan_lKey=lan_lKey" +
        " JOIN tCategory ON tCategory.cat_uni_lKey = tUnit.uni_lKey" + " WHERE (uni_bDeleted=0) AND cat_cat_lSuper IS NULL";

    private final static String UNI_SELECT_NAME =
        "SELECT uni_lKey, uni_sName, cat_sName, cat_lKey, lan_lKey, lan_sDescription, lan_sCode" + " FROM tUnit" +
            " JOIN tLanguage ON tLanguage.lan_lKey = tUnit.uni_lan_lKey" + " JOIN tCategory ON tCategory.cat_uni_lKey = tUnit.uni_lKey" +
            " WHERE (uni_bDeleted=0) AND cat_cat_lSuper IS NULL ";

    private final static String UNI_INSERT =
        "INSERT INTO tUnit" + " (uni_lKey,uni_lan_lKey,uni_sName,uni_sDescription," + "uni_lSuperKey,uni_bDeleted,uni_dteTimestamp)" +
            " VALUES (?,?,?,?,?,?," + "@currentTimestamp@" + ")";

    private final static String UNI_UPDATE =
        "UPDATE tUnit" + " SET uni_lan_lKey=?" + ",uni_sName=?" + ",uni_sDescription=?" + ",uni_lSuperKey=?" + ",uni_dteTimestamp=" +
            "@currentTimestamp@" + " WHERE uni_lKey=?";

    private final static String UNI_WHERE_CLAUSE = " uni_lKey=?";

    /**
     * @param xmlData String
     * @return int
     * @throws VerticalCreateException The exception description.
     */
    public int createUnit( String xmlData )
        throws VerticalCreateException
    {

        int key = -1;
        Document doc = XMLTool.domparse( xmlData, "unit" );
        Element root = doc.getDocumentElement();
        Map subelems = XMLTool.filterElements( root.getChildNodes() );

        // connection variables
        Connection con = null;
        PreparedStatement preparedStmt = null;

        try
        {
            con = getConnection();
            preparedStmt = con.prepareStatement( UNI_INSERT );

            // get the foreign keys
            // int sitekey = Integer.parseInt(root.getAttribute("sitekey"));
            int superkey = -1;
            String keyStr = root.getAttribute( "superkey" );
            if ( keyStr.length() > 0 )
            {
                superkey = Integer.parseInt( keyStr );
            }
            int languageKey = Integer.parseInt( root.getAttribute( "languagekey" ) );

            // attribute: key
            key = getNextKey( UNI_TABLE );
            preparedStmt.setInt( 1, key );

            // attribute: superkey
            if ( superkey >= 0 )
            {
                preparedStmt.setInt( 5, superkey );
            }
            else
            {
                preparedStmt.setNull( 5, Types.INTEGER );
            }

            // attribute: languagekey
            preparedStmt.setInt( 2, languageKey );

            // element: name
            Element subelem = (Element) subelems.get( "name" );
            String name = XMLTool.getElementText( subelem );
            preparedStmt.setString( 3, name );

            // element: description
            subelem = (Element) subelems.get( "description" );
            if ( subelem != null )
            {
                String description = XMLTool.getElementText( subelem );

                if ( description == null )
                {
                    preparedStmt.setNull( 4, Types.VARCHAR );
                }
                else
                {
                    preparedStmt.setString( 4, description );
                }
            }
            else
            {
                preparedStmt.setNull( 4, Types.VARCHAR );
            }

            // mark as not deleted
            preparedStmt.setBoolean( 6, false );

            // element: timestamp (using the database timestamp at update)
            /* no code */

            // add the unit
            preparedStmt.executeUpdate();

            // Set content types
            Element[] contentTypeElems = XMLTool.getElements( XMLTool.getElement( root, "contenttypes" ) );
            int[] contentTypeKeys = new int[contentTypeElems.length];
            for ( int j = 0; j < contentTypeElems.length; j++ )
            {
                contentTypeKeys[j] = Integer.parseInt( contentTypeElems[j].getAttribute( "key" ) );
            }
            setUnitContentTypes( key, contentTypeKeys );
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to create units: %t";
            VerticalEngineLogger.errorCreate(message, sqle );
        }
        catch ( NumberFormatException nfe )
        {
            String message = "Failed to parse a unit key: %t";
            VerticalEngineLogger.errorCreate(message, nfe );
        }

        finally
        {
            close( preparedStmt );
        }

        return key;
    }

    /**
     * @param unitKey int
     * @return String
     */
    public XMLDocument getUnit( int unitKey )
    {
        StringBuffer sql = new StringBuffer( UNI_SELECT );
        sql.append( " AND" );
        sql.append( UNI_WHERE_CLAUSE );
        int[] paramValue = {unitKey};

        return XMLDocumentFactory.create(getUnit(sql.toString(), paramValue));
    }

    private Document getUnit( String sql, int[] paramValue )
    {
        sql += " ORDER BY uni_sName ASC";

        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;
        Document doc = null;

        try
        {
            doc = XMLTool.createDocument( "units" );
            Element root = doc.getDocumentElement();

            con = getConnection();
            preparedStmt = con.prepareStatement( sql );
            int length = ( paramValue != null ? paramValue.length : 0 );
            for ( int i = 0; i < length; i++ )
            {
                preparedStmt.setInt( i + 1, paramValue[i] );
            }
            resultSet = preparedStmt.executeQuery();

            while ( resultSet.next() )
            {
                int unitkey = resultSet.getInt( "uni_lKey" );
                // int sitekey = resultSet.getInt("uni_sit_lKey");

                Element elem = XMLTool.createElement( doc, root, "unit" );
                elem.setAttribute( "key", Integer.toString( unitkey ) );
                // elem.setAttribute("sitekey", Integer.toString(sitekey));
                String superkey = resultSet.getString( "uni_lSuperKey" );
                if ( !resultSet.wasNull() )
                {
                    elem.setAttribute( "superkey", superkey );
                }
                elem.setAttribute( "languagekey", resultSet.getString( "uni_lan_lKey" ) );
                elem.setAttribute( "language", resultSet.getString( "lan_sDescription" ) );
                elem.setAttribute( "categorykey", resultSet.getString( "cat_lKey" ) );
                elem.setAttribute( "categoryname", resultSet.getString( "cat_sName" ) );

                // sub-elements
                XMLTool.createElement( doc, elem, "name", resultSet.getString( "uni_sName" ) );
                String description = resultSet.getString( "uni_sDescription" );
                if ( !resultSet.wasNull() )
                {
                    XMLTool.createElement( doc, elem, "description", description );
                }

                Timestamp timestamp = resultSet.getTimestamp( "uni_dteTimestamp" );
                XMLTool.createElement( doc, elem, "timestamp", CalendarUtil.formatTimestamp( timestamp, true ) );

                Element ctyElem = XMLTool.createElement( doc, elem, "contenttypes" );
                int[] contentTypeKeys = getUnitContentTypes( unitkey );
                for ( int contentTypeKey : contentTypeKeys )
                {
                    XMLTool.createElement( doc, ctyElem, "contenttype" ).setAttribute( "key", String.valueOf( contentTypeKey ) );
                }
            }
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to get units: %t";
            VerticalEngineLogger.error(message, sqle );
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
        }

        return doc;
    }

    public String getUnitName( int unitKey )
    {
        final UnitEntity entity = this.unitDao.findByKey(unitKey);
        return entity != null ? entity.getName() : null;
    }

    public Document getUnitNamesXML( Filter filter )
    {

        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;
        Document doc;

        try
        {
            doc = XMLTool.createDocument( "unitnames" );
            Element root = doc.getDocumentElement();

            StringBuffer sql = new StringBuffer( UNI_SELECT_NAME );
            sql.append(" ORDER BY uni_sName ASC");

            con = getConnection();
            preparedStmt = con.prepareStatement( sql.toString() );
            resultSet = preparedStmt.executeQuery();

            while ( resultSet.next() )
            {
                if ( filter != null && filter.filter( baseEngine, resultSet ) )
                {
                    continue;
                }

                String siteName = resultSet.getString( "uni_sName" );
                Element unitname = XMLTool.createElement( doc, root, "unitname", siteName );
                unitname.setAttribute( "key", resultSet.getString( "uni_lKey" ) );
                unitname.setAttribute( "categoryname", resultSet.getString( "cat_sName" ) );
                unitname.setAttribute( "categorykey", resultSet.getString( "cat_lKey" ) );
                unitname.setAttribute( "languagekey", resultSet.getString( "lan_lKey" ) );
                unitname.setAttribute( "language", resultSet.getString( "lan_sDescription" ) );
                unitname.setAttribute( "languagecode", resultSet.getString( "lan_sCode" ) );
            }

            resultSet.close();
            resultSet = null;
            preparedStmt.close();
            preparedStmt = null;
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to get unit names: %t";
            VerticalEngineLogger.error(message, sqle );
            doc = null;
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
        }

        return doc;
    }

    public XMLDocument getUnits()
    {
        StringBuffer sql = new StringBuffer( UNI_SELECT );
        return XMLDocumentFactory.create(getUnit(sql.toString(), null));
    }

    public void updateUnit( String xmlData )
    {

        Document doc = XMLTool.domparse( xmlData, "unit" );
        updateUnit( doc );
    }

    private void updateUnit( Document unitDoc )
    {

        // XML DOM
        Element root = unitDoc.getDocumentElement();

        // get the unit's sub-elements
        Map subelems = XMLTool.filterElements( root.getChildNodes() );

        // connection variables
        Connection con = null;
        PreparedStatement preparedStmt = null;

        try
        {
            // get the keys
            int unitkey = Integer.parseInt( root.getAttribute( "key" ) );
            // int sitekey = Integer.parseInt(root.getAttribute("sitekey"));
            int superkey = -1;
            String key = root.getAttribute( "superkey" );
            if ( key.length() > 0 )
            {
                superkey = Integer.parseInt( key );
            }
            int languageKey = Integer.parseInt( root.getAttribute( "languagekey" ) );

            con = getConnection();
            preparedStmt = con.prepareStatement( UNI_UPDATE );

            // attribute: key
            preparedStmt.setInt( 5, unitkey );

            // attribute: superkey
            if ( superkey >= 0 )
            {
                preparedStmt.setInt( 4, superkey );
            }
            else
            {
                preparedStmt.setNull( 4, Types.INTEGER );
            }

            // attribute: languagekey
            preparedStmt.setInt( 1, languageKey );

            // element: name
            Element subelem = (Element) subelems.get( "name" );
            String name = XMLTool.getElementText( subelem );
            preparedStmt.setString( 2, name );

            // element: description
            subelem = (Element) subelems.get( "description" );
            if ( subelem != null )
            {
                String description = XMLTool.getElementText( subelem );
                if ( description == null )
                {
                    preparedStmt.setNull( 3, Types.VARCHAR );
                }
                else
                {
                    preparedStmt.setString( 3, description );
                }
            }
            else
            {
                preparedStmt.setNull( 3, Types.VARCHAR );
            }

            // element: timestamp (using the database timestamp at update)
            /* no code */

            // update the unit
            preparedStmt.executeUpdate();

            // Set content types
            Element[] contentTypeElems = XMLTool.getElements( XMLTool.getElement( root, "contenttypes" ) );
            int[] contentTypeKeys = new int[contentTypeElems.length];
            for ( int i = 0; i < contentTypeElems.length; i++ )
            {
                contentTypeKeys[i] = Integer.parseInt( contentTypeElems[i].getAttribute( "key" ) );
            }
            setUnitContentTypes( unitkey, contentTypeKeys );
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to update unit: %t";
            VerticalEngineLogger.errorUpdate(message, sqle );
        }
        catch ( NumberFormatException nfe )
        {
            String message = "Failed to parse a unit key: %t";
            VerticalEngineLogger.errorUpdate(message, nfe );
        }
        finally
        {
            close( preparedStmt );
        }
    }

    private void setUnitContentTypes( int unitKey, int[] contentTypeKeys )
    {
        final UnitEntity entity = this.unitDao.findByKey(unitKey);
        if (entity == null) {
            return;
        }

        entity.getContentTypes().clear();

        for (final int contentTypeKey : contentTypeKeys) {
            final ContentTypeEntity contentType = this.contentTypeDao.findByKey(contentTypeKey);
            if (contentType != null) {
                entity.getContentTypes().add(contentType);
            }
        }
    }

    private int[] getUnitContentTypes( int unitKey )
    {
        final UnitEntity entity = this.unitDao.findByKey(unitKey);
        if (entity == null) {
            return new int[0];
        }

        final Set<ContentTypeEntity> contentTypes = entity.getContentTypes();

        int index = 0;
        final int[] result = new int[contentTypes.size()];
        for (final ContentTypeEntity contentType : contentTypes) {
            result[index++] = contentType.getKey();
        }

        return result;
    }

    public int getUnitLanguageKey( int unitKey )
    {
        final UnitEntity entity = this.unitDao.findByKey(unitKey);
        return entity != null ? entity.getLanguage().getKey().toInt() : -1;
    }
}

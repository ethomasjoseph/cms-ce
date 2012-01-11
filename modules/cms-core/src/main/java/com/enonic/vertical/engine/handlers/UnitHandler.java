/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.handlers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;

import com.enonic.cms.core.content.UnitEntity;
import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.engine.VerticalCreateException;
import com.enonic.vertical.engine.VerticalEngineLogger;

public class UnitHandler
    extends BaseHandler
{
    // Unit SQL

    private final static String UNI_TABLE = "tUnit";

    private final static String UNI_INSERT =
        "INSERT INTO tUnit" + " (uni_lKey,uni_lan_lKey,uni_sName,uni_sDescription," + "uni_lSuperKey,uni_bDeleted,uni_dteTimestamp)" +
            " VALUES (?,?,?,?,?,?," + "@currentTimestamp@" + ")";

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
}

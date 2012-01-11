/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.handlers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import com.google.common.collect.Sets;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enonic.esl.util.StringUtil;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.engine.AccessRight;
import com.enonic.vertical.engine.CategoryAccessRight;
import com.enonic.vertical.engine.VerticalCreateException;
import com.enonic.vertical.engine.VerticalEngineLogger;
import com.enonic.vertical.engine.dbmodel.CatAccessRightView;
import com.enonic.cms.framework.util.UUIDGenerator;

import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserKey;

final public class SecurityHandler
    extends BaseHandler
{
    private final static String MENUITEMAR_TABLE = "tMenuItemAR";

    private final static String DEFAULTMENUAR_TABLE = "tDefaultMenuAR";

    private final static String CAR_TABLE = "tCatAccessRight";

    private final static String COA_TABLE = "tConAccessRight2";

    private final static String MENUITEMAR_INSERT = "INSERT INTO " + MENUITEMAR_TABLE +
        " (mia_mei_lkey, mia_grp_hkey, mia_bRead, mia_bCreate, mia_bPublish, mia_bAdministrate, mia_bUpdate, mia_bDelete, mia_bAdd)" +
        " VALUES (?,?,?,?,?,?,?,?,?)";

    private final static String DEFAULTMENUAR_INSERT = "INSERT INTO " + DEFAULTMENUAR_TABLE +
        " (dma_men_lkey, dma_grp_hkey, dma_bRead, dma_bCreate, dma_bDelete, dma_bPublish, dma_bAdministrate, dma_bUpdate, dma_bAdd)" +
        " VALUES (?,?,?,?,?,?,?,?,?)";

    private final static String DEFAULTMENUAR_REMOVE_ALL = "DELETE FROM " + DEFAULTMENUAR_TABLE + " WHERE dma_men_lKey = ?";

    private final static String MENUITEMAR_REMOVE_ALL = "DELETE FROM " + MENUITEMAR_TABLE + " WHERE mia_mei_lKey = ?";

    private final static String MENUITEMAR_SECURITY_FILTER_1 =
        " EXISTS (SELECT mia_mei_lKey FROM " + MENUITEMAR_TABLE + " WHERE mia_mei_lKey = mei_lKey" + " AND mia_grp_hKey IN (";

    private final static String DEFAULTMENUAR_VALIDATE_AR_UPDATE_1 =
        " SELECT DISTINCT * FROM " + DEFAULTMENUAR_TABLE + " WHERE dma_men_lKey = ?" + " AND dma_bAdministrate = 1 AND dma_grp_hKey IN (";

    private final static String DEFAULTMENUAR_VALIDATE_AR_UPDATE_2 = ")";

    private final static String MENUITEMAR_VALIDATE_AR_UPDATE_1 =
        " SELECT DISTINCT * FROM " + MENUITEMAR_TABLE + " WHERE mia_mei_lKey = ?" + " AND mia_bAdministrate != 0 AND mia_grp_hKey IN (";

    private final static String MENUITEMAR_VALIDATE_AR_UPDATE_2 = ")";

    private final static String CAR_INSERT = "INSERT INTO " + CAR_TABLE + " VALUES (?,?,?,?,?,?,?)";

    private final static String CAR_DELETE_ALL = "DELETE FROM " + CAR_TABLE + " WHERE car_cat_lKey = ?";

    private final static String CAR_SELECT =
        "SELECT car_cat_lKey, grp_hKey, grp_sName, grp_lType, usr_hKey, usr_sUID, usr_sFullName, car_bRead, car_bCreate," +
            " car_bPublish, car_bAdministrate, car_bAdminRead FROM " + CatAccessRightView.getInstance().getReplacementSql();

    private final static String CAR_WHERE_CLAUSE_CAT = " car_cat_lKey = ?";

    private final static String CAR_WHERE_CLAUSE_GROUP_IN = " grp_hKey IN ";

    private final static String CAR_WHERE_CLAUSE_ADMINREAD = " car_bAdminRead = 1";

    private final static String CAR_WHERE_CLAUSE_SECURITY_FILTER =
        " EXISTS (SELECT car_grp_hKey FROM " + CAR_TABLE + " WHERE car_cat_lKey = cat_lKey" + " AND car_grp_hKey IN (%groups))";

    private final static String CAR_WHERE_CLAUSE_SECURITY_FILTER_RIGHTS =
        " EXISTS (SELECT car_grp_hKey FROM " + CAR_TABLE + " WHERE car_cat_lKey = cat_lKey" + " AND car_grp_hKey IN (%groups)" +
            " %filterRights )";

    private final static String CAR_SECURITY_FILTER_PUBLISH =
        "SELECT car_grp_hKey FROM " + CAR_TABLE + " WHERE car_cat_lKey = ? AND car_bPublish = 1 " + " AND car_grp_hKey IN (%0)";

    private final static String CAR_SECURITY_FILTER_ADMIN =
        "SELECT car_grp_hKey FROM " + CAR_TABLE + " WHERE car_cat_lKey = ? AND car_bAdministrate = 1 " + " AND car_grp_hKey IN (%0)";

    private final static String COA_INSERT =
        "INSERT INTO " + COA_TABLE + " (coa_con_lkey, coa_grp_hKey, coa_bRead, coa_bUpdate, coa_bDelete, coa_sKey) VALUES (?,?,?,?,?,?)";

    private final static String COA_DELETE_ALL = "DELETE FROM " + COA_TABLE + " WHERE coa_con_lKey = ?";

    private final static String COA_WHERE_CLAUSE_SECURITY_FILTER =
        " EXISTS (SELECT coa_grp_hKey FROM " + COA_TABLE + " WHERE coa_con_lKey = con_lKey" + " AND coa_grp_hKey IN (%0))";

    public String appendContentSQL( User user, int[] categoryKeys, String sql )
    {

        if ( user != null && user.isEnterpriseAdmin() )
        {
            return sql;
        }

        GroupHandler groupHandler = getGroupHandler();

        String epGroup = groupHandler.getEnterpriseAdministratorGroupKey();
        String[] groups = groupHandler.getAllGroupMembershipsForUser( user );
        Arrays.sort( groups );

        // allow if user is a member of the enterprise admin group
        if ( Arrays.binarySearch( groups, epGroup ) >= 0 )
        {
            return sql;
        }

        StringBuffer newSQL = new StringBuffer( sql );
        newSQL.append( " AND ((" );
        newSQL.append( COA_WHERE_CLAUSE_SECURITY_FILTER );
        StringBuffer temp = new StringBuffer( groups.length * 2 );
        for ( int i = 0; i < groups.length; ++i )
        {
            if ( i > 0 )
            {
                temp.append( "," );
            }
            temp.append( "'" );
            temp.append( groups[i] );
            temp.append( "'" );
        }
        newSQL.append( ")" );
        if ( categoryKeys != null && categoryKeys.length > 0 )
        {
            for ( int categoryKey : categoryKeys )
            {
                CategoryAccessRight categoryAccessRight = getCategoryAccessRight( null, user, new CategoryKey( categoryKey ) );
                if ( categoryAccessRight.getPublish() || categoryAccessRight.getAdministrate() )
                {
                    newSQL.append( " OR cat_lKey = " );
                    newSQL.append( categoryKey );
                }
            }
        }
        newSQL.append( ")" );

        return StringUtil.expandString( newSQL.toString(), temp );
    }

    private void createAccessRights( Connection _con, Element accessrightsElement )
        throws VerticalCreateException
    {

        Connection con = _con;
        PreparedStatement preparedStmt = null;
        String tmp;

        try
        {
            if ( con == null )
            {
                con = getConnection();
            }

            int key = Integer.parseInt( accessrightsElement.getAttribute( "key" ) );
            int type = Integer.parseInt( accessrightsElement.getAttribute( "type" ) );

            // prepare the appropriate SQL statement:
            switch ( type )
            {
                case AccessRight.MENUITEM:
                    preparedStmt = con.prepareStatement( MENUITEMAR_INSERT );
                    break;

                case AccessRight.MENUITEM_DEFAULT:
                    preparedStmt = con.prepareStatement( DEFAULTMENUAR_INSERT );
                    break;

                case AccessRight.CATEGORY:
                    preparedStmt = con.prepareStatement( CAR_INSERT );
                    break;

                case AccessRight.CONTENT:
                    preparedStmt = con.prepareStatement( COA_INSERT );
                    break;

                default:
                    String message = "Accessright type not supported: {0}";
                    VerticalEngineLogger.errorCreate(message, type, null );
                    break;
            }

            // set key
            preparedStmt.setInt( 1, key );

            Element[] elements = XMLTool.getElements( accessrightsElement, "accessright" );
            for ( int i = 0; i < elements.length; ++i )
            {

                // group key (all)
                String groupKey = elements[i].getAttribute( "groupkey" );
                preparedStmt.setString( 2, groupKey );

                // read (all)
                tmp = elements[i].getAttribute( "read" );
                if ( "true".equals( tmp ) )
                {
                    preparedStmt.setInt( 3, 1 );
                }
                else
                {
                    preparedStmt.setInt( 3, 0 );
                }

                switch ( type )
                {
                    case AccessRight.MENUITEM:
                        tmp = elements[i].getAttribute( "create" );
                        if ( "true".equals( tmp ) )
                        {
                            preparedStmt.setInt( 4, 1 );
                        }
                        else
                        {
                            preparedStmt.setInt( 4, 0 );
                        }

                        tmp = elements[i].getAttribute( "publish" );
                        if ( "true".equals( tmp ) )
                        {
                            preparedStmt.setInt( 5, 1 );
                        }
                        else
                        {
                            preparedStmt.setInt( 5, 0 );
                        }

                        tmp = elements[i].getAttribute( "administrate" );
                        if ( "true".equals( tmp ) )
                        {
                            preparedStmt.setInt( 6, 1 );
                        }
                        else
                        {
                            preparedStmt.setInt( 6, 0 );
                        }

                        tmp = elements[i].getAttribute( "update" );
                        if ( "true".equals( tmp ) )
                        {
                            preparedStmt.setInt( 7, 1 );
                        }
                        else
                        {
                            preparedStmt.setInt( 7, 0 );
                        }

                        tmp = elements[i].getAttribute( "delete" );
                        if ( "true".equals( tmp ) )
                        {
                            preparedStmt.setInt( 8, 1 );
                        }
                        else
                        {
                            preparedStmt.setInt( 8, 0 );
                        }

                        tmp = elements[i].getAttribute( "add" );
                        if ( "true".equals( tmp ) )
                        {
                            preparedStmt.setInt( 9, 1 );
                        }
                        else
                        {
                            preparedStmt.setInt( 9, 0 );
                        }

                        break;
                    case AccessRight.MENUITEM_DEFAULT:
                        tmp = elements[i].getAttribute( "create" );
                        if ( "true".equals( tmp ) )
                        {
                            preparedStmt.setInt( 4, 1 );
                        }
                        else
                        {
                            preparedStmt.setInt( 4, 0 );
                        }

                        tmp = elements[i].getAttribute( "delete" );
                        if ( "true".equals( tmp ) )
                        {
                            preparedStmt.setInt( 5, 1 );
                        }
                        else
                        {
                            preparedStmt.setInt( 5, 0 );
                        }

                        tmp = elements[i].getAttribute( "publish" );
                        if ( "true".equals( tmp ) )
                        {
                            preparedStmt.setInt( 6, 1 );
                        }
                        else
                        {
                            preparedStmt.setInt( 6, 0 );
                        }

                        tmp = elements[i].getAttribute( "administrate" );
                        if ( "true".equals( tmp ) )
                        {
                            preparedStmt.setInt( 7, 1 );
                        }
                        else
                        {
                            preparedStmt.setInt( 7, 0 );
                        }

                        tmp = elements[i].getAttribute( "update" );
                        if ( "true".equals( tmp ) )
                        {
                            preparedStmt.setInt( 8, 1 );
                        }
                        else
                        {
                            preparedStmt.setInt( 8, 0 );
                        }

                        tmp = elements[i].getAttribute( "add" );
                        if ( "true".equals( tmp ) )
                        {
                            preparedStmt.setInt( 9, 1 );
                        }
                        else
                        {
                            preparedStmt.setInt( 9, 0 );
                        }

                        break;

                    case AccessRight.CATEGORY:
                        tmp = elements[i].getAttribute( "create" );
                        if ( "true".equals( tmp ) )
                        {
                            preparedStmt.setInt( 4, 1 );
                        }
                        else
                        {
                            preparedStmt.setInt( 4, 0 );
                        }

                        tmp = elements[i].getAttribute( "publish" );
                        if ( "true".equals( tmp ) )
                        {
                            preparedStmt.setInt( 5, 1 );
                        }
                        else
                        {
                            preparedStmt.setInt( 5, 0 );
                        }

                        tmp = elements[i].getAttribute( "administrate" );
                        if ( "true".equals( tmp ) )
                        {
                            preparedStmt.setInt( 6, 1 );
                        }
                        else
                        {
                            preparedStmt.setInt( 6, 0 );
                        }

                        tmp = elements[i].getAttribute( "adminread" );
                        if ( "true".equals( tmp ) )
                        {
                            preparedStmt.setInt( 7, 1 );
                        }
                        else
                        {
                            preparedStmt.setInt( 7, 0 );
                        }

                        break;

                    case AccessRight.CONTENT:
                        tmp = elements[i].getAttribute( "update" );
                        if ( "true".equals( tmp ) )
                        {
                            preparedStmt.setInt( 4, 1 );
                        }
                        else
                        {
                            preparedStmt.setInt( 4, 0 );
                        }

                        tmp = elements[i].getAttribute( "delete" );
                        if ( "true".equals( tmp ) )
                        {
                            preparedStmt.setInt( 5, 1 );
                        }
                        else
                        {
                            preparedStmt.setInt( 5, 0 );
                        }

                        preparedStmt.setString( 6, UUIDGenerator.randomUUID() );
                        break;

                    case AccessRight.SECTION:
                        tmp = elements[i].getAttribute( "read" );
                        if ( "true".equals( tmp ) )
                        {
                            preparedStmt.setInt( 3, 1 );
                        }
                        else
                        {
                            preparedStmt.setInt( 3, 0 );
                        }

                        tmp = elements[i].getAttribute( "publish" );
                        if ( "true".equals( tmp ) )
                        {
                            preparedStmt.setInt( 4, 1 );
                        }
                        else
                        {
                            preparedStmt.setInt( 4, 0 );
                        }

                        tmp = elements[i].getAttribute( "approve" );
                        if ( "true".equals( tmp ) )
                        {
                            preparedStmt.setInt( 5, 1 );
                        }
                        else
                        {
                            preparedStmt.setInt( 5, 0 );
                        }

                        tmp = elements[i].getAttribute( "administrate" );
                        if ( "true".equals( tmp ) )
                        {
                            preparedStmt.setInt( 6, 1 );
                        }
                        else
                        {
                            preparedStmt.setInt( 6, 0 );
                        }

                        break;

                    default:
                        String message = "Accessright type not supported: {0}";
                        VerticalEngineLogger.errorCreate(message, type, null );
                        break;
                }

                preparedStmt.executeUpdate();
            }
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to create access rights: %t";
            VerticalEngineLogger.errorCreate(message, sqle );
        }
        finally
        {
            close( preparedStmt );
            if ( _con == null )
            {
            }
        }
    }

    private CategoryAccessRight getCategoryAccessRight( Connection _con, User user, CategoryKey categoryKey )
    {

        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;
        GroupHandler groupHandler = getGroupHandler();
        CategoryAccessRight categoryAccessRight = new CategoryAccessRight();

        // if enterprise administrator, return full rights
        String eaGroup = groupHandler.getEnterpriseAdministratorGroupKey();
        String[] groups = groupHandler.getAllGroupMembershipsForUser( user );
        Arrays.sort( groups );
        if ( user.isEnterpriseAdmin() || Arrays.binarySearch( groups, eaGroup ) >= 0 )
        {
            categoryAccessRight.setRead( true );
            categoryAccessRight.setCreate( true );
            categoryAccessRight.setPublish( true );
            categoryAccessRight.setAdministrate( true );
            categoryAccessRight.setAdminRead( true );
            return categoryAccessRight;
        }

        // [read, create, publish, administrate, adminread]
        int READ = 0, CREATE = 1, PUBLISH = 2, ADMIN = 3, ADMINREAD = 4;
        boolean[] rights = new boolean[5];

        try
        {
            if ( _con == null )
            {
                con = getConnection();
            }
            else
            {
                con = _con;
            }

            StringBuffer sql = new StringBuffer( CAR_SELECT );
            sql.append( " WHERE" );
            sql.append( CAR_WHERE_CLAUSE_CAT );
            sql.append( " AND" );
            sql.append( CAR_WHERE_CLAUSE_GROUP_IN );

            // generate list of groupkeys
            sql.append( " (" );
            for ( int i = 0; i < groups.length; ++i )
            {
                if ( i != 0 )
                {
                    sql.append( "," );
                }

                sql.append( "'" );
                sql.append( groups[i] );
                sql.append( "'" );
            }
            sql.append( ")" );

            // get accessrights for the groups
            preparedStmt = con.prepareStatement( sql.toString() );
            preparedStmt.setInt( 1, categoryKey.toInt() );
            resultSet = preparedStmt.executeQuery();

            while ( resultSet.next() )
            {
                rights[READ] |= resultSet.getBoolean( "car_bRead" );
                rights[CREATE] |= resultSet.getBoolean( "car_bCreate" );
                rights[PUBLISH] |= resultSet.getBoolean( "car_bPublish" );
                rights[ADMINREAD] |= resultSet.getBoolean( "car_bAdminRead" );
                if ( resultSet.getBoolean( "car_bAdministrate" ) )
                {
                    rights[ADMIN] = true;
                    break;
                }
            }

            categoryAccessRight.setRead( rights[READ] );
            categoryAccessRight.setCreate( rights[CREATE] );
            categoryAccessRight.setPublish( rights[PUBLISH] );
            categoryAccessRight.setAdministrate( rights[ADMIN] );
            categoryAccessRight.setAdminRead( rights[ADMINREAD] );
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to get maximum category access right: %t";
            VerticalEngineLogger.error(message, sqle );
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            if ( _con == null )
            {
            }
        }

        return categoryAccessRight;
    }

    public void inheritCategoryAccessRights( int superCategoryKey, CategoryKey categoryKey )
    {

        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;

        try
        {
            StringBuffer sql = new StringBuffer( CAR_SELECT );
            sql.append( " WHERE" );
            sql.append( CAR_WHERE_CLAUSE_CAT );

            con = getConnection();
            preparedStmt = con.prepareStatement( sql.toString() );
            preparedStmt.setInt( 1, superCategoryKey );
            resultSet = preparedStmt.executeQuery();

            ArrayList<ArrayList<Comparable<?>>> rightsList = new ArrayList<ArrayList<Comparable<?>>>();
            while ( resultSet.next() )
            {
                ArrayList<Comparable<?>> rights = new ArrayList<Comparable<?>>();
                rights.add( resultSet.getString( "grp_hKey" ) );
                rights.add( resultSet.getInt( "car_bRead" ) );
                rights.add( resultSet.getInt( "car_bCreate" ) );
                rights.add( resultSet.getInt( "car_bPublish" ) );
                rights.add( resultSet.getInt( "car_bAdministrate" ) );
                rights.add( resultSet.getInt( "car_bAdminRead" ) );

                rightsList.add( rights );
            }
            close( resultSet );
            close( preparedStmt );

            preparedStmt = con.prepareStatement( CAR_INSERT );
            for ( int i = 0; i < rightsList.size(); ++i )
            {
                preparedStmt.setInt( 1, categoryKey.toInt() );

                ArrayList<Comparable<?>> rights = rightsList.get( i );
                for ( int j = 0; j < rights.size(); ++j )
                {
                    preparedStmt.setObject( j + 2, rights.get( j ) );
                }

                preparedStmt.executeUpdate();
            }
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to inherit category access rights: %t";
            VerticalEngineLogger.error(message, sqle );
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
        }
    }

    public void updateAccessRights( User user, Document doc )
    {

        Connection con = null;
        PreparedStatement preparedStmt = null;

        Element rootElement = doc.getDocumentElement();
        int type = Integer.parseInt( rootElement.getAttribute( "type" ) );
        int key = Integer.parseInt( rootElement.getAttribute( "key" ) );

        if ( !validateAccessRightsUpdate( user, type, key ) )
        {
            VerticalEngineLogger.errorSecurity("Access denied.", null );
        }

        try
        {
            con = getConnection();

            String sql = null;
            switch ( type )
            {
                case AccessRight.MENUITEM:
                    sql = MENUITEMAR_REMOVE_ALL;
                    break;

                case AccessRight.MENUITEM_DEFAULT:
                    sql = DEFAULTMENUAR_REMOVE_ALL;
                    break;

                case AccessRight.CATEGORY:
                    sql = CAR_DELETE_ALL;
                    break;

                case AccessRight.CONTENT:
                    sql = COA_DELETE_ALL;
                    break;

                default:
                    String message = "Accessright type not supported: {0}";
                    VerticalEngineLogger.errorUpdate(message, type, null );
            }

            preparedStmt = con.prepareStatement( sql );
            preparedStmt.setInt( 1, key );
            preparedStmt.executeUpdate();

            createAccessRights( con, rootElement );
        }
        catch ( SQLException e )
        {
            VerticalEngineLogger.errorUpdate("A database error occurred: %t", e );
        }
        catch ( VerticalCreateException e )
        {
            VerticalEngineLogger.errorUpdate("Error creating accessrights: %t", e );
        }
        finally
        {
            close( preparedStmt );
        }
    }

    private boolean validateAccessRightsUpdate( User user, int type, int key )
    {

        GroupHandler groupHandler = getGroupHandler();

        if ( user.isEnterpriseAdmin() )
        {
            return true;
        }

        // array containing groups that should always
        // pass security check:
        Set<String> autoAllowGroups = Sets.newHashSet();
        autoAllowGroups.add( groupHandler.getEnterpriseAdministratorGroupKey() );

        String[] groups = getGroupHandler().getAllGroupMembershipsForUser( user );

        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;
        boolean result = false;

        try
        {
            // set the correct sql:
            String sql1 = null;
            String sql2 = null;
            switch ( type )
            {
                case AccessRight.MENUITEM:
                {
                    sql1 = MENUITEMAR_VALIDATE_AR_UPDATE_1;
                    sql2 = MENUITEMAR_VALIDATE_AR_UPDATE_2;

                    // add site administrators group to the list of approved groups:
                    autoAllowGroups.add( groupHandler.getAdminGroupKey() );

                    break;
                }

                case AccessRight.MENUITEM_DEFAULT:
                {
                    sql1 = DEFAULTMENUAR_VALIDATE_AR_UPDATE_1;
                    sql2 = DEFAULTMENUAR_VALIDATE_AR_UPDATE_2;

                    // add site administrators group to the list of approved groups:
                    autoAllowGroups.add( groupHandler.getAdminGroupKey() );

                    break;
                }

                case AccessRight.CATEGORY:
                {
                    sql1 = CAR_SECURITY_FILTER_ADMIN;

                    // add site administrators group to the list of approved groups:
                    autoAllowGroups.add( groupHandler.getAdminGroupKey() );

                    break;
                }

                case AccessRight.CONTENT:
                {
                    String userKey = user.getKey().toString();
                    UserKey ownerKey = getContentHandler().getOwnerKey( key );
                    String anonymousUserKey = getUserHandler().getAnonymousUser().getKey().toString();
                    if ( userKey.equals( ownerKey.toString() ) && userKey.equals( anonymousUserKey ) == false )
                    {
                        result = true;
                    }
                    else
                    {
                        sql1 = CAR_SECURITY_FILTER_PUBLISH;

                        // add site administrators group to the list of approved groups:
                        autoAllowGroups.add( groupHandler.getAdminGroupKey() );

                        // get the content's category key
                        key = getContentHandler().getCategoryKey( key ).toInt();
                    }
                    break;
                }

                default:
                {
                    // unknown access rights type, throw runtime exception
                    VerticalEngineLogger.fatalEngine("Access denied.", null );
                    result = false;
                }
            }

            if ( !result )
            {
                con = getConnection();

                if ( type == AccessRight.MENUITEM_DEFAULT || type == AccessRight.MENUITEM )
                {
                    StringBuffer sql = new StringBuffer( sql1 );
                    for ( int i = 0; i < groups.length; ++i )
                    {
                        if ( i != 0 )
                        {
                            sql.append( "," );
                        }

                        if ( autoAllowGroups.contains( groups[i] ) )
                        {
                            result = true;
                            break;
                        }

                        sql.append( "'" );
                        sql.append( groups[i] );
                        sql.append( "'" );
                    }
                    if ( !result )
                    {
                        sql.append( sql2 );

                        //con = getConnection();
                        preparedStmt = con.prepareStatement( sql.toString() );
                        preparedStmt.setInt( 1, key );
                        resultSet = preparedStmt.executeQuery();

                        if ( resultSet.next() )
                        {
                            result = true;
                        }
                    }
                }
                else if ( type == AccessRight.CATEGORY || type == AccessRight.CONTENT || type == AccessRight.SECTION )
                {
                    StringBuffer temp = new StringBuffer( groups.length * 2 );
                    for ( int i = 0; i < groups.length; i++ )
                    {
                        if ( i > 0 )
                        {
                            temp.append( "," );
                        }
                        if ( autoAllowGroups.contains( groups[i] ) )
                        {
                            result = true;
                            break;
                        }
                        temp.append( "'" );
                        temp.append( groups[i] );
                        temp.append( "'" );
                    }
                    if ( !result )
                    {
                        sql1 = StringUtil.expandString( sql1, temp );

                        //con = getConnection();
                        preparedStmt = con.prepareStatement( sql1 );
                        preparedStmt.setInt( 1, key );
                        resultSet = preparedStmt.executeQuery();

                        if ( resultSet.next() )
                        {
                            result = true;
                        }
                    }
                }
            }
        }
        catch ( SQLException e )
        {
            VerticalEngineLogger.error("A database error occurred: %t", e );
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
        }

        return result;
    }

    public String appendCategorySQL( User user, String sql )
    {

        StringBuffer buf = new StringBuffer( sql );

        appendCategorySQL( user, buf, true );

        return buf.toString();
    }

    public void appendCategorySQL( User user, StringBuffer sql, boolean adminread )
    {

        if ( user != null && user.isEnterpriseAdmin() )
        {
            return;
        }

        GroupHandler groupHandler = getGroupHandler();
        String[] groups = groupHandler.getAllGroupMembershipsForUser( user );
        Arrays.sort( groups );

        if ( isSiteAdmin( user, groups ) )
        {
            return;
        }

        StringBuffer newSQL = sql;
        newSQL.append( " AND" );
        StringBuffer sqlFilterRights = new StringBuffer( "" );
        if ( adminread )
        {
            newSQL.append( CAR_WHERE_CLAUSE_SECURITY_FILTER_RIGHTS );
            if ( adminread )
            {
                sqlFilterRights.append( " AND" );
                sqlFilterRights.append( CAR_WHERE_CLAUSE_ADMINREAD );
            }
        }
        else
        {
            newSQL.append( CAR_WHERE_CLAUSE_SECURITY_FILTER );
        }
        StringBuffer sb_groups = new StringBuffer( groups.length * 2 );
        for ( int i = 0; i < groups.length; ++i )
        {
            if ( i > 0 )
            {
                sb_groups.append( "," );
            }
            sb_groups.append( "'" );
            sb_groups.append( groups[i] );
            sb_groups.append( "'" );
        }

        StringUtil.replaceString( newSQL, "%groups", sb_groups.toString() );
        StringUtil.replaceString( newSQL, "%filterRights", sqlFilterRights.toString() );
    }

    public String appendMenuItemSQL( User user, String sql )
    {
        StringBuffer bufferSQL = new StringBuffer( sql );
        appendMenuItemSQL( user, bufferSQL );
        return bufferSQL.toString();
    }

    private void appendMenuItemSQL( User user, StringBuffer sql )
    {

        if ( user != null && user.isEnterpriseAdmin() )
        {
            return;
        }

        GroupHandler groupHandler = getGroupHandler();
        String epGroup = groupHandler.getEnterpriseAdministratorGroupKey();

        StringBuffer newSQL = sql;

        // find all groups that the user is a member of
        String[] groups = groupHandler.getAllGroupMembershipsForUser( user );

        // use standard sql if user is a member of
        // the enterprise admin group
        for ( int i = 0; i < groups.length; ++i )
        {
            if ( groups[i].equals( epGroup ) )
            {
                return;
            }
        }

        newSQL.append( " AND ( " );

        // appending group clause
        newSQL.append( MENUITEMAR_SECURITY_FILTER_1 );
        for ( int i = 0; i < groups.length; ++i )
        {
            if ( i != 0 )
            {
                newSQL.append( "," );
            }
            newSQL.append( "'" );
            newSQL.append( groups[i] );
            newSQL.append( "'" );
        }
        newSQL.append( ")" );

        newSQL.append( ")" );
        newSQL.append( ")" );
    }

    public boolean isEnterpriseAdmin( User user )
    {
        if ( user.isEnterpriseAdmin() )
        {
            return true;
        }

        GroupHandler groupHandler = getGroupHandler();
        String epGroup = groupHandler.getEnterpriseAdministratorGroupKey();

        String[] groups = groupHandler.getAllGroupMembershipsForUser( user );
        for ( int i = 0; i < groups.length; ++i )
        {
            if ( groups[i].equals( epGroup ) )
            {
                return true;
            }
        }

        return false;
    }

    private boolean isSiteAdmin( User user, String[] groups )
    {
        if ( user.isEnterpriseAdmin() )
        {
            return true;
        }

        GroupHandler groupHandler = getGroupHandler();
        String epGroup = groupHandler.getEnterpriseAdministratorGroupKey();
        String saGroup = groupHandler.getAdminGroupKey();

        for ( int i = 0; i < groups.length; ++i )
        {
            if ( groups[i].equals( epGroup ) || groups[i].equals( saGroup ) )
            {
                return true;
            }
        }

        return false;
    }
}

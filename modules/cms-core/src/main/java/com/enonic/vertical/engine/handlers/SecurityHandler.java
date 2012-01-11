/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.handlers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import com.enonic.esl.util.StringUtil;
import com.enonic.vertical.engine.CategoryAccessRight;
import com.enonic.vertical.engine.VerticalEngineLogger;
import com.enonic.vertical.engine.dbmodel.CatAccessRightView;

import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.security.user.User;

final public class SecurityHandler
    extends BaseHandler
{
    private final static String MENUITEMAR_TABLE = "tMenuItemAR";

    private final static String CAR_TABLE = "tCatAccessRight";

    private final static String COA_TABLE = "tConAccessRight2";

    private final static String MENUITEMAR_SECURITY_FILTER_1 =
        " EXISTS (SELECT mia_mei_lKey FROM " + MENUITEMAR_TABLE + " WHERE mia_mei_lKey = mei_lKey" + " AND mia_grp_hKey IN (";

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
                CategoryAccessRight categoryAccessRight = getCategoryAccessRight( user, new CategoryKey( categoryKey ) );
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

    private CategoryAccessRight getCategoryAccessRight( User user, CategoryKey categoryKey )
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
            con = getConnection();

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
        }

        return categoryAccessRight;
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

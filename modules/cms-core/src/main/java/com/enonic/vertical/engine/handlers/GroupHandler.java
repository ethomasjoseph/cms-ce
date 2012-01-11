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
import java.util.HashSet;
import java.util.Set;

import com.enonic.cms.store.dao.GroupDao;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.vertical.engine.VerticalEngineLogger;
import com.enonic.vertical.event.VerticalEventListener;
import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserType;
import com.enonic.cms.core.security.userstore.UserStoreKey;

public final class GroupHandler
    extends BaseHandler
    implements VerticalEventListener
{
    final static private String GROUP_TABLE = "tGroup";

    final static private String GRPGRPMEM_TABLE = "tGrpGrpMembership";

    final static private String GRPGRPMEM_GET_MULTIPLE_MEMBERSHIPS =
        "SELECT * FROM " + GRPGRPMEM_TABLE + " LEFT JOIN " + GROUP_TABLE + " ON " + GROUP_TABLE + ".grp_hKey = " + GRPGRPMEM_TABLE +
            ".ggm_mbr_grp_hKey WHERE ggm_mbr_grp_hKey IN ";

    @Autowired
    private GroupDao groupDao;

    private String getAuthenticatedUsersGroupKey( final UserStoreKey userStoreKey )
    {
        if ( userStoreKey == null )
        {
            return null;
        }

        final GroupEntity entity = this.groupDao.findBuiltInAuthenticatedUsers(userStoreKey);
        if ( entity == null ) {
            return null;
        }
        
        return entity.getGroupKey().toString();
    }

    public String[] getAllGroupMembershipsForUser( User user )
    {
        return getAllGroupMembershipsForUser( user.getKey().toString(), user.getType(), user.getUserGroupKey(), user.getUserStoreKey() );
    }

    private String[] getAllGroupMembershipsForUser( String userKey, UserType userType, GroupKey userGroupKey, UserStoreKey userStoreKey )
    {

        Connection con = null;
        String[] groups;
        if ( userKey != null )
        {
            if ( userType != UserType.ANONYMOUS && userType != UserType.ADMINISTRATOR )
            {
                groups = new String[]{userGroupKey.toString(), getAuthenticatedUsersGroupKey( userStoreKey ), getAnonymousGroupKey()};
            }
            else
            {
                groups = new String[]{getAnonymousGroupKey()};
            }
        }
        else
        {
            groups = new String[]{getAnonymousGroupKey()};
        }

        Set<String> excludeGroups = new HashSet<String>();

        excludeGroups.addAll( Arrays.asList( groups ) );

        try
        {
            con = getConnection();
            String[] addGroups = null;
            while ( addGroups == null || addGroups.length > 0 )
            {
                addGroups = getGroupMemberships( con, groups, excludeGroups );

                // extend group array
                String[] newGroups = new String[groups.length + addGroups.length];
                System.arraycopy( groups, 0, newGroups, 0, groups.length ); // copy old groups
                System.arraycopy( addGroups, 0, newGroups, groups.length, addGroups.length ); // add new groups

                // add groups to the excluded set
                excludeGroups.addAll( Arrays.asList( addGroups ) );

                groups = newGroups;
            }
        }
        catch ( SQLException e )
        {
            VerticalEngineLogger.error("A database error occurred: %t", e );
        }

        return groups;
    }

    private String[] getGroupMemberships( Connection _con, String[] groups, Set<String> excludeGroups )
    {

        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;
        ArrayList<String> newGroups = new ArrayList<String>();

        Connection con = _con;

        try
        {
            if ( con == null )
            {
                con = getConnection();
            }

            StringBuffer sql = new StringBuffer( "(" );
            int groupCount = 0;
            for ( int i = 0; i < groups.length; ++i )
            {
                if ( i > 0 )
                {
                    sql.append( "," );
                }

                sql.append( "'" );
                sql.append( groups[i] );
                sql.append( "'" );
                ++groupCount;
            }
            sql.append( ")" );

            // there is no point in going any further if there are no groups
            if ( groupCount > 0 )
            {
                preparedStmt = con.prepareStatement( GRPGRPMEM_GET_MULTIPLE_MEMBERSHIPS + sql.toString() );

                resultSet = preparedStmt.executeQuery();

                while ( resultSet.next() )
                {
                    String groupKey = resultSet.getString( "ggm_grp_hKey" );
                    if ( ( excludeGroups == null || !excludeGroups.contains( groupKey ) ) && !newGroups.contains( groupKey ) )
                    {
                        newGroups.add( groupKey );
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
            if ( _con == null )
            {
            }
        }

        return newGroups.toArray( new String[newGroups.size()] );
    }

    public String getAdminGroupKey()
    {
        final GroupEntity entity = this.groupDao.findBuiltInAdministrator();
        return entity != null ? entity.getGroupKey().toString() : null;
    }

    private String getAnonymousGroupKey()
    {
        final GroupEntity entity = this.groupDao.findBuiltInAnonymous();
        return entity != null ? entity.getGroupKey().toString() : null;
    }

    public String getEnterpriseAdministratorGroupKey()
    {
        final GroupEntity entity = this.groupDao.findBuiltInEnterpriseAdministrator();
        return entity != null ? entity.getGroupKey().toString() : null;
    }
}


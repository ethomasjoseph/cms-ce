package com.enonic.cms.admin.group;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.MatchMode;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sun.jersey.api.NotFoundException;

import com.enonic.cms.core.search.SearchSortOrder;
import com.enonic.cms.core.search.account.AccountIndexData;
import com.enonic.cms.core.search.account.AccountIndexField;
import com.enonic.cms.core.search.account.AccountKey;
import com.enonic.cms.core.search.account.AccountSearchHit;
import com.enonic.cms.core.search.account.AccountSearchQuery;
import com.enonic.cms.core.search.account.AccountSearchResults;
import com.enonic.cms.core.search.account.AccountSearchService;
import com.enonic.cms.core.search.account.Group;
import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.group.StoreNewGroupCommand;
import com.enonic.cms.core.security.group.UpdateGroupCommand;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.core.security.userstore.UserStoreService;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.UserDao;

@Component
@Path("/admin/data/group")
@Produces(MediaType.APPLICATION_JSON)
public final class GroupResource
{
    private static final Logger LOG = LoggerFactory.getLogger( GroupResource.class );

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserStoreService userStoreService;

    @Autowired
    private AccountSearchService searchService;

    @Autowired
    private GroupModelTranslator groupModelTranslator;

    @Autowired
    protected SecurityService securityService;

    @POST
    @Path("join")
    public void join( @FormParam("key") @DefaultValue("") final String key,
                      @FormParam("isUser") @DefaultValue("false") final boolean isUser,
                      @FormParam("join") final List<String> join )
    {

        GroupEntity groupToAddTo = null;
        if ( isUser )
        {
            UserEntity user = userDao.findByKey( key );
            if ( user != null )
            {
                groupToAddTo = user.getUserGroup();
            }
        }
        else
        {
            groupToAddTo = groupDao.find( key );
        }

        if ( groupToAddTo != null )
        {
            GroupEntity groupToAdd;
            for ( String s : join )
            {
                groupToAdd = groupDao.find( s );
                if ( groupToAdd != null && !groupToAdd.hasMembership( groupToAddTo ) )
                {
                    groupToAdd.addMembership( groupToAddTo );
                }
            }
        }
        //TODO: Add update user group code
    }


    @POST
    @Path("leave")
    public void leave( @FormParam("key") @DefaultValue("") final String key,
                       @FormParam("isUser") @DefaultValue("false") final boolean isUser,
                       @FormParam("leave") final List<String> leave )
    {

        GroupEntity groupToRemoveFrom = null;
        if ( isUser )
        {
            UserEntity user = userDao.findByKey( key );
            if ( user != null )
            {
                groupToRemoveFrom = user.getUserGroup();
            }
        }
        else
        {
            groupToRemoveFrom = groupDao.find( key );
        }

        if ( groupToRemoveFrom != null )
        {
            GroupEntity groupToRemove;
            for ( String s : leave )
            {
                groupToRemove = groupDao.find( s );
                if ( groupToRemove != null && groupToRemove.hasMembership( groupToRemoveFrom ) )
                {
                    groupToRemove.removeMembership( groupToRemoveFrom );
                }
            }
        }
        //TODO: Add update user group code
    }


    @POST
    @Path("delete")
    public void delete( @FormParam("key") @DefaultValue("") final String key )
    {
        GroupEntity group = groupDao.find( key );
        if ( group != null )
        {
            groupDao.delete( group );
        }
    }

    @GET
    @Path( "list_" )
    public List<GroupModel> getGroups(@QueryParam("query") String query){
        List<GroupEntity> groups = groupDao.findByCriteria( query, null, true, MatchMode.START );
        return groupModelTranslator.toListModel( groups );
    }

    @GET
    @Path( "list" )
    public List<GroupModel> getGroups( @QueryParam("query") String query,
                                       @QueryParam("key") String key,
                                       @QueryParam("limit") @DefaultValue("50") final int limit )
    {
        if ( StringUtils.isNotEmpty( key ) )
        {
            final String[] groupKeys = StringUtils.split( key, "," );
            return getGroups( groupKeys );
        }
        final AccountSearchQuery searchQueryCountFacets = new AccountSearchQuery()
            .setCount( limit )
            .setIncludeResults( true )
            .setQuery( query )
            .setGroups( true )
            .setUsers( false )
            .setIncludeFacets( false )
            .setSortField( AccountIndexField.DISPLAY_NAME_FIELD )
            .setSortOrder( SearchSortOrder.ASC );

        final AccountSearchResults searchResults = searchService.search( searchQueryCountFacets );

        final List<GroupEntity> groups = new ArrayList<GroupEntity>();

        for ( AccountSearchHit searchHit : searchResults )
        {
            GroupEntity groupEntity = this.groupDao.findByKey( new GroupKey( searchHit.getKey().toString() ) );
            groups.add( groupEntity );
        }

        return groupModelTranslator.toListModel( groups );
    }

    private List<GroupModel> getGroups( final String... groupKeys )
    {
        final List<GroupEntity> groups = new ArrayList<GroupEntity>();
        for ( String groupKey : groupKeys )
        {
            GroupEntity groupEntity = this.groupDao.findByKey( new GroupKey( groupKey.trim() ) );
            if ( groupEntity != null )
            {
                groups.add( groupEntity );
            }
        }

        return groupModelTranslator.toListModel( groups );
    }
    
    @GET
    @Path("detail")
    public GroupModel getGroupDetails(@QueryParam("key") final String key)
    {
        final GroupEntity entity = findEntity( key );

        return groupModelTranslator.toModel( entity );
    }

    @POST
    @Path("update")
    @Consumes("application/json")
    public Map<String, Object> updateGroup( GroupModel group )
    {
        final boolean isValid = isValidGroupData( group );
        final Map<String, Object> res = new HashMap<String, Object>();
        if ( isValid )
        {
            if ( group.getKey() == null )
            {
                StoreNewGroupCommand command = groupModelTranslator.toNewGroupCommand( group );
                command.setExecutor( getCurrentUser() );
                GroupKey groupKey = userStoreService.storeNewGroup( command );
                res.put( "groupkey", groupKey.toString() );
                indexGroup( groupKey.toString() );
            }
            else
            {
                UpdateGroupCommand command = groupModelTranslator.toUpdateGroupCommand( group, getCurrentUser().getKey() );
                userStoreService.updateGroup( command );
                res.put( "groupkey", group.getKey() );
                indexGroup( group.getKey() );
            }
            res.put( "success", true );
        }
        else
        {
            res.put( "success", false );
            res.put( "error", "Validation failed" );
        }
        return res;
    }

    private boolean isValidGroupData( GroupModel groupData )
    {
        if ( StringUtils.isBlank( groupData.getName() ) )
        {
            return false;
        }

        return validateMembersInUserStore(groupData);
    }

    private boolean validateMembersInUserStore( GroupModel groupData )
    {
        UserStoreEntity userStore = ( groupData.getUserStore() == null ) ? null : userStoreService.findByName( groupData.getUserStore() );
        if ( userStore == null )
        {
            userStore = userStoreService.getDefaultUserStore();
        }
        final UserStoreKey userStoreKey = userStore.getKey();

        List<Map<String, String>> members = groupData.getMembers();
        for ( Map<String, String> memberFields : members )
        {
            final String memberKey = memberFields.get( "key" );
            final UserStoreKey memberUserStoreKey = getMemberUserStore( memberKey );
            if ( memberUserStoreKey == null || ( !memberUserStoreKey.equals( userStoreKey ) ) )
            {
                LOG.warn( memberKey + " cannot be member of group '" + groupData.getName() +
                              "'. Group and member must be located in same user store." );
                return false;
            }
        }
        return true;
    }

    private UserStoreKey getMemberUserStore( final String memberKey )
    {
        final UserKey userKey = new UserKey( memberKey );
        final UserEntity user = securityService.getUser( userKey );
        if ( user != null )
        {
            return user.getUserStoreKey();
        }
        else
        {
            final GroupEntity group = securityService.getGroup( new GroupKey( memberKey ) );
            return group == null ? null : group.getUserStoreKey();
        }
    }

    private void indexGroup( final String groupKey )
    {
        final GroupEntity groupEntity = this.groupDao.find( groupKey );
        if ( groupEntity == null )
        {
            searchService.deleteIndex( groupKey );
            return;
        }

        final Group group = new Group();
        group.setKey( new AccountKey( groupEntity.getGroupKey().toString() ) );
        group.setName( groupEntity.getName() );
        group.setDisplayName( groupEntity.getDisplayName() );
        group.setGroupType( groupEntity.getType() );
        if ( groupEntity.getUserStore() != null )
        {
            group.setUserStoreName( groupEntity.getUserStore().getName() );
        }
        final DateTime lastModified = ( groupEntity.getLastModified() == null ) ? null : new DateTime( groupEntity.getLastModified() );
        group.setLastModified( lastModified );
        final AccountIndexData accountIndexData = new AccountIndexData( group );

        searchService.index( accountIndexData );
    }

    private GroupEntity findEntity( final String key )
    {
        if ( key == null )
        {
            throw new NotFoundException();
        }

        final GroupEntity entity = this.groupDao.find( key );
        if ( ( entity == null ) || entity.isDeleted() )
        {
            throw new NotFoundException();
        }

        return entity;
    }

    private UserEntity getCurrentUser()
    {
        return userDao.findBuiltInEnterpriseAdminUser();
    }

}

package com.enonic.cms.admin.group;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.group.StoreNewGroupCommand;
import com.enonic.cms.core.security.group.UpdateGroupCommand;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.store.dao.UserStoreDao;

@Component
public class GroupModelTranslator
{

    @Autowired
    private UserStoreDao userStoreDao;

    @Autowired
    protected SecurityService securityService;

    public GroupModel toModel( final GroupEntity entity )
    {
        final GroupModel model = new GroupModel();
        model.setKey( entity.getGroupKey().toString() );
        model.setName( entity.getName() );
        model.setQualifiedName( entity.getQualifiedName().toString() );
        model.setDisplayName( entity.getDisplayName() );
        model.setDescription( entity.getDescription() );
        model.setLastModified( entity.getLastModified() );
        model.setRestricted( entity.isRestricted() );
        model.setBuiltIn( entity.isBuiltIn() );

        if ( entity.getUserStore() != null )
        {
            model.setUserStore( entity.getUserStore().getName() );
        }
        else
        {
            model.setUserStore( "system" );
        }

        return model;
    }

    public List<GroupModel> toListModel( final List<GroupEntity> groups )
    {
        List<GroupModel> groupModels = new ArrayList<GroupModel>();
        for ( GroupEntity gr : groups )
        {
            groupModels.add( toModel( gr ) );
        }
        return groupModels;
    }

    public StoreNewGroupCommand toNewGroupCommand( GroupModel group )
    {
        final StoreNewGroupCommand command = new StoreNewGroupCommand();

        UserStoreEntity userStore = ( group.getUserStore() == null ) ? null : userStoreDao.findByName( group.getUserStore() );
        if ( userStore == null )
        {
            userStore = userStoreDao.findDefaultUserStore();
        }
        command.setDescription( group.getDescription() );
        command.setName( group.getName() );
        command.setRestriced( group.isRestricted() );
        if ( userStore != null )
        {
            command.setUserStoreKey( userStore.getKey() );
            command.setType( GroupType.USERSTORE_GROUP );
        }
        else
        {
            command.setType( GroupType.GLOBAL_GROUP );
        }

        for ( Map<String, String> memberFields : group.getMembers() )
        {
            final String memberKey = memberFields.get( "key" );
            final GroupKey groupKey = getMemberGroupKey( memberKey );
            command.addMember( groupKey );
        }

        return command;
    }

    public UpdateGroupCommand toUpdateGroupCommand( GroupModel group, UserKey updater )
    {
        final UpdateGroupCommand command = new UpdateGroupCommand( updater, new GroupKey( group.getKey() ) );

        UserStoreEntity userStore = ( group.getUserStore() == null ) ? null : userStoreDao.findByName( group.getUserStore() );
        if ( userStore == null )
        {
            userStore = userStoreDao.findDefaultUserStore();
        }
        command.setDescription( group.getDescription() );
        command.setName( group.getName() );
        command.setRestricted( group.isRestricted() );

        for ( Map<String, String> memberFields : group.getMembers() )
        {
            final String memberKey = memberFields.get( "key" );
            final GroupKey groupKey = getMemberGroupKey( memberKey );
            final GroupEntity groupMember = securityService.getGroup( groupKey );
            command.addMember( groupMember );
        }

        return command;
    }

    private GroupKey getMemberGroupKey( final String memberKey )
    {
        final UserKey userKey = new UserKey( memberKey );
        final UserEntity user = securityService.getUser( userKey );
        if ( user != null )
        {
            return user.getUserGroupKey();
        }
        else
        {
            return new GroupKey( memberKey );
        }
    }

}

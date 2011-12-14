package com.enonic.cms.admin.account;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.user.UserEntity;

import com.enonic.cms.domain.EntityPageList;

@Component
public final class AccountModelTranslator
{

    AccountModel toAModel( final UserEntity entity )
    {
        final UserModel model = new UserModel();
        model.setKey( entity.getKey().toString() );
        model.setName( entity.getName() );
        model.setEmail( entity.getEmail() );
        model.setQualifiedName( entity.getQualifiedName().toString() );
        model.setDisplayName( entity.getDisplayName() );
        model.setLastModified( entity.getLastModified() );
        //TODO: not implemented
        model.setLastLogged( "2001-01-01" );
        //TODO: not implemented
        model.setCreated( "1998-09-13" );
        List<Map<String, String>> groups = new ArrayList<Map<String, String>>();
        for ( GroupEntity group : entity.getAllMembershipsGroups() )
        {
            Map<String, String> groupMap = new HashMap<String, String>();
            groupMap.put( "name", group.getDisplayName() );
            groupMap.put( "key", group.getGroupKey().toString() );
            groups.add( groupMap );
        }
        model.setGroups( groups );
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

    private AccountModel toAModel( final GroupEntity entity )
    {
        final GroupModel model = new GroupModel();
        model.setKey( entity.getGroupKey().toString() );
        model.setName( entity.getName() );
        model.setQualifiedName( entity.getQualifiedName().toString() );
        model.setDisplayName( entity.getDisplayName() );
        model.setLastModified( entity.getLastModified() );

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

    AccountsModel toModel( final Collection<UserEntity> userList, final Collection<GroupEntity> groupList )
    {
        final AccountsModel model = new AccountsModel();
        model.setTotal( userList.size() + groupList.size() );

        for ( final UserEntity entity : userList )
        {
            AccountModel aModel = toAModel( entity );
            model.addAccount( aModel );
        }
        for ( final GroupEntity entity : groupList )
        {
            AccountModel aModel = toAModel( entity );
            model.addAccount( aModel );
        }

        return model;
    }

    AccountsModel toModel( final EntityPageList accountList )
    {
        final AccountsModel model = new AccountsModel();
        model.setTotal( accountList.getTotal() );

        for ( final Object entity : accountList.getList() )
        {
            if ( entity instanceof UserEntity )
            {
                AccountModel aModel = toAModel( (UserEntity) entity );
                model.addAccount( aModel );
            }
            else if ( entity instanceof GroupEntity )
            {
                AccountModel aModel = toAModel( (GroupEntity) entity );
                model.addAccount( aModel );
            }
            else
            {
                throw new IllegalArgumentException( "Expected UserEntity or GroupEntity." );
            }
        }

        return model;
    }

    AccountModel toGroupInfo( GroupEntity group )
    {
        GroupModel groupModel = (GroupModel) toAModel( group );
        List<AccountModel> members = new ArrayList<AccountModel>();
        for ( GroupEntity member : group.getMembers( false ) )
        {
            AccountModel accountModel = null;
            if ( member.getType().equals( GroupType.USER ) )
            {
                accountModel = toAModel( member.getUser() );
            }
            else
            {
                accountModel = toAModel( member );
            }
            members.add( accountModel );
        }
        groupModel.setMembers( members );
        return groupModel;
    }
}

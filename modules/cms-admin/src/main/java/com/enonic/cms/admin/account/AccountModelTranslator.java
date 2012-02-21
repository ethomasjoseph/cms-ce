package com.enonic.cms.admin.account;

import com.enonic.cms.admin.user.AddressModel;
import com.enonic.cms.admin.user.UserInfoModel;
import com.enonic.cms.api.client.model.user.Address;
import com.enonic.cms.api.client.model.user.UserInfo;
import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.domain.EntityPageList;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

@Component
public final class AccountModelTranslator
{
    private SimpleDateFormat dateFormatter = new SimpleDateFormat( "yyyy-MM-dd" );

    AccountModel toAModel( final UserEntity entity )
    {
        final UserModel model = new UserModel();
        model.setKey( entity.getKey().toString() );
        model.setName( entity.getName() );
        model.setEmail( entity.getEmail() );
        model.setQualifiedName( entity.getQualifiedName().toString() );
        model.setDisplayName( entity.getDisplayName() );
        model.setLastModified( entity.getLastModified() );
        model.setBuiltIn( entity.isBuiltIn() );
        model.setEditable( !entity.isAnonymous() && !isEnterpriseAdminUser( entity ) );
        //TODO: not implemented
        model.setLastLogged( "2001-01-01" );
        //TODO: not implemented
        model.setCreated( "1998-09-13" );
        String qName;
        List<Map<String, String>> groups = new ArrayList<Map<String, String>>();
        for ( GroupEntity group : entity.getAllMemberships() )
        {
            Map<String, String> groupMap = new HashMap<String, String>();
            groupMap.put( "name", group.getDisplayName() );
            qName = group.getQualifiedName().getUserStoreName();
            groupMap.put( "qualifiedName", group.getDisplayName() + ( qName != null ? " (" + qName + ")" : "" ) );
            groupMap.put( "type", group.isBuiltIn()? "role" : "group" );
            groupMap.put( "key", group.getGroupKey().toString() );
            groupMap.put( "direct", String.valueOf( entity.isMemberOf( group, false ) ) );
            groups.add( groupMap );
        }
        Collections.sort( groups, new Comparator<Map<String, String>>() {
            @Override
            public int compare(Map<String, String> group1, Map<String, String> group2) {
                int result = 0;
                if ( group1 == null && group2 != null )
                    result = 1;
                else if ( group1 != null && group2 == null )
                    result = -1;
                else if ( group1 != null && group2 != null ) {
                    String name1 = group1.get("name"),
                            name2 = group2.get("name");
                    if ( name1 == null && name2 != null )
                        result = 1;
                    else if ( name1 != null && name2 == null )
                        result = -1;
                    else if ( name1 != null && name2 != null )
                        result = name1.compareTo( name2 );
                }
                return result;
            }
        });
        model.setGroups( groups );

        if ( entity.getUserStore() != null )
        {
            model.setUserStore( entity.getUserStore().getName() );
        }
        else
        {
            model.setUserStore( "system" );
        }
        model.setHasPhoto( entity.hasPhoto() );
        model.setUserInfo( toUserInfoModel( entity ) );

        return model;
    }

    private UserInfoModel toUserInfoModel( final UserEntity entity )
    {
        UserInfoModel userInfoModel = new UserInfoModel();
        UserInfo userInfo = entity.getUserInfo();
        String birthday = null;
        if ( userInfo.getBirthday() != null )
        {
            birthday = dateFormatter.format( userInfo.getBirthday() );
        }
        userInfoModel.setBirthday( birthday );
        userInfoModel.setCountry( userInfo.getCountry() );
        userInfoModel.setDescription( userInfo.getDescription() );
        userInfoModel.setFax( userInfo.getFax() );
        userInfoModel.setFirstName( userInfo.getFirstName() );
        userInfoModel.setGlobalPosition( userInfo.getGlobalPosition() );
        userInfoModel.setHomePage( userInfo.getHomePage() );
        if ( userInfo.getHtmlEmail() != null )
        {
            userInfoModel.setHtmlEmail( userInfo.getHtmlEmail().toString() );
        }
        userInfoModel.setInitials( userInfo.getInitials() );
        userInfoModel.setLastName( userInfo.getLastName() );
        if ( userInfo.getLocale() != null )
        {
            userInfoModel.setLocale( userInfo.getLocale().toString() );
        }
        userInfoModel.setMemberId( userInfo.getMemberId() );
        userInfoModel.setMiddleName( userInfo.getMiddleName() );
        userInfoModel.setMobile( userInfo.getMobile() );
        userInfoModel.setNickName( userInfo.getOrganization() );
        userInfoModel.setPersonalId( userInfo.getPersonalId() );
        userInfoModel.setPhone( userInfo.getPhone() );
        userInfoModel.setPrefix( userInfo.getPrefix() );
        userInfoModel.setSuffix( userInfo.getSuffix() );
        if ( userInfo.getTimeZone() != null )
        {
            userInfoModel.setTimeZone( userInfo.getTimeZone().getDisplayName() );
        }
        userInfoModel.setTitle( userInfo.getTitle() );
        if ( userInfo.getGender() != null )
        {
            userInfoModel.setGender( userInfo.getGender().toString() );
        }
        userInfoModel.setOrganization( userInfo.getOrganization() );
        for ( Address address : userInfo.getAddresses() )
        {
            userInfoModel.getAddresses().add( toAddressModel( address ) );
        }

        return userInfoModel;
    }

    private AddressModel toAddressModel( final Address address )
    {
        AddressModel addressModel = new AddressModel();
        addressModel.setCountry( address.getCountry() );
        addressModel.setIsoCountry( address.getIsoCountry() );
        addressModel.setRegion( address.getRegion() );
        addressModel.setIsoRegion( address.getIsoRegion() );
        addressModel.setLabel( address.getLabel() );
        addressModel.setPostalAddress( address.getPostalAddress() );
        addressModel.setPostalCode( address.getPostalCode() );
        addressModel.setStreet( address.getStreet() );
        return addressModel;
    }

    private AccountModel toAModel( final GroupEntity entity )
    {
        final GroupModel model = new GroupModel();
        model.setKey( entity.getGroupKey().toString() );
        model.setName( entity.getName() );
        model.setQualifiedName( entity.getQualifiedName().toString() );
        model.setDisplayName( entity.getDisplayName() );
        model.setLastModified( entity.getLastModified() );
        model.setBuiltIn( entity.isBuiltIn() );
        model.setPublic( !entity.isRestricted() );
        model.setDescription( entity.getDescription() );
        model.setEditable( !isAuthenticatedUsersRole( entity ) && !isAnonymousUsersRole( entity ) );
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

    private boolean isEnterpriseAdminUser( final UserEntity entity )
    {
        if ( entity.isRoot() )
        {
            return true;
        }
        else if ( entity.isAnonymous() || ( entity.getUserGroup() == null ) )
        {
            return false;
        }
        else
        {
            return GroupType.ENTERPRISE_ADMINS.equals( entity.getUserGroup().getType() );
        }
    }

    private boolean isEnterpriseAdminsRole( final GroupEntity entity )
    {
        return entity.getType().equals( GroupType.ENTERPRISE_ADMINS );
    }

    private boolean isAuthenticatedUsersRole( final GroupEntity entity )
    {
        return entity.getType().equals( GroupType.AUTHENTICATED_USERS );
    }

    private boolean isAnonymousUsersRole( final GroupEntity entity )
    {
        return entity.getType().equals( GroupType.ANONYMOUS );
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
        Collections.sort( members );
        groupModel.setMembers( members );
        return groupModel;
    }
}

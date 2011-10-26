package com.enonic.cms.core.search.indexing;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enonic.cms.core.search.account.AccountKey;
import com.enonic.cms.core.search.account.Group;
import com.enonic.cms.core.search.account.User;
import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.store.dao.GroupEntityDao;
import com.enonic.cms.store.dao.UserEntityDao;

import com.enonic.cms.domain.EntityPageList;

@Component
class AccountDaoImpl
    implements InitializingBean, AccountDao
{
    private static final String GLOBAL_USERSTORE_NAME = "_Global";
    private HibernateTemplate hibernateTemplate;

    private GroupEntityDao groupDao;

    private UserEntityDao userDao;

    public void afterPropertiesSet()
        throws Exception
    {
        this.userDao = new UserEntityDao();
        this.userDao.setHibernateTemplate( this.hibernateTemplate );
        this.groupDao = new GroupEntityDao();
        this.groupDao.setHibernateTemplate( this.hibernateTemplate );
    }

    @Override
    public int getGroupsCount()
    {
        return this.groupDao.findAll( false ).size();
    }

    @Override
    public int getUsersCount()
    {
        return this.userDao.findAll( false ).size();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public List<User> findAllUsers( int from, int count )
    {
        EntityPageList<UserEntity> users = userDao.findAll( from, count );
        List<User> list = new ArrayList<User>();
        for ( UserEntity user : users.getList() )
        {
            list.add( convertUserEntityToAccount( user ) );
        }
        return list;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public List<Group> findAllGroups( int from, int count )
    {
        EntityPageList<GroupEntity> groups = groupDao.findAll( from, count );

        List<Group> list = new ArrayList<Group>();
        for ( GroupEntity group : groups.getList() )
        {
            list.add( convertGroupEntityToAccount( group ) );
        }
        return list;
    }

    private Group convertGroupEntityToAccount( GroupEntity groupEntity )
    {
        final Group group = new Group();
        group.setKey( new AccountKey( groupEntity.getGroupKey().toString() ) );
        group.setName( groupEntity.getName() );
        group.setDisplayName( groupEntity.getDisplayName() );
        group.setGroupType( groupEntity.getType() );
        if ( groupEntity.getUserStore() != null )
        {
            group.setUserStoreName( groupEntity.getUserStore().getName() );
        }
        else if ( groupEntity.isGlobal() )
        {
            group.setUserStoreName( GLOBAL_USERSTORE_NAME );
        }
        group.setLastModified( new DateTime( groupEntity.getLastModified() ) );

        return group;
    }

    private User convertUserEntityToAccount( UserEntity userEntity )
    {
        final User user = new User();
        user.setKey( new AccountKey( userEntity.getKey().toString() ) );
        user.setName( userEntity.getName() );
        user.setEmail( userEntity.getEmail() );
        user.setDisplayName( userEntity.getDisplayName() );
        if ( userEntity.isBuiltIn() )
        {
            user.setUserStoreName( GLOBAL_USERSTORE_NAME );
        }
        else if ( userEntity.getUserStore() != null )
        {
            user.setUserStoreName( userEntity.getUserStore().getName() );
        }
        user.setLastModified( userEntity.getTimestamp() );
        user.setUserInfo( userEntity.getUserInfo() );

        return user;
    }

    @Autowired
    public void setHibernateTemplate( HibernateTemplate hibernateTemplate )
    {
        this.hibernateTemplate = hibernateTemplate;
    }

}

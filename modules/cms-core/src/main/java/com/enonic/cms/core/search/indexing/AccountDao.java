package com.enonic.cms.core.search.indexing;

import java.util.List;

import com.enonic.cms.core.search.account.Group;
import com.enonic.cms.core.search.account.User;

public interface AccountDao
{
    int getGroupsCount();

    int getUsersCount();

    List<User> findAllUsers( int from, int count );

    List<Group> findAllGroups( int from, int count );
}

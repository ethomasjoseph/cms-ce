package com.enonic.cms.core.search.account;

import com.enonic.cms.core.security.group.GroupType;

public class Group
    extends Account
{
    private GroupType groupType;

    public Group()
    {
        super( AccountType.GROUP );
    }

    public GroupType getGroupType()
    {
        return groupType;
    }

    public void setGroupType( GroupType groupType )
    {
        this.groupType = groupType;
    }
}

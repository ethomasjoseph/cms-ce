package com.enonic.cms.admin.account;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

import com.enonic.cms.admin.common.LoadStoreRequest;

public final class AccountLoadRequest
    extends LoadStoreRequest
{
    @DefaultValue("")
    @QueryParam("userstores")
    private String userstores;

    private boolean selectUsers;

    private boolean selectGroups;

    @DefaultValue("")
    @QueryParam("organizations")
    private String organizations;

    @DefaultValue("all")
    @QueryParam("type")
    public void setType( String accountType )
    {
        if ( "users".equals( accountType ) )
        {
            selectUsers = true;
            selectGroups = false;
        }
        else if ( "groups".equals( accountType ) )
        {
            selectUsers = false;
            selectGroups = true;
        }
        else
        {
            selectUsers = true;
            selectGroups = true;
        }
    }

    public String getUserstores()
    {
        return userstores;
    }

    public void setUserstores( String userstores )
    {
        this.userstores = userstores;
    }

    public boolean isSelectUsers()
    {
        return selectUsers;
    }

    public void setSelectUsers( boolean selectUsers )
    {
        this.selectUsers = selectUsers;
    }

    public boolean isSelectGroups()
    {
        return selectGroups;
    }

    public void setSelectGroups( boolean selectGroups )
    {
        this.selectGroups = selectGroups;
    }

    public String getOrganizations()
    {
        return organizations;
    }

    public void setOrganizations( String organizations )
    {
        this.organizations = organizations;
    }
}

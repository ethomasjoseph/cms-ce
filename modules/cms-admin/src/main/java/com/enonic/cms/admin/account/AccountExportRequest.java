package com.enonic.cms.admin.account;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;

public final class AccountExportRequest
{

    private String userstores;

    private String[] keys;

    private boolean selectUsers;

    private boolean selectGroups;

    private String organizations;

    private String sort;

    private String dir;

    private String query;


    public AccountExportRequest()
    {
        keys = new String[0];
    }

    @DefaultValue("all")
    @FormParam("type")
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

    @DefaultValue("")
    @FormParam("userstores")
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

    @DefaultValue("")
    @FormParam("organizations")
    public void setOrganizations( String organizations )
    {
        this.organizations = organizations;
    }

    public String[] getKeys()
    {
        return keys;
    }

    @DefaultValue("")
    @FormParam("keys")
    public void setKeys( String keys )
    {
        final String[] keyList = ( keys == null ) || ( keys.trim().isEmpty() ) ? new String[0] : keys.split( "," );
        this.keys = keyList;
    }

    public String getSort()
    {
        return this.sort;
    }

    @FormParam("sort")
    public void setSort( final String sort )
    {
        this.sort = sort;
    }

    public String getSortDir()
    {
        return this.dir;
    }

    @DefaultValue("ASC")
    @FormParam("dir")
    public void setSortDir( final String dir )
    {
        if ( "DESC".equalsIgnoreCase( dir ) )
        {
            this.dir = "DESC";
        }
        else
        {
            this.dir = "ASC";
        }
    }

    public String getQuery()
    {
        return this.query;
    }

    @DefaultValue("")
    @FormParam("query")
    public void setQuery( final String query )
    {
        this.query = query;
    }
}

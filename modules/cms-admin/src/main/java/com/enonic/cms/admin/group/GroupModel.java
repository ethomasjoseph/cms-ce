package com.enonic.cms.admin.group;

import java.util.Date;

public class GroupModel
{
    private String key;

    private String name;

    private String email;

    private String qualifiedName;

    private String displayName;

    private String userStore;

    private Date lastModified;

    private boolean builtIn;

    public String getType()
    {
        return builtIn ? "role" : "group";
    }

    public void setKey( String key )
    {
        this.key = key;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public void setEmail( String email )
    {
        this.email = email;
    }

    public void setQualifiedName( String qualifiedName )
    {
        this.qualifiedName = qualifiedName;
    }

    public void setUserStore( String userStore )
    {
        this.userStore = userStore;
    }

    public void setDisplayName( String displayName )
    {
        this.displayName = displayName;
    }

    public void setLastModified( Date lastModified )
    {
        this.lastModified = lastModified;
    }

    public String getKey()
    {
        return key;
    }

    public String getName()
    {
        return name;
    }

    public String getEmail()
    {
        return email;
    }

    public String getQualifiedName()
    {
        return qualifiedName;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getUserStore()
    {
        return userStore;
    }

    public Date getLastModified()
    {
        return lastModified;
    }

    public void setBuiltIn( boolean builtIn )
    {
        this.builtIn = builtIn;
    }

    public boolean isBuiltIn()
    {
        return this.builtIn;
    }
}

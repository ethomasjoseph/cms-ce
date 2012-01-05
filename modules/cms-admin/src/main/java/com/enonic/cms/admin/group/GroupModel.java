package com.enonic.cms.admin.group;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class GroupModel
{
    private String key;

    private String name;

    private String email;

    private String qualifiedName;

    private String displayName;

    private String description;

    private String userStore;

    private Date lastModified;

    private boolean restricted;

    private List<Map<String, String>> members;

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

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public boolean isRestricted()
    {
        return restricted;
    }

    public void setRestricted( boolean restricted )
    {
        this.restricted = restricted;
    }

    public void setBuiltIn( boolean builtIn )
    {
        this.builtIn = builtIn;
    }

    public boolean isBuiltIn()
    {
        return this.builtIn;
    }

    public List<Map<String, String>> getMembers()
    {
        return members;
    }

    public void setMembers( List<Map<String, String>> members )
    {
        this.members = members;
    }
}

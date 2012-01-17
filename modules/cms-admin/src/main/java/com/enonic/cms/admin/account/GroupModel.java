package com.enonic.cms.admin.account;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonAutoDetect
public class GroupModel
        implements AccountModel
{

    private String key;

    @JsonProperty(USER_NAME)
    private String name;

    private String qualifiedName;

    private String displayName;

    private String userStore;

    private Date lastModified;

    private String lastLogged;

    private List<AccountModel> members;

    private boolean builtIn;

    private boolean editable;

    private String description;

    private Boolean isPublic;

    public GroupModel()
    {
        this.members = new ArrayList<AccountModel>();
    }

    @Override
    public String getAccountType()
    {
        return builtIn ? "role" : "group";
    }

    @Override
    public void setKey( String key )
    {
        this.key = key;
    }

    @Override
    public String getKey()
    {
        return this.key;
    }

    @Override
    public void setName( String name )
    {
        this.name = name;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public void setQualifiedName( String qualifiedName )
    {
        this.qualifiedName = qualifiedName;
    }

    @Override
    public String getQualifiedName()
    {
        return this.qualifiedName;
    }

    @Override
    public void setUserStore( String userStore )
    {
        this.userStore = userStore;
    }

    @Override
    public String getUserStore()
    {
        return this.userStore;
    }

    @Override
    public void setDisplayName( String displayName )
    {
        this.displayName = displayName;
    }

    @Override
    public String getDisplayName()
    {
        return this.displayName;
    }

    @Override
    public void setLastModified( Date lastModified )
    {
        this.lastModified = lastModified;
    }

    @Override
    public Date getLastModified()
    {
        return this.lastModified;
    }

    @Override
    public boolean hasPhoto()
    {
        return false;
    }

    public List<AccountModel> getMembers()
    {
        return members;
    }

    public void setMembers( List<AccountModel> members )
    {
        this.members = members;
    }

    public void setBuiltIn( boolean builtIn )
    {
        this.builtIn = builtIn;
    }

    public boolean isBuiltIn()
    {
        return this.builtIn;
    }

    @Override
    public boolean isEditable()
    {
        return editable;
    }

    public void setEditable(boolean editable)
    {
        this.editable = editable;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public Boolean isPublic()
    {
        return isPublic;
    }

    public void setPublic( Boolean aPublic )
    {
        isPublic = aPublic;
    }
}

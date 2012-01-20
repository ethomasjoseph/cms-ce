package com.enonic.cms.admin.account;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;

import com.enonic.cms.admin.user.UserInfoModel;

@JsonAutoDetect
public final class UserModel implements AccountModel
{
    private String key;

    public UserModel()
    {
    }

    @JsonProperty(USER_NAME)
    private String name;

    private String email;

    private String qualifiedName;

    @JsonProperty(DISPLAY_NAME)
    private String displayName;

    private String userStore;

    private Date lastModified;

    private boolean hasPhoto = false;

    private boolean builtIn;

    private boolean editable;

    private UserInfoModel userInfo;

    private String lastLogged;

    private List<Map<String, String>> groups;

    public List<Map<String, String>> getGroups()
    {
        return groups;
    }

    public void setGroups( List<Map<String, String>> groups )
    {
        this.groups = groups;
    }

    public String getLastLogged()
    {
        return lastLogged;
    }

    public void setLastLogged( String lastLogged )
    {
        this.lastLogged = lastLogged;
    }

    public String getCreated()
    {
        return created;
    }

    public void setCreated( String created )
    {
        this.created = created;
    }

    private String created;

    @JsonProperty(USER_INFO)
    public UserInfoModel getUserInfo()
    {
        return userInfo;
    }

    public void setUserInfo( UserInfoModel userInfo )
    {
        this.userInfo = userInfo;
    }

    @Override
    public String getAccountType()
    {
        return "user";
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

    @Override
    public boolean hasPhoto()
    {
        return this.hasPhoto;
    }

    @Override
    public void setBuiltIn( boolean builtIn )
    {
        this.builtIn = builtIn;
    }

    @Override
    public boolean isBuiltIn()
    {
        return builtIn;
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

    public void setHasPhoto( boolean hasPhoto )
    {
        this.hasPhoto = hasPhoto;
    }

    @Override
    public int compareTo( AccountModel o )
    {
        if (o instanceof UserModel)
        {
            return this.getDisplayName().compareTo( o.getDisplayName() );
        }
        if (o instanceof GroupModel)
        {
            return 1;
        }
        return this.getName().compareTo( o.getName() );
    }
}

package com.enonic.cms.core.search.account;

import com.enonic.cms.core.search.SearchSortOrder;

public final class AccountSearchQuery
{
    private String query;
    private int from;
    private int count;
    private String[] userStore;
    private boolean users;
    private boolean groups;
    private SearchSortOrder sortOrder;
    private AccountIndexField sortField;

    public AccountSearchQuery()
    {
        this.query = "";
        this.from = 0;
        this.count = 0;
        this.userStore = null;
        this.users = true;
        this.groups = true;
        this.sortField = null;
    }

    public boolean getGroups()
    {
        return this.groups;
    }

    public AccountSearchQuery setGroups(boolean selectGroups)
    {
        this.groups = selectGroups;
        return this;
    }

    public boolean getUsers()
    {
        return this.users;
    }

    public AccountSearchQuery setUsers(boolean selectUsers)
    {
        this.users = selectUsers;
        return this;
    }

    public String getQuery()
    {
        return this.query != null ? this.query : "";
    }

    public AccountSearchQuery setQuery(String value)
    {
        this.query = value;
        return this;
    }

    public int getFrom()
    {
        return this.from;
    }

    public AccountSearchQuery setFrom(int from)
    {
        this.from = from;
        return this;
    }

    public int getCount()
    {
        return this.count;
    }

    public AccountSearchQuery setCount(int count)
    {
        this.count = count;
        return this;
    }

    public String[] getUserStores()
    {
        return userStore;
    }

    public AccountSearchQuery setUserStores( String... userStore )
    {
        if ( userStore == null || userStore.length == 0 || userStore.length == 1 && userStore[0].equals( "" ) )
        {
            this.userStore = null;
        }
        else
        {
            this.userStore = userStore;
        }
        return this;
    }

    public SearchSortOrder getSortOrder()
    {
        return sortOrder;
    }

    public AccountSearchQuery setSortOrder( SearchSortOrder sortOrder )
    {
        this.sortOrder = sortOrder;
        return this;
    }

    public AccountIndexField getSortField()
    {
        return sortField;
    }

    public AccountSearchQuery setSortField( AccountIndexField sortField )
    {
        this.sortField = sortField;
        return this;
    }
}

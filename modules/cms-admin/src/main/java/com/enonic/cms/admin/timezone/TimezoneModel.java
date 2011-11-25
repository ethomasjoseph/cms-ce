package com.enonic.cms.admin.timezone;

public final class TimezoneModel
{
    private String id;
    private String shortName;
    private String name;
    private String offset;

    public String getId()
    {
        return id;
    }

    public void setId( String id )
    {
        this.id = id;
    }

    public String getShortName()
    {
        return shortName;
    }

    public void setShortName( String shortName )
    {
        this.shortName = shortName;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getOffset()
    {
        return offset;
    }

    public void setOffset( String offset )
    {
        this.offset = offset;
    }
}

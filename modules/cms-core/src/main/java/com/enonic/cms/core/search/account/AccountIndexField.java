package com.enonic.cms.core.search.account;

public enum AccountIndexField
{
    DISPLAY_NAME_FIELD( "displayName" ),
    LAST_MODIFIED_FIELD( "lastModified" ),
    TYPE_FIELD( "type" ),
    USERSTORE_FIELD( "userstore" ),
    NAME_FIELD( "name" ),
    FIRST_NAME_FIELD( "first-name" ),
    KEY_FIELD( "key" );
    ;

    private final String id;

    AccountIndexField( String id )
    {
        this.id = id;
    }

    public String id()
    {
        return this.id;
    }

    public static AccountIndexField parse( String id )
    {
        if ( id == null )
        {
            return null;
        }
        id = id.toLowerCase();
        for ( AccountIndexField field : AccountIndexField.values() )
        {
            if ( field.id.toLowerCase().equals( id ) )
            {
                return field;
            }
        }
        return null;

    }
}

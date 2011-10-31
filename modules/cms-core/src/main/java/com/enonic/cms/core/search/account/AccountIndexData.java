package com.enonic.cms.core.search.account;

import java.io.IOException;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

public class AccountIndexData
{
    private final AccountKey key;

    private final XContentBuilder data;

    public AccountIndexData( final Account account )
    {
        key = account.getKey();
        data = build( account );
    }

    private XContentBuilder build( final Account account )
    {
        try
        {
            switch ( account.getType() )
            {

                case USER:
                    return buildUser( (User) account );

                case GROUP:
                    return buildGroup( (Group) account );

                default:
                    throw new UnsupportedOperationException( "Unable to build index for account of type " + account.getType() );
            }
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    private XContentBuilder buildUser( final User user )
        throws Exception
    {
        final XContentBuilder result = buildAccountStart(user);
        addField( result, AccountIndexField.EMAIL_FIELD.id(), user.getEmail() );

        buildAccountEnd( result );
        return result;
    }

    private XContentBuilder buildGroup( final Group group )
        throws Exception
    {
        final XContentBuilder result = buildAccountStart(group);

        buildAccountEnd( result );
        return result;
    }

    private XContentBuilder buildAccountStart( final Account account )
        throws Exception
    {
        final XContentBuilder result = XContentFactory.jsonBuilder();
        result.startObject();
        addField( result, AccountIndexField.KEY_FIELD.id(), account.getKey().toString() );

        addField( result, AccountIndexField.TYPE_FIELD.id(), account.getType().name() );
        addField( result, AccountIndexField.NAME_FIELD.id(), account.getName() );
        addField( result, AccountIndexField.DISPLAY_NAME_FIELD.id(), account.getDisplayName() );
        addField( result, AccountIndexField.USERSTORE_FIELD.id(), account.getUserStoreName() );
        addField( result, AccountIndexField.LAST_MODIFIED_FIELD.id(), account.getLastModified() );

        return result;
    }

    private void buildAccountEnd( final XContentBuilder contentBuilder )
        throws Exception
    {
        contentBuilder.endObject();
    }

    private void addField( XContentBuilder result, String name, Object value )
        throws Exception
    {
        if ( value == null )
        {
            return;
        }
        if ( value instanceof String )
        {
            value = ( (String) value ).trim();
        }

        result.field( name, value );
    }

    AccountKey getKey()
    {
        return key;
    }

    XContentBuilder getData()
    {
        return data;
    }

    public String toString()
    {
        try
        {
            return data.string();
        }
        catch ( IOException e )
        {
            return "";
        }
    }
}

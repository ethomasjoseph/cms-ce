package com.enonic.cms.admin.account;

import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;

import com.enonic.cms.core.security.user.QualifiedUsername;

public class QualifiedUsernameMatcher
    implements IArgumentMatcher
{
    private final QualifiedUsername qualifiedUsername;

    public QualifiedUsernameMatcher( QualifiedUsername qualifiedUsername )
    {
        this.qualifiedUsername = qualifiedUsername;
    }

    public static final QualifiedUsername eqQualifiedUsername( QualifiedUsername qualifiedUsername )
    {
        EasyMock.reportMatcher( new QualifiedUsernameMatcher( qualifiedUsername ) );
        return null;
    }

    @Override
    public boolean matches( Object argument )
    {
        if ( argument instanceof QualifiedUsername )
        {
            QualifiedUsername qu = (QualifiedUsername) argument;
            if ( qu.toString().equals( qualifiedUsername.toString() ) )
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public void appendTo( StringBuffer buffer )
    {
        buffer.append( "qualifiedUsername(" ).append( qualifiedUsername ).append( ")" );
    }
}

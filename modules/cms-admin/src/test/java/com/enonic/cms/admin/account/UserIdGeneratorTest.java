package com.enonic.cms.admin.account;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.enonic.cms.core.security.user.QualifiedUsername;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.store.dao.UserDao;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.*;

public class UserIdGeneratorTest
{

    private UserDao userDao;

    private UserStoreKey userStoreKey1;

    @Before
    public void setUp()
    {
        userDao = createMock( UserDao.class );
        userStoreKey1 = new UserStoreKey( 1 );
    }

    @Test
    public void testCharacterConversion()
        throws IOException
    {
        final AsciiLettersTextFilter charConverter = new AsciiLettersTextFilter();

        final String asciiLowerString = "abcdefghijklmnopqrstuvwxyz";
        final String asciiLowerStringConverted = charConverter.convertUnicodeToAsciiLetters( asciiLowerString );

        assertEquals( asciiLowerString, asciiLowerStringConverted );

        final String latinOneNorwegianString = "\u00E6\u00C6\u00E5\u00C5\u00F8\u00D8";
        final String latinOneNorwegianStringConverted = charConverter.convertUnicodeToAsciiLetters( latinOneNorwegianString );

        assertEquals( "aAaAoO", latinOneNorwegianStringConverted );

        final String latinOneRomanceLanguageString =
            "\u00F1\u00D1\u00E7\u00C7\u00C0\u00C1\u00C8\u00C9\u00CC\u00CD\u00D2\u00D3\u00D9" +
                "\u00DA\u00E0\u00E1\u00E8\u00E9\u00EC\u00ED\u00F2\u00F3\u00F9\u00FA";
        final String latinOneRomanceLanguageStringConverted = charConverter.convertUnicodeToAsciiLetters( latinOneRomanceLanguageString );

        assertEquals( "nNcCAAEEIIOOUUaaeeiioouu", latinOneRomanceLanguageStringConverted );
    }

    @Test
    public void testGenerateUserIdDefaultLength()
        throws IOException
    {
        final UserIdGenerator userIdGenerator = new UserIdGenerator( userDao );
        assertEquals( userIdGenerator.getMaximumLength(), 8 );
        assertEquals( userIdGenerator.getMaximumSuffixCount(), 100 );

        final String firstName = "Bj\u00F8rn";
        final String lastName = "D\u00E6hlie";

        final String userId = userIdGenerator.generateUserId( firstName, lastName, userStoreKey1 );

        assertEquals( "bjornd", userId );
    }

    @Test
    public void testGenerateUserIdExtraLength()
        throws IOException
    {
        final UserIdGenerator userIdGenerator = new UserIdGenerator( userDao );
        userIdGenerator.setMaximumLength( 10 );
        userIdGenerator.setMaximumSuffixCount( 100 );

        final String firstName = "Ole Einar";
        final String lastName = "Bj\u00F8rndalen";

        simulateUserExists( userStoreKey1, "oleeinarb", "oleeinarbj", "oleeinabjo", "oleeinbjor", "oleeibjorn", "oleebjornd" );
        final String userId = userIdGenerator.generateUserId( firstName, lastName, userStoreKey1 );

        assertEquals( "olebjornda", userId );
    }

    @Test
    public void testGenerateUserIdSuffix()
        throws IOException
    {
        final UserIdGenerator userIdGenerator = new UserIdGenerator( userDao );
        userIdGenerator.setMaximumLength( 8 );
        userIdGenerator.setMaximumSuffixCount( 100 );

        final String firstName = "M";
        final String lastName = "Bj\u00F8rgen";
        final String usernameNotInUse = "mbjorg80";

        final String[] usedNames = new String[]{"mb", "mbj", "mbjo", "mbjor", "mbjorg", "mbjorge", "mbjorgen"};
        final List<String> prefixCombinationNames = new ArrayList<String>();
        final int maximumSuffix = userIdGenerator.getMaximumSuffixCount();
        for ( String usedName : usedNames )
        {
            for ( int i = 0; i < maximumSuffix; i++ )
            {
                String userName = usedName + i;
                if ( !userName.equals( usernameNotInUse ) )
                {
                    prefixCombinationNames.add( userName );
                }
            }
        }
        prefixCombinationNames.addAll( Arrays.asList( usedNames ) );
        final String[] allUsedNames = new String[prefixCombinationNames.size()];
        prefixCombinationNames.toArray( allUsedNames );

        simulateUserExists( userStoreKey1, allUsedNames );
        final String userId = userIdGenerator.generateUserId( firstName, lastName, userStoreKey1 );

        assertEquals( usernameNotInUse, userId );
    }

    private void simulateUserExists( UserStoreKey userStoreKey, String... userIds )
    {
        for ( String userId : userIds )
        {
            UserEntity user = new UserEntity();
            QualifiedUsername qualifiedUsername = new QualifiedUsername( userStoreKey, userId );

            expect( userDao.findByQualifiedUsername( QualifiedUsernameMatcher.eqQualifiedUsername( qualifiedUsername ) ) ).andReturn(
                user ).anyTimes();
        }

        expect( userDao.findByQualifiedUsername( (QualifiedUsername) anyObject() ) ).andReturn( null ).anyTimes();

        replay( userDao );
    }

}

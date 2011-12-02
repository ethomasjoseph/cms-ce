/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.admin.account;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.IOUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Before;
import org.junit.Test;

import com.enonic.cms.api.client.model.user.Address;
import com.enonic.cms.api.client.model.user.Gender;
import com.enonic.cms.api.client.model.user.UserInfo;
import com.enonic.cms.core.search.account.AccountKey;
import com.enonic.cms.core.search.account.AccountSearchHit;
import com.enonic.cms.core.search.account.AccountSearchResults;
import com.enonic.cms.core.search.account.AccountType;
import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.UserDao;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.*;


public class AccountsCsvExportTest
{
    private static final DateTimeFormatter timeFormatter = DateTimeFormat.forPattern( "yyyy-MM-dd HH:mm ZZ" );

    private UserDao userDao;

    private GroupDao groupDao;

    @Before
    public void setUp()
    {
        userDao = createMock( UserDao.class );
        groupDao = createMock( GroupDao.class );
    }

    private void setUpUserDao( List<UserEntity> users )
    {
        for ( UserEntity user : users )
        {
            expect( userDao.findByKey( eq( user.getKey().toString() ) ) ).andReturn( user ).anyTimes();
        }
        replay( userDao );
    }

    private void setUpGroupDao( List<GroupEntity> groups )
    {
        for ( GroupEntity group : groups )
        {
            expect( groupDao.findByKey( eq( group.getGroupKey() ) ) ).andReturn( group ).anyTimes();
        }
        replay( groupDao );
    }

    @Test
    public void exportAccountsCsv()
        throws IOException
    {
        final List<UserEntity> testUsers = createTestUserSet();
        setUpUserDao( testUsers );

        final List<GroupEntity> testGroups = createTestGroupSet();
        setUpGroupDao( testGroups );

        final AccountsCsvExport csvExport = new AccountsCsvExport( groupDao, userDao );

        final AccountSearchResults accounts = createSearchResults( testUsers, testGroups );

        final String csvContent = csvExport.generateCsv( accounts ).trim();

        final String expectedContent = normalizeNewLineChars( readFile( "Accounts_export.csv" ).trim() );

        assertEquals( expectedContent, csvContent );
    }

    @Test
    public void generatedFilename()
        throws IOException
    {
        GroupDao groupDao = null;
        UserDao userDao = null;
        final AccountsCsvExport csvExport = new AccountsCsvExport( groupDao, userDao );

        DateTimeFormatter dtf = DateTimeFormat.forPattern( "yyyyMMdd'T'HHmmss.SSS" );
        final Date exportDate = dtf.parseDateTime( "20111230T010203.789" ).toDate();
        final String csvFilename = csvExport.getExportFileName( exportDate );
        assertEquals( "Accounts-20111230T010203.csv", csvFilename );
    }

    private String normalizeNewLineChars( String text )
    {
        text = text.replaceAll( "\\r\\n", "\n" );
        text = text.replaceAll( "\\r", "\n" );
        return text.replaceAll( "\\n", "\r\n" );
    }

    private String readFile( String filename )
        throws IOException
    {
        InputStream is = getClass().getResourceAsStream( filename );
        StringWriter writer = new StringWriter();
        IOUtils.copy( is, writer );
        return writer.toString();
    }

    private List<GroupEntity> createTestGroupSet()
    {
        final List<GroupEntity> groups = new ArrayList<GroupEntity>();

        final UserStoreEntity userstore1 = new UserStoreEntity();
        userstore1.setName( "demo" );

        final UserStoreEntity userstore2 = new UserStoreEntity();
        userstore2.setName( "System" );

        final GroupEntity group1 = new GroupEntity();
        group1.setKey( new GroupKey( "100" ) );
        group1.setName( "Authenticated Users" );
        group1.setUserStore( userstore1 );
        group1.setType( GroupType.ADMINS );
        groups.add( group1 );

        final GroupEntity group2 = new GroupEntity();
        group2.setKey( new GroupKey( "101" ) );
        group2.setName( "Contributors" );
        group2.setUserStore( userstore2 );
        group2.setType( GroupType.USERSTORE_GROUP );
        groups.add( group2 );
        
        return groups;
    }

    private List<UserEntity> createTestUserSet()
    {
        final List<UserEntity> users = new ArrayList<UserEntity>();

        final UserStoreEntity userstore1 = new UserStoreEntity();
        userstore1.setName( "System" );

        final UserStoreEntity userstore2 = new UserStoreEntity();
        userstore2.setName( "demo" );

        final UserEntity user1 = new UserEntity();
        user1.setKey( new UserKey( "1" ) );
        user1.setDisplayName( "Enterprise Administrator" );
        user1.setName( "admin" );
        user1.setUserStore( userstore1 );
        user1.setTimestamp( timeFormatter.parseDateTime( "2009-09-30 15:00 +02:00" ) );
        users.add( user1 );

        final UserEntity user2 = new UserEntity();
        user2.setKey( new UserKey( "2" ) );
        user2.setDisplayName( "John Developer" );
        user2.setName( "dev" );
        user2.setUserStore( userstore2 );
        user2.setEmail( "dev@example.com" );
        user2.setTimestamp( timeFormatter.parseDateTime( "2011-08-04 10:28 +02:00" ) );
        UserInfo userInfo = user2.getUserInfo();
        userInfo.setFirstName( "John" );
        userInfo.setLastName( "Developer" );
        userInfo.setPrefix( "pre" );
        userInfo.setSuffix( "suf" );
        userInfo.setMiddleName( "mid" );
        userInfo.setTitle( "title" );
        userInfo.setCountry( "NO" );
        userInfo.setGender( Gender.FEMALE );
        userInfo.setInitials( "Mr" );
        userInfo.setDescription( "desc" );
        userInfo.setMobile( "dev" );
        userInfo.setNickName( "Enonic Travels" );

        userInfo.setOrganization( "Enonic Travels" );
        userInfo.setBirthday( ISODateTimeFormat.basicDate().parseDateTime( "20110803" ).toDate() );

        userInfo.setLocale( new Locale( "no" ) );
        userInfo.setMemberId( "123" );
        userInfo.setPersonalId( "456" );
        userInfo.setPhone( "123456" );
        userInfo.setMobile( "6548978" );
        userInfo.setFax( "123456789" );
        userInfo.setMiddleName( "mid" );

        Address address = new Address();
        address.setLabel( "home" );
        address.setPostalAddress( "Oslo" );
        address.setPostalCode( "1234" );
        address.setCountry( "Norway" );
        address.setRegion( "" );
        address.setIsoCountry( "NO" );
        address.setStreet( "kirkegata" );
        address.setIsoRegion( "02" );
        userInfo.setAddresses( address );

        users.add( user2 );

        return users;
    }

    private AccountSearchResults createSearchResults( List<UserEntity> users, List<GroupEntity> groups )
    {
        final AccountSearchResults results = new AccountSearchResults( 0, users.size() + groups.size() );

        for ( UserEntity user : users )
        {
            AccountSearchHit hit = new AccountSearchHit( new AccountKey( user.getKey().toString() ), AccountType.USER, 0 );
            results.add( hit );
        }
        for ( GroupEntity group : groups )
        {
            AccountSearchHit hit = new AccountSearchHit( new AccountKey( group.getGroupKey().toString() ), AccountType.GROUP, 0 );
            results.add( hit );
        }
        return results;
    }
}

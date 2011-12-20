/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.itest.search;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.content.index.ContentDocument;

import static org.junit.Assert.*;

public class ContentIndexServiceImplTest_basic_add_remove
    extends ContentIndexServiceBaseTest
{
    private static final Logger LOG = LoggerFactory.getLogger( ContentIndexServiceImplTest_basic_add_remove.class.getName() );

    @Test
    public void add_and_remove_document()
        throws Exception
    {
        // Check if indexed
        assertFalse( service.isIndexed( new ContentKey( 1322 ) ) );

        // Setup standard values
        setUpStandardTestValues();

        // Check if indexed
        assertTrue( service.isIndexed( new ContentKey( 1322 ) ) );

        // Remove content
        int removeCount = service.remove( new ContentKey( 1322 ) );

        // Check removed properly.
        assertTrue( removeCount > 0 );
        assertFalse( service.isIndexed( new ContentKey( 1322 ) ) );
    }


    @Test
    public void update_values_for_document()
        throws Exception
    {
        final GregorianCalendar date = new GregorianCalendar( 2008, Calendar.FEBRUARY, 28 );

        setUpStandardTestValues();

        ContentKey contentKey = new ContentKey( 1322 );

        // Index the same content again
        ContentDocument doc1 = new ContentDocument( contentKey );
        doc1.setCategoryKey( new CategoryKey( 9 ) );
        doc1.setContentTypeKey( new ContentTypeKey( 32 ) );
        doc1.setContentTypeName( "Adults" );
        doc1.setTitle( "Homer" );

        // but this time with two indexes removed, and two added
        doc1.addUserDefinedField( "data/person/age", "38" );
        doc1.addUserDefinedField( "data/person/firstname", "elvis" );
        doc1.addUserDefinedField( "data/person/surname", "presley" );
        doc1.addUserDefinedField( "data/person/description",
                                  "crude, overweight, incompetent, clumsy, thoughtless and a borderline alcoholic" );

        // Publish from February 28th to March 28th.
        doc1.setPublishFrom( date.getTime() );
        date.add( Calendar.MONTH, 1 );
        doc1.setPublishTo( date.getTime() );
        date.add( Calendar.MONTH, -1 );
        doc1.setStatus( 2 );
        doc1.setPriority( 0 );
        service.index( doc1, false );

        assertTrue( this.service.isIndexed( new ContentKey( 1322 ) ) );

        //List<ContentIndexEntity> indexes = contentIndexDao.findByContentKey( contentKey );
        //      assertEquals( NUMBER_OF_STANDARD_INDEX_FIELDS + 4, indexes.size() );
        // verifyStandardValuesForAllIndexes( doc1, indexes );

        // checkUserDefinedFields( indexes, doc1.getUserDefinedFields(), doc1.getUserDefinedFields().size(),
        //                        doc1.getUserDefinedFields().size() );

    }


    private void setUpStandardTestValues()
    {
        List<ContentDocument> docs = indexDataCreator.createSimpleIndexDataList();

        doIndexContentDocuments( docs );


    }

    //    @Test
//    public void testIndexSameValuesTwice()
//        throws Exception
//    {
//
//        final GregorianCalendar date = new GregorianCalendar( 2008, Calendar.FEBRUARY, 28 );
//        setUpStandardTestValues();
//
//        ContentKey contentKey = new ContentKey( 1322 );
//        // Index the same content again
//        ContentDocument doc1 = new ContentDocument( contentKey );
//        doc1.setCategoryKey( new CategoryKey( 9 ) );
//        doc1.setContentTypeKey( new ContentTypeKey( 32 ) );
//        doc1.setContentTypeName( "Adults" );
//        doc1.setTitle( "Homer" );
//
//        doc1.addUserDefinedField( "data/person/age", "38" );
//        doc1.addUserDefinedField( "data/person/gender", "male" );
//        doc1.addUserDefinedField( "data/person/description",
//                                  "crude, overweight, incompetent, clumsy, thoughtless and a borderline alcoholic" );
//
//        // Publish from February 28th to March 28th.
//        doc1.setPublishFrom( date.getTime() );
//        date.add( Calendar.MONTH, 1 );
//        doc1.setPublishTo( date.getTime() );
//        date.add( Calendar.MONTH, -1 );
//        doc1.setStatus( 2 );
//        doc1.setPriority( 0 );
//        service.index( doc1, false );
//
//        ContentIndexQuery query = new ContentIndexQuery( "key = 1322" );
//
//        ContentResultSet resultSet = service.query( query );
//
//        assertEquals( 1, resultSet.getTotalCount() );

    // assertEquals( NUMBER_OF_STANDARD_INDEX_FIELDS + 3, indexes.size() );

    //verifyStandardValuesForAllIndexes( doc1, indexes );

    //checkUserDefinedFields( indexes, doc1.getUserDefinedFields(), doc1.getUserDefinedFields().size(),
    //                        doc1.getUserDefinedFields().size() );
//    }

//
//    @Test
//    public void testLargeTextFieldShortenValue()
//    {
//        setUpStandardTestValues();
//
//        ContentKey contentKey = new ContentKey( 1322 );
//        ContentDocument doc1 = createContentDocWithNoUserFields( contentKey );
//
//        int numberOfRowsExpected = 5;
//        doc1.addUserDefinedField( "data/text", createStringFillingXRows( numberOfRowsExpected ) );
//
//        service.index( doc1, false );
//       
//        List<ContentIndexEntity> indexes = contentIndexDao.findByContentKey( contentKey );
//       // assertEquals( NUMBER_OF_STANDARD_INDEX_FIELDS + numberOfRowsExpected, indexes.size() );
//        checkUserDefinedFields( indexes, doc1.getUserDefinedFields(), numberOfRowsExpected, doc1.getUserDefinedFields().size() );
//
//        // Set new, shorter value for the text-field, and index again
//        doc1 = createContentDocWithNoUserFields( contentKey );
//
//        int newNumberOfRowsExpected = 3;
//        doc1.addUserDefinedField( "data/text", createStringFillingXRows( newNumberOfRowsExpected ) );
//
//        service.index( doc1, false );
//       
//        indexes = contentIndexDao.findByContentKey( contentKey );
//      //  assertEquals( NUMBER_OF_STANDARD_INDEX_FIELDS + newNumberOfRowsExpected, indexes.size() );
//        checkUserDefinedFields( indexes, doc1.getUserDefinedFields(), newNumberOfRowsExpected, doc1.getUserDefinedFields().size() );
//    }
//
//    @Test
//    public void testLargeTextFieldExtendValue()
//    {
//        setUpStandardTestValues();
//
//        ContentKey contentKey = new ContentKey( 1322 );
//        ContentDocument doc1 = createContentDocWithNoUserFields( contentKey );
//
//        int numberOfRowsExpected = 5;
//        doc1.addUserDefinedField( "data/text", createStringFillingXRows( numberOfRowsExpected ) );
//
//        service.index( doc1, false );
//      
//        List<ContentIndexEntity> indexes = contentIndexDao.findByContentKey( contentKey );
//       // assertEquals( NUMBER_OF_STANDARD_INDEX_FIELDS + numberOfRowsExpected, indexes.size() );
//        checkUserDefinedFields( indexes, doc1.getUserDefinedFields(), numberOfRowsExpected, doc1.getUserDefinedFields().size() );
//
//        // Set new, shorter value for the text-field, and index again
//        doc1 = createContentDocWithNoUserFields( contentKey );
//
//        int newNumberOfRowsExpected = 10;
//        doc1.addUserDefinedField( "data/text", createStringFillingXRows( newNumberOfRowsExpected ) );
//
//        service.index( doc1, false );
//       
//        indexes = contentIndexDao.findByContentKey( contentKey );
//      //  assertEquals( NUMBER_OF_STANDARD_INDEX_FIELDS + newNumberOfRowsExpected, indexes.size() );
//        checkUserDefinedFields( indexes, doc1.getUserDefinedFields(), newNumberOfRowsExpected, doc1.getUserDefinedFields().size() );
//    }
//
//    @Test
//    public void testLargeTextFieldRemoveValue()
//    {
//        setUpStandardTestValues();
//
//        ContentKey contentKey = new ContentKey( 1322 );
//        ContentDocument doc1 = createContentDocWithNoUserFields( contentKey );
//
//        int numberOfRowsExpected = 5;
//        doc1.addUserDefinedField( "data/text", createStringFillingXRows( numberOfRowsExpected ) );
//
//        service.index( doc1, false );
//        contentIndexDao.getHibernateTemplate().flush();
//        contentIndexDao.getHibernateTemplate().clear();
//
//        List<ContentIndexEntity> indexes = contentIndexDao.findByContentKey( contentKey );
//      //  assertEquals( NUMBER_OF_STANDARD_INDEX_FIELDS + numberOfRowsExpected, indexes.size() );
//        checkUserDefinedFields( indexes, doc1.getUserDefinedFields(), numberOfRowsExpected, doc1.getUserDefinedFields().size() );
//
//        // Set new, shorter value for the text-field, and index again
//        doc1 = createContentDocWithNoUserFields( contentKey );
//
//        service.index( doc1, false );
//        contentIndexDao.getHibernateTemplate().flush();
//        contentIndexDao.getHibernateTemplate().clear();
//
//        indexes = contentIndexDao.findByContentKey( contentKey );
//     //   assertEquals( NUMBER_OF_STANDARD_INDEX_FIELDS, indexes.size() );
//    }
//
//    @Test
//    public void testLargeTextFieldChangeStatus()
//    {
//        setUpStandardTestValues();
//
//        ContentKey contentKey = new ContentKey( 1322 );
//        ContentDocument doc1 = createContentDocWithNoUserFields( contentKey );
//        doc1.setStatus( 1 );
//
//        int numberOfRowsExpected = 5;
//        String userText = createStringFillingXRows( numberOfRowsExpected );
//        doc1.addUserDefinedField( "data/text", userText );
//
//        service.index( doc1, false );
//        contentIndexDao.getHibernateTemplate().flush();
//        contentIndexDao.getHibernateTemplate().clear();
//
//        List<ContentIndexEntity> indexes = contentIndexDao.findByContentKey( contentKey );
//    //    assertEquals( NUMBER_OF_STANDARD_INDEX_FIELDS + numberOfRowsExpected, indexes.size() );
//        checkUserDefinedFields( indexes, doc1.getUserDefinedFields(), numberOfRowsExpected, doc1.getUserDefinedFields().size() );
//
//        // Set new status, this should be populated to all index-values
//        doc1 = createContentDocWithNoUserFields( contentKey );
//        doc1.setStatus( 2 );
//
//        int newNumberOfRowsExpected = 5;
//        doc1.addUserDefinedField( "data/text", userText );
//
//        service.index( doc1, false );
//        contentIndexDao.getHibernateTemplate().flush();
//        contentIndexDao.getHibernateTemplate().clear();
//
//        indexes = contentIndexDao.findByContentKey( contentKey );
//     //   assertEquals( NUMBER_OF_STANDARD_INDEX_FIELDS + newNumberOfRowsExpected, indexes.size() );
//        checkUserDefinedFields( indexes, doc1.getUserDefinedFields(), newNumberOfRowsExpected, doc1.getUserDefinedFields().size() );
//
//        verifyStandardValuesForAllIndexes( doc1, indexes );
//    }
//
//    @Test
//    public void testLargeBinaryExtractedTextFieldShortenValue()
//    {
//        setUpStandardTestValues();
//
//        ContentKey contentKey = new ContentKey( 1322 );
//        ContentDocument doc1 = createContentDocWithNoUserFields( contentKey );
//
//        int numberOfRowsExpected = 10;
//        doc1.setBinaryExtractedText( new BigText( createStringFillingXRows( numberOfRowsExpected ) ) );
//
//        service.index( doc1, false );
//        contentIndexDao.getHibernateTemplate().flush();
//        contentIndexDao.getHibernateTemplate().clear();
//
//        List<ContentIndexEntity> indexes = contentIndexDao.findByContentKey( contentKey );
//     //   assertEquals( NUMBER_OF_STANDARD_INDEX_FIELDS + numberOfRowsExpected, indexes.size() );
//        verifyStandardValuesForAllIndexes( doc1, indexes );
//
//        // Set new, shorter value for the text-field, and index again
//        doc1 = createContentDocWithNoUserFields( contentKey );
//
//        int newNumberOfRowsExpected = 3;
//        doc1.setBinaryExtractedText( new BigText( createStringFillingXRows( newNumberOfRowsExpected ) ) );
//
//        service.index( doc1, false );
//        contentIndexDao.getHibernateTemplate().flush();
//        contentIndexDao.getHibernateTemplate().clear();
//
//        indexes = contentIndexDao.findByContentKey( contentKey );
//    //    assertEquals( NUMBER_OF_STANDARD_INDEX_FIELDS + newNumberOfRowsExpected, indexes.size() );
//        verifyStandardValuesForAllIndexes( doc1, indexes );
//    }

    //    @Test
//    public void testIndexDuplicatePaths()
//        throws Exception
//    {
//
//        final GregorianCalendar date = new GregorianCalendar( 2008, Calendar.FEBRUARY, 28 );
//        ContentDocument doc1 = new ContentDocument( new ContentKey( 1322 ) );
//        doc1.setCategoryKey( new CategoryKey( 9 ) );
//        doc1.setContentTypeKey( new ContentTypeKey( 32 ) );
//        doc1.setContentTypeName( "Adults" );
//        doc1.setTitle( "Homer" );
//        doc1.addUserDefinedField( "data/person/image", "38" );
//        doc1.addUserDefinedField( "data/person/image", "39" );
//        // Publish from February 28th to March 28th.
//        doc1.setPublishFrom( date.getTime() );
//        date.add( Calendar.MONTH, 1 );
//        doc1.setPublishTo( date.getTime() );
//        date.add( Calendar.MONTH, -1 );
//        doc1.setStatus( 2 );
//        doc1.setPriority( 0 );
//        service.index( doc1, true );
//
//        contentIndexDao.getHibernateTemplate().flush();
//        contentIndexDao.getHibernateTemplate().clear();
//
//        ContentKey contentKey = new ContentKey( 1322 );
//        // Index the same content again
//        doc1 = new ContentDocument( contentKey );
//        doc1.setCategoryKey( new CategoryKey( 9 ) );
//        doc1.setContentTypeKey( new ContentTypeKey( 32 ) );
//        doc1.setContentTypeName( "Adults" );
//        doc1.setTitle( "Homer" );
//
//        doc1.addUserDefinedField( "data/person/image", "39" );
//        doc1.addUserDefinedField( "data/person/image", "38" );
//
//        // Publish from February 28th to March 28th.
//        doc1.setPublishFrom( date.getTime() );
//        date.add( Calendar.MONTH, 1 );
//        doc1.setPublishTo( date.getTime() );
//        date.add( Calendar.MONTH, -1 );
//        doc1.setStatus( 2 );
//        doc1.setPriority( 0 );
//        service.index( doc1, false );
//
//        contentIndexDao.getHibernateTemplate().flush();
//        contentIndexDao.getHibernateTemplate().clear();
//
//        List<ContentIndexEntity> indexes = contentIndexDao.findByContentKey( contentKey );
//        //    assertEquals( NUMBER_OF_STANDARD_INDEX_FIELDS + 2, indexes.size() );
//
//        verifyStandardValuesForAllIndexes( doc1, indexes );
//
//        checkUserDefinedFields( indexes, doc1.getUserDefinedFields(), doc1.getUserDefinedFields().size(), 1 );
//    }
//
//    @Test
//    public void testChangedValue()
//        throws Exception
//    {
//        final GregorianCalendar date = new GregorianCalendar( 2008, Calendar.FEBRUARY, 28 );
//        setUpStandardTestValues();
//
//        ContentKey contentKey = new ContentKey( 1322 );
//
//        // Index the same content again
//        ContentDocument doc1 = new ContentDocument( contentKey );
//        doc1.setCategoryKey( new CategoryKey( 9 ) );
//        doc1.setContentTypeKey( new ContentTypeKey( 32 ) );
//        doc1.setContentTypeName( "Adults" );
//        doc1.setTitle( "Marge" );
//
//        doc1.addUserDefinedField( "data/person/age", "39" );
//        doc1.addUserDefinedField( "data/person/gender", "female" );
//        doc1.addUserDefinedField( "data/person/description",
//                                  "crude, overweight, incompetent, clumsy, thoughtless and a borderline alcoholic" );
//
//        // Publish from February 28th to March 28th.
//        doc1.setPublishFrom( date.getTime() );
//        date.add( Calendar.MONTH, 1 );
//        doc1.setPublishTo( date.getTime() );
//        date.add( Calendar.MONTH, -1 );
//        doc1.setStatus( 2 );
//        doc1.setPriority( 0 );
//        service.index( doc1, false );
//
//        contentIndexDao.getHibernateTemplate().flush();
//        contentIndexDao.getHibernateTemplate().clear();
//
//        List<ContentIndexEntity> indexes = contentIndexDao.findByContentKey( contentKey );
//        //     assertEquals( NUMBER_OF_STANDARD_INDEX_FIELDS + 3, indexes.size() );
//        verifyStandardValuesForAllIndexes( doc1, indexes );
//
//        checkUserDefinedFields( indexes, doc1.getUserDefinedFields(), doc1.getUserDefinedFields().size(),
//                                doc1.getUserDefinedFields().size() );
//
//    }
//
//    @Test
//    public void testRemoveValue()
//        throws Exception
//    {
//        final GregorianCalendar date = new GregorianCalendar( 2008, Calendar.FEBRUARY, 28 );
//        setUpStandardTestValues();
//
//        ContentKey contentKey = new ContentKey( 1322 );
//
//        // Index the same content again
//        ContentDocument doc1 = new ContentDocument( contentKey );
//        doc1.setCategoryKey( new CategoryKey( 9 ) );
//        doc1.setContentTypeKey( new ContentTypeKey( 32 ) );
//        doc1.setContentTypeName( "Adults" );
//        doc1.setTitle( "Homer" );
//
//        doc1.addUserDefinedField( "data/person/age", "38" );
//        doc1.addUserDefinedField( "data/person/description",
//                                  "crude, overweight, incompetent, clumsy, thoughtless and a borderline alcoholic" );
//
//        // Publish from February 28th to March 28th.
//        doc1.setPublishFrom( date.getTime() );
//        date.add( Calendar.MONTH, 1 );
//        doc1.setPublishTo( date.getTime() );
//        date.add( Calendar.MONTH, -1 );
//        doc1.setStatus( 2 );
//        doc1.setPriority( 0 );
//        service.index( doc1, false );
//
//        contentIndexDao.getHibernateTemplate().flush();
//        contentIndexDao.getHibernateTemplate().clear();
//
//        List<ContentIndexEntity> indexes = contentIndexDao.findByContentKey( contentKey );
////        assertEquals( NUMBER_OF_STANDARD_INDEX_FIELDS + 2, indexes.size() );
//        verifyStandardValuesForAllIndexes( doc1, indexes );
//
//        checkUserDefinedFields( indexes, doc1.getUserDefinedFields(), doc1.getUserDefinedFields().size(),
//                                doc1.getUserDefinedFields().size() );
//
//    }
//
//    @Test
//    public void testAddNewValue()
//        throws Exception
//    {
//        final GregorianCalendar date = new GregorianCalendar( 2008, Calendar.FEBRUARY, 28 );
//        setUpStandardTestValues();
//
//        ContentKey contentKey = new ContentKey( 1322 );
//
//        // Index the same content again
//        ContentDocument doc1 = new ContentDocument( contentKey );
//        doc1.setCategoryKey( new CategoryKey( 9 ) );
//        doc1.setContentTypeKey( new ContentTypeKey( 32 ) );
//        doc1.setContentTypeName( "Adults" );
//        doc1.setTitle( "Homer" );
//
//        doc1.addUserDefinedField( "data/person/age", "38" );
//        doc1.addUserDefinedField( "data/person/gender", "male" );
//        doc1.addUserDefinedField( "data/person/surname", "Simpson" );
//        doc1.addUserDefinedField( "data/person/description",
//                                  "crude, overweight, incompetent, clumsy, thoughtless and a borderline alcoholic" );
//
//        // Publish from February 28th to March 28th.
//        doc1.setPublishFrom( date.getTime() );
//        date.add( Calendar.MONTH, 1 );
//        doc1.setPublishTo( date.getTime() );
//        date.add( Calendar.MONTH, -1 );
//        doc1.setStatus( 2 );
//        doc1.setPriority( 0 );
//        service.index( doc1, false );
//
//        contentIndexDao.getHibernateTemplate().flush();
//        contentIndexDao.getHibernateTemplate().clear();
//
//        List<ContentIndexEntity> indexes = contentIndexDao.findByContentKey( contentKey );
//        //     assertEquals( NUMBER_OF_STANDARD_INDEX_FIELDS + 4, indexes.size() );
//        verifyStandardValuesForAllIndexes( doc1, indexes );
//
//        checkUserDefinedFields( indexes, doc1.getUserDefinedFields(), doc1.getUserDefinedFields().size(),
//                                doc1.getUserDefinedFields().size() );
//
//    }
//
//   
//
//    @Test
//    public void testRemoveTwoAndAddOne()
//        throws Exception
//    {
//        final GregorianCalendar date = new GregorianCalendar( 2008, Calendar.FEBRUARY, 28 );
//        setUpStandardTestValues();
//
//        ContentKey contentKey = new ContentKey( 1322 );
//
//        // Index the same content again
//        ContentDocument doc1 = new ContentDocument( contentKey );
//        doc1.setCategoryKey( new CategoryKey( 9 ) );
//        doc1.setContentTypeKey( new ContentTypeKey( 32 ) );
//        doc1.setContentTypeName( "Adults" );
//        doc1.setTitle( "Homer" );
//
//        // but this time with two indexes removed, but only one added
//        doc1.addUserDefinedField( "data/person/age", "38" );
//        doc1.addUserDefinedField( "data/person/surname", "presley" );
//        doc1.addUserDefinedField( "data/person/description",
//                                  "crude, overweight, incompetent, clumsy, thoughtless and a borderline alcoholic" );
//
//        // Publish from February 28th to March 28th.
//        doc1.setPublishFrom( date.getTime() );
//        date.add( Calendar.MONTH, 1 );
//        doc1.setPublishTo( date.getTime() );
//        date.add( Calendar.MONTH, -1 );
//        doc1.setStatus( 2 );
//        doc1.setPriority( 0 );
//        service.index( doc1, false );
//
//        contentIndexDao.getHibernateTemplate().flush();
//        contentIndexDao.getHibernateTemplate().clear();
//
//        List<ContentIndexEntity> indexes = contentIndexDao.findByContentKey( contentKey );
//        //     assertEquals( NUMBER_OF_STANDARD_INDEX_FIELDS + 3, indexes.size() );
//        verifyStandardValuesForAllIndexes( doc1, indexes );
//
//        checkUserDefinedFields( indexes, doc1.getUserDefinedFields(), doc1.getUserDefinedFields().size(),
//                                doc1.getUserDefinedFields().size() );
//
//    }

    //    @Test
//    public void testRemoveByCategory()
//        throws Exception
//    {
//        // Setup standard values
//        setUpStandardTestValues();
//
//        // Check contents exists
//        assertTrue( this.service.isIndexed( new ContentKey( 1322 ) ) );
//        assertTrue( this.service.isIndexed( new ContentKey( 1323 ) ) );
//        assertTrue( this.service.isIndexed( new ContentKey( 1327 ) ) );
//
//        // Remove by category
//        this.service.removeByCategory( new CategoryKey( 9 ) );
//
//        // Check contents deleted
//        assertFalse( this.service.isIndexed( new ContentKey( 1322 ) ) );
//        assertFalse( this.service.isIndexed( new ContentKey( 1323 ) ) );
//        assertTrue( this.service.isIndexed( new ContentKey( 1327 ) ) );
//
//        // Remove content
//        this.service.removeByCategory( new CategoryKey( 7 ) );
//
//        // Check if indexed
//        assertFalse( this.service.isIndexed( new ContentKey( 1327 ) ) );
//    }
//
//    @Test
//    public void testRemoveByContentType()
//        throws Exception
//    {
//        // Setup standard values
//        setUpStandardTestValues();
//
//        // Check contents exists
//        assertTrue( this.service.isIndexed( new ContentKey( 1322 ) ) );
//        assertTrue( this.service.isIndexed( new ContentKey( 1323 ) ) );
//        assertTrue( this.service.isIndexed( new ContentKey( 1324 ) ) );
//
//        // Remove by content type
//        this.service.removeByContentType( new ContentTypeKey( 32 ) );
//
//        // Check contents deleted
//        assertFalse( this.service.isIndexed( new ContentKey( 1322 ) ) );
//        assertTrue( this.service.isIndexed( new ContentKey( 1323 ) ) );
//        assertFalse( this.service.isIndexed( new ContentKey( 1324 ) ) );
//
//        // Remove content
//        this.service.removeByContentType( new ContentTypeKey( 37 ) );
//
//        // Check if indexed
//        assertFalse( this.service.isIndexed( new ContentKey( 1323 ) ) );
//    }
//
//    @Test
//    public void testSimpleQuerying()
//    {
//        // Setup standard values
//        setUpStandardTestValues();
//
//        ContentIndexQuery query1 = new ContentIndexQuery( "key = 1321" );
//        ContentResultSet res1 = service.query( query1 );
//        assertEquals( 0, res1.getLength() );
//
//        ContentIndexQuery query2 = new ContentIndexQuery( "key = 1322" );
//        ContentResultSet res2 = service.query( query2 );
//        assertEquals( 1, res2.getLength() );
//
//        ContentIndexQuery query3 = new ContentIndexQuery( "key = '1322'" );
//        ContentResultSet res3 = service.query( query3 );
//        assertEquals( 1, res3.getLength() );
//
//        ContentIndexQuery query3b = new ContentIndexQuery( "title = 'Bart'" );
//        ContentResultSet res3b = service.query( query3b );
//        assertEquals( 1, res3b.getLength() );
//        assertEquals( 1323, res3b.getKey( 0 ).toInt() );
//
//        ContentIndexQuery query4 = new ContentIndexQuery( "key != 1322" );
//        ContentResultSet res4 = service.query( query4 );
//        assertEquals( 3, res4.getLength() );
//
//        ContentIndexQuery query5 = new ContentIndexQuery( "key != '1324'" );
//        ContentResultSet res5 = service.query( query5 );
//        assertEquals( 3, res5.getLength() );
//
//        ContentIndexQuery query6 = new ContentIndexQuery( "key > 1323" );
//        ContentResultSet res6 = service.query( query6 );
//        assertEquals( 2, res6.getLength() );
//        assertEquals( 1324, res6.getKey( 0 ).toInt() );
//
//        ContentIndexQuery query7 = new ContentIndexQuery( "key < 1327" );
//        ContentResultSet res7 = service.query( query7 );
//        assertEquals( 3, res7.getLength() );
//
//        ContentIndexQuery query8 = new ContentIndexQuery( "key >= 1323" );
//        ContentResultSet res8 = service.query( query8 );
//        assertEquals( 3, res8.getLength() );
//
//        ContentIndexQuery query9 = new ContentIndexQuery( "key <= 1324" );
//        ContentResultSet res9 = service.query( query9 );
//        assertEquals( 3, res9.getLength() );
//    }
//
//    @Test
//    public void testCombinedQuerying()
//    {
//        // Setup standard values
//        setUpStandardTestValues();
//
//        ContentIndexQuery query1 = new ContentIndexQuery( "categorykey = 9 AND key < 1323" );
//        ContentResultSet res1 = service.query( query1 );
//        assertEquals( 1, res1.getLength() );
//
//        ContentIndexQuery query2 = new ContentIndexQuery( "key > 1322 AND contenttypekey = 32" );
//        ContentResultSet res2 = service.query( query2 );
//        assertEquals( 2, res2.getLength() );
//
//        ContentIndexQuery query3 = new ContentIndexQuery( "categorykey = 9 OR key >= 1324" );
//        ContentResultSet res3 = service.query( query3 );
//        assertEquals( 4, res3.getLength() );
//
//        ContentIndexQuery query4 = new ContentIndexQuery( "key > 1320 AND categorykey = 9 AND contenttypekey = 32" );
//        ContentResultSet res4 = service.query( query4 );
//        assertEquals( 2, res4.getLength() );
//
//        ContentIndexQuery query5 = new ContentIndexQuery( "key > 1322 AND (categorykey = 7 OR contenttypekey = 34)" );
//        ContentResultSet res5 = service.query( query5 );
//        assertEquals( 1, res5.getLength() );
//
//        ContentIndexQuery query6 = new ContentIndexQuery( "key != 1322 AND (categorykey = 9 OR contenttypekey = 33)" );
//        ContentResultSet res6 = service.query( query6 );
//        assertEquals( 2, res6.getLength() );
//    }
//
//    @Test
//    public void testInQueries()
//    {
//        setUpStandardTestValues();
//
//        ContentIndexQuery query1 = new ContentIndexQuery( "key IN (1320, 1321, 1322, 1324)" );
//        ContentResultSet res1 = service.query( query1 );
//        assertEquals( 2, res1.getLength() );
//
//        ContentIndexQuery query2 = new ContentIndexQuery( "title IN ('Bender', 'Bart', 'Zoidberg')" );
//        ContentResultSet res2 = service.query( query2 );
//        assertEquals( 2, res2.getLength() );
//
//        ContentIndexQuery query3 = new ContentIndexQuery( "categorykey NOT IN (1, 4, 9, 16, 25, 36, 49, 64, 81, 100, 121)" );
//        ContentResultSet res3 = service.query( query3 );
//        assertEquals( 1, res3.getLength() );
//
//        ContentIndexQuery query4 = new ContentIndexQuery( "title NOT IN ('Home', 'Away', 'Fry', 'Boil')" );
//        ContentResultSet res4 = service.query( query4 );
//        assertEquals( 3, res4.getLength() );
//    }
//
//    @Test
//    public void testStringQueries()
//    {
//        setUpStandardTestValues();
//
//        ContentIndexQuery query1 = new ContentIndexQuery( "title LIKE 'B%'" );
//        ContentResultSet res1 = service.query( query1 );
//        assertEquals( 2, res1.getLength() );
//
//        ContentIndexQuery query2 = new ContentIndexQuery( "title LIKE '%er'" );
//        ContentResultSet res2 = service.query( query2 );
//        assertEquals( 2, res2.getLength() );
//
//        ContentIndexQuery query3 = new ContentIndexQuery( "title LIKE '%end%'" );
//        ContentResultSet res3 = service.query( query3 );
//        assertEquals( 1, res3.getLength() );
//
//        ContentIndexQuery query4 = new ContentIndexQuery( "title STARTS WITH 'Home'" );
//        ContentResultSet res4 = service.query( query4 );
//        assertEquals( 1, res4.getLength() );
//
//        ContentIndexQuery query5 = new ContentIndexQuery( "title ENDS WITH 'art'" );
//        ContentResultSet res5 = service.query( query5 );
//        assertEquals( 1, res5.getLength() );
//
//        ContentIndexQuery query6 = new ContentIndexQuery( "title CONTAINS 'r'" );
//        ContentResultSet res6 = service.query( query6 );
//        assertEquals( 4, res6.getLength() );
//
//        ContentIndexQuery query7 = new ContentIndexQuery( "title STARTS WITH 'B' AND title ENDS WITH 'er'" );
//        ContentResultSet res7 = service.query( query7 );
//        assertEquals( 1, res7.getLength() );
//    }
//
//    @Test
//    public void testDateQueries()
//    {
//        setUpStandardTestValues();
//
//        ContentIndexQuery query1 = new ContentIndexQuery( "publishFrom = date('2008-02-28T00:00:00')" );
//        ContentResultSet res1 = service.query( query1 );
//        assertEquals( 1, res1.getLength() );
//
//        ContentIndexQuery query2 = new ContentIndexQuery( "publishFrom = date('2008-02-28T00:00:00')" );
//        ContentResultSet res2 = service.query( query2 );
//        assertEquals( 1, res2.getLength() );
//
//        ContentIndexQuery query3 = new ContentIndexQuery( "publishFrom <= date('2008-02-29T00:00:00')" );
//        ContentResultSet res3 = service.query( query3 );
//        assertEquals( 2, res3.getLength() );
//
//        ContentIndexQuery query4 = new ContentIndexQuery( "publishFrom > date('2008-02-28')" );
//        ContentResultSet res4 = service.query( query4 );
//        assertEquals( 3, res4.getLength() );
//
//        ContentIndexQuery query5 =
//            new ContentIndexQuery( "publishFrom >= date('2008-02-29T00:00:00') AND publishTo < date('2008-03-29T00:00:00')" );
//        ContentResultSet res5 = service.query( query5 );
//        assertEquals( 1, res5.getLength() );
//    }
//
//    @Test
//    public void testQueriesWithOrderBy()
//    {
//        setUpStandardTestValues();
//
//        ContentIndexQuery query1 = new ContentIndexQuery( "ORDER BY key DESC" );
//        ContentResultSet res1 = service.query( query1 );
//        assertEquals( 4, res1.getLength() );
//        assertEquals( 1327, res1.getKey( 0 ).toInt() );
//        assertEquals( 1324, res1.getKey( 1 ).toInt() );
//        assertEquals( 1323, res1.getKey( 2 ).toInt() );
//
//        ContentIndexQuery query2 = new ContentIndexQuery( "categorykey = 9 ORDER BY title ASC" );
//        ContentResultSet res2 = service.query( query2 );
//        assertEquals( 3, res2.getLength() );
//        assertEquals( 1323, res2.getKey( 0 ).toInt() );
//        assertEquals( 1324, res2.getKey( 1 ).toInt() );
//
//        ContentIndexQuery query3 = new ContentIndexQuery( "ORDER BY title ASC" );
//        ContentResultSet res3 = service.query( query3 );
//        assertEquals( 4, res3.getLength() );
//        assertEquals( 1323, res3.getKey( 0 ).toInt() );
//        assertEquals( 1324, res3.getKey( 1 ).toInt() );
//        assertEquals( 1327, res3.getKey( 2 ).toInt() );
//
//        ContentIndexQuery query4 = new ContentIndexQuery( "ORDER BY publishto ASC" );
//        ContentResultSet res4 = service.query( query4 );
//        assertEquals( 4, res4.getLength() );
//        assertEquals( 1327, res4.getKey( 2 ).toInt() );
//        assertEquals( 1323, res4.getKey( 3 ).toInt() );
//
//        ContentIndexQuery query5 = new ContentIndexQuery( "ORDER BY publishto DESC" );
//        ContentResultSet res5 = service.query( query5 );
//        assertEquals( 4, res5.getLength() );
//        assertEquals( 1323, res5.getKey( 0 ).toInt() );
//        assertEquals( 1327, res5.getKey( 1 ).toInt() );
//
//        ContentIndexQuery query6 = new ContentIndexQuery( "ORDER BY categorykey DESC, title ASC" );
//        ContentResultSet res6 = service.query( query6 );
//        assertEquals( 4, res6.getLength() );
//        assertEquals( 1323, res6.getKey( 0 ).toInt() );
//        assertEquals( 1324, res6.getKey( 1 ).toInt() );
//        assertEquals( 1322, res6.getKey( 2 ).toInt() );
//
//        ContentIndexQuery query7 = new ContentIndexQuery( "ORDER BY categorykey ASC, publishfrom DESC, publishto DESC" );
//        ContentResultSet res7 = service.query( query7 );
//        assertEquals( 4, res7.getLength() );
//        assertEquals( 1327, res7.getKey( 0 ).toInt() );
//        assertEquals( 1324, res7.getKey( 2 ).toInt() );
//        assertEquals( 1322, res7.getKey( 3 ).toInt() );
//    }
//
//    @Test
//    public void testFulltextQueries()
//    {
//        setUpStandardTestValues();
//
//        ContentIndexQuery query1 = new ContentIndexQuery( "* CONTAINS 'male'" );
//        ContentResultSet res1 = service.query( query1 );
//        assertEquals( 3, res1.getLength() );
//
//        ContentIndexQuery query2 = new ContentIndexQuery( "contenttypekey = 32 AND * CONTAINS 'alcoholic'" );
//        ContentResultSet res2 = service.query( query2 );
//        assertEquals( 2, res2.getLength() );
//
//        ContentIndexQuery query3 = new ContentIndexQuery( "categorykey = 9 AND * CONTAINS 'alcoholic' ORDER BY title" );
//        ContentResultSet res3 = service.query( query3 );
//        assertEquals( 2, res3.getLength() );
//    }
//
//    @Test
//    public void testQueriesOnUserDefinedData()
//    {
//        setUpStandardTestValues();
//
//        ContentIndexQuery query1 = new ContentIndexQuery( "data/person/age > 9" );
//        ContentResultSet res1 = service.query( query1 );
//        assertEquals( 3, res1.getLength() );
//
//        ContentIndexQuery query2 = new ContentIndexQuery( "data/person/gender = 'male'" );
//        ContentResultSet res2 = service.query( query2 );
//        assertEquals( 3, res2.getLength() );
//
//        ContentIndexQuery query3 = new ContentIndexQuery( "data/person/description LIKE '%alcoholic%'" );
//        ContentResultSet res3 = service.query( query3 );
//        assertEquals( 2, res3.getLength() );
//
//        ContentIndexQuery query4 = new ContentIndexQuery( "data/person/gender = 'male' AND data/person/description LIKE '%alcoholic%'" );
//        ContentResultSet res4 = service.query( query4 );
//        assertEquals( 1, res4.getLength() );
//        assertEquals( 1322, res4.getKey( 0 ).toInt() );
//
//        ContentIndexQuery query5 = new ContentIndexQuery( "data/person/description LIKE '%alcoholic%' ORDER BY data/person/age DESC" );
//        ContentResultSet res5 = service.query( query5 );
//        assertEquals( 2, res5.getLength() );
//        assertEquals( 1322, res5.getKey( 0 ).toInt() );
//    }
//
//    @Test
//    public void testQueriesOnUserDefinedDataWithDot()
//    {
//        setUpStandardTestValues();
//
//        ContentIndexQuery query1 = new ContentIndexQuery( "data.person.age > 9" );
//        ContentResultSet res1 = service.query( query1 );
//        assertEquals( 3, res1.getLength() );
//
//        ContentIndexQuery query2 = new ContentIndexQuery( "data.person.gender = 'male'" );
//        ContentResultSet res2 = service.query( query2 );
//        assertEquals( 3, res2.getLength() );
//    }
//
//    @Test
//    public void testQueryIndexValues()
//    {
//        setUpStandardTestValues();
//
//        IndexValueQuery query = new IndexValueQuery( "data/person/age" );
//        IndexValueResultSet result = service.query( query );
//
//        assertEquals( 4, result.getCount() );
//        assertEquals( 0, result.getFromIndex() );
//        assertEquals( 4, result.getTotalCount() );
//
//        assertEquals( "5", result.getIndexValue( 0 ).getValue() );
//        assertEquals( new ContentKey( 1324 ), result.getIndexValue( 0 ).getContentKey() );
//
//        assertEquals( "38", result.getIndexValue( 3 ).getValue() );
//        assertEquals( new ContentKey( 1322 ), result.getIndexValue( 3 ).getContentKey() );
//    }
//
//    @Test
//    public void testQueryAggregatedIndexValues()
//    {
//        setUpStandardTestValues();
//
//        AggregatedQuery query = new AggregatedQuery( "data/person/age" );
//        AggregatedResult result = service.query( query );
//
//        assertEquals( 4, result.getCount() );
//        assertEquals( 5.0, result.getMinValue(), 0 );
//        assertEquals( 38.0, result.getMaxValue(), 0 );
//        assertEquals( 81.0, result.getSumValue(), 0 );
//        assertEquals( 20.25, result.getAverageValue(), 0 );
//    }
//
//    @Test
//    public void testOneWordSearchOnTitleAndData()
//    {
//        service.index( createContentDocument( 123, "ost", "ost", null ), false );
//        service.index( createContentDocument( 124, "ost", "kake", null ), false );
//        service.index( createContentDocument( 125, "kake", "ost", null ), false );
//        service.index( createContentDocument( 126, "kake", "kake", null ), false );
//
//        assertContentResultSetEquals( new int[]{123},
//                                      service.query( new ContentIndexQuery( "title CONTAINS 'ost' AND data/* CONTAINS 'ost'" ) ) );
//
//        assertContentResultSetEquals( new int[]{123, 124, 125},
//                                      service.query( new ContentIndexQuery( "title CONTAINS 'ost' OR data/* CONTAINS 'ost'" ) ) );
//
//        assertContentResultSetEquals( new int[]{124, 125, 126},
//                                      service.query( new ContentIndexQuery( "title CONTAINS 'kake' OR data/* CONTAINS 'kake'" ) ) );
//
//        assertContentResultSetEquals( new int[]{},
//                                      service.query( new ContentIndexQuery( "title CONTAINS 'fisk' OR data/* CONTAINS 'fisk'" ) ) );
//    }
//
//    @Test
//    public void testOneWordSearchOnTitleAndFulltext()
//    {
//        service.index( createContentDocument( 123, "ost", null, "ost" ), false );
//        service.index( createContentDocument( 124, "ost", null, "kake" ), false );
//        service.index( createContentDocument( 125, "kake", null, "ost" ), false );
//        service.index( createContentDocument( 126, "kake", null, "kake" ), false );
//
//        assertContentResultSetEquals( new int[]{123},
//                                      service.query( new ContentIndexQuery( "title CONTAINS 'ost' AND fulltext CONTAINS 'ost'" ) ) );
//
//        assertContentResultSetEquals( new int[]{123, 124, 125},
//                                      service.query( new ContentIndexQuery( "title CONTAINS 'ost' OR fulltext CONTAINS 'ost'" ) ) );
//
//        assertContentResultSetEquals( new int[]{124, 125, 126},
//                                      service.query( new ContentIndexQuery( "title CONTAINS 'kake' OR fulltext CONTAINS 'kake'" ) ) );
//
//        assertContentResultSetEquals( new int[]{},
//                                      service.query( new ContentIndexQuery( "title CONTAINS 'fisk' OR fulltext CONTAINS 'fisk'" ) ) );
//    }
//
//
//    @Test
//    public void testOneWordSearchOnTitleAndUnknown()
//    {
//        service.index( createContentDocument( 123, "ost", null, null ), false );
//        service.index( createContentDocument( 124, "kake", null, null ), false );
//
//        assertContentResultSetEquals( new int[]{},
//                                      service.query( new ContentIndexQuery( "title CONTAINS 'ost' AND unknown CONTAINS 'ost'" ) ) );
//
//        assertContentResultSetEquals( new int[]{123},
//                                      service.query( new ContentIndexQuery( "title CONTAINS 'ost' OR unknown CONTAINS 'ost'" ) ) );
//
//        assertContentResultSetEquals( new int[]{124},
//                                      service.query( new ContentIndexQuery( "unknown CONTAINS 'kake' OR title CONTAINS 'kake'" ) ) );
//
//        assertContentResultSetEquals( new int[]{},
//                                      service.query( new ContentIndexQuery( "title CONTAINS 'fisk' OR unknown CONTAINS 'fisk'" ) ) );
//    }
//
//
//    @Test
//    public void testOneWordSearchOnTitleAndDataAndFulltext()
//    {
//        service.index( createContentDocument( 121, "ost", "ost", "ost" ), false );
//        service.index( createContentDocument( 122, "kake", "ost", "ost" ), false );
//        service.index( createContentDocument( 123, "ost", "kake", "ost" ), false );
//        service.index( createContentDocument( 124, "ost", "ost", "kake" ), false );
//        service.index( createContentDocument( 125, "kake", "kake", "ost" ), false );
//        service.index( createContentDocument( 126, "kake", "ost", "kake" ), false );
//        service.index( createContentDocument( 127, "ost", "kake", "kake" ), false );
//        service.index( createContentDocument( 128, "kake", "kake", "kake" ), false );
//
//        assertContentResultSetEquals( new int[]{121}, service.query(
//            new ContentIndexQuery( "title CONTAINS 'ost' AND data/* CONTAINS 'ost' AND fulltext CONTAINS 'ost'" ) ) );
//
//        assertContentResultSetEquals( new int[]{121, 122, 123, 124, 125, 126, 127}, service.query(
//            new ContentIndexQuery( "title CONTAINS 'ost' OR data/* CONTAINS 'ost' OR fulltext CONTAINS 'ost'" ) ) );
//
//        assertContentResultSetEquals( new int[]{121, 122, 123, 124, 127}, service.query(
//            new ContentIndexQuery( "title CONTAINS 'ost' OR (data/* CONTAINS 'ost' AND fulltext CONTAINS 'ost')" ) ) );
//
//        assertContentResultSetEquals( new int[]{121, 122, 123, 124, 126}, service.query(
//            new ContentIndexQuery( "data/* CONTAINS 'ost' OR (title CONTAINS 'ost' AND fulltext CONTAINS 'ost')" ) ) );
//
//        assertContentResultSetEquals( new int[]{121, 122, 123, 124, 125}, service.query(
//            new ContentIndexQuery( "fulltext CONTAINS 'ost' OR (title CONTAINS 'ost' AND data/* CONTAINS 'ost')" ) ) );
//
//        assertContentResultSetEquals( new int[]{121, 123, 124}, service.query(
//            new ContentIndexQuery( "title CONTAINS 'ost' AND (data/* CONTAINS 'ost' OR fulltext CONTAINS 'ost')" ) ) );
//
//        assertContentResultSetEquals( new int[]{121, 122, 124}, service.query(
//            new ContentIndexQuery( "data/* CONTAINS 'ost' AND (title CONTAINS 'ost' OR fulltext CONTAINS 'ost')" ) ) );
//
//        assertContentResultSetEquals( new int[]{121, 122, 123}, service.query(
//            new ContentIndexQuery( "fulltext CONTAINS 'ost' AND (title CONTAINS 'ost' OR data/* CONTAINS 'ost')" ) ) );
//    }
//
//    @Test
//    public void testOneWordSearchOnTitleAndDataAndUnknown()
//    {
//        service.index( createContentDocument( 123, "ost", "ost", null ), false );
//        service.index( createContentDocument( 124, "ost", "kake", null ), false );
//        service.index( createContentDocument( 125, "kake", "ost", null ), false );
//        service.index( createContentDocument( 126, "kake", "kake", null ), false );
//
//        assertContentResultSetEquals( new int[]{123}, service.query(
//            new ContentIndexQuery( "(title CONTAINS 'ost' AND data/* CONTAINS 'ost') OR unknown CONTAINS 'fisk'" ) ) );
//
//        assertContentResultSetEquals( new int[]{123, 124, 125}, service.query(
//            new ContentIndexQuery( "title CONTAINS 'ost' OR data/* CONTAINS 'ost' OR unknown CONTAINS 'fisk'" ) ) );
//
//        assertContentResultSetEquals( new int[]{}, service.query(
//            new ContentIndexQuery( "(title CONTAINS 'ost' OR data/* CONTAINS 'ost') AND unknown CONTAINS 'fisk'" ) ) );
//
//        assertContentResultSetEquals( new int[]{124, 125, 126}, service.query(
//            new ContentIndexQuery( "title CONTAINS 'kake' OR data/* CONTAINS 'kake' OR unknown CONTAINS 'fisk'" ) ) );
//
//        assertContentResultSetEquals( new int[]{}, service.query(
//            new ContentIndexQuery( "title CONTAINS 'fisk' OR data/* CONTAINS 'fisk' OR unknown CONTAINS 'fisk'" ) ) );
//    }
//
//
//    @Test
//    public void testOneWordSearchOnTitleAndFulltextAndUnknown()
//    {
//        service.index( createContentDocument( 123, "ost", null, "ost" ), false );
//        service.index( createContentDocument( 124, "ost", null, "kake" ), false );
//        service.index( createContentDocument( 125, "kake", null, "ost" ), false );
//        service.index( createContentDocument( 126, "kake", null, "kake" ), false );
//
//        assertContentResultSetEquals( new int[]{123}, service.query(
//            new ContentIndexQuery( "(title CONTAINS 'ost' AND fulltext CONTAINS 'ost') OR unknown CONTAINS 'fisk'" ) ) );
//
//        assertContentResultSetEquals( new int[]{123, 124, 125}, service.query(
//            new ContentIndexQuery( "title CONTAINS 'ost' OR fulltext CONTAINS 'ost' OR unknown CONTAINS 'fisk'" ) ) );
//
//        assertContentResultSetEquals( new int[]{}, service.query(
//            new ContentIndexQuery( "title CONTAINS 'ost' AND fulltext CONTAINS 'ost' AND unknown CONTAINS 'fisk'" ) ) );
//
//        assertContentResultSetEquals( new int[]{124, 125, 126}, service.query(
//            new ContentIndexQuery( "title CONTAINS 'kake' OR fulltext CONTAINS 'kake' OR unknown CONTAINS 'fisk'" ) ) );
//
//        assertContentResultSetEquals( new int[]{}, service.query(
//            new ContentIndexQuery( "title CONTAINS 'fisk' OR fulltext CONTAINS 'fisk' OR unknown CONTAINS 'fisk'" ) ) );
//    }
//
//
//    @Test
//    public void testOneWordSearchOnTitleAndDataAndFulltextAndUnknown()
//    {
//        service.index( createContentDocument( 121, "ost", "ost", "ost" ), false );
//        service.index( createContentDocument( 122, "kake", "ost", "ost" ), false );
//        service.index( createContentDocument( 123, "ost", "kake", "ost" ), false );
//        service.index( createContentDocument( 124, "ost", "ost", "kake" ), false );
//        service.index( createContentDocument( 125, "kake", "kake", "ost" ), false );
//        service.index( createContentDocument( 126, "kake", "ost", "kake" ), false );
//        service.index( createContentDocument( 127, "ost", "kake", "kake" ), false );
//        service.index( createContentDocument( 128, "kake", "kake", "kake" ), false );
//
//        assertContentResultSetEquals( new int[]{121}, service.query( new ContentIndexQuery(
//            "(title CONTAINS 'ost' AND data/* CONTAINS 'ost' AND fulltext CONTAINS 'ost') OR unknown CONTAINS 'fisk'" ) ) );
//
//        assertContentResultSetEquals( new int[]{}, service.query( new ContentIndexQuery(
//            "(title CONTAINS 'ost' AND data/* CONTAINS 'ost' AND fulltext CONTAINS 'ost') AND unknown CONTAINS 'fisk'" ) ) );
//
//        assertContentResultSetEquals( new int[]{121, 122, 123, 124, 125, 126, 127}, service.query( new ContentIndexQuery(
//            "(title CONTAINS 'ost' OR data/* CONTAINS 'ost' OR fulltext CONTAINS 'ost') OR unknown CONTAINS 'fisk'" ) ) );
//
//        assertContentResultSetEquals( new int[]{}, service.query( new ContentIndexQuery(
//            "(title CONTAINS 'ost' OR data/* CONTAINS 'ost' OR fulltext CONTAINS 'ost') AND unknown CONTAINS 'fisk'" ) ) );
//
//        assertContentResultSetEquals( new int[]{121, 122, 123, 124, 127}, service.query( new ContentIndexQuery(
//            "(title CONTAINS 'ost' OR (data/* CONTAINS 'ost' AND fulltext CONTAINS 'ost')) OR unknown CONTAINS 'fisk'" ) ) );
//
//        assertContentResultSetEquals( new int[]{}, service.query( new ContentIndexQuery(
//            "(title CONTAINS 'ost' OR (data/* CONTAINS 'ost' AND fulltext CONTAINS 'ost')) AND unknown CONTAINS 'fisk'" ) ) );
//
//        assertContentResultSetEquals( new int[]{121, 122, 123, 124, 126}, service.query( new ContentIndexQuery(
//            "(data/* CONTAINS 'ost' OR (title CONTAINS 'ost' AND fulltext CONTAINS 'ost')) OR unknown CONTAINS 'fisk'" ) ) );
//
//        assertContentResultSetEquals( new int[]{}, service.query( new ContentIndexQuery(
//            "(data/* CONTAINS 'ost' OR (title CONTAINS 'ost' AND fulltext CONTAINS 'ost')) AND unknown CONTAINS 'fisk'" ) ) );
//
//        assertContentResultSetEquals( new int[]{121, 122, 123, 124, 125}, service.query( new ContentIndexQuery(
//            "(fulltext CONTAINS 'ost' OR (title CONTAINS 'ost' AND data/* CONTAINS 'ost')) OR unknown CONTAINS 'fisk'" ) ) );
//
//        assertContentResultSetEquals( new int[]{}, service.query( new ContentIndexQuery(
//            "(fulltext CONTAINS 'ost' OR (title CONTAINS 'ost' AND data/* CONTAINS 'ost')) AND unknown CONTAINS 'fisk'" ) ) );
//
//        assertContentResultSetEquals( new int[]{121, 123, 124}, service.query( new ContentIndexQuery(
//            "(title CONTAINS 'ost' AND (data/* CONTAINS 'ost' OR fulltext CONTAINS 'ost')) OR unknown CONTAINS 'fisk'" ) ) );
//
//        assertContentResultSetEquals( new int[]{}, service.query( new ContentIndexQuery(
//            "(title CONTAINS 'ost' AND (data/* CONTAINS 'ost' OR fulltext CONTAINS 'ost')) AND unknown CONTAINS 'fisk'" ) ) );
//
//        assertContentResultSetEquals( new int[]{121, 122, 124}, service.query( new ContentIndexQuery(
//            "(data/* CONTAINS 'ost' AND (title CONTAINS 'ost' OR fulltext CONTAINS 'ost')) OR unknown CONTAINS 'fisk'" ) ) );
//
//        assertContentResultSetEquals( new int[]{}, service.query( new ContentIndexQuery(
//            "(data/* CONTAINS 'ost' AND (title CONTAINS 'ost' OR fulltext CONTAINS 'ost')) AND unknown CONTAINS 'fisk'" ) ) );
//
//        assertContentResultSetEquals( new int[]{121, 122, 123}, service.query( new ContentIndexQuery(
//            "(fulltext CONTAINS 'ost' AND (title CONTAINS 'ost' OR data/* CONTAINS 'ost')) OR unknown CONTAINS 'fisk'" ) ) );
//
//        assertContentResultSetEquals( new int[]{}, service.query( new ContentIndexQuery(
//            "(fulltext CONTAINS 'ost' AND (title CONTAINS 'ost' OR data/* CONTAINS 'ost')) AND unknown CONTAINS 'fisk'" ) ) );
//    }
//
//    @Test
//    public void testTwoWordSearchOnTitleAndData()
//    {
//        service.index( createContentDocument( 1021, "ost", "ost", null ), false );
//        service.index( createContentDocument( 1022, "ost", "kake", null ), false );
//        service.index( createContentDocument( 1023, "kake", "ost", null ), false );
//        service.index( createContentDocument( 1024, "kake", "kake", null ), false );
//        service.index( createContentDocument( 1025, "ostkake", "ostkake", null ), false );
//        service.index( createContentDocument( 1026, "ostkake", "kakekake", null ), false );
//        service.index( createContentDocument( 1027, "kakekake", "ostkake", null ), false );
//        service.index( createContentDocument( 1028, "kakekake", "kakekake", null ), false );
//
//        /* Search = ost kake */
//        assertContentResultSetEquals( new int[]{1025, 1026, 1027}, service.query( new ContentIndexQuery(
//            "((title CONTAINS 'ost') AND (title CONTAINS 'kake')) OR ((data/* CONTAINS 'ost') AND (data/* CONTAINS 'kake'))" ) ) );
//
//        /* Search = ost kake */
//        assertContentResultSetEquals( new int[]{1022, 1023, 1025, 1026, 1027}, service.query( new ContentIndexQuery(
//            "(title CONTAINS 'ost' OR data/* CONTAINS 'ost') AND (title CONTAINS 'kake' OR data/* CONTAINS 'kake')" ) ) );
//
//        /* Search = ost ost */
//        assertContentResultSetEquals( new int[]{1021, 1022, 1023, 1025, 1026, 1027}, service.query( new ContentIndexQuery(
//            "((title CONTAINS 'ost') AND (title CONTAINS 'ost')) OR ((data/* CONTAINS 'ost') AND (data/* CONTAINS 'ost'))" ) ) );
//
//        /* Search = ost ost */
//        assertContentResultSetEquals( new int[]{1021, 1022, 1023, 1025, 1026, 1027}, service.query( new ContentIndexQuery(
//            "(title CONTAINS 'ost' OR data/* CONTAINS 'ost') AND (title CONTAINS 'ost' OR data/* CONTAINS 'ost')" ) ) );
//    }
//
//    @Test
//    public void testTwoWordSearchOnTitleAndFulltext()
//    {
//        service.index( createContentDocument( 1021, "ost", null, "ost" ), false );
//        service.index( createContentDocument( 1022, "ost", null, "kake" ), false );
//        service.index( createContentDocument( 1023, "kake", null, "ost" ), false );
//        service.index( createContentDocument( 1024, "kake", null, "kake" ), false );
//        service.index( createContentDocument( 1025, "ostkake", null, "ostkake" ), false );
//        service.index( createContentDocument( 1026, "ostkake", null, "kakekake" ), false );
//        service.index( createContentDocument( 1027, "kakekake", null, "ostkake" ), false );
//        service.index( createContentDocument( 1028, "kakekake", null, "kakekake" ), false );
//
//        assertContentResultSetEquals( new int[]{1025, 1026, 1027}, service.query( new ContentIndexQuery(
//            "((title CONTAINS 'ost') AND (title CONTAINS 'kake')) OR ((fulltext CONTAINS 'ost') AND (fulltext CONTAINS 'kake'))" ) ) );
//
//        assertContentResultSetEquals( new int[]{1022, 1023, 1025, 1026, 1027}, service.query( new ContentIndexQuery(
//            "(title CONTAINS 'ost' OR fulltext CONTAINS 'ost') AND (title CONTAINS 'kake' OR fulltext CONTAINS 'kake')" ) ) );
//
//        assertContentResultSetEquals( new int[]{1021, 1022, 1023, 1025, 1026, 1027}, service.query( new ContentIndexQuery(
//            "((title CONTAINS 'ost') AND (title CONTAINS 'ost')) OR ((fulltext CONTAINS 'ost') AND (fulltext CONTAINS 'ost'))" ) ) );
//
//        assertContentResultSetEquals( new int[]{1021, 1022, 1023, 1025, 1026, 1027}, service.query( new ContentIndexQuery(
//            "(title CONTAINS 'ost' OR fulltext CONTAINS 'ost') AND (title CONTAINS 'ost' OR fulltext CONTAINS 'ost')" ) ) );
//    }
//
//    @Test
//    public void testSearchOnTextWithSingleQuote()
//    {
//        service.index( createContentDocument( 1021, "ost", null, "Det var ost's store appetitt som vant.  ;)" ), false );
//        service.index( createContentDocument( 1022, "ost's", null, "Det var ost sin skyld.  ;)" ), false );
//
//        assertContentResultSetEquals( new int[]{1021}, service.query( new ContentIndexQuery( "fulltext CONTAINS \"ost's\"" ) ) );
//
//        assertContentResultSetEquals( new int[]{1022}, service.query( new ContentIndexQuery( "title = \"ost's\"" ) ) );
//    }
//
//    @Test
//    public void testTwoWordSearchOnTitleAndDataAndFulltext()
//    {
//        service.index( createContentDocument( 1021, "ost", "ost", "ost" ), false );
//        service.index( createContentDocument( 1022, "kake", "ost", "ost" ), false );
//        service.index( createContentDocument( 1023, "ost", "kake", "ost" ), false );
//        service.index( createContentDocument( 1024, "ost", "ost", "kake" ), false );
//        service.index( createContentDocument( 1025, "kake", "kake", "ost" ), false );
//        service.index( createContentDocument( 1026, "kake", "ost", "kake" ), false );
//        service.index( createContentDocument( 1027, "ost", "kake", "kake" ), false );
//        service.index( createContentDocument( 1028, "kake", "kake", "kake" ), false );
//
//        service.index( createContentDocument( 1031, "ostkake", "ostkake", "ostkake" ), false );
//        service.index( createContentDocument( 1032, "kakekake", "ostkake", "ostkake" ), false );
//        service.index( createContentDocument( 1033, "ostkake", "kakekake", "ostkake" ), false );
//        service.index( createContentDocument( 1034, "ostkake", "ostkake", "kakekake" ), false );
//        service.index( createContentDocument( 1035, "kakekake", "kakekake", "ostkake" ), false );
//        service.index( createContentDocument( 1036, "kakekake", "ostkake", "kakekake" ), false );
//        service.index( createContentDocument( 1037, "ostkake", "kakekake", "kakekake" ), false );
//        service.index( createContentDocument( 1038, "kakekake", "kakekake", "kakekake" ), false );
//
//        assertContentResultSetEquals( new int[]{1031}, service.query( new ContentIndexQuery(
//            "(title CONTAINS 'ost' AND title CONTAINS 'kake') AND (data/* CONTAINS 'ost' AND data/* CONTAINS 'kake') AND (fulltext CONTAINS 'ost' AND fulltext CONTAINS 'kake')" ) ) );
//
//        assertContentResultSetEquals( new int[]{1031, 1032}, service.query( new ContentIndexQuery(
//            "(title CONTAINS 'ost' OR title CONTAINS 'kake') AND (data/* CONTAINS 'ost' AND data/* CONTAINS 'kake') AND (fulltext CONTAINS 'ost' AND fulltext CONTAINS 'kake')" ) ) );
//
//        assertContentResultSetEquals( new int[]{1031, 1033}, service.query( new ContentIndexQuery(
//            "(title CONTAINS 'ost' AND title CONTAINS 'kake') AND (data/* CONTAINS 'ost' OR data/* CONTAINS 'kake') AND (fulltext CONTAINS 'ost' AND fulltext CONTAINS 'kake')" ) ) );
//
//        assertContentResultSetEquals( new int[]{1031, 1034}, service.query( new ContentIndexQuery(
//            "(title CONTAINS 'ost' AND title CONTAINS 'kake') AND (data/* CONTAINS 'ost' AND data/* CONTAINS 'kake') AND (fulltext CONTAINS 'ost' OR fulltext CONTAINS 'kake')" ) ) );
//
//        assertContentResultSetEquals( new int[]{1031, 1032, 1033}, service.query( new ContentIndexQuery(
//            "((title CONTAINS 'ost' AND title CONTAINS 'kake') OR (data/* CONTAINS 'ost' AND data/* CONTAINS 'kake')) AND (fulltext CONTAINS 'ost' AND fulltext CONTAINS 'kake')" ) ) );
//
//        assertContentResultSetEquals( new int[]{1031, 1032, 1034}, service.query( new ContentIndexQuery(
//            "((title CONTAINS 'ost' AND title CONTAINS 'kake') OR (fulltext CONTAINS 'ost' AND fulltext CONTAINS 'kake')) AND (data/* CONTAINS 'ost' AND data/* CONTAINS 'kake')" ) ) );
//
//        assertContentResultSetEquals( new int[]{1031, 1033, 1034}, service.query( new ContentIndexQuery(
//            "((data/* CONTAINS 'ost' AND data/* CONTAINS 'kake') OR (fulltext CONTAINS 'ost' AND fulltext CONTAINS 'kake')) AND (title CONTAINS 'ost' AND title CONTAINS 'kake')" ) ) );
//
//        assertContentResultSetEquals( new int[]{1031, 1032, 1033, 1035}, service.query( new ContentIndexQuery(
//            "(title CONTAINS 'ost' OR title CONTAINS 'kake') AND (data/* CONTAINS 'ost' OR data/* CONTAINS 'kake') AND (fulltext CONTAINS 'ost' AND fulltext CONTAINS 'kake')" ) ) );
//
//        assertContentResultSetEquals( new int[]{1031, 1032, 1034, 1036}, service.query( new ContentIndexQuery(
//            "(title CONTAINS 'ost' OR title CONTAINS 'kake') AND (data/* CONTAINS 'ost' AND data/* CONTAINS 'kake') AND (fulltext CONTAINS 'ost' OR fulltext CONTAINS 'kake')" ) ) );
//
//        assertContentResultSetEquals( new int[]{1031, 1033, 1034, 1037}, service.query( new ContentIndexQuery(
//            "(title CONTAINS 'ost' AND title CONTAINS 'kake') AND (data/* CONTAINS 'ost' OR data/* CONTAINS 'kake') AND (fulltext CONTAINS 'ost' OR fulltext CONTAINS 'kake')" ) ) );
//
//        assertContentResultSetEquals( new int[]{1031, 1032, 1033, 1034, 1035, 1036, 1037}, service.query( new ContentIndexQuery(
//            "(title CONTAINS 'ost' AND title CONTAINS 'kake') OR (data/* CONTAINS 'ost' AND data/* CONTAINS 'kake') OR (fulltext CONTAINS 'ost' AND fulltext CONTAINS 'kake')" ) ) );
//
//        assertContentResultSetEquals(
//            new int[]{1021, 1022, 1023, 1024, 1025, 1026, 1027, 1028, 1031, 1032, 1033, 1034, 1035, 1036, 1037, 1038}, service.query(
//            new ContentIndexQuery(
//                "(title CONTAINS 'ost' OR title CONTAINS 'kake') AND (data/* CONTAINS 'ost' OR data/* CONTAINS 'kake') AND (fulltext CONTAINS 'ost' OR fulltext CONTAINS 'kake')" ) ) );
//
//        assertContentResultSetEquals(
//            new int[]{1021, 1022, 1023, 1024, 1025, 1026, 1027, 1028, 1031, 1032, 1033, 1034, 1035, 1036, 1037, 1038}, service.query(
//            new ContentIndexQuery(
//                "((title CONTAINS 'ost' OR title CONTAINS 'kake') OR (data/* CONTAINS 'ost' OR data/* CONTAINS 'kake')) AND (fulltext CONTAINS 'ost' OR fulltext CONTAINS 'kake')" ) ) );
//
//        assertContentResultSetEquals(
//            new int[]{1021, 1022, 1023, 1024, 1025, 1026, 1027, 1028, 1031, 1032, 1033, 1034, 1035, 1036, 1037, 1038}, service.query(
//            new ContentIndexQuery(
//                "((title CONTAINS 'ost' OR title CONTAINS 'kake') OR (fulltext CONTAINS 'ost' OR fulltext CONTAINS 'kake')) AND (data/* CONTAINS 'ost' OR data/* CONTAINS 'kake')" ) ) );
//
//        assertContentResultSetEquals(
//            new int[]{1021, 1022, 1023, 1024, 1025, 1026, 1027, 1028, 1031, 1032, 1033, 1034, 1035, 1036, 1037, 1038}, service.query(
//            new ContentIndexQuery(
//                "((data/* CONTAINS 'ost' OR data/* CONTAINS 'kake') OR (fulltext CONTAINS 'ost' OR fulltext CONTAINS 'kake')) AND (title CONTAINS 'ost' OR title CONTAINS 'kake')" ) ) );
//
//        assertContentResultSetEquals(
//            new int[]{1021, 1022, 1023, 1024, 1025, 1026, 1027, 1028, 1031, 1032, 1033, 1034, 1035, 1036, 1037, 1038}, service.query(
//            new ContentIndexQuery(
//                "(title CONTAINS 'ost' OR title CONTAINS 'kake') OR ((data/* CONTAINS 'ost' OR data/* CONTAINS 'kake') OR (fulltext CONTAINS 'ost' OR fulltext CONTAINS 'kake'))" ) ) );
//    }
//
//    @Test
//    public void testTwoWordSearchOnTitleAndDataAndFulltextAndUnknown()
//    {
//        service.index( createContentDocument( 1021, "ost", "ost", "ost" ), false );
//        service.index( createContentDocument( 1022, "kake", "ost", "ost" ), false );
//        service.index( createContentDocument( 1023, "ost", "kake", "ost" ), false );
//        service.index( createContentDocument( 1024, "ost", "ost", "kake" ), false );
//        service.index( createContentDocument( 1025, "kake", "kake", "ost" ), false );
//        service.index( createContentDocument( 1026, "kake", "ost", "kake" ), false );
//        service.index( createContentDocument( 1027, "ost", "kake", "kake" ), false );
//        service.index( createContentDocument( 1028, "kake", "kake", "kake" ), false );
//
//        service.index( createContentDocument( 1031, "ostkake", "ostkake", "ostkake" ), false );
//        service.index( createContentDocument( 1032, "kakekake", "ostkake", "ostkake" ), false );
//        service.index( createContentDocument( 1033, "ostkake", "kakekake", "ostkake" ), false );
//        service.index( createContentDocument( 1034, "ostkake", "ostkake", "kakekake" ), false );
//        service.index( createContentDocument( 1035, "kakekake", "kakekake", "ostkake" ), false );
//        service.index( createContentDocument( 1036, "kakekake", "ostkake", "kakekake" ), false );
//        service.index( createContentDocument( 1037, "ostkake", "kakekake", "kakekake" ), false );
//        service.index( createContentDocument( 1038, "kakekake", "kakekake", "kakekake" ), false );
//
//        assertContentResultSetEquals( new int[]{1031}, service.query( new ContentIndexQuery(
//            "(title CONTAINS 'ost' AND title CONTAINS 'kake') AND (data/* CONTAINS 'ost' AND data/* CONTAINS 'kake') AND (fulltext CONTAINS 'ost' AND fulltext CONTAINS 'kake') OR unknown CONTAINS 'fisk'" ) ) );
//
//        assertContentResultSetEquals( new int[]{1031, 1032}, service.query( new ContentIndexQuery(
//            "(title CONTAINS 'ost' OR title CONTAINS 'kake') AND (data/* CONTAINS 'ost' AND data/* CONTAINS 'kake') AND (fulltext CONTAINS 'ost' AND fulltext CONTAINS 'kake') OR unknown CONTAINS 'fisk'" ) ) );
//
//        assertContentResultSetEquals( new int[]{1031, 1033}, service.query( new ContentIndexQuery(
//            "(title CONTAINS 'ost' AND title CONTAINS 'kake') AND (data/* CONTAINS 'ost' OR data/* CONTAINS 'kake') AND (fulltext CONTAINS 'ost' AND fulltext CONTAINS 'kake') OR unknown CONTAINS 'fisk'" ) ) );
//
//        assertContentResultSetEquals( new int[]{1031, 1034}, service.query( new ContentIndexQuery(
//            "(title CONTAINS 'ost' AND title CONTAINS 'kake') AND (data/* CONTAINS 'ost' AND data/* CONTAINS 'kake') AND (fulltext CONTAINS 'ost' OR fulltext CONTAINS 'kake') OR unknown CONTAINS 'fisk'" ) ) );
//
//        assertContentResultSetEquals( new int[]{1031, 1032, 1033}, service.query( new ContentIndexQuery(
//            "((title CONTAINS 'ost' AND title CONTAINS 'kake') OR (data/* CONTAINS 'ost' AND data/* CONTAINS 'kake')) AND (fulltext CONTAINS 'ost' AND fulltext CONTAINS 'kake') OR unknown CONTAINS 'fisk'" ) ) );
//
//        assertContentResultSetEquals( new int[]{1031, 1032, 1034}, service.query( new ContentIndexQuery(
//            "((title CONTAINS 'ost' AND title CONTAINS 'kake') OR (fulltext CONTAINS 'ost' AND fulltext CONTAINS 'kake')) AND (data/* CONTAINS 'ost' AND data/* CONTAINS 'kake') OR unknown CONTAINS 'fisk'" ) ) );
//
//        assertContentResultSetEquals( new int[]{1031, 1033, 1034}, service.query( new ContentIndexQuery(
//            "((data/* CONTAINS 'ost' AND data/* CONTAINS 'kake') OR (fulltext CONTAINS 'ost' AND fulltext CONTAINS 'kake')) AND (title CONTAINS 'ost' AND title CONTAINS 'kake') OR unknown CONTAINS 'fisk'" ) ) );
//
//        assertContentResultSetEquals( new int[]{1031, 1032, 1033, 1035}, service.query( new ContentIndexQuery(
//            "(title CONTAINS 'ost' OR title CONTAINS 'kake') AND (data/* CONTAINS 'ost' OR data/* CONTAINS 'kake') AND (fulltext CONTAINS 'ost' AND fulltext CONTAINS 'kake') OR unknown CONTAINS 'fisk'" ) ) );
//
//        assertContentResultSetEquals( new int[]{1031, 1032, 1034, 1036}, service.query( new ContentIndexQuery(
//            "(title CONTAINS 'ost' OR title CONTAINS 'kake') AND (data/* CONTAINS 'ost' AND data/* CONTAINS 'kake') AND (fulltext CONTAINS 'ost' OR fulltext CONTAINS 'kake') OR unknown CONTAINS 'fisk'" ) ) );
//
//        assertContentResultSetEquals( new int[]{1031, 1033, 1034, 1037}, service.query( new ContentIndexQuery(
//            "(title CONTAINS 'ost' AND title CONTAINS 'kake') AND (data/* CONTAINS 'ost' OR data/* CONTAINS 'kake') AND (fulltext CONTAINS 'ost' OR fulltext CONTAINS 'kake') OR unknown CONTAINS 'fisk'" ) ) );
//
//        assertContentResultSetEquals( new int[]{1031, 1032, 1033, 1034, 1035, 1036, 1037}, service.query( new ContentIndexQuery(
//            "(title CONTAINS 'ost' AND title CONTAINS 'kake') OR (data/* CONTAINS 'ost' AND data/* CONTAINS 'kake') OR (fulltext CONTAINS 'ost' AND fulltext CONTAINS 'kake') OR unknown CONTAINS 'fisk'" ) ) );
//
//        assertContentResultSetEquals(
//            new int[]{1021, 1022, 1023, 1024, 1025, 1026, 1027, 1028, 1031, 1032, 1033, 1034, 1035, 1036, 1037, 1038}, service.query(
//            new ContentIndexQuery(
//                "(title CONTAINS 'ost' OR title CONTAINS 'kake') AND (data/* CONTAINS 'ost' OR data/* CONTAINS 'kake') AND (fulltext CONTAINS 'ost' OR fulltext CONTAINS 'kake') OR unknown CONTAINS 'fisk'" ) ) );
//
//        assertContentResultSetEquals(
//            new int[]{1021, 1022, 1023, 1024, 1025, 1026, 1027, 1028, 1031, 1032, 1033, 1034, 1035, 1036, 1037, 1038}, service.query(
//            new ContentIndexQuery(
//                "((title CONTAINS 'ost' OR title CONTAINS 'kake') OR (data/* CONTAINS 'ost' OR data/* CONTAINS 'kake')) AND (fulltext CONTAINS 'ost' OR fulltext CONTAINS 'kake') OR unknown CONTAINS 'fisk'" ) ) );
//
//        assertContentResultSetEquals(
//            new int[]{1021, 1022, 1023, 1024, 1025, 1026, 1027, 1028, 1031, 1032, 1033, 1034, 1035, 1036, 1037, 1038}, service.query(
//            new ContentIndexQuery(
//                "((title CONTAINS 'ost' OR title CONTAINS 'kake') OR (fulltext CONTAINS 'ost' OR fulltext CONTAINS 'kake')) AND (data/* CONTAINS 'ost' OR data/* CONTAINS 'kake') OR unknown CONTAINS 'fisk'" ) ) );
//
//        assertContentResultSetEquals(
//            new int[]{1021, 1022, 1023, 1024, 1025, 1026, 1027, 1028, 1031, 1032, 1033, 1034, 1035, 1036, 1037, 1038}, service.query(
//            new ContentIndexQuery(
//                "((data/* CONTAINS 'ost' OR data/* CONTAINS 'kake') OR (fulltext CONTAINS 'ost' OR fulltext CONTAINS 'kake')) AND (title CONTAINS 'ost' OR title CONTAINS 'kake') OR unknown CONTAINS 'fisk'" ) ) );
//
//        assertContentResultSetEquals(
//            new int[]{1021, 1022, 1023, 1024, 1025, 1026, 1027, 1028, 1031, 1032, 1033, 1034, 1035, 1036, 1037, 1038}, service.query(
//            new ContentIndexQuery(
//                "(title CONTAINS 'ost' OR title CONTAINS 'kake') OR ((data/* CONTAINS 'ost' OR data/* CONTAINS 'kake') OR (fulltext CONTAINS 'ost' OR fulltext CONTAINS 'kake')) OR unknown CONTAINS 'fisk'" ) ) );
//    }
//
//    @Test
//    public void testIndexingAndSearchOnOwnerQualifiedName()
//    {
//        ContentDocument doc = createContentDocument( 101, "ost", null );
//        doc.setOwnerQualifiedName( "incamono\\jvs" );
//
//        service.index( doc, false );
//
//        assertContentResultSetEquals( new int[]{101}, service.query( new ContentIndexQuery( "owner/qualifiedName = 'incamono\\jvs'" ) ) );
//    }
//
//    @Test
//    public void testQueryWithOrderByMultipleRelatedContentDoesNotCreateDuplicateContentKeys()
//    {
//        service.index( createContentDocument( 101, "title", new String[][]{{"data/myrelated", "3"}, {"data/myrelated", "9"}} ), false );
//
//        assertContentResultSetEquals( new int[]{101}, service.query( new ContentIndexQuery( "", "data/myrelated ASC" ) ) );
//    }
//
//    @Test
//    public void testIndexingAndSearchOnAssigneeQualifiedName()
//    {
//        ContentDocument assignedToJVS = new ContentDocument( new ContentKey( 1101 ) );
//        assignedToJVS.setCategoryKey( new CategoryKey( 9 ) );
//        assignedToJVS.setContentTypeKey( new ContentTypeKey( 32 ) );
//        assignedToJVS.setContentTypeName( "Article" );
//        assignedToJVS.setTitle( "title" );
//        assignedToJVS.setStatus( 2 );
//        assignedToJVS.setPriority( 0 );
//        assignedToJVS.setAssigneeQualifiedName( "incamono\\jvs" );
//
//        ContentDocument assignedToTAN = new ContentDocument( new ContentKey( 1102 ) );
//        assignedToTAN.setCategoryKey( new CategoryKey( 9 ) );
//        assignedToTAN.setContentTypeKey( new ContentTypeKey( 32 ) );
//        assignedToTAN.setContentTypeName( "Article" );
//        assignedToTAN.setTitle( "title" );
//        assignedToTAN.setStatus( 2 );
//        assignedToTAN.setPriority( 0 );
//        assignedToTAN.setAssigneeQualifiedName( "incamono\\tan" );
//
//        ContentDocument assignedToNone = new ContentDocument( new ContentKey( 1103 ) );
//        assignedToNone.setCategoryKey( new CategoryKey( 9 ) );
//        assignedToNone.setContentTypeKey( new ContentTypeKey( 32 ) );
//        assignedToNone.setContentTypeName( "Article" );
//        assignedToNone.setTitle( "title" );
//        assignedToNone.setStatus( 2 );
//        assignedToNone.setPriority( 0 );
//
//        service.index( assignedToJVS, false );
//        service.index( assignedToTAN, false );
//        service.index( assignedToNone, false );
//
//        assertContentResultSetEquals( new int[]{1101, 1102, 1103}, service.query( new ContentIndexQuery( "categorykey = 9" ) ) );
//        assertContentResultSetEquals( new int[]{1101}, service.query(
//            new ContentIndexQuery( "categorykey = 9 and assignee/qualifiedName = 'incamono\\jvs'" ) ) );
//        assertContentResultSetEquals( new int[]{1102}, service.query(
//            new ContentIndexQuery( "categorykey = 9 and assignee/qualifiedName = 'incamono\\tan'" ) ) );
//    }
//
//    @Test
//    public void testIndexingAndSearchWithOrderyByAssigneeQualifiedName()
//    {
//        ContentDocument assignedToJVS = new ContentDocument( new ContentKey( 1101 ) );
//        assignedToJVS.setCategoryKey( new CategoryKey( 9 ) );
//        assignedToJVS.setContentTypeKey( new ContentTypeKey( 32 ) );
//        assignedToJVS.setContentTypeName( "Article" );
//        assignedToJVS.setTitle( "title" );
//        assignedToJVS.setStatus( 2 );
//        assignedToJVS.setPriority( 0 );
//        assignedToJVS.setAssigneeQualifiedName( "incamono\\jvs" );
//
//        ContentDocument assignedToTAN = new ContentDocument( new ContentKey( 1102 ) );
//        assignedToTAN.setCategoryKey( new CategoryKey( 9 ) );
//        assignedToTAN.setContentTypeKey( new ContentTypeKey( 32 ) );
//        assignedToTAN.setContentTypeName( "Article" );
//        assignedToTAN.setTitle( "title" );
//        assignedToTAN.setStatus( 2 );
//        assignedToTAN.setPriority( 0 );
//        assignedToTAN.setAssigneeQualifiedName( "incamono\\tan" );
//
//        ContentDocument assignedToNone = new ContentDocument( new ContentKey( 1103 ) );
//        assignedToNone.setCategoryKey( new CategoryKey( 9 ) );
//        assignedToNone.setContentTypeKey( new ContentTypeKey( 32 ) );
//        assignedToNone.setContentTypeName( "Article" );
//        assignedToNone.setTitle( "title" );
//        assignedToNone.setStatus( 2 );
//        assignedToNone.setPriority( 0 );
//
//        service.index( assignedToJVS, false );
//        service.index( assignedToTAN, false );
//        service.index( assignedToNone, false );
//
//        assertContentResultSetEquals( new int[]{1101, 1102, 1103}, service.query( new ContentIndexQuery( "categorykey = 9" ) ) );
//        assertContentResultSetEquals( new int[]{1102, 1101, 1103},
//                                      service.query( new ContentIndexQuery( "categorykey = 9", "assignee/qualifiedname desc" ) ) );
//        assertContentResultSetEquals( new int[]{1102, 1101}, service.query( new ContentIndexQuery(
//            "categorykey = 9 AND ( assignee/qualifiedName = 'incamono\\jvs' OR assignee/qualifiedName = 'incamono\\tan' )",
//            "assignee/qualifiedname desc" ) ) );
//        assertContentResultSetEquals( new int[]{1103, 1101, 1102,},
//                                      service.query( new ContentIndexQuery( "categorykey = 9", "assignee/qualifiedname asc" ) ) );
//    }
//
//    @Test
//    public void testIndexingAndSearchOnAssignerQualifiedName()
//    {
//        ContentDocument assignerIsJVS = new ContentDocument( new ContentKey( 1101 ) );
//        assignerIsJVS.setCategoryKey( new CategoryKey( 9 ) );
//        assignerIsJVS.setContentTypeKey( new ContentTypeKey( 32 ) );
//        assignerIsJVS.setContentTypeName( "Article" );
//        assignerIsJVS.setTitle( "title" );
//        assignerIsJVS.setStatus( 2 );
//        assignerIsJVS.setPriority( 0 );
//        assignerIsJVS.setAssignerQualifiedName( "incamono\\jvs" );
//
//        ContentDocument assignerIsTAN = new ContentDocument( new ContentKey( 1102 ) );
//        assignerIsTAN.setCategoryKey( new CategoryKey( 9 ) );
//        assignerIsTAN.setContentTypeKey( new ContentTypeKey( 32 ) );
//        assignerIsTAN.setContentTypeName( "Article" );
//        assignerIsTAN.setTitle( "title" );
//        assignerIsTAN.setStatus( 2 );
//        assignerIsTAN.setPriority( 0 );
//        assignerIsTAN.setAssignerQualifiedName( "incamono\\tan" );
//
//        ContentDocument assigerIsNone = new ContentDocument( new ContentKey( 1103 ) );
//        assigerIsNone.setCategoryKey( new CategoryKey( 9 ) );
//        assigerIsNone.setContentTypeKey( new ContentTypeKey( 32 ) );
//        assigerIsNone.setContentTypeName( "Article" );
//        assigerIsNone.setTitle( "title" );
//        assigerIsNone.setStatus( 2 );
//        assigerIsNone.setPriority( 0 );
//
//        service.index( assignerIsJVS, false );
//        service.index( assignerIsTAN, false );
//        service.index( assigerIsNone, false );
//
//        assertContentResultSetEquals( new int[]{1101, 1102, 1103}, service.query( new ContentIndexQuery( "categorykey = 9" ) ) );
//        assertContentResultSetEquals( new int[]{1101}, service.query(
//            new ContentIndexQuery( "categorykey = 9 and assigner/qualifiedName = 'incamono\\jvs'" ) ) );
//        assertContentResultSetEquals( new int[]{1102}, service.query(
//            new ContentIndexQuery( "categorykey = 9 and assigner/qualifiedName = 'incamono\\tan'" ) ) );
//    }
//
//    @Test
//    public void testIndexingAndSearchWithOrderyByAssignerQualifiedName()
//    {
//        ContentDocument assignerIsJVS = new ContentDocument( new ContentKey( 1101 ) );
//        assignerIsJVS.setCategoryKey( new CategoryKey( 9 ) );
//        assignerIsJVS.setContentTypeKey( new ContentTypeKey( 32 ) );
//        assignerIsJVS.setContentTypeName( "Article" );
//        assignerIsJVS.setTitle( "title" );
//        assignerIsJVS.setStatus( 2 );
//        assignerIsJVS.setPriority( 0 );
//        assignerIsJVS.setAssignerQualifiedName( "incamono\\jvs" );
//
//        ContentDocument assignerIsTAN = new ContentDocument( new ContentKey( 1102 ) );
//        assignerIsTAN.setCategoryKey( new CategoryKey( 9 ) );
//        assignerIsTAN.setContentTypeKey( new ContentTypeKey( 32 ) );
//        assignerIsTAN.setContentTypeName( "Article" );
//        assignerIsTAN.setTitle( "title" );
//        assignerIsTAN.setStatus( 2 );
//        assignerIsTAN.setPriority( 0 );
//        assignerIsTAN.setAssignerQualifiedName( "incamono\\tan" );
//
//        ContentDocument assignerIsNone = new ContentDocument( new ContentKey( 1103 ) );
//        assignerIsNone.setCategoryKey( new CategoryKey( 9 ) );
//        assignerIsNone.setContentTypeKey( new ContentTypeKey( 32 ) );
//        assignerIsNone.setContentTypeName( "Article" );
//        assignerIsNone.setTitle( "title" );
//        assignerIsNone.setStatus( 2 );
//        assignerIsNone.setPriority( 0 );
//
//        service.index( assignerIsJVS, false );
//        service.index( assignerIsTAN, false );
//        service.index( assignerIsNone, false );
//
//        assertContentResultSetEquals( new int[]{1101, 1102, 1103}, service.query( new ContentIndexQuery( "categorykey = 9" ) ) );
//        assertContentResultSetEquals( new int[]{1102, 1101, 1103},
//                                      service.query( new ContentIndexQuery( "categorykey = 9", "assigner/qualifiedname desc" ) ) );
//        assertContentResultSetEquals( new int[]{1102, 1101}, service.query( new ContentIndexQuery(
//            "categorykey = 9 AND ( assigner/qualifiedName = 'incamono\\jvs' OR assigner/qualifiedName = 'incamono\\tan' )",
//            "assigner/qualifiedname desc" ) ) );
//        assertContentResultSetEquals( new int[]{1103, 1101, 1102,},
//                                      service.query( new ContentIndexQuery( "categorykey = 9", "assigner/qualifiedname asc" ) ) );
//    }
//
//    @Test
//    public void testIndexingAndSearchOnAssigmentDueDate()
//    {
//        ContentDocument due2010_06_01T00_00_00 = new ContentDocument( new ContentKey( 1101 ) );
//        due2010_06_01T00_00_00.setCategoryKey( new CategoryKey( 9 ) );
//        due2010_06_01T00_00_00.setContentTypeKey( new ContentTypeKey( 32 ) );
//        due2010_06_01T00_00_00.setContentTypeName( "Article" );
//        due2010_06_01T00_00_00.setTitle( "title" );
//        due2010_06_01T00_00_00.setStatus( 2 );
//        due2010_06_01T00_00_00.setPriority( 0 );
//        due2010_06_01T00_00_00.setAssignmentDueDate( new DateTime( 2010, 6, 1, 0, 0, 0, 0 ).toDate() );
//
//        ContentDocument due2010_06_01T12_00_00 = new ContentDocument( new ContentKey( 1102 ) );
//        due2010_06_01T12_00_00.setCategoryKey( new CategoryKey( 9 ) );
//        due2010_06_01T12_00_00.setContentTypeKey( new ContentTypeKey( 32 ) );
//        due2010_06_01T12_00_00.setContentTypeName( "Article" );
//        due2010_06_01T12_00_00.setTitle( "title" );
//        due2010_06_01T12_00_00.setStatus( 2 );
//        due2010_06_01T12_00_00.setPriority( 0 );
//        due2010_06_01T12_00_00.setAssignmentDueDate( new DateTime( 2010, 6, 1, 12, 0, 0, 0 ).toDate() );
//
//        ContentDocument notDue = new ContentDocument( new ContentKey( 1103 ) );
//        notDue.setCategoryKey( new CategoryKey( 9 ) );
//        notDue.setContentTypeKey( new ContentTypeKey( 32 ) );
//        notDue.setContentTypeName( "Article" );
//        notDue.setTitle( "title" );
//        notDue.setStatus( 2 );
//        notDue.setPriority( 0 );
//
//        service.index( due2010_06_01T00_00_00, false );
//        service.index( due2010_06_01T12_00_00, false );
//        service.index( notDue, false );
//
//        assertContentResultSetEquals( new int[]{1101, 1102, 1103}, service.query( new ContentIndexQuery( "categorykey = 9" ) ) );
//        assertContentResultSetEquals( new int[]{1101}, service.query(
//            new ContentIndexQuery( "categorykey = 9 and assignmentDueDate = '2010-06-01T00:00:00'" ) ) );
//        assertContentResultSetEquals( new int[]{1102}, service.query(
//            new ContentIndexQuery( "categorykey = 9 and assignmentDueDate = date('2010-06-01 12:00:00')" ) ) );
//        assertContentResultSetEquals( new int[]{1103},
//                                      service.query( new ContentIndexQuery( "categorykey = 9 and assignmentDueDate = ''" ) ) );
//        assertContentResultSetEquals( new int[]{1101, 1102},
//                                      service.query( new ContentIndexQuery( "categorykey = 9 and assignmentDueDate != ''" ) ) );
//    }

    private ContentDocument createContentDocument( int contentKey, String title, String preface, String fulltext )
    {
        return createContentDocument( contentKey, title, new String[][]{{"data/preface", preface}, {"fulltext", fulltext}} );
    }

    private ContentDocument createContentDocument( int contentKey, String title, String[][] fields )
    {
        ContentDocument doc = new ContentDocument( new ContentKey( contentKey ) );
        doc.setCategoryKey( new CategoryKey( 9 ) );
        doc.setContentTypeKey( new ContentTypeKey( 32 ) );
        doc.setContentTypeName( "Article" );
        if ( title != null )
        {
            doc.setTitle( title );
        }
        if ( fields != null )
        {
            for ( String[] field : fields )
            {
                doc.addUserDefinedField( field[0], field[1] );
            }
        }
        doc.setStatus( 2 );
        doc.setPriority( 0 );
        return doc;
    }

    private ContentDocument createContentDocument( ContentKey contentKey, CategoryKey categoryKey, ContentTypeKey contentTypeKey,
                                                   String title, String[][] fields )
    {
        return createContentDocument( contentKey, categoryKey, contentTypeKey, 2, title, fields );
    }

    private ContentDocument createContentDocument( ContentKey contentKey, CategoryKey categoryKey, ContentTypeKey contentTypeKey,
                                                   int status, String title, String[][] fields )
    {
        ContentDocument doc = new ContentDocument( contentKey );
        doc.setCategoryKey( categoryKey );
        doc.setContentTypeKey( contentTypeKey );
        doc.setContentTypeName( "Article" );
        if ( title != null )
        {
            doc.setTitle( title );
        }
        if ( fields != null )
        {
            for ( String[] field : fields )
            {
                doc.addUserDefinedField( field[0], field[1] );
            }
        }
        doc.setStatus( status );
        doc.setPriority( 0 );
        return doc;
    }

//    @Test
//    public void testSplittedFulltextIndexWithAnd()
//    {
//        service.index( createContentDocument( 101, "title", new String[][]{{"fulltext", "fisk ost"}, {"fulltext", "torsk tine"}} ), false );
//        service.index( createContentDocument( 102, "title", new String[][]{{"data/text", "ku ost"}, {"data/text", "gryte tine"}} ), false );
//
//        assertContentResultSetEquals( new int[]{101}, service.query(
//            new ContentIndexQuery( "fulltext CONTAINS 'fisk' AND fulltext CONTAINS 'torsk'", "" ) ) );
//    }
//
//    @Test
//    public void testSplittedFulltextIndexWithOr()
//    {
//        service.index( createContentDocument( 101, "title", new String[][]{{"fulltext", "fisk ost"}, {"fulltext", "torsk tine"}} ), false );
//        service.index( createContentDocument( 102, "title", new String[][]{{"data/text", "ku ost"}, {"data/text", "gryte tine"}} ), false );
//
//        assertContentResultSetEquals( new int[]{101}, service.query(
//            new ContentIndexQuery( "fulltext CONTAINS 'fisk' OR fulltext CONTAINS 'torsk'", "" ) ) );
//    }
//
//
//    @Test
//    public void testSplittedNormalIndexWithAnd()
//    {
//        service.index( createContentDocument( 101, "title", new String[][]{{"data/text", "fisk ost"}, {"data/text", "torsk tine"}} ),
//                       false );
//        service.index( createContentDocument( 102, "title", new String[][]{{"data/text", "ku ost"}, {"data/text", "gryte tine"}} ), false );
//
//        assertContentResultSetEquals( new int[]{101}, service.query(
//            new ContentIndexQuery( "data/text CONTAINS 'fisk' AND data/text CONTAINS 'torsk'", "" ) ) );
//    }
//
//    @Test
//    public void testSplittedNormalIndexWithOr()
//    {
//        service.index( createContentDocument( 101, "title", new String[][]{{"data/text", "fisk ost"}, {"data/text", "torsk tine"}} ), false );
//        service.index( createContentDocument( 102, "title", new String[][]{{"data/text", "ku ost"}, {"data/text", "gryte tine"}} ), false );
//
//        assertContentResultSetEquals( new int[]{101}, service.query(
//            new ContentIndexQuery( "data/text CONTAINS 'fisk' OR data/text CONTAINS 'torsk'", "" ) ) );
//    }
//
//    @Test
//    public void testMultipleSameLikeExactWords()
//    {
//        service.index( createContentDocument( 101, "title", new String[][]{{"data/heading", "ENONIC"}, {"data/preface", "ENONIC"},
//            {"data/text", "ENONIC"}} ), false );
//
//        assertContentResultSetEquals( new int[]{101}, service.query(
//            new ContentIndexQuery( "data/heading LIKE '%ENONIC%' or data/preface LIKE '%ENONIC%' or data/text LIKE '%ENONIC%'", "" ) ) );
//    }
//
//    @Test
//    public void testEinarTing1()
//    {
//        service.index( createContentDocument( 100, "title", new String[][]{{"data/preface", "denne skal ikke gi treff"},
//            {"data/description", "dette skal ikke gi treff"}} ), false );
//
//        service.index( createContentDocument( 101, "title", new String[][]{{"data/preface", "dette er en ingress ja"},
//            {"data/description", "dette er en beskrivelse ja"}} ), false );
//
//        assertContentResultSetEquals( new int[]{101}, service.query(
//            new ContentIndexQuery( "data/preface CONTAINS 'ingress' AND data/description CONTAINS 'beskrivelse'" ) ) );
//
//    }
//
//    @Test
//    public void testEinarTing2()
//    {
//        service.index( createContentDocument( 100, "title", new String[][]{{"data/preface", "denne skal ikke gi treff"},
//            {"data/description", "dette skal ikke gi treff"}} ), false );
//
//        service.index( createContentDocument( 101, "title", new String[][]{{"data/preface", "dette er en ingress ja"},
//            {"data/description", "dette er en beskrivelse ja"}} ), false );
//
//        assertContentResultSetEquals( new int[]{101}, service.query(
//            new ContentIndexQuery( "data/* CONTAINS 'ingress' AND data/* CONTAINS 'beskrivelse'" ) ) );
//
//    }
//
//    @Test
//    public void testEinarTing3()
//    {
//        service.index( createContentDocument( 100, "title", new String[][]{{"data/preface", "denne skal ikke gi treff"},
//            {"data/description", "dette skal ikke gi treff"}} ), false );
//
//        service.index( createContentDocument( 101, "title", new String[][]{{"data/preface", "dette er en ingress ja"},
//            {"data/description", "dette er en beskrivelse ja"}} ), false );
//
//        assertContentResultSetEquals( new int[]{101}, service.query(
//            new ContentIndexQuery( "data/* CONTAINS 'ingress' OR data/* CONTAINS 'beskrivelse'" ) ) );
//
//    }
//
//    @Test
//    public void testOrderByStatus()
//    {
//        service.index( createContentDocument( new ContentKey( 101 ), new CategoryKey( 1 ), new ContentTypeKey( 10 ), 3, "c1",
//                                              new String[][]{{"data/dummy", "dummy value"}, {"data/dummy2", "dummy value 2"}} ), false );
//        service.index( createContentDocument( new ContentKey( 102 ), new CategoryKey( 1 ), new ContentTypeKey( 10 ), 0, "c2",
//                                              new String[][]{{"data/dummy", "dummy value"}, {"data/dummy2", "dummy value 2"}} ), false );
//        service.index( createContentDocument( new ContentKey( 103 ), new CategoryKey( 1 ), new ContentTypeKey( 10 ), 2, "c3",
//                                              new String[][]{{"data/dummy", "dummy value"}, {"data/dummy2", "dummy value 2"}} ), false );
//
//        assertEquals( ContentKey.convertToList( new int[]{102, 103, 101} ),
//                      service.query( new ContentIndexQuery( "contenttypekey = 10 and title STARTS WITH 'c'", "status asc" ) ).getKeys() );
//
//        assertEquals( ContentKey.convertToList( new int[]{101, 103, 102} ),
//                      service.query( new ContentIndexQuery( "contenttypekey = 10 and title STARTS WITH 'c'", "status desc" ) ).getKeys() );
//
//    }
//
//    @Test
//    public void testOrderByPublishfrom()
//    {
//        ContentDocument doc1 = createContentDocument( new ContentKey( 101 ), new CategoryKey( 1 ), new ContentTypeKey( 10 ), 0, "c1",
//                                                      new String[][]{{"data/dummy", "dummy value"}, {"data/dummy2", "dummy value 2"}} );
//        doc1.setPublishFrom( new DateTime( 2010, 10, 1, 0, 0, 0, 2 ).toDate() );
//        service.index( doc1, false );
//        ContentDocument doc2 = createContentDocument( new ContentKey( 102 ), new CategoryKey( 1 ), new ContentTypeKey( 10 ), 0, "c2",
//                                                      new String[][]{{"data/dummy", "dummy value"}, {"data/dummy2", "dummy value 2"}} );
//        doc2.setPublishFrom( new DateTime( 2010, 10, 1, 0, 0, 0, 0 ).toDate() );
//        service.index( doc2, false );
//        ContentDocument doc3 = createContentDocument( new ContentKey( 103 ), new CategoryKey( 1 ), new ContentTypeKey( 10 ), 0, "c3",
//                                                      new String[][]{{"data/dummy", "dummy value"}, {"data/dummy2", "dummy value 2"}} );
//        doc3.setPublishFrom( new DateTime( 2010, 10, 1, 0, 0, 0, 1 ).toDate() );
//        service.index( doc3, false );
//
//        assertEquals( ContentKey.convertToList( new int[]{102, 103, 101} ),
//                      service.query( new ContentIndexQuery( "contenttypekey = 10 and title STARTS WITH 'c'", "publishFrom asc" ) )
//                          .getKeys() );
//
//        assertEquals( ContentKey.convertToList( new int[]{101, 103, 102} ),
//                      service.query( new ContentIndexQuery( "contenttypekey = 10 and title STARTS WITH 'c'", "publishFrom desc" ) )
//                          .getKeys() );
//
//    }
//
//    @Test
//    public void testOrderByPublishto()
//    {
//        ContentDocument doc1 = createContentDocument( new ContentKey( 101 ), new CategoryKey( 1 ), new ContentTypeKey( 10 ), 0, "c1",
//                                                      new String[][]{{"data/dummy", "dummy value"}, {"data/dummy2", "dummy value 2"}} );
//        doc1.setPublishTo( new DateTime( 2010, 10, 1, 0, 0, 0, 2 ).toDate() );
//        service.index( doc1, false );
//        ContentDocument doc2 = createContentDocument( new ContentKey( 102 ), new CategoryKey( 1 ), new ContentTypeKey( 10 ), 0, "c2",
//                                                      new String[][]{{"data/dummy", "dummy value"}, {"data/dummy2", "dummy value 2"}} );
//        doc2.setPublishTo( new DateTime( 2010, 10, 1, 0, 0, 0, 0 ).toDate() );
//        service.index( doc2, false );
//        ContentDocument doc3 = createContentDocument( new ContentKey( 103 ), new CategoryKey( 1 ), new ContentTypeKey( 10 ), 0, "c3",
//                                                      new String[][]{{"data/dummy", "dummy value"}, {"data/dummy2", "dummy value 2"}} );
//        doc3.setPublishTo( new DateTime( 2010, 10, 1, 0, 0, 0, 1 ).toDate() );
//        service.index( doc3, false );
//
//        assertEquals( ContentKey.convertToList( new int[]{102, 103, 101} ),
//                      service.query( new ContentIndexQuery( "contenttypekey = 10 and title STARTS WITH 'c'", "publishTo asc" ) )
//                          .getKeys() );
//
//        assertEquals( ContentKey.convertToList( new int[]{101, 103, 102} ),
//                      service.query( new ContentIndexQuery( "contenttypekey = 10 and title STARTS WITH 'c'", "publishTo desc" ) )
//                          .getKeys() );
//
//    }
//
//    @Test
//    public void testContentQueryWithCategoryFilter()
//    {
//
//        ContentDocument doc1 = createContentDocument( new ContentKey( 1 ), new CategoryKey( 101 ), new ContentTypeKey( 10 ), "title1",
//                                                      new String[][]{{"data/heading", "title1"}} );
//        service.index( doc1, false );
//
//        ContentDocument doc2 = createContentDocument( new ContentKey( 2 ), new CategoryKey( 101 ), new ContentTypeKey( 10 ), "title2",
//                                                      new String[][]{{"data/heading", "title2"}} );
//        service.index( doc2, false );
//
//        ContentDocument doc3 = createContentDocument( new ContentKey( 3 ), new CategoryKey( 102 ), new ContentTypeKey( 10 ), "title3",
//                                                      new String[][]{{"data/heading", "title3"}} );
//        service.index( doc3, false );
//
//        ContentIndexQuery query = new ContentIndexQuery( "data/heading CONTAINS 'title'", "" );
//        query.setCategoryFilter( createCategoryKeyList( 101 ) );
//        assertContentResultSetEquals( new int[]{1, 2}, service.query( query ) );
//
//        query = new ContentIndexQuery( "data/heading CONTAINS 'title'", "" );
//        query.setCategoryFilter( createCategoryKeyList( 102 ) );
//        assertContentResultSetEquals( new int[]{3}, service.query( query ) );
//    }
//
//    @Test
//    public void testContentQueryWithContentTypeFilter()
//    {
//
//        ContentDocument doc1 = createContentDocument( new ContentKey( 1 ), new CategoryKey( 101 ), new ContentTypeKey( 10 ), "title1",
//                                                      new String[][]{{"data/heading", "title1"}} );
//        service.index( doc1, false );
//
//        ContentDocument doc2 = createContentDocument( new ContentKey( 2 ), new CategoryKey( 101 ), new ContentTypeKey( 10 ), "title2",
//                                                      new String[][]{{"data/heading", "title2"}} );
//        service.index( doc2, false );
//
//        ContentDocument doc3 = createContentDocument( new ContentKey( 3 ), new CategoryKey( 101 ), new ContentTypeKey( 11 ), "title3",
//                                                      new String[][]{{"data/heading", "title3"}} );
//        service.index( doc3, false );
//
//        ContentIndexQuery query = new ContentIndexQuery( "data/heading CONTAINS 'title'", "" );
//        query.setContentTypeFilter( createContentTypeList( 10 ) );
//        assertContentResultSetEquals( new int[]{1, 2}, service.query( query ) );
//
//        query = new ContentIndexQuery( "data/heading CONTAINS 'title'", "" );
//        query.setContentTypeFilter( createContentTypeList( 11 ) );
//        assertContentResultSetEquals( new int[]{3}, service.query( query ) );
//    }
//
//    @Test
//    public void testContentQueryWithCategoryFilterAndComplexLogicalExpression()
//    {
//
//        ContentDocument doc1 = createContentDocument( new ContentKey( 1 ), new CategoryKey( 101 ), new ContentTypeKey( 10 ), "title1",
//                                                      new String[][]{{"data/a", "1"}, {"data/b", "2"}, {"data/c", "3"}} );
//        service.index( doc1, false );
//
//        ContentDocument doc2 = createContentDocument( new ContentKey( 2 ), new CategoryKey( 101 ), new ContentTypeKey( 11 ), "title2",
//                                                      new String[][]{{"data/a", "2"}, {"data/b", "2"}, {"data/c", "1"}} );
//        service.index( doc2, false );
//
//        ContentDocument doc3 = createContentDocument( new ContentKey( 3 ), new CategoryKey( 101 ), new ContentTypeKey( 10 ), "title3",
//                                                      new String[][]{{"data/a", "2"}, {"data/b", "1"}, {"data/c", "3"}} );
//        service.index( doc3, false );
//
//        ContentIndexQuery query = new ContentIndexQuery( "(data/a = 1 AND data/b = 2)", "" );
//        query.setCategoryFilter( createCategoryKeyList( 101 ) );
//        assertContentResultSetEquals( new int[]{1}, service.query( query ) );
//
//        query = new ContentIndexQuery( "(data/a = 1 AND data/b = 2) OR data/c = 1", "" );
//        query.setCategoryFilter( createCategoryKeyList( 101 ) );
//        assertContentResultSetEquals( new int[]{1, 2}, service.query( query ) );
//
//        query = new ContentIndexQuery( "(data/a = 1 OR data/b = 2) AND data/c = 1", "" );
//        query.setCategoryFilter( createCategoryKeyList( 101 ) );
//        assertContentResultSetEquals( new int[]{2}, service.query( query ) );
//
//        query = new ContentIndexQuery( "data/a = 3 OR data/b = 1 OR data/c = 1", "" );
//        query.setCategoryFilter( createCategoryKeyList( 101 ) );
//        assertContentResultSetEquals( new int[]{2, 3}, service.query( query ) );
//
//        query = new ContentIndexQuery( "(data/a = 2 OR data/b = 2) AND (data/a = 1 OR data/c = 3)", "" );
//        query.setCategoryFilter( createCategoryKeyList( 101 ) );
//        assertContentResultSetEquals( new int[]{1, 3}, service.query( query ) );
//
//        query = new ContentIndexQuery( "(data/a = 2 AND data/b = 2) OR (data/a = 1 AND data/c = 3)", "" );
//        query.setCategoryFilter( createCategoryKeyList( 101 ) );
//        assertContentResultSetEquals( new int[]{1, 2}, service.query( query ) );
//    }
//
//    @Test
//    public void testContentQueryWithCategoryFilterAndContentTypeNameSearch()
//    {
//
//        ContentDocument doc1 = createContentDocument( new ContentKey( 1 ), new CategoryKey( 101 ), new ContentTypeKey( 10 ), "title1",
//                                                      new String[][]{{"data/heading", "title1"}} );
//        doc1.setContentTypeName( "Article3" );
//        service.index( doc1, false );
//
//        ContentDocument doc2 = createContentDocument( new ContentKey( 2 ), new CategoryKey( 101 ), new ContentTypeKey( 10 ), "title2",
//                                                      new String[][]{{"data/heading", "title2"}} );
//        doc2.setContentTypeName( "Article3" );
//        service.index( doc2, false );
//
//        ContentDocument doc3 = createContentDocument( new ContentKey( 3 ), new CategoryKey( 101 ), new ContentTypeKey( 11 ), "title3",
//                                                      new String[][]{{"data/heading", "title3"}} );
//        doc3.setContentTypeName( "Loooooooooooooong-content-type-name-it-is" );
//        service.index( doc3, false );
//
//        ContentIndexQuery query = new ContentIndexQuery( "contenttype = 'Article3'", "" );
//        query.setContentTypeFilter( createContentTypeList( 10 ) );
//        assertContentResultSetEquals( new int[]{1, 2}, service.query( query ) );
//
//        query = new ContentIndexQuery( "contenttype = 'Loooooooooooooong-content-type-name-it-is'", "" );
//        query.setContentTypeFilter( createContentTypeList( 11 ) );
//        assertContentResultSetEquals( new int[]{3}, service.query( query ) );
//    }
//
//    @Test
//    public void testQueryReturnsEmptyResultWhenNowIsOneMillisecondBeforePublishFrom()
//    {
//        ContentDocument doc1 = new ContentDocument( new ContentKey( 3001 ) );
//        doc1.setCategoryKey( new CategoryKey( 201 ) );
//        doc1.setContentTypeKey( new ContentTypeKey( 101 ) );
//        doc1.setContentTypeName( "Person" );
//        doc1.setTitle( "Jørund Vier Skriubakken" );
//        doc1.setPublishFrom( new DateTime( 2010, 4, 19, 13, 1, 0, 0 ).toDate() );
//        doc1.setStatus( 2 );
//        doc1.setPriority( 0 );
//        service.index( doc1, true );
//
//        ContentIndexQuery query = new ContentIndexQuery( "@key = 3001" );
//        query.setContentOnlineAtFilter( new DateTime( 2010, 4, 19, 13, 0, 59, 999 ).toDate() );
//
//        ContentResultSet contentResultSet = service.query( query );
//        assertEquals( 0, contentResultSet.getKeys().size() );
//    }
//
//    @Test
//    public void testQueryReturnsAResultWhenNowIsSameAsPublishFrom()
//    {
//        ContentDocument doc1 = new ContentDocument( new ContentKey( 3001 ) );
//        doc1.setCategoryKey( new CategoryKey( 201 ) );
//        doc1.setContentTypeKey( new ContentTypeKey( 101 ) );
//        doc1.setContentTypeName( "Person" );
//        doc1.setTitle( "Jørund Vier Skriubakken" );
//        doc1.setPublishFrom( new DateTime( 2010, 4, 19, 13, 1, 0, 0 ).toDate() );
//        doc1.setStatus( 2 );
//        doc1.setPriority( 0 );
//        service.index( doc1, true );
//
//        ContentIndexQuery query = new ContentIndexQuery( "@key = 3001" );
//        query.setContentOnlineAtFilter( new DateTime( 2010, 4, 19, 13, 1, 0, 0 ).toDate() );
//
//        ContentResultSet contentResultSet = service.query( query );
//        assertEquals( 1, contentResultSet.getKeys().size() );
//    }
//
//    @Test
//    public void testQueryReturnsAResultWhenNowIsOneMillisecondAfterPublishFrom()
//    {
//        ContentDocument doc1 = new ContentDocument( new ContentKey( 3001 ) );
//        doc1.setCategoryKey( new CategoryKey( 201 ) );
//        doc1.setContentTypeKey( new ContentTypeKey( 101 ) );
//        doc1.setContentTypeName( "Person" );
//        doc1.setTitle( "Jørund Vier Skriubakken" );
//        doc1.setPublishFrom( new DateTime( 2010, 4, 19, 13, 1, 0, 0 ).toDate() );
//        doc1.setStatus( 2 );
//        doc1.setPriority( 0 );
//        service.index( doc1, true );
//
//        ContentIndexQuery query = new ContentIndexQuery( "@key = 3001" );
//        query.setContentOnlineAtFilter( new DateTime( 2010, 4, 19, 13, 1, 0, 1 ).toDate() );
//
//        ContentResultSet contentResultSet = service.query( query );
//        assertEquals( 1, contentResultSet.getKeys().size() );
//    }
//
//    @Test
//    public void testQueryReturnsEmptytWhenNowIsSameAsBothPublishFromAndPublishTo()
//    {
//        ContentDocument doc1 = new ContentDocument( new ContentKey( 3001 ) );
//        doc1.setCategoryKey( new CategoryKey( 201 ) );
//        doc1.setContentTypeKey( new ContentTypeKey( 101 ) );
//        doc1.setContentTypeName( "Person" );
//        doc1.setTitle( "Jørund Vier Skriubakken" );
//        doc1.setPublishFrom( new DateTime( 2010, 4, 19, 13, 1, 0, 0 ).toDate() );
//        doc1.setPublishTo( new DateTime( 2010, 4, 19, 13, 1, 0, 0 ).toDate() );
//        doc1.setStatus( 2 );
//        doc1.setPriority( 0 );
//        service.index( doc1, true );
//
//        ContentIndexQuery query = new ContentIndexQuery( "@key = 3001" );
//        query.setContentOnlineAtFilter( new DateTime( 2010, 4, 19, 13, 1, 0, 0 ).toDate() );
//
//        ContentResultSet contentResultSet = service.query( query );
//        assertEquals( 0, contentResultSet.getKeys().size() );
//    }
//
//    @Test
//    public void testQueryReturnsEmptyWhenNowIsAfterPublishFromAndSameAsPublishTo()
//    {
//        ContentDocument doc1 = new ContentDocument( new ContentKey( 3001 ) );
//        doc1.setCategoryKey( new CategoryKey( 201 ) );
//        doc1.setContentTypeKey( new ContentTypeKey( 101 ) );
//        doc1.setContentTypeName( "Person" );
//        doc1.setTitle( "Jørund Vier Skriubakken" );
//        doc1.setPublishFrom( new DateTime( 2010, 4, 19, 13, 1, 0, 0 ).toDate() );
//        doc1.setPublishTo( new DateTime( 2010, 4, 19, 13, 2, 0, 0 ).toDate() );
//        doc1.setStatus( 2 );
//        doc1.setPriority( 0 );
//        service.index( doc1, true );
//
//        ContentIndexQuery query = new ContentIndexQuery( "@key = 3001" );
//        query.setContentOnlineAtFilter( new DateTime( 2010, 4, 19, 13, 2, 0, 0 ).toDate() );
//
//        ContentResultSet contentResultSet = service.query( query );
//        assertEquals( 0, contentResultSet.getKeys().size() );
//    }
//
//    @Test
//    public void testQueryReturnsAResultWhenNowIsAfterPublishFromAndWithinThePublishToMinute()
//    {
//        ContentDocument doc1 = new ContentDocument( new ContentKey( 3001 ) );
//        doc1.setCategoryKey( new CategoryKey( 201 ) );
//        doc1.setContentTypeKey( new ContentTypeKey( 101 ) );
//        doc1.setContentTypeName( "Person" );
//        doc1.setTitle( "J�rund Vier Skriubakken" );
//        doc1.setPublishFrom( new DateTime( 2010, 4, 19, 13, 1, 0, 0 ).toDate() );
//        doc1.setPublishTo( new DateTime( 2010, 4, 19, 13, 2, 0, 0 ).toDate() );
//        doc1.setStatus( 2 );
//        doc1.setPriority( 0 );
//        service.index( doc1, true );
//
//        ContentIndexQuery query = new ContentIndexQuery( "@key = 3001" );
//        query.setContentOnlineAtFilter( new DateTime( 2010, 4, 19, 13, 1, 59, 999 ).toDate() );
//
//        ContentResultSet contentResultSet = service.query( query );
//        assertEquals( 1, contentResultSet.getKeys().size() );
//    }
//
//    @Test
//    public void testQueryThrowsExceptionWhenQueryHasNoRestrictions()
//    {
//        String expectedMessageStartsWidth =
//            "Prevented executing a content index query that is too open (i.e. possibly fetching all content):";
//
//        ContentIndexQuery query = new ContentIndexQuery( "" );
//        try
//        {
//            service.query( query );
//            fail( "Expected exception" );
//        }
//        catch ( Exception e )
//        {
//            assertException( IllegalArgumentException.class, expectedMessageStartsWidth, e );
//        }
//
//        query = new ContentIndexQuery( "" );
//        query.setCount( 101 );
//        try
//        {
//            service.query( query );
//            fail( "Expected exception" );
//        }
//        catch ( Exception e )
//        {
//            assertException( IllegalArgumentException.class, expectedMessageStartsWidth, e );
//        }
//
//        query = new ContentIndexQuery( "" );
//        query.setCategoryFilter( new ArrayList<CategoryKey>() );
//        query.setCount( 101 );
//        try
//        {
//            service.query( query );
//            fail( "Expected exception" );
//        }
//        catch ( Exception e )
//        {
//            assertException( IllegalArgumentException.class, expectedMessageStartsWidth, e );
//        }
//
//        query = new ContentIndexQuery( "" );
//        query.setContentFilter( new ArrayList<ContentKey>() );
//        query.setCount( 101 );
//        try
//        {
//            service.query( query );
//            fail( "Expected exception" );
//        }
//        catch ( Exception e )
//        {
//            assertException( IllegalArgumentException.class, expectedMessageStartsWidth, e );
//        }
//
//        query = new ContentIndexQuery( "" );
//        query.setSectionFilter( new ArrayList<MenuItemEntity>(), ContentIndexQuery.SectionFilterStatus.APPROVED_ONLY );
//        query.setCount( 101 );
//        try
//        {
//            service.query( query );
//            fail( "Expected exception" );
//        }
//        catch ( Exception e )
//        {
//            assertException( IllegalArgumentException.class, expectedMessageStartsWidth, e );
//        }
//    }


}

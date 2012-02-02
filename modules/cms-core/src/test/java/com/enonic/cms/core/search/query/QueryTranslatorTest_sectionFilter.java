package com.enonic.cms.core.search.query;

import java.util.HashSet;
import java.util.Set;

import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;

import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 2/2/12
 * Time: 2:54 PM
 */
public class QueryTranslatorTest_sectionFilter
    extends QueryTranslatorBaseTest
{

    @Test
    public void testFilterQuery_section_filter_default_status()
        throws Exception
    {
        String expected_search_result = "{\n" +
            "  \"from\" : 0,\n" +
            "  \"size\" : " + QUERY_DEFAULT_SIZE + ",\n" +
            "  \"query\" : {\n" +
            "    \"match_all\" : {\n" +
            "    }\n" +
            "  },\n" +
            "  \"filter\" : {\n" +
            "    \"bool\" : {\n" +
            "      \"should\" : {\n" +
            "        \"terms\" : {\n" +
            "          \"contentlocations_approved_numeric\" : [ \"1\", \"2\", \"3\" ]\n" +
            "        }\n" +
            "      },\n" +
            "      \"should\" : {\n" +
            "        \"terms\" : {\n" +
            "          \"contentlocations_unapproved_numeric\" : [ \"1\", \"2\", \"3\" ]\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";

        Set<MenuItemEntity> sectionFilter = new HashSet<MenuItemEntity>();
        MenuItemEntity entity1 = new MenuItemEntity();
        entity1.setKey( 1 );
        sectionFilter.add( entity1 );
        MenuItemEntity entity2 = new MenuItemEntity();
        entity2.setKey( 2 );
        sectionFilter.add( entity2 );
        MenuItemEntity entity3 = new MenuItemEntity();
        entity3.setKey( 3 );
        sectionFilter.add( entity3 );

        ContentIndexQuery query = createContentQuery( sectionFilter );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        compareStringsIgnoreFormatting( expected_search_result, builder.toString() );
    }

    @Test
    public void testFilterQuery_section_filter_unapproved_only()
        throws Exception
    {
        String expected_search_result = "{\n" +
            "  \"from\" : 0,\n" +
            "  \"size\" : " + QUERY_DEFAULT_SIZE + ",\n" +
            "  \"query\" : {\n" +
            "    \"match_all\" : {\n" +
            "    }\n" +
            "  },\n" +
            "  \"filter\" : {\n" +
            "    \"terms\" : {\n" +
            "      \"contentlocations_unapproved_numeric\" : [ \"1\", \"2\", \"3\" ]\n" +
            "    }\n" +
            "  }\n" +
            "}";

        Set<MenuItemEntity> sectionFilter = new HashSet<MenuItemEntity>();
        MenuItemEntity entity1 = new MenuItemEntity();
        entity1.setKey( 1 );
        sectionFilter.add( entity1 );
        MenuItemEntity entity2 = new MenuItemEntity();
        entity2.setKey( 2 );
        sectionFilter.add( entity2 );
        MenuItemEntity entity3 = new MenuItemEntity();
        entity3.setKey( 3 );
        sectionFilter.add( entity3 );

        ContentIndexQuery query = createContentQuery( "" );
        query.setSectionFilter( sectionFilter, ContentIndexQuery.SectionFilterStatus.UNAPPROVED_ONLY );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        compareStringsIgnoreFormatting( expected_search_result, builder.toString() );
    }


    @Test
    public void testFilterQuery_section_filter_approved_only()
        throws Exception
    {
        String expected_search_result = "{\n" +
            "  \"from\" : 0,\n" +
            "  \"size\" : " + QUERY_DEFAULT_SIZE + ",\n" +
            "  \"query\" : {\n" +
            "    \"match_all\" : {\n" +
            "    }\n" +
            "  },\n" +
            "  \"filter\" : {\n" +
            "    \"terms\" : {\n" +
            "      \"contentlocations_approved_numeric\" : [ \"1\", \"2\", \"3\" ]\n" +
            "    }\n" +
            "  }\n" +
            "}";

        Set<MenuItemEntity> sectionFilter = new HashSet<MenuItemEntity>();
        MenuItemEntity entity1 = new MenuItemEntity();
        entity1.setKey( 1 );
        sectionFilter.add( entity1 );
        MenuItemEntity entity2 = new MenuItemEntity();
        entity2.setKey( 2 );
        sectionFilter.add( entity2 );
        MenuItemEntity entity3 = new MenuItemEntity();
        entity3.setKey( 3 );
        sectionFilter.add( entity3 );

        ContentIndexQuery query = createContentQuery( "" );
        query.setSectionFilter( sectionFilter, ContentIndexQuery.SectionFilterStatus.APPROVED_ONLY );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        compareStringsIgnoreFormatting( expected_search_result, builder.toString() );
    }

}

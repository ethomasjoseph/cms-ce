package com.enonic.cms.core.search.query;

import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;

import com.enonic.cms.core.content.index.ContentIndexQuery;

import static junit.framework.Assert.assertEquals;

public class QueryTranslatorTest_range
    extends QueryTranslatorBaseTest
{

    @Test
    public void testGreaterThan_key_int()
        throws Exception
    {
        String expected_search_result =
            "{\r\n" + "  \"from\" : 0,\r\n" + "  \"size\" : " + QUERY_DEFAULT_SIZE + ",\r\n" + "  \"query\" : {\r\n" +
                "    \"range\" : {\r\n" + "      \"key_numeric\" : {\r\n" + "        \"from\" : 100.0,\r\n" + "        \"to\" : null,\r\n" +
                "        \"include_lower\" : false,\r\n" + "        \"include_upper\" : true\r\n" + "      }\r\n" + "    }\r\n" +
                "  }\r\n}";

        ContentIndexQuery query = createContentQuery( "key > 100" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        assertEquals( expected_search_result, builder.toString() );
    }

    @Test
    public void testGreaterThan_key_double()
        throws Exception
    {
        String expected_search_result =
            "{\r\n" + "  \"from\" : 0,\r\n" + "  \"size\" : " + QUERY_DEFAULT_SIZE + ",\r\n" + "  \"query\" : {\r\n" +
                "    \"range\" : {\r\n" + "      \"key_numeric\" : {\r\n" + "        \"from\" : 100.0,\r\n" + "        \"to\" : null,\r\n" +
                "        \"include_lower\" : false,\r\n" + "        \"include_upper\" : true\r\n" + "      }\r\n" + "    }\r\n" +
                "  }\r\n}";

        ContentIndexQuery query = createContentQuery( "key > 100.0" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        assertEquals( expected_search_result, builder.toString() );

    }

    @Test
    public void testGreaterThan_key_string()
        throws Exception
    {
        String expected_search_result =
            "{\r\n" + "  \"from\" : 0,\r\n" + "  \"size\" : " + QUERY_DEFAULT_SIZE + ",\r\n" + "  \"query\" : {\r\n" +
                "    \"range\" : {\r\n" + "      \"key\" : {\r\n" + "        \"from\" : \"100\",\r\n" + "        \"to\" : null,\r\n" +
                "        \"include_lower\" : false,\r\n" + "        \"include_upper\" : true\r\n" + "      }\r\n" + "    }\r\n" +
                "  }\r\n}";

        ContentIndexQuery query = createContentQuery( "key > '100'" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        assertEquals( expected_search_result, builder.toString() );
    }

    @Test
    public void testGreaterThanEquals_key_string()
        throws Exception
    {
        String expected_search_result =
            "{\r\n" + "  \"from\" : 0,\r\n" + "  \"size\" : " + QUERY_DEFAULT_SIZE + ",\r\n" + "  \"query\" : {\r\n" +
                "    \"range\" : {\r\n" + "      \"key\" : {\r\n" + "        \"from\" : \"100\",\r\n" + "        \"to\" : null,\r\n" +
                "        \"include_lower\" : true,\r\n" + "        \"include_upper\" : true\r\n" + "      }\r\n" + "    }\r\n" + "  }\r\n}";

        ContentIndexQuery query = createContentQuery( "key >= '100'" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        assertEquals( expected_search_result, builder.toString() );
    }

    @Test
    public void testLessThan_key_string()
        throws Exception
    {
        String expected_search_result =
            "{\r\n" + "  \"from\" : 0,\r\n" + "  \"size\" : " + QUERY_DEFAULT_SIZE + ",\r\n" + "  \"query\" : {\r\n" +
                "    \"range\" : {\r\n" + "      \"key\" : {\r\n" + "        \"from\" : null,\r\n" + "        \"to\" : \"100\",\r\n" +
                "        \"include_lower\" : true,\r\n" + "        \"include_upper\" : false\r\n" + "      }\r\n" + "    }\r\n" +
                "  }\r\n}";

        ContentIndexQuery query = createContentQuery( "key < '100'" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        assertEquals( expected_search_result, builder.toString() );
    }

    @Test
    public void testLessThanEquals_key_string()
        throws Exception
    {
        String expected_search_result =
            "{\r\n" + "  \"from\" : 0,\r\n" + "  \"size\" : " + QUERY_DEFAULT_SIZE + ",\r\n" + "  \"query\" : {\r\n" +
                "    \"range\" : {\r\n" + "      \"key\" : {\r\n" + "        \"from\" : null,\r\n" + "        \"to\" : \"100\",\r\n" +
                "        \"include_lower\" : true,\r\n" + "        \"include_upper\" : true\r\n" + "      }\r\n" + "    }\r\n" + "  }\r\n}";

        ContentIndexQuery query = createContentQuery( "key <= '100'" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        assertEquals( expected_search_result, builder.toString() );
    }


}

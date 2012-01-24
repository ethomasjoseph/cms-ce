package com.enonic.cms.core.search.query;

import org.elasticsearch.index.query.QueryBuilder;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 1/24/12
 * Time: 12:48 PM
 */
public class LikeQueryBuilderCreatorTest
    extends QueryTranslatorBaseTest
{

    @Test
    public void testWildCardQuery()
    {
        String expected = "{\n" +
            "  \"wildcard\" : {\n" +
            "    \"_all\" : {\n" +
            "      \"wildcard\" : \"test\"\n" +
            "    }\n" +
            "  }\n" +
            "}";

        QueryPath queryPath = QueryPathCreator.createQueryPath( "*" );

        final QueryBuilder queryBuilder = LikeQueryBuilderCreator.buildLikeQuery( queryPath, "test" );

        System.out.println( queryBuilder.toString() );

        compareStringsIgnoreFormatting( expected, queryBuilder.toString() );
    }


    @Test
    public void testAttachmentAsHasChild()
    {
        String expected = "{\n" +
            "  \"has_child\" : {\n" +
            "    \"query\" : {\n" +
            "      \"wildcard\" : {\n" +
            "        \"attachment_title\" : {\n" +
            "          \"wildcard\" : \"test\"\n" +
            "        }\n" +
            "      }\n" +
            "    },\n" +
            "    \"type\" : \"binaries\"\n" +
            "  }\n" +
            "}";

        QueryPath queryPath = QueryPathCreator.createQueryPath( "attachment_title" );

        final QueryBuilder queryBuilder = LikeQueryBuilderCreator.buildLikeQuery( queryPath, "test" );

        compareStringsIgnoreFormatting( expected, queryBuilder.toString() );
    }

    @Test
    public void testLike()
    {
        String expected = "{\n" +
            "  \"wildcard\" : {\n" +
            "    \"data_title\" : {\n" +
            "      \"wildcard\" : \"test\"\n" +
            "    }\n" +
            "  }\n" +
            "}";

        QueryPath queryPath = QueryPathCreator.createQueryPath( "data_title" );

        final QueryBuilder queryBuilder = LikeQueryBuilderCreator.buildLikeQuery( queryPath, "test" );

        System.out.println( queryBuilder.toString() );

        compareStringsIgnoreFormatting( expected, queryBuilder.toString() );
    }

}

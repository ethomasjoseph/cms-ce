package com.enonic.cms.core.search.query;

import org.junit.Test;

import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.content.index.queryexpression.QueryParserException;

public class QueryTranslatorTest_invalid_expression
    extends QueryTranslatorBaseTest
{
    @Test(expected = QueryParserException.class)
    public void testNotParsableExpression()
        throws Exception
    {
        ContentIndexQuery query = createContentQuery( "title INN (\"Hello\")" );

        getQueryTranslator().build( query );
    }
}

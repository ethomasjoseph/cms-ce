package com.enonic.cms.core.search.query;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;

import com.enonic.cms.core.search.builder.IndexFieldNameConstants;

public class TermQueryBuilderCreator
    extends BaseQueryBuilder
{
    public TermQueryBuilderCreator()
    {
    }

    public static QueryBuilder buildTermQuery( QueryPath path, Object singleValue )
    {
        TermQueryBuilder termQuery;

        final boolean isWildCardPath = path.isWildCardPath();

        if ( isWildCardPath )
        {
            path.setMatchAllPath();
        }

        //HANDLE NUMERIC WILDCARD

        if ( singleValue instanceof Number && !isWildCardPath )
        {
            Number number = (Number) singleValue;
            termQuery = QueryBuilders.termQuery( path.getPath() + IndexFieldNameConstants.NUMERIC_FIELD_POSTFIX, number );
        }
        else if ( singleValue instanceof Number )
        {
            Number number = (Number) singleValue;
            termQuery = QueryBuilders.termQuery( QueryFieldNameResolver.resolveQueryFieldName( path.getPath() ), number );
        }
        else
        {
            String stringValue = (String) singleValue;
            termQuery = QueryBuilders.termQuery( path.getPath(), StringUtils.lowerCase( stringValue ) );
        }

        if ( path.doRenderAsHasChildQuery() )
        {
            return wrapInHasChildQuery( path, termQuery );
        }
        else
        {
            return termQuery;
        }

    }


}

/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import java.util.Collection;

import org.springframework.util.Assert;

import com.enonic.cms.core.resolver.ContentAccessResolver;
import com.enonic.cms.store.dao.ContentDao;

import com.enonic.cms.core.content.resultset.RelatedChildContent;
import com.enonic.cms.core.content.resultset.RelatedContent;
import com.enonic.cms.core.content.resultset.RelatedContentResultSet;
import com.enonic.cms.core.content.resultset.RelatedContentResultSetImpl;


public class RelatedContentFetcherForContentVersion
    extends AbstractRelatedContentFetcher
{

    private Collection<ContentVersionEntity> originallyRequestedContentVersions;

    public RelatedContentFetcherForContentVersion( ContentDao contentDao, ContentAccessResolver contentAccessResolver )
    {
        super( contentDao, contentAccessResolver );
    }

    public RelatedContentResultSet fetch( final Collection<ContentVersionEntity> versions )
    {
        return doFetch( versions, false );
    }

    public RelatedContentResultSet fetch( final Collection<ContentVersionEntity> versions, final boolean includeVisited )
    {
        return doFetch( versions, includeVisited );
    }

    private RelatedContentResultSet doFetch( Collection<ContentVersionEntity> versions, final boolean includeVisited )
    {
        Assert.notNull( versions, "versions cannot be null" );

        originallyRequestedContentVersions = versions;
        relatedContentResultSet = new RelatedContentResultSetImpl();

        boolean fetchChildren = maxChildrenLevel > 0;
        if ( fetchChildren )
        {
            Collection<RelatedChildContent> rootRelatedChildren = doFindRelatedChildren( versions );
            if ( versions.size() > 0 )
            {
                doAddAndFetchChildren( rootRelatedChildren, maxChildrenLevel, includeVisited );
                for ( RelatedChildContent rootRelatedChild : rootRelatedChildren )
                {
                    if ( isAddableToRootRelated( rootRelatedChild ) )
                    {
                        relatedContentResultSet.addRootRelatedChild( rootRelatedChild );
                    }
                }
            }
        }

        return relatedContentResultSet;
    }

    @Override
    protected boolean isAddableToRootRelated( RelatedContent relatedToAdd )
    {
        return includeOfflineContent() || isAvailable( relatedToAdd );
    }

    @Override
    protected boolean isAddable( final RelatedContent relatedToAdd, final boolean includeVisited )
    {
        final ContentEntity content = relatedToAdd.getContent();
        final boolean contentIsAllreadyVisited = visitedChildRelatedContent.contains( relatedToAdd.getContent().getKey() );

        final boolean contentVersionIsInOriginallyRequestedContentVersionSet =
            originallyRequestedContentVersions.contains( content.getMainVersion() );

        return ( includeOfflineContent() || isAvailable( relatedToAdd ) ) && ( includeVisited || !contentIsAllreadyVisited ) &&
            !contentVersionIsInOriginallyRequestedContentVersionSet;
    }
}

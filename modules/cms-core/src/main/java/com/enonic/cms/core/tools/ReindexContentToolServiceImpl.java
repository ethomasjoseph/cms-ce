/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.tools;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.enonic.cms.core.content.ContentService;
import com.enonic.cms.core.content.IndexService;
import com.enonic.cms.core.content.RegenerateIndexBatcher;
import com.enonic.cms.core.content.contenttype.ContentTypeEntity;

public class ReindexContentToolServiceImpl
    implements ReindexContentToolService
{

    private IndexService indexService;

    private ContentService contentService;

    protected static final int BATCH_SIZE = 10;


    public void reindexAllContent( List<String> logEntries )
    {
        logEntries.clear();

        long globalStart = System.currentTimeMillis();

        Collection<ContentTypeEntity> contentTypes = contentService.getAllContentTypes();

        logEntries.add( "Generating indexes for " + contentTypes.size() + " content types..." );

        RegenerateIndexBatcher batcher = null;

        int count = 1;
        for ( ContentTypeEntity contentType : contentTypes )
        {

            StringBuffer message = new StringBuffer();
            message.append( "Generating indexes for '" ).append( contentType.getName() ).append( "'" );
            message.append( " (#" ).append( count++ ).append( " of " ).append( contentTypes.size() ).append( ")..." );

            logEntries.add( message.toString() );

            long start = System.currentTimeMillis();

            batcher = new RegenerateIndexBatcher( indexService, contentService );

            batcher.regenerateIndex( contentType, BATCH_SIZE, logEntries );

            long end = System.currentTimeMillis();

            logEntries.add( "... index values generated in " + ( end - start ) + " ms" );
        }

        long globalTimeUsed = ( System.currentTimeMillis() - globalStart ) / 1000;
        String timeUsed = globalTimeUsed > 240 ? globalTimeUsed / 60 + " min" : globalTimeUsed + " sec";

        logEntries.add( "Reindexing of all content types was successful!" );
        logEntries.add( "Total time used: " + timeUsed );

        batcher.optimizeIndex();
    }

    @Autowired
    public void setIndexService( @Qualifier("indexService") IndexService value )
    {
        this.indexService = value;
    }

    @Autowired
    public void setContentService( @Qualifier("contentService") ContentService value )
    {
        this.contentService = value;
    }
}

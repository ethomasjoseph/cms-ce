/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.admin.account;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.search.indexing.AccountIndexing;

@Component
@Path("/admin/data/account/indexing/")
@Produces(MediaType.APPLICATION_JSON)
public final class IndexingResource
{
    @Autowired
    private AccountIndexing accountIndexing;

    @GET
    public Map<String, Object> handleIndex( final @QueryParam("start") boolean start )
    {
        if ( start )
        {
            accountIndexing.indexAccounts();
        }

        Map<String, Object> res = new HashMap<String, Object>();
        res.put( "indexing", accountIndexing.isRunning() );
        res.put( "progress", accountIndexing.getProgress() );
        return res;
    }

}

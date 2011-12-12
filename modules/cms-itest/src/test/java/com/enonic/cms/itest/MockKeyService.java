/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.itest;

import com.enonic.cms.core.service.KeyService;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Extend this mock class with functionality when needed.
 */
public class MockKeyService
    implements KeyService
{

    private static Map<String, AtomicInteger> nextKeyByTableName = new HashMap<String, AtomicInteger>();

    public int generateNextKeySafe( String tableName )
    {
        synchronized ( nextKeyByTableName )
        {
            AtomicInteger nextKey = nextKeyByTableName.get( tableName.toLowerCase() );
            if ( nextKey == null )
            {
                nextKey = new AtomicInteger( 0 );
                nextKeyByTableName.put( tableName.toLowerCase(), nextKey );
            }
            return nextKey.getAndIncrement();
        }
    }
}

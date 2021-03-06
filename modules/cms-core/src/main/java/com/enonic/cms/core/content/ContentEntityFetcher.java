/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import java.util.List;
import java.util.Map;

/**
 * This interface defines the content entity fetcher.
 */
public interface ContentEntityFetcher
{

    Map<ContentKey, ContentEntity> fetch( List<ContentKey> keys );

}

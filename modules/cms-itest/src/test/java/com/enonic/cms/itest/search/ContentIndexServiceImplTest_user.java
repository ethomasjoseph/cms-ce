package com.enonic.cms.itest.search;

import org.junit.Test;

import com.enonic.cms.core.content.index.ContentDocument;
import com.enonic.cms.core.content.index.ContentIndexQuery;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 1/13/12
 * Time: 2:18 PM
 */
public class ContentIndexServiceImplTest_user
    extends ContentIndexServiceTestBase
{

    @Test
    public void testIndexingAndSearchOnOwnerQualifiedName()
    {
        ContentDocument doc = createContentDocument( 101, "ost", null );
        doc.setOwnerQualifiedName( "incamono\\jvs" );

        service.index( doc, false );

        letTheIndexFinishItsWork();

        assertContentResultSetEquals( new int[]{101}, service.query( new ContentIndexQuery( "owner/qualifiedName = 'incamono\\jvs'", 10 ) ) );
    }


}

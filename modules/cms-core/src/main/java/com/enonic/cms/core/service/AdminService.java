/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.service;

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.core.language.LanguageKey;
import com.enonic.cms.core.content.binary.BinaryData;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.security.user.User;

public interface AdminService
{
    public int getContentKey( int categoryKey, String contentTitle );

    public int getCategoryKey( int superCategoryKey, String name );

    public int createCategory( User user, int superCategoryKey, String name );

    public int createCategory( User user, String xmlData );

    public XMLDocument getCategory( User user, int categoryKey );

    public int getCategoryKey( int contentKey );

    public int createContentType( User user, String xmlData );

    public void createLanguage( User user, String languageCode, String description );

    public int createMenuItem( User user, String xmlData );

    public int createUnit( String xmlData );

    public BinaryData getBinaryData( User user, int binaryDataKey );

    public XMLDocument getContent( User user, int contentKey, int parentLevel, int childrenLevel, int parentChildrenLevel );

    public XMLDocument getContentType( int contentTypeKey );

    public XMLDocument getContentType( int contentTypeKey, boolean includeContentCount );

    public int getContentTypeKey( int contentKey );

    public XMLDocument getLanguage( LanguageKey languageKey );

    public XMLDocument getMenuItem( User user, int key, boolean withParents );

    public XMLDocument getMenuItem( User user, int key, boolean withParents, boolean complete );

    public String getMenuItemName( int menuItemKey );

    public String getPageTemplate( int pageTemplateKey );

    public XMLDocument getGroup( String gKey );

    public XMLDocument getContent( User user, CategoryKey categoryKey, boolean includeSubCategories, String orderBy, int index, int count,
                                   int childrenLevel, int parentLevel, int parentChildrenLevel );

    public boolean isEnterpriseAdmin( User user );

    public XMLDocument getPath( User user, int type, int key );

    public XMLDocument getContentHandler( int contentHandlerKey );

    public int createContentHandler( User user, String xmlData );

    public XMLDocument getData( User user, int type, int[] keys );

    public int getBinaryDataKey( int contentKey, String label );

    public boolean initializeDatabaseSchema()
        throws Exception;

    public boolean initializeDatabaseValues()
        throws Exception;
}

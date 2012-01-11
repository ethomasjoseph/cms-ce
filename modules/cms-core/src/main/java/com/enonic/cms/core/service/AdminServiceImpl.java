/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.service;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.enonic.vertical.engine.AdminEngine;
import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.core.language.LanguageKey;
import com.enonic.cms.core.content.binary.BinaryData;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.structure.page.template.PageTemplateKey;

public class AdminServiceImpl
    implements AdminService
{
    public void setAdminEngine( AdminEngine value )
    {
        adminEngine = value;
    }

    protected AdminEngine adminEngine;

    public int getContentKey( int categoryKey, String contentTitle )
    {
        return adminEngine.getContentKey( categoryKey, contentTitle );
    }

    public int getCategoryKey( int superCategoryKey, String name )
    {
        return adminEngine.getCategoryKey( superCategoryKey, name );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int createCategory( User user, int superCategoryKey, String name )
    {
        return adminEngine.createCategory( user, superCategoryKey, name );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int createCategory( User user, String xmlData )
    {
        return adminEngine.createCategory( user, xmlData );
    }

    public XMLDocument getCategory( User user, int categoryKey )
    {
        return adminEngine.getCategory( user, categoryKey );
    }

    public int getCategoryKey( int contentKey )
    {
        return adminEngine.getCategoryKey( contentKey );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int createContentType( User user, String xmlData )
    {
        return adminEngine.createContentType( user, xmlData );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void createLanguage( User user, String languageCode, String description )
    {
        adminEngine.createLanguage( user, languageCode, description );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int createMenuItem( User user, String xmlData )
    {
        return adminEngine.createMenuItem( user, xmlData );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int createUnit( String xmlData )
    {
        return adminEngine.createUnit( xmlData );
    }

    public BinaryData getBinaryData( User user, int binaryDataKey )
    {
        return adminEngine.getBinaryData( binaryDataKey );
    }

    public XMLDocument getContent( User user, int contentKey, int parentLevel, int childrenLevel, int parentChildrenLevel )
    {
        return adminEngine.getContent( user, contentKey, parentLevel, childrenLevel, parentChildrenLevel );
    }

    public XMLDocument getContentType( int contentTypeKey )
    {
        return adminEngine.getContentType( contentTypeKey );
    }

    public XMLDocument getContentType( int contentTypeKey, boolean includeContentCount )
    {
        return adminEngine.getContentType( contentTypeKey, includeContentCount );
    }

    public int getContentTypeKey( int contentKey )
    {
        return adminEngine.getContentTypeKey( contentKey );
    }

    public XMLDocument getLanguage( LanguageKey languageKey )
    {
        return adminEngine.getLanguage( languageKey );
    }

    public XMLDocument getMenuItem( User user, int key, boolean withParents )
    {
        return adminEngine.getMenuItem( user, key, withParents );
    }

    public XMLDocument getMenuItem( User user, int key, boolean withParents, boolean complete )
    {
        return adminEngine.getMenuItem( user, key, withParents, complete );
    }

    public String getMenuItemName( int menuItemKey )
    {
        return adminEngine.getMenuItemName( menuItemKey );
    }

    public String getPageTemplate( int pageTemplateKey )
    {
        return adminEngine.getPageTemplate( new PageTemplateKey( pageTemplateKey ) );
    }

    public XMLDocument getGroup( String gKey )
    {
        return adminEngine.getGroup( gKey );
    }

    public XMLDocument getContent( User user, CategoryKey categoryKey, boolean includeSubCategories, String orderBy, int index, int count,
                                   int childrenLevel, int parentLevel, int parentChildrenLevel )
    {
        return adminEngine.getContent( user, categoryKey, includeSubCategories, orderBy, index, count, childrenLevel, parentLevel,
                                       parentChildrenLevel );
    }

    public boolean isEnterpriseAdmin( User user )
    {
        return adminEngine.isEnterpriseAdmin( user );
    }

    public XMLDocument getPath( User user, int type, int key )
    {
        return adminEngine.getPath( user, type, key );
    }

    public XMLDocument getContentHandler( int contentHandlerKey )
    {
        return adminEngine.getContentHandler( contentHandlerKey );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int createContentHandler( User user, String xmlData )
    {
        return adminEngine.createContentHandler( user, xmlData );
    }

    public XMLDocument getData( User user, int type, int[] keys )
    {
        return adminEngine.getData(type, keys );
    }

    public int getBinaryDataKey( int contentKey, String label )
    {
        return adminEngine.getBinaryDataKey( contentKey, label );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean initializeDatabaseSchema()
        throws Exception
    {
        return adminEngine.initializeDatabaseSchema();
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean initializeDatabaseValues()
        throws Exception
    {
        return adminEngine.initializeDatabaseValues();
    }
}

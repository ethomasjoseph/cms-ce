/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine;

import java.util.*;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.engine.criteria.CategoryCriteria;
import com.enonic.vertical.engine.handlers.BinaryDataHandler;
import com.enonic.vertical.engine.handlers.CategoryHandler;
import com.enonic.vertical.engine.handlers.CommonHandler;
import com.enonic.vertical.engine.handlers.ContentHandler;
import com.enonic.vertical.engine.handlers.ContentObjectHandler;
import com.enonic.vertical.engine.handlers.GroupHandler;
import com.enonic.vertical.engine.handlers.LanguageHandler;
import com.enonic.vertical.engine.handlers.LogHandler;
import com.enonic.vertical.engine.handlers.MenuHandler;
import com.enonic.vertical.engine.handlers.PageHandler;
import com.enonic.vertical.engine.handlers.PageTemplateHandler;
import com.enonic.vertical.engine.handlers.SectionHandler;
import com.enonic.vertical.engine.handlers.SecurityHandler;
import com.enonic.vertical.engine.handlers.SystemHandler;
import com.enonic.vertical.engine.handlers.UnitHandler;
import com.enonic.vertical.engine.handlers.UserHandler;

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.language.LanguageKey;
import com.enonic.cms.core.content.ContentService;
import com.enonic.cms.core.content.ContentXMLCreator;
import com.enonic.cms.core.content.access.ContentAccessResolver;
import com.enonic.cms.core.content.binary.BinaryData;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.category.access.CategoryAccessResolver;
import com.enonic.cms.core.content.query.ContentByCategoryQuery;
import com.enonic.cms.core.content.query.RelatedContentQuery;
import com.enonic.cms.core.content.resultset.ContentResultSet;
import com.enonic.cms.core.content.resultset.RelatedContentResultSet;
import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.userstore.MemberOfResolver;
import com.enonic.cms.core.structure.page.template.PageTemplateKey;
import com.enonic.cms.store.dao.GroupDao;

public final class AdminEngine
    extends BaseEngine
    implements InitializingBean
{
    private BinaryDataHandler binaryDataHandler;

    private CategoryHandler categoryHandler;

    private CommonHandler commonHandler;

    private ContentHandler contentHandler;

    private ContentService contentService;

    private ContentObjectHandler contentObjectHandler;

    private GroupHandler groupHandler;

    @Autowired
    private MemberOfResolver memberOfResolver;

    private LanguageHandler languageHandler;


    private LogHandler logHandler;

    private MenuHandler menuHandler;

    private PageHandler pageHandler;

    private PageTemplateHandler pageTemplateHandler;

    private SectionHandler sectionHandler;

    private SecurityHandler securityHandler;

    private SecurityService securityService;

    private SystemHandler systemHandler;

    private UnitHandler unitHandler;

    private UserHandler userHandler;

    @Autowired
    private GroupDao groupDao;

    public void afterPropertiesSet()
        throws Exception
    {
        // event listeners
        menuHandler.addListener( logHandler );
    }

    public CategoryHandler getCategoryHandler()
    {
        return categoryHandler;
    }

    public CommonHandler getCommonHandler()
    {
        return commonHandler;
    }

    public ContentHandler getContentHandler()
    {
        return contentHandler;
    }

    public ContentObjectHandler getContentObjectHandler()
    {
        return contentObjectHandler;
    }

    public GroupHandler getGroupHandler()
    {
        return groupHandler;
    }

    public LanguageHandler getLanguageHandler()
    {
        return languageHandler;
    }

    public LogHandler getLogHandler()
    {
        return logHandler;
    }

    public MenuHandler getMenuHandler()
    {
        return menuHandler;
    }

    public PageHandler getPageHandler()
    {
        return pageHandler;
    }

    public PageTemplateHandler getPageTemplateHandler()
    {
        return pageTemplateHandler;
    }

    public SectionHandler getSectionHandler()
    {
        return sectionHandler;
    }

    public SecurityHandler getSecurityHandler()
    {
        return securityHandler;
    }

    public UserHandler getUserHandler()
    {
        return userHandler;
    }

    public int getContentKey( int categoryKey, String contentTitle )
    {
        return contentHandler.getContentKey( CategoryKey.parse( categoryKey ), contentTitle );
    }

    public int getCategoryKey( int superCategoryKey, String name )
    {
        return categoryHandler.getCategoryKey( superCategoryKey, name );
    }

    public int createCategory( User user, int superCategoryKey, String name )
    {
        return categoryHandler.createCategory( user, CategoryKey.parse( superCategoryKey ), name );
    }

    public int createCategory( User user, String xmlData )
    {

        Document doc = XMLTool.domparse( xmlData, "category" );
        Element categoryElem = doc.getDocumentElement();
        if ( !isEnterpriseAdmin( user ) )
        {
            CategoryKey superCategoryKey = CategoryKey.parse( categoryElem.getAttribute( "supercategorykey" ) );

            if ( !securityHandler.validateCategoryCreate( user, superCategoryKey ) )
            {
                String message = "User does not have access rights to create a new category";
                VerticalEngineLogger.errorSecurity(message, null );
            }
        }

        return categoryHandler.createCategory(user, doc);
    }

    public int createContentType( User user, String xmlData )
    {

        Document doc = XMLTool.domparse( xmlData, "contenttype" );

        if ( !( securityHandler.isSiteAdmin( user ) || isDeveloper( user ) ) )
        {
            String message = "User is not administrator or developer";
            VerticalEngineLogger.errorSecurity(message, null );
        }

        return contentHandler.createContentType( doc );
    }

    public void createLanguage( User user, String languageCode, String description )
    {

        if ( !isEnterpriseAdmin( user ) )
        {
            String message = "User is not enterprise administrator";
            VerticalEngineLogger.errorSecurity(message, null );
        }

        languageHandler.createLanguage( languageCode, description );
    }

    public int createMenuItem( User user, String xmlData )
    {
        return menuHandler.createMenuItem(user, xmlData);
    }

    public int createUnit( String xmlData )
        throws VerticalSecurityException
    {
        return unitHandler.createUnit(xmlData);
    }

    public BinaryData getBinaryData( int binaryDataKey )
    {
        return binaryDataHandler.getBinaryData(binaryDataKey);
    }

    public XMLDocument getContent( User user, int contentKey, int parentLevel, int childrenLevel, int parentChildrenLevel )
    {
        Document doc =
            contentHandler.getContent( user, contentKey, false, parentLevel, childrenLevel, parentChildrenLevel);
        securityHandler.appendAccessRights( user, doc );
        return XMLDocumentFactory.create(doc);
    }

    public XMLDocument getCategory( User user, int categoryKey )
    {
        Document doc = categoryHandler.getCategory( user, CategoryKey.parse( categoryKey ) );
        boolean hasSubs = categoryHandler.hasSubCategories( CategoryKey.parse( categoryKey ) );
        Element categoryElem = XMLTool.getElement( doc.getDocumentElement(), "category" );
        if ( categoryElem != null )
        {
            categoryElem.setAttribute( "subcategories", String.valueOf( hasSubs ) );
        }
        securityHandler.appendAccessRights( user, doc );

        return XMLDocumentFactory.create(doc);
    }

    public XMLDocument getMenuItem( User user, int key, boolean withParents )
    {
        Document doc = menuHandler.getMenuItem( user, key, withParents );
        securityHandler.appendAccessRights( user, doc );
        return XMLDocumentFactory.create( doc );
    }

    public XMLDocument getMenuItem( User user, int key, boolean withParents, boolean complete )
    {
        Document doc = menuHandler.getMenuItem(user, key, withParents, complete, true);
        securityHandler.appendAccessRights( user, doc );
        return XMLDocumentFactory.create(doc);
    }

    public int getCategoryKey( int contentKey )
    {
        CategoryKey categoryKey = contentHandler.getCategoryKey(contentKey);
        if ( categoryKey == null )
        {
            return -1;
        }
        return categoryKey.toInt();
    }

    public XMLDocument getContentType( int contentTypeKey )
    {
        Document doc = contentHandler.getContentType( contentTypeKey, false );
        return XMLDocumentFactory.create(doc);
    }

    public XMLDocument getContentType( int contentTypeKey, boolean includeContentCount )
    {
        Document doc = contentHandler.getContentType( contentTypeKey, includeContentCount );
        return XMLDocumentFactory.create(doc);
    }

    public int getContentTypeKey( int contentKey )
    {
        return contentHandler.getContentTypeKey( contentKey );
    }

    public XMLDocument getLanguage( LanguageKey languageKey )
    {
        return languageHandler.getLanguage( languageKey );
    }

    public String getMenuItemName( int menuItemKey )
    {
        return menuHandler.getMenuItemName( menuItemKey );
    }

    public String getPageTemplate( PageTemplateKey pageTemplateKey )
    {
        return pageTemplateHandler.getPageTemplate( pageTemplateKey ).getAsString();
    }

    public XMLDocument getGroup( String gKey )
    {
        return XMLDocumentFactory.create( groupHandler.getGroup( gKey ) );
    }

    public XMLDocument getContent( User oldTypeUser, CategoryKey categoryKey, boolean includeSubCategories, String orderBy, int index,
                                   int count, int childrenLevel, int parentLevel, int parentChildrenLevel )
    {
        UserEntity user = securityService.getUser( oldTypeUser.getKey() );
        List<CategoryKey> categories = CategoryKey.convertToList( categoryKey );

        ContentByCategoryQuery contentByCategoryQuery = new ContentByCategoryQuery();
        contentByCategoryQuery.setUser( user );
        contentByCategoryQuery.setCategoryKeyFilter( categories, includeSubCategories ? Integer.MAX_VALUE : 1 );
        contentByCategoryQuery.setOrderBy( orderBy );
        contentByCategoryQuery.setIndex( index );
        contentByCategoryQuery.setCount( count );
        contentByCategoryQuery.setFilterIncludeOfflineContent();
        contentByCategoryQuery.setFilterAdminBrowseOnly( false );

        ContentResultSet contents = contentService.queryContent( contentByCategoryQuery );

        RelatedContentQuery relatedContentQuery = new RelatedContentQuery( new Date() );
        relatedContentQuery.setUser( user );
        relatedContentQuery.setContentResultSet( contents );
        relatedContentQuery.setParentLevel( parentLevel );
        relatedContentQuery.setChildrenLevel( childrenLevel );
        relatedContentQuery.setParentChildrenLevel( parentChildrenLevel );
        relatedContentQuery.setIncludeOnlyMainVersions( true );

        RelatedContentResultSet relatedContents = contentService.queryRelatedContent( relatedContentQuery );

        ContentXMLCreator xmlCreator = new ContentXMLCreator();
        xmlCreator.setResultIndexing( index, count );
        xmlCreator.setIncludeOwnerAndModifierData( true );
        xmlCreator.setIncludeContentData( true );
        xmlCreator.setIncludeCategoryData( true );
        xmlCreator.setIncludeRelatedContentData( true );
        xmlCreator.setIncludeUserRightsInfo( true, new CategoryAccessResolver( groupDao ), new ContentAccessResolver( groupDao ) );
        xmlCreator.setIncludeVersionsInfoForAdmin( true );
        xmlCreator.setIncludeAssignment( true );
        xmlCreator.setIncludeDraftInfo( true );
        xmlCreator.setIncludeRepositoryPathInfo( false );
        return xmlCreator.createContentsDocument( user, contents, relatedContents );
    }

    public boolean isEnterpriseAdmin( User user )
    {
        return memberOfResolver.hasEnterpriseAdminPowers( user.getKey() );
    }

    public boolean isDeveloper( User user )
    {
        return memberOfResolver.hasDeveloperPowers( user.getKey() );
    }

    public XMLDocument getPath( User user, int type, int key )
    {
        Document doc = XMLTool.createDocument( "data" );

        if ( type == Types.CATEGORY )
        {
            // Get unit
            int unitKey = categoryHandler.getUnitKey( CategoryKey.parse( key ) );
            Document unitDoc = commonHandler.getSingleData( Types.UNIT, unitKey );
            Element unitElem = (Element) unitDoc.getDocumentElement().getFirstChild();

            // Get categories
            CategoryCriteria criteria = new CategoryCriteria();
            criteria.setCategoryKey( key );
            criteria.setUseDisableAttribute( false );
            categoryHandler.getMenu( user, unitElem, criteria );

            doc.getDocumentElement().appendChild( doc.importNode( unitElem, true ) );
        }
        
        return XMLDocumentFactory.create( doc );
    }

    public XMLDocument getContentHandler( int contentHandlerKey )
    {
        return XMLDocumentFactory.create( contentHandler.getContentHandler( contentHandlerKey ) );
    }

    public int createContentHandler( User user, String xmlData )
    {

        if ( !securityHandler.isEnterpriseAdmin( user ) )
        {
            String message = "User does not have access rights to create content handlers.";
            VerticalEngineLogger.errorSecurity(message, null );
        }

        Document doc = XMLTool.domparse( xmlData );
        return contentHandler.createContentHandler(doc );
    }

    public XMLDocument getData(int type, int[] keys)
    {
        Document doc = commonHandler.getData( type, keys );
        return XMLDocumentFactory.create(doc);
    }

    public int getBinaryDataKey( int contentKey, String label )
    {
        return binaryDataHandler.getBinaryDataKey( contentKey, label );
    }

    public boolean initializeDatabaseSchema()
        throws Exception
    {
        return this.systemHandler.initializeDatabaseSchema();
    }

    public boolean initializeDatabaseValues()
        throws Exception
    {
        return this.systemHandler.initializeDatabaseValues();
    }

    public void setBinaryDataHandler( BinaryDataHandler binaryDataHandler )
    {
        this.binaryDataHandler = binaryDataHandler;
    }

    public void setCategoryHandler( CategoryHandler categoryHandler )
    {
        this.categoryHandler = categoryHandler;
    }

    public void setCommonHandler( CommonHandler commonHandler )
    {
        this.commonHandler = commonHandler;
    }

    public void setContentHandler( ContentHandler contentHandler )
    {
        this.contentHandler = contentHandler;
    }

    public void setContentService( ContentService service )
    {
        contentService = service;
    }

    public void setContentObjectHandler( ContentObjectHandler contentObjectHandler )
    {
        this.contentObjectHandler = contentObjectHandler;
    }

    public void setGroupHandler( GroupHandler groupHandler )
    {
        this.groupHandler = groupHandler;
    }

    public void setLanguageHandler( LanguageHandler languageHandler )
    {
        this.languageHandler = languageHandler;
    }

    public void setLogHandler( LogHandler logHandler )
    {
        this.logHandler = logHandler;
    }

    public void setMenuHandler( MenuHandler menuHandler )
    {
        this.menuHandler = menuHandler;
    }

    public void setPageHandler( PageHandler pageHandler )
    {
        this.pageHandler = pageHandler;
    }

    public void setPageTemplateHandler( PageTemplateHandler pageTemplateHandler )
    {
        this.pageTemplateHandler = pageTemplateHandler;
    }

    public void setSectionHandler( SectionHandler sectionHandler )
    {
        this.sectionHandler = sectionHandler;
    }

    public void setUserHandler( UserHandler userHandler )
    {
        this.userHandler = userHandler;
    }

    public void setUnitHandler( UnitHandler unitHandler )
    {
        this.unitHandler = unitHandler;
    }

    public void setSystemHandler( SystemHandler systemHandler )
    {
        this.systemHandler = systemHandler;
    }

    public void setSecurityHandler( SecurityHandler securityHandler )
    {
        this.securityHandler = securityHandler;
    }

    public void setSecurityService( SecurityService service )
    {
        securityService = service;
    }
}

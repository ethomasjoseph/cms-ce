/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.handlers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.enonic.cms.store.dao.*;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.esl.sql.model.Table;
import com.enonic.vertical.VerticalProperties;
import com.enonic.vertical.engine.BaseEngine;
import com.enonic.vertical.engine.dbmodel.VerticalDatabase;

import com.enonic.cms.core.log.LogService;
import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.userstore.MemberOfResolver;
import com.enonic.cms.core.service.KeyService;

import com.enonic.cms.core.content.ContentService;

public abstract class BaseHandler
{
    protected final VerticalDatabase db = VerticalDatabase.getInstance();

    protected BaseEngine baseEngine;

    protected VerticalProperties verticalProperties;

    // Services:

    @Autowired
    protected LogService logService;

    @Autowired
    protected ContentService contentService;

    private KeyService keyService;

    protected SecurityService securityService;

    // Daos:

    @Autowired
    protected BinaryDataDao binaryDataDao;

    @Autowired
    protected ContentBinaryDataDao contentBinaryDataDao;

    @Autowired
    protected ContentDao contentDao;

    @Autowired
    protected PortletDao portletDao;

    @Autowired
    protected CategoryDao categoryDao;

    @Autowired
    protected GroupDao groupDao;

    @Autowired
    protected LanguageDao languageDao;

    @Autowired
    protected MenuItemDao menuItemDao;

    @Autowired
    protected PageDao pageDao;

    @Autowired
    protected PageTemplateDao pageTemplateDao;

    @Autowired
    protected ResourceDao resourceDao;

    @Autowired
    protected SiteDao siteDao;

    @Autowired
    protected UserDao userDao;

    @Autowired
    protected UnitDao unitDao;

    @Autowired
    protected ContentTypeDao contentTypeDao;

    @Autowired
    protected UserStoreDao userStoreDao;

    @Autowired
    protected MemberOfResolver memberOfResolver;

    @Autowired
    protected SessionFactory sessionFactory;

    public BaseHandler()
    {

    }

    public void init()
    {
    }

    public void setVerticalProperties( VerticalProperties value )
    {
        this.verticalProperties = value;
    }

    public void setBaseEngine( BaseEngine value )
    {
        this.baseEngine = value;
    }

    public void setKeyService( KeyService value )
    {
        this.keyService = value;
    }

    public void setSecurityService( SecurityService service )
    {
        securityService = service;
    }

    protected final CategoryHandler getCategoryHandler()
    {
        return baseEngine.getCategoryHandler();
    }

    protected final ContentHandler getContentHandler()
    {
        return baseEngine.getContentHandler();
    }

    protected final CommonHandler getCommonHandler()
    {
        return baseEngine.getCommonHandler();
    }

    protected final GroupHandler getGroupHandler()
    {
        return baseEngine.getGroupHandler();
    }

    protected final LanguageHandler getLanguageHandler()
    {
        return baseEngine.getLanguageHandler();
    }

    protected final MenuHandler getMenuHandler()
    {
        return baseEngine.getMenuHandler();
    }

    protected final PageHandler getPageHandler()
    {
        return baseEngine.getPageHandler();
    }

    protected final PageTemplateHandler getPageTemplateHandler()
    {
        return baseEngine.getPageTemplateHandler();
    }

    protected final SectionHandler getSectionHandler()
    {
        return baseEngine.getSectionHandler();
    }

    protected final SecurityHandler getSecurityHandler()
    {
        return baseEngine.getSecurityHandler();
    }

    protected final UserHandler getUserHandler()
    {
        return baseEngine.getUserHandler();
    }

    protected final void close( ResultSet resultSet )
    {

        baseEngine.close( resultSet );
    }

    protected final void close( Statement stmt )
    {

        baseEngine.close( stmt );
    }

    protected final Connection getConnection()
        throws SQLException
    {
        return baseEngine.getConnection();
    }

    public final int getNextKey( String tableName )
    {
        return keyService.generateNextKeySafe( tableName );
    }

    public final int getNextKey( Table table )
    {
        return keyService.generateNextKeySafe( table.getName() );
    }
}

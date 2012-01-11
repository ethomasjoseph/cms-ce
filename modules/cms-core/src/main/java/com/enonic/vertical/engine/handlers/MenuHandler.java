/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.handlers;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.enonic.esl.sql.model.Column;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.VerticalLogger;
import com.enonic.vertical.engine.VerticalCreateException;
import com.enonic.vertical.engine.VerticalEngineLogger;
import com.enonic.vertical.engine.VerticalSecurityException;
import com.enonic.vertical.engine.XDG;
import com.enonic.vertical.engine.handlers.model.MenuItemModel;
import com.enonic.vertical.event.MenuHandlerEvent;
import com.enonic.vertical.event.MenuHandlerListener;
import com.enonic.vertical.event.VerticalEventMulticaster;

import com.enonic.cms.framework.util.TIntArrayList;

import com.enonic.cms.core.CalendarUtil;
import com.enonic.cms.core.SiteKey;
import com.enonic.cms.core.portal.PrettyPathNameCreator;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.user.UserSpecification;
import com.enonic.cms.core.structure.RunAsType;
import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemKey;
import com.enonic.cms.core.structure.menuitem.MenuItemType;
import com.enonic.cms.core.structure.page.PageEntity;
import com.enonic.cms.core.structure.page.template.PageTemplateEntity;
import com.enonic.cms.core.structure.page.template.PageTemplateKey;
import com.enonic.cms.core.structure.page.template.PageTemplateType;

public final class MenuHandler
    extends BaseHandler
{
    private final VerticalEventMulticaster multicaster = new VerticalEventMulticaster();

    private static final String ELEMENT_NAME_MENU_NAME = "menu-name";

    private static final String ELEMENT_NAME_DISPLAY_NAME = "displayname";

    private static final String COLUMN_NAME_DISPLAY_NAME = "mei_sDisplayName";

    private static final String COLUMN_NAME_ALTERNATIVE_NAME = "mei_sSubTitle";

    private static final String ELEMENT_NAME_MENUITEM_NAME = "name";

    public synchronized void addListener( MenuHandlerListener mhl )
    {
        multicaster.add( mhl );
    }

    final static private String MENU_ITEM_TABLE = "tMenuItem";

    final static private String MENU_ITEM_COLS =
        "mei_lKey, mei_bHidden, mei_mid_lKey, mei_usr_hOwner, mei_usr_hModifier, mei_lOrder, mei_men_lkey, mei_lParent, mei_sName, mei_sDescription, " +
            "mei_sKeywords, " + COLUMN_NAME_ALTERNATIVE_NAME +
            ", mei_xmlData, mei_pag_lKey, mei_sURL, mei_burlopennewwin, lan_lKey, lan_sCode, " +
            "lan_sDescription, mei_dteTimestamp, mei_lRunAs, " + COLUMN_NAME_DISPLAY_NAME;

    final static private String MENU_SELECT_ITEM_KEYS_BY_PARENT = "SELECT mei_lKey FROM tMenuItem WHERE mei_lParent = ? ";

    final static private String MENU_ITEM_INSERT =
        "INSERT INTO " + MENU_ITEM_TABLE + " (mei_lKey, mei_sName, mei_men_lKey, mei_mid_lKey, " +
            " mei_lParent, mei_lOrder, mei_dteTimestamp," + " " + COLUMN_NAME_ALTERNATIVE_NAME +
            ", mei_bHidden, mei_sDescription, mei_usr_hOwner," +
            " mei_usr_hModifier, mei_xmlData, mei_sKeywords, mei_lan_lKey, mei_bSection, mei_lRunAs, " + COLUMN_NAME_DISPLAY_NAME + ") " +
            " VALUES (?, ?, ?, ?, ?, ?, " + "@currentTimestamp@" + ", ?, ?, ?, ?, ?, " + "?" + ", ?, ?, 0, ?, ?)";

    final static private String MENU_ITEM_SELECT_BY_KEY =
        "SELECT " + MENU_ITEM_COLS + " FROM " + MENU_ITEM_TABLE + " LEFT JOIN tMenu ON tMenu.men_lKey = " + MENU_ITEM_TABLE +
            ".mei_men_lKey " + " LEFT JOIN tLanguage ON " + MENU_ITEM_TABLE + ".mei_lan_lKey = tLanguage.lan_lKey" + " WHERE mei_lKey = ?";

    final static private String MENU_ITEM_URL_UPDATE =
        "UPDATE " + MENU_ITEM_TABLE + " SET mei_sURL = ?, mei_burlopennewwin = ? WHERE mei_lKey = ?";

    final static private String MENU_ITEM_PAGE_UPDATE_KEY =
        "UPDATE " + MENU_ITEM_TABLE + " SET mei_pag_lKey = ?, mei_mid_lKey = ? WHERE mei_lKey = ?";

    static private Hashtable<String, Integer> menuItemTypes;

    private void buildDocumentTypeXML( Element menuitemElem, Element documentElem )
    {

        if ( documentElem != null )
        {
            if ( verticalProperties.isStoreXHTMLOn() )
            {
                Node n = documentElem.getFirstChild();
                if ( n != null && n.getNodeType() == Node.CDATA_SECTION_NODE )
                {
                    int menuItemKey = Integer.parseInt( menuitemElem.getAttribute( "key" ) );
                    String menuItemName = XMLTool.getElementText( XMLTool.getElement( menuitemElem, "name" ) );
                    Document doc = menuitemElem.getOwnerDocument();
                    String docString = XMLTool.getElementText( documentElem );
                    documentElem.removeChild( n );
                    XMLTool.createXHTMLNodes( doc, documentElem, docString, true );
                    String menuKey = menuitemElem.getAttribute( "menukey" );
                    VerticalLogger.error( "Received invalid XML from database, menukey=" + menuKey + ", menuitem key=" + menuItemKey +
                                              ", name=" + menuItemName + ". Running Tidy.." );
                }
                documentElem.setAttribute( "mode", "xhtml" );
            }
            else
            {
                Node n = documentElem.getFirstChild();
                if ( n == null )
                {
                    Document doc = menuitemElem.getOwnerDocument();
                    XMLTool.createCDATASection( doc, menuitemElem, "Scratch document." );
                }
                else if ( n.getNodeType() != Node.CDATA_SECTION_NODE )
                {
                    Document doc = menuitemElem.getOwnerDocument();
                    String docString = XMLTool.serialize( documentElem );
                    XMLTool.createCDATASection( doc, documentElem, docString );
                    VerticalEngineLogger.debug("Expected CDATA, found XML. Serialized it." );
                }
            }
        }
    }

    private boolean hasChild( int parentKey, int key, boolean recursive )
        throws SQLException
    {
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;
        TIntArrayList keyArray = new TIntArrayList();
        Connection con = null;

        try
        {
            con = getConnection();
            preparedStmt = con.prepareStatement( MENU_SELECT_ITEM_KEYS_BY_PARENT );
            preparedStmt.setInt( 1, parentKey );
            resultSet = preparedStmt.executeQuery();

            while ( resultSet.next() )
            {
                int currentKey = resultSet.getInt( 1 );
                if ( currentKey == key )
                {
                    return true;
                }
                else
                {
                    keyArray.add( currentKey );
                }
            }

            int arraySize = keyArray.size();
            if ( arraySize == 0 )
            {
                return false;
            }

            if ( recursive )
            {
                for ( int i = 0; i < arraySize; ++i )
                {
                    if ( hasChild( keyArray.get( i ), key, recursive ) )
                    {
                        return true;
                    }
                }
            }
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
        }

        return false;
    }

    private void tagParents( Element menuItemsElement )
    {
        Node tmpNode = menuItemsElement.getParentNode();
        while ( tmpNode != null && tmpNode.getNodeType() == Node.ELEMENT_NODE && !( (Element) tmpNode ).getTagName().equals( "menu" ) )
        {
            ( (Element) tmpNode ).setAttribute( "path", "true" );
            tmpNode = tmpNode.getParentNode().getParentNode();
        }
    }

    private Element buildMenuItemXML( Document doc, Element menuItemsElement, ResultSet result, int tagItem, boolean complete,
                                      boolean includePageConfig, boolean includeHidden, boolean includeTypeSpecificXML, boolean tagItems,
                                      int levels )
        throws SQLException
    {

        int key = result.getInt( "mei_lKey" );

        // check if menuitem is hidden:
        int hiddenInt = result.getInt( "mei_bHidden" );
        boolean hidden = result.wasNull() || hiddenInt == 1;

        // propagate upwards in the XML and tag parents:
        if ( key == tagItem )
        {
            tagParents( menuItemsElement );
        }

        // simply return null if we don't want to include
        // hidden menuitems:
        if ( ( !includeHidden && hidden ) || levels == 0 )
        {
            // special case: if includeHidden is false, we must
            // check to see if the menuitem that is to be tagged
            // is a child of this menuitem
            if ( tagItems )
            {
                if ( tagItem != -1 && tagItem != key )
                {
                    if ( hasChild( key, tagItem, true ) )
                    {
                        tagParents( menuItemsElement );

                        if ( hidden )
                        {
                            Node n = menuItemsElement.getParentNode();
                            if ( n.getNodeType() == Node.ELEMENT_NODE )
                            {
                                ( (Element) n ).setAttribute( "active", "true" );
                            }
                        }
                    }
                }
                else if ( tagItem == key )
                {
                    Node n = menuItemsElement.getParentNode();
                    if ( n.getNodeType() == Node.ELEMENT_NODE )
                    {
                        ( (Element) n ).setAttribute( "active", "true" );
                    }
                }
            }
            return null;
        }

        ////// + build xml for menu item
        Element menuItemElement = XMLTool.createElement( doc, menuItemsElement, "menuitem" );
        menuItemElement.setAttribute( "key", String.valueOf( key ) );

        // tag the menuitem?
        if ( tagItem == key && !hidden )
        {
            menuItemElement.setAttribute( "path", "true" );
            menuItemElement.setAttribute( "active", "true" );
        }

        // attribute: owner
        menuItemElement.setAttribute( "owner", result.getString( "mei_usr_hOwner" ) );

        // attribute: modifier
        menuItemElement.setAttribute( "modifier", result.getString( "mei_usr_hModifier" ) );

        // attribute: order
        menuItemElement.setAttribute( "order", result.getString( "mei_lOrder" ) );

        // Add timestamp attribute
        menuItemElement.setAttribute( "timestamp", CalendarUtil.formatTimestamp(
                result.getTimestamp( "mei_dteTimestamp" ) ) );

        // attribute: language
        int lanKey = result.getInt( "lan_lKey" );
        String lanCode = result.getString( "lan_sCode" );
        String lanDesc = result.getString( "lan_sDescription" );
        if ( lanDesc != null )
        {
            menuItemElement.setAttribute( "languagekey", String.valueOf( lanKey ) );
            menuItemElement.setAttribute( "languagecode", lanCode );
            menuItemElement.setAttribute( "language", lanDesc );
        }

        // attribute menykey:
        int menuKey = result.getInt( "mei_men_lkey" );
        if ( !result.wasNull() )
        {
            menuItemElement.setAttribute( "menukey", String.valueOf( menuKey ) );
        }

        // attribute parent:
        int parentKey = result.getInt( "mei_lParent" );
        if ( !result.wasNull() )
        {
            menuItemElement.setAttribute( "parent", String.valueOf( parentKey ) );
        }

        // element: name
        XMLTool.createElement( doc, menuItemElement, "name", result.getString( "mei_sName" ) );

        // display-name
        XMLTool.createElement( doc, menuItemElement, ELEMENT_NAME_DISPLAY_NAME, result.getString( COLUMN_NAME_DISPLAY_NAME ) );

        // short-name:
        String tmp = result.getString( COLUMN_NAME_ALTERNATIVE_NAME );
        if ( !result.wasNull() && tmp.length() > 0 )
        {
            XMLTool.createElement( doc, menuItemElement, ELEMENT_NAME_MENU_NAME, tmp );
        }

        menuItemElement.setAttribute( "runAs", RunAsType.get( result.getInt( "mei_lRunAs" ) ).toString() );

        // description:
        String desc = result.getString( "mei_sDescription" );
        if ( !result.wasNull() )
        {
            XMLTool.createElement( doc, menuItemElement, "description", desc );
        }
        else
        {
            XMLTool.createElement( doc, menuItemElement, "description" );
        }

        // keywords:
        String keywords = result.getString( "mei_sKeywords" );
        if ( !result.wasNull() )
        {
            XMLTool.createElement( doc, menuItemElement, "keywords", keywords );
        }
        else
        {
            XMLTool.createElement( doc, menuItemElement, "keywords" );
        }

        // visibility:
        if ( !hidden )
        {
            menuItemElement.setAttribute( "visible", "yes" );
        }
        else
        {
            menuItemElement.setAttribute( "visible", "no" );
        }

        // contentkey
        int contentKey = getMenuItemContentKey( key );
        if ( contentKey != -1 )
        {
            menuItemElement.setAttribute( "contentkey", String.valueOf( contentKey ) );
        }

        // element menuitemdata:
        InputStream is = result.getBinaryStream( "mei_xmlData" );
        Element documentElem;
        if ( result.wasNull() )
        {
            XMLTool.createElement( doc, menuItemElement, "parameters" );
            documentElem = XMLTool.createElement( doc, "document" );
            if ( complete )
            {
                XMLTool.createElement( doc, menuItemElement, "data" );
            }
        }
        else
        {
            Document dataDoc = XMLTool.domparse( is );
            Element dataElem = (Element) doc.importNode( dataDoc.getDocumentElement(), true );
            Element parametersElem = XMLTool.getElement( dataElem, "parameters" );
            dataElem.removeChild( parametersElem );
            menuItemElement.appendChild( parametersElem );
            if ( complete )
            {
                documentElem = XMLTool.getElement( dataElem, "document" );
                if ( documentElem != null )
                {
                    dataElem.removeChild( documentElem );
                    menuItemElement.appendChild( documentElem );
                }
                menuItemElement.appendChild( dataElem );
            }
            else
            {
                documentElem = XMLTool.createElement( doc, "document" );
            }
        }

        // attribute: menu item type
        MenuItemType menuItemType = MenuItemType.get( result.getInt( "mei_mid_lKey" ) );
        menuItemElement.setAttribute( "type", menuItemType.getName() );

        if ( includeTypeSpecificXML )
        {
            // build type-specific XML:
            switch ( menuItemType )
            {
                case PAGE:
                    buildPageTypeXML( result, doc, menuItemElement, complete && includePageConfig );
                    break;

                case URL:
                    buildURLTypeXML( result, doc, menuItemElement );
                    break;

                case CONTENT:
                    MenuItemKey sectionKey = getSectionHandler().getSectionKeyByMenuItem( new MenuItemKey( key ) );
                    if ( sectionKey != null )
                    {
                        buildSectionTypeXML( key, menuItemElement );
                    }
                    buildDocumentTypeXML( menuItemElement, documentElem );
                    buildPageTypeXML( result, doc, menuItemElement, complete && includePageConfig );
                    break;

                case LABEL:
                    break;
                case SECTION:
                    buildSectionTypeXML( key, menuItemElement );
                    break;
                case SHORTCUT:
                    buildShortcutTypeXML( key, menuItemElement );
                    break;
            }
        }

        return menuItemElement;
    }

    private void buildPageTypeXML( ResultSet result, Document doc, Element menuItemElement, boolean includePageConfig )
        throws SQLException
    {

        int pKey = result.getInt( "mei_pag_lKey" );

        if ( includePageConfig )
        {
            Document pageDoc = getPageHandler().getPage( pKey, includePageConfig ).getAsDOMDocument();
            Element rootElem = pageDoc.getDocumentElement();
            Element pageElem = XMLTool.getElement( rootElem, "page" );
            menuItemElement.appendChild( menuItemElement.getOwnerDocument().importNode( pageElem, true ) );
        }
        else
        {
            final PageEntity page = pageDao.findByKey( pKey );
            final PageTemplateEntity pageTemplate = page.getTemplate();
            Element pageElem = XMLTool.createElement( doc, menuItemElement, "page" );
            pageElem.setAttribute( "key", String.valueOf( pKey ) );
            pageElem.setAttribute( "pagetemplatekey", String.valueOf( pageTemplate.getKey() ) );
            pageElem.setAttribute( "pagetemplatetype", String.valueOf( pageTemplate.getType().getKey() ) );
        }
    }

    private void buildSectionTypeXML( int menuItemKey, Element menuItemElement )
    {
        Document sectionDoc = getSectionHandler().getSectionByMenuItem( menuItemKey );
        XMLTool.mergeDocuments( menuItemElement, sectionDoc, true );
    }

    private void buildShortcutTypeXML( int menuItemKey, Element menuItemElement )
    {
        StringBuffer sql = XDG.generateSelectSQL( db.tMenuItem, db.tMenuItem.mei_lKey );
        Object[] columnValues = getCommonHandler().getObjects( sql.toString(), menuItemKey );
        if ( columnValues.length > 0 )
        {
            Element shortcutElem = XMLTool.createElement( menuItemElement.getOwnerDocument(), menuItemElement, "shortcut" );
            Integer shortcut = (Integer) columnValues[19];
            final String shortcutKey = ( shortcut == null ) ? "" : String.valueOf( shortcut );
            shortcutElem.setAttribute( "key", shortcutKey );
            final String menuItemName = ( shortcut == null ) ? "" : getMenuItemName( shortcut );
            shortcutElem.setAttribute( "name", menuItemName );
            Integer forward = (Integer) columnValues[20];
            if ( forward == 0 )
            {
                shortcutElem.setAttribute( "forward", "false" );
            }
            else
            {
                shortcutElem.setAttribute( "forward", "true" );
            }

        }
    }

    private void buildURLTypeXML( ResultSet result, Document doc, Element menuItemElement )
    {

        try
        {
            String url = result.getString( "mei_sURL" );
            Element urlElement = XMLTool.createElement( doc, menuItemElement, "url", url );

            // attribute: newWindow
            int newWindow = result.getInt( "mei_burlopennewwin" );
            String attrValue;
            if ( newWindow == 0 )
            {
                attrValue = "no";
            }
            else
            {
                attrValue = "yes";
            }
            urlElement.setAttribute( "newwindow", attrValue );

        }
        catch ( SQLException sqle )
        {
            System.err.println( "[MenuHandler_DB2Impl:Error] SQL exception." );
        }
    }

    public int createMenuItem( User user, String xmlData )
        throws VerticalCreateException, VerticalSecurityException
    {

        Document doc = XMLTool.domparse( xmlData );
        Element menuItemsElement = doc.getDocumentElement();
        Element menuItemElement = XMLTool.getElement( menuItemsElement, "menuitem" );

        // get parent key
        MenuItemKey parentKey = null;
        String tmp = menuItemElement.getAttribute( "parent" );
        if ( tmp != null && tmp.length() > 0 )
        {
            parentKey = new MenuItemKey( tmp );
        }

        SiteKey siteKey = null;
        tmp = menuItemElement.getAttribute( "menukey" );
        if ( tmp != null && tmp.length() > 0 )
        {
            siteKey = new SiteKey( tmp );
        }

        int order;
        tmp = menuItemElement.getAttribute( "order" );
        if ( tmp != null && tmp.length() > 0 )
        {
            order = Integer.parseInt( tmp );
        }
        else
        {
            order = getNextOrder( siteKey == null ? -1 : siteKey.toInt(), parentKey == null ? -1 : parentKey.toInt() );
        }

        return createMenuItem( user, null, menuItemElement, siteKey, order, parentKey, false, new HashMap<Integer, MenuItemModel>() );
    }

    private int getNextOrder( int menuKey, int parentKey )
    {
        StringBuffer sql = XDG.generateSelectSQL( db.tMenuItem, db.tMenuItem.mei_lOrder, false, db.tMenuItem.mei_men_lKey );
        if ( parentKey != -1 )
        {
            XDG.appendWhereSQL( sql, db.tMenuItem.mei_lParent, XDG.OPERATOR_EQUAL, parentKey );
        }

        XDG.appendOrderBySQL( sql, db.tMenuItem.mei_lOrder, false );

        int highestOrder = getCommonHandler().getInt( sql.toString(), menuKey );

        if ( highestOrder == -1 )
        {
            return 0;
        }
        else if ( highestOrder < Integer.MAX_VALUE )
        {
            return highestOrder + 1;
        }
        else
        {
            return Integer.MAX_VALUE;
        }
    }

    private int createMenuItem( User user, CopyContext copyContext, Element menuItemElement, SiteKey siteKey, int order,
                                MenuItemKey parentKey, boolean useOldKey, Map<Integer, MenuItemModel> menuItems )
        throws VerticalCreateException, VerticalSecurityException
    {

        // security check:
        if ( !getSecurityHandler().validateMenuItemCreate( user, siteKey.toInt(), parentKey == null ? -1 : parentKey.toInt() ) )
        {
            VerticalEngineLogger.errorSecurity( "Not allowed to create menuitem in this position.", null );
        }

        String menuItemName = XMLTool.getElementText( XMLTool.getElement( menuItemElement, ELEMENT_NAME_MENUITEM_NAME ) );

        if ( StringUtils.isEmpty( menuItemName ) )
        {
            menuItemName = generateMenuItemName( menuItemElement );
        }

        menuItemName = ensureUniqueMenuItemName( siteKey, parentKey, menuItemName, null );

        // check whether name is unique for this parent
        if ( menuItemNameExists( siteKey, parentKey, menuItemName, null ) )
        {
            VerticalEngineLogger.errorCreate("Menu item name already exists on this level: {0}",
                                              new Object[]{menuItemName}, null );
        }

        Element tmp_element;
        Hashtable<String, Integer> menuItemTypes = getMenuItemTypesAsHashtable();

        // Get menuitem type:
        String miType = menuItemElement.getAttribute( "type" );
        Integer type = menuItemTypes.get( miType );
        if ( type == null )
        {
            VerticalEngineLogger.errorCreate("Invalid menu item type {0}.", new Object[]{type}, null );
        }

        Connection con = null;
        PreparedStatement preparedStmt = null;
        MenuItemKey menuItemKey = null;

        try
        {
            con = getConnection();

            // key
            String keyStr = menuItemElement.getAttribute( "key" );
            if ( !useOldKey || keyStr == null || keyStr.length() == 0 )
            {
                menuItemKey = new MenuItemKey( getNextKey( MENU_ITEM_TABLE ) );
            }
            else
            {
                menuItemKey = new MenuItemKey( keyStr );
            }
            if ( copyContext != null )
            {
                copyContext.putMenuItemKey( Integer.parseInt( keyStr ), menuItemKey.toInt() );
            }

            String tmp;

            preparedStmt = con.prepareStatement( MENU_ITEM_INSERT );

            preparedStmt.setInt( 1, menuItemKey.toInt() );

            // element: name
            validateMenuItemName( menuItemName );
            preparedStmt.setString( 2, menuItemName );

            // menu key:
            preparedStmt.setInt( 3, siteKey.toInt() );

            // attribute: menu item type
            preparedStmt.setInt( 4, type );

            // parent
            if ( parentKey == null )
            {
                preparedStmt.setNull( 5, Types.INTEGER );
            }
            else
            {
                preparedStmt.setInt( 5, parentKey.toInt() );
            }

            // order:
            preparedStmt.setInt( 6, order );

            // pre-fetch data element
            Element dataElem = XMLTool.getElement( menuItemElement, "data" );

            // element: parameters
            tmp_element = XMLTool.getElement( menuItemElement, "parameters" );
            if ( tmp_element != null )
            {
                dataElem.appendChild( tmp_element );
            }

            // alternative name:
            tmp_element = XMLTool.getElement( menuItemElement, ELEMENT_NAME_MENU_NAME );
            if ( tmp_element != null )
            {
                tmp = XMLTool.getElementText( tmp_element );
                preparedStmt.setString( 7, tmp );
            }
            else
            {
                preparedStmt.setNull( 7, Types.VARCHAR );
            }

            // visibility:
            tmp = menuItemElement.getAttribute( "visible" );
            if ( "no".equals( tmp ) )
            {
                preparedStmt.setInt( 8, 1 );
            }
            else
            {
                preparedStmt.setInt( 8, 0 );
            }

            // description:
            tmp_element = XMLTool.getElement( menuItemElement, "description" );
            String data = XMLTool.getElementText( tmp_element );
            if ( data != null )
            {
                preparedStmt.setString( 9, data );
            }
            else
            {
                preparedStmt.setNull( 9, Types.VARCHAR );
            }

            if ( type == 4 )
            {
                Element docElem = XMLTool.getElement( menuItemElement, "document" );

                if ( docElem != null )
                {
                    dataElem.appendChild( docElem );
                }
            }

            // attribute: owner/modifier
            String ownerKey = menuItemElement.getAttribute( "owner" );
            preparedStmt.setString( 10, ownerKey );
            preparedStmt.setString( 11, ownerKey );

            // data
            if ( dataElem != null )
            {
                Document dataDoc = XMLTool.createDocument();
                dataDoc.appendChild( dataDoc.importNode( dataElem, true ) );

                byte[] bytes = XMLTool.documentToBytes( dataDoc, "UTF-8" );
                preparedStmt.setBytes( 12, bytes );
            }
            else
            {
                preparedStmt.setNull( 12, Types.BLOB );
            }

            // keywords
            tmp_element = XMLTool.getElement( menuItemElement, "keywords" );
            String keywords = XMLTool.getElementText( tmp_element );
            if ( keywords == null || keywords.length() == 0 )
            {
                preparedStmt.setNull( 13, Types.VARCHAR );
            }
            else
            {
                preparedStmt.setString( 13, keywords );
            }

            // language
            String lanKey = menuItemElement.getAttribute( "languagekey" );
            if ( ( lanKey != null ) && ( lanKey.length() > 0 ) )
            {
                preparedStmt.setInt( 14, Integer.parseInt( lanKey ) );
            }
            else
            {
                preparedStmt.setNull( 14, Types.INTEGER );
            }

            RunAsType runAs = RunAsType.INHERIT;
            String runAsStr = menuItemElement.getAttribute( "runAs" );
            if ( StringUtils.isNotEmpty( runAsStr ) )
            {
                runAs = RunAsType.valueOf( runAsStr );
            }
            preparedStmt.setInt( 15, runAs.getKey() );

            // Display-name
            String displayName = getElementValue( menuItemElement, ELEMENT_NAME_DISPLAY_NAME );
            preparedStmt.setString( 16, displayName );

            // execute statement:
            preparedStmt.executeUpdate();

            boolean menuItemAlreadyExists = StringUtils.isNotEmpty( keyStr );
            if ( menuItemAlreadyExists )
            {
                menuItems.put( Integer.valueOf( keyStr ),  new MenuItemModel( menuItemKey.toInt(), type, getShortcutKey( menuItemElement )) );
            }

            // Create type specific data.
            switch ( type )
            {
                case 1:
                    // page
                    createPage( con, menuItemElement, type, menuItemKey );
                    break;

                case 2:
                    // URL
                    createOrUpdateURL( con, menuItemElement, menuItemKey );
                    break;

                case 4:
                    // document: nothing
                    // page
                    Element pageElem = XMLTool.getElement( menuItemElement, "page" );
                    PageTemplateKey pageTemplateKey = new PageTemplateKey( pageElem.getAttribute( "pagetemplatekey" ) );
                    PageTemplateEntity pageTemplate = pageTemplateDao.findByKey( pageTemplateKey.toInt() );
                    PageTemplateType pageTemplateType = pageTemplate.getType();
                    if ( pageTemplateType == PageTemplateType.SECTIONPAGE || pageTemplateType == PageTemplateType.NEWSLETTER )
                    {
                        createSection( menuItemElement, menuItemKey );
                    }
                    createPage( con, menuItemElement, type, menuItemKey );
                    break;

                case 5:
                    // label
                    break;

                case 6:
                    // section
                    createSection( menuItemElement, menuItemKey );
                    break;

                case 7:
                    // shortcut
                    createOrOverrideShortcut( menuItemElement, menuItemKey );
                    break;

                default:
                    VerticalEngineLogger.errorCreate("Unknown menuitem type: {0}", new Object[]{type}, null );
            }

            // set contentkey if present
            String contentKeyStr = menuItemElement.getAttribute( "contentkey" );
            if ( contentKeyStr.length() == 0 )
            {
                contentKeyStr = "-1";
            }
            setMenuItemContentKey( menuItemKey, Integer.parseInt( contentKeyStr ) );

            // fire event
            if ( multicaster.hasListeners() && copyContext == null )
            {
                MenuHandlerEvent e = new MenuHandlerEvent( user, siteKey.toInt(), menuItemKey.toInt(), menuItemName, this );
                multicaster.createdMenuItem( e );
            }

            UserSpecification userSpecification = new UserSpecification();
            userSpecification.setDeletedState( UserSpecification.DeletedState.ANY );
            userSpecification.setKey( new UserKey( ownerKey ) );
            UserEntity owner = userDao.findSingleBySpecification( userSpecification );
            String ownerGroupKey = null;
            if ( owner.getUserGroup() != null )
            {
                ownerGroupKey = owner.getUserGroup().getGroupKey().toString();
            }

            getSecurityHandler().inheritMenuItemAccessRights( siteKey.toInt(), parentKey == null ? -1 : parentKey.toInt(),
                                                              menuItemKey.toInt(), ownerGroupKey );

            // Create other
            Element menuItemsElement = XMLTool.getElement( menuItemElement, "menuitems" );
            if ( menuItemsElement != null )
            {
                Element[] elems = XMLTool.getElements( menuItemsElement );
                for ( int i = 0; i < elems.length; i++ )
                {
                    createMenuItem( user, copyContext, elems[i], siteKey, i, menuItemKey, useOldKey, menuItems );
                }
            }
        }
        catch ( SQLException e )
        {
            VerticalEngineLogger.errorCreate("A database error occurred: %t", e );
        }
        finally
        {
            close( preparedStmt );
        }

        return menuItemKey.toInt();
    }

    private String generateMenuItemName( Element menuItemElement )
    {
        String menuItemName;

        String suggestedName = getElementValue( menuItemElement, ELEMENT_NAME_MENU_NAME );

        if ( StringUtils.isBlank( suggestedName ) )
        {
            suggestedName = getElementValue( menuItemElement, ELEMENT_NAME_DISPLAY_NAME );
        }

        menuItemName = PrettyPathNameCreator.generatePrettyPathName( suggestedName );

        return menuItemName;
    }

    private String ensureUniqueMenuItemName( SiteKey siteKey, MenuItemKey parentKey, String menuItemName, MenuItemKey existingKey )
    {
        int i = 0;

        String baseName = menuItemName;

        while ( true )
        {
            if ( !menuItemNameExists( siteKey, parentKey, menuItemName, existingKey ) )
            {
                return menuItemName;
            }
            else
            {
                i++;
                menuItemName = baseName + "(" + i + ")";
            }

            Assert.isTrue( i < 100, "Not able to generate menuitem-name within 100 attempts to create unique" );
        }
    }


    private String getElementValue( Element menuItemElement, String elementName )
    {
        Element tmp_element;
        String tmp;
        tmp_element = XMLTool.getElement( menuItemElement, elementName );
        tmp = XMLTool.getElementText( tmp_element );
        return tmp;
    }

    private void createOrUpdateURL( Connection con, Element elem, MenuItemKey menuItemKey )
        throws VerticalCreateException
    {

        Element urlElement = XMLTool.getElement( elem, "url" );
        String tmp = urlElement.getAttribute( "newwindow" );
        int bNewWindow = -1;
        if ( "yes".equals( tmp ) )
        {
            bNewWindow = 1;
        }
        else if ( "no".equals( tmp ) )
        {
            bNewWindow = 0;
        }
        else
        {
            String msg = "Please specify 'yes' or 'no' in 'newwindow' attribute.";
            VerticalEngineLogger.errorCreate(msg, null );
        }

        String url = XMLTool.getElementText( urlElement );

        PreparedStatement preparedStmt = null;
        try
        {
            preparedStmt = con.prepareStatement( MENU_ITEM_URL_UPDATE );

            preparedStmt.setString( 1, url );
            preparedStmt.setInt( 2, bNewWindow );
            preparedStmt.setInt( 3, menuItemKey.toInt() );

            preparedStmt.executeUpdate();
        }
        catch ( SQLException e )
        {
            VerticalEngineLogger.errorCreate("A database error occurred: %t", e );
        }
        finally
        {
            close( preparedStmt );
        }
    }

    public void setMenuItemContentTypes( int menuItemKey, int[] contentTypeKeys )
    {
        try
        {
            MenuItemKey sectionKey = getSectionHandler().getSectionKeyByMenuItem( new MenuItemKey( menuItemKey ) );
            if ( sectionKey != null )
            {
                getSectionHandler().setContentTypesForSection( sectionKey.toInt(), contentTypeKeys );
            }
        }
        catch ( VerticalCreateException vce )
        {
            String message = "Failed to create section contenttype filter: %t";
            VerticalEngineLogger.error(message, vce );
        }
    }

    private void createSection( Element menuItemElem, MenuItemKey menuItemKey )
        throws VerticalCreateException, VerticalSecurityException
    {
        Element sectionElem = XMLTool.getElement( menuItemElem, "section" );
        boolean ordered = "true".equals( sectionElem.getAttribute( "ordered" ) );

        Element[] contentTypeElems = XMLTool.selectElements( sectionElem, "contenttypes/contenttype" );
        int[] contentTypes = new int[contentTypeElems.length];
        for ( int i = 0; i < contentTypeElems.length; i++ )
        {
            contentTypes[i] = Integer.parseInt( contentTypeElems[i].getAttribute( "key" ) );
        }

        getSectionHandler().createSection( menuItemKey.toInt(), ordered, contentTypes );
    }

    private Integer getShortcutKey( Element menuItemElement )
    {
        Element shortcutElem = XMLTool.getElement( menuItemElement, "shortcut" );

        return shortcutElem != null
                ? Integer.valueOf( shortcutElem.getAttribute( "key" ) )
                : null;
    }

    private void createOrOverrideShortcut( Element shortcutDestinationMenuItem, MenuItemKey shortcutMenuItem )
            throws VerticalCreateException
    {
        Element shortcutElem = XMLTool.getElement( shortcutDestinationMenuItem, "shortcut" );
        int shortcut = Integer.parseInt( shortcutElem.getAttribute( "key" ) );
        boolean forward = Boolean.valueOf( shortcutElem.getAttribute( "forward" ) );
        StringBuffer sql = XDG.generateUpdateSQL( db.tMenuItem, new Column[]{db.tMenuItem.mei_mei_lShortcut,
                db.tMenuItem.mei_bShortcutForward}, new Column[]{db.tMenuItem.mei_lKey} );

        Connection con = null;
        PreparedStatement prepStmt = null;

        try

        {
            con = getConnection();
            prepStmt = con.prepareStatement( sql.toString() );

            prepStmt.setInt( 1, shortcut );
            prepStmt.setBoolean( 2, forward );
            prepStmt.setInt( 3, shortcutMenuItem.toInt() );
            prepStmt.executeUpdate();
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to create menu item shortcut: %t";
            VerticalEngineLogger.errorCreate( message, sqle );
        }
        finally
        {
            close( prepStmt );
        }
    }

    private void createPage( Connection con, Element elem, int type, MenuItemKey menuItemKey )
        throws VerticalCreateException
    {

        Element pageElem = XMLTool.getElement( elem, "page" );
        int pKey;

        Document pageDoc = XMLTool.createDocument();
        pageDoc.appendChild( pageDoc.importNode( pageElem, true ) );
        pKey = getPageHandler().createPage( XMLTool.documentToString( pageDoc ) );

        PreparedStatement preparedStmt = null;
        try
        {
            preparedStmt = con.prepareStatement( MENU_ITEM_PAGE_UPDATE_KEY );
            preparedStmt.setInt( 1, pKey );
            preparedStmt.setInt( 2, type );
            preparedStmt.setInt( 3, menuItemKey.toInt() );
            preparedStmt.executeUpdate();
        }
        catch ( SQLException e )
        {
            VerticalEngineLogger.errorCreate("A database error occurred: %t", e );
        }
        finally
        {
            close( preparedStmt );
        }
    }

    public Document getMenuItem( User user, int key, boolean withParents )
    {
        return getMenuItem( user, key, withParents, false, false );
    }

    public Document getMenuItem( User user, int key, boolean withParents, boolean complete, boolean includePageConfig )
    {
        Document doc;
        Element rootElement;
        doc = XMLTool.createDocument( "menuitems" );
        rootElement = doc.getDocumentElement();

        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;
        try
        {
            con = getConnection();

            preparedStmt = con.prepareStatement( getSecurityHandler().appendMenuItemSQL( user, MENU_ITEM_SELECT_BY_KEY ) );
            preparedStmt.setInt( 1, key );
            resultSet = preparedStmt.executeQuery();

            if ( resultSet.next() )
            {
                buildMenuItemXML( doc, rootElement, resultSet, -1, complete, includePageConfig, true, true, true, -1 );
            }

            // include parents?
            if ( withParents )
            {
                // yep. call getMenuItemDOM recursivly.
                Element menuItemElement = (Element) doc.getDocumentElement().getFirstChild();
                if ( menuItemElement.hasAttribute( "parent" ) )
                {
                    int parentKey = Integer.valueOf( menuItemElement.getAttribute( "parent" ) );
                    while ( parentKey >= 0 )
                    {
                        // get the parent:
                        doc = getMenuItem( user, parentKey, false, false, false );

                        // move the child inside the parent:
                        rootElement = doc.getDocumentElement();
                        Element parentElement = (Element) rootElement.getFirstChild();
                        if ( parentElement != null )
                        {
                            Element menuItemsElement = XMLTool.createElement( doc, parentElement, "menuitems" );
                            menuItemsElement.appendChild( doc.importNode( menuItemElement, true ) );
                            menuItemElement = parentElement;

                            if ( menuItemElement.hasAttribute( "parent" ) )
                            {
                                parentKey = Integer.valueOf( menuItemElement.getAttribute( "parent" ) );
                            }
                            else
                            {
                                parentKey = -1;
                            }
                        }
                        else
                        {
                            parentKey = -1;
                        }
                    }
                }
            }
        }
        catch ( SQLException sqle )
        {
            VerticalEngineLogger.error("SQL error.", sqle );
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
        }

        return doc;

    }

    public String getMenuItemName( int menuItemKey )
    {
        StringBuffer sql = XDG.generateSelectSQL( db.tMenuItem, db.tMenuItem.mei_sName, false, db.tMenuItem.mei_lKey );
        return getCommonHandler().getString( sql.toString(), menuItemKey );
    }

    private Hashtable<String, Integer> getMenuItemTypesAsHashtable()
    {
        if ( menuItemTypes == null )
        {
            menuItemTypes = new Hashtable<String, Integer>();
            for ( MenuItemType menuItemType : MenuItemType.values() )
            {
                menuItemTypes.put( menuItemType.getName(), menuItemType.getKey() );
            }
        }
        return menuItemTypes;

    }

    private void setMenuItemContentKey( MenuItemKey menuItemKey, int contentKey )
    {
        // first delete the contentkey for this menu item
        StringBuffer sql = XDG.generateRemoveSQL( db.tMenuItemContent, db.tMenuItemContent.mic_mei_lKey );
        getCommonHandler().executeSQL( sql.toString(), menuItemKey.toInt() );

        // now insert the new value
        if ( contentKey != -1 )
        {
            sql = XDG.generateInsertSQL( db.tMenuItemContent );
            getCommonHandler().executeSQL( sql.toString(), new int[]{menuItemKey.toInt(), contentKey} );
        }
    }

    private int getMenuItemContentKey( int menuItemKey )
    {
        StringBuffer sql =
            XDG.generateSelectSQL( db.tMenuItemContent, db.tMenuItemContent.mic_con_lKey, false, db.tMenuItemContent.mic_mei_lKey );
        return getCommonHandler().getInt( sql.toString(), menuItemKey );
    }

    private void validateMenuItemName( String name )
    {
        if ( name == null || name.trim().length() == 0 )
        {
            throw new IllegalArgumentException( "Name must be set: '" + name + "'" );
        }
        if ( name.endsWith( " " ) )
        {
            throw new IllegalArgumentException( "Name cannot end with a space: '" + name + "'" );
        }
        if ( name.startsWith( " " ) )
        {
            throw new IllegalArgumentException( "Name cannot start with a space: '" + name + "'" );
        }
        if ( name.indexOf( "  " ) >= 0 )
        {
            throw new IllegalArgumentException( "Name cannot contain double spaces: '" + name + "'" );
        }
    }

    private boolean menuItemNameExists( SiteKey siteKey, MenuItemKey parentKey, String newNameOfMenuItem, MenuItemKey excludeKey )
    {
        if ( parentKey != null )
        {
            MenuItemEntity parent = menuItemDao.findByKey( parentKey );
            final MenuItemEntity childByName = parent.getChildByName( newNameOfMenuItem );
            if ( childByName == null )
            {
                return false;
            }
            else
            {
                if ( childByName.getMenuItemKey().equals( excludeKey ) )
                {
                    return false;
                }
                else
                {
                    return true;
                }
            }
        }
        else
        {
            SiteEntity site = siteDao.findByKey( siteKey );
            final MenuItemEntity childByName = site.getChild( newNameOfMenuItem );
            if ( childByName == null )
            {
                return false;
            }
            else
            {
                if ( childByName.getMenuItemKey().equals( excludeKey ) )
                {
                    return false;
                }
                else
                {
                    return true;
                }
            }
        }
    }
}

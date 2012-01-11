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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.VerticalLogger;
import com.enonic.vertical.engine.VerticalEngineLogger;
import com.enonic.vertical.engine.XDG;
import com.enonic.vertical.event.MenuHandlerListener;
import com.enonic.vertical.event.VerticalEventMulticaster;

import com.enonic.cms.framework.util.TIntArrayList;

import com.enonic.cms.core.CalendarUtil;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.structure.RunAsType;
import com.enonic.cms.core.structure.menuitem.MenuItemKey;
import com.enonic.cms.core.structure.menuitem.MenuItemType;
import com.enonic.cms.core.structure.page.PageEntity;
import com.enonic.cms.core.structure.page.template.PageTemplateEntity;

public final class MenuHandler
    extends BaseHandler
{
    private final VerticalEventMulticaster multicaster = new VerticalEventMulticaster();

    private static final String ELEMENT_NAME_MENU_NAME = "menu-name";

    private static final String ELEMENT_NAME_DISPLAY_NAME = "displayname";

    private static final String COLUMN_NAME_DISPLAY_NAME = "mei_sDisplayName";

    private static final String COLUMN_NAME_ALTERNATIVE_NAME = "mei_sSubTitle";

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

    final static private String MENU_ITEM_SELECT_BY_KEY =
        "SELECT " + MENU_ITEM_COLS + " FROM " + MENU_ITEM_TABLE + " LEFT JOIN tMenu ON tMenu.men_lKey = " + MENU_ITEM_TABLE +
            ".mei_men_lKey " + " LEFT JOIN tLanguage ON " + MENU_ITEM_TABLE + ".mei_lan_lKey = tLanguage.lan_lKey" + " WHERE mei_lKey = ?";

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

    private int getMenuItemContentKey( int menuItemKey )
    {
        StringBuffer sql =
            XDG.generateSelectSQL( db.tMenuItemContent, db.tMenuItemContent.mic_con_lKey, false, db.tMenuItemContent.mic_mei_lKey );
        return getCommonHandler().getInt( sql.toString(), menuItemKey );
    }
}

/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.handlers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enonic.esl.sql.model.Column;
import com.enonic.esl.util.RelationAggregator;
import com.enonic.esl.util.RelationNode;
import com.enonic.esl.util.RelationTree;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.engine.VerticalEngineLogger;
import com.enonic.vertical.engine.XDG;
import com.enonic.vertical.engine.dbmodel.CategoryTable;
import com.enonic.vertical.engine.dbmodel.CategoryView;
import com.enonic.vertical.engine.dbmodel.ConAccessRight2Table;
import com.enonic.vertical.engine.dbmodel.ContentPublishedView;

import com.enonic.cms.framework.hibernate.support.InClauseBuilder;
import com.enonic.cms.framework.xml.XMLDocument;

import com.enonic.cms.core.CalendarUtil;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.category.CategoryEntity;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.category.CategoryXmlCreator;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.store.dao.CategoryDao;

public class CategoryHandler
    extends BaseHandler
{
    @Autowired
    private CategoryDao categoryDao;

    private int getContentCount( CategoryKey categoryKey, boolean recursive )
    {

        if ( categoryKey == null )
        {
            return 0;
        }
        return getContentCount( new int[]{categoryKey.toInt()}, recursive );
    }

    private int getContentCount( int[] categoryKeys, boolean recursive )
    {

        if ( categoryKeys == null || categoryKeys.length == 0 )
        {
            return 0;
        }

        int contentCount = 0;

        for ( int i = 0; i < categoryKeys.length; i++ )
        {
            CategoryEntity category = categoryDao.findByKey( new CategoryKey( categoryKeys[i] ) );
            List<ContentKey> contents = contentDao.findContentKeysByCategory( category );
            contentCount += contents.size();

            if ( recursive )
            {
                List<CategoryKey> childrenKeys = category.getChildrenKeys();
                int[] catKeys = new int[childrenKeys.size()];
                for ( int j = 0; j < childrenKeys.size(); j++ ) {
                    catKeys[j] = childrenKeys.get( j ).toInt();
                }
                if ( childrenKeys.size() > 0 )
                {
                    contentCount += getContentCount( catKeys, recursive );
                }
            }

        }

        return contentCount;
    }

    public int getContentTypeKey( CategoryKey categoryKey )
    {
        int contentTypeKey = -1;
        CategoryEntity category = categoryDao.findByKey( categoryKey );
        if ( category != null && category.getContentType() != null )
        {
            contentTypeKey = category.getContentType().getKey();
        }
        return contentTypeKey;
    }

    public Document getSuperCategoryNames( CategoryKey categoryKey, boolean withContentCount, boolean includeCategory )
    {
        Map<CategoryEntity, Integer> contentCountMap = new HashMap<CategoryEntity, Integer>();
        List<CategoryEntity> categories = new ArrayList<CategoryEntity>();

        CategoryXmlCreator xmlCreator = new CategoryXmlCreator();

        if ( categoryKey == null )
        {
            return xmlCreator.createEmptyCategoryNamesDocument( "No categorykey given" ).getAsDOMDocument();
        }

        CategoryEntity category = categoryDao.findByKey( categoryKey );

        if ( category == null )
        {
            return xmlCreator.createEmptyCategoryNamesDocument( "No category found" ).getAsDOMDocument();
        }

        CategoryEntity currCategory;

        if ( includeCategory )
        {
            currCategory = category;
        }
        else
        {
            currCategory = category.getParent();
        }

        while ( currCategory != null )
        {
            categories.add( currCategory );

            if ( withContentCount )
            {
                contentCountMap.put( category, getContentCount( category.getKey(), true ) );
            }

            currCategory = currCategory.getParent();
        }

        // Reverse list to get the root first in result
        Collections.reverse( categories );

        XMLDocument newDoc = xmlCreator.createCategoryNames( categories, contentCountMap );

        return newDoc.getAsDOMDocument();
    }

    private int getUnitKey( CategoryKey categoryKey )
    {

        if ( categoryKey == null )
        {
            return -1;
        }

        CategoryEntity category = categoryDao.findByKey( categoryKey );
        if ( category == null || category.getUnitExcludeDeleted() == null )
        {
            return -1;
        }

        return category.getUnitExcludeDeleted().getKey();
    }

    public Document getCategories( User olduser, CategoryKey categoryKey, int levels, boolean topLevel, boolean details, boolean catCount,
                                   boolean contentCount )
    {
        Document doc = XMLTool.createDocument();

        try
        {
            RelationNode node = getCategoryTree( olduser, categoryKey, contentCount );
            if ( node != null )
            {
                fetchCategories( doc, node, levels, topLevel, details, catCount, contentCount );
            }
            else
            {
                doc = XMLTool.createDocument( "categories" );
                if ( contentCount )
                {
                    doc.getDocumentElement().setAttribute( "count", "0" );
                }
            }
        }
        catch ( SQLException e )
        {
            VerticalEngineLogger.error("Failed to fetch relation keys: %t", e );
        }

        return doc;
    }

    private void fetchCategories( Document doc, RelationNode node, int levels, boolean topLevel, boolean details, boolean catCount,
                                  boolean contentCount )
        throws SQLException
    {
        node.selectLevels( levels == 0 ? Integer.MAX_VALUE : levels );
        Map<Number, Element> entryMap = fetchCategoryElements( doc, node.findSelectedKeys(), details );
        Element root = buildCategoriesElement( doc, entryMap, node, topLevel, catCount, contentCount );
        doc.appendChild( root );
    }

    private Element buildCategoriesElement( Document doc, Map<Number, Element> entryMap, RelationNode node, boolean topLevel,
                                            boolean catCount, boolean contentCount )
    {
        Element elem = doc.createElement( "categories" );

        if ( catCount )
        {
            int count = node != null ? node.getChildCount() : 0;
            int totalCount = node != null ? node.getTotalChildCount() : 0;

            if ( topLevel )
            {
                count = 1;
                totalCount += 1;
            }

            elem.setAttribute( "count", String.valueOf( count ) );
            elem.setAttribute( "totalcount", String.valueOf( totalCount ) );
        }

        if ( topLevel )
        {
            Element tmp = buildCategoryElement( doc, entryMap, node, catCount, contentCount );
            if ( tmp != null )
            {
                elem.appendChild( tmp );
            }
        }
        else
        {
            List entries = node.getChildren();
            for ( Iterator i = entries.iterator(); i.hasNext(); )
            {
                Element tmp = buildCategoryElement( doc, entryMap, (RelationNode) i.next(), catCount, contentCount );

                if ( tmp != null )
                {
                    elem.appendChild( tmp );
                }
            }
        }

        return elem;
    }


    private Element buildCategoryElement( Document doc, Map<Number, Element> entryMap, RelationNode node, boolean catCount,
                                          boolean contentCount )
    {
        if ( node == null )
        {
            return null;
        }

        if ( !node.isSelected() )
        {
            return null;
        }

        Element elem = entryMap.get( node.getKey() );
        if ( elem == null )
        {
            return null;
        }

        if ( contentCount )
        {
            int[] count = findContentCount( node );
            elem.setAttribute( "contentcount", String.valueOf( count[0] ) );
            elem.setAttribute( "totalcontentcount", String.valueOf( count[1] ) );
        }

        Element tmp = buildCategoriesElement( doc, entryMap, node, false, catCount, contentCount );
        if ( tmp != null )
        {
            elem.appendChild( tmp );
        }

        return elem;
    }

    private int[] findContentCount( RelationNode node )
    {
        int count = 0;
        int totalCount = 0;

        Object data = node.getData();
        if ( data instanceof Number )
        {
            count = ( (Number) data ).intValue();
        }

        data = node.accept( new RelationAggregator() );
        if ( data instanceof Number )
        {
            totalCount = ( (Number) data ).intValue();
        }

        return new int[]{count, totalCount};
    }

    private Map<Number, Element> fetchCategoryElements( Document doc, Set keys, boolean details )
        throws SQLException
    {
        Connection conn;
        PreparedStatement stmt = null;
        ResultSet result = null;
        Map<Number, Element> map = new HashMap<Number, Element>();

        if ( keys.isEmpty() )
        {
            return map;
        }

        try
        {
            conn = getConnection();
            String sql = getSelectCategoryByKeysSQL( keys );
            stmt = conn.prepareStatement( sql );
            result = stmt.executeQuery();

            while ( result.next() )
            {
                Number key = (Number) result.getObject( 1 );
                Number ctyKey = (Number) result.getObject( 2 );
                Number superKey = (Number) result.getObject( 3 );
                String ownerKey = (String) result.getObject( 4 );
                String ownerUID = result.getString( 5 );
                String ownerName = result.getString( 6 );
                String modifierKey = (String) result.getObject( 7 );
                String modifierUID = result.getString( 8 );
                String modifierName = result.getString( 9 );
                Timestamp created = result.getTimestamp( 10 );
                Timestamp timestamp = result.getTimestamp( 11 );
                String name = result.getString( 12 );
                String description = result.getString( 13 );

                Element root = doc.createElement( "category" );
                root.setAttribute( "key", key.toString() );

                if ( superKey != null )
                {
                    root.setAttribute( "superkey", superKey.toString() );
                }

                if ( details )
                {
                    if ( ctyKey != null )
                    {
                        root.setAttribute( "contenttypekey", ctyKey.toString() );
                    }

                    root.setAttribute( "created", CalendarUtil.formatTimestamp( created, false ) );
                    root.setAttribute( "timestamp", CalendarUtil.formatTimestamp( timestamp, false ) );

                    Element owner = XMLTool.createElement( doc, root, "owner", ownerName );
                    owner.setAttribute( "key", ownerKey );
                    owner.setAttribute( "uid", ownerUID );

                    Element modifier = XMLTool.createElement( doc, root, "modifier", modifierName );
                    modifier.setAttribute( "key", modifierKey );
                    modifier.setAttribute( "uid", modifierUID );

                    if ( description != null )
                    {
                        Element descr = XMLTool.createElement( doc, root, "description" );
                        XMLTool.createCDATASection( doc, descr, description );
                    }
                }

                XMLTool.createElement( doc, root, "title", name );
                map.put( key, root );
            }
        }
        finally
        {
            close( result );
            close( stmt );
        }

        return map;
    }

    private RelationNode getCategoryTree( User olduser, CategoryKey categoryKey, boolean contentCount )
        throws SQLException
    {
        GroupHandler handler = getGroupHandler();
        SecurityHandler secHandler = getSecurityHandler();

        String[] groups = null;
        if ( !secHandler.isEnterpriseAdmin( olduser ) )
        {
            groups = handler.getAllGroupMembershipsForUser( olduser );
        }

        return getCategoryTree( olduser, categoryKey, groups, contentCount );
    }

    private RelationNode getCategoryTree( User olduser, CategoryKey categoryKey, String[] groups, boolean contentCount )
        throws SQLException
    {
        Connection con;
        PreparedStatement stmt = null;
        ResultSet result = null;
        RelationTree tree = new RelationTree();
        RelationNode node = null;

        int oldstyleCategoryKey = categoryKey != null ? categoryKey.toInt() : -1;
        try
        {
            con = getConnection();
            int unitKey = getUnitKey( categoryKey );
            String sql = getSelectCategoriesSQL( olduser );
            stmt = con.prepareStatement( sql );
            stmt.setInt( 1, unitKey );
            result = stmt.executeQuery();

            while ( result.next() )
            {
                Number child = (Number) result.getObject( 1 );
                Number parent = (Number) result.getObject( 2 );

                if ( parent == null )
                {
                    tree.addChild( child );
                }
                else
                {
                    tree.addChild( parent, child );
                }
            }
            tree.setRoot( oldstyleCategoryKey );
            node = tree.getNode( oldstyleCategoryKey );

            close( result );
            result = null;
            close( stmt );
            stmt = null;

            boolean anyCategoriesFound = node != null;

            if ( contentCount && anyCategoriesFound )
            {
                sql = getSelectContentCountSQL( categoryKey, groups );
                stmt = con.prepareStatement( sql );
                result = stmt.executeQuery();

                while ( result.next() )
                {
                    Integer entry = result.getInt( 1 );
                    Integer count = result.getInt( 2 );

                    RelationNode current = node.getNode( entry );
                    if ( current != null )
                    {
                        current.setData( count );
                    }
                }
            }
        }
        finally
        {
            close( result );
            close( stmt );
        }

        return node;
    }

    private String getSelectCategoriesSQL( User olduser )
    {
        SecurityHandler securityHandler = getSecurityHandler();
        CategoryView view = CategoryView.getInstance();
        Column[] selectColumns = {view.cat_lKey, view.cat_cat_lSuper};
        Column[] whereColumns = {view.cat_uni_lKey};
        StringBuffer sql = XDG.generateSelectSQL( view, selectColumns, false, whereColumns );

        securityHandler.appendCategorySQL( olduser, sql, false );
        return sql.toString();
    }

    private String getKeySetSQL( String[] keys )
    {
        if ( keys != null )
        {
            StringBuilder sql = new StringBuilder();

            if ( keys.length == 0 )
            {
                sql.append( "IS NULL" );
            }
            else if ( keys.length == 1 )
            {
                sql.append( "= '" ).append( String.valueOf( keys[0] ) ).append( "'" );
            }
            else
            {
                sql.append( "IN (" );
                for ( int i = 0; i < keys.length; i++ )
                {
                    if ( i > 0 )
                    {
                        sql.append( ", " );
                    }
                    sql.append( "'" );
                    sql.append( keys[i] );
                    sql.append( "'" );
                }
                sql.append( ")" );
            }

            return sql.toString();
        }
        else
        {
            return null;
        }
    }

    private String getSelectContentCountSQL( CategoryKey key, String[] groups )
    {
        ContentPublishedView view = ContentPublishedView.getInstance();
        ConAccessRight2Table accessTable = this.db.tConAccessRight2;

        StringBuilder sql = new StringBuilder();
        sql.append( "SELECT " ).append( view.cat_lKey.getName() );
        sql.append( ", COUNT(" ).append( view.con_lKey.getName() ).append( ")" );
        sql.append( " FROM " ).append( view.getReplacementSql() );

        String groupSql = getKeySetSQL( groups );
        if ( groupSql != null )
        {
            sql.append( " LEFT JOIN " ).append( accessTable.getName() );
            sql.append( " ON " ).append( accessTable.coa_con_lKey.getName() );
            sql.append( " = " ).append( view.con_lKey );
        }

        sql.append( " WHERE " ).append( view.cat_uni_lKey.getName() );
        sql.append( " IN (" ).append( getSelectUnitByCategorySQL( key ) ).append( ")" );

        if ( groupSql != null )
        {
            sql.append( " AND " ).append( accessTable.coa_grp_hKey.getName() );
            sql.append( " " ).append( groupSql );
            sql.append( " AND " ).append( accessTable.coa_bRead.getName() ).append( " = 1" );
        }

        sql.append( " GROUP BY " ).append( view.cat_lKey.getName() );
        return sql.toString();
    }

    private String getSelectUnitByCategorySQL( CategoryKey key )
    {
        CategoryTable table = this.db.tCategory;
        StringBuilder sql = new StringBuilder();
        sql.append( "SELECT " ).append( table.cat_uni_lKey.getName() );
        sql.append( " FROM " ).append( table.getName() );
        sql.append( " WHERE " ).append( table.cat_lKey.getName() );
        sql.append( " = " ).append( key.toInt() );
        return sql.toString();
    }

    private String getSelectCategoryByKeysSQL( Set keys )
    {
        CategoryView table = CategoryView.getInstance();

        StringBuilder sql = new StringBuilder();
        sql.append( "SELECT " ).append( table.cat_lKey.getName() );
        sql.append( ", " ).append( table.cat_cty_lKey.getName() );
        sql.append( ", " ).append( table.cat_cat_lSuper.getName() );
        sql.append( ", " ).append( table.usr_hOwner.getName() );
        sql.append( ", " ).append( table.usr_sOwnerUID.getName() );
        sql.append( ", " ).append( table.usr_sOwnerName.getName() );
        sql.append( ", " ).append( table.usr_hModifier.getName() );
        sql.append( ", " ).append( table.usr_sModifierUID.getName() );
        sql.append( ", " ).append( table.usr_sModifierName.getName() );
        sql.append( ", " ).append( table.cat_dteCreated.getName() );
        sql.append( ", " ).append( table.cat_dteTimestamp.getName() );
        sql.append( ", " ).append( table.cat_sName.getName() );
        sql.append( ", " ).append( table.cat_sDescription.getName() );
        sql.append( " FROM " ).append( table.getReplacementSql() );
        sql.append( " WHERE " );

        InClauseBuilder inClauseFilter = new InClauseBuilder<Object>( table.cat_lKey.getName(), keys )
        {
            public void appendValue( StringBuffer sql, Object value )
            {
                sql.append( value );
            }
        };

        sql.append( inClauseFilter );
        return sql.toString();
    }
}

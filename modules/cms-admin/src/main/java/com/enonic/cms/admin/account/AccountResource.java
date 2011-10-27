package com.enonic.cms.admin.account;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sun.jersey.api.core.InjectParam;

import com.enonic.cms.core.search.Facet;
import com.enonic.cms.core.search.FacetEntry;
import com.enonic.cms.core.search.Facets;
import com.enonic.cms.core.search.SearchSortOrder;
import com.enonic.cms.core.search.account.AccountIndexField;
import com.enonic.cms.core.search.account.AccountSearchHit;
import com.enonic.cms.core.search.account.AccountSearchResults;
import com.enonic.cms.core.search.account.AccountSearchQuery;
import com.enonic.cms.core.search.account.AccountSearchService;
import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.UserDao;

import com.enonic.cms.domain.EntityPageList;

@Component
@Path("/admin/data/account")
@Produces("application/json")
public final class AccountResource
{
    private static final Logger LOG = LoggerFactory.getLogger( AccountResource.class );

    @Autowired
    private UserDao userDao;

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private AccountSearchService searchService;

    private AccountModelTranslator modelTranslator;

    public AccountResource()
    {
        modelTranslator = new AccountModelTranslator();
    }

    @GET
    @Path("search")
    public Map<String, Object> list( @InjectParam final AccountLoadRequest req )
    {
        LOG.info(
            "Search accounts: query='" + req.getQuery() + "' , index=" + req.getStart() + ", count=" + req.getLimit() + ", selectUsers=" +
                req.isSelectUsers() + ", selectGroups=" + req.isSelectGroups() + ", userstores=" + req.getUserstores() );

        final String userstores = req.getUserstores();
        final String[] userstoreList = ( userstores == null ) ? new String[0] : userstores.split( "," );

        final AccountSearchQuery searchQueryCountFacets = new AccountSearchQuery()
            .setCountOnly( true )
            .setCount( req.getLimit() )
            .setFrom( req.getStart() )
            .setQuery( req.getQuery() )
            .setGroups( req.isSelectGroups() )
            .setUsers( req.isSelectUsers() )
            .setSortField( AccountIndexField.parse( req.getSort() ) )
            .setSortOrder( SearchSortOrder.valueOf( req.getSortDir() ) );

        final AccountSearchResults searchCountFacets = searchService.search( searchQueryCountFacets );
        final AccountSearchQuery searchQuery = new AccountSearchQuery( searchQueryCountFacets )
            .setCountOnly( false )
            .setUserStores( userstoreList );
        final AccountSearchResults searchResults = searchService.search( searchQuery );

        final List list = new ArrayList();

        for ( AccountSearchHit searchHit : searchResults )
        {
            switch ( searchHit.getAccountType() )
            {
                case GROUP:
                    GroupEntity groupEntity = this.groupDao.findByKey( new GroupKey( searchHit.getKey().toString() ) );
                    list.add( groupEntity );
                    break;

                case USER:
                    UserEntity userEntity = this.userDao.findByKey( searchHit.getKey().toString() );
                    list.add( userEntity );
                    break;
            }
        }

        final EntityPageList accountList = new EntityPageList( searchResults.getCount(), searchResults.getTotal(), list);
        AccountsModel accountsModel = modelTranslator.toModel( accountList );

        setFacets( accountsModel, searchCountFacets );

        Map<String, Object> result = new HashMap<String, Object>();
        result.put( "results", accountsModel );
        return result;
    }

    private void setFacets( AccountsModel accountsModel, AccountSearchResults searchResults )
    {
        final Facets facets = searchResults.getFacets();

        for ( Facet facet : facets )
        {
            final SearchFacetModel searchFacetModel = new SearchFacetModel( facet.getName() );
            for ( FacetEntry facetEntry : facet )
            {
                searchFacetModel.setEntryCount( facetEntry.getTerm(), facetEntry.getCount() );
            }
            accountsModel.addFacet( searchFacetModel );
        }
    }

}

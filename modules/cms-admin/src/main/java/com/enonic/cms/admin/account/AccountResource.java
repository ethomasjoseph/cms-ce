package com.enonic.cms.admin.account;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
import com.enonic.cms.core.search.account.AccountKey;
import com.enonic.cms.core.search.account.AccountSearchHit;
import com.enonic.cms.core.search.account.AccountSearchQuery;
import com.enonic.cms.core.search.account.AccountSearchResults;
import com.enonic.cms.core.search.account.AccountSearchService;
import com.enonic.cms.core.search.account.AccountType;
import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreService;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.UserDao;

import com.enonic.cms.domain.EntityPageList;

@Component
@Path("/admin/data/account")
@Produces(MediaType.APPLICATION_JSON)
public final class AccountResource
{
    private static final Logger LOG = LoggerFactory.getLogger( AccountResource.class );

    @Autowired
    private UserDao userDao;

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private UserStoreService userStoreService;

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
                req.isSelectUsers() + ", selectGroups=" + req.isSelectGroups() + ", userstores=" + req.getUserstores() + ", orgs=" +
                req.getOrganizations() );

        final AccountSearchResults searchResults = search( req );

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

        final EntityPageList accountList = new EntityPageList( searchResults.getCount(), searchResults.getTotal(), list );
        AccountsModel accountsModel = modelTranslator.toModel( accountList );

        setFacets( accountsModel, searchResults );

        Map<String, Object> result = new HashMap<String, Object>();
        result.put( "results", accountsModel );
        return result;
    }

    private AccountSearchResults search( final AccountLoadRequest req )
    {
        final String userstores = req.getUserstores();
        final String[] userstoreList = ( userstores == null ) ? new String[0] : userstores.split( "," );

        final String organizations = req.getOrganizations();
        final String[] organizationList = ( organizations == null ) ? new String[0] : organizations.split( "," );

        final AccountSearchQuery searchQueryCountFacets =
            new AccountSearchQuery().setIncludeResults( true ).setCount( req.getLimit() ).setFrom( req.getStart() ).setQuery(
                req.getQuery() ).setGroups( req.isSelectGroups() ).setUsers( req.isSelectUsers() ).setUserStores(
                userstoreList ).setOrganizations( organizationList ).setSortField( AccountIndexField.parse( req.getSort() ) ).setSortOrder(
                SearchSortOrder.valueOf( req.getSortDir() ) );

        final AccountSearchResults searchResults = searchService.search( searchQueryCountFacets );
        return searchResults;
    }

    private void setFacets( AccountsModel accountsModel, AccountSearchResults searchResults )
    {
        final Facets facets = searchResults.getFacets();
        facets.consolidate();

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

    @POST
    @Path("export")
    public Response exportAsCsv( @InjectParam final AccountExportRequest req,
                                 @DefaultValue("ISO-8859-1") @FormParam("encoding") String characterEncoding )
        throws UnsupportedEncodingException
    {
        final int accountsExportLimit = 5000;

        final AccountSearchResults searchResults;
        if ( req.getKeys().length > 0 )
        {
            searchResults = getAccountListForKeys( req.getKeys() );
        }
        else
        {
            final AccountLoadRequest searchRequest = new AccountLoadRequest();
            searchRequest.setOrganizations( req.getOrganizations() );
            searchRequest.setUserstores( req.getUserstores() );
            searchRequest.setSelectGroups( req.isSelectGroups() );
            searchRequest.setSelectUsers( req.isSelectUsers() );
            searchRequest.setQuery( req.getQuery() );
            searchRequest.setSort( req.getSort() );
            searchRequest.setSortDir( req.getSortDir() );
            searchRequest.setLimit( accountsExportLimit );
            searchResults = search( searchRequest );
        }
        final AccountsCsvExport csvExport = new AccountsCsvExport( groupDao, userDao );
        final String content = csvExport.generateCsv( searchResults );
        final String filename = csvExport.getExportFileName( new Date() );
        final String attachmentHeader = "attachment; filename=" + filename;

        final byte[] data = content.getBytes( characterEncoding );

        return Response.ok( data )
            .type( "text/csv; charset=" + characterEncoding )
            .header( "Content-Encoding", characterEncoding )
            .header( "Content-Disposition", attachmentHeader )
            .build();
    }

    private AccountSearchResults getAccountListForKeys( final String[] keys )
    {
        // TODO: refactor this when accounts API and model classes are in place
        final AccountSearchResults accounts = new AccountSearchResults( 0, keys.length );
        for ( final String key : keys )
        {
            final AccountType type = userDao.findByKey( key ) == null ? AccountType.GROUP : AccountType.USER;
            final AccountSearchHit account = new AccountSearchHit( new AccountKey( key ), type, 0 );
            accounts.add( account );
        }
        return accounts;
    }

    @GET
    @Path("suggestusername")
    public Response suggestUsername( @QueryParam("firstname") @DefaultValue("") final String firstName,
                                     @QueryParam("lastname") @DefaultValue("") final String lastName,
                                     @QueryParam("userstore") @DefaultValue("") final String userStoreName )
    {
        final UserIdGenerator userIdGenerator = new UserIdGenerator( userDao );

        final UserStoreEntity store = userStoreService.findByName( userStoreName );

        if ( store == null )
        {
            return Response.status( Response.Status.NOT_FOUND ).build();
        }

        final String suggestedUserName = userIdGenerator.generateUserId( firstName.trim(), lastName.trim(), store.getKey() );
        final Map<String, String> response = new HashMap<String, String>();
        response.put( "username", suggestedUserName );
        return Response.ok( response ).build();
    }

}

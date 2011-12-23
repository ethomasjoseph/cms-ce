package com.enonic.cms.admin.user;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sun.jersey.api.NotFoundException;
import com.sun.jersey.api.core.InjectParam;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

import com.enonic.cms.core.search.account.AccountIndexData;
import com.enonic.cms.core.search.account.AccountKey;
import com.enonic.cms.core.search.account.AccountSearchService;
import com.enonic.cms.core.security.user.StoreNewUserCommand;
import com.enonic.cms.core.security.user.UpdateUserCommand;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.userstore.UserStoreService;
import com.enonic.cms.store.dao.UserDao;

import com.enonic.cms.domain.EntityPageList;

@Component
@Path("/admin/data/user")
@Produces(MediaType.APPLICATION_JSON)
public final class UsersResource
{

    private static final Logger LOG = LoggerFactory.getLogger( UsersResource.class );

    private static final int PHOTO_CACHE_TIMEOUT = Period.minutes( 5 ).getSeconds();

    private static final String UPLOAD_PATH = "/admin/resources/uploads/";

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserStoreService userStoreService;

    @Autowired
    private UserPhotoService photoService;

    @Autowired
    private UserModelTranslator userModelTranslator;

    @Autowired
    private AccountSearchService searchService;

    public UsersResource()
    {
    }

    @GET
    @Path("list")
    public UsersModel getAll( @InjectParam final UserLoadRequest req )
    {
        final EntityPageList<UserEntity> list =
                this.userDao.findAll( req.getStart(), req.getLimit(), req.buildHqlQuery(), req.buildHqlOrder() );
        return userModelTranslator.toModel( list );
    }

    @GET
    @Path("userinfo")
    public UserModel getUserInfo( @QueryParam("key") final String key )
    {
        final UserEntity entity = findEntity( key );
        return userModelTranslator.toUserInfoModel( entity );
    }

    @GET
    @Path("photo")
    @Produces("image/png")
    public Response getPhoto( @QueryParam("key") final String key, 
                              @QueryParam("thumb") @DefaultValue("false") final boolean thumb,
                              @Context final Request request)
        throws Exception
    {
        final UserEntity entity = findEntity( key );
        if ( entity.getPhoto() == null )
        {
            return Response.status( Response.Status.NOT_FOUND ).build();
        }
        byte[] photo = this.photoService.renderPhoto( entity, thumb ? 40 : 100 );
        final String photoHash = Integer.toHexString( Arrays.hashCode( photo ) );
        final EntityTag eTag = new EntityTag( photoHash );

        Response.ResponseBuilder builder = request.evaluatePreconditions( eTag );
        if ( builder == null )
        {
            builder = Response.ok( photo );
        }

        final CacheControl cc = new CacheControl();
        cc.setMaxAge( PHOTO_CACHE_TIMEOUT );
        return builder.cacheControl( cc ).tag( eTag ).build();
    }

    @POST
    @Path("/photo")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Map<String, Object> uploadPhoto( @FormDataParam("file") InputStream fileInputStream,
                               @FormDataParam("file") FormDataContentDisposition fileDetail,
                               @Context ServletContext context)
    {
        Map<String, Object> response = new HashMap<String, Object>();
        try
        {
            final File folder = new File( context.getRealPath( UPLOAD_PATH ) );
            if ( !folder.exists() )
            {
                folder.mkdirs();
            }
            final String filename = StringUtils.substringBeforeLast( fileDetail.getFileName(), "." );
            final String extension = "." + StringUtils.substringAfterLast( fileDetail.getFileName(), "." );
            final File uploadFile = File.createTempFile( filename, extension, folder );

            int read;
            final byte[] bytes = new byte[1024];
            final OutputStream out = new FileOutputStream( uploadFile );
            while ( ( read = fileInputStream.read( bytes ) ) != -1 )
            {
                out.write( bytes, 0, read );
            }
            out.flush();
            out.close();
            response.put( "success", true );
            response.put( "src", "resources/uploads/" + uploadFile.getName() );
            response.put( "photoRef", uploadFile.getName() );
        }
        catch ( IOException e )
        {
            LOG.error( "Could not store uploaded photo", e );
            response.put( "success", false );
        }
        return response;
    }

    @POST
    @Path("changepassword")
    public Map<String, Object> changePassword( @FormParam("cpw_password") final String password,
                                               @FormParam("userKey") final String userKey )
    {

        Map<String, Object> res = new HashMap<String, Object>();
        if ( password.length() <= User.MAX_PASSWORD_LENGTH && password.length() >= User.MIN_PASSWORD_LENGTH )
        {
            LOG.info( "Password has been changed for user " + userKey );
            res.put( "success", true );
        }
        else
        {
            res.put( "success", false );
            res.put( "errorMsg", "Password is out of possible length" );
        }
        return res;
    }

    @POST
    @Path("delete")
    public Map<String, Object> deleteUser( @FormParam("userKey") final String userKey )
    {
        Map<String, Object> res = new HashMap<String, Object>();
        LOG.info( "User was deleted: " + userKey );
        res.put( "success", true );
        return res;
    }

    @POST
    @Path("update")
    @Consumes("application/json")
    public Map<String, Object> saveUser( UserModel userData, @Context ServletContext context )
    {
        final boolean isValid = isValidUserData( userData );
        final Map<String, Object> res = new HashMap<String, Object>();
        if ( isValid )
        {
            final String photoRef = userData.getPhoto();
            if ( StringUtils.isNotEmpty( photoRef ) )
            {
                final File photoFile = new File( context.getRealPath( UPLOAD_PATH ), photoRef );
                if ( photoFile.exists() )
                {
                    userData.setPhoto( photoFile.getAbsolutePath() );
                }
                else
                {
                    userData.setPhoto( null );
                }
            }

            if ( userData.getKey() == null )
            {
                StoreNewUserCommand command = userModelTranslator.toNewUserCommand( userData );
                UserKey userKey = userStoreService.storeNewUser( command );
                indexUser( userKey.toString() );
            }
            else
            {
                UpdateUserCommand command = userModelTranslator.toUpdateUserCommand( userData );
                userStoreService.updateUser( command );
                indexUser( userData.getKey() );
            }
            res.put( "success", true );
        }
        else
        {
            res.put( "success", false );
            res.put( "error", "Validation failed" );
        }
        return res;
    }

    private void indexUser( String userKey )
    {
        final UserEntity userEntity = this.userDao.findByKey( userKey );
        if ( userEntity == null )
        {
            searchService.deleteIndex( userKey );
            return;
        }

        final com.enonic.cms.core.search.account.User user = new com.enonic.cms.core.search.account.User();
        user.setKey( new AccountKey( userEntity.getKey().toString() ) );
        user.setName( userEntity.getName() );
        user.setEmail( userEntity.getEmail() );
        user.setDisplayName( userEntity.getDisplayName() );
        user.setUserStoreName( userEntity.getUserStore().getName() );
        user.setLastModified( userEntity.getTimestamp() );
        user.setUserInfo( userEntity.getUserInfo() );
        final AccountIndexData accountIndexData = new AccountIndexData( user );
        searchService.index( accountIndexData );
    }

    private boolean isValidUserData( UserModel userData )
    {
        boolean isValid = StringUtils.isNotBlank( userData.getDisplayName() ) && StringUtils.isNotBlank( userData.getName() ) &&
            StringUtils.isNotBlank( userData.getEmail() );
        return isValid;
    }

    private UserEntity findEntity( final String key )
    {
        if ( key == null )
        {
            throw new NotFoundException();
        }

        final UserEntity entity = this.userDao.findByKey( key );
        if ( entity == null )
        {
            throw new NotFoundException();
        }

        return entity;
    }

}

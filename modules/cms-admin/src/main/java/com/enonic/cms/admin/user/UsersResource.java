package com.enonic.cms.admin.user;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sun.jersey.api.NotFoundException;
import com.sun.jersey.api.core.InjectParam;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.user.StoreNewUserCommand;
import com.enonic.cms.core.security.user.UpdateUserCommand;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.userstore.UserStoreService;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.UserDao;

import com.enonic.cms.domain.EntityPageList;

@Component
@Path("/admin/data/user")
@Produces("application/json")
public final class UsersResource
{

    private static final Logger LOG = LoggerFactory.getLogger( UsersResource.class );

    @Autowired
    private UserDao userDao;

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private UserStoreService userStoreService;

    @Autowired
    private UserPhotoService photoService;

    @Autowired
    private UserModelTranslator userModelTranslator;

    private final URI GROUP_PHOTO_ICON;

    private final URI GROUP_PHOTO_THUMB_ICON;

    private final URI USER_PHOTO_ICON;

    private final URI USER_PHOTO_THUMB_ICON;

    public UsersResource()
        throws URISyntaxException
    {
        GROUP_PHOTO_ICON = new URI( "admin/resources/icons/128x128/group.png" );
        GROUP_PHOTO_THUMB_ICON = new URI( "admin/resources/icons/32x32/group.png" );
        USER_PHOTO_THUMB_ICON = new URI( "admin/resources/icons/256x256/dummy-user.png" );
        USER_PHOTO_ICON = new URI( "admin/resources/icons/256x256/dummy-user.png" );
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
    public Response getPhoto( @QueryParam("key") final String key, @QueryParam("thumb") @DefaultValue("false") final boolean thumb )
        throws Exception
    {
        try
        {
            final UserEntity entity = findEntity( key );
            if ( entity.getPhoto() == null )
            {
                final URI iconUrl = thumb ? USER_PHOTO_THUMB_ICON : USER_PHOTO_ICON;
                return Response.status( Response.Status.MOVED_PERMANENTLY ).location( iconUrl ).build();
            }
            byte[] photo = this.photoService.renderPhoto( entity, thumb ? 40 : 100 );
            return Response.ok(photo).build();
        }
        catch ( NotFoundException e )
        {
            if ( isGroup( key ) )
            {
                final URI iconUrl = thumb ? GROUP_PHOTO_THUMB_ICON : GROUP_PHOTO_ICON;
                return Response.status( Response.Status.MOVED_PERMANENTLY ).location( iconUrl ).build();
            }
            throw e;
        }
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
            File folder = new File( context.getRealPath( "/admin/resources/uploads/" ) );
            if ( !folder.exists() )
            {
                folder.mkdirs();
            }
            File file = new File( folder.getPath() + "/" + fileDetail.getFileName() );
            if ( file.exists() )
            {
                file.delete();
            }
            int read;
            byte[] bytes = new byte[1024];
            OutputStream out = new FileOutputStream( file );
            while ( ( read = fileInputStream.read( bytes ) ) != -1 )
            {
                out.write( bytes, 0, read );
            }
            out.flush();
            out.close();
            response.put( "success", true );
            response.put( "src", "resources/uploads/" + fileDetail.getFileName() );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
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
    public Map<String, Object> saveUser( UserModel userData )
    {
        boolean isValid =
                StringUtils.isNotBlank( userData.getDisplayName() ) && StringUtils.isNotBlank( userData.getName() ) &&
                        StringUtils.isNotBlank( userData.getEmail() );
        Map<String, Object> res = new HashMap<String, Object>();
        if ( isValid )
        {
            if ( userData.getKey() == null )
            {
                StoreNewUserCommand command = userModelTranslator.toNewUserCommand( userData );
                userStoreService.storeNewUser( command );
            }
            else
            {
                UpdateUserCommand command = userModelTranslator.toUpdateUserCommand( userData );
                userStoreService.updateUser( command );
            }
            res.put( "success", true );
        }
        else
        {
            res.put( "success", false );
            res.put( "error", "Validation was failed" );
        }
        return res;
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

    private boolean isGroup( final String key )
    {
        if ( key == null )
        {
            return false;
        }

        final GroupEntity groupEntity = this.groupDao.find( key );
        return groupEntity != null;
    }

}

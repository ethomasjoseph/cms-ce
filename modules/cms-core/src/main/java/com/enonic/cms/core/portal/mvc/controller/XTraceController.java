package com.enonic.cms.core.portal.mvc.controller;

import com.enonic.cms.core.security.InvalidCredentialsException;
import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.user.QualifiedUsername;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.userstore.MemberOfResolver;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.core.security.userstore.UserStoreService;

import com.enonic.cms.store.dao.UserDao;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Strings;
import com.google.common.io.ByteStreams;

import com.enonic.cms.framework.util.MimeTypeResolver;

public class XTraceController
    extends AbstractController
{
    @Autowired
    protected ResourceLoader resourceLoader;

    @Autowired
    protected SecurityService securityService;

    @Autowired
    protected UserStoreService userStoreService;

    @Autowired
    protected MemberOfResolver memberOfResolver;

    @Autowired
    private UserDao userDao;

    @Override
    protected ModelAndView handleRequestInternal( HttpServletRequest request, HttpServletResponse response )
        throws Exception
    {
        final String path = request.getRequestURI();
        if ( path.matches( ".+/resources/.+" ) )
        {
            handleResource( request, response );
            return null;
        }

        return handleAuthenticationForm( request, response );
    }

    private ModelAndView handleAuthenticationForm( HttpServletRequest request, HttpServletResponse response )
        throws Exception
    {
        final Map<String, Object> model = new HashMap<String, Object>();
        model.put( "authenticationFailed", false );

        if ( isAuthenticationFormSubmitted( request ) )
        {
            try
            {
                authenticateUser( request );
                HttpSession httpSession = request.getSession( true );
                httpSession.setAttribute( "X-Trace-Server-Enabled", "true" );

                response.sendRedirect( (String) request.getAttribute( "xtrace.originalUrl" ) );

                return null;
            }
            catch ( InvalidCredentialsException ice )
            {
                model.put( "authenticationFailed", true );
            }
        }

        model.put( "userStores", createUserStoreMap() );

        return new ModelAndView( "xtraceAuthenticationPage", model );
    }

    private void authenticateUser( HttpServletRequest request )
        throws InvalidCredentialsException
    {
        final String userName = request.getParameter( "_xtrace_username" );
        final String password = request.getParameter( "_xtrace_password" );
        final String userStore = request.getParameter( "_xtrace_userstore" );

        final UserStoreKey userStoreKey = new UserStoreKey( Integer.parseInt( userStore ) );
        final UserStoreEntity systemUserStore = userStoreService.getUserStore( userStoreKey );
        final QualifiedUsername qname = new QualifiedUsername( systemUserStore.getKey(), userName );

        final UserEntity user = userDao.findByQualifiedUsername( qname );
        if ( !memberOfResolver.hasDeveloperPowers( user ) )
        {
            throw new InvalidCredentialsException( user.getKey().toString() );
        }

        securityService.authenticateUser( qname, password );
    }

    private boolean isAuthenticationFormSubmitted( HttpServletRequest request )
    {
        if ( !"POST".equalsIgnoreCase( request.getMethod() ) )
        {
            return false;
        }
        String xtraceAuthentication = request.getParameter( "_xtrace_authentication" );

        if ( "true".equalsIgnoreCase( xtraceAuthentication ) )
        {
            return true;
        }
        return false;
    }

    private HashMap<String, String> createUserStoreMap()
    {
        final HashMap<String, String> userStoreMap = new HashMap<String, String>();

        final List<UserStoreEntity> userStoreList = userStoreService.findAll();
        for ( UserStoreEntity userStore : userStoreList )
        {
            // TODO: Is this the way to get  userStore key? -> userStore.getKey().toString()
            userStoreMap.put( userStore.getKey().toString(), userStore.getName() );
        }

        return userStoreMap;
    }

    private void handleResource( HttpServletRequest request, HttpServletResponse response )
        throws Exception
    {
        final String fileName = FilenameUtils.getName( request.getRequestURI() );
        final String mimeType = MimeTypeResolver.getInstance().getMimeType( fileName );
        final InputStream inputStream = this.resourceLoader.getResource( "WEB-INF/xtrace/" + fileName ).getInputStream();
        final ServletOutputStream outputStream = response.getOutputStream();

        ByteStreams.copy( inputStream, outputStream );

        response.setContentType( mimeType );
    }
}
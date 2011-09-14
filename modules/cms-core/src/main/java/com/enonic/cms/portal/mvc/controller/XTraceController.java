package com.enonic.cms.portal.mvc.controller;

import com.enonic.cms.core.security.InvalidCredentialsException;
import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.user.QualifiedUsername;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.core.security.userstore.UserStoreService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

public class XTraceController extends AbstractController
{
    @Resource
    protected SecurityService securityService;

    @Resource
    protected UserStoreService userStoreService;

    @Override
    protected ModelAndView handleRequestInternal( HttpServletRequest request, HttpServletResponse response ) throws Exception
    {
        final String path = request.getRequestURI();

        // Request .+/resources/.+ -> serve resources (http://www.enonic.com/en/community/_xtrace/resources/main.css
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
        model.put( "loginFailed", false );

        if ( isAuthenticationFormSubmitted( request ) )
        {
            try
            {
                authenticateUser( request );
                HttpSession httpSession = request.getSession( true );
                httpSession.setAttribute( "serverTracingIsEnabled", "true" );

                response.sendRedirect( (String) request.getAttribute( "xtrace.originalUrl" ) );

                return null;
            }
            catch ( InvalidCredentialsException ice )
            {
                model.put( "loginFailed", true );
            }
        }

        return new ModelAndView( "xtraceLogin", model );
    }

    private void authenticateUser( HttpServletRequest request )
            throws InvalidCredentialsException
    {
        String userName = request.getParameter( "_xtrace_username" );
        String password = request.getParameter( "_xtrace_password" );

        // TODO: Get user store key from url parameter
        UserStoreKey userStoreKey = new UserStoreKey( 3 );
        UserStoreEntity systemUserStore = userStoreService.getUserStore( userStoreKey );

        QualifiedUsername qname = new QualifiedUsername( systemUserStore.getKey(), userName );

        securityService.authenticateUser( qname, password );
    }

    private boolean isAuthenticationFormSubmitted( HttpServletRequest request )
    {
        if( !"POST".equalsIgnoreCase( request.getMethod() ) )
        {
            return false;
        }
        String xtraceAuthentication = request.getParameter( "_xtrace_authentication" );

        if( "true".equalsIgnoreCase( xtraceAuthentication ) )
        {
            return true;
        }
        return false;
    }

    private void handleResource( HttpServletRequest request, HttpServletResponse response )
            throws Exception
    {
    }
}

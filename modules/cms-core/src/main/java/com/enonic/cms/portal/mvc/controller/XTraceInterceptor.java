package com.enonic.cms.portal.mvc.controller;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class XTraceInterceptor
        extends HandlerInterceptorAdapter
{
    @Override
    public boolean preHandle( HttpServletRequest request, HttpServletResponse response, Object handler ) throws Exception
    {
        if ( !clientIsEnabled( request ) )
        {
            return true;
        }

        if ( clientIsAuthenticated( request ) )
        {
            return true;
        }

        forwardToAuthenticationForm( request, response );

        return false;
    }

    private boolean clientIsEnabled( HttpServletRequest request )
    {
        return "true".equals( request.getHeader( "X-Trace-Client-Enabled" ) );
    }

    private boolean clientIsAuthenticated( HttpServletRequest request )
    {
        return "true".equals( request.getSession().getAttribute( "serverTracingIsEnabled") );
    }

    private void forwardToAuthenticationForm( HttpServletRequest request, HttpServletResponse response )
            throws Exception
    {
        request.setAttribute( "xtrace.originalUrl", request.getRequestURL().toString() );
        request.getRequestDispatcher( request.getRequestURI() + "/_xtrace/login" ).forward( request, response );
    }

}

package com.enonic.cms.core.portal.mvc.controller;

import org.apache.commons.codec.binary.Base64;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.enonic.cms.core.portal.livetrace.PageRenderingTrace;
import com.enonic.cms.core.portal.xtrace.JsonSerializer;
import com.enonic.cms.core.portal.xtrace.XTraceHelper;

public class XTraceInterceptor
    extends HandlerInterceptorAdapter
{
    @Override
    public boolean preHandle( HttpServletRequest request, HttpServletResponse response, Object handler )
        throws Exception
    {
        if ( !XTraceHelper.clientIsEnabled( request ) )
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

    private boolean clientIsAuthenticated( HttpServletRequest request )
    {
        return "true".equals( request.getSession().getAttribute( "X-Trace-Server-Enabled" ) );
    }

    private void forwardToAuthenticationForm( HttpServletRequest request, HttpServletResponse response )
        throws Exception
    {
        request.setAttribute( "xtrace.originalUrl", request.getRequestURL().toString() );
        request.getRequestDispatcher( request.getRequestURI() + "/_xtrace/login" ).forward( request, response );
    }

    public void postHandle( HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView )
        throws Exception
    {
        final PageRenderingTrace currentPageRenderingTrace = XTraceHelper.getCurrentPageRenderingTrace();

        if ( !XTraceHelper.clientIsEnabled( request ) )
        {
            return;
        }

        if ( currentPageRenderingTrace == null )
        {
            return;
        }

        String portalRequestId = String.valueOf( currentPageRenderingTrace.getPortalRequestTrace().getCompletedNumber() );
        response.setHeader( "X-Trace-Info-Id", portalRequestId );
    }
}
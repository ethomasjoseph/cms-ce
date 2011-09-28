package com.enonic.cms.portal.mvc.controller;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.enonic.cms.portal.livetrace.LivePortalTraceService;
import com.enonic.cms.portal.livetrace.PortalRequestTrace;
import com.enonic.cms.portal.xtrace.JsonSerializer;

public class XTraceInterceptor
        extends HandlerInterceptorAdapter
{
    @Autowired
    LivePortalTraceService livePortalTraceService;

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
        return "true".equals( request.getSession().getAttribute( "X-Trace-Server-Enabled") );
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
        PortalRequestTrace requestTrace = livePortalTraceService.getCurrentPortalRequestTrace();

        final String traceInfo = new JsonSerializer().serialize( requestTrace );
        final String traceInfoEncoded = new String( Base64.encodeBase64( traceInfo.getBytes() ) );
        setResponseHeaders( response, traceInfoEncoded );
    }

    private void setResponseHeaders( HttpServletResponse response, String traceInfo )
    {
        final Integer charsPrHeader = 1000;
        final int traceInfoCharLength = traceInfo.length();

        final double numberOfHeadersAsDouble = Math.ceil( traceInfoCharLength / charsPrHeader.doubleValue() );
        final int numberOfHeaders = (int) ( numberOfHeadersAsDouble );

        for ( int i = 0; i < numberOfHeaders; i++ )
        {
            final int beginIndex = charsPrHeader * i;
            int endIndex = beginIndex + charsPrHeader;
            if ( endIndex >= traceInfoCharLength )
                endIndex = beginIndex + ( traceInfoCharLength - beginIndex );

            final String headerValue = traceInfo.substring( beginIndex, endIndex );

            response.setHeader( "X-Trace-Info-" + i, headerValue );
        }
    }

}
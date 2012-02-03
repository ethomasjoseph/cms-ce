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
        return "true".equals( request.getSession().getAttribute( "X-Trace-Client-Authenticated" ) );
    }

    private void forwardToAuthenticationForm( HttpServletRequest request, HttpServletResponse response )
            throws Exception
    {
        request.setAttribute( "xtrace.originalUrl", request.getRequestURL().toString() );
        request.getRequestDispatcher( request.getRequestURI() + "/_xtrace/authenticate" ).forward( request, response );
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

        final String traceInfo = new JsonSerializer().serialize( currentPageRenderingTrace );
        final String traceInfoEncoded = new String( Base64.encodeBase64( traceInfo.getBytes() ) );

        setResponseHeaders( response, traceInfoEncoded );
    }

    private void setResponseHeaders( HttpServletResponse response, String traceInfo )
    {
        final Integer charsPrHeader = 1000;
        final int charLength = traceInfo.length();
        final double numberOfHeadersAsDouble = Math.ceil( charLength / charsPrHeader.doubleValue() );
        final int numberOfHeaders = (int) ( numberOfHeadersAsDouble );

        for ( int i = 0; i < numberOfHeaders; i++ )
        {
            final int beginIndex = charsPrHeader * i;

            int endIndex = beginIndex + charsPrHeader;
            if ( endIndex >= charLength )
                endIndex = beginIndex + ( charLength - beginIndex );

            final String value = traceInfo.substring( beginIndex, endIndex );

            response.setHeader( createResponseHeaderName(i), value );
        }
    }

    private String createResponseHeaderName( int index )
    {
        final int i = index + 1;

        String key = Integer.toString( i );
        if ( i < 10 )
        {
            key = "0" + key;
        }

        return "X-Trace-Info-" + key;
    }

}
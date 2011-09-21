package com.enonic.cms.portal.mvc.controller;

import org.apache.commons.codec.binary.Base64;
import org.springframework.web.servlet.ModelAndView;
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
        final String traceInfo = "{  \"xtrace\": {    \"version\": 1,    \"page\": {      \"key\": 73,      \"name\": \"Home\",      \"cacheable\": false,      \"cache_hit\": false,      \"page_template_name\": \"document\",      \"start_time\": 0,      \"end_time\": 1692,      \"xslt\": {        \"processing_time\": 28      },      \"total_time\": 1692,      \"run_as_user\": \"local\\\\tan\",      \"functions\": [        {          \"name\": \"getMenuBranch\",          \"start_time\": 0,          \"end_time\": 1,          \"time\": 128        }      ],      \"windows\": [        {          \"key\": 1925,          \"name\": \"Frontpage banner (950px)\",          \"cacheable\": true,          \"cache_hit\": true,          \"start_time\": 128,          \"end_time\": 480,          \"total_time\": 352,          \"xslt\": {            \"processing_time\": 7          },          \"functions\": [            {              \"name\": \"getContentBySection\",              \"start_time\": 128,              \"end_time\": 1,              \"time\": 345            }          ]        },        {          \"key\": 1926,          \"name\": \"Case studies (frontpage box)\",          \"cacheable\": true,          \"cache_hit\": true,          \"start_time\": 480,          \"end_time\": 1368,          \"total_time\": 888,          \"xslt\": {            \"processing_time\": 19          },          \"functions\": [            {              \"name\": \"getContentBySection\",              \"start_time\": 480,              \"end_time\": 1,              \"time\": 39            },            {              \"name\": \"getContentBySection\",              \"start_time\": 519,              \"end_time\": 1,              \"time\": 528            },            {              \"name\": \"getMenuItem\",              \"start_time\": 1047,              \"end_time\": 1,              \"time\": 3            },            {              \"name\": \"getContentBySection\",              \"start_time\": 1050,              \"end_time\": 1,              \"time\": 295            },            {              \"name\": \"getMenuItem\",              \"start_time\": 1345,              \"end_time\": 1,              \"time\": 4            }          ]        },        {          \"key\": 1926,          \"name\": \"Vignette list (boxes)\",          \"cacheable\": true,          \"cache_hit\": true,          \"start_time\": 1368,          \"end_time\": 1664,          \"total_time\": 296,          \"xslt\": {            \"processing_time\": 19          },          \"functions\": [            {              \"name\": \"getContentBySection\",              \"start_time\": 1368,              \"end_time\": 1,              \"time\": 287            }          ]        }      ]    }  }}";
        final String traceInfoEncoded = new String( Base64.encodeBase64( traceInfo.getBytes() ) );
        setResponseHeaders( response, traceInfoEncoded );
    }

    // TODO: Make this functionality more testable?
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
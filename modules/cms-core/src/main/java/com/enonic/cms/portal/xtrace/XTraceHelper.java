package com.enonic.cms.portal.xtrace;

import javax.servlet.http.HttpServletRequest;

import com.enonic.cms.portal.livetrace.PageRenderingTrace;

public class XTraceHelper
{
    private final static ThreadLocal<PageRenderingTrace> CURRENT_TRACE = new ThreadLocal<PageRenderingTrace>();

    public static boolean clientIsEnabled( HttpServletRequest request )
    {
        return "true".equals( request.getHeader( "X-Trace-Client-Enabled" ) );
    }

    public static void setCurrentTrace( PageRenderingTrace pageRenderingTrace )
    {
        CURRENT_TRACE.set( pageRenderingTrace );
    }

    public static PageRenderingTrace getCurrentPageRenderingTrace()
    {
        PageRenderingTrace renderingTrace = CURRENT_TRACE.get();
        CURRENT_TRACE.set( null );

        return renderingTrace;
    }

}

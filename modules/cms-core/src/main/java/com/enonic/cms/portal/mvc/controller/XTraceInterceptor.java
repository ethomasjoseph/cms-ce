package com.enonic.cms.portal.mvc.controller;

import com.sun.deploy.net.HttpResponse;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

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

        if ( !login( request ) && !clientIsLoggedIn( request ) )
        {
            printLoginFormHtml( response );
            return false;
        }

        if ( login( request ) )
        {
            HttpSession httpSession = request.getSession(true);
            httpSession.setAttribute( "serverTracingIsEnabled", true );

            return true;
        }

        return true;
    }

    private boolean login( HttpServletRequest request )
    {
        String userName = request.getParameter( "username" );
        String password = request.getParameter( "password" );

        return userName != null && !userName.isEmpty() && password != null && !password.isEmpty();
    }

    private boolean clientIsEnabled( HttpServletRequest request )
    {
        String serverTraceHeader = request.getHeader( "X-Server-Trace" );
        return serverTraceHeader != null && serverTraceHeader.equals( "true" );
    }

    private boolean clientIsLoggedIn( HttpServletRequest request )
    {
        Object serverTracingIsEnabled = request.getSession().getAttribute( "serverTracingIsEnabled");
        return serverTracingIsEnabled != null && serverTracingIsEnabled.equals( true );
    }

    private void printLoginFormHtml( HttpServletResponse response )
            throws IOException
    {
        response.setContentType( "text/html" );
        PrintWriter out = response.getWriter();

        StringBuilder sb = new StringBuilder();

        sb.append("<!doctype html>");
        sb.append("<html lang='en'>");
        sb.append("<head>");
        sb.append("    <meta charset='utf-8'>");
        sb.append("    <title>Login</title>");
        sb.append("</head>");
        sb.append("<body>");
        sb.append("<div>");
        sb.append("    <form action='.' method='post'>");
        sb.append("        <div>");
        sb.append("            <label for='username'>Username:</label>");
        sb.append("            <br/>");
        sb.append("            <input type='text' name='username' id='username'/>");
        sb.append("            <br/>");
        sb.append("            <label for='password'>Password:</label>");
        sb.append("            <br/>");
        sb.append("            <input type='password' name='password' id='password'/>");
        sb.append("            <br/>");
        sb.append("            <input type='submit' value='Log in'/>");
        sb.append("        </div>");
        sb.append("    </form>");
        sb.append("</div>");
        sb.append("<script>");
        sb.append("    document.getElementById( 'username' ).focus();");
        sb.append("</script>");
        sb.append("</body>");
        sb.append("</html>");
        sb.append( "<html>" );
        sb.append( "</html>" );

        out.println( sb );
    }
}

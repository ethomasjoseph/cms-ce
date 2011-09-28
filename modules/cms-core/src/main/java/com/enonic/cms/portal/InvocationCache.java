/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import com.enonic.cms.portal.datasource.DataSourceContext;
import com.enonic.cms.portal.livetrace.DatasourceExecutionTracer;
import com.enonic.cms.portal.livetrace.LivePortalTraceService;

/**
 * Keeps track of all executed methods and the result returned.  If a method is invoked with the same parameters, the same result is
 * returned as in the first invocation, without executing the method.
 */
public final class InvocationCache
{

    private final HashMap<String, Object> cache;

    private final LivePortalTraceService livePortalTraceService;

    public InvocationCache()
    {
        this( null );
    }

    public InvocationCache( LivePortalTraceService livePortalTraceService )
    {
        this.cache = new HashMap<String, Object>();
        this.livePortalTraceService = livePortalTraceService;
    }

    private void appendSignature( StringBuffer str, Method method )
    {
        str.append( method.getName() ).append( "(" );

        Class[] params = method.getParameterTypes();
        for ( int i = 0; i < params.length; i++ )
        {
            if ( i > 0 )
            {
                str.append( "," );
            }

            str.append( params[i].getName() );
        }

        str.append( ")" );
    }


    private String getCacheKey( Object targetObject, Method method, Object[] args )
    {
        StringBuffer str = new StringBuffer();
        str.append( targetObject.hashCode() ).append( "-" );
        appendSignature( str, method );
        str.append( "-" );
        appendArguments( str, args );
        return str.toString();
    }

    public Object invoke( Object targetObject, Method method, Object[] args, boolean isCacheable )
        throws Throwable
    {
        Object result;

        if ( isCacheable )
        {
            String key = getCacheKey( targetObject, method, args );
            result = this.cache.get( key );

            if ( result == null )
            {
                result = invokeReal( targetObject, method, args );
                this.cache.put( key, result );
            }
            else
            {
                DatasourceExecutionTracer.traceIsCacheUsed( true, livePortalTraceService );
            }
        }
        else
        {
            result = invokeReal( targetObject, method, args );
        }

        return result;
    }

    private Object invokeReal( Object targetObject, Method method, Object[] args )
        throws Throwable
    {
        try
        {
            return method.invoke( targetObject, args );
        }
        catch ( InvocationTargetException e )
        {
            throw e.getTargetException();
        }
    }

    private void appendArguments( StringBuffer str, Object[] args )
    {
        for ( int i = 0; i < args.length; i++ )
        {
            if ( i > 0 )
            {
                str.append( "," );
            }

            if ( args[i] == null )
            {
                str.append( "null" );
            }
            else if ( args[i] instanceof int[] )
            {

                appendArguments( str, (int[]) args[i] );
            }
            else if ( ( args[i] instanceof DataSourceContext ) )
            {
                // skip data source context, not necessary as long as it contains the same values for every datasource
            }
            else
            {
                str.append( args[i].toString() );
            }

        }
    }

    private void appendArguments( StringBuffer str, int[] args )
    {
        str.append( "{" );
        for ( int i = 0; i < args.length; i++ )
        {
            if ( i > 0 )
            {
                str.append( "," );
            }
            str.append( args[i] );
        }
        str.append( "}" );
    }
}

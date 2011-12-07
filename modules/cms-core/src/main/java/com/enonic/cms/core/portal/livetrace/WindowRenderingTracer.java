/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.livetrace;

import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.structure.page.Window;
import com.enonic.cms.core.structure.portlet.PortletEntity;

/**
 * Nov 25, 2010
 */
public class WindowRenderingTracer
{
    public static WindowRenderingTrace startTracing( final LivePortalTraceService livePortalTraceService )
    {
        final PortalRequestTrace portalRequestTrace = livePortalTraceService.getCurrentPortalRequestTrace();

        if ( portalRequestTrace != null )
        {
            return livePortalTraceService.startWindowRenderTracing( portalRequestTrace );
        }
        else
        {
            return null;
        }
    }

    public static void stopTracing( final WindowRenderingTrace trace, final LivePortalTraceService livePortalTraceService )
    {
        if ( trace != null )
        {
            livePortalTraceService.stopTracing( trace );
        }
    }

    public static void traceRequestedWindow( final WindowRenderingTrace trace, final Window window )
    {
        if ( trace != null && window != null )
        {
            final PortletEntity portlet = window.getPortlet();
            if ( portlet != null )
            {
                trace.setPortletName( portlet.getName() );
            }
        }
    }

    public static void traceRenderer( final WindowRenderingTrace trace, final UserEntity renderer )
    {
        if ( trace != null && renderer != null )
        {
            trace.setRenderer( renderer.getQualifiedName() );
        }
    }

    public static void traceUsedCachedResult( final WindowRenderingTrace trace, boolean cacheable, boolean usedCachedResult )
    {
        if ( trace != null )
        {
            trace.setCacheable( cacheable );
            trace.setUsedCachedResult( usedCachedResult );
        }
    }

    public static void startConcurrencyBlockTimer( WindowRenderingTrace trace )
    {
        if ( trace != null )
        {
            trace.startConcurrencyBlockTimer();
        }
    }

    public static void stopConcurrencyBlockTimer( WindowRenderingTrace trace )
    {
        if ( trace != null )
        {
            trace.stopConcurrencyBlockTimer();
        }
    }
}

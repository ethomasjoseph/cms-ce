package com.enonic.cms.portal.xtrace;

import com.enonic.cms.portal.livetrace.DatasourceExecutionTrace;
import com.enonic.cms.portal.livetrace.PageRenderingTrace;
import com.enonic.cms.portal.livetrace.WindowRenderingTrace;
import com.google.gson.*;

import java.util.List;

// TODO: Calculate accurate start times and end times
public class JsonSerializer
{
    public String serialize( PageRenderingTrace pageRenderingTrace )
    {
        JsonObject wrapper = new JsonObject();

        JsonObject xtrace = new JsonObject();
        appendVersion( xtrace );
        appendPage( xtrace, pageRenderingTrace );

        wrapper.add( "xtrace", xtrace );

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();

        Gson gson = gsonBuilder.create();

        return gson.toJson( wrapper );
    }

    private void appendVersion( JsonObject xtrace )
    {
        xtrace.addProperty( "version", "1.0" );
    }

    private void appendPage( JsonObject xtrace, PageRenderingTrace pageRenderingTrace )
    {
        xtrace.add( "page", createPage( pageRenderingTrace ) );
    }

    private JsonObject createPage( PageRenderingTrace pageRenderingTrace )
    {
        JsonObject page = new JsonObject();

        page.addProperty( "key", "TODO" );
        page.addProperty( "name", pageRenderingTrace.getPortalRequestTrace().getUrl() );
        page.addProperty( "cacheable", "TODO" );
        page.addProperty( "cache_hit", pageRenderingTrace.isUsedCachedResult() );
        page.addProperty( "page_template_name", "TODO" );
        page.addProperty( "start_time", 0 );
        page.addProperty( "end_time", pageRenderingTrace.getDuration().getExecutionTimeInMilliseconds() );

        JsonObject xsltObj = new JsonObject();
        xsltObj.addProperty( "processing_time", pageRenderingTrace.getInstructionPostProcessingTrace().getDuration().getExecutionTimeInMilliseconds() );
        page.add( "xslt", xsltObj );

        page.addProperty( "total_time", pageRenderingTrace.getDuration().getExecutionTimeInMilliseconds() );

        String userStoreName = "";
        if ( pageRenderingTrace.getRenderer().getUserStoreName() != null )
        {
            userStoreName = pageRenderingTrace.getRenderer().getUserStoreName() + "\\";
        }

        String qualifiedName = userStoreName + pageRenderingTrace.getRenderer().getUsername();

        page.addProperty( "run_as_user", qualifiedName );

        appendPageDatasources( page, pageRenderingTrace, 0 );

        long dataSourcesTotalExecutionTime = calculatePageDatasourcesExecutionTimesMS( pageRenderingTrace.getDatasourceExecutionTraces() );
        appendWindows( page, pageRenderingTrace, dataSourcesTotalExecutionTime );

        return page;
    }

    private void appendPageDatasources( JsonObject page, PageRenderingTrace pageRenderingTrace, long startTime )
    {
        page.add( "datasources", createDatasources( pageRenderingTrace.getDatasourceExecutionTraces(), startTime ) );
    }

    private void appendWindows( JsonObject page, PageRenderingTrace pageRenderingTrace, long startTime )
    {
        page.add( "windows", createWindows( pageRenderingTrace.getWindowRenderingTraces(), startTime ) );
    }

    private JsonArray createWindows( List<WindowRenderingTrace> windows, long startTime )
    {
        JsonArray windowsArray = new JsonArray();

        long tempStartTime = startTime;
        for ( WindowRenderingTrace window : windows )
        {
            createWindow( windowsArray, window, tempStartTime );
            tempStartTime = tempStartTime + window.getDuration().getExecutionTimeInMilliseconds();
        }

        return windowsArray;
    }

    private void createWindow( JsonArray windowsArray, WindowRenderingTrace window, long startTime )
    {
        JsonObject windowObject = new JsonObject();
        windowObject.addProperty( "key", "TODO" );
        windowObject.addProperty( "name", window.getPortletName() );
        windowObject.addProperty( "cacheable", "TODO" );
        windowObject.addProperty( "cache_hit", window.isUsedCachedResult() );
        windowObject.addProperty( "start_time", startTime );

        windowObject.addProperty( "end_time", startTime + window.getDuration().getExecutionTimeInMilliseconds() );
        windowObject.addProperty( "total_time", window.getDuration().getExecutionTimeInMilliseconds() );

        JsonObject xsltObject = new JsonObject();
        xsltObject.addProperty( "processing_time", window.getInstructionPostProcessingTrace().getDuration().getExecutionTimeInMilliseconds() );
        windowObject.add( "xslt", xsltObject );

        String userStoreName = "";
        if ( window.getRenderer().getUserStoreName() != null )
        {
            userStoreName = window.getRenderer().getUserStoreName() + "\\";
        }

        String qualifiedName = userStoreName + window.getRenderer().getUsername();

        windowObject.addProperty( "run_as_user", qualifiedName );

        appendWindowDatasources( windowObject, window, startTime );

        windowsArray.add( windowObject );
    }

    private void appendWindowDatasources( JsonObject windowObject, WindowRenderingTrace window, long startTime )
    {
        windowObject.add( "datasources", createDatasources( window.getDatasourceExecutionTraces(), startTime ) );
    }


    private JsonArray createDatasources( List<DatasourceExecutionTrace> datasources, long startTime )
    {
        JsonArray datasourceArray = new JsonArray();

        long tempStartTime = startTime;
        for ( DatasourceExecutionTrace datasource : datasources )
        {
            createDatasource( datasourceArray, datasource, tempStartTime );
            tempStartTime = tempStartTime + datasource.getDuration().getExecutionTimeInMilliseconds();
        }

        return datasourceArray;
    }

    private void createDatasource( JsonArray jsonArray, DatasourceExecutionTrace datasource, long startTime )
    {
        JsonObject datasourceObject = new JsonObject();
        datasourceObject.addProperty( "name", datasource.getMethodName() );
        datasourceObject.addProperty( "start_time", startTime );
        datasourceObject.addProperty( "end_time", startTime + datasource.getDuration().getExecutionTimeInMilliseconds() );
        datasourceObject.addProperty( "time", datasource.getDuration().getExecutionTimeInMilliseconds() );

        jsonArray.add( datasourceObject );
    }

    private long calculatePageDatasourcesExecutionTimesMS( List<DatasourceExecutionTrace> datasources )
    {
        long total = 0;
        for ( DatasourceExecutionTrace datasource : datasources )
        {
            total += datasource.getDuration().getExecutionTimeInMilliseconds();
        }

        return total;
    }

}

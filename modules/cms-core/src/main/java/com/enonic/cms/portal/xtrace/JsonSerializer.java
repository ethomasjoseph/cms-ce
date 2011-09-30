package com.enonic.cms.portal.xtrace;

import com.enonic.cms.portal.livetrace.DatasourceExecutionTrace;
import com.enonic.cms.portal.livetrace.PageRenderingTrace;
import com.enonic.cms.portal.livetrace.WindowRenderingTrace;
import com.google.gson.*;

import java.util.List;

// TODO: Calculate the start times and end times
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
            userStoreName = pageRenderingTrace.getRenderer().getUserStoreName();
        }

        String qualifiedName = userStoreName + "\\" + pageRenderingTrace.getRenderer().getUsername();

        page.addProperty( "run_as_user", qualifiedName );

        appendPageDatasources( page, pageRenderingTrace );
        appendWindows( page, pageRenderingTrace );

        return page;
    }

    private void appendPageDatasources( JsonObject page, PageRenderingTrace pageRenderingTrace )
    {
        page.add( "datasources", createDatasources( pageRenderingTrace.getDatasourceExecutionTraces() ) );
    }

    private void appendWindows( JsonObject page, PageRenderingTrace pageRenderingTrace )
    {
        page.add( "windows", createWindows( pageRenderingTrace.getWindowRenderingTraces() ) );
    }

    private JsonArray createWindows( List<WindowRenderingTrace> windows )
    {
        JsonArray windowsArray = new JsonArray();

        for ( WindowRenderingTrace window : windows )
        {
            createWindow( windowsArray, window );
        }

        return windowsArray;
    }

    private void createWindow( JsonArray windowsArray, WindowRenderingTrace window )
    {
        JsonObject windowObject = new JsonObject();
        windowObject.addProperty( "key", "TODO" );
        windowObject.addProperty( "name", window.getPortletName() );
        windowObject.addProperty( "cacheable", "TODO" );
        windowObject.addProperty( "cache_hit", window.isUsedCachedResult() );
        windowObject.addProperty( "start_time", window.getDuration().getStartTime().toString() );
        windowObject.addProperty( "end_time", window.getDuration().getStopTime().toString() );
        windowObject.addProperty( "total_time", window.getDuration().getExecutionTimeInMilliseconds() );

        JsonObject xsltObject = new JsonObject();
        xsltObject.addProperty( "processing_time", window.getInstructionPostProcessingTrace().getDuration().getExecutionTimeInMilliseconds() );
        windowObject.add( "xslt", xsltObject );

        windowObject.addProperty( "run_as_user", window.getRenderer().getUsername() );

        appendWindowDatasources( windowObject, window );

        windowsArray.add( windowObject );
    }

    private void appendWindowDatasources( JsonObject windowObject, WindowRenderingTrace window )
    {
        windowObject.add( "datasources", createDatasources( window.getDatasourceExecutionTraces() ) );
    }


    private JsonArray createDatasources( List<DatasourceExecutionTrace> datasources )
    {
        JsonArray datasourceArray = new JsonArray();

        for ( DatasourceExecutionTrace datasource : datasources )
        {
            createDatasource( datasourceArray, datasource );
        }

        return datasourceArray;
    }

    private void createDatasource( JsonArray jsonArray, DatasourceExecutionTrace datasource )
    {
        JsonObject datasourceObject = new JsonObject();
        datasourceObject.addProperty( "name", datasource.getMethodName() );
        datasourceObject.addProperty( "start_time", datasource.getDuration().getStartTime().toString() );
        datasourceObject.addProperty( "end_time", datasource.getDuration().getStopTime().toString() );
        datasourceObject.addProperty( "time", datasource.getDuration().getExecutionTimeInMilliseconds() );

        jsonArray.add( datasourceObject );
    }

}

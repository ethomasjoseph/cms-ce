package com.enonic.cms.portal.xtrace;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.enonic.cms.core.security.user.QualifiedUsername;
import com.enonic.cms.portal.livetrace.DatasourceExecutionTrace;
import com.enonic.cms.portal.livetrace.InstructionPostProcessingTrace;
import com.enonic.cms.portal.livetrace.PageRenderingTrace;
import com.enonic.cms.portal.livetrace.WindowRenderingTrace;

// TODO: Calculate accurate start times and end times
public class JsonSerializer
{
    private PageRenderingTrace pageTrace;

    private long timeZero = 0;

    public String serialize( PageRenderingTrace pageRenderingTrace )
    {
        this.pageTrace = pageRenderingTrace;
        this.timeZero = pageRenderingTrace.getDuration().getStartTime().getMillis();

        JsonObject wrapper = new JsonObject();

        JsonObject xtrace = new JsonObject();
        appendVersion( xtrace );
        appendPage( xtrace );

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

    private void appendPage( JsonObject xtrace )
    {
        xtrace.add( "page", createPage() );
    }

    private JsonObject createPage()
    {
        JsonObject pageObject = new JsonObject();

        pageObject.addProperty( "key", "TODO" );
        pageObject.addProperty( "name", pageTrace.getPortalRequestTrace().getUrl() );
        pageObject.addProperty( "cacheable", "TODO" );
        pageObject.addProperty( "cache_hit", pageTrace.isUsedCachedResult() );
        pageObject.addProperty( "page_template_name", "TODO" );
        pageObject.addProperty( "start_time", 0 );
        pageObject.addProperty( "stop_time", pageTrace.getDuration().getStopTime().getMillis() - timeZero );
        pageObject.addProperty( "total_time", pageTrace.getDuration().getExecutionTimeInMilliseconds() );
        pageObject.addProperty( "ran_as_user", resolveQualifiedUsernameAsString( pageTrace.getRenderer() ) );

        appendPageDatasources( pageObject );
        appendXsltTransformingObjectForPage( pageObject );
        appendWindows( pageObject );

        pageObject.add( "instruction_post_processing",
                        createInstructionPostProcessingObject( pageTrace.getInstructionPostProcessingTrace() ) );

        return pageObject;
    }

    private void appendPageDatasources( JsonObject page )
    {
        if ( !pageTrace.isUsedCachedResult() )
        {
            page.add( "datasources", createDatasources( pageTrace.getDatasourceExecutionTraces() ) );
        }
    }

    private void appendWindows( JsonObject page )
    {
        page.add( "windows", createWindows( pageTrace.getWindowRenderingTraces() ) );
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

    private void createWindow( JsonArray windowsArray, WindowRenderingTrace windowTrace )
    {
        JsonObject windowObject = new JsonObject();
        windowObject.addProperty( "key", "TODO" );
        windowObject.addProperty( "name", windowTrace.getPortletName() );
        windowObject.addProperty( "cacheable", "TODO" );
        windowObject.addProperty( "cache_hit", windowTrace.isUsedCachedResult() );
        windowObject.addProperty( "start_time", windowTrace.getDuration().getStartTime().getMillis() - timeZero );
        windowObject.addProperty( "stop_time", windowTrace.getDuration().getStopTime().getMillis() - timeZero );
        windowObject.addProperty( "total_time", windowTrace.getDuration().getExecutionTimeInMilliseconds() );
        windowObject.addProperty( "ran_as_user", resolveQualifiedUsernameAsString( windowTrace.getRenderer() ) );

        appendWindowDatasources( windowObject, windowTrace );
        appendXsltTransformingObjectForWindow( windowObject, windowTrace );

        windowObject.add( "instruction_post_processing",
                          createInstructionPostProcessingObject( windowTrace.getInstructionPostProcessingTrace() ) );

        windowsArray.add( windowObject );
    }

    private void appendXsltTransformingObjectForPage( JsonObject pageObject )
    {
        if ( !pageTrace.isUsedCachedResult() )
        {
            long startTime = pageTrace.hasDatasourceExecutionTraces() ? pageTrace.getDatasourceExecutionTraces().get(
                pageTrace.getDatasourceExecutionTraces().size() - 1 ).getDuration().getStopTime().getMillis() - timeZero : 0;

            long endTime = pageTrace.getInstructionPostProcessingTrace().getDuration().getStartTime().getMillis() - timeZero;

            JsonObject xsltTransformingObject = new JsonObject();
            xsltTransformingObject.addProperty( "start_time", startTime );
            xsltTransformingObject.addProperty( "stop_time", endTime );
            xsltTransformingObject.addProperty( "total_time", endTime - startTime );
            pageObject.add( "xslt_transforming", xsltTransformingObject );
        }
    }

    private void appendXsltTransformingObjectForWindow( JsonObject windowObject, WindowRenderingTrace windowTrace )
    {
        if ( !windowTrace.isUsedCachedResult() )
        {
            long startTime = windowTrace.hasDatasourceExecutionTraces() ? windowTrace.getDatasourceExecutionTraces().get(
                windowTrace.getDatasourceExecutionTraces().size() - 1 ).getDuration().getStopTime().getMillis() - timeZero
                : windowTrace.getDuration().getStartTime().getMillis() - timeZero;

            long endTime = windowTrace.getInstructionPostProcessingTrace().getDuration().getStartTime().getMillis() - timeZero;

            JsonObject xsltTransformingObject = new JsonObject();
            xsltTransformingObject.addProperty( "start_time", startTime );
            xsltTransformingObject.addProperty( "stop_time", endTime );
            xsltTransformingObject.addProperty( "total_time", endTime - startTime );
            windowObject.add( "xslt_transforming", xsltTransformingObject );
        }
    }

    private JsonObject createInstructionPostProcessingObject( InstructionPostProcessingTrace trace )
    {
        JsonObject object = new JsonObject();
        object.addProperty( "total_time", trace.getDuration().getExecutionTimeInMilliseconds() );
        object.addProperty( "start_time", trace.getDuration().getStartTime().getMillis() - timeZero );
        object.addProperty( "stop_time", trace.getDuration().getStopTime().getMillis() - timeZero );
        return object;
    }

    private void appendWindowDatasources( JsonObject windowObject, WindowRenderingTrace window )
    {
        if ( !window.isUsedCachedResult() )
        {
            windowObject.add( "datasources", createDatasources( window.getDatasourceExecutionTraces() ) );
        }
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
        datasourceObject.addProperty( "start_time", datasource.getDuration().getStartTime().getMillis() - timeZero );
        datasourceObject.addProperty( "stop_time", datasource.getDuration().getStopTime().getMillis() - timeZero );
        datasourceObject.addProperty( "total_time", datasource.getDuration().getExecutionTimeInMilliseconds() );

        jsonArray.add( datasourceObject );
    }

    private String resolveQualifiedUsernameAsString( QualifiedUsername qualifiedUsername )
    {
        String userStoreName = "";
        if ( qualifiedUsername.getUserStoreName() != null )
        {
            userStoreName = qualifiedUsername.getUserStoreName() + "\\";
        }

        return userStoreName + qualifiedUsername.getUsername();
    }

    private long calculateTotalExecutionTimeForWindows( List<WindowRenderingTrace> windows )
    {
        long total = 0;
        for ( WindowRenderingTrace window : windows )
        {
            total += window.getDuration().getExecutionTimeInMilliseconds();
        }

        return total;
    }

    private long calculateTotalExecutionTimeForDatasources( List<DatasourceExecutionTrace> datasources )
    {
        long total = 0;
        for ( DatasourceExecutionTrace datasource : datasources )
        {
            total += datasource.getDuration().getExecutionTimeInMilliseconds();
        }

        return total;
    }

}

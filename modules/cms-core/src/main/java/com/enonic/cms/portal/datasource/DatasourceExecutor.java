/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.datasource;

import org.jdom.Document;
import org.jdom.Element;

import com.enonic.cms.portal.datasource.context.DatasourcesContextXmlCreator;
import com.enonic.cms.portal.datasource.expressionfunctions.ExpressionContext;
import com.enonic.cms.portal.datasource.methodcall.MethodCall;
import com.enonic.cms.portal.datasource.methodcall.MethodCallFactory;
import com.enonic.cms.portal.livetrace.DatasourceExecutionTrace;
import com.enonic.cms.portal.livetrace.DatasourceExecutionTracer;
import com.enonic.cms.portal.livetrace.LivePortalTraceService;
import com.enonic.cms.portal.rendering.tracing.DataTraceInfo;
import com.enonic.cms.portal.rendering.tracing.RenderTrace;

public class DatasourceExecutor
{
    private DatasourceExecutorContext context;

    private DatasourcesContextXmlCreator datasourcesContextXmlCreator;

    private LivePortalTraceService livePortalTraceService;

    private DatasourceExecutionTrace trace;

    public DatasourceExecutor( DatasourceExecutorContext datasourceExecutorContext )
    {
        this.context = datasourceExecutorContext;
    }

    public Document getDataSourceResult( Datasources datasources )
    {
        Document resultDoc = new Document( new Element( resolveResultRootElementName( datasources ) ) );
        Element verticaldataEl = resultDoc.getRootElement();

        Element contextEl = datasourcesContextXmlCreator.createContextElement( datasources, context );
        verticaldataEl.addContent( contextEl );

        // execute data sources
        for ( Datasource datasource : datasources.getDatasourceElements() )
        {
            trace =
                DatasourceExecutionTracer.startTracing( context.getDatasourcesType(), datasource.getMethodName(), livePortalTraceService );
            try
            {
                DatasourceExecutionTracer.traceRunnableCondition( trace, datasource.getCondition() );

                boolean runnableByCondition = isRunnableByCondition( datasource );
                DatasourceExecutionTracer.traceIsExecuted( trace, runnableByCondition );
                if ( runnableByCondition )
                {
                    Document datasourceResultDocument = executeMethodCall( datasource );
                    verticaldataEl.addContent( datasourceResultDocument.getRootElement().detach() );
                }
            }
            finally
            {
                DatasourceExecutionTracer.stopTracing( trace, livePortalTraceService );
            }
        }

        setTraceDataSourceResult( resultDoc );

        return resultDoc;
    }

    /**
     * Checks the condition attribute of the xml element, and evaluates it to decide if the datasource should be run. Returns true if
     * condition does not exists, is empty, contains 'true' or evaluates to true.
     */
    protected boolean isRunnableByCondition( final Datasource datasource )
    {
        // Note: Made protected to enable testing. Should normally be tested through public methods

        if ( datasource.getCondition() == null || datasource.getCondition().equals( "" ) )
        {
            return true;
        }

        try
        {
            ExpressionContext expressionFunctionsContext = new ExpressionContext();
            expressionFunctionsContext.setSite( context.getSite() );
            expressionFunctionsContext.setMenuItem( context.getMenuItem() );
            expressionFunctionsContext.setContentFromRequest( context.getContentFromRequest() );
            expressionFunctionsContext.setUser( context.getUser() );
            expressionFunctionsContext.setPortalInstanceKey( context.getPortalInstanceKey() );
            expressionFunctionsContext.setLocale( context.getLocale() );
            expressionFunctionsContext.setDeviceClass( context.getDeviceClass() );
            expressionFunctionsContext.setPortletWindowRenderedInline( context.isPortletWindowRenderedInline() );

            ExpressionFunctionsExecutor expressionExecutor = new ExpressionFunctionsExecutor();
            expressionExecutor.setExpressionContext( expressionFunctionsContext );
            expressionExecutor.setHttpRequest( context.getHttpRequest() );
            expressionExecutor.setRequestParameters( context.getRequestParameters() );

            String evaluatedExpression = expressionExecutor.evaluate( datasource.getCondition() );
            return evaluatedExpression.equals( "true" );
        }
        catch ( Exception e )
        {
            throw new DatasourceException( "Failed to evaluate expression", e );
        }

    }


    private String resolveResultRootElementName( Datasources datasources )
    {
        final String name = datasources.getResultRootName();

        if ( name != null && name.length() > 0 )
        {
            return name;
        }
        return context.getDefaultResultRootElementName();
    }

    private Document executeMethodCall( Datasource datasource )
    {
        MethodCall methodCall = MethodCallFactory.create( context, datasource );

        DatasourceExecutionTracer.traceMethodCall( methodCall, trace );

        return executeMethodCall( datasource, methodCall );
    }

    private Document executeMethodCall( final Datasource datasource, final MethodCall methodCall )
    {
        Document document = methodCall.invoke();

        Document jdomDocument = (Document) document.clone();

        if ( datasource.getResultElementName() != null )
        {
            Element originalRootEl = jdomDocument.getRootElement();
            Element wrappingResultElement = new Element( datasource.getResultElementName() );
            wrappingResultElement.addContent( originalRootEl.detach() );
            jdomDocument = new Document( wrappingResultElement );
        }

        return jdomDocument;
    }

    private void setTraceDataSourceResult( Document result )
    {
        DataTraceInfo info = RenderTrace.getCurrentDataTraceInfo();
        if ( info != null )
        {
            info.setDataSourceResult( result );
        }
    }

    private Element getOrCreateElement( Element parent, String name )
    {
        Element elem = parent.getChild( name );
        if ( elem != null )
        {
            return elem;
        }

        elem = new Element( name );
        parent.addContent( elem );
        return elem;
    }


    public void setContext( DatasourceExecutorContext value )
    {
        this.context = value;
    }

    public void setDatasourcesContextXmlCreator( DatasourcesContextXmlCreator datasourcesContextXmlCreator )
    {
        this.datasourcesContextXmlCreator = datasourcesContextXmlCreator;
    }

    public void setLivePortalTraceService( LivePortalTraceService livePortalTraceService )
    {
        this.livePortalTraceService = livePortalTraceService;
    }
}

/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.itest.portal.datasource.expressionfunctions;

import com.enonic.cms.core.RequestParameters;
import com.enonic.cms.core.portal.datasource.ExpressionFunctionsExecutor;
import com.enonic.cms.core.portal.datasource.expressionfunctions.ExpressionContext;
import com.enonic.cms.core.portal.datasource.expressionfunctions.ExpressionFunctionsFactory;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.time.MockTimeService;
import com.enonic.cms.itest.AbstractSpringTest;
import com.enonic.cms.itest.util.DomainFixture;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.Assert.assertEquals;

public class ExpressionFunctionsExecutorTest
    extends AbstractSpringTest
{
    @Autowired
    private DomainFixture fixture;

    private MockTimeService timeService;

    private UserEntity defaultUser;

    private ExpressionContext expressionContext;

    private ExpressionFunctionsFactory efFactory;

    private ExpressionFunctionsExecutor efExecutor;


    @Before
    public void before()
    {
        fixture.initSystemData();

        defaultUser = fixture.createAndStoreNormalUserWithUserGroup( "testuser", "testuser", "testuserstore" );

        timeService = new MockTimeService();

        expressionContext = new ExpressionContext();
        expressionContext.setUser( defaultUser );
        SiteEntity site = new SiteEntity();
        site.setKey( 0 );
        expressionContext.setSite( site );

        efFactory = new ExpressionFunctionsFactory();
        efFactory.setTimeService( timeService );
        efFactory.setContext( expressionContext );

        efExecutor = new ExpressionFunctionsExecutor();
        efExecutor.setExpressionContext( expressionContext );
    }

    @Test
    public void testParametersEvaulation()
        throws Exception
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter( "subCat", "18" );
        request.addParameter( "sub-cat", "27" );
        efExecutor.setHttpRequest( request );
        efExecutor.setRequestParameters( new RequestParameters( request.getParameterMap() ) );

        String evaluated18 = efExecutor.evaluate( "${param.subCat}" );
        assertEquals( "18", evaluated18 );

        String evaluated27 = efExecutor.evaluate( "${param['sub-cat']}" );
        assertEquals( "27", evaluated27 );
    }

    @Test
    public void testEvaluateCurrentDateWithTime()
        throws Exception
    {
        timeService.setTimeNow( new DateTime( 2010, 5, 28, 12, 30, 4, 2 ) );

        String evaluted = efExecutor.evaluate( "@publishfrom >= ${currentDate( 'yyyy.MM.dd HH:mm' )}" );
        assertEquals( "@publishfrom >= 2010.05.28 12:30", evaluted );
    }

    @Test
    public void testEvaluateCurrentDateWithoutTime()
        throws Exception
    {
        timeService.setTimeNow( new DateTime( 2010, 5, 28, 12, 30, 4, 2 ) );

        String evaluted = efExecutor.evaluate( "@publishfrom >= ${currentDate( 'yyyy.MM.dd' )}" );
        assertEquals( "@publishfrom >= 2010.05.28", evaluted );
    }

    @Test
    public void testEvaluateCurrentDateMinusOffset()
        throws Exception
    {
        timeService.setTimeNow( new DateTime( 2010, 5, 28, 12, 30, 4, 2 ) );

        String evaluted =
            efExecutor.evaluate( "@publishfrom >= ${currentDateMinusOffset( 'yyyy.MM.dd HH:mm', periodHoursMinutes( 2, 35 ) )}" );
        assertEquals( "@publishfrom >= 2010.05.28 09:55", evaluted );

        evaluted = efExecutor.evaluate( "@publishfrom >= ${currentDateMinusOffset( 'yyyy.MM.dd HH:mm', 'PT2H35M' )}" );
        assertEquals( "@publishfrom >= 2010.05.28 09:55", evaluted );

        // .. and with negative periods

        evaluted = efExecutor.evaluate( "@publishfrom >= ${currentDateMinusOffset( 'yyyy.MM.dd HH:mm', periodHoursMinutes( -2, -35 ) )}" );
        assertEquals( "@publishfrom >= 2010.05.28 15:05", evaluted );

        evaluted = efExecutor.evaluate( "@publishfrom >= ${currentDateMinusOffset( 'yyyy.MM.dd HH:mm', 'PT-2H-35M' )}" );
        assertEquals( "@publishfrom >= 2010.05.28 15:05", evaluted );
    }

    @Test
    public void testEvaluateCurrentDatePlusOffset()
        throws Exception
    {
        timeService.setTimeNow( new DateTime( 2010, 5, 28, 12, 30, 4, 2 ) );

        String evaluted =
            efExecutor.evaluate( "@publishfrom >= ${currentDatePlusOffset( 'yyyy.MM.dd HH:mm', periodHoursMinutes( 2, 5 ) )}" );
        assertEquals( "@publishfrom >= 2010.05.28 14:35", evaluted );

        evaluted = efExecutor.evaluate( "@publishfrom >= ${currentDatePlusOffset( 'yyyy.MM.dd HH:mm', 'PT2H5M' )}" );
        assertEquals( "@publishfrom >= 2010.05.28 14:35", evaluted );

        // .. and with negative periods

        evaluted = efExecutor.evaluate( "@publishfrom >= ${currentDatePlusOffset( 'yyyy.MM.dd HH:mm', periodHoursMinutes( -2, -5 ) )}" );
        assertEquals( "@publishfrom >= 2010.05.28 10:25", evaluted );

        evaluted = efExecutor.evaluate( "@publishfrom >= ${currentDatePlusOffset( 'yyyy.MM.dd HH:mm', 'PT-2H-5M' )}" );
        assertEquals( "@publishfrom >= 2010.05.28 10:25", evaluted );
    }

    @Test
    public void testEvaluatePositiveDurationHoursMinutes()
        throws Exception
    {
        timeService.setTimeNow( new DateTime( 2010, 5, 28, 12, 30, 4, 2 ) );

        String evaluted = efExecutor.evaluate( "${periodHoursMinutes( 2, 5 )}" );
        assertEquals( "PT2H5M", evaluted );
    }

    @Test
    public void testEvaluateNegativeDurationHoursMinutes()
        throws Exception
    {
        timeService.setTimeNow( new DateTime( 2010, 5, 28, 12, 30, 4, 2 ) );

        String evaluted = efExecutor.evaluate( "${periodHoursMinutes( -2, -5 )}" );
        assertEquals( "PT-2H-5M", evaluted );
    }

    @Test
    public void testPortalSiteKey()
        throws Exception
    {
        String evaluted = efExecutor.evaluate( "${portal.siteKey}" );
        assertEquals( "0", evaluted );
    }
}

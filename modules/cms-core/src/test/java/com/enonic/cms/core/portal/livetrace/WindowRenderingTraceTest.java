package com.enonic.cms.core.portal.livetrace;


import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class WindowRenderingTraceTest
{
    @Test
    public void isConcurrencyBlocked_returns_false_when_concurrency_block_timer_not_stopped()
    {
        WindowRenderingTrace trace = new WindowRenderingTrace( null, null );
        trace.startConcurrencyBlockTimer();
        assertFalse( trace.isConcurrencyBlocked() );
    }

    @Test
    public void isConcurrencyBlocked_returns_true_when_concurrency_block_was_timed_to_be_larger_than_threshold()
    {
        WindowRenderingTrace trace = new WindowRenderingTrace( null, null );
        trace.startConcurrencyBlockTimer();
        try
        {
            Thread.sleep( 50 );
        }
        catch ( InterruptedException e )
        {
            e.printStackTrace();
        }
        trace.stopConcurrencyBlockTimer();
        assertTrue( trace.isConcurrencyBlocked() );
    }

    @Test
    public void getConcurrencyBlockingTime_returns_zero_when_concurrency_block_timer_not_stopped()
    {
        WindowRenderingTrace trace = new WindowRenderingTrace( null, null );
        trace.startConcurrencyBlockTimer();
        assertTrue( trace.getConcurrencyBlockingTime() == 0 );
    }

    @Test
    public void getConcurrencyBlockingTime_returns_zero_when_concurrency_block_was_timed_to_be_less_than_threshold()
    {
        WindowRenderingTrace trace = new WindowRenderingTrace( null, null );
        trace.startConcurrencyBlockTimer();
        try
        {
            Thread.sleep( 1 );
        }
        catch ( InterruptedException e )
        {
            e.printStackTrace();
        }
        trace.stopConcurrencyBlockTimer();
        assertTrue( trace.getConcurrencyBlockingTime() == 0 );
    }

    @Test
    public void getConcurrencyBlockingTime_returns_larger_than_zero_when_concurrency_block_was_timed_to_be_larger_than_threshold()
    {
        WindowRenderingTrace trace = new WindowRenderingTrace( null, null );
        trace.startConcurrencyBlockTimer();
        try
        {
            Thread.sleep( 50 );
        }
        catch ( InterruptedException e )
        {
            e.printStackTrace();
        }
        trace.stopConcurrencyBlockTimer();
        assertTrue( trace.getConcurrencyBlockingTime() >= 50 );
    }
}

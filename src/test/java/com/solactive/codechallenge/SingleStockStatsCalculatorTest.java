package com.solactive.codechallenge;

import com.solactive.codechallenge.calculator.StatsCalculator;
import junit.framework.TestCase;

public class SingleStockStatsCalculatorTest extends TestCase {

    final StatsCalculator calculator = new StatsCalculator("FOO");

    public void testInstId() {
        assertEquals(calculator.instId, "FOO");
    }

    public void testEmpty() {
        assertNull(calculator.currentStats());
    }

    public void testShowingExpiredEventIsNop() {
        calculator.show(StatsCalculator.WINDOW_MILLIS, StatsCalculator.WINDOW_MILLIS - 1, 1.0f);
        calculator.show(StatsCalculator.WINDOW_MILLIS + 1, 0, 100.0f);

        final var stats = calculator.currentStats();
        assertEquals(1.f, stats.avg);
        assertEquals(1.f, stats.min);
        assertEquals(1.f, stats.max);
        assertEquals(1, stats.count);
    }

    public void testSimpleEvents() {
        // arithmetic series...

        var n = 100000;
        var a_1 = 1;
        var a_n = a_1 + n - 1;

        for (int a_i = a_1; a_i <= a_n; a_i++)
            calculator.show(0, 0, a_i);

        final var stats = calculator.currentStats();

        var sum = (float)n / 2 * (a_1 + a_n);
        assertEquals(sum / n, stats.avg);
        assertEquals((float) a_1, stats.min);
        assertEquals((float) a_n, stats.max);
        assertEquals(n, stats.count);
    }

    public void testOutOfOrderEvents() {
        for (int i = 1; i <= 10; i++)
            calculator.show(10, 10 - i, i);

        final var stats = calculator.currentStats();

        var sum = 10.f / 2 * (1 + 10);
        assertEquals(sum / 10, stats.avg);
        assertEquals(1.f, stats.min);
        assertEquals(10.f, stats.max);
        assertEquals(10, stats.count);
    }
}

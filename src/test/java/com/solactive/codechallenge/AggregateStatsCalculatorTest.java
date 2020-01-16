package com.solactive.codechallenge;

import com.solactive.codechallenge.calculator.StatsCalculatorAggregate;
import com.solactive.codechallenge.json.StatisticsMsg;
import junit.framework.TestCase;

public class AggregateStatsCalculatorTest extends TestCase {
    public void testReferentialMapping() {
        final var map = new StockStatsDb();
        assertEquals(0, map.inernalId("MSFT"));
        assertEquals(0, map.inernalId("MSFT"));
        assertEquals(1, map.inernalId("GOOG"));
        assertEquals(0, map.inernalId("MSFT"));
        assertEquals(1, map.inernalId("GOOG"));
    }

    public void testNonExistentInternalIdYieldsNullCalculator() {
        final var map = new StockStatsDb();
        assertNull(map.stockStats(1000));
    }

    public void testCalculator() {
        final var map = new StockStatsDb();
        var msft = map.stockStats(map.inernalId("MSFT"));
        assertNotNull(msft);
        assertEquals(
            new StatisticsMsg(1.f, 1.f, 1.f, 1),
            msft.show(0, 0, 1.f)
        );
    }

    public void testCalculatorAggregationOneStock() {
        final var map = new StockStatsDb();
        final var aggStats = new StatsCalculatorAggregate(map);

        var msft = map.stockStats(map.inernalId("MSFT"));
        assertEquals(1, map.allStockStats().size());

        msft.show(0, 0, 1.f);
        aggStats.recalculate();

        assertEquals(msft.currentStats(), aggStats.currentStats());
    }

    public void testCalculatorAggregation() {
        final var map = new StockStatsDb();
        final var aggStats = new StatsCalculatorAggregate(map);

        var msft = map.stockStats(map.inernalId("MSFT"));
        assertEquals(1, map.allStockStats().size());
        var goog = map.stockStats(map.inernalId("GOOG"));
        assertEquals(2, map.allStockStats().size());

        msft.show(0, 0, 1.f);
        aggStats.recalculate();

        goog.show(0, 0, 2.f);
        aggStats.recalculate();

        assertEquals(
            new StatisticsMsg(1.5f, 2.f, 1.f, 2),
            aggStats.currentStats()
        );
    }
}

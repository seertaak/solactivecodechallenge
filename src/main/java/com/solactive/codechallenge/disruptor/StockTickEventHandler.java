package com.solactive.codechallenge.disruptor;

import com.lmax.disruptor.EventHandler;
import com.solactive.codechallenge.calculator.StatsAggregator;
import com.solactive.codechallenge.calculator.StockToStatsCalculatorMap;

public class StockTickEventHandler implements EventHandler<StockTickEvent> {
    private final StockToStatsCalculatorMap _map;
    private final StatsAggregator _aggStats;

    public StockTickEventHandler(final StockToStatsCalculatorMap map, final StatsAggregator aggStats) {
        _map = map;
        _aggStats = aggStats;
    }

    @Override
    public void onEvent(StockTickEvent stockTickEvent, long l, boolean b) throws Exception {
        final var stockStats = _map.stockStats(stockTickEvent.internalStockId);

        // Below should never happen, because code which handles stock tick messages first
        // creates the stock stockStats before passing on the StockTickEvent.
        if (stockStats == null)
            throw new RuntimeException("Stock tick event for unknown stock with internal ID: " + stockTickEvent.internalStockId);

        final var now = System.currentTimeMillis();

        stockStats.show(now, stockTickEvent.timestamp, stockTickEvent.price);
        _aggStats.recalculate();
    }
}

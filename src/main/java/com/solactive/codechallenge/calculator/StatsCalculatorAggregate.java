package com.solactive.codechallenge.calculator;

import com.solactive.codechallenge.StockStatsDb;
import com.solactive.codechallenge.json.StatisticsMsg;

import java.util.ArrayList;
import java.util.List;

public class StatsCalculatorAggregate {
    private final StockStatsDb _refMapping;

    private final List<StatisticsMsg> _latestStockStats = new ArrayList<>(StockStatsDb.MAX_STOCKS);

    public StatsCalculatorAggregate(StockStatsDb refMapping) {
        _refMapping = refMapping;

        for (int i = 0; i < StockStatsDb.MAX_STOCKS; i++)
            _latestStockStats.add(null);
    }

    private volatile float _avg = 0f; // we want more precision since it's calculated by summing.
    private volatile float _min = Float.MAX_VALUE;
    private volatile float _max = 0f;
    private volatile int _count = 0;

    public void recalculate() {
        var count = 0;
        var avg = 0.0;
        var min = Float.MAX_VALUE;
        var max = 0.f;

        final var allStockStats = _refMapping.allStockStats();

        for (final var stockStats: allStockStats) {
            final var stats = stockStats.currentStats();
            if (stats == null) continue;

            count += stats.count;
            avg += stats.avg;
            min = Math.min(min, stats.min);
            max = Math.max(max, stats.max);
        }

        // publish the values.
        _count = count;
        _avg = (float) avg / (float) allStockStats.size();
        _min = min;
        _max = max;
    }

    public StatisticsMsg currentStats() {
        return new StatisticsMsg(_avg, _max, _min, _count);
    }
}

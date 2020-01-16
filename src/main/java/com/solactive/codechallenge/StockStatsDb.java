package com.solactive.codechallenge;

import com.solactive.codechallenge.calculator.StatsCalculatorSingleStock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class StockStatsDb {
    public static final int MAX_STOCKS = 50000;

    private final AtomicInteger _lastInternalId = new AtomicInteger(0);

    private Map<String, Integer> _instIdToInternalId = new HashMap<>();
    private List<StatsCalculatorSingleStock> _stats = new ArrayList<>(MAX_STOCKS);

    public synchronized int inernalId(String instId) {
        var internalId = _instIdToInternalId.get(instId);
        if (internalId == null) {
            internalId = _lastInternalId.getAndIncrement();
            _instIdToInternalId.put(instId, internalId);
            _stats.add(new StatsCalculatorSingleStock(instId));
        }
        return internalId;
    }

    // NB: no need to synchronize since we have pre-allocated the storage for _stats
    // which means we never need to grow the list.
    public StatsCalculatorSingleStock stockStats(int internalId) {
        if (internalId >= _stats.size()) return null;
        return _stats.get(internalId);
    }

    public List<StatsCalculatorSingleStock> allStockStats() { return _stats; }
}

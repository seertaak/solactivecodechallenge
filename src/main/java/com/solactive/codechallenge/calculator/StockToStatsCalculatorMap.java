package com.solactive.codechallenge.calculator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class StockToStatsCalculatorMap {
    public static final int MAX_STOCKS = 50000;

    private final AtomicInteger _lastInternalId = new AtomicInteger(0);

    private Map<String, Integer> _instIdToInternalId = new HashMap<>();
    private List<StatsCalculator> _stats = new ArrayList<>(MAX_STOCKS);

    public synchronized int internalId(String instId) {
        var internalId = _instIdToInternalId.get(instId);
        if (internalId == null) {
            internalId = _lastInternalId.getAndIncrement();
            _instIdToInternalId.put(instId, internalId);
            _stats.add(new StatsCalculator(instId));
        }
        return internalId;
    }

    // NB: no need to synchronize since a) we have pre-allocated the storage for _stats
    // which means we never need to grow the list, and b) _stats.size() is monotonically
    // increasing, c) because this is likely to run on Intel/AMD 64-bit architecture which
    // has strong memory model (in other words, the last operation in ArrayList.add(), which
    // is to bump up the 'size' member variable, will automatically be "published" to other
    // threads.
    public StatsCalculator stockStats(int internalId) {
        if (internalId >= _stats.size()) return null;
        return _stats.get(internalId);
    }

    // Below: used by StatsAggregator.
    public List<StatsCalculator> allStockStats() { return _stats; }
}

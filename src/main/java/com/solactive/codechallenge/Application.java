package com.solactive.codechallenge;

import com.lmax.disruptor.InsufficientCapacityException;
import com.lmax.disruptor.LiteTimeoutBlockingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;
import com.solactive.codechallenge.calculator.StatsCalculatorAggregate;
import com.solactive.codechallenge.json.StatisticsMsg;
import com.solactive.codechallenge.json.TicksMsg;

import java.util.concurrent.TimeUnit;

public class Application {
    public static final int DEF_BUFFER_CAPACITY = 1 << 22; // ~ = 1GB memory, allows 33.6MM messages in queue.

    private final Disruptor<StockTickEvent> _disruptor;
    private final StockStatsDb _instIdToStockStats;
    private final StatsCalculatorAggregate _aggregateStats;

    public Application() {
        this(DEF_BUFFER_CAPACITY);
    }

    public Application(int bufferSize) {
        _disruptor = new Disruptor<>(
                StockTickEvent::new,
                bufferSize,
                DaemonThreadFactory.INSTANCE,
                ProducerType.MULTI,
                new LiteTimeoutBlockingWaitStrategy(1, TimeUnit.MICROSECONDS)
        );

        _instIdToStockStats = new StockStatsDb();
        _aggregateStats = new StatsCalculatorAggregate(_instIdToStockStats);

        _disruptor.handleEventsWith(new StockTickEventHandler(_instIdToStockStats, _aggregateStats));
        _disruptor.start();
    }

    public StatisticsMsg currStockStats(String instId) {
        final var internalStockId = _instIdToStockStats.inernalId(instId);
        final var stockStats = _instIdToStockStats.stockStats(internalStockId);
        return stockStats != null ? stockStats.currentStats() : null;
    }

    public StatisticsMsg currAggregateStats() {
        return _aggregateStats.currentStats();
    }

    public void showTicksMsg(final TicksMsg msg) {
        // Preliminary definition: in the paragraph below, "consumer" refers to
        // per-stock code which calculates how our statistics should update after
        // having been shown the event.
        //
        // Remark:
        // We want to do very little in this call: basically we simply offer the message to
        // a ring buffer - c'est tout. This ensures that this method returns quickly,
        // which in "real life" may well be connected to, say, a Solar Flare card
        // with its own (small!) fixed-size buffer. Also, if we have huge bursts of
        // messages, in this approach, the system degrades gracefully. To wit, what
        // OUGHT to happen (although due to time concerns, I didn't stress test)
        // is that the oldest messages are simply dropped from the ring buffer
        // resulting in not-totally-accurate measurements, but Martin Percossi's
        // opinion this is a fair trade-off, because the alternative is that the
        // entire system gets "backed up", and then the problems PERSIST even after
        // the burst of messages has subsided. This way, once the burst is gone,
        // our accuracy reverts to perfect. Also, this approach lets the disruptor
        // perform the load balancing; that is, it is the responsibility of the
        // disruptor to determine the appropriate number of consumer threads, although
        // one might imagine improvements there, e.g. pinning consumers to CPU threads
        // and playing with OS settings to ensure those guys are NEVER preempted.

        final var buffer = _disruptor.getRingBuffer();

        // NOTE: internalId() blocks, but is O(1) and the time ought to be negligible.
        final var internalStockId = _instIdToStockStats.inernalId(msg.instrument);

        // Now: put the message in the ring buffer for consumers (i.e. stock stat
        // calculators) to update their state.
        try {
            long sequence = buffer.tryNext();
            try {
                var event = buffer.get(sequence);
                event.internalStockId = internalStockId;
                event.price = msg.price;
                event.timestamp = msg.timestamp;
            } finally {
                buffer.publish(sequence);
            }
        } catch (InsufficientCapacityException e) {
            // really we should log the error here, or keep or something... TODO!
        }
    }
}

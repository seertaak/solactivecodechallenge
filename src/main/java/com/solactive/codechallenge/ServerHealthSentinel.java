package com.solactive.codechallenge;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This is only partially done due to time constraints. The idea is this would offer
 * an obviously thread-safe API for the major events in the system. (See below.)
 * Separately a scheduled executor runs every minute printing information about the
 * health of the system.
 */
public class ServerHealthSentinel {

    public void start() {
        Executors.newScheduledThreadPool(1)
                .scheduleAtFixedRate(
                        () -> tickTock(),
                        1,
                        1,
                        TimeUnit.MINUTES
                );
    }

    public void tickTock() {
        // TODO: in here, track things like. Use StatsCalculator to print minute-by-minute stats, we already
        //       wrote it, might as well use it!
        //       1. number of Ticks POST requests processed correctly, and dropped due to being malformed,
        //       2. number of statistics GET messages processed, perhaps by-stock.
        //       3. number of messages accepted (throughput) by disruptor, number rejected.
        //       4. memory stats. there's some JVM api, would have to look it up.
        //       5. GC pauses.
        //       6. Moon phase and alignment of planets.
    }
}

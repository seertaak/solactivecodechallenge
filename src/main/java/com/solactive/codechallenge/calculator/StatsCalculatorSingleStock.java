package com.solactive.codechallenge.calculator;

import com.solactive.codechallenge.json.StatisticsMsg;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.PriorityQueue;

public class StatsCalculatorSingleStock {
    public final String instId;

    private static class Slot implements Comparable<Slot> {
        public final float price;
        public final long time;

        private Slot(long time, float price) {
            this.time = time;
            this.price = price;
        }

        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
        }

        @Override
        public int compareTo(Slot that) {
            return (int) (this.time - that.time);
        }
    }

    public static final long WINDOW_MILLIS = 60*1000;

    public StatsCalculatorSingleStock(final String instId) {
        this.instId = instId;
    }

    private volatile boolean _noDataYet = true;
    private volatile float _avg = 0f; // we want more precision since it's calculated by summing.
    private volatile float _min = 0f;
    private volatile float _max = 0f;
    private volatile int _count = 0;

    final private PriorityQueue<Slot> _buffer = new PriorityQueue<>();

    public StatisticsMsg show(final long now, final long time, final float price) {
        if (time < now - StatsCalculatorSingleStock.WINDOW_MILLIS || price <= 0)
            return null;

        synchronized (this) {
            // out with the old...
            while (!_buffer.isEmpty() && _buffer.peek().time < now - WINDOW_MILLIS)
                _buffer.poll();

            // ... in with the new.
            _buffer.offer(new Slot(time, price));

            final var count = _buffer.size();

            var avg = 0.0;
            var min = Float.MAX_VALUE;
            var max = 0.f;

            for (final var msg: _buffer) {
                final var prc = msg.price;
                avg += prc;
                min = Math.min(min, prc);
                max = Math.max(max, prc);
            }

            avg /= count;

            // publish the values.
            _count = count;
            _avg = (float) avg;
            _min = min;
            _max = max;
            _noDataYet = false;

            return new StatisticsMsg((float) avg, max, min, count);
        }
    }

    public StatisticsMsg currentStats() {
        if (_noDataYet) return null;
        return new StatisticsMsg(_avg, _max, _min, _count);
    }
}

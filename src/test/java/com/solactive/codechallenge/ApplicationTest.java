package com.solactive.codechallenge;

import com.solactive.codechallenge.json.StatisticsMsg;
import com.solactive.codechallenge.json.TicksMsg;
import junit.framework.TestCase;

public class ApplicationTest extends TestCase {
    public void testInstantiation() {
        final var app = new Application(1024); // whatever

        final var now = System.currentTimeMillis();
        app.showTicksMsg(new TicksMsg("MSFT", 1.f, now));

        // TODO: make below more robust.
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }

        assertEquals(
                new StatisticsMsg(1.f, 1.f, 1.f, 1),
                app.currStockStats("MSFT")
        );
    }

    public void testSaturation() {
        final var app = new Application(128); // small.

        final var now = System.currentTimeMillis();

        final var n = 128*100000;

        // let's just smother it with the same event. Is practically guaranteed to overflow.
        for (int i = 0; i < n; i++)
            app.showTicksMsg(new TicksMsg("MSFT", 1.f, now));

        // TODO: make below more robust.
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }

        final var stats = app.currStockStats("MSFT");

        assertEquals(
                1.f,
                stats.avg
        );

        assertTrue(stats.count < n);
    }
}

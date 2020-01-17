package com.solactive.codechallenge;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;

import com.solactive.codechallenge.json.StatisticsMsg;
import com.solactive.codechallenge.json.TicksMsg;
import org.glassfish.grizzly.http.server.HttpServer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;

public class IntegrationTest {

    private HttpServer server;
    private WebTarget target;

    @Before
    public void setUp() throws Exception {
        // start the server
        server = Main.startServer();
        // create the client
        Client c = ClientBuilder.newClient();

        // uncomment the following line if you want to enable
        // support for JSON in the client (you also have to uncomment
        // dependency on jersey-media-json module in pom.xml and Main.startServer())
        // --
        // c.configuration().enable(new org.glassfish.jersey.media.json.JsonJaxbFeature());

        target = c.target(Main.BASE_URI);
    }

    @After
    public void tearDown() throws Exception {
        server.stop();
    }

    @Test
    public void testSinglePostGet() {
        var msg = new TicksMsg("TSLA", 100.f, System.currentTimeMillis());
        var status = target.path("/ticks").request().post(Entity.json(msg)).getStatus();
        assertEquals(201, status);
        assertEquals(
                new StatisticsMsg(100.f, 100f, 100f, 1),
                target.path("/statistics/IBM.N").request().get().readEntity(StatisticsMsg.class)
        );
    }

    @Test
    public void testAggStats() {
        final var t = System.currentTimeMillis();
        target.path("/ticks").request().post(Entity.json(new TicksMsg("IBM.N", 100.f, t)));
        target.path("/ticks").request().post(Entity.json(new TicksMsg("MSFT", 50.f, t)));

        assertEquals(
                new StatisticsMsg(75.f, 100f, 50f, 2),
                target.path("/statistics").request().get().readEntity(StatisticsMsg.class)
        );
    }

    @Test
    public void testStream() {
        final var t = System.currentTimeMillis();
        final var T = t + 10*1000;
        final var random = new Random();

        var sum = 0.;
        var count = 0;
        var min = Float.MAX_VALUE;
        var max = 0.f;

        for (var u = t; u < T; u += random.nextInt(50)) {
            final var prc = 99.f + 2.f*random.nextFloat();
            sum += prc;
            count++;
            min = Math.min(min, prc);
            max = Math.max(max, prc);

            target.path("/ticks").request().post(Entity.json(
                    new TicksMsg(
                            "GOOG",
                            prc,
                            t
                    )));
        }

        final var epsilon = 1e-2;

        assertEquals(
                new StatisticsMsg((float) (sum/count), max, min, count),
                target.path("/statistics/GOOG").request().get().readEntity(StatisticsMsg.class)
        );
    }
}

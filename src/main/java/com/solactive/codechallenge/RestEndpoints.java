package com.solactive.codechallenge;

import com.solactive.codechallenge.calculator.StatsCalculatorSingleStock;
import com.solactive.codechallenge.json.StatisticsMsg;
import com.solactive.codechallenge.json.TicksMsg;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * All endpoints: /ticks and /statistics.
 */
@Path("/")
public class RestEndpoints {
    public final Random random = new Random();

    private final Map<String, Object> statistics = new ConcurrentHashMap<>();

    @GET
    @Path("/statistics")
    @Produces(MediaType.APPLICATION_JSON)
    public StatisticsMsg getAggregateStatistics() {
        return Main.app.currAggregateStats();
    }

    @GET
    @Path("/statistics/{inst_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public StatisticsMsg getStatistics(@PathParam("inst_id") String instId) {
        return Main.app.currStockStats(instId);
    }

    @POST
    @Path("/ticks")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postTicks(final TicksMsg msg) {
        final long now = System.currentTimeMillis();
        if (msg.timestamp < now - StatsCalculatorSingleStock.WINDOW_MILLIS || msg.instrument == null || msg.price <= 0)
            return Response.status(Response.Status.NO_CONTENT).build();

        Main.app.showTicksMsg(msg);

        return Response.status(Response.Status.CREATED).build();
    }
}

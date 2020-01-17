package com.solactive.codechallenge;

import com.solactive.codechallenge.calculator.StatsCalculator;
import com.solactive.codechallenge.json.StatisticsMsg;
import com.solactive.codechallenge.json.TicksMsg;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * All endpoints: /ticks and /statistics.
 */
@Path("/")
public class RestEndpoints {
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
        if (msg.timestamp < now - StatsCalculator.WINDOW_MILLIS
                || msg.timestamp >= now + 10 // ignore messages from THE FUTURE. (+ 10 is for clock drift)
                || msg.instrument == null
                || msg.price <= 0)
            return Response.status(Response.Status.NO_CONTENT).build();

        Main.app.showTicksMsg(msg);

        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    @Path("/ping")
    public String ping() {
        return "pong";
    }
}

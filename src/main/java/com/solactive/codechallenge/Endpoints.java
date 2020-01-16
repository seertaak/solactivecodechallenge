package com.solactive.codechallenge;

import com.solactive.codechallenge.message.Statistics;
import com.solactive.codechallenge.message.Ticks;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Random;

/**
 * All endpoints: /ticks and /statistics.
 */
@Path("/")
public class Endpoints {
    public final Random random = new Random();

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Path("/statistics")
    @Produces(MediaType.APPLICATION_JSON)
    public Statistics getAggregateStatistics() {
        return new Statistics(random.nextFloat(), random.nextFloat(), random.nextFloat(), random.nextInt());
    }

    @GET
    @Path("/statistics/{inst_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Statistics getStatistics(@PathParam("inst_id") String instId) {
        return new Statistics(random.nextFloat(), random.nextFloat(), random.nextFloat(), random.nextInt());
    }

    @POST
    @Path("/ticks")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postTicks(Ticks msg) {
        return Response.ok().entity(msg).build();
    }
}

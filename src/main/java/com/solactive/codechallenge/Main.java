package com.solactive.codechallenge;

import org.eclipse.persistence.jaxb.rs.MOXyJsonProvider;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.moxy.json.MoxyJsonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;

/**
 * Main class.
 *
 */
public class Main {
    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://localhost:8080/";

    public final static Application app = new Application();
    public final static ServerHealthSentinel healthSentinel = new ServerHealthSentinel();

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {
        // create a resource config that scans for JAX-RS resources and providers
        // in com.solactive.codechallenge package
        final ResourceConfig rc = new ResourceConfig().packages("com.solactive.codechallenge");
        rc.register(MoxyJsonFeature.class);
        rc.register(MOXyJsonProvider.class);

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    /**
     * Main method.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        // TODO: I would like to be able to specify a set of threads, along with CPU cores
        //       that will handle incoming HTTP requests.
        //
        // TODO: Separately, I would like to specify a separate set of threads and CPU cores
        //       for the calculation agents. (The disruptor is the link between the two.)
        //
        //

        final var server = startServer();
        healthSentinel.start();

        System.out.println("Current time: " + System.currentTimeMillis());
        System.in.read();
        server.stop();
    }
}


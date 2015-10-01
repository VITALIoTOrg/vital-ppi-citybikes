package eu.vital.reply;

import eu.vital.reply.services.HiPPIv2;
import eu.vital.reply.utils.ConfigReader;
import eu.vital.reply.utils.PpiApplicationEventListener;
import eu.vital.reply.utils.PpiRequestEventListener;
import eu.vital.reply.utils.StatCounter;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

/**
 * Main Class that will start a Grizzly HTTP server instance
 * that exposes all the JAX-RS resource on a defined package.
 *
 * @author <a href="mailto:f.deceglia@reply.it">Fabrizio de Ceglia</a>
 * @version 1.0.0
 */
public class Main {
    // Base URI the Grizzly HTTP server will listen on
    public static String BASE_URI;

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in the ppi package.
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {
        // create a resource config that scans for JAX-RS resources and providers
        // in eu.vital.reply package
        /*.packages("eu.vital.reply.services")*/
        final ResourceConfig rc = new ResourceConfig(HiPPIv2.class, PpiApplicationEventListener.class).setApplicationName("HiReplyPPI");

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    /**
     * Main method. Starts the HTTP Server instance.
     * @param args
     * @throws IOException, URISyntaxException, JAXBException, SAXException
     */
    public static void main(String[] args) throws IOException, URISyntaxException, JAXBException, SAXException
    {

        ConfigReader configReader = ConfigReader.getInstance();

        String hostName = configReader.get(ConfigReader.SERVER_HOSTNAME);
        String hostPort = configReader.get(ConfigReader.SERVER_PORT);

        BASE_URI = "http://"+hostName+":"+hostPort+"/";

        final HttpServer server = startServer();
        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));
        System.in.read();
        server.stop();
    }
}


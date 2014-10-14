package eu.vital.reply.services;

import eu.vital.reply.clients.HiReplySvc;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * System resource (exposed at "/system" path)
 */
@Path("system")
public class HiSystem
{
    private HiReplySvc hiReplySvc;

    private Logger logger;

    public HiSystem()
    {
        hiReplySvc = new HiReplySvc();
        logger = LogManager.getLogger(HiSystem.class);
    }


    /**
     *
     * @return
     */
    @Path("snapshot")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getSnapshot() {
        return hiReplySvc.getSnapshot().getIoTSystem().getID(); // test, deve resituire json
    }

    @Path("serviceClasses")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getServiceClasses() {
        return "System serviceClasses";
    }

    @Path("countForClass/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getCountForClass(@PathParam("id") String id) {
        return "System countForClass {" + id + "}";
    }

    @Path("idsForClass/{id}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getIdsForClass(@PathParam("id") String id) {
        return "System idsForClass {" + id + "}";
    }

    @Path("allServicesIds")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getAllServicesId() {
        return "System allServicesId";
    }
}

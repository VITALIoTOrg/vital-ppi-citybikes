package eu.vital.reply.services;

import eu.vital.reply.clients.HiReplySvc;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Service resource (exposed at "/service" path)
 *
 * Created by a.martelli on 09/10/2014.
 */
@Path("service")
public class HiService
{
    private Logger logger;
    private HiReplySvc hiReplySvc;
    public HiService()
    {
        hiReplySvc = new HiReplySvc();
        logger = LogManager.getLogger(HiService.class);
    }

    @Path("{id}/snapshot")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getSnapshot(@PathParam("id") String id) {
        return "System getSnapshot {" + id + "}";
    }

    @Path("{id}/running")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String isRunning(@PathParam("id") String id) {
        //return "System isRunning {" + id + "}";
        return this.hiReplySvc.isServiceRunning(id);
    }

    @Path("{id}/property/names")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getPropertyNames(@PathParam("id") String id) {
        return "System getPropertyNames {" + id + "}";
    }

    @Path("{id}/property/{name}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getPropertyValue(@PathParam("id") String id, @PathParam("name") String name) {
        return "System getPropertyValue {" + id + "," + name + "}";
    }

    @Path("{id}/property/{name}/{value}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public boolean setPropertyValue(@PathParam("id") String id, @PathParam("name") String name,
                                    @PathParam("value") String value) throws IOException, URISyntaxException {
        //return "System setPropertyValue {service:" + id + ", property:" + name + ", value: " + value + "}";
        return this.hiReplySvc.setPropertyValue(id,name,value);
    }

    @Path("{id}/property/{propid}/attribute/{attrid}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getPropertyAttribute(@PathParam("id") String id, @PathParam("propid") String property,
                                       @PathParam("attrid") String attribute) {
        return "System getPropertyAttribute {" + id + "," + property + ", " + attribute + "}";
    }

    @Path("{id}/property/{propid}/history/{starttime}/{endtime}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getPropertyHistory(@PathParam("id") String id, @PathParam("propid") String property,
                                       @PathParam("starttime") String startTime, @PathParam("endtime") String endTime) {
        return "System getPropertyAttribute {" + id + "," + property + ", " + startTime + ", " + endTime + "}";
    }
}

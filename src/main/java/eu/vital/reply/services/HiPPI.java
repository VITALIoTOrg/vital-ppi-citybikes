package eu.vital.reply.services;

/**
 * Created by f.deceglia on 24/11/2014.
 */

import eu.vital.reply.utils.ConfigReader;
import eu.vital.reply.utils.JsonUtils;
import eu.vital.reply.clients.HiReplySvc;
import eu.vital.reply.jsonpojos.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;


@Path("")
public class HiPPI {

    private Logger logger;
    private HiReplySvc hiReplySvc;
    private ConfigReader configReader;

    private String hostPort;
    private String hostName;

    public HiPPI() {

        configReader = ConfigReader.getInstance();
        hiReplySvc = new HiReplySvc();
        logger = LogManager.getLogger(HiService.class);

        hostName = configReader.get(ConfigReader.SERVER_HOSTNAME);
        hostPort = configReader.get(ConfigReader.SERVER_PORT);

    }

    @Path("/external/metadata")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String getMetadata(String bodyRequest) {

        EmptyRequest emptyRequest = null;

        try {
            emptyRequest = (EmptyRequest) JsonUtils.deserializeJson(bodyRequest, EmptyRequest.class);
        } catch (IOException e) {
            // TODO --> LOG
            e.printStackTrace();
        }

        return null;
    }

    @Path("/external/lifecycle_information")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String getLifecycleInformation(String bodyRequest) {

        EmptyRequest emptyRequest = null;

        try {
            emptyRequest = (EmptyRequest) JsonUtils.deserializeJson(bodyRequest, EmptyRequest.class);
        } catch (IOException e) {
            // TODO --> LOG
            e.printStackTrace();
        }

        return null;
    }

    @Path("/ico/metadata")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String getIcoMetadata(String bodyRequest) {

        ICORequest icoRequest = null;

        try {
            icoRequest = (ICORequest) JsonUtils.deserializeJson(bodyRequest, ICORequest.class);
        } catch (IOException e) {
            // TODO --> LOG
            e.printStackTrace();
        }

        return null;
    }

    @Path("observation")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String getObservation(String bodyRequest) {

        ObservationRequest observationRequest = null;

        try {
            observationRequest = (ObservationRequest) JsonUtils.deserializeJson(bodyRequest, ObservationRequest.class);
        } catch (IOException e) {
            // TODO --> LOG
            e.printStackTrace();
        }


        String foo = "ciao bello ciaoo";

        return null;
    }



}

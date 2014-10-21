package eu.vital.reply.services;

import eu.vital.reply.ConfigReader;
import eu.vital.reply.Utils.JsonUtils;
import eu.vital.reply.clients.HiReplySvc;
import eu.vital.reply.jsonpojos.IoTSystem;
import eu.vital.reply.jsonpojos.MsmHasOperation;
import eu.vital.reply.jsonpojos.SystemOperation;
import eu.vital.reply.jsonpojos.ProvidesService;
import eu.vital.reply.xmlpojos.ServiceList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * System resource (exposed at "/system" path)
 */
@Path("system")
public class HiSystem
{
    private HiReplySvc hiReplySvc;

    private Logger logger;

    private ConfigReader configReader;
    private String hostName;
    private String hostPort;

    public HiSystem()
    {
        configReader = ConfigReader.getInstance();
        hiReplySvc = new HiReplySvc();
        logger = LogManager.getLogger(HiSystem.class);

        hostName = configReader.get(ConfigReader.SERVER_HOSTNAME);
        hostPort = configReader.get(ConfigReader.SERVER_PORT);
    }

    @Path("info")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getInfo() {

        ServiceList system = hiReplySvc.getSnapshot();

        IoTSystem ioTSystem = new IoTSystem();
        ProvidesService service = new ProvidesService();
        MsmHasOperation operation = new MsmHasOperation();
        ArrayList<ProvidesService> serviceList = new ArrayList<>();
        ArrayList<MsmHasOperation> operations = new ArrayList<>();

        ioTSystem.setContext("http://vital.iot.org/system.jsonld");
        ioTSystem.setName(system.getIoTSystem().getID());
        ioTSystem.setDescription(system.getIoTSystem().getDescription());
        ioTSystem.setUri(system.getIoTSystem().getUri());
        ioTSystem.setStatus(system.getIoTSystem().getStatus());
        ioTSystem.setOperator(system.getIoTSystem().getOperator());
        ioTSystem.setServiceArea(system.getIoTSystem().getServiceArea());

        service.setType("System");
        operation.setType("SystemInfo");
        operation.setHrestHasAddress("http://"+hostName+":"+hostPort+"/system/info");
        operation.setHrestHasMethod("hrest:GET");

        operations.add(operation);

        service.setMsmHasOperation(operations);

        serviceList.add(service);

        //start servizio 2

        service = new ProvidesService();
        operation = new MsmHasOperation();
        operations = new ArrayList<>();

        service.setType("Service");
        operation.setType("AllService");
        operation.setHrestHasAddress("http://"+hostName+":"+hostPort+"/service/all");
        operation.setHrestHasMethod("hrest:GET");

        operations.add(operation);

        operation = new MsmHasOperation();

        operation.setType("ServiceInfo");
        operation.setHrestHasAddress("http://"+hostName+":"+hostPort+"/service/{id}/info");
        operation.setHrestHasMethod("hrest:GET");

        operations.add(operation);

        operation = new MsmHasOperation();

        operation.setType("ServicePropertyValue");
        operation.setHrestHasAddress("http://"+hostName+":"+hostPort+"vital.hireply/service/{id}/property/{name}");
        operation.setHrestHasMethod("hrest:GET");

        operations.add(operation);

        service.setMsmHasOperation(operations);

        serviceList.add(service);

        //end servizio 2

        ioTSystem.setProvidesService(serviceList);

        String out = "";

        try {
            out = JsonUtils.serializeJson(ioTSystem);
        } catch (IOException e) {
            this.logger.error("JSON UTILS IO EXCEPTION");
            e.printStackTrace();
        }

        return out;
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

package eu.vital.reply.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.vital.reply.Utils.JsonUtils;
import eu.vital.reply.clients.HiReplySvc;
import eu.vital.reply.jsonpojos.Location;
import eu.vital.reply.jsonpojos.ObservedProperty;
import eu.vital.reply.jsonpojos.Sensor;
import eu.vital.reply.xmlpojos.ServiceList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

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


    @Path("all")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getAllSensors() {

        //TODO: deve essere generico per ogni sensore

        List<ServiceList.TrafficSensor> trafficSensors = this.hiReplySvc.getSnapshot().getTrafficSensor();
        List<Sensor> sensors = new ArrayList<>();

        Sensor sensor = null;
        ServiceList.TrafficSensor currentSensor = null;
        String id = "";


        for (int i=0; i<trafficSensors.size(); i++) {

            sensor = new Sensor();
            currentSensor = trafficSensors.get(i);
            id = currentSensor.getID();

            sensor.setContext("http://vital-iot.org/contexts/sensor.jsonld");
            sensor.setName(currentSensor.getID());
            sensor.setType("Traffic");
            sensor.setDescription(currentSensor.getDescription());
            sensor.setUri("http://uri.example");

            Location location = new Location();
            location.setType("geo:Point");
            String[] splitted = currentSensor.getPhysicalLocation().split(";");
            location.setGeoLat(splitted[0]);
            location.setGeoLong(splitted[1]);

            sensor.setLocation(location);

            int dirCount = currentSensor.getDirectionCount();
            List<ObservedProperty> observedProperties = new ArrayList<>();

            if (dirCount == 1) {
                //speed e color
                ObservedProperty speed = new ObservedProperty();
                speed.setType("http://"+id+"/type.example.Speed");
                speed.setUri("http://"+id+"/uri.example.speed");
                ObservedProperty color = new ObservedProperty();
                color.setType("http://"+id+"/type.example.Color");
                color.setUri("http://" + id + "/uri.example.color");
                observedProperties.add(speed);
                observedProperties.add(color);
            }

            if (dirCount == 2) {
                //speed e color + reverse
                ObservedProperty speed = new ObservedProperty();
                speed.setType("http://reply.eu/Speed");
                speed.setUri("http://host:port/service/"+id+"/property/Speed");
                ObservedProperty color = new ObservedProperty();
                color.setType("http://reply.eu/Color");
                color.setUri("http://host:port/service/" + id + "/property/Color");
                observedProperties.add(speed);
                observedProperties.add(color);
                ObservedProperty revspeed = new ObservedProperty();
                revspeed.setType("http://reply.eu/ReverseSpeed");
                revspeed.setUri("http://host:port/service/" + id + "property/ReverseSpeed");
                ObservedProperty revcolor = new ObservedProperty();
                revcolor.setType("http://reply.eu/ReverseColor");
                revcolor.setUri("http://host:port/service/" + id + "/property/ReverseColor");
                observedProperties.add(revspeed);
                observedProperties.add(revcolor);
            }

            sensor.setSsnObserves(observedProperties);
            sensor.setSsnMadeObservation("http://host:port/service/"+id+"/obsvn/1");

            sensors.add(sensor);

        }

        String out = "";

        try {
            out = JsonUtils.serializeJson(sensors);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return out;
    }


    @Path("{id}/info")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getSensorInfo(@PathParam("id") String id) {

        //TODO: deve essere generico per ogni tipo di sensore

        String filter = hiReplySvc.createFilter("ID",id);

        List<ServiceList.TrafficSensor> trafficSensors = this.hiReplySvc.getSnapshotFiltered(filter).getTrafficSensor();
        ServiceList.TrafficSensor currentSensor = null;

        try {
            currentSensor = trafficSensors.get(0);
        } catch (IndexOutOfBoundsException e) {
            logger.error("ID: "+id+" not present.");
            return "{\n" +
                    "\"ERROR\": \"ID "+id+" not present.\"\n"+
                    "}";
        }

        Sensor sensor = new Sensor();

        sensor.setContext("http://vital-iot.org/contexts/sensor.jsonld");
        sensor.setName(currentSensor.getID());
        sensor.setType("Traffic");
        sensor.setDescription(currentSensor.getDescription());
        sensor.setUri("http://uri.example");

        Location location = new Location();
        location.setType("geo:Point");
        String[] splitted = currentSensor.getPhysicalLocation().split(";");
        location.setGeoLat(splitted[0]);
        location.setGeoLong(splitted[1]);

        sensor.setLocation(location);

        int dirCount = currentSensor.getDirectionCount();
        List<ObservedProperty> observedProperties = new ArrayList<>();

        if (dirCount == 1) {
            //speed e color
            ObservedProperty speed = new ObservedProperty();
            speed.setType("http://"+id+"/type.example.Speed");
            speed.setUri("http://"+id+"/uri.example.speed");
            ObservedProperty color = new ObservedProperty();
            color.setType("http://"+id+"/type.example.Color");
            color.setUri("http://" + id + "/uri.example.color");
            observedProperties.add(speed);
            observedProperties.add(color);
        }

        if (dirCount == 2) {
            //speed e color + reverse
            ObservedProperty speed = new ObservedProperty();
            speed.setType("http://reply.eu/Speed");
            speed.setUri("http://host:port/service/"+id+"/property/Speed");
            ObservedProperty color = new ObservedProperty();
            color.setType("http://reply.eu/Color");
            color.setUri("http://host:port/service/" + id + "/property/Color");
            observedProperties.add(speed);
            observedProperties.add(color);
            ObservedProperty revspeed = new ObservedProperty();
            revspeed.setType("http://reply.eu/ReverseSpeed");
            revspeed.setUri("http://host:port/service/" + id + "property/ReverseSpeed");
            ObservedProperty revcolor = new ObservedProperty();
            revcolor.setType("http://reply.eu/ReverseColor");
            revcolor.setUri("http://host:port/service/" + id + "/property/ReverseColor");
            observedProperties.add(revspeed);
            observedProperties.add(revcolor);
        }

        sensor.setSsnObserves(observedProperties);
        sensor.setSsnMadeObservation("http://host:port/service/"+id+"/obsvn/1");

        String out = "";

        try {
            out = JsonUtils.serializeJson(sensor);
        } catch (IOException e) {
            logger.error("IO EXCEPTION Sensor INFO JSON serialize");
            e.printStackTrace();
        }

        return out;
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

    @Path("{id}/property/{name}/{raw}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public boolean setPropertyValue(@PathParam("id") String id, @PathParam("name") String name,
                                    @PathParam("raw") String value) throws IOException, URISyntaxException {
        //return "System setPropertyValue {service:" + id + ", property:" + name + ", raw: " + raw + "}";
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

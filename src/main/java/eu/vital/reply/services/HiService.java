package eu.vital.reply.services;

import eu.vital.reply.utils.ConfigReader;
import eu.vital.reply.utils.JsonUtils;
import eu.vital.reply.clients.HiReplySvc;
import eu.vital.reply.jsonpojos.*;
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
    private ConfigReader configReader;

    private String hostPort;
    private String hostName;


    public HiService()
    {
        configReader = ConfigReader.getInstance();
        hiReplySvc = new HiReplySvc();
        logger = LogManager.getLogger(HiService.class);

        hostName = configReader.get(ConfigReader.SERVER_HOSTNAME);
        hostPort = configReader.get(ConfigReader.SERVER_PORT);
    }

    @Path("{id}/property/{propertyname}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getPropertyValue(@PathParam("id") String id, @PathParam("propertyname") String propertyname) {

        //TODO: da rendere generico per ogni tipo di sensore

        /*
        per rendere generico il metodo, sfruttare il presso di ogni id:
        - cnc12145-I_TrS_600 --> TrS: Sensore del traffico
        - cnc12145-Thermometer1 --> Thermometer: Sensore di temperatura
        [...]

        if (id.contains("TrS")) {
            buildTrafficSensor()
        }
        if (id.contains("Temperature") {
            buildTempSensor()
        }
        [...]

         */

        String filter = hiReplySvc.createFilter("ID",id);

        List<ServiceList.TrafficSensor> trafficSensors = this.hiReplySvc.getSnapshotFiltered(filter).getTrafficSensor();

        ServiceList.TrafficSensor currentSensor = null;

        try {
            currentSensor = trafficSensors.get(0);
        } catch (IndexOutOfBoundsException e) {
            logger.error("ID: "+id+" not present.");
            return "{\n" +
                    "\"error\": \"ID "+id+" not present.\"\n"+
                    "}";
        }

        Measure m = new Measure();

        m.setContext("http://vital-iot.org/contexts/measurement.jsonld");
        m.setUri("http://"+hostName+":"+hostPort+"/service/" + id + "/property/" + propertyname);
        m.setType("ssn:Observation");

        SsnObservationProperty ssnObservationProperty = new SsnObservationProperty();
        ssnObservationProperty.setType("http://lsm.deri.ie/OpenIoT/"+propertyname);

        m.setSsnObservationProperty(ssnObservationProperty);

        SsnObservationResultTime ssnObservationResultTime = new SsnObservationResultTime();
        ssnObservationResultTime.setInXSDDateTime(currentSensor.getMeasureTime().toString());

        m.setSsnObservationResultTime(ssnObservationResultTime);

        DulHasLocation dulHasLocation = new DulHasLocation();
        dulHasLocation.setType("geo:Point");
        String[] splitted = currentSensor.getPhysicalLocation().split(";");
        dulHasLocation.setGeoLat(splitted[1]);
        dulHasLocation.setGeoLong(splitted[0]);
        dulHasLocation.setGeoAlt("0.0");

        m.setDulHasLocation(dulHasLocation);

        SsnObservationQuality ssnObservationQuality = new SsnObservationQuality();
        SsnHasMeasurementProperty ssnHasMeasurementProperty = new SsnHasMeasurementProperty();
        ssnHasMeasurementProperty.setType("Reliability");
        ssnHasMeasurementProperty.setHasValue("HighReliability");
        ssnObservationQuality.setSsnHasMeasurementProperty(ssnHasMeasurementProperty);

        SsnObservationResult ssnObservationResult = new SsnObservationResult();
        ssnObservationResult.setType("ssn:SensorOutput");
        SsnHasValue ssnHasValue = new SsnHasValue();
        ssnHasValue.setType("ssn:ObservationValue");

        float speedValue;
        int colorValue;

        if (currentSensor.getDirectionCount() == 1) {
            if (propertyname.equals("Speed")) {
                speedValue = currentSensor.getSpeed();
                ssnHasValue.setValue(""+speedValue);
                ssnHasValue.setQudtUnit("qudt:KmH");
            } else if (propertyname.equals("Color")) {
                colorValue = currentSensor.getColor();
                ssnHasValue.setValue(""+colorValue);
                ssnHasValue.setQudtUnit("qudt:Color");
            } else {
                return "{\n" +
                        "\"error\": \"ID "+id+" has no "+propertyname+" property.\"\n"+
                        "}";
            }
        }

        if (currentSensor.getDirectionCount() == 2) {
            if (propertyname.equals("Speed")) {
                speedValue = currentSensor.getSpeed();
                ssnHasValue.setValue(""+speedValue);
                ssnHasValue.setQudtUnit("qudt:KmH");
            } else if (propertyname.equals("Color")) {
                colorValue = currentSensor.getColor();
                ssnHasValue.setValue(""+colorValue);
                ssnHasValue.setQudtUnit("qudt:Color");
            } else if (propertyname.equals("ReverseSpeed")) {
                speedValue = currentSensor.getReverseSpeed();
                ssnHasValue.setValue(""+speedValue);
                ssnHasValue.setQudtUnit("qudt:KmH");
            } else if (propertyname.equals("ReverseColor")) {
                colorValue = currentSensor.getReverseColor();
                ssnHasValue.setValue(""+colorValue);
                ssnHasValue.setQudtUnit("qudt:Color");
            } else {
                return "{\n" +
                        "\"error\": \"ID "+id+" has no "+propertyname+" property.\"\n"+
                        "}";
            }
        }

        ssnObservationResult.setSsnHasValue(ssnHasValue);

        m.setSsnObservationResult(ssnObservationResult);

        String out = "";

        try {
            out = JsonUtils.serializeJson(m);
        } catch (IOException e) {
            logger.error("IO EXCEPTION Measure JSON serialize");
            e.printStackTrace();
        }

        return out;
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
            sensor.setUri("http://"+hostName+":"+hostPort+"/service/"+id+"/info");

            int status = currentSensor.getStatus();

            if (status==1) {
                sensor.setStatus("Running");
            } else if (status==0) {
                sensor.setStatus("Unavailable");
            } else {
                sensor.setStatus("");
            }


            HasLastKnownLocation location = new HasLastKnownLocation();
            location.setType("geo:Point");
            String[] splitted = currentSensor.getPhysicalLocation().split(";");
            location.setGeoLat(splitted[1]);
            location.setGeoLong(splitted[0]);

            sensor.setHasLastKnownLocation(location);

            int dirCount = currentSensor.getDirectionCount();
            List<SsnObserf> observedProperties = new ArrayList<>();

            if (dirCount == 1) {
                //speed e color
                SsnObserf speed = new SsnObserf();
                speed.setType("http://"+id+"/type.example.Speed");
                speed.setUri("http://"+hostName+":"+hostPort+"/service/"+id+"/property/Speed");
                SsnObserf color = new SsnObserf();
                color.setType("http://"+id+"/type.example.Color");
                color.setUri("http://"+hostName+":"+hostPort+"/service/" + id + "/property/Color");
                observedProperties.add(speed);
                observedProperties.add(color);
            }

            if (dirCount == 2) {
                //speed e color + reverse
                SsnObserf speed = new SsnObserf();
                speed.setType("http://reply.eu/Speed");
                speed.setUri("http://"+hostName+":"+hostPort+"/service/"+id+"/property/Speed");
                SsnObserf color = new SsnObserf();
                color.setType("http://reply.eu/Color");
                color.setUri("http://"+hostName+":"+hostPort+"/service/" + id + "/property/Color");
                observedProperties.add(speed);
                observedProperties.add(color);
                SsnObserf revspeed = new SsnObserf();
                revspeed.setType("http://reply.eu/ReverseSpeed");
                revspeed.setUri("http://"+hostName+":"+hostPort+"/service/" + id + "property/ReverseSpeed");
                SsnObserf revcolor = new SsnObserf();
                revcolor.setType("http://reply.eu/ReverseColor");
                revcolor.setUri("http://"+hostName+":"+hostPort+"/service/" + id + "/property/ReverseColor");
                observedProperties.add(revspeed);
                observedProperties.add(revcolor);
            }

            sensor.setSsnObserves(observedProperties);
            sensor.setSsnMadeObservation("http://"+hostName+":"+hostPort+"/service/"+id+"/info");

            sensors.add(sensor);

        }

        String out = "";

        try {
            out = JsonUtils.serializeJson(sensors);
        } catch (IOException e) {
            logger.error("IO EXCEPTION Sensor INFO JSON serialize");
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
                    "\"error\": \"ID "+id+" not present\"\n"+
                    "}";
        }

        Sensor sensor = new Sensor();

        sensor.setContext("http://vital-iot.org/contexts/sensor.jsonld");
        sensor.setName(currentSensor.getID());
        sensor.setType("Traffic");
        sensor.setDescription(currentSensor.getDescription());
        sensor.setUri("http://"+hostName+":"+hostPort+"/service/"+id+"/info");

        int status = currentSensor.getStatus();
        if (status==1) {
            sensor.setStatus("Running");
        } else if (status==0) {
            sensor.setStatus("Unavailable");
        } else {
            sensor.setStatus("");
        }

        HasLastKnownLocation location = new HasLastKnownLocation();
        location.setType("geo:Point");
        String[] splitted = currentSensor.getPhysicalLocation().split(";");
        location.setGeoLat(splitted[1]);
        location.setGeoLong(splitted[0]);

        sensor.setHasLastKnownLocation(location);

        int dirCount = currentSensor.getDirectionCount();
        List<SsnObserf> observedProperties = new ArrayList<>();

        if (dirCount == 1) {
            //speed e color
            SsnObserf speed = new SsnObserf();
            speed.setType("http://reply.eu/vital/Speed");
            speed.setUri("http://"+hostName+":"+hostPort+"/service/" + id + "/property/Speed");
            SsnObserf color = new SsnObserf();
            color.setType("http://reply.eu/vital/Color");
            color.setUri("http://"+hostName+":"+hostPort+"/service/" + id + "/property/Color");
            observedProperties.add(speed);
            observedProperties.add(color);
        }

        if (dirCount == 2) {
            //speed e color + reverse
            SsnObserf speed = new SsnObserf();
            speed.setType("http://reply.eu/vital/Speed");
            speed.setUri("http://"+hostName+":"+hostPort+"/service/" + id + "/property/Speed");
            SsnObserf color = new SsnObserf();
            color.setType("http://reply.eu/vital/Color");
            color.setUri("http://"+hostName+":"+hostPort+"/service/" + id + "/property/Color");
            observedProperties.add(speed);
            observedProperties.add(color);
            SsnObserf revspeed = new SsnObserf();
            revspeed.setType("http://reply.eu/vital/ReverseSpeed");
            revspeed.setUri("http://"+hostName+":"+hostPort+"/service/" + id + "/property/ReverseSpeed");
            SsnObserf revcolor = new SsnObserf();
            revcolor.setType("http://reply.eu/vital/ReverseColor");
            revcolor.setUri("http://"+hostName+":"+hostPort+"/service/" + id + "/property/ReverseColor");
            observedProperties.add(revspeed);
            observedProperties.add(revcolor);
        }

        sensor.setSsnObserves(observedProperties);
        sensor.setSsnMadeObservation("http://"+hostName+":"+hostPort+"/service/"+id+"/info");

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

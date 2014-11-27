package eu.vital.reply.services;

/**
 * Created by f.deceglia on 24/11/2014.
 */

import eu.vital.reply.utils.ConfigReader;
import eu.vital.reply.utils.JsonUtils;
import eu.vital.reply.clients.HiReplySvc;
import eu.vital.reply.jsonpojos.*;
import eu.vital.reply.xmlpojos.ServiceList;
import eu.vital.reply.xmlpojos.ValueList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Path("")
public class HiPPI {

    private Logger logger;
    private HiReplySvc hiReplySvc;
    private ConfigReader configReader;

    private String hostPort;
    private String hostName;
    private String symbolicUri;
    private String ontBaseUri;

    public HiPPI() {

        configReader = ConfigReader.getInstance();
        hiReplySvc = new HiReplySvc();
        logger = LogManager.getLogger(HiService.class);

        hostName = configReader.get(ConfigReader.SERVER_HOSTNAME);
        hostPort = configReader.get(ConfigReader.SERVER_PORT);
        symbolicUri = configReader.get(ConfigReader.SYMBOLIC_URI);
        ontBaseUri = configReader.get(ConfigReader.ONT_BASE_URI_PROPERTY);

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
        // TODO --> check sulla request, trattamento di eventuali filtri

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

        if (system.getIoTSystem().getStatus().equals("Running")) {
            ioTSystem.setStatus("vital:Running");
        }

        ioTSystem.setOperator(system.getIoTSystem().getOperator());
        ioTSystem.setServiceArea(system.getIoTSystem().getServiceArea());

        service.setType("ICOManager");
        service.setContext("http://vital-iot.org/contexts/service.jsonld");
        operation.setType("GetMetadata");
        operation.setHrestHasAddress("http://"+hostName+":"+hostPort+"/ico/metadata");
        operation.setHrestHasMethod("hrest:POST");

        operations.add(operation);

        service.setMsmHasOperation(operations);

        serviceList.add(service);

        //start servizio 2

        service = new ProvidesService();
        operation = new MsmHasOperation();
        operations = new ArrayList<>();

        service.setType("ObservationManager");
        service.setContext("http://vital-iot.org/contexts/service.jsonld");
        operation.setType("GetObservation");
        operation.setHrestHasAddress("http://"+hostName+":"+hostPort+"/observation");
        operation.setHrestHasMethod("hrest:POST");

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
        // TODO --> check sulla request, trattamento di eventuali filtri

        return out;
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
        // TODO --> check sulla request, trattamento di eventuali filtri

        ServiceList system = hiReplySvc.getSnapshot();
        LifecycleInformation lifecycleInformation = new LifecycleInformation();

        lifecycleInformation.setContext("http://vital.iot.org/system.jsonld");
        lifecycleInformation.setUri(system.getIoTSystem().getUri());

        if (system.getIoTSystem().getStatus().equals("Running")) {
            lifecycleInformation.setStatus("vital:Running");
        }

        String out = "";

        try {
            out = JsonUtils.serializeJson(lifecycleInformation);
        } catch (IOException e) {
            this.logger.error("JSON UTILS IO EXCEPTION");
            e.printStackTrace();
        }

        return out;
    }

    @Path("/ico/metadata")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String getIcoMetadata(String bodyRequest) {

        ICORequest icoRequest = new ICORequest();

        try {
            icoRequest = (ICORequest) JsonUtils.deserializeJson(bodyRequest, ICORequest.class);
        } catch (IOException e) {
            // TODO --> LOG
            e.printStackTrace();
        }
        // TODO --> check sulla request, trattamento di eventuali filtri

        List<String> requestedSensor = new ArrayList<>();

        try {
            requestedSensor = icoRequest.getIcos();
        } catch (NullPointerException e) {
            // TODO --> LOG
            e.printStackTrace();
        }


        List<Sensor> sensors = new ArrayList<>(); //lista da restituire in output


        if (requestedSensor.size() == 0) {
            //restituisci tutti i sensori
            List<ServiceList.TrafficSensor> trafficSensors = this.hiReplySvc.getSnapshot().getTrafficSensor();
            for (int i = 0; i < trafficSensors.size(); i++) {
                sensors.add(this.createSensorFromTraffic(trafficSensors.get(i)));
            }
        } else {
            //restituisci solo i sensori desirati
            for (int i = 0; i < requestedSensor.size(); i++) {
                String currentId = requestedSensor.get(i).replaceAll(this.symbolicUri+"ico/","");

                String filter = hiReplySvc.createFilter("ID",currentId);

                ServiceList.TrafficSensor currentTrafficSensor = null;

                try {
                    currentTrafficSensor = this.hiReplySvc.getSnapshotFiltered(filter).getTrafficSensor().get(0);;
                } catch (IndexOutOfBoundsException e) {
                    logger.error("ID: "+currentId+" not present.");
                    return "{\n" +
                            "\"error\": \"ID "+currentId+" not present\"\n"+
                            "}";
                }

                sensors.add(this.createSensorFromTraffic(currentTrafficSensor));

            }
        }

        String out = "";

        try {
            out = JsonUtils.serializeJson(sensors);
        } catch (IOException e) {
            this.logger.error("JSON UTILS IO EXCEPTION");
            e.printStackTrace();
        }

        return out;
    }

    @Path("observation")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String getObservation(String bodyRequest) {

        ObservationRequest observationRequest = null;
        ArrayList<Measure> measures = new ArrayList<>();

        try {
            observationRequest = (ObservationRequest) JsonUtils.deserializeJson(bodyRequest, ObservationRequest.class);
        } catch (IOException e) {
            // TODO --> LOG
            e.printStackTrace();
        }
        // TODO --> check sulla request, trattamento di eventuali filtri

        String id = observationRequest.getIco().replaceAll(this.symbolicUri+"ico/", "");

        //controllo che il sensore richiesto (id) sia effettivamente presente sul virtualizzatore. in caso nn è presente genero un json di errore
        ServiceList.TrafficSensor currentSensor = this.retrieveSensor(id);

        if (currentSensor == null) {
            return "{\n" +
                    "\"error\": \"ID "+id+" not present.\"\n"+
                    "}";
        }

        //controllo che la proprietà richiesta sia tra quelle possibili (Speed, Color, Reverse Speed, Reverse Color) del sensore
        String property = observationRequest.getProperty().replaceAll(this.ontBaseUri,"");

        if (!this.checkTrafficProperty(currentSensor, property)) {
            return "{\n" +
                    "\"error\": \"Property "+property+" not present for the"+id+" sensor.\"\n"+
                    "}";
        }

        if (observationRequest.getFrom() != null && observationRequest.getTo()!=null ) {
            //get history range

            SimpleDateFormat arrivedFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            SimpleDateFormat hiReplyFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            Date fromDate = null;
            Date toDate = null;
            Date fromDateHiReply = null;
            Date toDateHiReply = null;

            try {
                fromDate = arrivedFormat.parse(observationRequest.getFrom());
                toDate = arrivedFormat.parse(observationRequest.getTo());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            try {
                fromDateHiReply = hiReplyFormat.parse(hiReplyFormat.format(fromDate));
                toDateHiReply = hiReplyFormat.parse(hiReplyFormat.format(toDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            List<HistoryMeasure> historyMeasures = this.getHistoryMeasures(hiReplySvc.getPropertyHistoricalValues(id, property, fromDateHiReply, toDateHiReply));

            for (int i = 0; i < historyMeasures.size(); i++) {

                measures.add(this.createMeasureFromHistoryMeasure(historyMeasures.get(i), currentSensor, property));

            }

        } else if (observationRequest.getFrom() != null && observationRequest.getTo() == null) {
            //get tutti i valori da from
        } else if (observationRequest.getFrom() == null && observationRequest.getTo() == null) {
            //get ultimo valore

            measures.add(this.createMeasureFromSensor(currentSensor, property));

        }

        String out = "";

        try {
            out = JsonUtils.serializeJson(measures);
        } catch (IOException e) {
            //TODO --> add log
            e.printStackTrace();
        }

        return out;
    }

    private class HistoryMeasure {

        private float value;
        private Date date;

        public HistoryMeasure(float value, Date date) {
            this.value = value;
            this.date = date;
        }

        public void setValue(float value) {
            this.value = value;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public float getValue() {
            return this.value;
        }

        public Date getDate() {
            return this.date;
        }

    }

    private List<HistoryMeasure> getHistoryMeasures(ValueList valueList) {

        ArrayList<HistoryMeasure> historyMeasures = new ArrayList<>();
        List<String> values = valueList.getValue();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy HH:mm:ss");

        for (int i = 0; i<values.size(); i++) {

            String currentValue = values.get(i);
            String[] splitted = currentValue.split(","); //splitted[0] = value --- splitted[1] data
            float auxValue = Float.parseFloat(splitted[0]);
            Date auxDate = new Date();

            try {
                auxDate = dateFormat.parse(splitted[1]);
            } catch (ParseException e) {
                // TODO --> add log
                e.printStackTrace();
            }

            historyMeasures.add(new HistoryMeasure(auxValue, auxDate));

        }

        return historyMeasures;
    }

    private Sensor createSensorFromTraffic(ServiceList.TrafficSensor currentSensor) {
        Sensor sensor = new Sensor();
        String id = currentSensor.getID();

        sensor.setContext("http://vital-iot.org/contexts/sensor.jsonld");
        sensor.setName(id);
        sensor.setType("Traffic");
        sensor.setDescription(currentSensor.getDescription());
        sensor.setUri("http://"+symbolicUri+"/ico/"+id);

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
            speed.setType("http://lsm.deri.ie/OpenIot/Speed");
            speed.setUri("http://"+symbolicUri+"ico/"+id+"/Speed");
            SsnObserf color = new SsnObserf();
            color.setType("http://lsm.deri.ie/OpenIot/Color");
            color.setUri("http://"+symbolicUri+"ico/" + id + "/Color");
            observedProperties.add(speed);
            observedProperties.add(color);
        }

        if (dirCount == 2) {
            //speed e color + reverse
            SsnObserf speed = new SsnObserf();
            speed.setType("http://lsm.deri.ie/OpenIot/Speed");
            speed.setUri("http://"+symbolicUri+"ico/"+id+"/Speed");
            SsnObserf color = new SsnObserf();
            color.setType("http://lsm.deri.ie/OpenIot/Color");
            color.setUri("http://"+symbolicUri+"ico/" + id + "/Color");
            observedProperties.add(speed);
            observedProperties.add(color);
            SsnObserf revspeed = new SsnObserf();
            revspeed.setType("http://lsm.deri.ie/OpenIot/ReverseSpeed");
            revspeed.setUri("http://"+symbolicUri+"ico/" + id + "/ReverseSpeed");
            SsnObserf revcolor = new SsnObserf();
            revcolor.setType("http://lsm.deri.ie/OpenIot/ReverseColor");
            revcolor.setUri("http://"+symbolicUri+"ico/" + id + "/ReverseColor");
            observedProperties.add(revspeed);
            observedProperties.add(revcolor);
        }

        sensor.setSsnObserves(observedProperties);

        return sensor;
    }

    private Measure createMeasureFromSensor(ServiceList.TrafficSensor currentSensor, String property) {
        Measure m = new Measure();

        m.setContext("http://vital-iot.org/contexts/measurement.jsonld");
        m.setUri("http://"+symbolicUri+"ico/" + currentSensor.getID() + "/observation");
        m.setType("ssn:Observation");

        SsnObservationProperty ssnObservationProperty = new SsnObservationProperty();
        ssnObservationProperty.setType("http://lsm.deri.ie/OpenIoT/"+property);

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
            if (property.equals("Speed")) {
                speedValue = currentSensor.getSpeed();
                ssnHasValue.setValue(""+speedValue);
                ssnHasValue.setQudtUnit("qudt:KmH");
            } else if (property.equals("Color")) {
                colorValue = currentSensor.getColor();
                ssnHasValue.setValue(""+colorValue);
                ssnHasValue.setQudtUnit("qudt:Color");
            } else {
                return null;
            }
        }

        if (currentSensor.getDirectionCount() == 2) {
            if (property.equals("Speed")) {
                speedValue = currentSensor.getSpeed();
                ssnHasValue.setValue(""+speedValue);
                ssnHasValue.setQudtUnit("qudt:KmH");
            } else if (property.equals("Color")) {
                colorValue = currentSensor.getColor();;
                ssnHasValue.setValue(""+colorValue);
                ssnHasValue.setQudtUnit("qudt:Color");
            } else if (property.equals("ReverseSpeed")) {
                speedValue = currentSensor.getSpeed();
                ssnHasValue.setValue(""+speedValue);
                ssnHasValue.setQudtUnit("qudt:KmH");
            } else if (property.equals("ReverseColor")) {
                colorValue = currentSensor.getColor();
                ssnHasValue.setValue(""+colorValue);
                ssnHasValue.setQudtUnit("qudt:Color");
            } else {
                return null;
            }
        }

        ssnObservationResult.setSsnHasValue(ssnHasValue);

        m.setSsnObservationResult(ssnObservationResult);


        return m;
    }


    private Measure createMeasureFromHistoryMeasure(HistoryMeasure historyMeasure, ServiceList.TrafficSensor currentSensor, String property) {

        Measure m = new Measure();

        m.setContext("http://vital-iot.org/contexts/measurement.jsonld");
        m.setUri("http://"+symbolicUri+"ico/" + currentSensor.getID() + "/observation");
        m.setType("ssn:Observation");

        SsnObservationProperty ssnObservationProperty = new SsnObservationProperty();
        ssnObservationProperty.setType("http://lsm.deri.ie/OpenIoT/"+property);

        m.setSsnObservationProperty(ssnObservationProperty);

        SsnObservationResultTime ssnObservationResultTime = new SsnObservationResultTime();
        ssnObservationResultTime.setInXSDDateTime(historyMeasure.getDate().toString());

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
            if (property.equals("Speed")) {
                speedValue = historyMeasure.getValue();
                ssnHasValue.setValue(""+speedValue);
                ssnHasValue.setQudtUnit("qudt:KmH");
            } else if (property.equals("Color")) {
                colorValue = Math.round(historyMeasure.getValue());
                ssnHasValue.setValue(""+colorValue);
                ssnHasValue.setQudtUnit("qudt:Color");
            } else {
                return null;
            }
        }

        if (currentSensor.getDirectionCount() == 2) {
            if (property.equals("Speed")) {
                speedValue = historyMeasure.getValue();
                ssnHasValue.setValue(""+speedValue);
                ssnHasValue.setQudtUnit("qudt:KmH");
            } else if (property.equals("Color")) {
                colorValue = Math.round(historyMeasure.getValue());
                ssnHasValue.setValue(""+colorValue);
                ssnHasValue.setQudtUnit("qudt:Color");
            } else if (property.equals("ReverseSpeed")) {
                speedValue = historyMeasure.getValue();
                ssnHasValue.setValue(""+speedValue);
                ssnHasValue.setQudtUnit("qudt:KmH");
            } else if (property.equals("ReverseColor")) {
                colorValue = Math.round(historyMeasure.getValue());
                ssnHasValue.setValue(""+colorValue);
                ssnHasValue.setQudtUnit("qudt:Color");
            } else {
                return null;
            }
        }

        ssnObservationResult.setSsnHasValue(ssnHasValue);

        m.setSsnObservationResult(ssnObservationResult);

        return m;
    }

    private ServiceList.TrafficSensor retrieveSensor(String id) {

        String filter = hiReplySvc.createFilter("ID",id);

        List<ServiceList.TrafficSensor> trafficSensors = this.hiReplySvc.getSnapshotFiltered(filter).getTrafficSensor();

        ServiceList.TrafficSensor currentSensor = null;

        try {
            currentSensor = trafficSensors.get(0);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }

        return currentSensor;
    }

    private boolean checkTrafficProperty(ServiceList.TrafficSensor currentSensor, String property) {
        if (currentSensor.getDirectionCount() == 1) {
            if (!property.equals("Speed") && !property.equals("Color")) {
                return false;
            }
        } else if (currentSensor.getDirectionCount() == 2) {
            if (!property.equals("Speed") && !property.equals("Color") && !property.equals("ReverseSpeed") && !property.equals("ReverseColor")) {
                return false;
            }
        }
        return true;
    }
}

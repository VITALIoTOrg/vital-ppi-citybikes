package eu.vital.reply.services;

import eu.vital.reply.clients.HiReplySvc;
import eu.vital.reply.jsonpojos.*;
import eu.vital.reply.utils.ConfigReader;
import eu.vital.reply.utils.JsonUtils;
import eu.vital.reply.utils.StatCounter;
import eu.vital.reply.xmlpojos.ServiceList;
import eu.vital.reply.xmlpojos.ValueList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * HiPPI Class that provides all the REST API that a system attached to Vital will expose
 *
 * @author <a href="mailto:f.deceglia@reply.it">Fabrizio de Ceglia</a>
 * @author <a href="mailto:l.bracco@reply.it">Lorenzo Bracco</a>
 * @version 2.0.0
 */

@Path("")
public class HiPPI {

    private Logger logger;
    private HiReplySvc hiReplySvc;

    private String symbolicUri;
    private String ontBaseUri;
    private String contextsUri;

    private String transfProt;

    private String speedProp;
    private String colorProp;
    private String reverseSpeedProp;
    private String reverseColorProp;

    private String logVerbosity;
    private String hiLogVerbositySetting;

    private AtomicInteger requestCount;
    private AtomicInteger requestError;

    @Context
    private UriInfo uriInfo;

    public HiPPI() {

        ConfigReader configReader = ConfigReader.getInstance();

        hiReplySvc = new HiReplySvc();
        logger = LogManager.getLogger(HiPPI.class);

        symbolicUri = configReader.get(ConfigReader.SYMBOLIC_URI);
        ontBaseUri = configReader.get(ConfigReader.ONT_BASE_URI_PROPERTY);
        contextsUri = configReader.get(ConfigReader.CONTEXTS_LOC);

        transfProt = configReader.get(ConfigReader.TRANSF_PROTOCOL);

        speedProp = configReader.get(ConfigReader.SPEED_PROP);
        colorProp = configReader.get(ConfigReader.COLOR_PROP);
        reverseSpeedProp = configReader.get(ConfigReader.REVERSE_SPEED_PROP);
        reverseColorProp = configReader.get(ConfigReader.REVERSE_COLOR_PROP);

        logVerbosity = configReader.get(ConfigReader.LOG_VERBOSITY);
        hiLogVerbositySetting = configReader.get(ConfigReader.HI_LOGS_VERBOSITITY_SETTING);

        requestCount = new AtomicInteger(0);
        requestError = new AtomicInteger(0);

    }

    /**
     * Method that returns the metadata of System. This method is mandatory.
     * @param bodyRequest <br>
     *            JSON-LD String with the body request <br>
     *            { <br>
     *            } <br>
     * @return Returns a string with the serialized JSON-LD IoTSystem.
     */
    @Path("/metadata")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String getMetadata(String bodyRequest) throws Exception {

        int i;

        try {
            JsonUtils.deserializeJson(bodyRequest, EmptyRequest.class);
        } catch (IOException e) {
            this.logger.error("/METADATA error parsing request header");
            return "{\n" +
                    "\"error\": \"Malformed request body\"\n"+
                    "}";
        }

        ServiceList system = hiReplySvc.getSnapshot();

        IoTSystem ioTSystem = new IoTSystem();
        ArrayList<String> services = new ArrayList<>();
        ArrayList<String> sensors = new ArrayList<>();

        // Context must point to a real file
        ioTSystem.setContext(contextsUri + "system.jsonld");
        ioTSystem.setId(system.getIoTSystem().getUri());
        ioTSystem.setType("vital:VitalSystem"); // is it really it? Think so...
        ioTSystem.setName(system.getIoTSystem().getName());
        ioTSystem.setDescription(system.getIoTSystem().getDescription());

        ioTSystem.setOperator(system.getIoTSystem().getOperator());
        ioTSystem.setServiceArea("http://dbpedia.org/page/" + system.getIoTSystem().getServiceArea());

        List<ServiceList.TrafficSensor> trafficSensors = system.getTrafficSensor();
        for(i = 0; i < trafficSensors.size(); i++) {
            sensors.add(this.createSensorFromTraffic(trafficSensors.get(i)).getId());
        }
        sensors.add(this.createMonitoringSensor().getId());
        ioTSystem.setSensors(sensors);

        // Adding services (described in /service/metadata)
        services.add(this.transfProt + this.symbolicUri + "/service/configuration");
        services.add(this.transfProt + this.symbolicUri + "/service/monitoring");
        services.add(this.transfProt + this.symbolicUri + "/service/observation");

        ioTSystem.setServices(services);

        if (system.getIoTSystem().getStatus().equals("Running")) {
            ioTSystem.setStatus("vital:Running");
        }

        String out;

        try {
            out = JsonUtils.serializeJson(ioTSystem);
        } catch (IOException e) {
            this.logger.error("JSON UTILS IO EXCEPTION - metadata information");
            throw new Exception("JSON UTILS IO EXCEPTION - metadata information");
        }

        return out;
    }

    @Path("/system/performance")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getSupportedPerformanceMetrics() throws Exception {

        String out;

        PerformanceMetricsMetadata performanceMetricsMetadata = new PerformanceMetricsMetadata();

        List<Metric> list = new ArrayList<>();

        Metric metric = new Metric();
        metric.setType(this.transfProt + this.ontBaseUri + "UsedMem");
        metric.setId(this.transfProt + this.symbolicUri + "/sensor/monitoring/usedMem");
        list.add(metric);

        metric = new Metric();
        metric.setType(this.transfProt + this.ontBaseUri + "AvailableMem");
        metric.setId(this.transfProt + this.symbolicUri + "/sensor/monitoring/availableMem");
        list.add(metric);

        metric = new Metric();
        metric.setType(this.transfProt + this.ontBaseUri + "AvailableDisk");
        metric.setId(this.transfProt + this.symbolicUri + "/sensor/monitoring/availableDisk");
        list.add(metric);

        metric = new Metric();
        metric.setType(this.transfProt + this.ontBaseUri + "SysLoad");
        metric.setId(this.transfProt + this.symbolicUri + "/sensor/monitoring/sysLoad");
        list.add(metric);

        metric = new Metric();
        metric.setType(this.transfProt + this.ontBaseUri + "ServedRequests");
        metric.setId(this.transfProt + this.symbolicUri + "/sensor/monitoring/servedRequests");
        list.add(metric);

        metric = new Metric();
        metric.setType(this.transfProt + this.ontBaseUri + "Errors");
        metric.setId(this.transfProt + this.symbolicUri + "/sensor/monitoring/errors");
        list.add(metric);

        metric = new Metric();
        metric.setType(this.transfProt + this.ontBaseUri + "SysUptime");
        metric.setId(this.transfProt + this.symbolicUri + "/sensor/monitoring/sysUptime");
        list.add(metric);

        metric = new Metric();
        metric.setType(this.transfProt + this.ontBaseUri + "PendingRequests");
        metric.setId(this.transfProt + this.symbolicUri + "/sensor/monitoring/pendingRequests");
        list.add(metric);

        performanceMetricsMetadata.setMetrics(list);

        try {
            out = JsonUtils.serializeJson(performanceMetricsMetadata);
        } catch (IOException e) {
            this.logger.error("JSON UTILS IO EXCEPTION - getPerformanceMetric information");
            throw new Exception("JSON UTILS IO EXCEPTION - getPerformanceMetric information");
        }

        return out;
    }

    @Path("/system/performance")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String getPerformanceMetrics(String bodyRequest) throws Exception {

        int i, len;
        MetricRequest metricRequest;
        ArrayList<PerformanceMetric> metrics = new ArrayList<>();
        String pm;

        String out;

        try {
            metricRequest = (MetricRequest) JsonUtils.deserializeJson(bodyRequest, MetricRequest.class);
        } catch (IOException e) {
            this.logger.error("GET METRIC - IOException parsing the json request");
            return "{\n" +
                    "\"error\": \"Malformed request body\"\n"+
                    "}";
        }

        List<String>pms = metricRequest.getMetric();
        len = pms.size();
        for(i = 0; i < len; i++) {
            pm = pms.get(i).replaceAll("http://" + this.ontBaseUri, "");

            PerformanceMetric metric;
            if (pm.toLowerCase().contains("usedmem")) {
                metric = this.getMemoryUsed();
            } else if (pm.toLowerCase().contains("availablemem")) {
                metric = this.getMemoryAvailable();
            } else if (pm.toLowerCase().contains("availabledisk")) {
                metric = this.getDiskAvailable();
            } else if (pm.toLowerCase().contains("sysload")) {
                metric = this.getCpuUsage();
            } else if (pm.toLowerCase().contains("servedrequests")) {
                metric = this.getServedRequest();
            } else if (pm.toLowerCase().contains("errors")) {
                metric = this.getErrors();
            } else if (pm.toLowerCase().contains("sysuptime")) {
                metric = this.getUpTime();
            } else if (pm.toLowerCase().contains("pendingrequests")) {
                metric = this.getPendingRequest();
            } else {
                return "{\n" +
                        "\"error\": \"Performance " + pm.toLowerCase() + " not present.\"\n" +
                        "}";
            }

            if (metric != null) {
                metric.setSsnFeatureOfInterest(this.transfProt + this.symbolicUri);
                metrics.add(metric);
            }
        }

        try {
            out = JsonUtils.serializeJson(metrics);
        } catch (IOException e) {
            this.logger.error("GET METRIC - serialize to json response IO Exception");
            throw new Exception("GET METRIC - serialize to json response IO Exception");
        }

        return out;
    }

    @Path("/configuration")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getConfiguration() throws Exception {

        String out;

        ConfigurationGetBody response = new ConfigurationGetBody();

        Parameter parameter = new Parameter();
        List<Parameter> parameters = new ArrayList<>();

        parameter.setName(this.logVerbosity);
        parameter.setValue(this.hiReplySvc.getSnapshot().getTaskManager().getLogsPriorityLevel());
        parameter.setType("http://www.w3.org/2001/XMLSchema#string");
        parameter.setPermissions("rw");

        parameters.add(parameter);

        response.setParameters(parameters);

        try {
            out = JsonUtils.serializeJson(response);
        } catch (IOException e) {
            this.logger.error("getConfigurationOptions - Deserialize JSON UTILS IO EXCEPTION");
            throw new Exception("getConfigurationOptions - Deserialize JSON UTILS IO EXCEPTION");
        }

        return out;
    }

    @Path("/configuration")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response setConfiguration(String bodyRequest) {
        int i;

        ConfigurationReqBody configurationReqBody;
        boolean success = false;

        try {
            configurationReqBody = (ConfigurationReqBody) JsonUtils.deserializeJson(bodyRequest, ConfigurationReqBody.class);
        } catch (IOException e) {
            this.logger.error("setConfigurationOptions -  error parsing request header");
            return Response.serverError().build();
        }

        String taskManagerServiceId = this.hiReplySvc.getSnapshot().getTaskManager().getID();

        List<Parameter_> configList = configurationReqBody.getParameters();

        for(i = 0; i < configList.size(); i++) {
            String currentConfiguration = configList.get(i).getName();
            if(currentConfiguration.equals(this.logVerbosity)) {
                String logsPriorityValue = configList.get(i).getValue().toUpperCase();
                try {
                    success = this.hiReplySvc.setPropertyValue(taskManagerServiceId, hiLogVerbositySetting, logsPriorityValue);
                } catch (Exception e) {
                    success = false;
                }
            }
        }

        if(success) {
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    /**
     * Method that returns the status of the system. This method is not mandatory.
     * @return Returns a string with the serialized JSON-LD status information.
     */
    @Path("/system/status")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public String getSystemStatus(String bodyRequest) throws Exception {

        ServiceList system = hiReplySvc.getSnapshot();
        PerformanceMetric lifecycleInformation = new PerformanceMetric();

        Date now = new Date();
        String id = Long.toHexString(now.getTime());
        SimpleDateFormat printedDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

        lifecycleInformation.setContext(contextsUri + "measurement.jsonld");
        lifecycleInformation.setId(this.transfProt + this.symbolicUri + "/sensor/monitoring/observation/" + id);
        lifecycleInformation.setType("ssn:Observation");

        SsnObservationProperty_ ssnObservationProperty_ = new SsnObservationProperty_();
        ssnObservationProperty_.setType("vital:OperationalState");
        lifecycleInformation.setSsnObservationProperty(ssnObservationProperty_);

        SsnObservationResultTime_ ssnObservationResultTime_ = new SsnObservationResultTime_();

        ssnObservationResultTime_.setTimeInXSDDateTime(printedDateFormat.format(now));
        lifecycleInformation.setSsnObservationResultTime(ssnObservationResultTime_);

        SsnObservationResult_ ssnObservationResult_ = new SsnObservationResult_();
        ssnObservationResult_.setType("ssn:SensorOutput");
        SsnHasValue_ ssnHasValue_ = new SsnHasValue_();
        ssnHasValue_.setType("ssn:ObservationValue");
        if (system.getIoTSystem().getStatus().equals("Running")) {
            ssnHasValue_.setValue("vital:Running");
        } else {
            ssnHasValue_.setValue("vital:Unavailable");
        }
        ssnObservationResult_.setSsnHasValue(ssnHasValue_);
        lifecycleInformation.setSsnObservationResult(ssnObservationResult_);

        lifecycleInformation.setSsnFeatureOfInterest(this.transfProt + this.symbolicUri);

        String out;

        try {
            out = JsonUtils.serializeJson(lifecycleInformation);
        } catch (IOException e) {
            this.logger.error("JSON UTILS IO EXCEPTION - lifecycle information");
            throw new Exception("JSON UTILS IO EXCEPTION - lifecycle information");
        }

        return out;
    }

    /**
     * Method that returns the metadata about the requested service(s). This method is optional.
     * @param bodyRequest <br>
     *            JSON-LD String with the body request <br>
     *            { <br>
     *              "id": <br>
     *              [ <br>
     *                  "http://example.com/service/1" <br>
     *              ], <br>
     *              "type": <br>
     *              [ <br>
     *                  "http://vital-iot.eu/ontology/ns/MonitoringService" <br>
     *              ] <br>
     *            } <br>
     * If the id and type fields are omitted, all the services are requested <br>
     * @return Returns a serialized String with the list of the requested services. <br>
     */
    @Path("/service/metadata")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String getServiceMetadata(String bodyRequest) throws Exception {

        int i;
        IdTypeRequest serviceRequest;

        try {
            serviceRequest = (IdTypeRequest) JsonUtils.deserializeJson(bodyRequest, IdTypeRequest.class);
        } catch (IOException e) {
            this.logger.error("GET SERVICE METADATA - IOException parsing the json request");
            return "{\n" +
                    "\"error\": \"Malformed request body\"\n"+
                    "}";
        }

        List<String> requestedService;
        List<String> requestedType;

        try {
            requestedService = serviceRequest.getId();
            requestedType = serviceRequest.getType();
        } catch (NullPointerException e) {
            this.logger.error("/service/metadata IO Exception - Requested Sensor");
            throw new Exception("/service/metadata IO Exception - Requested Sensor");
        }

        List<Service> services = new ArrayList<>(); // output list

        if ((requestedService.size() == 0) && (requestedType.size() == 0)) {
            // then all the services must be returned
            services.add(createObservationService());
            services.add(createMonitoringService());
            services.add(createConfigurationService());
        } else {
            String currentType;
            Service tmpService;
            for (i = 0; i < requestedType.size(); i++) {
                currentType = requestedType.get(i).replaceAll("http://" + this.ontBaseUri, "");
                if (currentType.contains("ObservationService")) {
                    tmpService = this.createObservationService();
                    if(!services.contains(tmpService)) {
                        services.add(tmpService);
                    }
                }
                else {
                    if (currentType.contains("MonitoringService")) {
                        tmpService = this.createMonitoringService();
                        if (!services.contains(tmpService)) {
                            services.add(tmpService);
                        }
                    }
                    else {
                        if (currentType.contains("ConfigurationService")) {
                            tmpService = this.createConfigurationService();
                            if (!services.contains(tmpService)) {
                                services.add(tmpService);
                            }
                        }
                    }
                }
            }
            // return only some selected services
            String currentId;
            for (i = 0; i < requestedService.size(); i++) {
                currentId = requestedService.get(i).replaceAll(this.transfProt + this.symbolicUri + "/service/", "");
                if(currentId.contains("observation")) {
                    tmpService = this.createObservationService();
                    if(!services.contains(tmpService)) {
                        services.add(tmpService);
                    }
                }
                else {
                    if(currentId.contains("monitoring")) {
                        tmpService = this.createMonitoringService();
                        if(!services.contains(tmpService)) {
                            services.add(tmpService);
                        }
                    }
                    else {
                        if(currentId.contains("configuration")) {
                            tmpService = this.createConfigurationService();
                            if(!services.contains(tmpService)) {
                                services.add(tmpService);
                            }
                        }
                    }
                }
            }
        }

        String out;

        try {
            out = JsonUtils.serializeJson(services);
        } catch (IOException e) {
            this.logger.error("getSensorMetadata - Deserialize JSON UTILS IO EXCEPTION");
            throw new Exception("getSensorMetadata - Deserialize JSON UTILS IO EXCEPTION");
        }

        return out;
    }

    /**
     * Method that returns the metadata about the requested sensor(s). This method is mandatory.
     * @param bodyRequest <br>
     *            JSON-LD String with the body request <br>
     *            { <br>
     *              "id": <br>
     *              [ <br>
     *                  "http://www.example.com/ico/123/", <br>
     *                  "http://www.example.com/ico/1234/", <br>
     *                  "http://www.example.com/ico/12345/" <br>
     *              ], <br>
     *              "type": <br>
     *              [ <br>
     *                  "http://vital-iot.eu/ontology/ns/VitalSensor", <br>
     *                  "http://vital-iot.eu/ontology/ns/MonitoringSensor" <br>
     *              ] <br>
     *            } <br>
     * If the id and type fields are omitted, all the sensors are requested <br>
     * @return Returns a serialized String with the list of the requested sensors. <br>
     */
    @Path("/sensor/metadata")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String getSensorMetadata(String bodyRequest) throws Exception {

        int i, j;
        IdTypeRequest sensorRequest;
        ServiceList system = null;

        try {
            sensorRequest = (IdTypeRequest) JsonUtils.deserializeJson(bodyRequest, IdTypeRequest.class);
        } catch (IOException e) {
            this.logger.error("GET SENSOR METADATA - IOException parsing the json request");
            return "{\n" +
                    "\"error\": \"Malformed request body\"\n"+
                    "}";
        }

        List<String> requestedSensor;
        List<String> requestedType;

        try {
            requestedSensor = sensorRequest.getId();
            requestedType = sensorRequest.getType();
        } catch (NullPointerException e) {
            this.logger.error("/sensor/metadata IO Exception - Requested Sensor");
            throw new Exception("/sensor/metadata IO Exception - Requested Sensor");
        }

        List<Sensor> sensors = new ArrayList<>(); // output list

        if ((requestedSensor.size() == 0) && (requestedType.size() == 0)) {
            // then all the sensors must be returned
        	if(system == null) {
        		system = hiReplySvc.getSnapshot();
        	}
            List<ServiceList.TrafficSensor> trafficSensors = system.getTrafficSensor();
            for (i = 0; i < trafficSensors.size(); i++) {
                sensors.add(this.createSensorFromTraffic(trafficSensors.get(i)));
            }
            sensors.add(this.createMonitoringSensor());
        } else {
            String currentType;
            Sensor tmpSensor;
            for (i = 0; i < requestedType.size(); i++) {
                currentType = requestedType.get(i).replaceAll("http://" + this.ontBaseUri, "");
                if (currentType.toLowerCase().contains("monitoringsensor")) {
                    tmpSensor = this.createMonitoringSensor();
                    if(!sensors.contains(tmpSensor)) {
                        sensors.add(tmpSensor);
                    }
                } else {
                	currentType = requestedType.get(i).replaceAll("http://" + this.ontBaseUri, "");
                    if (currentType.toLowerCase().contains("vitalsensor")) {
                    	if(system == null) {
                    		system = hiReplySvc.getSnapshot();
                    	}
	                    List<ServiceList.TrafficSensor> trafficSensors = system.getTrafficSensor();
	                    for(j = 0; j < trafficSensors.size(); j++) {
	                        tmpSensor = this.createSensorFromTraffic(trafficSensors.get(j));
	                        if(!sensors.contains(tmpSensor)) {
	                            sensors.add(tmpSensor);
	                        }
	                    }
                    }
                }
            }
            // return only some selected sensors
            String currentId;
            for (i = 0; i < requestedSensor.size(); i++) {
                currentId = requestedSensor.get(i).replaceAll(this.transfProt + this.symbolicUri + "/sensor/", "");

                if (currentId.toLowerCase().contains("monitoring")) {
                    tmpSensor = this.createMonitoringSensor();
                    if(!sensors.contains(tmpSensor)) {
                        sensors.add(tmpSensor);
                    }
                } else {
                    String filter = hiReplySvc.createFilter("ID", currentId);

                    ServiceList.TrafficSensor currentTrafficSensor;

                    try {
                        currentTrafficSensor = this.hiReplySvc.getSnapshotFiltered(filter).getTrafficSensor().get(0);
                        tmpSensor = this.createSensorFromTraffic(currentTrafficSensor);
                        if(!sensors.contains(tmpSensor)) {
                            sensors.add(tmpSensor);
                        }
                    } catch (IndexOutOfBoundsException e) {
                        logger.error("getSensorMetadata ID: " + currentId + " not present.");
                        // if not present goes on looking for the other requested sensors
                    }
                }

            }
        }

        String out;

        try {
            out = JsonUtils.serializeJson(sensors);
        } catch (IOException e) {
            this.logger.error("getSensorMetadata - Deserialize JSON UTILS IO EXCEPTION");
            throw new Exception("getSensorMetadata - Deserialize JSON UTILS IO EXCEPTION");
        }

        return out;
    }

    /**
     * Method that returns the status of the requested sensor(s). This method is optional.
     * @param bodyRequest <br>
     *            JSON-LD String with the body request <br>
     *            { <br>
     *              "id": <br>
     *              [ <br>
     *                  "http://www.example.com/ico/123/", <br>
     *                  "http://www.example.com/ico/1234/", <br>
     *                  "http://www.example.com/ico/12345/" <br>
     *              ], <br>
     *              "type": <br>
     *              [ <br>
     *                  "http://vital-iot.eu/ontology/ns/VitalSensor", <br>
     *                  "http://vital-iot.eu/ontology/ns/MonitoringSensor" <br>
     *              ] <br>
     *            } <br>
     * If the id and type fields are omitted, all the sensors are requested <br>
     * @return Returns a serialized String with the list of the requested sensors. <br>
     */
    @Path("/sensor/status")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String getSensorStatus(String bodyRequest) throws Exception {

        int i, j;
        IdTypeRequest sensorRequest;
        ServiceList system = null;
        

        try {
            sensorRequest = (IdTypeRequest) JsonUtils.deserializeJson(bodyRequest, IdTypeRequest.class);
        } catch (IOException e) {
            this.logger.error("GET SENSOR STATUS - IOException parsing the json request");
            return "{\n" +
                    "\"error\": \"Malformed request body\"\n"+
                    "}";
        }

        List<String> requestedSensor;
        List<String> requestedType;

        try {
            requestedSensor = sensorRequest.getId();
            requestedType = sensorRequest.getType();
        } catch (NullPointerException e) {
            this.logger.error("/sensor/status IO Exception - Requested Sensor");
            throw new Exception("/sensor/status IO Exception - Requested Sensor");
        }

        ArrayList<SensorStatus> measures = new ArrayList<>();

        if ((requestedSensor.size() == 0) && (requestedType.size() == 0)) {
            // then all the sensors must be returned
        	if(system == null) {
        		system = hiReplySvc.getSnapshot();
        	}
            List<ServiceList.TrafficSensor> trafficSensors = system.getTrafficSensor();
            for (i = 0; i < trafficSensors.size(); i++) {
                measures.add(this.createStatusMeasureFromSensor(trafficSensors.get(i), "OperationalState"));
            }
            //sensors.add(this.createMonitoringSensor());
        } else {
            String currentType;
            SensorStatus tmpMeasure;
            for (i = 0; i < requestedType.size(); i++) {
            	currentType = requestedType.get(i).replaceAll("http://" + this.ontBaseUri, "");
                if (currentType.toLowerCase().contains("monitoringsensor")) {
                    //tmpSensor = this.createMonitoringSensor();
                    //if(!sensors.contains(tmpSensor)) {
                    //    sensors.add(tmpSensor);
                    //}
                } else {
                	currentType = requestedType.get(i).replaceAll("http://" + this.ontBaseUri, "");
                    if (currentType.toLowerCase().contains("vitalsensor")) {
                    	if(system == null) {
                    		system = hiReplySvc.getSnapshot();
                    	}
	                    List<ServiceList.TrafficSensor> trafficSensors = system.getTrafficSensor();
	                    for(j = 0; j < trafficSensors.size(); j++) {
	                    	tmpMeasure = this.createStatusMeasureFromSensor(trafficSensors.get(j), "OperationalState");
	                    	if(!measures.contains(tmpMeasure)) {
	                    		measures.add(tmpMeasure);
	                    	}
	                    }
                    }
                }
            }
            // return only some selected sensors
            String currentId;
            for (i = 0; i < requestedSensor.size(); i++) {
                currentId = requestedSensor.get(i).replaceAll(this.transfProt + this.symbolicUri + "/sensor/", "");

                if (currentId.toLowerCase().contains("monitoring")) {
                    //tmpSensor = this.createMonitoringSensor();
                    //if(!sensors.contains(tmpSensor)) {
                    //    sensors.add(tmpSensor);
                    //}
                } else {
                    String filter = hiReplySvc.createFilter("ID", currentId);

                    ServiceList.TrafficSensor currentTrafficSensor;

                    try {
                        currentTrafficSensor = this.hiReplySvc.getSnapshotFiltered(filter).getTrafficSensor().get(0);
                        tmpMeasure = this.createStatusMeasureFromSensor(currentTrafficSensor, "OperationalState");
                    	if(!measures.contains(tmpMeasure)) {
                    		measures.add(tmpMeasure);
                    	}
                    } catch (IndexOutOfBoundsException e) {
                        logger.error("getSensorStatus ID: " + currentId + " not present.");
                        // if not present goes on looking for the other requested sensors
                    }
                }

            }
        }

        String out;

        try {
            out = JsonUtils.serializeJson(measures);
        } catch (IOException e) {
            this.logger.error("getSensorMetadata - Deserialize JSON UTILS IO EXCEPTION");
            throw new Exception("getSensorMetadata - Deserialize JSON UTILS IO EXCEPTION");
        }

        return out;
    }

    /**
     * Method that returns the observation about the requested ICO/ICOs. This method is mandatory.
     * @param bodyRequest <br>
     *            JSON-LD String with the body request <br>
     *            { <br>
     *              "sensor":
     *              [ <br>
     *                  "http://www.example.com/ico/123/", <br>
     *                  "http://www.example.com/ico/1234/", <br>
     *                  "http://www.example.com/ico/12345/" <br>
     *              ], <br>
     *              "property": "http://lsm.deri.ie/OpenIot/Temperature", <br>
     *              "from": "2014-11-17T09:00:00+02:00", <br>
     *              "to": "2014-11-17T11:00:00+02:00" <br>
     *            } <br>
     * <b>from</b> and <b>to</b> determine the time interval, when the observations to return were taken.
     * Both <b>to</b> and <b>from</b> are optional:
     * <ul>
     *     <li>
     *     If to is omitted, then all observations taken after from are returned
     *     </li>
     *     <li>
     *     If both from and to are omitted, then the last observation taken from the specified ICO for the specified property is returned
     *     </li>
     * </ul>
     * <b>property</b> is the requested property
     * @return Returns a serialized String with the list of the requested measures.
     */
    @Path("/sensor/observation")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String getObservation(String bodyRequest) throws Exception {
        int i, s, len;
        ObservationRequest observationRequest;
        ArrayList<Measure> measures = new ArrayList<>();
        ArrayList<PerformanceMetric> metrics = new ArrayList<>();
        String id;

        String out;

        try {
            observationRequest = (ObservationRequest) JsonUtils.deserializeJson(bodyRequest, ObservationRequest.class);
        } catch (IOException e) {
            this.logger.error("GET OBSERVATION - IOException parsing the json request");
            return "{\n" +
                    "\"error\": \"Malformed request body\"\n"+
                    "}";
        }

        List<String>ids = observationRequest.getSensor();
        len = ids.size();
        for(s = 0; s < len; s++) {
            id = ids.get(s).replaceAll(this.transfProt + this.symbolicUri + "/sensor/", "");

            if (id.contains("monitoring")) {
                // Monitoring sensor
                PerformanceMetric metric;
                // get the requested property
                String requestedProperty = observationRequest.getProperty();
                SsnObservationProperty_ ob;
                if (requestedProperty.toLowerCase().contains("usedmem")) {
                    metric = this.getMemoryUsed();
                } else if (requestedProperty.toLowerCase().contains("availablemem")) {
                    metric = this.getMemoryAvailable();
                } else if (requestedProperty.toLowerCase().contains("availabledisk")) {
                    metric = this.getDiskAvailable();
                } else if (requestedProperty.toLowerCase().contains("sysload")) {
                    metric = this.getCpuUsage();
                } else if (requestedProperty.toLowerCase().contains("servedrequests")) {
                    metric = this.getServedRequest();
                } else if (requestedProperty.toLowerCase().contains("errors")) {
                    metric = this.getErrors();
                } else if (requestedProperty.toLowerCase().contains("sysuptime")) {
                    metric = this.getUpTime();
                } else if (requestedProperty.toLowerCase().contains("pendingrequests")) {
                    metric = this.getPendingRequest();
                } else {
                    return "{\n" +
                            "\"error\": \"Performance " + requestedProperty + " not present.\"\n" +
                            "}";
                }

                if(metric != null) {
                    metric.setSsnObservedBy(ids.get(s));
                    ob = new SsnObservationProperty_();
                    ob.setType("http://" + this.ontBaseUri + metric.getSsnObservationProperty().getType().replaceAll("vital:", ""));
                    metric.setSsnObservationProperty(ob);
                    metrics.add(metric);
                }

                try {
                    out = JsonUtils.serializeJson(metrics);
                } catch (IOException e) {
                    this.logger.error("GET OBSERVATION - serialize to json response IO Exception");
                    throw new Exception("GET OBSERVATION - serialize to json response IO Exception");
                }

                return out;

            } else {
                // check if the sensor exists
                ServiceList.TrafficSensor currentSensor = this.retrieveSensor(id);

                if(currentSensor == null) {
                    return "{\n" +
                            "\"error\": \"ID " + id + " not present.\"\n" +
                            "}";
                }

                // check if the sensor has the requested property
                String property = observationRequest.getProperty().replaceAll("http://" + this.ontBaseUri, "");

                if(!this.checkTrafficProperty(currentSensor, property)) {
                    return "{\n" +
                            "\"error\": \"Property " + property + " not present for " + id + " sensor.\"\n" +
                            "}";
                }

                if(observationRequest.getFrom() != null && observationRequest.getTo() != null) {
                    // get history range
                    SimpleDateFormat arrivedFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
                    SimpleDateFormat hiReplyFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

                    Date fromDate;
                    Date toDate;
                    Date fromDateHiReply;
                    Date toDateHiReply;

                    try {
                        fromDate = arrivedFormat.parse(observationRequest.getFrom());
                        toDate = arrivedFormat.parse(observationRequest.getTo());
                    } catch (ParseException e) {
                        this.logger.error("GET OBSERVATION - Parse exception during parse date");
                        return "{\n" +
                                "\"error\": \"Malformed date in the request body\"\n" +
                                "}";
                    }

                    try {
                        fromDateHiReply = hiReplyFormat.parse(hiReplyFormat.format(fromDate));
                        toDateHiReply = hiReplyFormat.parse(hiReplyFormat.format(toDate));
                    } catch (ParseException e) {
                        this.logger.error("GET OBSERVATION - Parse exception during parse date");
                        return "{\n" +
                                "\"error\": \"Malformed date in the request body\"\n" +
                                "}";
                    }

                    List<HistoryMeasure> historyMeasures = this.getHistoryMeasures(hiReplySvc.getPropertyHistoricalValues(id, property, fromDateHiReply, toDateHiReply));

                    for(i = 0; i < historyMeasures.size(); i++) {
                    	Measure tmpMes;
                    	tmpMes = this.createMeasureFromHistoryMeasure(historyMeasures.get(i), currentSensor, property);
                    	if(tmpMes != null) {
                    		measures.add(tmpMes);
                    	}
                    }

                } else if(observationRequest.getFrom() != null && observationRequest.getTo() == null) {
                    // get all values since from
                    SimpleDateFormat arrivedFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    SimpleDateFormat hiReplyFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

                    Date fromDate;
                    Date toDate = new Date(); // hireply still needs end date (use current date)
                    Date fromDateHiReply;
                    Date toDateHiReply;

                    try {
                        fromDate = arrivedFormat.parse(observationRequest.getFrom());
                    } catch (ParseException e) {
                        this.logger.error("GET OBSERVATION - Parse exception during parse date");
                        return "{\n" +
                                "\"error\": \"Malformed date in the request body\"\n" +
                                "}";
                    }

                    try {
                        fromDateHiReply = hiReplyFormat.parse(hiReplyFormat.format(fromDate));
                        toDateHiReply = hiReplyFormat.parse(hiReplyFormat.format(toDate));
                    } catch (ParseException e) {
                        this.logger.error("GET OBSERVATION - Parse exception during parse date for hi reply format");
                        throw new Exception("GET OBSERVATION - Parse exception during parse date for hi reply format");
                    }

                    List<HistoryMeasure> historyMeasures = this.getHistoryMeasures(hiReplySvc.getPropertyHistoricalValues(id, property, fromDateHiReply, toDateHiReply));

                    for(i = 0; i < historyMeasures.size(); i++) {
                    	Measure tmpMes;
                    	tmpMes = this.createMeasureFromHistoryMeasure(historyMeasures.get(i), currentSensor, property);
                    	if(tmpMes != null) {
                    		measures.add(tmpMes);
                    	}
                    }

                } else if(observationRequest.getFrom() == null && observationRequest.getTo() == null) {
                    // get last value only
                	Measure tmpMes;
                	tmpMes = this.createMeasureFromSensor(currentSensor, property);
                	if(tmpMes != null) {
                		measures.add(tmpMes);
                	}
                }
            }
        }

        try {
            out = JsonUtils.serializeJson(measures);
        } catch (IOException e) {
            this.logger.error("GET OBSERVATION - serialize to json response IO Exception");
            throw new Exception("GET OBSERVATION - serialize to json response IO Exception");
        }

        return out;
    }

    private PerformanceMetric getPendingRequest() throws Exception {

        /* Minus 1 because this method has not yet ended (is still pending) */

        int pendingRequest = StatCounter.getPendingRequest() - 1;

        Date now = new Date();
        String id = Long.toHexString(now.getTime());
        SimpleDateFormat printedDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

        PerformanceMetric pendingReq = new PerformanceMetric();

        pendingReq.setContext(contextsUri + "measurement.jsonld");
        pendingReq.setId(this.transfProt + this.symbolicUri + "/sensor/monitoring/observation/" + id);
        pendingReq.setType("ssn:Observation");

        SsnObservationProperty_ ssnObservationProperty_ = new SsnObservationProperty_();
        ssnObservationProperty_.setType("vital:PendingRequests");

        pendingReq.setSsnObservationProperty(ssnObservationProperty_);

        SsnObservationResultTime_ ssnObservationResultTime_ = new SsnObservationResultTime_();

        ssnObservationResultTime_.setTimeInXSDDateTime(printedDateFormat.format(now));
        pendingReq.setSsnObservationResultTime(ssnObservationResultTime_);

        SsnObservationResult_ ssnObservationResult_ = new SsnObservationResult_();
        ssnObservationResult_.setType("ssn:SensorOutput");
        SsnHasValue_ ssnHasValue_ = new SsnHasValue_();
        ssnHasValue_.setType("ssn:ObservationValue");
        ssnHasValue_.setValue(pendingRequest + "");
        ssnHasValue_.setQudtUnit("qudt:number");
        ssnObservationResult_.setSsnHasValue(ssnHasValue_);
        pendingReq.setSsnObservationResult(ssnObservationResult_);

        return pendingReq;
    }

    private PerformanceMetric getUpTime() throws Exception {

        Date now = new Date();
        String id = Long.toHexString(now.getTime());

        Date hiReplyStartTime = this.hiReplySvc.getSnapshot().getTaskManager().getLastStartTime().toGregorianCalendar().getTime();

        long span = now.getTime() - hiReplyStartTime.getTime();

        SimpleDateFormat printedDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

        PerformanceMetric sysUpTime = new PerformanceMetric();

        sysUpTime.setContext(contextsUri + "measurement.jsonld");
        sysUpTime.setId(this.transfProt + this.symbolicUri + "/sensor/monitoring/observation/" + id);
        sysUpTime.setType("ssn:Observation");

        SsnObservationProperty_ ssnObservationProperty_ = new SsnObservationProperty_();
        ssnObservationProperty_.setType("vital:SysUptime");

        sysUpTime.setSsnObservationProperty(ssnObservationProperty_);

        SsnObservationResultTime_ ssnObservationResultTime_ = new SsnObservationResultTime_();

        ssnObservationResultTime_.setTimeInXSDDateTime(printedDateFormat.format(now));
        sysUpTime.setSsnObservationResultTime(ssnObservationResultTime_);

        SsnObservationResult_ ssnObservationResult_ = new SsnObservationResult_();
        ssnObservationResult_.setType("ssn:SensorOutput");
        SsnHasValue_ ssnHasValue_ = new SsnHasValue_();
        ssnHasValue_.setType("ssn:ObservationValue");
        ssnHasValue_.setValue(span+"");
        ssnHasValue_.setQudtUnit("qudt:milliseconds");
        ssnObservationResult_.setSsnHasValue(ssnHasValue_);
        sysUpTime.setSsnObservationResult(ssnObservationResult_);

        return sysUpTime;
    }

    private PerformanceMetric getServedRequest() throws Exception {

        /* Plus 1 for the current request */

        requestCount = StatCounter.getRequestNumber();
        int auxCount = requestCount.get();
        requestCount.set(auxCount + 1);

        Date now = new Date();
        String id = Long.toHexString(now.getTime());
        SimpleDateFormat printedDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

        PerformanceMetric servedRequest = new PerformanceMetric();

        servedRequest.setContext(contextsUri + "measurement.jsonld");
        servedRequest.setId(this.transfProt + this.symbolicUri + "/sensor/monitoring/observation/" + id);
        servedRequest.setType("ssn:Observation");

        SsnObservationProperty_ ssnObservationProperty_ = new SsnObservationProperty_();
        ssnObservationProperty_.setType("vital:ServedRequests");

        servedRequest.setSsnObservationProperty(ssnObservationProperty_);

        SsnObservationResultTime_ ssnObservationResultTime_ = new SsnObservationResultTime_();

        ssnObservationResultTime_.setTimeInXSDDateTime(printedDateFormat.format(now));
        servedRequest.setSsnObservationResultTime(ssnObservationResultTime_);

        SsnObservationResult_ ssnObservationResult_ = new SsnObservationResult_();
        ssnObservationResult_.setType("ssn:SensorOutput");
        SsnHasValue_ ssnHasValue_ = new SsnHasValue_();
        ssnHasValue_.setType("ssn:ObservationValue");
        ssnHasValue_.setValue(requestCount.get()+"");
        ssnHasValue_.setQudtUnit("qudt:Number");
        ssnObservationResult_.setSsnHasValue(ssnHasValue_);
        servedRequest.setSsnObservationResult(ssnObservationResult_);

        return servedRequest;
    }

    private PerformanceMetric getErrors() throws Exception {

        requestError = StatCounter.getErrorNumber();

        Date now = new Date();
        String id = Long.toHexString(now.getTime());
        SimpleDateFormat printedDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

        PerformanceMetric errors = new PerformanceMetric();

        errors.setContext(contextsUri + "measurement.jsonld");
        errors.setId(this.transfProt + this.symbolicUri + "/sensor/monitoring/observation/" + id);
        errors.setType("ssn:Observation");

        SsnObservationProperty_ ssnObservationProperty_ = new SsnObservationProperty_();
        ssnObservationProperty_.setType("vital:Errors");

        errors.setSsnObservationProperty(ssnObservationProperty_);

        SsnObservationResultTime_ ssnObservationResultTime_ = new SsnObservationResultTime_();

        ssnObservationResultTime_.setTimeInXSDDateTime(printedDateFormat.format(now));
        errors.setSsnObservationResultTime(ssnObservationResultTime_);

        SsnObservationResult_ ssnObservationResult_ = new SsnObservationResult_();
        ssnObservationResult_.setType("ssn:SensorOutput");
        SsnHasValue_ ssnHasValue_ = new SsnHasValue_();
        ssnHasValue_.setType("ssn:ObservationValue");
        ssnHasValue_.setValue(requestError.get()+"");
        ssnHasValue_.setQudtUnit("qudt:Number");
        ssnObservationResult_.setSsnHasValue(ssnHasValue_);
        errors.setSsnObservationResult(ssnObservationResult_);

        return errors;
    }

    private PerformanceMetric getMemoryUsed() throws Exception {

        ServiceList.TaskManager tm = this.hiReplySvc.getSnapshot().getTaskManager();

        BigInteger memoryUsed = tm.getMemoryConsumption();
        Date now = new Date();
        String id = Long.toHexString(now.getTime());
        SimpleDateFormat printedDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");


        PerformanceMetric memUsed = new PerformanceMetric();

        memUsed.setContext(contextsUri + "measurement.jsonld");
        memUsed.setId(this.transfProt + this.symbolicUri + "/sensor/monitoring/observation/" + id);
        memUsed.setType("ssn:Observation");

        SsnObservationProperty_ ssnObservationProperty_ = new SsnObservationProperty_();
        ssnObservationProperty_.setType("vital:UsedMem");
        memUsed.setSsnObservationProperty(ssnObservationProperty_);

        SsnObservationResultTime_ ssnObservationResultTime_ = new SsnObservationResultTime_();

        ssnObservationResultTime_.setTimeInXSDDateTime(printedDateFormat.format(now));
        memUsed.setSsnObservationResultTime(ssnObservationResultTime_);

        SsnObservationResult_ ssnObservationResult_ = new SsnObservationResult_();
        ssnObservationResult_.setType("ssn:SensorOutput");
        SsnHasValue_ ssnHasValue_ = new SsnHasValue_();
        ssnHasValue_.setType("ssn:ObservationValue");
        ssnHasValue_.setValue(memoryUsed.toString());
        ssnHasValue_.setQudtUnit("qudt:Byte");
        ssnObservationResult_.setSsnHasValue(ssnHasValue_);
        memUsed.setSsnObservationResult(ssnObservationResult_);

        return memUsed;
    }

    private PerformanceMetric getMemoryAvailable() throws Exception {

        ServiceList.TaskManager tm = this.hiReplySvc.getSnapshot().getTaskManager();

        BigInteger memAvailable = tm.getAvailMemoryCounter();
        Date now = new Date();
        String id = Long.toHexString(now.getTime());
        SimpleDateFormat printedDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

        PerformanceMetric memAval = new PerformanceMetric();

        memAval.setContext(contextsUri + "measurement.jsonld");
        memAval.setId(this.transfProt + this.symbolicUri + "/sensor/monitoring/observation/" + id);
        memAval.setType("ssn:Observation");

        SsnObservationProperty_ ssnObservationProperty_ = new SsnObservationProperty_();
        ssnObservationProperty_.setType("vital:AvailableMem");
        memAval.setSsnObservationProperty(ssnObservationProperty_);

        SsnObservationResultTime_ ssnObservationResultTime_ = new SsnObservationResultTime_();

        ssnObservationResultTime_.setTimeInXSDDateTime(printedDateFormat.format(now));
        memAval.setSsnObservationResultTime(ssnObservationResultTime_);

        SsnObservationResult_ ssnObservationResult_ = new SsnObservationResult_();
        ssnObservationResult_.setType("ssn:SensorOutput");
        SsnHasValue_ ssnHasValue_ = new SsnHasValue_();
        ssnHasValue_.setType("ssn:ObservationValue");
        ssnHasValue_.setValue(memAvailable.toString());
        ssnHasValue_.setQudtUnit("qudt:Byte");
        ssnObservationResult_.setSsnHasValue(ssnHasValue_);
        memAval.setSsnObservationResult(ssnObservationResult_);

        return memAval;
    }

    private PerformanceMetric getCpuUsage() throws Exception {

        ServiceList.TaskManager tm = this.hiReplySvc.getSnapshot().getTaskManager();

        float cpuUsage = tm.getCPUTotalCounter();
        Date now = new Date();
        String id = Long.toHexString(now.getTime());
        SimpleDateFormat printedDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

        PerformanceMetric load = new PerformanceMetric();

        load.setContext(contextsUri + "measurement.jsonld");
        load.setId(this.transfProt + this.symbolicUri + "/sensor/monitoring/observation/" + id);
        load.setType("ssn:Observation");

        SsnObservationProperty_ ssnObservationProperty_ = new SsnObservationProperty_();
        ssnObservationProperty_.setType("vital:SysLoad");
        load.setSsnObservationProperty(ssnObservationProperty_);

        SsnObservationResultTime_ ssnObservationResultTime_ = new SsnObservationResultTime_();

        ssnObservationResultTime_.setTimeInXSDDateTime(printedDateFormat.format(now));
        load.setSsnObservationResultTime(ssnObservationResultTime_);

        SsnObservationResult_ ssnObservationResult_ = new SsnObservationResult_();
        ssnObservationResult_.setType("ssn:SensorOutput");
        SsnHasValue_ ssnHasValue_ = new SsnHasValue_();
        ssnHasValue_.setType("ssn:ObservationValue");
        ssnHasValue_.setValue(cpuUsage+"");
        ssnHasValue_.setQudtUnit("qudt:Percentage");
        ssnObservationResult_.setSsnHasValue(ssnHasValue_);
        load.setSsnObservationResult(ssnObservationResult_);

        return load;
    }

    private PerformanceMetric getDiskAvailable() throws Exception {

        ServiceList.TaskManager tm = this.hiReplySvc.getSnapshot().getTaskManager();

        String strDiskAvailable = tm.getFreeDiskSpace();

        int bkSlashIndex = strDiskAvailable.indexOf("\\");
        int freeDiskSpace = Integer.parseInt (strDiskAvailable.substring(bkSlashIndex+2).replaceAll("\\s+",""));

        Date now = new Date();
        String id = Long.toHexString(now.getTime());
        SimpleDateFormat printedDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

        PerformanceMetric diskAval = new PerformanceMetric();

        diskAval.setContext(contextsUri + "measurement.jsonld");
        diskAval.setId(this.transfProt + this.symbolicUri + "/sensor/monitoring/observation/" + id);
        diskAval.setType("ssn:Observation");

        SsnObservationProperty_ ssnObservationProperty_ = new SsnObservationProperty_();
        ssnObservationProperty_.setType("vital:AvailableDisk");
        diskAval.setSsnObservationProperty(ssnObservationProperty_);

        SsnObservationResultTime_ ssnObservationResultTime_ = new SsnObservationResultTime_();

        ssnObservationResultTime_.setTimeInXSDDateTime(printedDateFormat.format(now));
        diskAval.setSsnObservationResultTime(ssnObservationResultTime_);

        SsnObservationResult_ ssnObservationResult_ = new SsnObservationResult_();
        ssnObservationResult_.setType("ssn:SensorOutput");
        SsnHasValue_ ssnHasValue_ = new SsnHasValue_();
        ssnHasValue_.setType("ssn:ObservationValue");
        ssnHasValue_.setValue(freeDiskSpace+"");
        ssnHasValue_.setQudtUnit("qudt:Byte");
        ssnObservationResult_.setSsnHasValue(ssnHasValue_);
        diskAval.setSsnObservationResult(ssnObservationResult_);

        return diskAval;
    }

    /*
        Private Class and Methods that adapt
        HiReply structure with Vital structure
    */
    private class HistoryMeasure {
        private float value;
        private Date date;

        public HistoryMeasure(float value, Date date) {
            this.value = value;
            this.date = date;
        }

        public float getValue() {
            return this.value;
        }

        public Date getDate() {
            return this.date;
        }
    }

    private List<HistoryMeasure> getHistoryMeasures(ValueList valueList) throws Exception {

        ArrayList<HistoryMeasure> historyMeasures = new ArrayList<>();
        List<String> values = valueList.getValue();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy HH:mm:ss");

        for(String currentValue : values) {
            String[] splitted = currentValue.split(","); // splitted[0] = value --- splitted[1] data
            float auxValue = Float.parseFloat(splitted[0]);
            Date auxDate;

            try {
                auxDate = dateFormat.parse(splitted[1]);
            } catch (ParseException e) {
                this.logger.error("ERROR PARSING DATE FROM HISTORY VALUE");
                throw new Exception("ERROR PARSING DATE FROM HISTORY VALUE");
            }

            historyMeasures.add(new HistoryMeasure(auxValue, auxDate));
        }

        return historyMeasures;
    }

    private Sensor createSensorFromTraffic(ServiceList.TrafficSensor currentSensor) throws Exception {
        Sensor sensor = new Sensor();
        String id = currentSensor.getID();

        sensor.setContext(contextsUri + "sensor.jsonld");
        sensor.setName(id);
        sensor.setType("vital:VitalSensor");
        sensor.setDescription(currentSensor.getDescription());
        sensor.setId(this.transfProt + this.symbolicUri + "/sensor/" + id);

        int status = currentSensor.getStatus();

        if (status == 1) {
            SimpleDateFormat timestampDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date timestamp = null;
        	String timestampS = currentSensor.getMeasureTime().toString();
            try {
                timestamp = timestampDateFormat.parse(timestampS);
            } catch (ParseException e) {
                this.logger.error("HiPPI - createSensorFromTraffic - ERROR PARSING DATE FROM HIREPLY TIMESTAMP");
                throw new Exception("HiPPI - createSensorFromTraffic - ERROR PARSING DATE FROM HIREPLY TIMESTAMP");
            }
            Date now = new Date();
            if(now.getTime() - timestamp.getTime() > 60 * 1000 * 60) { // If not updated for some time (1 hour)
            	sensor.setStatus("vital:Unavailable");
            }
            else {
            	sensor.setStatus("vital:Running");
            }
        } else if (status == 0) {
            sensor.setStatus("vital:Unavailable");
        } else {
            sensor.setStatus("");
        }

        HasLastKnownLocation location = new HasLastKnownLocation();
        location.setType("geo:Point");
        String[] splitted = currentSensor.getPhysicalLocation().split(";");
        location.setGeoLat(Double.valueOf(splitted[1]));
        location.setGeoLong(Double.valueOf(splitted[0]));

        sensor.setHasLastKnownLocation(location);

        int dirCount = currentSensor.getDirectionCount();
        List<SsnObserf> observedProperties = new ArrayList<>();

        if (dirCount == 1) {
            //speed e color
            SsnObserf speed = new SsnObserf();
            speed.setType("vital:" + this.speedProp);
            speed.setId(this.transfProt + this.symbolicUri + "/sensor/" + id + "/" + this.speedProp);
            SsnObserf color = new SsnObserf();
            color.setType("vital:" + this.colorProp);
            color.setId(this.transfProt + this.symbolicUri + "/sensor/" + id + "/" + this.colorProp);
            observedProperties.add(speed);
            observedProperties.add(color);
        }

        if (dirCount == 2) {
            //speed e color + reverse
            SsnObserf speed = new SsnObserf();
            speed.setType("vital:" + this.speedProp);
            speed.setId(this.transfProt + this.symbolicUri + "/sensor/" + id + "/" + this.speedProp);
            SsnObserf color = new SsnObserf();
            color.setType("vital:" + this.colorProp);
            color.setId(this.transfProt + this.symbolicUri + "/sensor/" + id + "/" +colorProp);
            observedProperties.add(speed);
            observedProperties.add(color);
            SsnObserf revspeed = new SsnObserf();
            revspeed.setType("vital:" + this.reverseSpeedProp);
            revspeed.setId(this.transfProt + this.symbolicUri + "/sensor/" + id + "/" + this.reverseSpeedProp);
            SsnObserf revcolor = new SsnObserf();
            revcolor.setType("vital:" + this.reverseColorProp);
            revcolor.setId(this.transfProt + this.symbolicUri + "/sensor/" + id + "/" + this.reverseColorProp);
            observedProperties.add(revspeed);
            observedProperties.add(revcolor);
        }

        sensor.setSsnObserves(observedProperties);

        return sensor;
    }

    private Service createObservationService() {
        Service observationService = new Service();
        observationService.setContext(contextsUri + "service.jsonld");
        observationService.setId(this.transfProt + this.symbolicUri + "/service/observation");
        observationService.setType("vital:ObservationService");
        List<Operation> operations = new ArrayList<>();
        Operation operation = new Operation();
        operation.setType("vital:GetObservations");
        operation.setHrestHasMethod("hrest:POST");
        operation.setHrestHasAddress(this.transfProt + this.symbolicUri + "/sensor/observation");
        operations.add(operation);
        observationService.setOperations(operations);

        return observationService;
    }

    private Service createConfigurationService() {
        Service configurationService = new Service();
        configurationService.setContext(contextsUri + "service.jsonld");
        configurationService.setId(this.transfProt + this.symbolicUri + "/service/configuration");
        configurationService.setType("vital:ConfigurationService");
        List<Operation> operations = new ArrayList<>();
        Operation operation = new Operation();
        operation.setType("vital:GetConfiguration");
        operation.setHrestHasMethod("hrest:GET");
        operation.setHrestHasAddress(this.transfProt + this.symbolicUri + "/configuration");
        operations.add(operation);
        operation = new Operation();
        operation.setType("vital:SetConfiguration");
        operation.setHrestHasMethod("hrest:POST");
        operation.setHrestHasAddress(this.transfProt + this.symbolicUri + "/configuration");
        operations.add(operation);
        configurationService.setOperations(operations);

        return configurationService;
    }

    private Service createMonitoringService() {
        Service monitoringService = new Service();
        monitoringService.setContext(contextsUri + "service.jsonld");
        monitoringService.setId(this.transfProt + this.symbolicUri + "/service/monitoring");
        monitoringService.setType("vital:MonitoringService");
        List<Operation> operations = new ArrayList<>();
        Operation operation = new Operation();
        operation.setType("vital:GetSystemStatus");
        operation.setHrestHasMethod("hrest:POST");
        operation.setHrestHasAddress(this.transfProt + this.symbolicUri + "/system/status");
        operations.add(operation);
        operation = new Operation();
        operation.setType("vital:GetSensorStatus");
        operation.setHrestHasMethod("hrest:POST");
        operation.setHrestHasAddress(this.transfProt + this.symbolicUri + "/sensor/status");
        operations.add(operation);
        operation = new Operation();
        operation.setType("vital:GetSupportedPerformanceMetrics");
        operation.setHrestHasMethod("hrest:GET");
        operation.setHrestHasAddress(this.transfProt + this.symbolicUri + "/system/performance");
        operations.add(operation);
        operation = new Operation();
        operation.setType("vital:GetPerformanceMetrics");
        operation.setHrestHasMethod("hrest:POST");
        operation.setHrestHasAddress(this.transfProt + this.symbolicUri + "/system/performance");
        operations.add(operation);
        monitoringService.setOperations(operations);

        return monitoringService;
    }

    private Sensor createMonitoringSensor() {
        Sensor sensor = new Sensor();

        String id = "monitoring";

        sensor.setContext(contextsUri + "sensor.jsonld");
        sensor.setName(id);
        sensor.setType("vital:MonitoringSensor");
        sensor.setDescription("HiReply Monitoring Sensor");
        sensor.setId(this.transfProt + this.symbolicUri + "/sensor/" + id);

        sensor.setStatus("vital:Running");

        List<SsnObserf> observedProperties = new ArrayList<>();

        SsnObserf observedProperty = new SsnObserf();
        observedProperty.setType("vital:MemUsed");
        observedProperty.setId(this.transfProt + this.symbolicUri + "/sensor/" + id + "/" + "usedMem");
        observedProperties.add(observedProperty);

        observedProperty = new SsnObserf();
        observedProperty.setType("vital:MemAvailable");
        observedProperty.setId(this.transfProt + this.symbolicUri + "/sensor/" + id + "/" + "availableMem");
        observedProperties.add(observedProperty);

        observedProperty = new SsnObserf();
        observedProperty.setType("vital:DiskAvailable");
        observedProperty.setId(this.transfProt + this.symbolicUri + "/sensor/" + id + "/" + "availableDisk");
        observedProperties.add(observedProperty);

        observedProperty = new SsnObserf();
        observedProperty.setType("vital:SysLoad");
        observedProperty.setId(this.transfProt + this.symbolicUri + "/sensor/" + id + "/" + "sysLoad");
        observedProperties.add(observedProperty);

        observedProperty = new SsnObserf();
        observedProperty.setType("vital:ServedRequest");
        observedProperty.setId(this.transfProt + this.symbolicUri + "/sensor/" +id+ "/" + "servedRequests");
        observedProperties.add(observedProperty);

        observedProperty = new SsnObserf();
        observedProperty.setType("vital:Errors");
        observedProperty.setId(this.transfProt + this.symbolicUri + "/sensor/" + id + "/" + "errors");
        observedProperties.add(observedProperty);

        observedProperty = new SsnObserf();
        observedProperty.setType("vital:SysUpTime");
        observedProperty.setId(this.transfProt + this.symbolicUri + "/sensor/" + id + "/" + "sysUptime");
        observedProperties.add(observedProperty);

        observedProperty = new SsnObserf();
        observedProperty.setType("vital:PendingRequests");
        observedProperty.setId(this.transfProt + this.symbolicUri + "/sensor/" + id + "/" + "pendingRequests");
        observedProperties.add(observedProperty);

        sensor.setSsnObserves(observedProperties);

        return sensor;
    }
    
    private SensorStatus createStatusMeasureFromSensor(ServiceList.TrafficSensor currentSensor, String property) throws Exception {
        SensorStatus m = new SensorStatus();

        Date date = new Date();
        SimpleDateFormat printedDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

        m.setContext(contextsUri + "measurement.jsonld");
        if(property.equals("OperationalState")) {
        	String id = Long.toHexString(date.getTime());
        	m.setId(this.transfProt + this.symbolicUri + "/sensor/monitoring/observation/" + id);
        }
        m.setType("ssn:Observation");

        SsnObservationProperty__ ssnObservationProperty = new SsnObservationProperty__();
        if(property.equals("OperationalState")) {
        	ssnObservationProperty.setType("vital:" + property);
        }

        m.setSsnObservationProperty(ssnObservationProperty);

        SsnObservationResultTime__ ssnObservationResultTime = new SsnObservationResultTime__();
        if(property.equals("OperationalState")) {
        	ssnObservationResultTime.setTimeInXSDDateTime(printedDateFormat.format(date));
        	m.setAdditionalProperty("ssn:featureOfInterest", this.transfProt + this.symbolicUri + "/sensor/" + currentSensor.getID());
        }

        m.setSsnObservationResultTime(ssnObservationResultTime);

        SsnObservationResult__ ssnObservationResult = new SsnObservationResult__();
        ssnObservationResult.setType("ssn:SensorOutput");
        SsnHasValue__ ssnHasValue = new SsnHasValue__();
        ssnHasValue.setType("ssn:ObservationValue");

        if(property.equals("OperationalState")) {
        	int status = currentSensor.getStatus();
            if (status == 1) {
            	SimpleDateFormat timestampDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                Date timestamp = null;
            	String timestampS = currentSensor.getMeasureTime().toString();
                try {
                    timestamp = timestampDateFormat.parse(timestampS);
                } catch (ParseException e) {
                    this.logger.error("HiPPI - createSensorFromTraffic - ERROR PARSING DATE FROM HIREPLY TIMESTAMP");
                    throw new Exception("HiPPI - createSensorFromTraffic - ERROR PARSING DATE FROM HIREPLY TIMESTAMP");
                }
                Date now = new Date();
                if(now.getTime() - timestamp.getTime() > 60 * 1000 * 60) { // If not updated for some time (1 hour)
                	ssnHasValue.setValue("vital:Unavailable");
                }
                else {
                	ssnHasValue.setValue("vital:Running");
                }
            } else if (status == 0) {
            	ssnHasValue.setValue("vital:Unavailable");
            } else {
            	ssnHasValue.setValue("");
            }
        }

        ssnObservationResult.setSsnHasValue(ssnHasValue);
        m.setSsnObservationResult(ssnObservationResult);

        return m;
    }

    private Measure createMeasureFromSensor(ServiceList.TrafficSensor currentSensor, String property) throws Exception {
        Measure m = null;

        SimpleDateFormat timestampDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat printedDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

        Date timestamp = null;
    	String hiReplyTimestamp = this.hiReplySvc.getPropertyAttribute(currentSensor.getID(), property, "Timestamp");
        try {
            timestamp = timestampDateFormat.parse(hiReplyTimestamp);
        } catch (ParseException e) {
            this.logger.error("HiPPI - createMeasureFromSensor - ERROR PARSING DATE FROM HIREPLY TIMESTAMP");
            throw new Exception("HiPPI - createMeasureFromSensor - ERROR PARSING DATE FROM HIREPLY TIMESTAMP");
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(timestamp);
        if(cal.get(Calendar.YEAR) != 1900) {
        	m = new Measure();
	        m.setContext(contextsUri + "measurement.jsonld");
	    	String id = Long.toHexString(timestamp.getTime());
	    	m.setId(this.transfProt + this.symbolicUri + "/sensor/" + currentSensor.getID() + "/observation/" + id);
	        m.setType("ssn:Observation");
	        m.setSsnObservedBy(this.transfProt + this.symbolicUri + "/sensor/" + currentSensor.getID());
	
	        SsnObservationProperty ssnObservationProperty = new SsnObservationProperty();
	        ssnObservationProperty.setType("http://" + this.ontBaseUri + property);
	
	        m.setSsnObservationProperty(ssnObservationProperty);
	
	        SsnObservationResultTime ssnObservationResultTime = new SsnObservationResultTime();
	        ssnObservationResultTime.setTimeInXSDDateTime(printedDateFormat.format(timestamp));
	
	        m.setSsnObservationResultTime(ssnObservationResultTime);
	
	        DulHasLocation dulHasLocation = new DulHasLocation();
	        dulHasLocation.setType("geo:Point");
	        String[] splitted = currentSensor.getPhysicalLocation().split(";");
	        dulHasLocation.setGeoLat(Double.valueOf(splitted[1]));
	        dulHasLocation.setGeoLong(Double.valueOf(splitted[0]));
	        dulHasLocation.setGeoAlt(0.0);
	
	        m.setDulHasLocation(dulHasLocation);
	
	        SsnObservationResult ssnObservationResult = new SsnObservationResult();
	        ssnObservationResult.setType("ssn:SensorOutput");
	        SsnHasValue ssnHasValue = new SsnHasValue();
	        ssnHasValue.setType("ssn:ObservationValue");
	        
	        float speedValue;
	        int colorValue;
	
	        if (currentSensor.getDirectionCount() == 1) {
	            if (property.equals(this.speedProp)) {
	                speedValue = currentSensor.getSpeed();
	                ssnHasValue.setValue((double) speedValue);
	                ssnHasValue.setQudtUnit("qudt:KilometerPerHour");
	            } else if (property.equals(this.colorProp)) {
	                colorValue = currentSensor.getColor();
	                ssnHasValue.setValue((double) colorValue);
	                ssnHasValue.setQudtUnit("qudt:Color");
	            } else {
	                return null;
	            }
	        }
	
	        if (currentSensor.getDirectionCount() == 2) {
	            if (property.equals(this.speedProp)) {
	                speedValue = currentSensor.getSpeed();
	                ssnHasValue.setValue((double) speedValue);
	                ssnHasValue.setQudtUnit("qudt:KilometerPerHour");
	            } else if (property.equals(this.colorProp)) {
	                colorValue = currentSensor.getColor();
	                ssnHasValue.setValue((double) colorValue);
	                ssnHasValue.setQudtUnit("qudt:Color");
	            } else if (property.equals(this.reverseSpeedProp)) {
	                speedValue = currentSensor.getSpeed();
	                ssnHasValue.setValue((double) speedValue);
	                ssnHasValue.setQudtUnit("qudt:KilometerPerHour");
	            } else if (property.equals(this.reverseColorProp)) {
	                colorValue = currentSensor.getColor();
	                ssnHasValue.setValue((double) colorValue);
	                ssnHasValue.setQudtUnit("qudt:Color");
	            } else {
	                return null;
	            }
	        }
	
	        ssnObservationResult.setSsnHasValue(ssnHasValue);
	        m.setSsnObservationResult(ssnObservationResult);
        }

        return m;
    }

    private Measure createMeasureFromHistoryMeasure(HistoryMeasure historyMeasure, ServiceList.TrafficSensor currentSensor, String property) {

        Measure m = null;

        SimpleDateFormat printedDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(historyMeasure.getDate());
        if(cal.get(Calendar.YEAR) != 1900) {
	        m = new Measure();
	        m.setContext(contextsUri + "measurement.jsonld");
	        String id = Long.toHexString(historyMeasure.getDate().getTime());
	        m.setId(this.transfProt + this.symbolicUri + "/sensor/" + currentSensor.getID() + "/observation/" + id);
	        m.setType("ssn:Observation");
	
	        SsnObservationProperty ssnObservationProperty = new SsnObservationProperty();
	        ssnObservationProperty.setType("http://" + this.ontBaseUri + property);
	        m.setSsnObservedBy(this.transfProt + this.symbolicUri + "/sensor/" + currentSensor.getID());
	
	        m.setSsnObservationProperty(ssnObservationProperty);
	
	        SsnObservationResultTime ssnObservationResultTime = new SsnObservationResultTime();
	        ssnObservationResultTime.setTimeInXSDDateTime(printedDateFormat.format(historyMeasure.getDate()));
	
	        m.setSsnObservationResultTime(ssnObservationResultTime);
	
	        DulHasLocation dulHasLocation = new DulHasLocation();
	        dulHasLocation.setType("geo:Point");
	        String[] splitted = currentSensor.getPhysicalLocation().split(";");
	        dulHasLocation.setGeoLat(Double.valueOf(splitted[1]));
	        dulHasLocation.setGeoLong(Double.valueOf(splitted[0]));
	        dulHasLocation.setGeoAlt(0.0);
	
	        m.setDulHasLocation(dulHasLocation);
	
	        SsnObservationResult ssnObservationResult = new SsnObservationResult();
	        ssnObservationResult.setType("ssn:SensorOutput");
	        SsnHasValue ssnHasValue = new SsnHasValue();
	        ssnHasValue.setType("ssn:ObservationValue");
	
	        float speedValue;
	        int colorValue;
	
	        if (currentSensor.getDirectionCount() == 1) {
	            if (property.equals(this.speedProp)) {
	                speedValue = historyMeasure.getValue();
	                ssnHasValue.setValue((double) speedValue);
	                ssnHasValue.setQudtUnit("qudt:KilometerPerHour");
	            } else if (property.equals(this.colorProp)) {
	                colorValue = Math.round(historyMeasure.getValue());
	                ssnHasValue.setValue((double) colorValue);
	                ssnHasValue.setQudtUnit("qudt:Color");
	            } else {
	                return null;
	            }
	        }
	
	        if (currentSensor.getDirectionCount() == 2) {
	            if (property.equals(this.speedProp)) {
	                speedValue = historyMeasure.getValue();
	                ssnHasValue.setValue((double) speedValue);
	                ssnHasValue.setQudtUnit("qudt:KilometerPerHour");
	            } else if (property.equals(this.colorProp)) {
	                colorValue = Math.round(historyMeasure.getValue());
	                ssnHasValue.setValue((double) colorValue);
	                ssnHasValue.setQudtUnit("qudt:Color");
	            } else if (property.equals(this.reverseSpeedProp)) {
	                speedValue = historyMeasure.getValue();
	                ssnHasValue.setValue((double) speedValue);
	                ssnHasValue.setQudtUnit("qudt:KilometerPerHour");
	            } else if (property.equals(this.reverseColorProp)) {
	                colorValue = Math.round(historyMeasure.getValue());
	                ssnHasValue.setValue((double) colorValue);
	                ssnHasValue.setQudtUnit("qudt:Color");
	            } else {
	                return null;
	            }
	        }
	
	        ssnObservationResult.setSsnHasValue(ssnHasValue);
	        m.setSsnObservationResult(ssnObservationResult);
        }

        return m;
    }

    private ServiceList.TrafficSensor retrieveSensor(String id) {

        String filter = hiReplySvc.createFilter("ID", id);
        List<ServiceList.TrafficSensor> trafficSensors = this.hiReplySvc.getSnapshotFiltered(filter).getTrafficSensor();

        ServiceList.TrafficSensor currentSensor;

        try {
            currentSensor = trafficSensors.get(0);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }

        return currentSensor;
    }

    private boolean checkTrafficProperty(ServiceList.TrafficSensor currentSensor, String property) {
        if (currentSensor.getDirectionCount() == 1) {
            if (!property.equals(this.speedProp) && !property.equals(this.colorProp)) {
                return false;
            }
        } else if (currentSensor.getDirectionCount() == 2) {
            if (!property.equals(this.speedProp) && !property.equals(this.colorProp) && !property.equals(this.reverseSpeedProp) && !property.equals(this.reverseColorProp)) {
                return false;
            }
        }
        return true;
    }
}

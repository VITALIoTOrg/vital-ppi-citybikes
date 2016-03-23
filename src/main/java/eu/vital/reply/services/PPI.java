package eu.vital.reply.services;

import eu.vital.reply.clients.IoTSystemClient;
import eu.vital.reply.jsonpojos.EmptyRequest;
import eu.vital.reply.jsonpojos.IoTSystem;
import eu.vital.reply.jsonpojos.Metric;
import eu.vital.reply.jsonpojos.MetricRequest;
import eu.vital.reply.jsonpojos.Network;
import eu.vital.reply.jsonpojos.PerformanceMetric;
import eu.vital.reply.jsonpojos.PerformanceMetricsMetadata;
import eu.vital.reply.jsonpojos.SsnHasValue_;
import eu.vital.reply.jsonpojos.SsnObservationProperty_;
import eu.vital.reply.jsonpojos.SsnObservationResultTime_;
import eu.vital.reply.jsonpojos.SsnObservationResult_;
import eu.vital.reply.jsonpojos.Station;
import eu.vital.reply.utils.ConfigReader;
import eu.vital.reply.utils.JsonUtils;
import eu.vital.reply.utils.StatCounter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * PPI Class that provides all the REST API that a system attached to VITAL will expose
 */

@Path("")
public class PPI {

    private Logger logger;
    private IoTSystemClient client;

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
    
    // VITAL ontology extended prefix
    private static final String ontologyPrefix = "http://vital-iot.eu/ontology/ns/";

    // IoT system data
    private static final String apiBasePath = "http://api.citybik.es/v2/networks";
    private static final String networkId = "to-bike";

    // To be able to return system metadata if CityBikes is temporarily unavailable
    private static HashMap<String, Network> networkCache;
    
    private static Date startupTime = new Date();

    @Context
    private UriInfo uriInfo;

    public PPI() {
        ConfigReader configReader = ConfigReader.getInstance();

        client = new IoTSystemClient();
        logger = LogManager.getLogger(PPI.class);

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

        if (startupTime == null) {
        	startupTime = new Date();
        }
    }

    @Path("/metadata")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMetadata(String bodyRequest, @Context UriInfo uri) {
        int i;
        Network network;
        IoTSystem iotSystem;
        List<String> services;
        List<String> sensors;

        try {
            JsonUtils.deserializeJson(bodyRequest, EmptyRequest.class);
        } catch (IOException e) {
            this.logger.error("[/metadata] Error parsing request");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

		network = client.getNetwork(apiBasePath, networkId).getNetwork();

		if (network == null) {
			// Try and get metadata from cache
			network = networkCache.get(networkId);
			if (network == null) {
				return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
			} else {
				iotSystem = new IoTSystem();
				// Found in cache, but unavailable
				iotSystem.setStatus("vital:Unavailable");
			}
		} else {
			// Well up and running
			networkCache.put(networkId, network);
			iotSystem = new IoTSystem();
			iotSystem.setStatus("vital:Running");
		}

        services = new ArrayList<String>();
        sensors = new ArrayList<String>();

        iotSystem.setContext("http://vital-iot.eu/contexts/system.jsonld");
        iotSystem.setId(uri.getBaseUri().toString());
        iotSystem.setType("vital:VitalSystem");
        iotSystem.setName(network.getName());
        iotSystem.setDescription("CityBikes " + network.getName() + " network operated by " + network.getCompany());
        iotSystem.setOperator(network.getCompany());
        iotSystem.setServiceArea("http://dbpedia.org/page/" + network.getLocation().getCity());

        List<Station> stations = network.getStations();
        for (i = 0; i < stations.size(); i++) {
            sensors.add(uri.getBaseUri() + "/sensor/" + stations.get(i).getId());
        }
        sensors.add(uri.getBaseUri() + "/sensor/monitoring");
        iotSystem.setSensors(sensors);

        services.add(uri.getBaseUri() + "/service/monitoring");
        services.add(uri.getBaseUri() + "/service/observation");
        iotSystem.setServices(services);

        try {
			return Response.status(Response.Status.OK)
				.entity(JsonUtils.serializeJson(iotSystem))
				.build();
		} catch (IOException e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
    }

    @Path("/system/performance")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSupportedPerformanceMetrics(@Context UriInfo uri) {
    	PerformanceMetricsMetadata performanceMetricsMetadata;
        List<Metric> list;
        Metric metric;

        performanceMetricsMetadata = new PerformanceMetricsMetadata();
        list = new ArrayList<Metric>();

        metric = new Metric();
        metric.setType(ontologyPrefix + "UsedMem");
        metric.setId(uri.getBaseUri() + "/sensor/monitoring/usedMem");
        list.add(metric);

        metric = new Metric();
        metric.setType(ontologyPrefix + "AvailableMem");
        metric.setId(uri.getBaseUri() + "/sensor/monitoring/availableMem");
        list.add(metric);

        metric = new Metric();
        metric.setType(ontologyPrefix + "AvailableDisk");
        metric.setId(uri.getBaseUri() + "/sensor/monitoring/availableDisk");
        list.add(metric);

        metric = new Metric();
        metric.setType(ontologyPrefix + "SysLoad");
        metric.setId(uri.getBaseUri() + "/sensor/monitoring/sysLoad");
        list.add(metric);

        metric = new Metric();
        metric.setType(ontologyPrefix + "ServedRequests");
        metric.setId(uri.getBaseUri() + "/sensor/monitoring/servedRequests");
        list.add(metric);

        metric = new Metric();
        metric.setType(ontologyPrefix + "Errors");
        metric.setId(uri.getBaseUri() + "/sensor/monitoring/errors");
        list.add(metric);

        metric = new Metric();
        metric.setType(ontologyPrefix + "SysUptime");
        metric.setId(uri.getBaseUri() + "/sensor/monitoring/sysUptime");
        list.add(metric);

        metric = new Metric();
        metric.setType(ontologyPrefix + "PendingRequests");
        metric.setId(uri.getBaseUri() + "/sensor/monitoring/pendingRequests");
        list.add(metric);

        performanceMetricsMetadata.setMetrics(list);

        try {
			return Response.status(Response.Status.OK)
				.entity(JsonUtils.serializeJson(performanceMetricsMetadata))
				.build();
		} catch (IOException e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
    }

    @Path("/system/performance")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPerformanceMetrics(String bodyRequest, @Context UriInfo uri) {
        List<String> requestedMetrics;
        List<PerformanceMetric> metrics;
        PerformanceMetric metric;
        Date date;
        Runtime runtime;
        SimpleDateFormat printedDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        String type, unit, value;

        try {
        	requestedMetrics = ((MetricRequest) JsonUtils.deserializeJson(bodyRequest, MetricRequest.class)).getMetric();
        } catch (IOException e) {
        	this.logger.error("[/system/performance] Error parsing request");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        date = new Date();
        runtime = Runtime.getRuntime();
        metrics = new ArrayList<PerformanceMetric>();
        for (String m : requestedMetrics) {
            if (m.contains("UsedMem".toLowerCase())) {
            	type = "vital:UsedMem";
            	unit = "qudt:Byte";
            	value = Long.toString(runtime.totalMemory());
            } else if (m.contains("AvailableMem".toLowerCase())) {
            	type = "vital:AvailableMem";
            	unit = "qudt:Byte";
            	value = Long.toString(runtime.freeMemory());
            } else if (m.contains("AvailableDisk".toLowerCase())) {
            	type = "vital:AvailableDisk";
            	unit = "qudt:Byte";
            	File file;
            	value = Long.toString(new File("/").getFreeSpace());
            } else if (m.contains("SysLoad".toLowerCase())) {
            	type = "vital:SysLoad";
            	unit = "qudt:Percentage";
            	try {
					value = Double.toString(getProcessCpuLoad());
				} catch (Exception e) {
					e.printStackTrace();
					return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
				}
            } else if (m.contains("ServedRequests".toLowerCase())) {
            	type = "vital:ServedRequests";
            	unit = "qudt:Number";
            	value = Integer.toString(StatCounter.getRequestNumber().get());
            } else if (m.contains("Errors".toLowerCase())) {
            	type = "vital:Errors";
            	unit = "qudt:Number";
            	value = Integer.toString(StatCounter.getErrorNumber().get());
            } else if (m.contains("SysUptime".toLowerCase())) {
            	type = "vital:SysUptime";
            	unit = "qudt:MilliSecond";
            	value = Long.toString(date.getTime() - startupTime.getTime());
            } else if (m.contains("PendingRequests".toLowerCase())) {
            	type = "vital:PendingRequests";
            	unit = "qudt:Number";
            	value = Integer.toString(StatCounter.getPendingRequest() - 1);
            } else {
            	this.logger.error("[/system/performance] Bad metric " + m);
                return Response.status(Response.Status.BAD_REQUEST).build();
            }

            metric = new PerformanceMetric();
            metric.setContext("http://vital-iot.eu/contexts/measurement.jsonld");
            metric.setId(uri.getBaseUri() + "/sensor/monitoring/observation/" + Long.toHexString(date.getTime()));
            metric.setType("ssn:Observation");

            SsnObservationProperty_ ssnObservationProperty_ = new SsnObservationProperty_();
            ssnObservationProperty_.setType(type);
            metric.setSsnObservationProperty(ssnObservationProperty_);

            SsnObservationResultTime_ ssnObservationResultTime_ = new SsnObservationResultTime_();
            ssnObservationResultTime_.setTimeInXSDDateTime(printedDateFormat.format(date));
            metric.setSsnObservationResultTime(ssnObservationResultTime_);

            SsnObservationResult_ ssnObservationResult_ = new SsnObservationResult_();
            ssnObservationResult_.setType("ssn:SensorOutput");
            SsnHasValue_ ssnHasValue_ = new SsnHasValue_();
            ssnHasValue_.setType("ssn:ObservationValue");
            ssnHasValue_.setValue(value);
            ssnHasValue_.setQudtUnit(unit);
            ssnObservationResult_.setSsnHasValue(ssnHasValue_);
            metric.setSsnObservationResult(ssnObservationResult_);

            metric.setSsnFeatureOfInterest(uri.getBaseUri().toString());
            metrics.add(metric);
        }

        try {
			return Response.status(Response.Status.OK)
				.entity(JsonUtils.serializeJson(metrics))
				.build();
		} catch (IOException e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
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

        ServiceList system = client.getSnapshot();
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
        		system = client.getSnapshot();
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
                    		system = client.getSnapshot();
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
                	/*if(system == null) {
                		system = client.getSnapshot();
                	}
                	List<ServiceList.TrafficSensor> trafficSensors = system.getTrafficSensor();
                    for(j = 0; j < trafficSensors.size(); j++) {
                    	ServiceList.TrafficSensor ts = trafficSensors.get(j);
                    	if(ts.getID().equals(currentId)) {
	                        tmpSensor = this.createSensorFromTraffic(trafficSensors.get(j));
	                        if(!sensors.contains(tmpSensor)) {
	                            sensors.add(tmpSensor);
	                        }
                    	}
                    }*/
                	// The above would mean one call for multiple sensors, but would degrade performance in cases with
                	// a few sensors only and would move some computation load on the PPI server which is not powerful
                    String filter = client.createFilter("ID", currentId);

                    ServiceList.TrafficSensor currentTrafficSensor;

                    try {
                        currentTrafficSensor = this.client.getSnapshotFiltered(filter).getTrafficSensor().get(0);
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
        		system = client.getSnapshot();
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
                    		system = client.getSnapshot();
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
                    String filter = client.createFilter("ID", currentId);

                    ServiceList.TrafficSensor currentTrafficSensor;

                    try {
                        currentTrafficSensor = this.client.getSnapshotFiltered(filter).getTrafficSensor().get(0);
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
        ObservationRequest observationRequest;
        ArrayList<Measure> measures = new ArrayList<>();
        ArrayList<PerformanceMetric> metrics = new ArrayList<>();
        String id;
        boolean missing;
        String errmsg = "";

        String out;

        try {
            observationRequest = (ObservationRequest) JsonUtils.deserializeJson(bodyRequest, ObservationRequest.class);
            missing = false;
            if (observationRequest.getSensor().isEmpty()) {
            	missing = true;
            	errmsg = errmsg + "sensor";
            }
            if (observationRequest.getProperty() == null) {
            	missing = true;
            	errmsg = errmsg + " and property";
            }
            if(missing)
            	throw new IOException("field(s) " + errmsg + " is/are required!");
        } catch (IOException e) {
            this.logger.error("GET OBSERVATION - IOException parsing the json request");
            return "{\n" +
                    "\"error\": \"Malformed request body: " + e.getMessage() + "\"\n" +
                    "}";
        }

        List<String>ids = observationRequest.getSensor();
        String property = observationRequest.getProperty().replaceAll("http://" + this.ontBaseUri, "");
        for (String sid : ids) {
            id = sid.replaceAll(this.transfProt + this.symbolicUri + "/sensor/", "");

            if (id.contains("monitoring")) {
                // Monitoring sensor
                PerformanceMetric metric;
                if (property.contains("UsedMem")) {
                    metric = this.getMemoryUsed();
                } else if (property.contains("AvailableMem")) {
                    metric = this.getMemoryAvailable();
                } else if (property.contains("AvailableDisk")) {
                    metric = this.getDiskAvailable();
                } else if (property.contains("SysLoad")) {
                    metric = this.getCpuUsage();
                } else if (property.contains("ServedRequests")) {
                    metric = this.getServedRequest();
                } else if (property.contains("Errors")) {
                    metric = this.getErrors();
                } else if (property.contains("SysUptime")) {
                    metric = this.getUpTime();
                } else if (property.contains("PendingRequests")) {
                    metric = this.getPendingRequest();
                } else {
                    return "{\n" +
                            "\"error\": \"Performance " + property + " not present.\"\n" +
                            "}";
                }

                if (metric != null) {
                    metric.setSsnObservedBy(sid);
                    SsnObservationProperty_ ob = new SsnObservationProperty_();
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
            	ServiceList.TrafficSensor currentSensor = this.retrieveSensor(id);

                if(currentSensor == null) {
                    return "{\n" +
                            "\"error\": \"ID " + id + " not present.\"\n" +
                            "}";
                }

                if(!this.checkTrafficProperty(currentSensor, property)) {
                    return "{\n" +
                            "\"error\": \"Property " + property + " not present for " + id + " sensor.\"\n" +
                            "}";
                }
                
                Measure tmpMes;

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

                    List<HistoryMeasure> historyMeasures = this.getHistoryMeasures(client.getPropertyHistoricalValues(id, property, fromDateHiReply, toDateHiReply));

                    for (HistoryMeasure hm : historyMeasures) {
                    	tmpMes = this.createMeasureFromHistoryMeasure(hm, currentSensor, property);
                    	if (tmpMes != null) {
                    		measures.add(tmpMes);
                    	}
                    }

                } else if (observationRequest.getFrom() != null && observationRequest.getTo() == null) {
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

                    List<HistoryMeasure> historyMeasures = this.getHistoryMeasures(client.getPropertyHistoricalValues(id, property, fromDateHiReply, toDateHiReply));

                    for (HistoryMeasure hm : historyMeasures) {
                    	tmpMes = this.createMeasureFromHistoryMeasure(hm, currentSensor, property);
                    	if (tmpMes != null) {
                    		measures.add(tmpMes);
                    	}
                    }

                } else if (observationRequest.getFrom() == null && observationRequest.getTo() == null) {
                    // get last value only
                	tmpMes = this.createMeasureFromSensor(currentSensor, property);
                	if (tmpMes != null) {
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
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa");

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
        String hiReplyTimestamp = currentSensor.getMeasureTime().toString();
        try {
            timestamp = timestampDateFormat.parse(hiReplyTimestamp);
        } catch (ParseException e) {
            this.logger.error("HiPPI - createMeasureFromSensor - ERROR PARSING DATE FROM HIREPLY TIMESTAMP");
            throw new Exception("HiPPI - createMeasureFromSensor - ERROR PARSING DATE FROM HIREPLY TIMESTAMP");
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(timestamp);
        if((cal.get(Calendar.YEAR) != 1900) && (currentSensor.getStatus() != 0)) {
        	m = new Measure();
	        m.setContext(contextsUri + "measurement.jsonld");
	    	String id = Long.toHexString(timestamp.getTime());
	    	m.setId(this.transfProt + this.symbolicUri + "/sensor/" + currentSensor.getID() + "/observation/" + id);
	        m.setType("ssn:Observation");
	        m.setSsnObservedBy(this.transfProt + this.symbolicUri + "/sensor/" + currentSensor.getID());
	
	        SsnObservationProperty ssnObservationProperty = new SsnObservationProperty();
	        ssnObservationProperty.setType("vital:" + property);
	
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
        if ((cal.get(Calendar.YEAR) != 1900) && (currentSensor.getStatus() != 0)) {
	        m = new Measure();
	        m.setContext(contextsUri + "measurement.jsonld");
	        String id = Long.toHexString(historyMeasure.getDate().getTime());
	        m.setId(this.transfProt + this.symbolicUri + "/sensor/" + currentSensor.getID() + "/observation/" + id);
	        m.setType("ssn:Observation");
	
	        SsnObservationProperty ssnObservationProperty = new SsnObservationProperty();
	        ssnObservationProperty.setType("vital:" + property);
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
	
	        if (currentSensor.getDirectionCount() == 1) {
	            if (property.equals(this.speedProp)) {
	                ssnHasValue.setValue((double) historyMeasure.getValue());
	                ssnHasValue.setQudtUnit("qudt:KilometerPerHour");
	            } else if (property.equals(this.colorProp)) {
	                ssnHasValue.setValue((double) Math.round(historyMeasure.getValue()));
	                ssnHasValue.setQudtUnit("qudt:Color");
	            } else {
	                return null;
	            }
	        }
	
	        if (currentSensor.getDirectionCount() == 2) {
	            if (property.equals(this.speedProp) || property.equals(this.reverseSpeedProp)) {
	                ssnHasValue.setValue((double) historyMeasure.getValue());
	                ssnHasValue.setQudtUnit("qudt:KilometerPerHour");
	            } else if (property.equals(this.colorProp) || property.equals(this.reverseColorProp)) {
	                ssnHasValue.setValue((double) Math.round(historyMeasure.getValue()));
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
    
    private static double getProcessCpuLoad() throws Exception {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ObjectName name = ObjectName.getInstance("java.lang:type=OperatingSystem");
        AttributeList list = mbs.getAttributes(name, new String[] { "ProcessCpuLoad" });

        if (list.isEmpty())
        	return Double.NaN;

        Attribute att = (Attribute) list.get(0);
        Double value = (Double) att.getValue();

        if (value == -1.0)
        	return Double.NaN;

        return ((int) (value * 1000) / 10.0);
    }
}

package eu.vital.reply.services;

import eu.vital.reply.clients.IoTSystemClient;
import eu.vital.reply.jsonpojos.EmptyRequest;
import eu.vital.reply.jsonpojos.HasLastKnownLocation;
import eu.vital.reply.jsonpojos.IdTypeRequest;
import eu.vital.reply.jsonpojos.IoTSystem;
import eu.vital.reply.jsonpojos.Metric;
import eu.vital.reply.jsonpojos.MetricRequest;
import eu.vital.reply.jsonpojos.Network;
import eu.vital.reply.jsonpojos.Operation;
import eu.vital.reply.jsonpojos.PerformanceMetric;
import eu.vital.reply.jsonpojos.PerformanceMetricsMetadata;
import eu.vital.reply.jsonpojos.Sensor;
import eu.vital.reply.jsonpojos.SensorStatus;
import eu.vital.reply.jsonpojos.Service;
import eu.vital.reply.jsonpojos.SsnHasValue_;
import eu.vital.reply.jsonpojos.SsnHasValue__;
import eu.vital.reply.jsonpojos.SsnObserf;
import eu.vital.reply.jsonpojos.SsnObservationProperty_;
import eu.vital.reply.jsonpojos.SsnObservationProperty__;
import eu.vital.reply.jsonpojos.SsnObservationResultTime_;
import eu.vital.reply.jsonpojos.SsnObservationResultTime__;
import eu.vital.reply.jsonpojos.SsnObservationResult_;
import eu.vital.reply.jsonpojos.SsnObservationResult__;
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

    // VITAL ontology extended prefix
    private static final String ontologyPrefix = "http://vital-iot.eu/ontology/ns/";

    // IoT system data
    private static final String apiBasePath = "http://api.citybik.es/v2/networks";
    private static final String networkId = "to-bike";

    // To be able to return network data if CityBikes is temporarily unavailable
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
			// Try and get network from cache
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
            if (m.contains("UsedMem")) {
            	type = "vital:UsedMem";
            	unit = "qudt:Byte";
            	value = Long.toString(runtime.totalMemory());
            } else if (m.contains("AvailableMem")) {
            	type = "vital:AvailableMem";
            	unit = "qudt:Byte";
            	value = Long.toString(runtime.freeMemory());
            } else if (m.contains("AvailableDisk")) {
            	type = "vital:AvailableDisk";
            	unit = "qudt:Byte";
            	value = Long.toString(new File("/").getFreeSpace());
            } else if (m.contains("SysLoad")) {
            	type = "vital:SysLoad";
            	unit = "qudt:Percentage";
            	try {
					value = Double.toString(getProcessCpuLoad());
				} catch (Exception e) {
					e.printStackTrace();
					return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
				}
            } else if (m.contains("ServedRequests")) {
            	type = "vital:ServedRequests";
            	unit = "qudt:Number";
            	value = Integer.toString(StatCounter.getRequestNumber().get());
            } else if (m.contains("Errors")) {
            	type = "vital:Errors";
            	unit = "qudt:Number";
            	value = Integer.toString(StatCounter.getErrorNumber().get());
            } else if (m.contains("SysUptime")) {
            	type = "vital:SysUptime";
            	unit = "qudt:MilliSecond";
            	value = Long.toString(date.getTime() - startupTime.getTime());
            } else if (m.contains("PendingRequests")) {
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

    @Path("/system/status")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSystemStatus(@Context UriInfo uri) {
    	Date now;
        Network network;
        SsnHasValue_ ssnHasValue_;
        PerformanceMetric lifecycleInformation;

		network = client.getNetwork(apiBasePath, networkId).getNetwork();

		if (network == null) {
			// Try and get network from cache
			network = networkCache.get(networkId);
			if (network == null) {
				return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
			} else {
				// Found in cache, but unavailable
				lifecycleInformation = new PerformanceMetric();
				ssnHasValue_ = new SsnHasValue_();
				ssnHasValue_.setValue("vital:Running");
				ssnHasValue_.setValue("vital:Unavailable");
			}
		} else {
			// Well up and running
			networkCache.put(networkId, network);
			lifecycleInformation = new PerformanceMetric();
			ssnHasValue_ = new SsnHasValue_();
			ssnHasValue_.setValue("vital:Running");
		}

        now = new Date();
        SimpleDateFormat printedDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

        lifecycleInformation.setContext("http://vital-iot.eu/contexts/measurement.jsonld");
        lifecycleInformation.setId(uri.getBaseUri() + "/sensor/monitoring/observation/" + Long.toHexString(now.getTime()));
        lifecycleInformation.setType("ssn:Observation");

        SsnObservationProperty_ ssnObservationProperty_ = new SsnObservationProperty_();
        ssnObservationProperty_.setType("vital:OperationalState");
        lifecycleInformation.setSsnObservationProperty(ssnObservationProperty_);

        SsnObservationResultTime_ ssnObservationResultTime_ = new SsnObservationResultTime_();

        ssnObservationResultTime_.setTimeInXSDDateTime(printedDateFormat.format(now));
        lifecycleInformation.setSsnObservationResultTime(ssnObservationResultTime_);

        SsnObservationResult_ ssnObservationResult_ = new SsnObservationResult_();
        ssnObservationResult_.setType("ssn:SensorOutput");
        
        ssnHasValue_.setType("ssn:ObservationValue");
        ssnObservationResult_.setSsnHasValue(ssnHasValue_);
        lifecycleInformation.setSsnObservationResult(ssnObservationResult_);

        lifecycleInformation.setSsnFeatureOfInterest(uri.getBaseUri().toString());

        try {
			return Response.status(Response.Status.OK)
				.entity(JsonUtils.serializeJson(lifecycleInformation))
				.build();
		} catch (IOException e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
    }

    @Path("/service/metadata")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getServiceMetadata(String bodyRequest, @Context UriInfo uri) {
        IdTypeRequest serviceRequest;
        Service tmpService;
        List<Service> services;

        try {
        	serviceRequest = (IdTypeRequest) JsonUtils.deserializeJson(bodyRequest, IdTypeRequest.class);
        } catch (IOException e) {
        	this.logger.error("[/service/metadata] Error parsing request");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        
        services = new ArrayList<Service>();

        if ((serviceRequest.getId().size() == 0) && (serviceRequest.getType().size() == 0)) {
            services.add(createObservationService(uri));
            services.add(createMonitoringService(uri));
        } else {
            for (String type : serviceRequest.getType()) {
                if (type.contains("ObservationService")) {
                	services.add(createObservationService(uri));
                }
                else if (type.contains("MonitoringService")) {
                    services.add(createMonitoringService(uri));
                }
            }
            for (String id : serviceRequest.getId()) {
                if (id.contains("observation")) {
                    tmpService = createObservationService(uri);
                    if (!services.contains(tmpService)) {
                        services.add(tmpService);
                    }
                }
                else if (id.contains("monitoring")) {
                    tmpService = createMonitoringService(uri);
                    if (!services.contains(tmpService)) {
                        services.add(tmpService);
                    }
                }
            }
        }

        try {
			return Response.status(Response.Status.OK)
				.entity(JsonUtils.serializeJson(services))
				.build();
		} catch (IOException e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
    }

    @Path("/sensor/metadata")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSensorMetadata(String bodyRequest, @Context UriInfo uri) {
        IdTypeRequest sensorRequest;
        Network network;
        Sensor tmpSensor;
        List<Sensor> sensors;

        try {
        	sensorRequest = (IdTypeRequest) JsonUtils.deserializeJson(bodyRequest, IdTypeRequest.class);
        } catch (IOException e) {
        	this.logger.error("[/sensor/metadata] Error parsing request");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

		network = client.getNetwork(apiBasePath, networkId).getNetwork();

		if (network == null) {
			// Try and get network from cache
			network = networkCache.get(networkId);
			if (network == null) {
				return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
			}
		} else {
			// Well up and running
			networkCache.put(networkId, network);
		}

        sensors = new ArrayList<Sensor>();

        if ((sensorRequest.getId().size() == 0) && (sensorRequest.getType().size() == 0)) {
            for (Station station : network.getStations()) {
                try {
					sensors.add(createSensorFromStation(station, uri));
				} catch (ParseException e) {
					e.printStackTrace();
					return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
				}
            }
            sensors.add(createMonitoringSensor(uri));
        } else {
            for (String type : sensorRequest.getType()) {
                if (type.contains("VitalSensor")) {
                	for (Station station : network.getStations()) {
                        try {
							sensors.add(createSensorFromStation(station, uri));
						} catch (ParseException e) {
							e.printStackTrace();
							return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
						}
                    }
                }
                else if (type.contains("MonitoringSensor")) {
                	sensors.add(createMonitoringSensor(uri));
                }
            }
            for (String id : sensorRequest.getId()) {
            	if (id.contains("monitoring")) {
                    tmpSensor = createMonitoringSensor(uri);
                    if (!sensors.contains(tmpSensor)) {
                    	sensors.add(tmpSensor);
                    }
                } else {
                	for (Station station : network.getStations()) {
                		if (id.contains(station.getId())) {
	                		try {
								tmpSensor = createSensorFromStation(station, uri);
							} catch (ParseException e) {
								e.printStackTrace();
								return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
							}
	                        if (!sensors.contains(tmpSensor)) {
	                        	sensors.add(tmpSensor);
	                        	break;
	                        }
                		}
                    }
                }
            }
        }

        try {
			return Response.status(Response.Status.OK)
				.entity(JsonUtils.serializeJson(sensors))
				.build();
		} catch (IOException e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
    }

    @Path("/sensor/status")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSensorStatus(String bodyRequest, @Context UriInfo uri) {
    	IdTypeRequest sensorRequest;
        Network network;
        SensorStatus tmpSensor;
        List<SensorStatus> sensorsStatus;

        try {
        	sensorRequest = (IdTypeRequest) JsonUtils.deserializeJson(bodyRequest, IdTypeRequest.class);
        } catch (IOException e) {
        	this.logger.error("[/sensor/status] Error parsing request");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

		network = client.getNetwork(apiBasePath, networkId).getNetwork();

		if (network == null) {
			// Try and get network from cache
			network = networkCache.get(networkId);
			if (network == null) {
				return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
			}
		} else {
			// Well up and running
			networkCache.put(networkId, network);
		}

        sensorsStatus = new ArrayList<SensorStatus>();

        if ((sensorRequest.getId().size() == 0) && (sensorRequest.getType().size() == 0)) {
            for (Station station : network.getStations()) {
                try {
					sensorsStatus.add(createStatusMeasureFromStation(station, uri));
				} catch (ParseException e) {
					e.printStackTrace();
					return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
				}
            }
            sensorsStatus.add(createMonitoringStatusMeasure(uri));
        } else {
            for (String type : sensorRequest.getType()) {
                if (type.contains("VitalSensor")) {
                	for (Station station : network.getStations()) {
                        try {
							sensorsStatus.add(createStatusMeasureFromStation(station, uri));
						} catch (ParseException e) {
							e.printStackTrace();
							return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
						}
                    }
                }
                else if (type.contains("MonitoringSensor")) {
                	sensorsStatus.add(createMonitoringStatusMeasure(uri));
                }
            }
            for (String id : sensorRequest.getId()) {
            	if (id.contains("monitoring")) {
                    tmpSensor = createMonitoringStatusMeasure(uri);
                    if (!sensorsStatus.contains(tmpSensor)) {
                    	sensorsStatus.add(tmpSensor);
                    }
                } else {
                	for (Station station : network.getStations()) {
                		if (id.contains(station.getId())) {
	                		try {
								tmpSensor = createStatusMeasureFromStation(station, uri);
							} catch (ParseException e) {
								e.printStackTrace();
								return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
							}
	                        if (!sensorsStatus.contains(tmpSensor)) {
	                        	sensorsStatus.add(tmpSensor);
	                        	break;
	                        }
                		}
                    }
                }
            }
        }

        try {
			return Response.status(Response.Status.OK)
				.entity(JsonUtils.serializeJson(sensorsStatus))
				.build();
		} catch (IOException e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
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

    private Sensor createMonitoringSensor(UriInfo uri) {
    	String id = "monitoring";
    	List<SsnObserf> observedProperties;
        Sensor sensor = new Sensor();

        sensor.setContext("http://vital-iot.eu/contexts/sensor.jsonld");
        sensor.setName(id);
        sensor.setType("vital:MonitoringSensor");
        sensor.setDescription("CityBikes monitoring sensor");
        sensor.setId(uri.getBaseUri() + "/sensor/" + id);

        sensor.setStatus("vital:Running");

        observedProperties = new ArrayList<SsnObserf>();

        SsnObserf observedProperty = new SsnObserf();
        observedProperty.setType("vital:MemUsed");
        observedProperty.setId(uri.getBaseUri() + "/sensor/" + id + "/" + "usedMem");
        observedProperties.add(observedProperty);

        observedProperty = new SsnObserf();
        observedProperty.setType("vital:MemAvailable");
        observedProperty.setId(uri.getBaseUri() + "/sensor/" + id + "/" + "availableMem");
        observedProperties.add(observedProperty);

        observedProperty = new SsnObserf();
        observedProperty.setType("vital:DiskAvailable");
        observedProperty.setId(uri.getBaseUri() + "/sensor/" + id + "/" + "availableDisk");
        observedProperties.add(observedProperty);

        observedProperty = new SsnObserf();
        observedProperty.setType("vital:SysLoad");
        observedProperty.setId(uri.getBaseUri() + "/sensor/" + id + "/" + "sysLoad");
        observedProperties.add(observedProperty);

        observedProperty = new SsnObserf();
        observedProperty.setType("vital:ServedRequest");
        observedProperty.setId(uri.getBaseUri() + "/sensor/" +id+ "/" + "servedRequests");
        observedProperties.add(observedProperty);

        observedProperty = new SsnObserf();
        observedProperty.setType("vital:Errors");
        observedProperty.setId(uri.getBaseUri() + "/sensor/" + id + "/" + "errors");
        observedProperties.add(observedProperty);

        observedProperty = new SsnObserf();
        observedProperty.setType("vital:SysUpTime");
        observedProperty.setId(uri.getBaseUri() + "/sensor/" + id + "/" + "sysUptime");
        observedProperties.add(observedProperty);

        observedProperty = new SsnObserf();
        observedProperty.setType("vital:PendingRequests");
        observedProperty.setId(uri.getBaseUri() + "/sensor/" + id + "/" + "pendingRequests");
        observedProperties.add(observedProperty);

        sensor.setSsnObserves(observedProperties);
        
        sensor.setStatus("vital:Running");

        return sensor;
    }

    private Sensor createSensorFromStation(Station station, UriInfo uri) throws ParseException {
    	SimpleDateFormat timestampDateFormat;
    	Date now, timestamp = null;
        String id = station.getId();
        Sensor sensor = new Sensor();

        sensor.setContext("http://vital-iot.eu/contexts/sensor.jsonld");
        sensor.setName(station.getName());
        sensor.setType("vital:VitalSensor");
        sensor.setDescription(station.getExtra().getDescription());
        sensor.setId(uri.getBaseUri() + "/sensor/" + id);

        timestampDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        timestamp = timestampDateFormat.parse(station.getTimestamp());
        now = new Date();
        if (now.getTime() - timestamp.getTime() > 60 * 1000 * 60) {
        	sensor.setStatus("vital:Unavailable");
        } else {
        	sensor.setStatus("vital:Running");
        }

        HasLastKnownLocation location = new HasLastKnownLocation();
        location.setType("geo:Point");
        location.setGeoLat(station.getLatitude());
        location.setGeoLong(station.getLongitude());
        sensor.setHasLastKnownLocation(location);

        List<SsnObserf> observedProperties = new ArrayList<SsnObserf>();

        SsnObserf freeBikes = new SsnObserf();
        freeBikes.setType("vital:AvailableBikes");
        freeBikes.setId(uri.getBaseUri() + "/sensor/" + id + "/" + "AvailableBikes".toLowerCase());
        SsnObserf emptySlots = new SsnObserf();
        emptySlots.setType("vital:EmptyDocks");
        emptySlots.setId(uri.getBaseUri() + "/sensor/" + id + "/" + "EmptyDocks".toLowerCase());
        observedProperties.add(freeBikes);
        observedProperties.add(emptySlots);

        sensor.setSsnObserves(observedProperties);

        return sensor;
    }

    private Service createObservationService(UriInfo uri) {
    	Operation operation;
    	List<Operation> operations;
        Service observationService = new Service();
        
        observationService.setContext("http://vital-iot.eu/contexts/service.jsonld");
        observationService.setId(uri.getBaseUri() + "/service/observation");
        observationService.setType("vital:ObservationService");
        operations = new ArrayList<Operation>();
        operation = new Operation();
        operation.setType("vital:GetObservations");
        operation.setHrestHasMethod("hrest:POST");
        operation.setHrestHasAddress(uri.getBaseUri() + "/sensor/observation");
        operations.add(operation);
        observationService.setOperations(operations);

        return observationService;
    }

    private Service createMonitoringService(UriInfo uri) {
    	Operation operation;
    	List<Operation> operations;
        Service monitoringService = new Service();
        
        monitoringService.setContext("http://vital-iot.eu/contexts/service.jsonld");
        monitoringService.setId(uri.getBaseUri() + "/service/monitoring");
        monitoringService.setType("vital:MonitoringService");
        operations = new ArrayList<Operation>();
        operation = new Operation();
        operation.setType("vital:GetSystemStatus");
        operation.setHrestHasMethod("hrest:POST");
        operation.setHrestHasAddress(uri.getBaseUri() + "/system/status");
        operations.add(operation);
        operation = new Operation();
        operation.setType("vital:GetSensorStatus");
        operation.setHrestHasMethod("hrest:POST");
        operation.setHrestHasAddress(uri.getBaseUri() + "/sensor/status");
        operations.add(operation);
        operation = new Operation();
        operation.setType("vital:GetSupportedPerformanceMetrics");
        operation.setHrestHasMethod("hrest:GET");
        operation.setHrestHasAddress(uri.getBaseUri() + "/system/performance");
        operations.add(operation);
        operation = new Operation();
        operation.setType("vital:GetPerformanceMetrics");
        operation.setHrestHasMethod("hrest:POST");
        operation.setHrestHasAddress(uri.getBaseUri() + "/system/performance");
        operations.add(operation);
        monitoringService.setOperations(operations);

        return monitoringService;
    }

    private SensorStatus createMonitoringStatusMeasure(UriInfo uri) {
    	SimpleDateFormat printedDateFormat;
    	Date now;
        SensorStatus m = new SensorStatus();

        printedDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        now = new Date();

        m.setContext("http://vital-iot.eu/contexts/measurement.jsonld");
    	m.setId(uri.getBaseUri() + "/sensor/monitoring/observation/" + Long.toHexString(now.getTime()));
        m.setType("ssn:Observation");

        SsnObservationProperty__ ssnObservationProperty = new SsnObservationProperty__();
        ssnObservationProperty.setType("vital:OperationalState");
        m.setSsnObservationProperty(ssnObservationProperty);

        SsnObservationResultTime__ ssnObservationResultTime = new SsnObservationResultTime__();
    	ssnObservationResultTime.setTimeInXSDDateTime(printedDateFormat.format(now));
    	m.setAdditionalProperty("ssn:featureOfInterest", uri.getBaseUri() + "/sensor/monitoring");
        m.setSsnObservationResultTime(ssnObservationResultTime);

        SsnObservationResult__ ssnObservationResult = new SsnObservationResult__();
        ssnObservationResult.setType("ssn:SensorOutput");
        SsnHasValue__ ssnHasValue = new SsnHasValue__();
        ssnHasValue.setType("ssn:ObservationValue");

    	ssnHasValue.setValue("vital:Running");
        ssnObservationResult.setSsnHasValue(ssnHasValue);
        m.setSsnObservationResult(ssnObservationResult);

        return m;
    }

    private SensorStatus createStatusMeasureFromStation(Station station, UriInfo uri) throws ParseException {
    	SimpleDateFormat timestampDateFormat, printedDateFormat;
    	Date now, timestamp = null;
        SensorStatus m = new SensorStatus();

        printedDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        timestampDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        now = new Date();

        m.setContext("http://vital-iot.eu/contexts/measurement.jsonld");
    	m.setId(uri.getBaseUri() + "/sensor/monitoring/observation/" + Long.toHexString(now.getTime()));
        m.setType("ssn:Observation");

        SsnObservationProperty__ ssnObservationProperty = new SsnObservationProperty__();
        ssnObservationProperty.setType("vital:OperationalState");
        m.setSsnObservationProperty(ssnObservationProperty);

        SsnObservationResultTime__ ssnObservationResultTime = new SsnObservationResultTime__();
    	ssnObservationResultTime.setTimeInXSDDateTime(printedDateFormat.format(now));
    	m.setAdditionalProperty("ssn:featureOfInterest", uri.getBaseUri() + "/sensor/" + station.getId());
        m.setSsnObservationResultTime(ssnObservationResultTime);

        SsnObservationResult__ ssnObservationResult = new SsnObservationResult__();
        ssnObservationResult.setType("ssn:SensorOutput");
        SsnHasValue__ ssnHasValue = new SsnHasValue__();
        ssnHasValue.setType("ssn:ObservationValue");

        timestamp = timestampDateFormat.parse(station.getTimestamp());
        if (now.getTime() - timestamp.getTime() > 60 * 1000 * 60) {
        	ssnHasValue.setValue("vital:Unavailable");
        } else {
        	ssnHasValue.setValue("vital:Running");
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

package eu.vital.reply.services;

import eu.vital.reply.clients.IoTSystemClient;
import eu.vital.reply.jsonpojos.DulHasLocation;
import eu.vital.reply.jsonpojos.EmptyRequest;
import eu.vital.reply.jsonpojos.HasLastKnownLocation;
import eu.vital.reply.jsonpojos.IdTypeRequest;
import eu.vital.reply.jsonpojos.IoTSystem;
import eu.vital.reply.jsonpojos.Measure;
import eu.vital.reply.jsonpojos.Metric;
import eu.vital.reply.jsonpojos.MetricRequest;
import eu.vital.reply.jsonpojos.Network;
import eu.vital.reply.jsonpojos.ObservationRequest;
import eu.vital.reply.jsonpojos.Operation;
import eu.vital.reply.jsonpojos.PerformanceMetric;
import eu.vital.reply.jsonpojos.PerformanceMetricsMetadata;
import eu.vital.reply.jsonpojos.Sensor;
import eu.vital.reply.jsonpojos.SensorStatus;
import eu.vital.reply.jsonpojos.Service;
import eu.vital.reply.jsonpojos.SsnHasValue;
import eu.vital.reply.jsonpojos.SsnHasValue_;
import eu.vital.reply.jsonpojos.SsnHasValue__;
import eu.vital.reply.jsonpojos.SsnObserf;
import eu.vital.reply.jsonpojos.SsnObservationProperty;
import eu.vital.reply.jsonpojos.SsnObservationProperty_;
import eu.vital.reply.jsonpojos.SsnObservationProperty__;
import eu.vital.reply.jsonpojos.SsnObservationResult;
import eu.vital.reply.jsonpojos.SsnObservationResultTime;
import eu.vital.reply.jsonpojos.SsnObservationResultTime_;
import eu.vital.reply.jsonpojos.SsnObservationResultTime__;
import eu.vital.reply.jsonpojos.SsnObservationResult_;
import eu.vital.reply.jsonpojos.SsnObservationResult__;
import eu.vital.reply.jsonpojos.Station;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

/*
 * PPI Class that provides all the REST API that a system attached to VITAL will expose
 */

@Path("")
public class PPI {

    private Logger logger;
    private IoTSystemClient client;

    // VITAL ontology extended prefix
    private static final String ontologyPrefix = "http://vital-iot.eu/ontology/ns/";

    // IoT system data
    private static final String apiBasePath = "http://api.citybik.es/v2/networks";
    private static final String networkId = "to-bike";

    // To be able to return network data if CityBikes is temporarily unavailable
    private static HashMap<String, Network> networkCache;
    
    private static Date startupTime = new Date();

    public PPI() {
    	TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    	
        client = new IoTSystemClient();
        logger = LogManager.getLogger(PPI.class);

        if (networkCache == null) {
        	networkCache = new HashMap<String, Network>();
        }

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
            logger.error("[/metadata] Error parsing request");
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
        iotSystem.setId(uri.getBaseUri() + networkId);
        iotSystem.setType("vital:VitalSystem");
        iotSystem.setName(network.getName());
        iotSystem.setDescription("CityBikes " + network.getName() + " network operated by " + network.getCompany());
        iotSystem.setOperator(network.getCompany());
        iotSystem.setServiceArea("http://dbpedia.org/page/" + network.getLocation().getCity());

        List<Station> stations = network.getStations();
        for (i = 0; i < stations.size(); i++) {
            sensors.add(uri.getBaseUri() + networkId + "/sensor/" + stations.get(i).getId());
        }
        sensors.add(uri.getBaseUri() + networkId + "/sensor/monitoring");
        iotSystem.setSensors(sensors);

        services.add(uri.getBaseUri() + networkId + "/service/monitoring");
        services.add(uri.getBaseUri() + networkId + "/service/observation");
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
        metric.setId(uri.getBaseUri() + networkId + "/sensor/monitoring/usedMem");
        list.add(metric);

        metric = new Metric();
        metric.setType(ontologyPrefix + "AvailableMem");
        metric.setId(uri.getBaseUri() + networkId + "/sensor/monitoring/availableMem");
        list.add(metric);

        metric = new Metric();
        metric.setType(ontologyPrefix + "AvailableDisk");
        metric.setId(uri.getBaseUri() + networkId + "/sensor/monitoring/availableDisk");
        list.add(metric);

        metric = new Metric();
        metric.setType(ontologyPrefix + "SysLoad");
        metric.setId(uri.getBaseUri() + networkId + "/sensor/monitoring/sysLoad");
        list.add(metric);

        metric = new Metric();
        metric.setType(ontologyPrefix + "ServedRequests");
        metric.setId(uri.getBaseUri() + networkId + "/sensor/monitoring/servedRequests");
        list.add(metric);

        metric = new Metric();
        metric.setType(ontologyPrefix + "Errors");
        metric.setId(uri.getBaseUri() + networkId + "/sensor/monitoring/errors");
        list.add(metric);

        metric = new Metric();
        metric.setType(ontologyPrefix + "SysUptime");
        metric.setId(uri.getBaseUri() + networkId + "/sensor/monitoring/sysUptime");
        list.add(metric);

        metric = new Metric();
        metric.setType(ontologyPrefix + "PendingRequests");
        metric.setId(uri.getBaseUri() + networkId + "/sensor/monitoring/pendingRequests");
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
        	logger.error("[/system/performance] Error parsing request");
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
            	logger.error("[/system/performance] Bad metric " + m);
                return Response.status(Response.Status.BAD_REQUEST).build();
            }

            metric = new PerformanceMetric();
            metric.setContext("http://vital-iot.eu/contexts/measurement.jsonld");
            metric.setId(uri.getBaseUri() + networkId + "/sensor/monitoring/observation/" + Long.toHexString(date.getTime()));
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

            metric.setSsnFeatureOfInterest(uri.getBaseUri() + networkId);
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
        lifecycleInformation.setId(uri.getBaseUri() + networkId + "/sensor/monitoring/observation/" + Long.toHexString(now.getTime()));
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

        lifecycleInformation.setSsnFeatureOfInterest(uri.getBaseUri() + networkId);

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
        	logger.error("[/service/metadata] Error parsing request");
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
        	logger.error("[/sensor/metadata] Error parsing request");
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
        	logger.error("[/sensor/status] Error parsing request");
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

    @Path("/sensor/observation")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getObservation(String bodyRequest, @Context UriInfo uri) {
        ObservationRequest observationRequest;
        Network network;
        ArrayList<Measure> measures = new ArrayList<Measure>();
        ArrayList<PerformanceMetric> metrics = new ArrayList<PerformanceMetric>();

        try {
        	observationRequest = (ObservationRequest) JsonUtils.deserializeJson(bodyRequest, ObservationRequest.class);
        	boolean missing = false;
        	String errmsg = "";
            if (observationRequest.getSensor().isEmpty()) {
            	missing = true;
            	errmsg = errmsg + "sensor";
            }
            if (observationRequest.getProperty() == null) {
            	missing = true;
            	errmsg = errmsg + " and property";
            }
            if (missing)
            	throw new IOException("field(s) " + errmsg + " is/are required!");
        } catch (IOException e) {
        	logger.error("[/sensor/observation] Error parsing request");
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

        for (String id : observationRequest.getSensor()) {
            if (id.contains("monitoring")) {
                // Monitoring sensor
                PerformanceMetric metric;
                Date date = new Date();
                Runtime runtime = Runtime.getRuntime();
                SimpleDateFormat printedDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
                String type, unit, value;
                if (observationRequest.getProperty().contains("UsedMem")) {
                	type = "vital:UsedMem";
                	unit = "qudt:Byte";
                	value = Long.toString(runtime.totalMemory());
                } else if (observationRequest.getProperty().contains("AvailableMem")) {
                	type = "vital:AvailableMem";
                	unit = "qudt:Byte";
                	value = Long.toString(runtime.freeMemory());
                } else if (observationRequest.getProperty().contains("AvailableDisk")) {
                	type = "vital:AvailableDisk";
                	unit = "qudt:Byte";
                	value = Long.toString(new File("/").getFreeSpace());
                } else if (observationRequest.getProperty().contains("SysLoad")) {
                	type = "vital:SysLoad";
                	unit = "qudt:Percentage";
                	try {
    					value = Double.toString(getProcessCpuLoad());
    				} catch (Exception e) {
    					e.printStackTrace();
    					return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    				}
                } else if (observationRequest.getProperty().contains("ServedRequests")) {
                	type = "vital:ServedRequests";
                	unit = "qudt:Number";
                	value = Integer.toString(StatCounter.getRequestNumber().get());
                } else if (observationRequest.getProperty().contains("Errors")) {
                	type = "vital:Errors";
                	unit = "qudt:Number";
                	value = Integer.toString(StatCounter.getErrorNumber().get());
                } else if (observationRequest.getProperty().contains("SysUptime")) {
                	type = "vital:SysUptime";
                	unit = "qudt:MilliSecond";
                	value = Long.toString(date.getTime() - startupTime.getTime());
                } else if (observationRequest.getProperty().contains("PendingRequests")) {
                	type = "vital:PendingRequests";
                	unit = "qudt:Number";
                	value = Integer.toString(StatCounter.getPendingRequest() - 1);
                } else {
                	logger.error("[/system/performance] Bad metric " + observationRequest.getProperty());
                    return Response.status(Response.Status.BAD_REQUEST).build();
                }

                metric = new PerformanceMetric();
                metric.setContext("http://vital-iot.eu/contexts/measurement.jsonld");
                metric.setId(uri.getBaseUri() + networkId + "/sensor/monitoring/observation/" + Long.toHexString(date.getTime()));
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

                metric.setSsnFeatureOfInterest(uri.getBaseUri() + networkId);
                metrics.add(metric);

                try {
        			return Response.status(Response.Status.OK)
        				.entity(JsonUtils.serializeJson(metrics))
        				.build();
        		} catch (IOException e) {
        			e.printStackTrace();
        			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        		}
            } else {
            	Measure tmpMeasure;
            	boolean found = false;
                for (Station station : network.getStations()) {
                	if (id.contains(station.getId())) {
                		try {
                			tmpMeasure = createMeasureFromStation(station, observationRequest.getProperty(), uri);
						} catch (ParseException e) {
							e.printStackTrace();
							return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
						}
                		if (tmpMeasure != null)
                			measures.add(tmpMeasure);
                		else
                			return Response.status(Response.Status.BAD_REQUEST).build();
                		found = true;
                		break;
                	}
                }
                if (!found)
                	return Response.status(Response.Status.BAD_REQUEST).build();
            }
        }

        try {
			return Response.status(Response.Status.OK)
				.entity(JsonUtils.serializeJson(measures))
				.build();
		} catch (IOException e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
    }

    private Sensor createMonitoringSensor(UriInfo uri) {
    	String id = "monitoring";
    	List<SsnObserf> observedProperties;
        Sensor sensor = new Sensor();

        sensor.setContext("http://vital-iot.eu/contexts/sensor.jsonld");
        sensor.setName(id);
        sensor.setType("vital:MonitoringSensor");
        sensor.setDescription("CityBikes monitoring sensor");
        sensor.setId(uri.getBaseUri() + networkId + "/sensor/" + id);

        sensor.setStatus("vital:Running");

        observedProperties = new ArrayList<SsnObserf>();

        SsnObserf observedProperty = new SsnObserf();
        observedProperty.setType("vital:MemUsed");
        observedProperty.setId(uri.getBaseUri() + networkId + "/sensor/" + id + "/" + "usedMem");
        observedProperties.add(observedProperty);

        observedProperty = new SsnObserf();
        observedProperty.setType("vital:MemAvailable");
        observedProperty.setId(uri.getBaseUri() + networkId + "/sensor/" + id + "/" + "availableMem");
        observedProperties.add(observedProperty);

        observedProperty = new SsnObserf();
        observedProperty.setType("vital:DiskAvailable");
        observedProperty.setId(uri.getBaseUri() + networkId + "/sensor/" + id + "/" + "availableDisk");
        observedProperties.add(observedProperty);

        observedProperty = new SsnObserf();
        observedProperty.setType("vital:SysLoad");
        observedProperty.setId(uri.getBaseUri() + networkId + "/sensor/" + id + "/" + "sysLoad");
        observedProperties.add(observedProperty);

        observedProperty = new SsnObserf();
        observedProperty.setType("vital:ServedRequest");
        observedProperty.setId(uri.getBaseUri() + networkId + "/sensor/" +id+ "/" + "servedRequests");
        observedProperties.add(observedProperty);

        observedProperty = new SsnObserf();
        observedProperty.setType("vital:Errors");
        observedProperty.setId(uri.getBaseUri() + networkId + "/sensor/" + id + "/" + "errors");
        observedProperties.add(observedProperty);

        observedProperty = new SsnObserf();
        observedProperty.setType("vital:SysUpTime");
        observedProperty.setId(uri.getBaseUri() + networkId + "/sensor/" + id + "/" + "sysUptime");
        observedProperties.add(observedProperty);

        observedProperty = new SsnObserf();
        observedProperty.setType("vital:PendingRequests");
        observedProperty.setId(uri.getBaseUri() + networkId + "/sensor/" + id + "/" + "pendingRequests");
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
        sensor.setId(uri.getBaseUri() + networkId + "/sensor/" + id);

        timestampDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
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
        freeBikes.setId(uri.getBaseUri() + networkId + "/sensor/" + id + "/" + "AvailableBikes".toLowerCase());
        SsnObserf emptySlots = new SsnObserf();
        emptySlots.setType("vital:EmptyDocks");
        emptySlots.setId(uri.getBaseUri() + networkId + "/sensor/" + id + "/" + "EmptyDocks".toLowerCase());
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
        observationService.setId(uri.getBaseUri() + networkId + "/service/observation");
        observationService.setType("vital:ObservationService");
        operations = new ArrayList<Operation>();
        operation = new Operation();
        operation.setType("vital:GetObservations");
        operation.setHrestHasMethod("hrest:POST");
        operation.setHrestHasAddress(uri.getBaseUri() + networkId + "/sensor/observation");
        operations.add(operation);
        observationService.setOperations(operations);

        return observationService;
    }

    private Service createMonitoringService(UriInfo uri) {
    	Operation operation;
    	List<Operation> operations;
        Service monitoringService = new Service();
        
        monitoringService.setContext("http://vital-iot.eu/contexts/service.jsonld");
        monitoringService.setId(uri.getBaseUri() + networkId + "/service/monitoring");
        monitoringService.setType("vital:MonitoringService");
        operations = new ArrayList<Operation>();
        operation = new Operation();
        operation.setType("vital:GetSystemStatus");
        operation.setHrestHasMethod("hrest:POST");
        operation.setHrestHasAddress(uri.getBaseUri() + networkId + "/system/status");
        operations.add(operation);
        operation = new Operation();
        operation.setType("vital:GetSensorStatus");
        operation.setHrestHasMethod("hrest:POST");
        operation.setHrestHasAddress(uri.getBaseUri() + networkId + "/sensor/status");
        operations.add(operation);
        operation = new Operation();
        operation.setType("vital:GetSupportedPerformanceMetrics");
        operation.setHrestHasMethod("hrest:GET");
        operation.setHrestHasAddress(uri.getBaseUri() + networkId + "/system/performance");
        operations.add(operation);
        operation = new Operation();
        operation.setType("vital:GetPerformanceMetrics");
        operation.setHrestHasMethod("hrest:POST");
        operation.setHrestHasAddress(uri.getBaseUri() + networkId + "/system/performance");
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
    	m.setId(uri.getBaseUri() + networkId + "/sensor/monitoring/observation/" + Long.toHexString(now.getTime()));
        m.setType("ssn:Observation");

        SsnObservationProperty__ ssnObservationProperty = new SsnObservationProperty__();
        ssnObservationProperty.setType("vital:OperationalState");
        m.setSsnObservationProperty(ssnObservationProperty);

        SsnObservationResultTime__ ssnObservationResultTime = new SsnObservationResultTime__();
    	ssnObservationResultTime.setTimeInXSDDateTime(printedDateFormat.format(now));
    	m.setAdditionalProperty("ssn:featureOfInterest", uri.getBaseUri() + networkId + "/sensor/monitoring");
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
        timestampDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        now = new Date();

        m.setContext("http://vital-iot.eu/contexts/measurement.jsonld");
    	m.setId(uri.getBaseUri() + networkId + "/sensor/monitoring/observation/" + Long.toHexString(now.getTime()));
        m.setType("ssn:Observation");

        SsnObservationProperty__ ssnObservationProperty = new SsnObservationProperty__();
        ssnObservationProperty.setType("vital:OperationalState");
        m.setSsnObservationProperty(ssnObservationProperty);

        SsnObservationResultTime__ ssnObservationResultTime = new SsnObservationResultTime__();
    	ssnObservationResultTime.setTimeInXSDDateTime(printedDateFormat.format(now));
    	m.setAdditionalProperty("ssn:featureOfInterest", uri.getBaseUri() + networkId + "/sensor/" + station.getId());
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

    private Measure createMeasureFromStation(Station station, String property, UriInfo uri) throws ParseException {
        Measure m;
        SimpleDateFormat printedDateFormat, timestampDateFormat;
        Date timestamp = null;

        printedDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        timestampDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        timestamp = timestampDateFormat.parse(station.getTimestamp());

    	m = new Measure();
        m.setContext("http://vital-iot.eu/contexts/measurement.jsonld");
    	m.setId(uri.getBaseUri() + networkId + "/sensor/" + station.getId() + "/observation/" + Long.toHexString(timestamp.getTime()));
        m.setType("ssn:Observation");
        m.setSsnObservedBy(uri.getBaseUri() + networkId + "/sensor/" + station.getId());

        SsnObservationProperty ssnObservationProperty = new SsnObservationProperty();
        ssnObservationProperty.setType("vital:" + property);
        m.setSsnObservationProperty(ssnObservationProperty);

        SsnObservationResultTime ssnObservationResultTime = new SsnObservationResultTime();
        ssnObservationResultTime.setTimeInXSDDateTime(printedDateFormat.format(timestamp));
        m.setSsnObservationResultTime(ssnObservationResultTime);

        DulHasLocation dulHasLocation = new DulHasLocation();
        dulHasLocation.setType("geo:Point");
        dulHasLocation.setGeoLat(station.getLatitude());
        dulHasLocation.setGeoLong(station.getLongitude());
        m.setDulHasLocation(dulHasLocation);

        SsnObservationResult ssnObservationResult = new SsnObservationResult();
        ssnObservationResult.setType("ssn:SensorOutput");
        SsnHasValue ssnHasValue = new SsnHasValue();
        ssnHasValue.setType("ssn:ObservationValue");

        if (property.equals("AvailableBikes")) {
            ssnHasValue.setValue(station.getFreeBikes());
            ssnHasValue.setQudtUnit("qudt:Number");
        } else if (property.equals("EmptyDocks")) {
            ssnHasValue.setValue(station.getEmptySlots());
            ssnHasValue.setQudtUnit("qudt:Number");
        } else {
            return null;
        }
        ssnObservationResult.setSsnHasValue(ssnHasValue);
        m.setSsnObservationResult(ssnObservationResult);

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

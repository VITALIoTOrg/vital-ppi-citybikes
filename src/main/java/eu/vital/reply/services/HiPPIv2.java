package eu.vital.reply.services;

/**
 * Created by l.bracco on 28/09/2015.
 */

import eu.vital.reply.clients.HiReplySvc;
import eu.vital.reply.jsonpojosv2.*;
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
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * HiPPIv2 Class that provides all the REST API that a system attached to Vital will expose
 *
 * @author <a href="mailto:f.deceglia@reply.it">Fabrizio de Ceglia</a>
 * @author <a href="mailto:l.bracco@reply.it">Lorenzo Bracco</a>
 * @version 2.0.0
 */

@Path("/v2")
public class HiPPIv2 {

    private Logger logger;
    private HiReplySvc hiReplySvc;
    private ConfigReader configReader;

    private String hostPort;
    private String hostName;
    private String symbolicUri;
    private String ontBaseUri;

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

    public HiPPIv2() {

        configReader = ConfigReader.getInstance();

        hiReplySvc = new HiReplySvc();
        logger = LogManager.getLogger(HiPPIv2.class);

        hostName = configReader.get(ConfigReader.SERVER_HOSTNAME);
        hostPort = configReader.get(ConfigReader.SERVER_PORT);
        symbolicUri = configReader.get(ConfigReader.SYMBOLIC_URI);
        ontBaseUri = configReader.get(ConfigReader.ONT_BASE_URI_PROPERTY);

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
        EmptyRequest emptyRequest = null;

        try {
            emptyRequest = (EmptyRequest) JsonUtils.deserializeJson(bodyRequest, EmptyRequest.class);
        } catch (IOException e) {
            this.logger.error("/v2/METADATA error parsing request header");
            return "{\n" +
                    "\"error\": \"Malformed request body\"\n"+
                    "}";
        }

        ServiceList system = hiReplySvc.getSnapshot();

        IoTSystem ioTSystem = new IoTSystem();
        ArrayList<String> services = new ArrayList<>();
        ArrayList<String> sensors = new ArrayList<>();

        // Using the context from Katerina's server... we should store it on and use it from our server
        ioTSystem.setContext("http://104.131.128.70:8080/istanbul-traffic/contexts/system.jsonld");
        ioTSystem.setId(system.getIoTSystem().getUri());
        ioTSystem.setType("vital:IoTSystem"); // is it really it?
        ioTSystem.setName(system.getIoTSystem().getID());
        ioTSystem.setDescription(system.getIoTSystem().getDescription());

        ioTSystem.setOperator(system.getIoTSystem().getOperator());
        ioTSystem.setServiceArea(system.getIoTSystem().getServiceArea());

        List<ServiceList.TrafficSensor> trafficSensors = this.hiReplySvc.getSnapshot().getTrafficSensor();
        for(i = 0; i < trafficSensors.size(); i++) {
            sensors.add(this.createSensorFromTraffic(trafficSensors.get(i)).getId());
        }
        sensors.add(this.createMonitoringSensor().getId());
        ioTSystem.setSensors(sensors);

        // Adding services (describes in /service/metadata)
        // TODO: create services IDs from configuration
        services.add("http://vital2.cloud.reply.eu:8080/service/configuration");
        services.add("http://vital2.cloud.reply.eu:8080/service/monitoring");
        services.add("http://vital2.cloud.reply.eu:8080/service/observation");

        ioTSystem.setServices(services);

        if (system.getIoTSystem().getStatus().equals("Running")) {
            ioTSystem.setStatus("vital:Running");
        }

        String out = "";

        try {
            out = JsonUtils.serializeJson(ioTSystem);
        } catch (IOException e) {
            this.logger.error("JSON UTILS IO EXCEPTION - metadata information");
            throw new Exception("JSON UTILS IO EXCEPTION - metadata information");
            //e.printStackTrace();
        }

        return out;
    }

    @Path("/performance")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getPerformanceMetrics() throws Exception {

        String out = "";

        PerformaceMetricsMetadata performaceMetricsMetadata = new PerformaceMetricsMetadata();

        List<SsnObserf> list = new ArrayList<>();

        SsnObserf ssnObserf = new SsnObserf();
        ssnObserf.setType(this.transfProt+this.ontBaseUri+"memUsed");
        ssnObserf.setUri(this.transfProt+this.symbolicUri+"ico/PerformanceIco/"+"memUsed");
        list.add(ssnObserf);

        ssnObserf = new SsnObserf();
        ssnObserf.setType(this.transfProt+this.ontBaseUri+"memAvailable");
        ssnObserf.setUri(this.transfProt+this.symbolicUri+"ico/PerformanceIco/"+"memAvailable");
        list.add(ssnObserf);

        ssnObserf = new SsnObserf();
        ssnObserf.setType(this.transfProt+this.ontBaseUri+"diskAvailable");
        ssnObserf.setUri(this.transfProt+this.symbolicUri+"ico/PerformanceIco/"+"memAvailable");
        list.add(ssnObserf);

        ssnObserf = new SsnObserf();
        ssnObserf.setType(this.transfProt+this.ontBaseUri+"cpuUsage");
        ssnObserf.setUri(this.transfProt+this.symbolicUri+"ico/PerformanceIco/"+"memAvailable");
        list.add(ssnObserf);

        ssnObserf = new SsnObserf();
        ssnObserf.setType(this.transfProt+this.ontBaseUri+"servedRequest");
        ssnObserf.setUri(this.transfProt+this.symbolicUri+"ico/PerformanceIco/"+"memAvailable");
        list.add(ssnObserf);

        ssnObserf = new SsnObserf();
        ssnObserf.setType(this.transfProt+this.ontBaseUri+"errors");
        ssnObserf.setUri(this.transfProt+this.symbolicUri+"ico/PerformanceIco/"+"memAvailable");
        list.add(ssnObserf);

        ssnObserf = new SsnObserf();
        ssnObserf.setType(this.transfProt+this.ontBaseUri+"upTime");
        ssnObserf.setUri(this.transfProt+this.symbolicUri+"ico/PerformanceIco/"+"memAvailable");
        list.add(ssnObserf);

        ssnObserf = new SsnObserf();
        ssnObserf.setType(this.transfProt+this.ontBaseUri+"pendingRequests");
        ssnObserf.setUri(this.transfProt+this.symbolicUri+"ico/PerformanceIco/"+"memAvailable");
        list.add(ssnObserf);

        performaceMetricsMetadata.setSsnObserves(list);

        try {
            out = JsonUtils.serializeJson(performaceMetricsMetadata);
        } catch (IOException e) {
            this.logger.error("JSON UTILS IO EXCEPTION - getPerformanceMetric information");
            throw new Exception("JSON UTILS IO EXCEPTION - getPerformanceMetric information");
            //e.printStackTrace();
        }

        return out;

    }

    /*
    test services for:
        - pending requests (/fool)
        - error requests (/exc)

    @Path("/fool")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getFool() throws InterruptedException {

        Thread.sleep(60000);

        return "{\n" +
                "\"fooled\": \"fooled service\"\n"+
                "}";

    }

    @Path("/exc")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getExc() throws Exception {
        String out = "";

        try {
            int a = 1/0;
        } catch (Exception e) {
            throw new Exception("eccezione di test");
        }

        return out;
    }
    */


    @Path("/configurationOptions")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getConfigurationOptions() throws Exception {

        String info = uriInfo.getBaseUri().toString();

        String out = "";

        ConfigurationOptionsGetBody response = new ConfigurationOptionsGetBody();

        ConfigurationOption configurationOption = new ConfigurationOption();
        List<ConfigurationOption> configurationOptions = new ArrayList<>();

        configurationOption.setName("logVerbosity");
        configurationOption.setValue(this.hiReplySvc.getSnapshot().getTaskManager().getLogsPriorityLevel());
        configurationOption.setType(this.transfProt + this.ontBaseUri + "string");
        configurationOption.setPermissions("rw");

        configurationOptions.add(configurationOption);

        response.setConfigurationOptions(configurationOptions);

        try {
            out = JsonUtils.serializeJson(response);
        } catch (IOException e) {
            this.logger.error("getConfigurationOptions - Deserialize JSON UTILS IO EXCEPTION");
            throw new Exception("getConfigurationOptions - Deserialize JSON UTILS IO EXCEPTION");
            //e.printStackTrace();
        }

        return out;
    }


    @Path("/configurationOptions")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response setConfigurationOptions(String bodyRequest) {

        ConfigurationOptionsReqBody configurationOptionsReqBody = null;
        boolean esito = false;

        try {
            configurationOptionsReqBody = (ConfigurationOptionsReqBody) JsonUtils.deserializeJson(bodyRequest, ConfigurationOptionsReqBody.class);
        } catch (IOException e) {
            this.logger.error("setConfigurationOptions -  error parsing request header");
            return Response.serverError().build();
        }

        String taskManagerServiceId = this.hiReplySvc.getSnapshot().getTaskManager().getID();

        List<ConfigurationOption_> configList = configurationOptionsReqBody.getConfigurationOptions();

        for (int i = 0; i<configList.size(); i++) {
            String currentConfigurationOptions = configList.get(i).getName();
            if (currentConfigurationOptions.equals(this.logVerbosity)) {
                String logsPriorityValue = configList.get(i).getValue().toUpperCase();
                try {
                    esito = this.hiReplySvc.setPropertyValue(taskManagerServiceId,hiLogVerbositySetting,logsPriorityValue);
                } catch (Exception e) {
                    esito = false;
                }
            }
        }

        if (esito) {
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    /**
     * Method that returns the Lifecycle information of the system. This method is not mandatory.
     * @param bodyRequest <br>
     *            JSON-LD String with the body request <br>
     *            { <br>
     *              "@context": "http://vital-iot.org/contexts/query.jsonld", <br>
     *              "type": "vital:iotSystem" <br>
     *            } <br>
     * @return Returns a string with the serialized JSON-LD Lifecycle information.
     */
    @Path("/external/lifecycle_information")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String getLifecycleInformation(String bodyRequest) throws Exception {

        EmptyRequest emptyRequest = null;

        try {
            emptyRequest = (EmptyRequest) JsonUtils.deserializeJson(bodyRequest, EmptyRequest.class);
        } catch (IOException e) {
            this.logger.error("lifecycle information -  error parsing request header");
            return "{\n" +
                    "\"error\": \"Malformed request body\"\n"+
                    "}";
        }
        // TODO --> check sulla request, trattamento di eventuali filtri

        ServiceList system = hiReplySvc.getSnapshot();
        LifecycleInformation lifecycleInformation = new LifecycleInformation();

        lifecycleInformation.setContext("http://vital.iot.org/system.jsonld");
        lifecycleInformation.setUri(system.getIoTSystem().getUri());

        if (system.getIoTSystem().getStatus().equals("Running")) {
            lifecycleInformation.setStatus("vital:Running");
        } else {
            lifecycleInformation.setStatus("vital:Unavailable");
        }

        String out = "";

        try {
            out = JsonUtils.serializeJson(lifecycleInformation);
        } catch (IOException e) {
            this.logger.error("JSON UTILS IO EXCEPTION - lifecycle information");
            throw new Exception("JSON UTILS IO EXCEPTION - lifecycle information");
            //e.printStackTrace();
        }

        return out;
    }

    /**
     * Method that returns the metadata about the requested ICO/ICOs. This method is mandatory.
     * @param bodyRequest <br>
     *            JSON-LD String with the body request <br>
     *            { <br>
     *              "@context": "http://vital-iot.org/contexts/query.jsonld", <br>
     *              "icos": <br>
     *              [ <br>
     *                  "http://www.example.com/ico/123/", <br>
     *                  "http://www.example.com/ico/1234/", <br>
     *                  "http://www.example.com/ico/12345/" <br>
     *              ] <br>
     *            } <br>
     * If the ICOs field is omitted, all the ICOs are requested <br>
     * @return Returns a serialized String with the list of the requested sensor. <br>
     */
    @Path("/ico/metadata")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String getIcoMetadata(String bodyRequest) throws Exception {

        ICORequest icoRequest = new ICORequest();

        try {
            icoRequest = (ICORequest) JsonUtils.deserializeJson(bodyRequest, ICORequest.class);
        } catch (IOException e) {
            this.logger.error("GET OBSERVATION - IOException parsing the json request");
            return "{\n" +
                    "\"error\": \"Malformed request body\"\n"+
                    "}";
        }
        // TODO --> check sulla request, trattamento di eventuali filtri

        List<String> requestedSensor = new ArrayList<>();

        try {
            requestedSensor = icoRequest.getIcos();
        } catch (NullPointerException e) {
            this.logger.error("/ico/metadata IO Exception - Requested Sensor");
            throw new Exception("/ico/metadata IO Exception - Requested Sensor");
            //e.printStackTrace();
        }


        List<Sensor> sensors = new ArrayList<>(); //lista da restituire in output


        if (requestedSensor.size() == 0) {
            //restituisci tutti i sensori
            List<ServiceList.TrafficSensor> trafficSensors = this.hiReplySvc.getSnapshot().getTrafficSensor();
            for (int i = 0; i < trafficSensors.size(); i++) {
                sensors.add(this.createSensorFromTraffic(trafficSensors.get(i)));
            }
            sensors.add(this.createMonitoringSensor());
        } else {
            //restituisci solo i sensori desirati
            for (int i = 0; i < requestedSensor.size(); i++) {
                String currentId = requestedSensor.get(i).replaceAll(this.transfProt+this.symbolicUri+"ico/","");

                if (currentId.contains("PerformanceIco")) {
                    sensors.add(this.createMonitoringSensor());
                } else {
                    String filter = hiReplySvc.createFilter("ID",currentId);

                    ServiceList.TrafficSensor currentTrafficSensor = null;

                    try {
                        currentTrafficSensor = this.hiReplySvc.getSnapshotFiltered(filter).getTrafficSensor().get(0);
                        sensors.add(this.createSensorFromTraffic(currentTrafficSensor));
                    } catch (IndexOutOfBoundsException e) {
                        logger.error("getIcoMetadata ID: " + currentId + " not present.");
                        //in caso di sensore nn presente, l'esecuzione continua per cercare gli altri
                    }
                }

            }
        }

        String out = "";

        try {
            out = JsonUtils.serializeJson(sensors);
        } catch (IOException e) {
            this.logger.error("getIcoMetadata - Deserialize JSON UTILS IO EXCEPTION");
            throw new Exception("getIcoMetadata - Deserialize JSON UTILS IO EXCEPTION");
            //e.printStackTrace();
        }

        return out;
    }



    /**
     * Method that returns the observation about the requested ICO/ICOs. This method is mandatory.
     * @param bodyRequest <br>
     *            JSON-LD String with the body request <br>
     *            { <br>
     *              "@context": "http://vital-iot.org/contexts/query.jsonld", <br>
     *              "ico": "http://www.example.com/ico/123", <br>
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
     * @return Returns a serialized String with the list of the requested measure.
     */
    @Path("observation")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String getObservation(String bodyRequest) throws Exception {

        ObservationRequest observationRequest = null;
        ArrayList<Measure> measures = new ArrayList<>();

        try {
            observationRequest = (ObservationRequest) JsonUtils.deserializeJson(bodyRequest, ObservationRequest.class);
        } catch (IOException e) {
            this.logger.error("GET OBSERVATION - IOException parsing the json request");
            return "{\n" +
                    "\"error\": \"Malformed request body\"\n"+
                    "}";
        }

        String id = observationRequest.getIco().replaceAll(this.transfProt+this.symbolicUri+"ico/", "");

        //id per sensore performance ==> PerformanceIco
        //http://vital2.cloud.reply.eu/ico/PerformanceIco

        if (id.contains("PerformanceIco")) {
            //caso del sensore delle perfomance

            //estrapolo la performance richiesta
            String requestedPerformance = observationRequest.getProperty();/*.replaceAll(this.transfProt+this.ontBaseUri,"");*/
            if (requestedPerformance.contains("memUsed")) {
                return this.getMemoryUsed();
            } else if (requestedPerformance.contains("memAvailable")) {
                return this.getMemoryAvailable();
            } else if (requestedPerformance.contains("diskAvailable")) {
                return this.getDiskAvailable();
            } else if (requestedPerformance.contains("cpuUsage")) {
                return this.getCpuUsage();
            } else if (requestedPerformance.contains("servedRequest")) {
                return this.getServedRequest();
            } else if (requestedPerformance.contains("errors")) {
                return this.getErrors();
            } else if (requestedPerformance.contains("upTime")) {
                return this.getUpTime();
            } else if (requestedPerformance.contains("pendingRequests")) {
                return this.getPendingRequest();
            } else {
                return "{\n" +
                        "\"error\": \"Performance "+requestedPerformance+" not present.\"\n"+
                        "}";
            }
        } else {
            //caso del sensore del traffico
            //controllo che il sensore richiesto (id) sia effettivamente presente sul virtualizzatore. in caso nn è presente genero un json di errore
            ServiceList.TrafficSensor currentSensor = this.retrieveSensor(id);

            if (currentSensor == null) {
                return "{\n" +
                        "\"error\": \"ID " + id + " not present.\"\n" +
                        "}";
            }

            //controllo che la proprietà richiesta sia tra quelle possibili (Speed, Color, Reverse Speed, Reverse Color) del sensore
            String property = observationRequest.getProperty().replaceAll(this.transfProt + this.ontBaseUri, "");

            if (!this.checkTrafficProperty(currentSensor, property)) {
                return "{\n" +
                        "\"error\": \"Property " + property + " not present for " + id + " sensor.\"\n" +
                        "}";
            }

            if (observationRequest.getFrom() != null && observationRequest.getTo() != null) {
                //get history range

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

                for (int i = 0; i < historyMeasures.size(); i++) {

                    measures.add(this.createMeasureFromHistoryMeasure(historyMeasures.get(i), currentSensor, property));

                }

            } else if (observationRequest.getFrom() != null && observationRequest.getTo() == null) {
                //get tutti i valori da from

                SimpleDateFormat arrivedFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                SimpleDateFormat hiReplyFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

                Date fromDate;
                Date toDate = new Date(); //hi reply ha cmq bisogno della data di fine, quindi imposto quella corrente
                Date fromDateHiReply = null;
                Date toDateHiReply = null;

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
                    //e.printStackTrace();
                }

                List<HistoryMeasure> historyMeasures = this.getHistoryMeasures(hiReplySvc.getPropertyHistoricalValues(id, property, fromDateHiReply, toDateHiReply));

                for (int i = 0; i < historyMeasures.size(); i++) {

                    measures.add(this.createMeasureFromHistoryMeasure(historyMeasures.get(i), currentSensor, property));

                }

            } else if (observationRequest.getFrom() == null && observationRequest.getTo() == null) {
                //get ultimo valore
                measures.add(this.createMeasureFromSensor(currentSensor, property));
            }

            String out = "";

            try {
                out = JsonUtils.serializeJson(measures);
            } catch (IOException e) {
                this.logger.error("GET OBSERVATION - serialize to json response IO Exception");
                throw new Exception("GET OBSERVATION - serialize to json response IO Exception");
                //e.printStackTrace();
            }

            return out;
        }
    }

    private String getPendingRequest() throws Exception {
        String out = "";

        /*
        Sottraggo 1 xke a questo punto la corrente resources è considerata in pending
        dato che al momento della richiesta QUESTO metodo non è ancora andato in FINISH --> risulta tra i pending
         */

        int pendingRequest = StatCounter.getPendingRequest()-1;

        Date date = new Date();
        SimpleDateFormat printedDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

        PerformanceMetric pendingReq = new PerformanceMetric();

        pendingReq.setContext("http://vital.iot.org/measurement.jsonld");
        pendingReq.setUri(this.transfProt+this.symbolicUri + "/iot/hireply/perf/pendingRequests");
        pendingReq.setType("ssn:Observation");

        SsnObservationProperty_ ssnObservationProperty_ = new SsnObservationProperty_();
        ssnObservationProperty_.setType(this.transfProt+this.ontBaseUri+"pendingRequests");

        pendingReq.setSsnObservationProperty(ssnObservationProperty_);

        SsnObservationResultTime_ ssnObservationResultTime_ = new SsnObservationResultTime_();

        ssnObservationResultTime_.setInXSDDateTime(printedDateFormat.format(date));
        pendingReq.setSsnObservationResultTime(ssnObservationResultTime_);

        SsnObservationQuality_ ssnObservationQuality_ = new SsnObservationQuality_();
        SsnHasMeasurementProperty_ ssnHasMeasurementProperty_ = new SsnHasMeasurementProperty_();
        ssnHasMeasurementProperty_.setType("Reliability");
        ssnHasMeasurementProperty_.setHasValue("HighReliability");
        ssnObservationQuality_.setSsnHasMeasurementProperty(ssnHasMeasurementProperty_);
        pendingReq.setSsnObservationQuality(ssnObservationQuality_);

        SsnObservationResult_ ssnObservationResult_ = new SsnObservationResult_();
        ssnObservationResult_.setType("pendingRequests");
        SsnHasValue_ ssnHasValue_ = new SsnHasValue_();
        ssnHasValue_.setType("ssn:ObservationValue");
        ssnHasValue_.setValue(pendingRequest+"");
        ssnHasValue_.setQudtUnit("qudt:number");
        ssnObservationResult_.setSsnHasValue(ssnHasValue_);
        pendingReq.setSsnObservationResult(ssnObservationResult_);

        try {
            out = JsonUtils.serializeJson(pendingReq);
        } catch (IOException e) {
            this.logger.error("pendingReq - Deserialize JSON UTILS IO EXCEPTION");
            throw new Exception("pendingReq - Deserialize JSON UTILS IO EXCEPTION");
            //e.printStackTrace();
        }

        return out;
    }


    private String getUpTime() throws Exception {

        String out = "";

        Date now = new Date();

        Date hiReplyStartTime = this.hiReplySvc.getSnapshot().getTaskManager().getLastStartTime().toGregorianCalendar().getTime();

        long span = now.getTime() - hiReplyStartTime.getTime();

        SimpleDateFormat printedDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

        PerformanceMetric sysUpTime = new PerformanceMetric();

        sysUpTime.setContext("http://vital.iot.org/measurement.jsonld");
        sysUpTime.setUri(this.transfProt+this.symbolicUri + "/iot/hireply/perf/upTime");
        sysUpTime.setType("ssn:Observation");

        SsnObservationProperty_ ssnObservationProperty_ = new SsnObservationProperty_();
        ssnObservationProperty_.setType(this.transfProt+this.ontBaseUri+"servedRequest");

        sysUpTime.setSsnObservationProperty(ssnObservationProperty_);

        SsnObservationResultTime_ ssnObservationResultTime_ = new SsnObservationResultTime_();

        ssnObservationResultTime_.setInXSDDateTime(printedDateFormat.format(now));
        sysUpTime.setSsnObservationResultTime(ssnObservationResultTime_);

        SsnObservationQuality_ ssnObservationQuality_ = new SsnObservationQuality_();
        SsnHasMeasurementProperty_ ssnHasMeasurementProperty_ = new SsnHasMeasurementProperty_();
        ssnHasMeasurementProperty_.setType("Reliability");
        ssnHasMeasurementProperty_.setHasValue("HighReliability");
        ssnObservationQuality_.setSsnHasMeasurementProperty(ssnHasMeasurementProperty_);
        sysUpTime.setSsnObservationQuality(ssnObservationQuality_);

        SsnObservationResult_ ssnObservationResult_ = new SsnObservationResult_();
        ssnObservationResult_.setType("upTime");
        SsnHasValue_ ssnHasValue_ = new SsnHasValue_();
        ssnHasValue_.setType("ssn:ObservationValue");
        ssnHasValue_.setValue(span+"");
        ssnHasValue_.setQudtUnit("qudt:milliseconds");
        ssnObservationResult_.setSsnHasValue(ssnHasValue_);
        sysUpTime.setSsnObservationResult(ssnObservationResult_);

        try {
            out = JsonUtils.serializeJson(sysUpTime);
        } catch (IOException e) {
            this.logger.error("upTime - Deserialize JSON UTILS IO EXCEPTION");
            throw new Exception("upTime - Deserialize JSON UTILS IO EXCEPTION");
            //e.printStackTrace();
        }

        return out;
    }


    private String getServedRequest() throws Exception {
        String out = "";

        /*aggiungo 1 al corrente. la callback aggiorna il numero solo
          a fine metodo, quindi senza il +1 il dato non sarebbe consistente
          mancando il conto dell'esecuzione corrente.
         */

        requestCount = StatCounter.getRequestNumber();
        int auxCount = requestCount.get();
        requestCount.set(auxCount+1);

        Date date = new Date();
        SimpleDateFormat printedDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

        PerformanceMetric servedRequest = new PerformanceMetric();

        servedRequest.setContext("http://vital.iot.org/measurement.jsonld");
        servedRequest.setUri(this.transfProt+this.symbolicUri + "/iot/hireply/perf/servedRequest");
        servedRequest.setType("ssn:Observation");

        SsnObservationProperty_ ssnObservationProperty_ = new SsnObservationProperty_();
        ssnObservationProperty_.setType(this.transfProt+this.ontBaseUri+"servedRequest");

        servedRequest.setSsnObservationProperty(ssnObservationProperty_);

        SsnObservationResultTime_ ssnObservationResultTime_ = new SsnObservationResultTime_();

        ssnObservationResultTime_.setInXSDDateTime(printedDateFormat.format(date));
        servedRequest.setSsnObservationResultTime(ssnObservationResultTime_);

        SsnObservationQuality_ ssnObservationQuality_ = new SsnObservationQuality_();
        SsnHasMeasurementProperty_ ssnHasMeasurementProperty_ = new SsnHasMeasurementProperty_();
        ssnHasMeasurementProperty_.setType("Reliability");
        ssnHasMeasurementProperty_.setHasValue("HighReliability");
        ssnObservationQuality_.setSsnHasMeasurementProperty(ssnHasMeasurementProperty_);
        servedRequest.setSsnObservationQuality(ssnObservationQuality_);

        SsnObservationResult_ ssnObservationResult_ = new SsnObservationResult_();
        ssnObservationResult_.setType("servedRequest");
        SsnHasValue_ ssnHasValue_ = new SsnHasValue_();
        ssnHasValue_.setType("ssn:ObservationValue");
        ssnHasValue_.setValue(requestCount.get()+"");
        ssnHasValue_.setQudtUnit("qudt:Number");
        ssnObservationResult_.setSsnHasValue(ssnHasValue_);
        servedRequest.setSsnObservationResult(ssnObservationResult_);

        try {
            out = JsonUtils.serializeJson(servedRequest);
        } catch (IOException e) {
            this.logger.error("servedReq - Deserialize JSON UTILS IO EXCEPTION");
            throw new Exception("servedReq - Deserialize JSON UTILS IO EXCEPTION");
            //e.printStackTrace();
        }

        return out;
    }


    private String getErrors() throws Exception {
        String out = "";

        requestError = StatCounter.getErrorNumber();

        Date date = new Date();
        SimpleDateFormat printedDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

        PerformanceMetric servedRequest = new PerformanceMetric();

        servedRequest.setContext("http://vital.iot.org/measurement.jsonld");
        servedRequest.setUri(this.transfProt+this.symbolicUri + "/iot/hireply/perf/errors");
        servedRequest.setType("ssn:Observation");

        SsnObservationProperty_ ssnObservationProperty_ = new SsnObservationProperty_();
        ssnObservationProperty_.setType(this.transfProt+this.ontBaseUri+"errors");

        servedRequest.setSsnObservationProperty(ssnObservationProperty_);

        SsnObservationResultTime_ ssnObservationResultTime_ = new SsnObservationResultTime_();

        ssnObservationResultTime_.setInXSDDateTime(printedDateFormat.format(date));
        servedRequest.setSsnObservationResultTime(ssnObservationResultTime_);

        SsnObservationQuality_ ssnObservationQuality_ = new SsnObservationQuality_();
        SsnHasMeasurementProperty_ ssnHasMeasurementProperty_ = new SsnHasMeasurementProperty_();
        ssnHasMeasurementProperty_.setType("Reliability");
        ssnHasMeasurementProperty_.setHasValue("HighReliability");
        ssnObservationQuality_.setSsnHasMeasurementProperty(ssnHasMeasurementProperty_);
        servedRequest.setSsnObservationQuality(ssnObservationQuality_);

        SsnObservationResult_ ssnObservationResult_ = new SsnObservationResult_();
        ssnObservationResult_.setType("servedRequest");
        SsnHasValue_ ssnHasValue_ = new SsnHasValue_();
        ssnHasValue_.setType("ssn:ObservationValue");
        ssnHasValue_.setValue(requestError.get()+"");
        ssnHasValue_.setQudtUnit("qudt:Number");
        ssnObservationResult_.setSsnHasValue(ssnHasValue_);
        servedRequest.setSsnObservationResult(ssnObservationResult_);

        try {
            out = JsonUtils.serializeJson(servedRequest);
        } catch (IOException e) {
            this.logger.error("errors - Deserialize JSON UTILS IO EXCEPTION");
            throw new Exception("errors - Deserialize JSON UTILS IO EXCEPTION");
            //e.printStackTrace();
        }

        return out;
    }


    private String getMemoryUsed() throws Exception {

        String out = "";

        ServiceList.TaskManager tm = this.hiReplySvc.getSnapshot().getTaskManager();

        BigInteger memoryUsed = tm.getMemoryConsumption();
        Date date = new Date();
        SimpleDateFormat printedDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");


        PerformanceMetric memUsed = new PerformanceMetric();

        memUsed.setContext("http://vital.iot.org/measurement.jsonld");
        memUsed.setUri(this.transfProt+this.symbolicUri + "/iot/hireply/perf/memUsed");
        memUsed.setType("ssn:Observation");

        SsnObservationProperty_ ssnObservationProperty_ = new SsnObservationProperty_();
        ssnObservationProperty_.setType(this.transfProt+this.ontBaseUri+"memUsed");
        memUsed.setSsnObservationProperty(ssnObservationProperty_);

        SsnObservationResultTime_ ssnObservationResultTime_ = new SsnObservationResultTime_();

        ssnObservationResultTime_.setInXSDDateTime(printedDateFormat.format(date));
        memUsed.setSsnObservationResultTime(ssnObservationResultTime_);

        SsnObservationQuality_ ssnObservationQuality_ = new SsnObservationQuality_();
        SsnHasMeasurementProperty_ ssnHasMeasurementProperty_ = new SsnHasMeasurementProperty_();
        ssnHasMeasurementProperty_.setType("Reliability");
        ssnHasMeasurementProperty_.setHasValue("HighReliability");
        ssnObservationQuality_.setSsnHasMeasurementProperty(ssnHasMeasurementProperty_);
        memUsed.setSsnObservationQuality(ssnObservationQuality_);

        SsnObservationResult_ ssnObservationResult_ = new SsnObservationResult_();
        ssnObservationResult_.setType("memoryMetric");
        SsnHasValue_ ssnHasValue_ = new SsnHasValue_();
        ssnHasValue_.setType("ssn:ObservationValue");
        ssnHasValue_.setValue(memoryUsed.toString());
        ssnHasValue_.setQudtUnit("qudt:Byte");
        ssnObservationResult_.setSsnHasValue(ssnHasValue_);
        memUsed.setSsnObservationResult(ssnObservationResult_);

        try {
            out = JsonUtils.serializeJson(memUsed);
        } catch (IOException e) {
            this.logger.error("memUsed - Deserialize JSON UTILS IO EXCEPTION");
            throw new Exception("memUsed - Deserialize JSON UTILS IO EXCEPTION");
            //e.printStackTrace();
        }

        return out;
    }


    private String getMemoryAvailable() throws Exception {

        String out = "";

        ServiceList.TaskManager tm = this.hiReplySvc.getSnapshot().getTaskManager();

        BigInteger memAvailable = tm.getAvailMemoryCounter();
        Date date = new Date();
        SimpleDateFormat printedDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

        PerformanceMetric memUsed = new PerformanceMetric();

        memUsed.setContext("http://vital.iot.org/measurement.jsonld");
        memUsed.setUri(this.transfProt+this.symbolicUri + "/iot/hireply/perf/memAvailable");
        memUsed.setType("ssn:Observation");

        SsnObservationProperty_ ssnObservationProperty_ = new SsnObservationProperty_();
        ssnObservationProperty_.setType(this.transfProt+this.ontBaseUri+"memAvailable");
        memUsed.setSsnObservationProperty(ssnObservationProperty_);

        SsnObservationResultTime_ ssnObservationResultTime_ = new SsnObservationResultTime_();

        ssnObservationResultTime_.setInXSDDateTime(printedDateFormat.format(date));
        memUsed.setSsnObservationResultTime(ssnObservationResultTime_);

        SsnObservationQuality_ ssnObservationQuality_ = new SsnObservationQuality_();
        SsnHasMeasurementProperty_ ssnHasMeasurementProperty_ = new SsnHasMeasurementProperty_();
        ssnHasMeasurementProperty_.setType("Reliability");
        ssnHasMeasurementProperty_.setHasValue("HighReliability");
        ssnObservationQuality_.setSsnHasMeasurementProperty(ssnHasMeasurementProperty_);
        memUsed.setSsnObservationQuality(ssnObservationQuality_);

        SsnObservationResult_ ssnObservationResult_ = new SsnObservationResult_();
        ssnObservationResult_.setType("memoryMetric");
        SsnHasValue_ ssnHasValue_ = new SsnHasValue_();
        ssnHasValue_.setType("ssn:ObservationValue");
        ssnHasValue_.setValue(memAvailable.toString());
        ssnHasValue_.setQudtUnit("qudt:Byte");
        ssnObservationResult_.setSsnHasValue(ssnHasValue_);
        memUsed.setSsnObservationResult(ssnObservationResult_);

        try {
            out = JsonUtils.serializeJson(memUsed);
        } catch (IOException e) {
            this.logger.error("memAvailable - Deserialize JSON UTILS IO EXCEPTION");
            throw new Exception("memAvailable - Deserialize JSON UTILS IO EXCEPTION");
            //e.printStackTrace();
        }

        return out;
    }


    private String getCpuUsage() throws Exception {

        String out = "";

        ServiceList.TaskManager tm = this.hiReplySvc.getSnapshot().getTaskManager();

        float cpuUsage = tm.getCPUTotalCounter();
        Date date = new Date();
        SimpleDateFormat printedDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

        PerformanceMetric memUsed = new PerformanceMetric();

        memUsed.setContext("http://vital.iot.org/measurement.jsonld");
        memUsed.setUri(this.transfProt+this.symbolicUri + "/iot/hireply/perf/cpuUsage");
        memUsed.setType("ssn:Observation");

        SsnObservationProperty_ ssnObservationProperty_ = new SsnObservationProperty_();
        ssnObservationProperty_.setType(this.transfProt+this.ontBaseUri+"cpuUsage");
        memUsed.setSsnObservationProperty(ssnObservationProperty_);

        SsnObservationResultTime_ ssnObservationResultTime_ = new SsnObservationResultTime_();

        ssnObservationResultTime_.setInXSDDateTime(printedDateFormat.format(date));
        memUsed.setSsnObservationResultTime(ssnObservationResultTime_);

        SsnObservationQuality_ ssnObservationQuality_ = new SsnObservationQuality_();
        SsnHasMeasurementProperty_ ssnHasMeasurementProperty_ = new SsnHasMeasurementProperty_();
        ssnHasMeasurementProperty_.setType("Reliability");
        ssnHasMeasurementProperty_.setHasValue("HighReliability");
        ssnObservationQuality_.setSsnHasMeasurementProperty(ssnHasMeasurementProperty_);
        memUsed.setSsnObservationQuality(ssnObservationQuality_);

        SsnObservationResult_ ssnObservationResult_ = new SsnObservationResult_();
        ssnObservationResult_.setType("memoryMetric");
        SsnHasValue_ ssnHasValue_ = new SsnHasValue_();
        ssnHasValue_.setType("ssn:ObservationValue");
        ssnHasValue_.setValue(cpuUsage+"");
        ssnHasValue_.setQudtUnit("qudt:Percentage");
        ssnObservationResult_.setSsnHasValue(ssnHasValue_);
        memUsed.setSsnObservationResult(ssnObservationResult_);

        try {
            out = JsonUtils.serializeJson(memUsed);
        } catch (IOException e) {
            this.logger.error("cpuUsage - Deserialize JSON UTILS IO EXCEPTION");
            throw new Exception("cpuUsage - Deserialize JSON UTILS IO EXCEPTION");
            //e.printStackTrace();
        }

        return out;
    }


    private String getDiskAvailable() throws Exception {

        String out = "";

        ServiceList.TaskManager tm = this.hiReplySvc.getSnapshot().getTaskManager();

        String strDiskAvailable = tm.getFreeDiskSpace();

        int bkSlashIndex = strDiskAvailable.indexOf("\\");
        int freeDiskSpace = Integer.parseInt (strDiskAvailable.substring(bkSlashIndex+2).replaceAll("\\s+",""));

        Date date = new Date();
        SimpleDateFormat printedDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

        PerformanceMetric memUsed = new PerformanceMetric();

        memUsed.setContext("http://vital.iot.org/measurement.jsonld");
        memUsed.setUri(this.transfProt+this.symbolicUri + "/iot/hireply/perf/diskAvailable");
        memUsed.setType("ssn:Observation");

        SsnObservationProperty_ ssnObservationProperty_ = new SsnObservationProperty_();
        ssnObservationProperty_.setType(this.transfProt+this.ontBaseUri+"diskAvailable");
        memUsed.setSsnObservationProperty(ssnObservationProperty_);

        SsnObservationResultTime_ ssnObservationResultTime_ = new SsnObservationResultTime_();

        ssnObservationResultTime_.setInXSDDateTime(printedDateFormat.format(date));
        memUsed.setSsnObservationResultTime(ssnObservationResultTime_);

        SsnObservationQuality_ ssnObservationQuality_ = new SsnObservationQuality_();
        SsnHasMeasurementProperty_ ssnHasMeasurementProperty_ = new SsnHasMeasurementProperty_();
        ssnHasMeasurementProperty_.setType("Reliability");
        ssnHasMeasurementProperty_.setHasValue("HighReliability");
        ssnObservationQuality_.setSsnHasMeasurementProperty(ssnHasMeasurementProperty_);
        memUsed.setSsnObservationQuality(ssnObservationQuality_);

        SsnObservationResult_ ssnObservationResult_ = new SsnObservationResult_();
        ssnObservationResult_.setType("memoryMetric");
        SsnHasValue_ ssnHasValue_ = new SsnHasValue_();
        ssnHasValue_.setType("ssn:ObservationValue");
        ssnHasValue_.setValue(freeDiskSpace+"");
        ssnHasValue_.setQudtUnit("qudt:Byte");
        ssnObservationResult_.setSsnHasValue(ssnHasValue_);
        memUsed.setSsnObservationResult(ssnObservationResult_);

        try {
            out = JsonUtils.serializeJson(memUsed);
        } catch (IOException e) {
            this.logger.error("diskAvailable - Deserialize JSON UTILS IO EXCEPTION");
            throw new Exception("diskAvailable - Deserialize JSON UTILS IO EXCEPTION");
            //e.printStackTrace();
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

    private List<HistoryMeasure> getHistoryMeasures(ValueList valueList) throws Exception {

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
                this.logger.error("ERROR PARSING DATE FROM HISTORY VALUE");
                throw new Exception("ERROR PARSING DATE FROM HISTORY VALUE");
                //e.printStackTrace();
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
        sensor.setType("vital:VitalSensor");
        sensor.setDescription(currentSensor.getDescription());
        sensor.setId(this.transfProt + this.symbolicUri + "sensor/"+id);

        int status = currentSensor.getStatus();

        if (status==1) {
            sensor.setStatus("vital:Running");
        } else if (status==0) {
            sensor.setStatus("vital:unavailable");
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
        List<SsnObserf_> observedProperties = new ArrayList<>();

        if (dirCount == 1) {
            //speed e color
            SsnObserf_ speed = new SsnObserf_();
            speed.setType(this.transfProt+this.ontBaseUri+this.speedProp);
            speed.setId(this.transfProt + this.symbolicUri + "ico/" + id + "/" + this.speedProp);
            SsnObserf_ color = new SsnObserf_();
            color.setType(this.transfProt+this.ontBaseUri+this.colorProp);
            color.setId(this.transfProt + this.symbolicUri + "ico/" + id + "/" + this.colorProp);
            observedProperties.add(speed);
            observedProperties.add(color);
        }

        if (dirCount == 2) {
            //speed e color + reverse
            SsnObserf_ speed = new SsnObserf_();
            speed.setType(this.transfProt+this.ontBaseUri+this.speedProp);
            speed.setId(this.transfProt + this.symbolicUri + "ico/" + id + "/" + this.speedProp);
            SsnObserf_ color = new SsnObserf_();
            color.setType(this.transfProt+this.ontBaseUri+this.colorProp);
            color.setId(this.transfProt + this.symbolicUri + "ico/" + id + "/" +colorProp);
            observedProperties.add(speed);
            observedProperties.add(color);
            SsnObserf_ revspeed = new SsnObserf_();
            revspeed.setType(this.transfProt+this.ontBaseUri+this.reverseSpeedProp);
            revspeed.setId(this.transfProt + this.symbolicUri + "ico/" + id + "/" + this.reverseSpeedProp);
            SsnObserf_ revcolor = new SsnObserf_();
            revcolor.setType(this.transfProt+this.ontBaseUri+this.reverseColorProp);
            revcolor.setId(this.transfProt + this.symbolicUri + "ico/" + id + "/" + this.reverseColorProp);
            observedProperties.add(revspeed);
            observedProperties.add(revcolor);
        }

        sensor.setSsnObserves(observedProperties);

        return sensor;
    }

    private Sensor createMonitoringSensor() {
        Sensor sensor = new Sensor();

        String id = "monitoring";

        sensor.setContext("http://vital-iot.org/contexts/sensor.jsonld");
        sensor.setName(id);
        sensor.setType("vital:MonitoringSensor");
        sensor.setDescription("HiReply Monitoring Sensor");
        sensor.setId(this.transfProt + this.symbolicUri + "sensor/"+id);

        sensor.setStatus("vital:Running");

        List<SsnObserf_> observedProperties = new ArrayList<>();

        SsnObserf_ observedProperty = new SsnObserf_();
        observedProperty.setType(this.transfProt+this.ontBaseUri+"memUsed");
        observedProperty.setId(this.transfProt + this.symbolicUri + "ico/" + id + "/" +"memUsed");
        observedProperties.add(observedProperty);

        observedProperty = new SsnObserf_();
        observedProperty.setType(this.transfProt+this.ontBaseUri+"memAvailable");
        observedProperty.setId(this.transfProt + this.symbolicUri + "ico/" + id + "/" + "memAvailable");
        observedProperties.add(observedProperty);

        observedProperty = new SsnObserf_();
        observedProperty.setType(this.transfProt+this.ontBaseUri+"diskAvailable");
        observedProperty.setId(this.transfProt + this.symbolicUri + "ico/" + id + "/" + "diskAvailable");
        observedProperties.add(observedProperty);

        observedProperty = new SsnObserf_();
        observedProperty.setType(this.transfProt+this.ontBaseUri+"cpuUsage");
        observedProperty.setId(this.transfProt + this.symbolicUri + "ico/" + id + "/" + "cpuUsage");
        observedProperties.add(observedProperty);

        observedProperty = new SsnObserf_();
        observedProperty.setType(this.transfProt+this.ontBaseUri+"servedRequest");
        observedProperty.setId(this.transfProt+this.symbolicUri+"ico/"+id+"/"+"servedRequest");
        observedProperties.add(observedProperty);

        observedProperty = new SsnObserf_();
        observedProperty.setType(this.transfProt+this.ontBaseUri+"errors");
        observedProperty.setId(this.transfProt + this.symbolicUri + "ico/" + id + "/"+"errors");
        observedProperties.add(observedProperty);

        observedProperty = new SsnObserf_();
        observedProperty.setType(this.transfProt+this.ontBaseUri+"upTime");
        observedProperty.setId(this.transfProt + this.symbolicUri + "ico/" + id + "/"+"upTime");
        observedProperties.add(observedProperty);

        observedProperty = new SsnObserf_();
        observedProperty.setType(this.transfProt+this.ontBaseUri+"pendingRequests");
        observedProperty.setId(this.transfProt + this.symbolicUri + "ico/" + id + "/" + "pendingRequests");
        observedProperties.add(observedProperty);

        sensor.setSsnObserves(observedProperties);

        return sensor;
    }

    private Measure createMeasureFromSensor(ServiceList.TrafficSensor currentSensor, String property) throws Exception {
        Measure m = new Measure();

        SimpleDateFormat timestampDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat printedDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

        String hiReplyTimestamp = this.hiReplySvc.getPropertyAttribute(currentSensor.getID(), property, "Timestamp");
        Date timestamp = null;
        try {
            timestamp = timestampDateFormat.parse(hiReplyTimestamp);
        } catch (ParseException e) {
            this.logger.error("HiPPI - createMeasureFromSensor - ERROR PARSING DATE FROM HIREPLY TIMESTAMP");
            throw new Exception("HiPPI - createMeasureFromSensor - ERROR PARSING DATE FROM HIREPLY TIMESTAMP");
            //e.printStackTrace();
        }

        m.setContext("http://vital-iot.org/contexts/measurement.jsonld");
        m.setUri(this.transfProt+this.symbolicUri+"ico/" + currentSensor.getID() + "/observation");
        m.setType("ssn:Observation");

        SsnObservationProperty ssnObservationProperty = new SsnObservationProperty();
        ssnObservationProperty.setType(this.transfProt+this.ontBaseUri+property);

        m.setSsnObservationProperty(ssnObservationProperty);

        SsnObservationResultTime ssnObservationResultTime = new SsnObservationResultTime();
        ssnObservationResultTime.setInXSDDateTime(printedDateFormat.format(timestamp));

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
            if (property.equals(this.speedProp)) {
                speedValue = currentSensor.getSpeed();
                ssnHasValue.setValue(""+speedValue);
                ssnHasValue.setQudtUnit("qudt:KilometerPerHour");
            } else if (property.equals(this.colorProp)) {
                colorValue = currentSensor.getColor();
                ssnHasValue.setValue(""+colorValue);
                ssnHasValue.setQudtUnit("qudt:Color");
            } else {
                return null;
            }
        }

        if (currentSensor.getDirectionCount() == 2) {
            if (property.equals(this.speedProp)) {
                speedValue = currentSensor.getSpeed();
                ssnHasValue.setValue(""+speedValue);
                ssnHasValue.setQudtUnit("qudt:KilometerPerHour");
            } else if (property.equals(this.colorProp)) {
                colorValue = currentSensor.getColor();;
                ssnHasValue.setValue(""+colorValue);
                ssnHasValue.setQudtUnit("qudt:Color");
            } else if (property.equals(this.reverseSpeedProp)) {
                speedValue = currentSensor.getSpeed();
                ssnHasValue.setValue(""+speedValue);
                ssnHasValue.setQudtUnit("qudt:KilometerPerHour");
            } else if (property.equals(this.reverseColorProp)) {
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

        SimpleDateFormat printedDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

        m.setContext("http://vital-iot.org/contexts/measurement.jsonld");
        m.setUri(this.transfProt+this.symbolicUri+"ico/" + currentSensor.getID() + "/observation");
        m.setType("ssn:Observation");

        SsnObservationProperty ssnObservationProperty = new SsnObservationProperty();
        ssnObservationProperty.setType(this.transfProt+this.ontBaseUri+property);

        m.setSsnObservationProperty(ssnObservationProperty);

        SsnObservationResultTime ssnObservationResultTime = new SsnObservationResultTime();
        ssnObservationResultTime.setInXSDDateTime(printedDateFormat.format(historyMeasure.getDate()));

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
            if (property.equals(this.speedProp)) {
                speedValue = historyMeasure.getValue();
                ssnHasValue.setValue(""+speedValue);
                ssnHasValue.setQudtUnit("qudt:KilometerPerHour");
            } else if (property.equals(this.colorProp)) {
                colorValue = Math.round(historyMeasure.getValue());
                ssnHasValue.setValue(""+colorValue);
                ssnHasValue.setQudtUnit("qudt:Color");
            } else {
                return null;
            }
        }

        if (currentSensor.getDirectionCount() == 2) {
            if (property.equals(this.speedProp)) {
                speedValue = historyMeasure.getValue();
                ssnHasValue.setValue(""+speedValue);
                ssnHasValue.setQudtUnit("qudt:KilometerPerHour");
            } else if (property.equals(this.colorProp)) {
                colorValue = Math.round(historyMeasure.getValue());
                ssnHasValue.setValue(""+colorValue);
                ssnHasValue.setQudtUnit("qudt:Color");
            } else if (property.equals(this.reverseSpeedProp)) {
                speedValue = historyMeasure.getValue();
                ssnHasValue.setValue(""+speedValue);
                ssnHasValue.setQudtUnit("qudt:KilometerPerHour");
            } else if (property.equals(this.reverseColorProp)) {
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

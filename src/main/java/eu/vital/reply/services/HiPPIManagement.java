package eu.vital.reply.services;

import eu.vital.reply.clients.HiReplySvc;
import eu.vital.reply.jsonpojos.*;
import eu.vital.reply.utils.ConfigReader;
import eu.vital.reply.utils.JsonUtils;
import eu.vital.reply.xmlpojos.ServiceList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by f.deceglia on 17/03/2015.
 */
@Path("")
public class HiPPIManagement {

    private Logger logger;
    private HiReplySvc hiReplySvc;
    private ConfigReader configReader;

    private String hostPort;
    private String hostName;
    private String symbolicUri;
    private String ontBaseUri;

    private String transfProt;



    public HiPPIManagement() {
        configReader = ConfigReader.getInstance();

        hiReplySvc = new HiReplySvc();
        logger = LogManager.getLogger(HiPPIManagement.class);

        hostName = configReader.get(ConfigReader.SERVER_HOSTNAME);
        hostPort = configReader.get(ConfigReader.SERVER_PORT);
        symbolicUri = configReader.get(ConfigReader.SYMBOLIC_URI);
        ontBaseUri = configReader.get(ConfigReader.ONT_BASE_URI_PROPERTY);

        transfProt = configReader.get(ConfigReader.TRANSF_PROTOCOL);
    }

    @Path("/iot/hireply/perf/memUsed")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getMemoryUsed() {

        String out = "";

        ServiceList.TaskManager tm = this.hiReplySvc.getSnapshot().getTaskManager();

        BigInteger memoryUsed = tm.getMemoryConsumption();
        Date date = new Date();
        SimpleDateFormat printedDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");


        MemUsed memUsed = new MemUsed();

        memUsed.setContext("");
        memUsed.setUri("");
        memUsed.setType("");

        SsnObservationProperty_ ssnObservationProperty_ = new SsnObservationProperty_();
        ssnObservationProperty_.setType(this.transfProt+this.ontBaseUri+"MemUsage");
        memUsed.setSsnObservationProperty(ssnObservationProperty_);

        SsnObservationResultTime_ ssnObservationResultTime_ = new SsnObservationResultTime_();

        // todo: update formato data vedi getobeservationvalue

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
        ssnHasValue_.setQudtUnit("byte");
        ssnObservationResult_.setSsnHasValue(ssnHasValue_);
        memUsed.setSsnObservationResult(ssnObservationResult_);

        try {
            out = JsonUtils.serializeJson(memUsed);
        } catch (IOException e) {
            this.logger.error("getIcoMetadata - Deserialize JSON UTILS IO EXCEPTION");
            e.printStackTrace();
        }

        return out;
    }




}

package eu.vital.reply.clients;

import eu.vital.reply.ConfigReader;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by a.martelli on 09/10/2014.
 */

/**
 * TODO: Mapping ServiceId con prefisso della macchina o no?
 * Es.: San11633-TrS_1 --> TrS_1 ??
 */
public class HiReplySvc
{
    private HttpClient http;
    private ConfigReader config;
    private Logger logger;

    private String host;
    private int port;
    private String getSnapshotPath;
    private String getPropNamesPath;
    private String getPropValuePath;
    private String setPropValuePath;
    private String getPropAttrPath;
    private String getPropHistValuesPath;
    private String isServiceRunningPath;

    public enum Operators
    {
        AND,
        OR
    }

    public HiReplySvc()
    {
        config = ConfigReader.getInstance();
        http   = HttpClients.createDefault();
        logger = LogManager.getLogger(HiReplySvc.class);

        host                  = config.get(ConfigReader.HI_HOSTNAME);
        port                  = Integer.parseInt(config.get(ConfigReader.HI_PORT));
        getSnapshotPath       = config.get(ConfigReader.HI_GETSNAPSHOT_PATH);
        getPropNamesPath      = config.get(ConfigReader.HI_GETPROPERTYNAMES_PATH);
        getPropValuePath      = config.get(ConfigReader.HI_GETPROPERTYVALUE_PATH);
        setPropValuePath      = config.get(ConfigReader.HI_SETPROPERTYVALUE_PATH);
        getPropAttrPath       = config.get(ConfigReader.HI_GETPROPERTYATTRIBUTE_PATH);
        getPropHistValuesPath = config.get(ConfigReader.HI_GETPROPERTYHISTORICALVALUES_PATH);
        isServiceRunningPath  = config.get(ConfigReader.HI_ISSERVICERUNNING_PATH);
    }

    public String getSnapshotFiltered(String filter) throws URISyntaxException, IOException
    {
        String respString;
        URI uri = new URIBuilder()
                .setScheme("http")
                .setHost(host)
                .setPort(port)
                .setPath(getSnapshotPath)
                .build();

        if(filter != null && !filter.isEmpty())
        {
            uri = new URIBuilder(uri)
                    .addParameter("filter", filter)
                    .build();
        }

        HttpGet get = new HttpGet(uri);

        HttpResponse resp = http.execute(get);
        respString = EntityUtils.toString(resp.getEntity());

        // TODO: XML: ServiceList -> known services [0..*]
        return cleanOutput(respString);
    }

    public String getSnapshot() throws IOException, URISyntaxException
    {
        return getSnapshotFiltered(null);
    }

    public String getPropertyNames(String serviceId) throws URISyntaxException, IOException
    {
        String respString;
        URI uri = new URIBuilder()
                .setScheme("http")
                .setHost(host)
                .setPort(port)
                .setPath(serviceId + "/" + getPropNamesPath)
                .build();

        HttpGet get = new HttpGet(uri);

        HttpResponse resp = http.execute(get);
        respString = EntityUtils.toString(resp.getEntity());

        // TODO: XML: PropertyList -> Property [0..*]
        return cleanOutput(respString);
    }

    public String getPropertyValue(String serviceId, String propertyName)
            throws URISyntaxException, IOException
    {
        String respString;
        URI uri = new URIBuilder()
                .setScheme("http")
                .setHost(host)
                .setPort(port)
                .setPath(serviceId + "/" + getPropValuePath)
                .addParameter("prop", propertyName)
                .build();

        HttpGet get = new HttpGet(uri);

        HttpResponse resp = http.execute(get);
        respString = EntityUtils.toString(resp.getEntity());

        return cleanOutput(respString);
    }

    public String setPropertyValue(String serviceId, String propertyName, String value)
            throws URISyntaxException, IOException
    {
        String respString;
        URI uri = new URIBuilder()
                .setScheme("http")
                .setHost(host)
                .setPort(port)
                .setPath(serviceId + "/" + setPropValuePath)
                .addParameter("prop", propertyName)
                .addParameter("value", value)
                .build();

        HttpGet get = new HttpGet(uri);

        HttpResponse resp = http.execute(get);
        respString = EntityUtils.toString(resp.getEntity());

        // TODO: XML: manage response string OK/Error
        return cleanOutput(respString);
    }

    public String getPropertyAttribute(String serviceId, String propertyName, String attributeName)
            throws URISyntaxException, IOException
    {
        String respString;
        URI uri = new URIBuilder()
                .setScheme("http")
                .setHost(host)
                .setPort(port)
                .setPath(serviceId + "/" + getPropAttrPath)
                .addParameter("prop", propertyName)
                .addParameter("attribute", attributeName)
                .build();

        HttpGet get = new HttpGet(uri);

        HttpResponse resp = http.execute(get);
        respString = EntityUtils.toString(resp.getEntity());

        return cleanOutput(respString);
    }

    public String getPropertyHistoricalValues(String serviceId, String propertyName, Date startTime, Date endTime)
            throws URISyntaxException, IOException
    {
        String respString;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        URI uri = new URIBuilder()
                .setScheme("http")
                .setHost(host)
                .setPort(port)
                .setPath(serviceId + "/" + getPropHistValuesPath)
                .addParameter("prop", propertyName)
                .addParameter("starttime", df.format(startTime))
                .addParameter("endtime", df.format(endTime))
                .build();

        HttpGet get = new HttpGet(uri);

        HttpResponse resp = http.execute(get);
        respString = EntityUtils.toString(resp.getEntity());

        // TODO: XML: ValueList -> Value [0..*]
        return cleanOutput(respString);
    }

    public String isServiceRunning(String serviceId) throws URISyntaxException, IOException
    {
        String respString;
        URI uri = new URIBuilder()
                .setScheme("http")
                .setHost(host)
                .setPort(port)
                .setPath(serviceId + "/" + isServiceRunningPath)
                .build();

        HttpGet get = new HttpGet(uri);

        HttpResponse resp = http.execute(get);
        respString = EntityUtils.toString(resp.getEntity());

        // TODO: XML: returns timestamp of start time. turn to boolean?
        return cleanOutput(respString);
    }

    public String createFilter(String key, String value)
    {
        return "[" + key + "]==" + value;
    }

    public String concatFilters(ArrayList<String> filters, Operators operator)
    {
        if(filters != null && !filters.isEmpty())
        {
            String res = filters.get(0);
            if(filters.size() > 1)
            {
                for(int i = 1; i < filters.size(); i++)
                {
                    res += "_" + operator.toString() + "_" + filters.get(i);
                }
            }
            return res;
        }
        return "";
    }

    private String cleanOutput(String msXml)
    {
        String tagBeg = "<string xmlns=\"http://schemas.microsoft.com/2003/10/Serialization/\">";
        String tagEnd = "</string>";
        int start = msXml.indexOf(tagBeg) + tagBeg.length();
        int stop  = msXml.indexOf(tagEnd);

        return //"<?xml version=\"1.0\"?>\n" +
                StringEscapeUtils.unescapeXml(msXml.substring(start, stop));
    }
}

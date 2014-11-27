package eu.vital.reply.clients;

import eu.vital.reply.utils.ConfigReader;
import eu.vital.reply.utils.UnmarshalUtil;
import eu.vital.reply.xmlpojos.PropertyList;
import eu.vital.reply.xmlpojos.ServiceList;
import eu.vital.reply.xmlpojos.ValueList;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by a.martelli on 09/10/2014.
 */

/**
 * TODO: Mapping ServiceId con prefisso della macchina o no?
 * Es.: San11633-TrS_1 --> TrS_1 ?? --> TrS per rendere generico ogni metodo. riconosco dal prefisso il tipo di sensore
 * TrS: Traffic Sensor
 * Thermometer: Temperature Sensor
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
        http = HttpClients.createDefault();
        logger = LogManager.getLogger(HiReplySvc.class);

        host = config.get(ConfigReader.HI_HOSTNAME);
        port = Integer.parseInt(config.get(ConfigReader.HI_PORT));
        getSnapshotPath = config.get(ConfigReader.HI_GETSNAPSHOT_PATH);
        getPropNamesPath = config.get(ConfigReader.HI_GETPROPERTYNAMES_PATH);
        getPropValuePath = config.get(ConfigReader.HI_GETPROPERTYVALUE_PATH);
        setPropValuePath = config.get(ConfigReader.HI_SETPROPERTYVALUE_PATH);
        getPropAttrPath = config.get(ConfigReader.HI_GETPROPERTYATTRIBUTE_PATH);
        getPropHistValuesPath = config.get(ConfigReader.HI_GETPROPERTYHISTORICALVALUES_PATH);
        isServiceRunningPath = config.get(ConfigReader.HI_ISSERVICERUNNING_PATH);
    }

    public ServiceList getSnapshotFiltered(String filter)
    {
        String respString = "";
        URI uri = null;
        try
        {
            uri = new URIBuilder()
                    .setScheme("http")
                    .setHost(host)
                    .setPort(port)
                    .setPath(getSnapshotPath)
                    .build();
        } catch (URISyntaxException e)
        {
            this.logger.error("getSnapshot - URI syntax exception");
            return null;
        }

        if (filter != null && !filter.isEmpty())
        {
            try
            {
                uri = new URIBuilder(uri)
                        .addParameter("filter", filter)
                        .build();
            } catch (URISyntaxException e)
            {
                this.logger.error("getSnapshot - URI syntax exception");
                return null;
            }
        }

        HttpGet get = new HttpGet(uri);

        HttpResponse resp = null;
        try
        {
            resp = http.execute(get);
            respString = this.cleanOutput(EntityUtils.toString(resp.getEntity()));
        } catch (IOException e)
        {
            this.logger.error("getSnapshot - HTTP IO exception");
            return null;
        }

        ServiceList serviceList = null;

        try
        {
            serviceList = (ServiceList) UnmarshalUtil.getInstance().unmarshal(respString);
        } catch(Exception e)
        {
            this.logger.error("getSnapshot - Unmarshalling exception: " + e.getMessage());
            return null;
        }

        return serviceList;
    }

    public ServiceList getSnapshot()
    {
        return getSnapshotFiltered(null);
    }

    public PropertyList getPropertyNames(String serviceId)
    {
        String respString = "";
        URI uri = null;
        try
        {
            uri = new URIBuilder()
                    .setScheme("http")
                    .setHost(host)
                    .setPort(port)
                    .setPath("/" + serviceId + getPropNamesPath)
                    .build();
        } catch (URISyntaxException e)
        {
            this.logger.error("getPropertyNames - Uri builder");
            return null;
        }

        HttpGet get = new HttpGet(uri);

        HttpResponse resp = null;
        try
        {
            resp = http.execute(get);
            respString = EntityUtils.toString(resp.getEntity());
        } catch (IOException e)
        {
            this.logger.error("getPropertyNames - HTTP get IO Exception");
            return null;
        }

        PropertyList props = null;
        try
        {
            props = (PropertyList) UnmarshalUtil.getInstance().unmarshal(respString);
        } catch(Exception e)
        {
            this.logger.error("getPropertyNames - Unmarshalling exception: " + e.getMessage());
            return null;
        }

        return props;
    }

    public String getPropertyValue(String serviceId, String propertyName)
    {
        String respString = "";
        URI uri = null;
        try
        {
            uri = new URIBuilder()
                    .setScheme("http")
                    .setHost(host)
                    .setPort(port)
                    .setPath("/" + serviceId + getPropValuePath)
                    .addParameter("prop", propertyName)
                    .build();
        } catch (URISyntaxException e)
        {
            this.logger.error("getPropertyValue - Uri builder");
            return null;
        }

        HttpGet get = new HttpGet(uri);

        HttpResponse resp = null;
        try
        {
            resp = http.execute(get);
            respString = EntityUtils.toString(resp.getEntity());
        } catch (IOException e)
        {
            this.logger.error("getPropertyValue - HTTP get execute IO Exception");
            return null;
        }

        return cleanOutput(respString);
    }

    public boolean setPropertyValue(String serviceId, String propertyName, String value)
    {
        String respString = "";
        URI uri = null;
        try
        {
            uri = new URIBuilder()
                    .setScheme("http")
                    .setHost(host)
                    .setPort(port)
                    .setPath("/" + serviceId + setPropValuePath)
                    .addParameter("prop", propertyName)
                    .addParameter("raw", value)
                    .build();
        } catch (URISyntaxException e)
        {
            this.logger.error("setPropertyValue - Uri builder");
            return false;
        }

        HttpGet get = new HttpGet(uri);

        HttpResponse resp = null;
        try
        {
            resp = http.execute(get);
            respString = this.cleanOutput(EntityUtils.toString(resp.getEntity()));
        } catch (IOException e)
        {
            this.logger.error("setPropertyValue - HTTP Get IO Exception");
            return false;
        }

        return respString.contains("correctly");
    }

    public String getPropertyAttribute(String serviceId, String propertyName, String attributeName)
    {
        String respString = "";
        URI uri = null;
        try
        {
            uri = new URIBuilder()
                    .setScheme("http")
                    .setHost(host)
                    .setPort(port)
                    .setPath("/" + serviceId + getPropAttrPath)
                    .addParameter("prop", propertyName)
                    .addParameter("attribute", attributeName)
                    .build();
        } catch (URISyntaxException e)
        {
            this.logger.error("setPropertyAttribute - Uri builder");
            return null;
        }

        HttpGet get = new HttpGet(uri);

        HttpResponse resp = null;
        try
        {
            resp = http.execute(get);
            respString = EntityUtils.toString(resp.getEntity());
        } catch (IOException e)
        {
            this.logger.error("setPropertyAttribute - HTTP Get IO Exception");
            return null;
        }

        return cleanOutput(respString);
    }

    public ValueList getPropertyHistoricalValues(String serviceId, String propertyName, Date startTime, Date endTime)
    {
        String respString = "";
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        String startDate = df.format(startTime);
        String endDate = df.format(endTime);

        URI uri = null;
        try
        {
            uri = new URIBuilder()
                    .setScheme("http")
                    .setHost(host)
                    .setPort(port)
                    .setPath("/" + serviceId + getPropHistValuesPath)
                    .addParameter("prop", propertyName)
                    .addParameter("starttime", startDate)
                    .addParameter("endtime", endDate)
                    .build();
        } catch (URISyntaxException e) {
            this.logger.error("getPropertyHistoricalValues - Uri builder");
            return null;
        }

        HttpGet get = new HttpGet(uri);

        HttpResponse resp;
        try
        {
            resp = http.execute(get);
            respString = EntityUtils.toString(resp.getEntity());
        } catch (IOException e)
        {
            this.logger.error("getPropertyHistoricalValues - HTTP Get IO Exception");
            return null;
        }

        ValueList values;

        respString = cleanOutput(respString);
        try
        {
            values = (ValueList) UnmarshalUtil.getInstance().unmarshal(respString);
        } catch (Exception e)
        {
            this.logger.error("getPropertyHistoricalValues - Unmarshalling exception: " + e.getMessage());
            return null;
        }

        return values;
    }

    public String isServiceRunning(String serviceId)
    {
        String respString = "";
        URI uri = null;
        try
        {
            uri = new URIBuilder()
                    .setScheme("http")
                    .setHost(host)
                    .setPort(port)
                    .setPath("/" + serviceId + isServiceRunningPath)
                    .build();
        } catch (URISyntaxException e)
        {
            this.logger.error("isServiceRunning - Uri builder");
            e.printStackTrace();
        }

        HttpGet get = new HttpGet(uri);

        HttpResponse resp = null;
        try
        {
            resp = http.execute(get);
        } catch (IOException e)
        {
            this.logger.error("isServiceRunning - HTTP Get Exception");
            e.printStackTrace();
        }
        try
        {
            respString = EntityUtils.toString(resp.getEntity());
        } catch (IOException e)
        {
            this.logger.error("isServiceRunning - HTTP Response Exception");
            e.printStackTrace();
        }

        // TODO: XML: returns timestamp of start time. turn to boolean?
        return cleanOutput(respString);
    }

    public String createFilter(String key, String value)
    {
        return "[" + key + "]==" + value;
    }

    public String concatFilters(ArrayList<String> filters, Operators operator)
    {
        if (filters != null && !filters.isEmpty())
        {
            String res = filters.get(0);
            if (filters.size() > 1)
            {
                for (int i = 1; i < filters.size(); i++)
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
        int stop = msXml.indexOf(tagEnd);

        return //"<?xml version=\"1.0\"?>\n" +
                StringEscapeUtils.unescapeXml(msXml.substring(start, stop));
    }
}

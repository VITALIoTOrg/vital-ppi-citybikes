package eu.vital.reply.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by a.martelli on 09/10/2014.
 */
public class ConfigReader
{
    private static ConfigReader instance;

    private Properties config;
    private Logger logger;

    public static final String SYMBOLIC_URI                        = "SYMBOLIC_URI";
    public static final String SERVER_HOSTNAME                     = "SERVER_HOSTNAME";
    public static final String SERVER_PORT                         = "SERVER_PORT";
    public static final String HI_HOSTNAME                         = "HI_HOSTNAME";
    public static final String HI_PORT                             = "HI_PORT";
    public static final String HI_GETSNAPSHOT_PATH                 = "HI_GETSNAPSHOT_PATH";
    public static final String HI_GETPROPERTYNAMES_PATH            = "HI_GETPROPERTYNAMES_PATH";
    public static final String HI_GETPROPERTYVALUE_PATH            = "HI_GETPROPERTYVALUE_PATH";
    public static final String HI_SETPROPERTYVALUE_PATH            = "HI_SETPROPERTYVALUE_PATH";
    public static final String HI_GETPROPERTYATTRIBUTE_PATH        = "HI_GETPROPERTYATTRIBUTE_PATH";
    public static final String HI_GETPROPERTYHISTORICALVALUES_PATH = "HI_GETPROPERTYHISTORICALVALUES_PATH";
    public static final String HI_ISSERVICERUNNING_PATH            = "HI_ISSERVICERUNNING_PATH";
    public static final String ONT_BASE_URI_PROPERTY               = "ONT_BASE_URI_PROPERTY";
    public static final String TRANSF_PROTOCOL                     = "TRANSF_PROTOCOL";
    public static final String SPEED_PROP                          = "SPEED_PROP";
    public static final String COLOR_PROP                          = "COLOR_PROP";
    public static final String REVERSE_SPEED_PROP                  = "REVERSE_SPEED_PROP";
    public static final String REVERSE_COLOR_PROP                  = "REVERSE_COLOR_PROP";
    public static final String LOG_VERBOSITY                       = "LOG_VERBOSITY";


    private ConfigReader()
    {
        logger = LogManager.getLogger(ConfigReader.class);
        config = new Properties();
        InputStream is =this.getClass().getResourceAsStream("/config.properties");
        try
        {
            config.load(is);
        }
        catch (IOException e)
        {
            this.logger.error("ConfigReader - IO EXCEPTION");
            e.printStackTrace();
        }
    }

    public static ConfigReader getInstance()
    {
        if(instance == null)
            instance = new ConfigReader();
        return instance;
    }

    public String get(String key)
    {
        return config.getProperty(key);
    }
}

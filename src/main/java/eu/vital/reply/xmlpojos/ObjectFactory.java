//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.10.22 at 03:34:30 PM CEST 
//


package eu.vital.reply.xmlpojos;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the eu.vital.reply.xmlpojos package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: eu.vital.reply.xmlpojos
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ServiceList }
     * 
     */
    public ServiceList createServiceList() {
        return new ServiceList();
    }

    /**
     * Create an instance of {@link ServiceList.Thermometer }
     * 
     */
    public ServiceList.Thermometer createServiceListThermometer() {
        return new ServiceList.Thermometer();
    }

    /**
     * Create an instance of {@link ServiceList.IoTSystem }
     * 
     */
    public ServiceList.IoTSystem createServiceListIoTSystem() {
        return new ServiceList.IoTSystem();
    }

    /**
     * Create an instance of {@link ServiceList.TrafficSensor }
     * 
     */
    public ServiceList.TrafficSensor createServiceListTrafficSensor() {
        return new ServiceList.TrafficSensor();
    }

    /**
     * Create an instance of {@link PropertyList }
     * 
     */
    public PropertyList createPropertyList() {
        return new PropertyList();
    }

}

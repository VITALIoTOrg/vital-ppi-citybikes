//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.10.17 at 05:17:07 PM CEST 
//


package eu.vital.reply.xmlpojos;

import org.apache.logging.log4j.LogManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Value" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "value"
})
@XmlRootElement(name = "ValueList")
public class ValueList {

    @XmlElement(name = "Value")
    protected List<String> value;

    /**
     * Gets the value of the value property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the value property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getValue().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    private List<String> getValue() {
        if (value == null) {
            value = new ArrayList<String>();
        }
        return this.value;
    }

    public class ValueItem
    {
        public String value;
        public Date timestamp;

        public ValueItem(String value, Date timestamp)
        {
            this.value = value;
            this.timestamp = timestamp;
        }
    }

    public List<ValueItem> getValueList()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH.mm.ss");
        List<ValueItem> items = new ArrayList<ValueItem>();

        for(String s : this.value)
        {
            String fields[] = s.split(",");
            Date ts;
            try
            {
                ts = sdf.parse(fields[1]);
            }
            catch (ParseException e)
            {
                LogManager.getLogger(ValueList.class).error("Error in parsing Value timestamp from XML");
                ts = new Date();
            }
            items.add(new ValueItem(fields[0], ts));
        }
        return items;
    }
}
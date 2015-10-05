package eu.vital.reply.utils;

import org.xml.sax.*;
import org.xml.sax.helpers.XMLFilterImpl;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.UnmarshallerHandler;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.StringReader;

/**
 * Created by a.martelli on 13/10/2014.
 */

public class UnmarshalUtil
{
    private static UnmarshalUtil instance;
    private XMLFilter xmlFilter;
    private UnmarshallerHandler unmarshallerHandler;

    private UnmarshalUtil()
    {
        try
        {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();
            XMLReader xmlReader = sp.getXMLReader();
            this.xmlFilter = new IoTXMLFilter(xmlReader);

            JAXBContext context = JAXBContext.newInstance("eu.vital.reply.xmlpojos");
            Unmarshaller um = context.createUnmarshaller();
            this.unmarshallerHandler = um.getUnmarshallerHandler();
            this.xmlFilter.setContentHandler(this.unmarshallerHandler);
        }
        catch (ParserConfigurationException | SAXException | JAXBException e)
        {
            e.printStackTrace();
        }
    }

    public static UnmarshalUtil getInstance()
    {
        if(instance == null)
            instance = new UnmarshalUtil();
        return instance;
    }

    public Object unmarshal(String xml) throws IOException, SAXException, JAXBException
    {
        StringReader sr = new StringReader(xml);
        this.xmlFilter.parse(new InputSource(sr));
        return unmarshallerHandler.getResult();
    }

    /**
     * Inner class: custom filter to strip all attributes with namespace
     */
    private static class IoTXMLFilter extends XMLFilterImpl
    {

        public IoTXMLFilter(XMLReader xmlReader) {
            super(xmlReader);
        }

        @Override
        public void startElement(String uri, String localName, String qName,
                                 Attributes attributes) throws SAXException
        {
            super.startElement(uri, localName, qName, attributes);
        }

        @Override
        public void endElement(String uri, String localName, String qName)
                throws SAXException
        {
            super.endElement(uri, localName, qName);
        }

    }
}

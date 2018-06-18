package uk.gov.dvla.osg.rpd.xml;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import uk.gov.dvla.osg.rpd.error.BadResponseModelXml;

public class xmlUtils {

    public BadResponseModelXml getXmlError(String data) throws JsonParseException, JsonMappingException, IOException {
        return new XmlMapper().readValue(data, BadResponseModelXml.class);
    }
}

package uk.gov.dvla.osg.rpd.xml;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import uk.gov.dvla.osg.rpd.error.RpdErrorResponse;

public class xmlUtils {

    /**
     * Deserialises the RPD error resonse when it is returned in xml format.
     *
     * @param data the XML string containing the error data
     * @return the xml error
     * @throws JsonParseException the json parse exception
     * @throws JsonMappingException the json mapping exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public RpdErrorResponse getXmlError(String data) throws JsonParseException, JsonMappingException, IOException {
        return new XmlMapper().readValue(data, RpdErrorResponse.class);
    }
}

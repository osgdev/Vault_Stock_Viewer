package uk.gov.dvla.osg.vault.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PropertyLoader {
    
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Properties properties = new Properties();
    
    public PropertyLoader(String filename) throws IOException {
        try (FileInputStream input = new FileInputStream(new File(filename))) {
            properties.load(input);
        }
    }
    
    public String getProperty(String key) throws RuntimeException {
        if (properties.containsKey(key)) {
            return properties.getProperty(key).toUpperCase();
        }
        throw new RuntimeException("Unable to load property ["+key+"] from file.");
    }
    
    public int getPropertyInt(String key) throws RuntimeException {
        if (properties.containsKey(key)) {
            String value = properties.getProperty(key);
            if (StringUtils.isNumeric(value)) {
                return Integer.parseInt(value);
            }
            LOGGER.fatal("Value [{}] is not valid for the property [{}].", value, key);
            throw new RuntimeException("Value ["+value+"] is not valid for the property ["+key+"].");
        }
        LOGGER.fatal("Unable to load property [{}] from Production Configuration file.", key);
        throw new RuntimeException("Unable to load property ["+key+"] from Production Configuration file.");
    }
}

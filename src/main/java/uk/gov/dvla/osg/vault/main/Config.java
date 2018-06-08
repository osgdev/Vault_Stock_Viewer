package uk.gov.dvla.osg.vault.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Config {
    
    private static final Logger LOGGER = LogManager.getLogger();
    
    /******************************************************************************************
    *              SINGLETON PATTERN
    ******************************************************************************************/
   private static String filename;

   private static class SingletonHelper {
       private static final Config INSTANCE = new Config();
   }

   public static Config getInstance() {
       if (StringUtils.isBlank(filename)) {
           throw new RuntimeException("Application Configuration not initialised before use");
       }
       return SingletonHelper.INSTANCE;
   }

   public static void init(String file) throws RuntimeException {
       if (StringUtils.isBlank(filename)) {
           if (new File(file).isFile()) {
               filename = file;
           } else {
               throw new RuntimeException("Application Configuration File " + filename + " does not exist on filepath.");
           }
       } else {
           throw new RuntimeException("Application Configuration has already been initialised");
       }
   }
   /*****************************************************************************************/
    
    // absolute filepath for the vault_stock.config file

    private String protocol = "";
    private String host = "";
    private String port = "";
    private String vaultUrl = "";
    private String loginUrl = "";
    private String logoutUrl = "";
  
    private Config() {
        Properties properties = new Properties();
        try {
            InputStream input = new FileInputStream(filename);
            properties.load(input);
        } catch (IOException ex) {
            LOGGER.fatal("Unable to load Application Configuration from file - [{}] {}", filename, ex.getMessage());
            System.exit(1);
        }
        
        protocol = properties.getProperty("protocol");
        host = properties.getProperty("host");
        port = properties.getProperty("port");
        loginUrl = properties.getProperty("loginUrl");
        logoutUrl = properties.getProperty("logoutUrl");
        vaultUrl = properties.getProperty("vaultUrl");
    }
    
    public String getProtocol() {
        return protocol;
    }

    public String getHost() {
        return host;
    }
    
    public String getPort() {
        return port;
    }
    
    public String getvaultUrl() {
        return vaultUrl;
    }
    
    public String getLoginUrl() {
        return loginUrl;
    }
    
    public String getLogoutUrl() {
        return logoutUrl;
    }
}

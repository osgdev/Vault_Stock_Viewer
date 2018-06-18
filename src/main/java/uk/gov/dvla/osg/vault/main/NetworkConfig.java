package uk.gov.dvla.osg.vault.main;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class NetworkConfig {
    
    private static final Logger LOGGER = LogManager.getLogger();
    
    /******************************************************************************************
    *              SINGLETON PATTERN
    ******************************************************************************************/
   private static String filename;

   private static class SingletonHelper {
       private static final NetworkConfig INSTANCE = new NetworkConfig();
   }

   public static NetworkConfig getInstance() {
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
  
    private NetworkConfig() {
        
        PropertyLoader loader = null;
        try {
            loader = new PropertyLoader(filename);
        } catch (IOException ex) {
            LOGGER.fatal("Unable to load properties from {}", filename);
            System.exit(1);
        }
        
        try {
            protocol = loader.getProperty("protocol");
            host = loader.getProperty("host");
            port = loader.getProperty("port");
            loginUrl = loader.getProperty("loginUrl");
            logoutUrl = loader.getProperty("logoutUrl");
            vaultUrl = loader.getProperty("vaultUrl");
        } catch (RuntimeException ex) {
            LOGGER.fatal(ex.getMessage());
            System.exit(1);
        }

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

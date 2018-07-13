package uk.gov.dvla.osg.vault.main;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * The Class NetworkConfig holds the RPD Rest API URL's.
 * It is loaded from a network configuration properties file which is stored
 * in the local file system. 
 */
public class NetworkConfig {
    
    private static final Logger LOGGER = LogManager.getLogger();
    
    /******************************************************************************************
    *              SINGLETON PATTERN
    ******************************************************************************************/
   private static String filename;

   private static class SingletonHelper {
       private static final NetworkConfig INSTANCE = new NetworkConfig();
   }


   /**
    * Gets the single instance of NetworkConfig.
    *
    * @return single instance of NetworkConfig
    * @throws RuntimeException if the method is called before initialising with the network configuration file
    */
   public static NetworkConfig getInstance() throws RuntimeException {
       if (StringUtils.isBlank(filename)) {
           throw new RuntimeException("Application Configuration not initialised before use");
       }
       return SingletonHelper.INSTANCE;
   }

   /**
    * Initialises the NetworkConfig with the network configuration file.
    *
    * @param file the network configuration file
    * @throws RuntimeException if the configuration file does not exist or if NetworkConfig has already been initialised.
    */
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

    private String vaultUrl = "";
    private String loginUrl = "";
    private String logoutUrl = "";
  
    /**
     * Instantiates a new network config from the fields in the property file.
     */
    private NetworkConfig() {
        
        // PropertyLoader loads the properties from the configuration file and validates each entry
        PropertyLoader loader = null;
        try {
            loader = new PropertyLoader(filename);
        } catch (IOException ex) {
            LOGGER.fatal("Unable to load properties from {}", filename);
            System.exit(1);
        }
        
        try {
            String protocol = loader.getProperty("protocol");
            String host = loader.getProperty("host");
            String port = loader.getProperty("port");
            String urlBase = protocol + host + ":" + port;
            loginUrl = urlBase + loader.getProperty("loginUrl");
            logoutUrl = urlBase + loader.getProperty("logoutUrl");
            vaultUrl = urlBase + loader.getProperty("vaultUrl");
        } catch (RuntimeException ex) {
            // Property value is missing from the file
            LOGGER.fatal(ex.getMessage());
            System.exit(1);
        }

    }

    
    /**
     * Gets the RPD vault url.
     *
     * @return the vault url
     */
    public String getvaultUrl() {
        return vaultUrl;
    }
    
    /**
     * Gets the login url.
     *
     * @return the login url
     */
    public String getLoginUrl() {
        return loginUrl;
    }
    
    /**
     * Gets the logout url.
     *
     * @return the logout url
     */
    public String getLogoutUrl() {
        return logoutUrl;
    }
}

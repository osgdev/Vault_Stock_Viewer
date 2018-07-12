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

    private String vaultUrl = "";
    private String loginUrl = "";
    private String logoutUrl = "";
  
    /**
     * Instantiates a new network config from the fields in the property file.
     */
    private NetworkConfig() {
        
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

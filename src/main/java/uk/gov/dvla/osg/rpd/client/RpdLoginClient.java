package uk.gov.dvla.osg.rpd.client;

import java.util.Optional;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import uk.gov.dvla.osg.rpd.error.RpdErrorResponse;
import uk.gov.dvla.osg.rpd.json.JsonUtils;
import uk.gov.dvla.osg.vault.main.NetworkConfig;


/**
 * Sends login request to the RPD webservice. Token is obtained from
 * response to authenticate user when submitting files.
 */
public class RpdLoginClient {
	
	static final Logger LOGGER = LogManager.getLogger();
	
	private RpdErrorResponse errorMsg = new RpdErrorResponse();
    private final String url;
    
    /**
     * Gets a new instance of the RpdLoginClient
     * @param config
     * @return a new instance of the RpdLoginClient.
     */
    public static RpdLoginClient getInstance(NetworkConfig config) {
        return new RpdLoginClient(config);
    }
    
    private RpdLoginClient(NetworkConfig config) {
        this.url = config.getProtocol() + config.getHost() + ":" + config.getPort() + config.getLoginUrl();
    }
    
    /**
     * Contacts RPD and attempts to retrieve a session token using the supplied credentials.
     * 
     * @param userName the RPD login name of the user.
     * @param password the RPD password for the user.
     * @return a session token if the credentials are valid, for all other conditions an empty optional.
     */
    public Optional<String> getSessionToken(String userName, String password) {
        try {
        	Response response = RpdRestClient.rpdLogin(url, userName, password);
        	String data = response.readEntity(String.class); 
            if (response.getStatus() == 200) {
                String token = JsonUtils.getTokenFromJson(data);
            	return Optional.of(token);
            } else {
                // If RPD url is incorrect or RPD is not available an HTML response is returned
                MediaType mediaType = response.getMediaType();
                if (mediaType.equals(MediaType.APPLICATION_JSON_TYPE)) {
                    // RPD provides clear error information, and so is mapped to model
                    errorMsg = JsonUtils.getError(data);
                } else {
                    // Handle in the outer catch block.
                    LOGGER.warn("Response is not JSON!");
                    throw new IllegalArgumentException();
                }
            }
        } catch (ProcessingException ex) {
            errorMsg.setCode("Login Error:");
			errorMsg.setMessage("Unable to connect to RPD web service. Connection timed out");
			errorMsg.setAction("Please wait a few minutes and then try again.");
			errorMsg.setException(ex);
        } catch (NullPointerException ex) {
            errorMsg.setCode("Login Error:");
            errorMsg.setMessage("Unable to connect to RPD web service. Invalid IP address for RPD");
            errorMsg.setAction("To resolve, check all parts of the login URL in the application config file.");
            errorMsg.setException(ex);
        } catch(IllegalArgumentException ex) {
            errorMsg.setCode("Login Error:");
            errorMsg.setMessage("Invalid URL in config file [" + url + "]. Please check configuration.");
            errorMsg.setAction("To resolve, check all parts of the login URL in the application config file. This problem is usually caused by either a missing value in the URL or an illegal character.");
            errorMsg.setException(ex);
        } catch (Exception ex) {
            errorMsg.setCode("Login Error:");
            errorMsg.setMessage("An unknown error occured while attempting to login to RPD");
            errorMsg.setAction("Please notify Dev Team.");
            errorMsg.setException(ex);
        }
        return Optional.empty();
    }

    /**
     * Retrieves the error response if an empty optional was returned from the getSessionToken method.
     * @return an error response object.
     */
    public RpdErrorResponse getErrorResponse() {
        return errorMsg;
    }
    
}

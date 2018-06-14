package uk.gov.dvla.osg.rpd.client;

import java.util.Optional;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import uk.gov.dvla.osg.rpd.error.BadResponseModel;
import uk.gov.dvla.osg.rpd.json.JsonUtils;
import uk.gov.dvla.osg.vault.main.NetworkConfig;


/**
 * Sends login request to the RPD webservice. Token is obtained from
 * response to authenticate user when submitting files.
 */
public class RpdLoginClient {
	
	static final Logger LOGGER = LogManager.getLogger();
	
	private BadResponseModel brm = null;
    private final String url;
    
    public RpdLoginClient(NetworkConfig config) {
        this.url = config.getProtocol() + config.getHost() + ":" + config.getPort() + config.getLoginUrl();
    }
    
    public Optional<String> getSessionToken(String userName, String password) {
        try {
        	Response response = RestClient.rpdLogin(url, userName, password);
        	String data = response.readEntity(String.class); 
            if (response.getStatus() == 200) {
                String token = JsonUtils.getTokenFromJson(data);
            	return Optional.of(token);
            } else {
            	// RPD provides clear error information, and so is mapped to model
                brm = JsonUtils.getError(data);
            }
        } catch (ProcessingException ex) {
			String errorMessage = "Unable to connect to RPD web service. Connection timed out";
			String errorAction = "Please wait a few minutes and then try again. If the problem persits, please contact Dev team.";
			brm = new BadResponseModel(errorMessage, errorAction);
        } catch (NullPointerException ex) {
            String errorMessage = "Unable to connect to RPD web service. Invalid IP address for RPD";
            String errorAction = "To resolve, check all parts of the login URL in the application config file.";
            brm = new BadResponseModel(errorMessage, errorAction);
        } catch(IllegalArgumentException ex) {
            String errorMessage = "Invalid URL in config file [" + url + "]. Please check configuration.";
            String errorAction = "To resolve, check all parts of the login URL in the application config file. "
                    +"This problem is usually caused by either a missing value in the URL or an illegal character.";
            brm = new BadResponseModel(errorMessage, errorAction);
        } catch (Exception ex) {
            String errorMessage = "Login error: " + ex.getClass().getSimpleName() + " " + ex.getMessage();
            String errorAction = ExceptionUtils.getStackTrace(ex);
            brm = new BadResponseModel(errorMessage, errorAction);
        }
        return Optional.empty();
    }

    public BadResponseModel getErrorResponse() {
        return brm;
    }
    
}

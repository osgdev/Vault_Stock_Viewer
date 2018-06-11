package uk.gov.dvla.osg.vault.network;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.GsonBuilder;

import uk.gov.dvla.osg.vault.main.NetworkConfig;
import uk.gov.dvla.osg.vault.utils.JsonUtils;


/**
 * Sends login request to the RPD webservice. Token is obtained from
 * response to authenticate user when submitting files.
 */
public class RpdLogIn {
	
	static final Logger LOGGER = LogManager.getLogger();
	
	private BadResponseModel brm = null;
    private final String url;
    
    public RpdLogIn(NetworkConfig config) {
        this.url = config.getProtocol() + config.getHost() + ":" + config.getPort() + config.getLoginUrl();
    }
    
    public String getSessionToken(String userName, String password) {
        try {
        	Response response = RestClient.rpdLogin(url, userName, password);
        	LOGGER.debug(response.toString());
        	String data = response.readEntity(String.class); 
            if (response.getStatus() == 200) {
                String token = JsonUtils.getTokenFromJson(data);
                LOGGER.debug("TOKEN: {}", token);
            	return token;
            } else {
            	// RPD provides clear error information, and so is mapped to model
                brm = new GsonBuilder().create().fromJson(data, BadResponseModel.class);
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
        return "";
    }

    public BadResponseModel getErrorResponse() {
        return brm;
    }
    
}

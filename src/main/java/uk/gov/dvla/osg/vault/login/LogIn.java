package uk.gov.dvla.osg.vault.login;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.GsonBuilder;

import uk.gov.dvla.osg.vault.main.Config;
import uk.gov.dvla.osg.vault.network.LoginBadResponseModel;
import uk.gov.dvla.osg.vault.network.RestClient;
import uk.gov.dvla.osg.vault.utils.JsonUtils;
import uk.gov.dvla.osg.vault.viewer.Session;


/**
 * Sends login request to the RPD webservice. Token is obtained from
 * response to authenticate user when submitting files.
 */
public class LogIn {
	
	static final Logger LOGGER = LogManager.getLogger();
	
    private String errorMessage = "";
    private String errorAction = "";
    private String errorCode = "";
    private final String url;
    
    public LogIn() {
        Config config = Config.getInstance();
        this.url = config.getProtocol() + config.getHost() + ":" + config.getPort() + config.getLoginUrl();
    }
    
    public void login() {
        try {
        	Response response = RestClient.rpdLogin(url);
        	LOGGER.debug(response.toString());
        	String data = response.readEntity(String.class); 
            if (response.getStatus() == 200) {
            	Session.getInstance().setToken(JsonUtils.getTokenFromJson(data));
            } else {
            	// RPD provides clear error information, and so is mapped to model
                LoginBadResponseModel br = new GsonBuilder().create().fromJson(data, LoginBadResponseModel.class);
                errorMessage = br.getMessage();
                errorAction = br.getAction();
                errorCode = br.getCode();
            }
        } catch (ProcessingException e) {
			errorMessage = "Unable to connect to RPD web service.";
            errorAction = "If the problem persits, please contact Dev team.";
            errorCode = "connection timed out";
        } catch (NullPointerException e) {
        	errorMessage = "Unable to connect to RPD web service.";
            errorAction = "If the problem persits, please contact Dev team.";
            errorCode = "Invalid IP address for RPD";
        } catch(IllegalArgumentException ex) {
            errorMessage = "Invalid URL in config file [" + url + "]. Please check configuration.";
            errorAction = "If the problem persits, please contact Dev team.";
        } catch (Exception e) {
			errorMessage = e.getMessage();
            errorAction = "If the problem persits, please contact Dev team.";
            errorCode = "Login error: " + e.getClass().getSimpleName();
        }
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getErrorAction() {
        return errorAction;
    }

    public String getErrorCode() {
        return errorCode;
    }
}

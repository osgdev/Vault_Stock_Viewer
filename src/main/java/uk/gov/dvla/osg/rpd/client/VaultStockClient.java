package uk.gov.dvla.osg.rpd.client;

import java.util.Optional;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import uk.gov.dvla.osg.rpd.error.BadResponseModelXml;
import uk.gov.dvla.osg.rpd.json.JsonUtils;
import uk.gov.dvla.osg.rpd.xml.xmlUtils;
import uk.gov.dvla.osg.vault.data.VaultStock;
import uk.gov.dvla.osg.vault.main.NetworkConfig;


/**
 * Sends login request to the RPD webservice. Token is obtained from
 * response to authenticate user when submitting files.
 */
public class VaultStockClient {
    
    static final Logger LOGGER = LogManager.getLogger();
    
    private BadResponseModelXml brm = null;
    private final String url;
    
    public VaultStockClient(NetworkConfig config) {
        this.url = config.getProtocol() + config.getHost() + ":" + config.getPort() + config.getvaultUrl();
    }
    
    public Optional<VaultStock> getStock(String token) {
        try {
            Response response = RestClient.vaultStock(url, token);
            LOGGER.debug(response.toString());
            String data = response.readEntity(String.class); 
            LOGGER.debug(data);
            if (response.getStatus() == 200) {
                return Optional.ofNullable(JsonUtils.loadStockFile(data));
            } else {
                // RPD provides clear error information, and so is mapped to model
                brm = new xmlUtils().getXmlError(data);
                //brm = new GsonBuilder().create().fromJson(data, BadResponseModel.class);
            }
        } catch (ProcessingException ex) {
            String errorMessage = "Unable to connect to RPD web service. Connection timed out";
            String errorAction = "Please wait a few minutes and then try again. If the problem persits, please contact Dev team.";
            brm = new BadResponseModelXml(errorMessage, errorAction);
        } catch (NullPointerException ex) {
            String errorMessage = "Unable to connect to RPD web service. Invalid IP address for RPD";
            String errorAction = "To resolve, check all parts of the login URL in the application config file.";
            brm = new BadResponseModelXml(errorMessage, errorAction);
        } catch(IllegalArgumentException ex) {
            String errorMessage = "Invalid URL in config file [" + url + "]. Please check configuration.";
            String errorAction = "To resolve, check all parts of the login URL in the application config file. "
                    +"This problem is usually caused by either a missing value in the URL or an illegal character.";
            brm = new BadResponseModelXml(errorMessage, errorAction);
        } catch (Exception ex) {
            String errorMessage = "Login error: " + ex.getClass().getSimpleName() + " " + ex.getMessage();
            String errorAction = ExceptionUtils.getStackTrace(ex);
            brm = new BadResponseModelXml(errorMessage, errorAction);
        }
        return Optional.empty();
    }

    public BadResponseModelXml getErrorResponse() {
        return brm;
    }
    
}

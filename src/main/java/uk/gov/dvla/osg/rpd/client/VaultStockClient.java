package uk.gov.dvla.osg.rpd.client;

import java.util.Optional;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import uk.gov.dvla.osg.rpd.error.RpdErrorResponse;
import uk.gov.dvla.osg.rpd.json.JsonUtils;
import uk.gov.dvla.osg.rpd.xml.xmlUtils;
import uk.gov.dvla.osg.vault.data.VaultStock;
import uk.gov.dvla.osg.vault.main.NetworkConfig;

/**
 * Sends login request to the RPD webservice. Token is obtained from response to
 * authenticate user when submitting files.
 */
public class VaultStockClient {

    static final Logger LOGGER = LogManager.getLogger();

    private RpdErrorResponse errorMessage = new RpdErrorResponse();
    private final String url;

    public VaultStockClient(NetworkConfig config) {
        this.url = config.getProtocol() + config.getHost() + ":" + config.getPort() + config.getvaultUrl();
    }

    public Optional<VaultStock> getStock(String token) {
        try {
            Response response = RpdRestClient.vaultStock(url, token);
            String data = response.readEntity(String.class);
            if (response.getStatus() == 200) {
                LOGGER.trace(data);
                return Optional.ofNullable(JsonUtils.loadStockFile(data));
            } else {
                // If RPD an RPD error response is recieved it will be retrieved as XML from the Vault
                MediaType mediaType = response.getMediaType();
                if (mediaType.equals(MediaType.TEXT_HTML_TYPE)) {
                    // RPD provides clear error information, and so is mapped to model
                    errorMessage = new xmlUtils().getXmlError(data);
                } else {
                    // Handle in the outer catch block.
                    throw new IllegalArgumentException();
                }

            }
        } catch (ProcessingException ex) {
            errorMessage.setCode("Vault Connection Error:");
            errorMessage.setMessage("Unable to connect to RPD web service. Connection timed out");
            errorMessage.setAction("Please wait a few minutes and then try again. If the problem persits, please contact Dev team.");
        } catch (NullPointerException ex) {
            errorMessage.setCode("Vault Connection Error:");
            errorMessage.setMessage("Unable to connect to RPD web service. Invalid IP address for RPD");
            errorMessage.setAction("To resolve, check all parts of the login URL in the application config file.");
        } catch (IllegalArgumentException ex) {
            errorMessage.setCode("Vault Connection Error:");
            errorMessage.setMessage("Invalid URL in config file [" + url + "]. Please check configuration.");
            errorMessage.setAction("To resolve, check all parts of the login URL in the application config file. " + "This problem is usually caused by either a missing value in the URL or an illegal character.");
        } catch (Exception ex) {
            errorMessage.setCode("Vault Connection Error:");
            errorMessage.setMessage("Unable to connect to vault.");
            errorMessage.setAction("Please contact Dev Team");
        }
        return Optional.empty();
    }

    public RpdErrorResponse getErrorResponse() {
        return errorMessage;
    }

}

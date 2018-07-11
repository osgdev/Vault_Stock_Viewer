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

    /**
     * Creates a new instance of VaultStockClient
     * @param config NetworkConfig object holding the vault url information.
     * @return a new instance of VaultStockClient
     */
    public static VaultStockClient getInstance(NetworkConfig config) {
        return new VaultStockClient(config);
    }
    
    private VaultStockClient(NetworkConfig config) {
        this.url = config.getProtocol() + config.getHost() + ":" + config.getPort() + config.getvaultUrl();
    }

    /**
     * Requests VaultStock data from RPD. If successful the response is converted to a VaultStock object, otherwise the 
     * error response is saved.
     * @param token the session token to authenticate with RPD.
     * @return VaultStock if session token is valid, an empty optional for error conditions.
     */
    public Optional<VaultStock> getStock(String token) {
        try {
            Response response = RpdRestClient.vaultStock(url, token);
            String data = response.readEntity(String.class);
            if (response.getStatus() == 200) {
                LOGGER.trace(data);
                return Optional.ofNullable(JsonUtils.loadStockFile(data));
            } else {
                MediaType mediaType = response.getMediaType();
                // If RPD has been contacted an RPD error response is recieved in XML format
                if (mediaType.equals(MediaType.APPLICATION_XML_TYPE)) {
                    errorMessage = new xmlUtils().getXmlError(data);
                } else {
                    // Unable to contact RPD, handle in the outer catch block.
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

    /**
     * Retrieves the error response if an empty optional was returned from the getStock method.
     * @return an error response object.
     */
    public RpdErrorResponse getErrorResponse() {
        return errorMessage;
    }

}

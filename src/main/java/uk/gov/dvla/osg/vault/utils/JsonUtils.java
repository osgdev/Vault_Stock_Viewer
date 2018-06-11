package uk.gov.dvla.osg.vault.utils;

import static uk.gov.dvla.osg.vault.utils.ErrorHandler.*;

import java.io.FileNotFoundException;
import java.io.FileReader;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import uk.gov.dvla.osg.vault.data.VaultStock;

/**
 * Utility methods to extract information from the JSON data responses that are
 * returned from the RPD REST api.
 */
public class JsonUtils {

	/**
	 * Extracts the user token from message body of a successful RPD login request
	 * 
	 * @param jsonString RPD login request message body
	 * @return session token, or blank string if token not available
	 */
	public static String getTokenFromJson(String jsonString) {
		try {
			return new JsonParser().parse(jsonString).getAsJsonObject().get("token").getAsString();
		} catch (JsonSyntaxException e) {
			ErrorMsg("getTokenFromJson", "String is not valid JSON.", e.getMessage());
		} catch (Exception e) {
			ErrorMsg("getTokenFromJson", e.getClass().getSimpleName(), e.getMessage());
		}
		return "";
	}
	
    /**
     * Deserializes the Json read from the supplied file.
     * @param jsonFile File retrieved from the Vault WebService.
     * @return Stock information from the Vault.
     * @throws Exception if there was a syntax error in the Json.
     */
    public static VaultStock loadStockFile(String jsonFile) throws Exception {
        try {
            return new Gson().fromJson(new FileReader(jsonFile), VaultStock.class);
        } catch (JsonSyntaxException | JsonIOException | FileNotFoundException ex) {
            LOGGER.fatal(ex.getMessage());
            throw ex;
        }
    }
}

package uk.gov.dvla.osg.rpd.json;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.management.ManagementFactory;

import com.google.gson.*;

import uk.gov.dvla.osg.rpd.error.BadResponseModel;
import uk.gov.dvla.osg.vault.data.VaultStock;

/**
 * Utility methods to extract information from the JSON data responses that are
 * returned from the RPD REST api.
 */
public class JsonUtils {
    private static final boolean DEBUG_MODE = ManagementFactory.getRuntimeMXBean().getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;

    /**
     * Extracts the user token from message body of a successful RPD login request
     * 
     * @param jsonString RPD login request message body
     * @return session token, or blank string if token not available
     */
    public static String getTokenFromJson(String jsonString) throws IllegalStateException, JsonSyntaxException {
        return new JsonParser().parse(jsonString).getAsJsonObject().get("token").getAsString();
    }

    /**
     * Deserializes the Json read from the supplied file.
     * 
     * @param jsonFile File retrieved from the Vault WebService.
     * @return Stock information from the Vault.
     */
    public static VaultStock loadStockFile(String jsonFile) throws JsonIOException, JsonSyntaxException, FileNotFoundException {
        Gson gsonBldr = new GsonBuilder().registerTypeAdapter(VaultStock.class, new EmptyStringAsNullTypeAdapter()).create();
        if (DEBUG_MODE) {
                return gsonBldr.fromJson(new FileReader(jsonFile), VaultStock.class);
        }
        return gsonBldr.fromJson(jsonFile, VaultStock.class);
    }
    
    public static BadResponseModel getError(String data) {
        return new Gson().fromJson(data, BadResponseModel.class);
    }
}
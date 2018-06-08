package uk.gov.dvla.osg.vault.utils;

import static uk.gov.dvla.osg.vault.utils.ErrorHandler.*;

import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

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
}

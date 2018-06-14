package uk.gov.dvla.osg.rpd.client;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import uk.gov.dvla.osg.vault.session.Session;

/**
 * Utility methods to transmit messages to the RPD REST service.
 * These are set by the RPD REST api and shouldn't be amended.
 */
public class RestClient {
	
	static final Logger LOGGER = LogManager.getLogger();
	
	/**
	 * Sends a login request to RPD using credentials in the Session object.
	 * @param url RPD login URL in format hostname:port address
	 * @return Response in JSON format, containing a session token for the currently logged in user
	 */
	public static Response rpdLogin(String url, String userName, String password) {
		//Note: unencrypted credentials is a requirement of the RPD RESTAPI
		return ClientBuilder.newClient()
							.target(url)
							.queryParam("name", userName)
							.queryParam("pwd", password)
							.request(MediaType.APPLICATION_JSON)
							.get();
	}
	
	   /**
     * Sends a login request to RPD using credentials in the Session object.
     * @param url RPD login URL in format hostname:port address
     * @return Response in JSON format, containing a session token for the currently logged in user
     */
    public static Response vaultStock(String url, String token) {
        return ClientBuilder.newClient()
                            .target(url)
                            .request(MediaType.APPLICATION_JSON)
                            .header("ippdcredential", "<credential token='" + token + "'/>")
                            .get();
    }
    
	/**
	 * Requests the RPD Group the currently logged in user belongs to. Requires
	 * a session token to already be set.
	 * @param url RPD RestApi URL in format hostname:port address
	 * @return Response in JSON format includes an array of groups to which the user belongs
	 */
	public static Response rpdGroup(String url, String token) {
		return ClientBuilder.newClient()
							.register(MultiPartFeature.class)
							.target(url)
							.queryParam("attribute", "User.Groups")
							.request(MediaType.APPLICATION_JSON)
							.header("token", token)
							.get();
	}

	/**
	 * Sends files to data input device in RPD. 
	 * @param url RPD RestApi URL in format hostname:port address
	 * @param multiPart Should contain the file(s) to transmit
	 * @return 202 status code if file was transmitted successfully
	 */
	public static Response rpdSubmit(String url, MultiPart multiPart, String token) {
		return ClientBuilder.newClient()
				.register(MultiPartFeature.class)
				.target(url)
				.request(MediaType.APPLICATION_JSON)
		        .header("ippdcredential", "<credential token='" + token + "'/>")
		        .post(Entity.entity(multiPart, multiPart.getMediaType()));
	}
	
	/**
	 * Log user out of RPD.
	 * @param url RPD logout URL in format hostname:port address
	 * @return Response in JSON format
	 */
	public static Response rpdLogOut(String url, String token) {
		return ClientBuilder.newClient().target(url)
				.path(Session.getInstance().getUserName())
				.request(MediaType.APPLICATION_JSON)
				.header("token", token)
				.post(null);
	}
}

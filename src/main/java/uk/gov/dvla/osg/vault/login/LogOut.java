package uk.gov.dvla.osg.vault.login;

import static uk.gov.dvla.osg.vault.utils.ErrorHandler.*;

import java.util.Optional;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;

import javafx.application.Platform;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import uk.gov.dvla.osg.vault.main.Config;
import uk.gov.dvla.osg.vault.mainform.ErrorHandler;
import uk.gov.dvla.osg.vault.network.RestClient;

public class LogOut {
    private static final boolean DEBUG_MODE = java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;
    
	/**
	 * Displays a confirmation dialog to the user to check they really do want to
	 * log off. If user chooses OK, the application closes regardless of the
	 * server's response.
	 */
	public static void logout() {
		// create dialog to confirm logout
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setTitle("Logout");
		// add logo to dialog window frame
		((Stage) dialog.getDialogPane().getScene().getWindow()).getIcons()
				.add(new Image(ErrorHandler.class.getResource("/Images/vault.png").toString()));
		DialogPane dialogPane = dialog.getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		dialogPane.setContentText("Do you really want to log out?");
		// display dialog and wait for a button to be clicked
		Optional<ButtonType> result = dialog.showAndWait();

		// logout if user clicks the OK button
		if (result.isPresent() && result.get() == ButtonType.OK) {

			if (!DEBUG_MODE) {
		        Config config = Config.getInstance();
		        String url = config.getProtocol() + config.getHost() + ":" + config.getPort() + config.getLogoutUrl();
    			try {
    				Response response = RestClient.rpdLogOut(url);
    				if (response.getStatus() != 200) {
    					ErrorMsg("Logout failed", "Unable to log user out of RPD web service.");
    				}
    			} catch (ProcessingException e) {
    				ErrorMsg("Connection timed out", "Unable to log user out of RPD web service.");
    			} catch (Exception e) {
    				ErrorMsg(e.getClass().getSimpleName(), e.getMessage());
    			}
			}
			// quits the application after error dialogs closed
			Platform.exit();
		}
	}
}

package uk.gov.dvla.osg.vault.login;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Optional;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import uk.gov.dvla.osg.rpd.client.RpdLoginClient;
import uk.gov.dvla.osg.rpd.error.RpdErrorResponse;
import uk.gov.dvla.osg.vault.error.ErrorHandler;
import uk.gov.dvla.osg.vault.main.NetworkConfig;
import uk.gov.dvla.osg.vault.mainform.MainFormController;
import uk.gov.dvla.osg.vault.session.Session;

/**
 * Form methods and authentication.
 */
public class LoginController {

	static final Logger LOGGER = LogManager.getLogger();
    
	@FXML
	private TextField nameField;
	@FXML
	public PasswordField passwordField;
	@FXML
	public Button btnLogin;
	@FXML
	public Label lblMessage;
	
	private Optional<String> token = Optional.empty();
	
	private final boolean DEBUG_MODE = ManagementFactory.getRuntimeMXBean().getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;
	
	/**
	 * Submits login request to RPD webservice. If token is retrieved then the user
	 * is authenticated, else the RPD error message is displayed.
	 */
	@FXML
	private void btnLoginClicked() {
	    Session session = Session.getInstance();
		session.setUserName(nameField.getText().trim());
		session.setPassword(passwordField.getText().trim());
		
			// disable buttons while RPD is contacted
			lblMessage.setText("Please wait...");
			btnLogin.setDisable(true);
			nameField.setDisable(true);
			passwordField.setDisable(true);

			final RpdLoginClient login = new RpdLoginClient(NetworkConfig.getInstance());
			// Login performed on background thread to prevent GUI freezing
			new Thread(() -> {
				LOGGER.trace("Attempting to login...");
				// bypass login while testing
				if (!DEBUG_MODE) {
					token = login.getSessionToken(session.getUserName(), session.getPassword());
				}
				// if token wasn't retrieved & not in debug mode, display error dialog
				if (!token.isPresent() && !DEBUG_MODE) {
					Platform.runLater(() -> {
					    RpdErrorResponse loginError = login.getErrorResponse();
					    LOGGER.error(loginError.toString());
						ErrorHandler.ErrorMsg(loginError.getCode(), loginError.getMessage(), loginError.getAction());
						// cleanup fields
						lblMessage.setText("");
						passwordField.setText("");
						nameField.setDisable(false);
						passwordField.setDisable(false);
						passwordField.requestFocus();
					});
				} else {
					LOGGER.trace("Login Complete.");
					// Add token to session data - blank if in debug mode
					if (token.isPresent()) {
					    Session.getInstance().setToken(token.get());					    
					}
					Platform.runLater(() -> {
						try {
							// close login page and load main view
							FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/MainForm.fxml"));
							Parent root = loader.load();
							Stage submitJobStage = new Stage();
							// Display logged in user in titlebar
							submitJobStage.setTitle("Vault Card Stock - User: " + session.getUserName());
							submitJobStage.getIcons()
									.add(new Image(getClass().getResourceAsStream("/Images/vault.png")));
							submitJobStage.setScene(new Scene(root));
							// force logout by routing the close request to the logout method
							submitJobStage.setOnCloseRequest(we -> {
								we.consume();
								((MainFormController)loader.getController()).logout();
							});
							submitJobStage.show();
							((Stage) btnLogin.getScene().getWindow()).close();
						} catch (IOException e) {
							Platform.runLater(() -> {
							    LOGGER.fatal(ExceptionUtils.getStackTrace(e));
							    ErrorHandler.ErrorMsg("We've no idea what just happened.", "","Best bet is to call Greg!");
							});
						}
					});
				}
			}).start();
		}

	/**
	 * Method responds to keypress events on the user and password fields. Login
	 * button is only enabled when both fields contain text.
	 */
	@FXML
	private void txtChanged() {
		if (nameField.getText().trim().equals("") || passwordField.getText().trim().equals("")) {
			btnLogin.setDisable(true);
		} else {
			btnLogin.setDisable(false);
		}
	}
}

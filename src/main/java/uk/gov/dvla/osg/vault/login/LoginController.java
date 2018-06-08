package uk.gov.dvla.osg.vault.login;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import uk.gov.dvla.osg.vault.mainform.ErrorHandler;
import uk.gov.dvla.osg.vault.mainform.MainFormController;
import uk.gov.dvla.osg.vault.viewer.Session;

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
	
	private final boolean DEBUG_MODE = java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;
	
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

			final LogIn login = new LogIn();

			// Login performed on background thread to prevent GUI freezing
			new Thread(() -> {
				LOGGER.trace("Attempting to login...");
				// bypass login while testing
				if (!DEBUG_MODE) {
					login.login();
				}
				// if token wasn't retrieved & not in debug mode, display error dialog
				if (StringUtils.isBlank(session.getToken()) && !DEBUG_MODE) {
					Platform.runLater(() -> {
						ErrorHandler.ErrorMsg(login.getErrorCode(), login.getErrorMessage(), login.getErrorAction());
						// cleanup fields
						lblMessage.setText("");
						passwordField.setText("");
						nameField.setDisable(false);
						passwordField.setDisable(false);
						passwordField.requestFocus();
					});
				} else {
					LOGGER.trace("Login Complete.");
					Platform.runLater(() -> {
						try {
							// close login page and load main view
							FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/MainForm.fxml"));
							Parent root = loader.load();
							Stage submitJobStage = new Stage();
							submitJobStage.setResizable(false);
							// Display logged in user in titlebar
							submitJobStage.setTitle("Vault Card Stock - " + session.getUserName());
							submitJobStage.getIcons()
									.add(new Image(getClass().getResourceAsStream("/Images/vault.png")));
							submitJobStage.setScene(new Scene(root));
							submitJobStage.show();
							// force logout by routing the close request to the logout method
							submitJobStage.setOnCloseRequest(we -> {
								we.consume();
								((MainFormController)loader.getController()).logout();
							});
							((Stage) btnLogin.getScene().getWindow()).close();
						} catch (IOException e) {
							Platform.runLater(() -> {
							    ErrorHandler.ErrorMsg(e.getClass().getSimpleName(), e.getMessage());
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

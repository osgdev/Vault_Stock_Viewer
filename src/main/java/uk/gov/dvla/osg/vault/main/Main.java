package uk.gov.dvla.osg.vault.main;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
    
    static final Logger LOGGER = LogManager.getLogger();
    
    @Override
    public void start(Stage primaryStage) {
        Parent loginScreen = null;
        try {
            loginScreen = FXMLLoader.load(getClass().getResource("/FXML/Login.fxml"));
        } catch (IOException ex) {
            LOGGER.error("Unable to load Login.fxml. Please check build path & re-build application.");
        }
        
        InputStream loginScreenLogo = getClass().getResourceAsStream("/Images/vault.png");
        
        if (loginScreenLogo == null) {
            LOGGER.error("Unable to load Login screen logo. Please check resource location & re-build application.");
            System.exit(999);
        }
        
        primaryStage.setTitle("Vault Stock");
        primaryStage.getIcons().add(new Image(loginScreenLogo));
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(loginScreen, 300, 200));
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        
        // Verify correct number of args
        if (args.length != 1) {
            LOGGER.fatal("Incorrect number of arguments supplied!");
            System.exit(1);
        }
        
        // Check config file path is correct
        String configFile = args[0];
        boolean propsFileExists = new File(configFile).exists();
        if (!propsFileExists) {
            LOGGER.fatal("Properties File '{}' doesn't exist", configFile);
            System.exit(1);
        }
        
        // Initialise the network configuration from the file
        NetworkConfig.init(configFile);
        
        // Launch the GUI
        launch(args);
    }

}

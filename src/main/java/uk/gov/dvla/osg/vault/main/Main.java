package uk.gov.dvla.osg.vault.main;

import java.io.File;

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
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/FXML/Login.fxml"));
        primaryStage.setTitle("Vault Stock");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/Images/vault.png")));
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root, 300, 200));
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        
        if (args.length != 1) {
            LOGGER.fatal("Incorrect number of arguments supplied!");
            System.exit(1);
        }
        String configFile = args[0];
        boolean propsFileExists = new File(configFile).exists();
        if (!propsFileExists) {
            LOGGER.fatal("Properties File '{}' doesn't exist", configFile);
            System.exit(1);
        }
        NetworkConfig.init(configFile);
        NetworkConfig.getInstance();
        launch(args);
    }

}

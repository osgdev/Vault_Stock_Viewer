package uk.gov.dvla.osg.vault.mainform;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import uk.gov.dvla.osg.vault.data.CardData;
import uk.gov.dvla.osg.vault.data.VaultStock;
import uk.gov.dvla.osg.vault.enums.CardClass;
import uk.gov.dvla.osg.vault.enums.Site;
import uk.gov.dvla.osg.vault.login.LogOut;
import uk.gov.dvla.osg.vault.viewer.JsonReader;

public class MainFormController {
    private static final Logger LOGGER = LogManager.getLogger();
    private final boolean DEBUG_MODE = java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;
    
    // TOP SECTION
    @FXML
    private ChoiceBox environmentChoice; 
    @FXML
    private ChoiceBox siteChoice;
    @FXML
    private ChoiceBox cardChoice;
    
    // TABLES - CARDS IN SCS
    @FXML
    private TableView<CardData> scs_mTachoTable;
    @FXML
    private TableColumn<CardData, String> scs_mTachoCol_Card;
    @FXML
    private TableColumn<CardData, String> scs_mTachoCol_Vol;
    
    @FXML
    private TableView<CardData> scs_mBrpTable;
    @FXML
    private TableColumn<CardData, String> scs_mBrpCol_Card;
    @FXML
    private TableColumn<CardData, String> scs_mBrpCol_Vol;
    
    @FXML
    private TableView<CardData> scs_mPolTable;
    @FXML
    private TableColumn<CardData, String> scs_mPolCol_Card;
    @FXML
    private TableColumn<CardData, String> scs_mPolCol_Vol;
    
    @FXML
    private TableView<CardData> scs_mDqcTable;
    @FXML
    private TableColumn<CardData, String> scs_mDqcCol_Card;
    @FXML
    private TableColumn<CardData, String> scs_mDqcCol_Vol;
    
    @FXML
    private TableView<CardData> scs_fTachoTable;
    @FXML
    private TableColumn<CardData, String> scs_fTachoCol_Card;
    @FXML
    private TableColumn<CardData, String> scs_fTachoCol_Vol;
    
    @FXML
    private TableView<CardData> scs_fBrpTable;
    @FXML
    private TableColumn<CardData, String> scs_fBrpCol_Card;
    @FXML
    private TableColumn<CardData, String> scs_fBrpCol_Vol;
    
    @FXML
    private TableView<CardData> scs_fPolTable;
    @FXML
    private TableColumn<CardData, String> scs_fPolCol_Card;
    @FXML
    private TableColumn<CardData, String> scs_fPolCol_Vol;
    
    @FXML
    private TableView<CardData> scs_fDqcTable;
    @FXML
    private TableColumn<CardData, String> scs_fDqcCol_Card;
    @FXML
    private TableColumn<CardData, String> scs_fDqcCol_Vol;
    
    // TABLES - CARDS OnCrate
    @FXML
    private TableView<CardData> onCrate_mTachoTable;
    @FXML
    private TableColumn<CardData, String> onCrate_mTachoCol_Card;
    @FXML
    private TableColumn<CardData, String> onCrate_mTachoCol_Vol;
    
    @FXML
    private TableView<CardData> onCrate_mBrpTable;
    @FXML
    private TableColumn<CardData, String> onCrate_mBrpCol_Card;
    @FXML
    private TableColumn<CardData, String> onCrate_mBrpCol_Vol;
    
    
    @FXML
    private TableView<CardData> onCrate_mPolTable;
    @FXML
    private TableColumn<CardData, String> onCrate_mPolCol_Card;
    @FXML
    private TableColumn<CardData, String> onCrate_mPolCol_Vol;
    
    @FXML
    private TableView<CardData> onCrate_mDqcTable;
    @FXML
    private TableColumn<CardData, String> onCrate_mDqcCol_Card;
    @FXML
    private TableColumn<CardData, String> onCrate_mDqcCol_Vol;
    
    @FXML
    private TableView<CardData> onCrate_fTachoTable;
    @FXML
    private TableColumn<CardData, String> onCrate_fTachoCol_Card;
    @FXML
    private TableColumn<CardData, String> onCrate_fTachoCol_Vol;
    
    @FXML
    private TableView<CardData> onCrate_fBrpTable;
    @FXML
    private TableColumn<CardData, String> onCrate_fBrpCol_Card;
    @FXML
    private TableColumn<CardData, String> onCrate_fBrpCol_Vol;
    
    @FXML
    private TableView<CardData> onCrate_fPolTable;
    @FXML
    private TableColumn<CardData, String> onCrate_fPolCol_Card;
    @FXML
    private TableColumn<CardData, String> onCrate_fPolCol_Vol;
    
    @FXML
    private TableView<CardData> onCrate_fDqcTable;
    @FXML
    private TableColumn<CardData, String> onCrate_fDqcCol_Card;
    @FXML
    private TableColumn<CardData, String> onCrate_fDqcCol_Vol;
    
    // TABLES - CARDS UCI
    @FXML
    private TableView<CardData> uci_mTachoTable;
    @FXML
    private TableColumn<CardData, String> uci_mTachoCol_Card;
    @FXML
    private TableColumn<CardData, String> uci_mTachoCol_uci;
    
    @FXML
    private TableView<CardData> uci_mBrpTable;
    @FXML
    private TableColumn<CardData, String> uci_mBrpCol_Card;
    @FXML
    private TableColumn<CardData, String> uci_mBrpCol_uci;
    
    @FXML
    private TableView<CardData> uci_mPolTable;
    @FXML
    private TableColumn<CardData, String> uci_mPolCol_Card;
    @FXML
    private TableColumn<CardData, String> uci_mPolCol_uci;
    
    @FXML
    private TableView<CardData> uci_mDqcTable;
    @FXML
    private TableColumn<CardData, String> uci_mDqcCol_Card;
    @FXML
    private TableColumn<CardData, String> uci_mDqcCol_uci;
    
    @FXML
    private TableView<CardData> uci_fTachoTable;
    @FXML
    private TableColumn<CardData, String> uci_fTachoCol_Card;
    @FXML
    private TableColumn<CardData, String> uci_fTachoCol_uci;
    
    @FXML
    private TableView<CardData> uci_fBrpTable;
    @FXML
    private TableColumn<CardData, String> uci_fBrpCol_Card;
    @FXML
    private TableColumn<CardData, String> uci_fBrpCol_uci;
    
    @FXML
    private TableView<CardData> uci_fPolTable;
    @FXML
    private TableColumn<CardData, String> uci_fPolCol_Card;
    @FXML
    private TableColumn<CardData, String> uci_fPolCol_uci;
    
    @FXML
    private TableView<CardData> uci_fDqcTable;
    @FXML
    private TableColumn<CardData, String> uci_fDqcCol_Card;
    @FXML
    private TableColumn<CardData, String> uci_fDqcCol_uci;
    
    private VaultStock vaultStock;
    private DataHandler dataHandler;
    
    
    @FXML
    private void initialize() {
        // Load Json File        
        loadJsonData();        
        loadChoiceBoxes();
        this.dataHandler = new DataHandler(vaultStock.getStockTotals().getTest());
        setCellValueFactories();
        setupTableData();
    }

    private void loadJsonData() {
        try {
            String file;
            if (DEBUG_MODE) { 
                file = "C:\\Users\\OSG\\Test Data\\live-vault-json.json";
            } else {
                file ="C:\\Users\\Broomhallp\\Desktop\\live-vault-json.json";
            }
            vaultStock = JsonReader.LoadStockFile(file);
        } catch (Exception ex) {
            // VaultStock wrtites msg to Log, display error msg dialog box to user
            LOGGER.fatal(ExceptionUtils.getStackTrace(ex));
        }
    }

    private void loadChoiceBoxes() {
        ObservableList<String> environmentList = FXCollections.observableArrayList("TEST", "PRODUCTION");
        ObservableList<String> siteList = FXCollections.observableArrayList("BOTH", "COMBINED");
        ObservableList<String> cardList = FXCollections.observableArrayList("ALL","TACHO", "BRP", "POL", "DQC");
        
        environmentChoice.setValue(environmentList.get(0));
        environmentChoice.setItems(environmentList);
        environmentChoice.getSelectionModel().selectedIndexProperty().addListener((ChangeListener) (observable, oldValue,  newValue) -> {
            environmentChoice.getSelectionModel().select((int)newValue);
            if (environmentChoice.getSelectionModel().getSelectedItem().equals("TEST")) {
                dataHandler = new DataHandler(vaultStock.getStockTotals().getTest());
            } else {
                dataHandler = new DataHandler(vaultStock.getStockTotals().getProduction());
            }
            setupTableData();
        });
   
        siteChoice.setValue(siteList.get(0));
        siteChoice.setItems(siteList);
        siteChoice.getSelectionModel().selectedIndexProperty().addListener((ChangeListener) (observable, oldValue,  newValue) -> {
            siteChoice.getSelectionModel().select((int)newValue);
        });
        
        cardChoice.setValue(cardList.get(0));
        cardChoice.setItems(cardList);
        cardChoice.getSelectionModel().selectedIndexProperty().addListener((ChangeListener) (observable, oldValue,  newValue) -> {
            cardChoice.getSelectionModel().select((int)newValue);
        });
    }

    private void setupTableData() {
        setDataSCS();
        setDataOnCrate();
        setTestDataUCI();
    }

    private void setCellValueFactories() {
        
        PropertyValueFactory<CardData, String> propValCardType = new PropertyValueFactory<>("cardType");
        PropertyValueFactory<CardData, String> propValCardVol = new PropertyValueFactory<>("volume");
        PropertyValueFactory<CardData, String> propValCardUci = new PropertyValueFactory<>("uci");
        // SCS - MORRISTON
        scs_mTachoCol_Card.setCellValueFactory(propValCardType);
        scs_mTachoCol_Vol.setCellValueFactory(propValCardVol);
        scs_mBrpCol_Card.setCellValueFactory(propValCardType);
        scs_mBrpCol_Vol.setCellValueFactory(propValCardVol);
        scs_mPolCol_Card.setCellValueFactory(propValCardType);
        scs_mPolCol_Vol.setCellValueFactory(propValCardVol);
        scs_mDqcCol_Card.setCellValueFactory(propValCardType);
        scs_mDqcCol_Vol.setCellValueFactory(propValCardVol);
        // SCS - FFORESTFACH
        scs_fTachoCol_Card.setCellValueFactory(propValCardType);
        scs_fTachoCol_Vol.setCellValueFactory(propValCardVol);
        scs_fBrpCol_Card.setCellValueFactory(propValCardType);
        scs_fBrpCol_Vol.setCellValueFactory(propValCardVol);
        scs_fPolCol_Card.setCellValueFactory(propValCardType);
        scs_fPolCol_Vol.setCellValueFactory(propValCardVol);
        scs_fDqcCol_Card.setCellValueFactory(propValCardType);
        scs_fDqcCol_Vol.setCellValueFactory(propValCardVol);
        // ON CRATE - MORRISTON
        onCrate_mTachoCol_Card.setCellValueFactory(propValCardType);
        onCrate_mTachoCol_Vol.setCellValueFactory(propValCardVol);
        onCrate_mBrpCol_Card.setCellValueFactory(propValCardType);
        onCrate_mBrpCol_Vol.setCellValueFactory(propValCardVol);
        onCrate_mPolCol_Card.setCellValueFactory(propValCardType);
        onCrate_mPolCol_Vol.setCellValueFactory(propValCardVol);
        onCrate_mDqcCol_Card.setCellValueFactory(propValCardType);
        onCrate_mDqcCol_Vol.setCellValueFactory(propValCardVol);
        // ON CRATE - FFORESTFACH
        onCrate_fTachoCol_Card.setCellValueFactory(propValCardType);
        onCrate_fTachoCol_Vol.setCellValueFactory(propValCardVol);
        onCrate_fBrpCol_Card.setCellValueFactory(propValCardType);
        onCrate_fBrpCol_Vol.setCellValueFactory(propValCardVol);
        onCrate_fPolCol_Card.setCellValueFactory(propValCardType);
        onCrate_fPolCol_Vol.setCellValueFactory(propValCardVol);
        onCrate_fDqcCol_Card.setCellValueFactory(propValCardType);
        onCrate_fDqcCol_Vol.setCellValueFactory(propValCardVol);
        // UCI - MORRISTON
        uci_mTachoCol_Card.setCellValueFactory(propValCardType);
        uci_mTachoCol_uci.setCellValueFactory(propValCardUci);
        uci_mBrpCol_Card.setCellValueFactory(propValCardType);
        uci_mBrpCol_uci.setCellValueFactory(propValCardUci);
        uci_mPolCol_Card.setCellValueFactory(propValCardType);
        uci_mPolCol_uci.setCellValueFactory(propValCardUci);
        uci_mDqcCol_Card.setCellValueFactory(propValCardType);
        uci_mDqcCol_uci.setCellValueFactory(propValCardUci);
        // UCI - FFORESTFACH
        uci_fTachoCol_Card.setCellValueFactory(propValCardType);
        uci_fTachoCol_uci.setCellValueFactory(propValCardUci);
        uci_fBrpCol_Card.setCellValueFactory(propValCardType);
        uci_fBrpCol_uci.setCellValueFactory(propValCardUci);
        uci_fPolCol_Card.setCellValueFactory(propValCardType);
        uci_fPolCol_uci.setCellValueFactory(propValCardUci);
        uci_fDqcCol_Card.setCellValueFactory(propValCardType);
        uci_fDqcCol_uci.setCellValueFactory(propValCardUci);
    }
    
    private void setDataSCS() {
        scs_mTachoTable.setItems(dataHandler.getScsData(CardClass.TACHO, Site.M));
        scs_mBrpTable.setItems(dataHandler.getScsData(CardClass.BID, Site.M));
        scs_mPolTable.setItems(dataHandler.getScsData(CardClass.POL, Site.M));
        scs_mDqcTable.setItems(dataHandler.getScsData(CardClass.DQC, Site.M));
        scs_fTachoTable.setItems(dataHandler.getScsData(CardClass.TACHO, Site.F));
        scs_fBrpTable.setItems(dataHandler.getScsData(CardClass.BID, Site.F));
        scs_fPolTable.setItems(dataHandler.getScsData(CardClass.POL, Site.F));
        scs_fDqcTable.setItems(dataHandler.getScsData(CardClass.DQC, Site.F));
    }
   
    private void setDataOnCrate() {
        onCrate_mTachoTable.setItems(dataHandler.getOnCrateData(CardClass.TACHO, Site.M));
        onCrate_mBrpTable.setItems(dataHandler.getOnCrateData(CardClass.BID, Site.M));
        onCrate_mPolTable.setItems(dataHandler.getOnCrateData(CardClass.POL, Site.M));
        onCrate_mDqcTable.setItems(dataHandler.getOnCrateData(CardClass.DQC, Site.M));
        onCrate_fTachoTable.setItems(dataHandler.getOnCrateData(CardClass.TACHO, Site.F));
        onCrate_fBrpTable.setItems(dataHandler.getOnCrateData(CardClass.BID, Site.F));
        onCrate_fPolTable.setItems(dataHandler.getOnCrateData(CardClass.POL, Site.F));
        onCrate_fDqcTable.setItems(dataHandler.getOnCrateData(CardClass.DQC, Site.F));
    }
    
    private void setTestDataUCI() {
        uci_mTachoTable.setItems(dataHandler.getUciData(CardClass.TACHO, Site.M));
        uci_mBrpTable.setItems(dataHandler.getUciData(CardClass.BID, Site.M));
        uci_mPolTable.setItems(dataHandler.getUciData(CardClass.POL, Site.M));
        uci_mDqcTable.setItems(dataHandler.getUciData(CardClass.DQC, Site.M));
        uci_fTachoTable.setItems(dataHandler.getUciData(CardClass.TACHO, Site.F));
        uci_fBrpTable.setItems(dataHandler.getUciData(CardClass.BID, Site.F));
        uci_fPolTable.setItems(dataHandler.getUciData(CardClass.POL, Site.F));
        uci_fDqcTable.setItems(dataHandler.getUciData(CardClass.DQC, Site.F));
    }
    
    /**
     * Files older than 7 days are deleted from the temp dir & then the user is
     * logged out and application shut down.
     */
    public void logout() {
        // contact RPD on background thread to prevent main window from freezing
            new Thread(() -> {
                Platform.runLater(() -> {
                    LogOut.logout();
                });
            }).start();
    }
}

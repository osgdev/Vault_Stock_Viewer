package uk.gov.dvla.osg.vault.mainform;

import static uk.gov.dvla.osg.vault.enums.CardClass.*;
import static uk.gov.dvla.osg.vault.enums.Site.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import javax.imageio.ImageIO;
import javax.print.attribute.standard.OrientationRequested;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.css.PseudoClass;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import uk.gov.dvla.osg.rpd.client.VaultStockClient;
import uk.gov.dvla.osg.rpd.error.RpdErrorResponse;
import uk.gov.dvla.osg.rpd.json.JsonUtils;
import uk.gov.dvla.osg.vault.data.CardData;
import uk.gov.dvla.osg.vault.data.VaultStock;
import uk.gov.dvla.osg.vault.enums.TableName;
import uk.gov.dvla.osg.vault.error.ErrorHandler;
import uk.gov.dvla.osg.vault.login.LogOut;
import uk.gov.dvla.osg.vault.main.NetworkConfig;
import uk.gov.dvla.osg.vault.output.PrintImage;
import uk.gov.dvla.osg.vault.output.Spreadsheet;
import uk.gov.dvla.osg.vault.session.Session;

public class MainFormController {

    private static final Logger LOGGER = LogManager.getLogger();
    private final boolean DEBUG_MODE = ManagementFactory.getRuntimeMXBean().getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;

    // TOP SECTION
    @FXML private ChoiceBox environmentChoice;
    @FXML private ChoiceBox siteChoice;
    @FXML private Label lblPrint;
    @FXML private Button btnPrint;
    @FXML private Label lblExcel;
    @FXML private Button btnExcel;
    @FXML private Label lblTime;
    @FXML private Button refreshBtn;
    
    // TABS
    @FXML private Tab onShelf_Tab;
    @FXML private Tab onCrate_Tab;
    @FXML private Tab firstUci_Tab;
    @FXML private Tab allStock_Tab;
    
    // GRIDS
    @FXML private GridPane onShelf_Grid;
    @FXML private GridPane onCrate_Grid;
    @FXML private GridPane firstUci_Grid;
    @FXML private GridPane allStock_Grid;
    
    // SIDE LABELS
    @FXML private Label onShelf_lblMorriston;
    @FXML private Label onShelf_lblFforestfach;
    @FXML private Label onCrate_lblMorriston;
    @FXML private Label onCrate_lblFforestfach;
    @FXML private Label allStock_lblMorriston;
    @FXML private Label allStock_lblFforestfach;

    // TABLES - CARDS IN VAULT
    @FXML private TableView<CardData> onShelf_mTachoTable;
    @FXML private TableColumn<CardData, String> onShelf_mTachoCol_Card;
    @FXML private TableColumn<CardData, String> onShelf_mTachoCol_Vol;

    @FXML private TableView<CardData> onShelf_mBrpTable;
    @FXML private TableColumn<CardData, String> onShelf_mBrpCol_Card;
    @FXML private TableColumn<CardData, String> onShelf_mBrpCol_Vol;

    @FXML private TableView<CardData> onShelf_mPolTable;
    @FXML private TableColumn<CardData, String> onShelf_mPolCol_Card;
    @FXML private TableColumn<CardData, String> onShelf_mPolCol_Vol;

    @FXML private TableView<CardData> onShelf_mDqcTable;
    @FXML private TableColumn<CardData, String> onShelf_mDqcCol_Card;
    @FXML private TableColumn<CardData, String> onShelf_mDqcCol_Vol;

    @FXML private TableView<CardData> onShelf_fTachoTable;
    @FXML private TableColumn<CardData, String> onShelf_fTachoCol_Card;
    @FXML private TableColumn<CardData, String> onShelf_fTachoCol_Vol;

    @FXML private TableView<CardData> onShelf_fBrpTable;
    @FXML private TableColumn<CardData, String> onShelf_fBrpCol_Card;
    @FXML private TableColumn<CardData, String> onShelf_fBrpCol_Vol;

    @FXML private TableView<CardData> onShelf_fPolTable;
    @FXML private TableColumn<CardData, String> onShelf_fPolCol_Card;
    @FXML private TableColumn<CardData, String> onShelf_fPolCol_Vol;

    @FXML private TableView<CardData> onShelf_fDqcTable;
    @FXML private TableColumn<CardData, String> onShelf_fDqcCol_Card;
    @FXML private TableColumn<CardData, String> onShelf_fDqcCol_Vol;

    // TABLES - CARDS OnCrate
    @FXML private TableView<CardData> onCrate_mTachoTable;
    @FXML private TableColumn<CardData, String> onCrate_mTachoCol_Card;
    @FXML private TableColumn<CardData, String> onCrate_mTachoCol_Vol;

    @FXML private TableView<CardData> onCrate_mBrpTable;
    @FXML private TableColumn<CardData, String> onCrate_mBrpCol_Card;
    @FXML private TableColumn<CardData, String> onCrate_mBrpCol_Vol;

    @FXML private TableView<CardData> onCrate_mPolTable;
    @FXML private TableColumn<CardData, String> onCrate_mPolCol_Card;
    @FXML private TableColumn<CardData, String> onCrate_mPolCol_Vol;

    @FXML private TableView<CardData> onCrate_mDqcTable;
    @FXML private TableColumn<CardData, String> onCrate_mDqcCol_Card;
    @FXML private TableColumn<CardData, String> onCrate_mDqcCol_Vol;

    @FXML private TableView<CardData> onCrate_fTachoTable;
    @FXML private TableColumn<CardData, String> onCrate_fTachoCol_Card;
    @FXML private TableColumn<CardData, String> onCrate_fTachoCol_Vol;

    @FXML private TableView<CardData> onCrate_fBrpTable;
    @FXML private TableColumn<CardData, String> onCrate_fBrpCol_Card;
    @FXML private TableColumn<CardData, String> onCrate_fBrpCol_Vol;

    @FXML private TableView<CardData> onCrate_fPolTable;
    @FXML private TableColumn<CardData, String> onCrate_fPolCol_Card;
    @FXML private TableColumn<CardData, String> onCrate_fPolCol_Vol;

    @FXML private TableView<CardData> onCrate_fDqcTable;
    @FXML private TableColumn<CardData, String> onCrate_fDqcCol_Card;
    @FXML private TableColumn<CardData, String> onCrate_fDqcCol_Vol;

    // TABLES - All Stock
    @FXML private TableView<CardData> allStock_mTachoTable;
    @FXML private TableColumn<CardData, String> allStock_mTachoCol_Card;
    @FXML private TableColumn<CardData, String> allStock_mTachoCol_Vol;
    
    @FXML private TableView<CardData> allStock_mBrpTable;
    @FXML private TableColumn<CardData, String> allStock_mBrpCol_Card;
    @FXML private TableColumn<CardData, String> allStock_mBrpCol_Vol;
    
    @FXML private TableView<CardData> allStock_mPolTable;
    @FXML private TableColumn<CardData, String> allStock_mPolCol_Card;
    @FXML private TableColumn<CardData, String> allStock_mPolCol_Vol;
    
    @FXML private TableView<CardData> allStock_mDqcTable;
    @FXML private TableColumn<CardData, String> allStock_mDqcCol_Card;
    @FXML private TableColumn<CardData, String> allStock_mDqcCol_Vol;
    
    @FXML private TableView<CardData> allStock_fTachoTable;
    @FXML private TableColumn<CardData, String> allStock_fTachoCol_Card;
    @FXML private TableColumn<CardData, String> allStock_fTachoCol_Vol;
    
    @FXML private TableView<CardData> allStock_fBrpTable;
    @FXML private TableColumn<CardData, String> allStock_fBrpCol_Card;
    @FXML private TableColumn<CardData, String> allStock_fBrpCol_Vol;
    
    @FXML private TableView<CardData> allStock_fPolTable;
    @FXML private TableColumn<CardData, String> allStock_fPolCol_Card;
    @FXML private TableColumn<CardData, String> allStock_fPolCol_Vol;
    
    @FXML private TableView<CardData> allStock_fDqcTable;
    @FXML private TableColumn<CardData, String> allStock_fDqcCol_Card;
    @FXML private TableColumn<CardData, String> allStock_fDqcCol_Vol;
    
    // TABLES - CARDS UCI
    @FXML private TableView<CardData> uci_mTachoTable;
    @FXML private TableColumn<CardData, String> uci_mTachoCol_Card;
    @FXML private TableColumn<CardData, String> uci_mTachoCol_uci;

    @FXML private TableView<CardData> uci_mBrpTable;
    @FXML private TableColumn<CardData, String> uci_mBrpCol_Card;
    @FXML private TableColumn<CardData, String> uci_mBrpCol_uci;

    @FXML private TableView<CardData> uci_mPolTable;
    @FXML private TableColumn<CardData, String> uci_mPolCol_Card;
    @FXML private TableColumn<CardData, String> uci_mPolCol_uci;

    @FXML private TableView<CardData> uci_mDqcTable;
    @FXML private TableColumn<CardData, String> uci_mDqcCol_Card;
    @FXML private TableColumn<CardData, String> uci_mDqcCol_uci;

    @FXML private TableView<CardData> uci_fTachoTable;
    @FXML private TableColumn<CardData, String> uci_fTachoCol_Card;
    @FXML private TableColumn<CardData, String> uci_fTachoCol_uci;

    @FXML private TableView<CardData> uci_fBrpTable;
    @FXML private TableColumn<CardData, String> uci_fBrpCol_Card;
    @FXML private TableColumn<CardData, String> uci_fBrpCol_uci;

    @FXML private TableView<CardData> uci_fPolTable;
    @FXML private TableColumn<CardData, String> uci_fPolCol_Card;
    @FXML private TableColumn<CardData, String> uci_fPolCol_uci;

    @FXML private TableView<CardData> uci_fDqcTable;
    @FXML private TableColumn<CardData, String> uci_fDqcCol_Card;
    @FXML private TableColumn<CardData, String> uci_fDqcCol_uci;

    private VaultStock vaultStock;
    private DataHandler dataHandler;

    /**
     * Retrieves vault stock data from RPD and initializes tables.
     */
    @FXML
    private void initialize() {
        // Add image to button
        setPrintButtonImage();
        setRefreshButtonImage();
        setExcelButtonImage();
        assignRefreshBtnAction();
        // Load values into the two combo boxes
        loadChoiceBoxes();
        // Set up the tables so they know which values to load
        setCellValueFactories();
        highlightTotals();
        // Download data from vault and load into tables
        refreshBtn.fire();
    }

    /**
     * Sets the print button image.
     */
    private void setPrintButtonImage() {
        Image buttonImage = new Image(getClass().getResourceAsStream("/Images/print.png"));
        btnPrint.setGraphic(new ImageView(buttonImage));
    }

    /**
     * Sets the refresh button image.
     */
    private void setRefreshButtonImage() {
        Image buttonImage = new Image(getClass().getResourceAsStream("/Images/refresh.png"));
        refreshBtn.setGraphic(new ImageView(buttonImage));
    }

    /**
     * Sets the excel button image.
     */
    private void setExcelButtonImage() {
        Image buttonImage = new Image(getClass().getResourceAsStream("/Images/excel.png"));
        btnExcel.setGraphic(new ImageView(buttonImage));
    }

    /**
     * Sets the data handler to contain either the "Production" or "Test" environment data.
     */
    private void checkSite() {
        if (vaultStock != null) {
            if (environmentChoice.getSelectionModel().getSelectedItem().equals("TEST")) {
                dataHandler = new DataHandler(vaultStock.getStockTotals().getTest());
            } else {
                dataHandler = new DataHandler(vaultStock.getStockTotals().getProduction());
            }
        }
    }

    /**
     * Loads data from the dataHandler into tables.
     */
    private void setupTableData() {
        if (siteChoice.getSelectionModel().getSelectedItem().equals("COMBINED")) {
            setupTableData_Combined();
        } else {
            setupTableData_BothSites();
        }
    }

    /**
     * Contacts the RPD Vault Api, downloads the data and returns it as a VaultStock object.
     * An error is shown if RPD cannot be contacted or if the user's session has timed-out through inactivity.
     *
     * @return the vault stock
     */
    private VaultStock loadJsonData() {
        try {
            if (DEBUG_MODE) {
                // String file = "C:\\Users\\OSG\\Desktop\\Raw Json.txt";
                // String file = "C:\\Users\\OSG\\Desktop\\NoTest.json";
                String file = "C:\\Users\\OSG\\Desktop\\live-vault-json.json";
                return JsonUtils.loadStockFile(file);
            } else {
                VaultStockClient vsc = VaultStockClient.getInstance(NetworkConfig.getInstance());
                Optional<VaultStock> vs = vsc.getStock(Session.getInstance().getToken());
                if (vs.isPresent()) {
                    return vs.get();
                } else {
                    RpdErrorResponse error = vsc.getErrorResponse();
                    LOGGER.error(error.toString());
                    Platform.runLater(() -> ErrorHandler.ErrorMsg(error.getCode(), error.getMessage(), error.getAction()));
                }
            }
        } catch (JsonIOException | JsonSyntaxException | FileNotFoundException ex) {
            LOGGER.fatal(ex.getMessage());
            // Display error msg dialog box to user
            Platform.runLater(() -> ErrorHandler.ErrorMsg("An error occured while connecting to the vault.", ex.getMessage()));
        }
        return null;
    }

    /**
     * Loads both choice boxes and assigns listeners.
     */
    private void loadChoiceBoxes() {
        ObservableList<String> environmentList = FXCollections.observableArrayList("PRODUCTION", "TEST");
        ObservableList<String> siteList = FXCollections.observableArrayList("BOTH", "COMBINED");

        environmentChoice.setItems(environmentList);
        environmentChoice.getSelectionModel().selectFirst();
        environmentChoice.getSelectionModel().selectedIndexProperty().addListener((ChangeListener) (observable, oldValue, newValue) -> {
            environmentChoice.getSelectionModel().select((int) newValue);
            checkSite();
            setupTableData();
        });

        siteChoice.setItems(siteList);
        siteChoice.getSelectionModel().selectFirst();
        siteChoice.getSelectionModel().selectedIndexProperty().addListener((ChangeListener) (observable, oldValue, newValue) -> {
            siteChoice.getSelectionModel().select((int) newValue);
            checkSite();
            setupTableData();
        });

    }

    /**
     * DataEntry objects are passed into the table. Cell value factories tell the table, which
     * fields from DataEntry will be loaded into each column.
     */
    private void setCellValueFactories() {
        // The names of fields in the DataEntry object.
        PropertyValueFactory<CardData, String> propValCardType = new PropertyValueFactory<>("cardType");
        PropertyValueFactory<CardData, String> propValCardVol = new PropertyValueFactory<>("volume");
        PropertyValueFactory<CardData, String> propValCardUci = new PropertyValueFactory<>("uci");
        // SCS - MORRISTON
        onShelf_mTachoCol_Card.setCellValueFactory(propValCardType);
        onShelf_mTachoCol_Vol.setCellValueFactory(propValCardVol);
        onShelf_mBrpCol_Card.setCellValueFactory(propValCardType);
        onShelf_mBrpCol_Vol.setCellValueFactory(propValCardVol);
        onShelf_mPolCol_Card.setCellValueFactory(propValCardType);
        onShelf_mPolCol_Vol.setCellValueFactory(propValCardVol);
        onShelf_mDqcCol_Card.setCellValueFactory(propValCardType);
        onShelf_mDqcCol_Vol.setCellValueFactory(propValCardVol);
        // SCS - FFORESTFACH
        onShelf_fTachoCol_Card.setCellValueFactory(propValCardType);
        onShelf_fTachoCol_Vol.setCellValueFactory(propValCardVol);
        onShelf_fBrpCol_Card.setCellValueFactory(propValCardType);
        onShelf_fBrpCol_Vol.setCellValueFactory(propValCardVol);
        onShelf_fPolCol_Card.setCellValueFactory(propValCardType);
        onShelf_fPolCol_Vol.setCellValueFactory(propValCardVol);
        onShelf_fDqcCol_Card.setCellValueFactory(propValCardType);
        onShelf_fDqcCol_Vol.setCellValueFactory(propValCardVol);
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
        // ALL STOCK - MORRISTON
        allStock_mTachoCol_Card.setCellValueFactory(propValCardType);
        allStock_mTachoCol_Vol.setCellValueFactory(propValCardVol);
        allStock_mBrpCol_Card.setCellValueFactory(propValCardType);
        allStock_mBrpCol_Vol.setCellValueFactory(propValCardVol);
        allStock_mPolCol_Card.setCellValueFactory(propValCardType);
        allStock_mPolCol_Vol.setCellValueFactory(propValCardVol);
        allStock_mDqcCol_Card.setCellValueFactory(propValCardType);
        allStock_mDqcCol_Vol.setCellValueFactory(propValCardVol);
        // ALL STOCK - FFORESTFACH
        allStock_fTachoCol_Card.setCellValueFactory(propValCardType);
        allStock_fTachoCol_Vol.setCellValueFactory(propValCardVol);
        allStock_fBrpCol_Card.setCellValueFactory(propValCardType);
        allStock_fBrpCol_Vol.setCellValueFactory(propValCardVol);
        allStock_fPolCol_Card.setCellValueFactory(propValCardType);
        allStock_fPolCol_Vol.setCellValueFactory(propValCardVol);
        allStock_fDqcCol_Card.setCellValueFactory(propValCardType);
        allStock_fDqcCol_Vol.setCellValueFactory(propValCardVol);
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

    /**
     * Sets the grids to display both rows of tables and then calls
     * methods that load the data into each tab.
     */
    private void setupTableData_BothSites() {
        onShelf_lblMorriston.setText("MORRISTON");
        onShelf_lblFforestfach.setText("FFORESTFACH");
        onShelf_Grid.setRowSpan(onShelf_lblMorriston, 1);

        onCrate_lblMorriston.setText("MORRISTON");
        onCrate_lblFforestfach.setText("FFORESTFACH");
        onCrate_Grid.setRowSpan(onCrate_lblMorriston, 1);

        allStock_lblMorriston.setText("MORRISTON");
        allStock_lblFforestfach.setText("FFORESTFACH");
        onCrate_Grid.setRowSpan(allStock_lblMorriston, 1);
        
        setData_InVault_BothSites();
        setData_OnCrate_BothSites();
        setData_AllStock_BothSites();
        setData_UCI();
    }

    /**
     * Sets the in vault data for both sites.
     * Loads the data from the dataHandler into each table and adjusts the tables so 
     * they span 1 row each and all are made visible.
     */
    private void setData_InVault_BothSites() {
        onShelf_mTachoTable.setItems(dataHandler.getOnShelfDataForSingleSite(TACHO, M));
        onShelf_Grid.setRowSpan(onShelf_mTachoTable, 1);

        onShelf_mBrpTable.setItems(dataHandler.getOnShelfDataForSingleSite(BID, M));
        onShelf_Grid.setRowSpan(onShelf_mBrpTable, 1);

        onShelf_mPolTable.setItems(dataHandler.getOnShelfDataForSingleSite(POL, M));
        onShelf_Grid.setRowSpan(onShelf_mPolTable, 1);

        onShelf_mDqcTable.setItems(dataHandler.getOnShelfDataForSingleSite(DQC, M));
        onShelf_Grid.setRowSpan(onShelf_mDqcTable, 1);

        onShelf_fTachoTable.setItems(dataHandler.getOnShelfDataForSingleSite(TACHO, F));
        onShelf_fTachoTable.setVisible(true);

        onShelf_fBrpTable.setItems(dataHandler.getOnShelfDataForSingleSite(BID, F));
        onShelf_fBrpTable.setVisible(true);

        onShelf_fPolTable.setItems(dataHandler.getOnShelfDataForSingleSite(POL, F));
        onShelf_fPolTable.setVisible(true);

        onShelf_fDqcTable.setItems(dataHandler.getOnShelfDataForSingleSite(DQC, F));
        onShelf_fDqcTable.setVisible(true);
    }

    /**
     * Sets the data on crate both sites.
     */
    private void setData_OnCrate_BothSites() {
        onCrate_mTachoTable.setItems(dataHandler.getOnCrateDataForSingleSite(TACHO, M));
        onCrate_Grid.setRowSpan(onCrate_mTachoTable, 1);

        onCrate_mBrpTable.setItems(dataHandler.getOnCrateDataForSingleSite(BID, M));
        onCrate_Grid.setRowSpan(onCrate_mBrpTable, 1);

        onCrate_mPolTable.setItems(dataHandler.getOnCrateDataForSingleSite(POL, M));
        onCrate_Grid.setRowSpan(onCrate_mPolTable, 1);

        onCrate_mDqcTable.setItems(dataHandler.getOnCrateDataForSingleSite(DQC, M));
        onCrate_Grid.setRowSpan(onCrate_mDqcTable, 1);

        onCrate_fTachoTable.setItems(dataHandler.getOnCrateDataForSingleSite(TACHO, F));
        onCrate_fTachoTable.setVisible(true);

        onCrate_fBrpTable.setItems(dataHandler.getOnCrateDataForSingleSite(BID, F));
        onCrate_fBrpTable.setVisible(true);

        onCrate_fPolTable.setItems(dataHandler.getOnCrateDataForSingleSite(POL, F));
        onCrate_fPolTable.setVisible(true);

        onCrate_fDqcTable.setItems(dataHandler.getOnCrateDataForSingleSite(DQC, F));
        onCrate_fDqcTable.setVisible(true);
    }

    /**
     * Sets the data all stock both sites.
     */
    private void setData_AllStock_BothSites() {
        allStock_mTachoTable.setItems(dataHandler.getAllStockDataForSingleSite(TACHO, M));
        allStock_Grid.setRowSpan(allStock_mTachoTable, 1);

        allStock_mBrpTable.setItems(dataHandler.getAllStockDataForSingleSite(BID, M));
        allStock_Grid.setRowSpan(allStock_mBrpTable, 1);

        allStock_mPolTable.setItems(dataHandler.getAllStockDataForSingleSite(POL, M));
        allStock_Grid.setRowSpan(allStock_mPolTable, 1);

        allStock_mDqcTable.setItems(dataHandler.getAllStockDataForSingleSite(DQC, M));
        allStock_Grid.setRowSpan(allStock_mDqcTable, 1);

        allStock_fTachoTable.setItems(dataHandler.getAllStockDataForSingleSite(TACHO, F));
        allStock_fTachoTable.setVisible(true);

        allStock_fBrpTable.setItems(dataHandler.getAllStockDataForSingleSite(BID, F));
        allStock_fBrpTable.setVisible(true);

        allStock_fPolTable.setItems(dataHandler.getAllStockDataForSingleSite(POL, F));
        allStock_fPolTable.setVisible(true);

        allStock_fDqcTable.setItems(dataHandler.getAllStockDataForSingleSite(DQC, F));
        allStock_fDqcTable.setVisible(true);
    }
    
    /**
     * Sets the UCI data. 
     * This data remains the same for the "Both Sites" and "Combined" modes.
     */
    private void setData_UCI() {
        uci_mTachoTable.setItems(dataHandler.getUciDataForSingleSite(TACHO, M));
        uci_mBrpTable.setItems(dataHandler.getUciDataForSingleSite(BID, M));
        uci_mPolTable.setItems(dataHandler.getUciDataForSingleSite(POL, M));
        uci_mDqcTable.setItems(dataHandler.getUciDataForSingleSite(DQC, M));
        uci_fTachoTable.setItems(dataHandler.getUciDataForSingleSite(TACHO, F));
        uci_fBrpTable.setItems(dataHandler.getUciDataForSingleSite(BID, F));
        uci_fPolTable.setItems(dataHandler.getUciDataForSingleSite(POL, F));
        uci_fDqcTable.setItems(dataHandler.getUciDataForSingleSite(DQC, F));
    }

    /**
     * Setup table data for combining volumes for both sites.
     * The top (Morriston) label is expanded across both rows so that it sits in the center of the grid.
     */
    private void setupTableData_Combined() {
        onShelf_lblMorriston.setText("MORRISTON\n&\nFFORESTFACH");
        onShelf_lblFforestfach.setText("");
        onShelf_Grid.setRowSpan(onShelf_lblMorriston, 2);

        onCrate_lblMorriston.setText("MORRISTON\n&\nFFORESTFACH");
        onCrate_lblFforestfach.setText("");
        onCrate_Grid.setRowSpan(onCrate_lblMorriston, 2);

        allStock_lblMorriston.setText("MORRISTON\n&\nFFORESTFACH");
        allStock_lblFforestfach.setText("");
        allStock_Grid.setRowSpan(allStock_lblMorriston, 2);
        
        setData_OnShelf_Combined();
        setData_OnCrate_Combined();
        setData_AllStock_Combined();
        setData_UCI();
    }

    /**
     * Sets the data on shelf combined.
     * Tables in the top (Morriston) row are expanded across both rows so they sit in the center of the grid.
     * The bottom (Fforestfach) row is hidden by setting the visibility to false for each Fforestfach table.
     */
    private void setData_OnShelf_Combined() {
        onShelf_mTachoTable.setItems(dataHandler.getOnShelfDataForCombined(TACHO));
        onShelf_Grid.setRowSpan(onShelf_mTachoTable, 2);

        onShelf_mBrpTable.setItems(dataHandler.getOnShelfDataForCombined(BID));
        onShelf_Grid.setRowSpan(onShelf_mBrpTable, 2);

        onShelf_mPolTable.setItems(dataHandler.getOnShelfDataForCombined(POL));
        onShelf_Grid.setRowSpan(onShelf_mPolTable, 2);

        onShelf_mDqcTable.setItems(dataHandler.getOnShelfDataForCombined(DQC));
        onShelf_Grid.setRowSpan(onShelf_mDqcTable, 2);

        onShelf_fTachoTable.setVisible(false);
        onShelf_fBrpTable.setVisible(false);
        onShelf_fPolTable.setVisible(false);
        onShelf_fDqcTable.setVisible(false);
    }

    /**
     * Sets the data on crate combined.
     */
    private void setData_OnCrate_Combined() {
        onCrate_mTachoTable.setItems(dataHandler.getOnCrateDataForCombined(TACHO));
        onCrate_Grid.setRowSpan(onCrate_mTachoTable, 2);

        onCrate_mBrpTable.setItems(dataHandler.getOnCrateDataForCombined(BID));
        onCrate_Grid.setRowSpan(onCrate_mBrpTable, 2);

        onCrate_mPolTable.setItems(dataHandler.getOnCrateDataForCombined(POL));
        onCrate_Grid.setRowSpan(onCrate_mPolTable, 2);

        onCrate_mDqcTable.setItems(dataHandler.getOnCrateDataForCombined(DQC));
        onCrate_Grid.setRowSpan(onCrate_mDqcTable, 2);

        onCrate_fTachoTable.setVisible(false);
        onCrate_fBrpTable.setVisible(false);
        onCrate_fPolTable.setVisible(false);
        onCrate_fDqcTable.setVisible(false);
    }

    /**
     * Sets the data all stock combined.
     */
    private void setData_AllStock_Combined() {
        allStock_mTachoTable.setItems(dataHandler.getAllStockDataForCombined(TACHO));
        allStock_Grid.setRowSpan(allStock_mTachoTable, 2);

        allStock_mBrpTable.setItems(dataHandler.getAllStockDataForCombined(BID));
        allStock_Grid.setRowSpan(allStock_mBrpTable, 2);

        allStock_mPolTable.setItems(dataHandler.getAllStockDataForCombined(POL));
        allStock_Grid.setRowSpan(allStock_mPolTable, 2);

        allStock_mDqcTable.setItems(dataHandler.getAllStockDataForCombined(DQC));
        allStock_Grid.setRowSpan(allStock_mDqcTable, 2);

        allStock_fTachoTable.setVisible(false);
        allStock_fBrpTable.setVisible(false);
        allStock_fPolTable.setVisible(false);
        allStock_fDqcTable.setVisible(false);
    }
    
    /**
     * Assigns an action to the refresh button.
     * This action calls the getJsonData method in the background, shows a progress indicator on the button
     * and updates the message label to display the time of last refresh. 
     */
    private void assignRefreshBtnAction() {
        AtomicInteger taskExecution = new AtomicInteger(0);

        refreshBtn.setOnAction(e -> {
            ProgressIndicator progressIndicator = new ProgressIndicator();
            refreshBtn.setGraphic(progressIndicator);

            Task<Void> task = new Task<Void>() {
                {
                    setOnFailed(a -> {
                        updateMessage("Failed!");
                        setRefreshButtonImage();
                    });
                    setOnSucceeded(a -> {
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                        LocalDateTime now = LocalDateTime.now();
                        updateMessage("Last Refresh:\n" + dtf.format(now));
                        setRefreshButtonImage();
                    });
                    setOnCancelled(a -> {
                        updateMessage("Cancelled");
                        setRefreshButtonImage();
                    });
                }

                @Override
                protected Void call() throws Exception {
                    updateMessage("Downloading... ");
                    // Short pause
                    if (DEBUG_MODE) {
                        Thread.sleep(2000);
                    }

                    vaultStock = loadJsonData();

                    if (vaultStock != null) {
                        checkSite();
                        setupTableData();
                    }

                    return null;
                }
            };

            lblTime.textProperty().unbind();
            lblTime.textProperty().bind(task.messageProperty());

            Thread taskThread = new Thread(task, "task-thread-" + taskExecution.getAndIncrement());
            taskThread.start();

            // Move focus to read only control to remove the focus highlight from button
            lblTime.requestFocus();
        });
    }

    /**
     * Assings the "totalRow" CSS styling to the bottom row of each table.
     * The CSS styling sets the font to Bold for these rows.
     */
    public void highlightTotals() {
        final PseudoClass totalRowPseudoClass = PseudoClass.getPseudoClass("totalrow");
        // In Vault Tab
        onShelf_mTachoTable.setRowFactory(tableView -> new HighlightTotalRow(totalRowPseudoClass));
        onShelf_mBrpTable.setRowFactory(tableView -> new HighlightTotalRow(totalRowPseudoClass));
        onShelf_mDqcTable.setRowFactory(tableView -> new HighlightTotalRow(totalRowPseudoClass));
        onShelf_mPolTable.setRowFactory(tableView -> new HighlightTotalRow(totalRowPseudoClass));
        onShelf_fTachoTable.setRowFactory(tableView -> new HighlightTotalRow(totalRowPseudoClass));
        onShelf_fBrpTable.setRowFactory(tableView -> new HighlightTotalRow(totalRowPseudoClass));
        onShelf_fDqcTable.setRowFactory(tableView -> new HighlightTotalRow(totalRowPseudoClass));
        onShelf_fPolTable.setRowFactory(tableView -> new HighlightTotalRow(totalRowPseudoClass));
        // On Crate Tab
        onCrate_mTachoTable.setRowFactory(tableView -> new HighlightTotalRow(totalRowPseudoClass));
        onCrate_mBrpTable.setRowFactory(tableView -> new HighlightTotalRow(totalRowPseudoClass));
        onCrate_mDqcTable.setRowFactory(tableView -> new HighlightTotalRow(totalRowPseudoClass));
        onCrate_mPolTable.setRowFactory(tableView -> new HighlightTotalRow(totalRowPseudoClass));
        onCrate_fTachoTable.setRowFactory(tableView -> new HighlightTotalRow(totalRowPseudoClass));
        onCrate_fBrpTable.setRowFactory(tableView -> new HighlightTotalRow(totalRowPseudoClass));
        onCrate_fDqcTable.setRowFactory(tableView -> new HighlightTotalRow(totalRowPseudoClass));
        onCrate_fPolTable.setRowFactory(tableView -> new HighlightTotalRow(totalRowPseudoClass));
        // All Stock Tab
        allStock_mTachoTable.setRowFactory(tableView -> new HighlightTotalRow(totalRowPseudoClass));
        allStock_mBrpTable.setRowFactory(tableView -> new HighlightTotalRow(totalRowPseudoClass));
        allStock_mDqcTable.setRowFactory(tableView -> new HighlightTotalRow(totalRowPseudoClass));
        allStock_mPolTable.setRowFactory(tableView -> new HighlightTotalRow(totalRowPseudoClass));
        allStock_fTachoTable.setRowFactory(tableView -> new HighlightTotalRow(totalRowPseudoClass));
        allStock_fBrpTable.setRowFactory(tableView -> new HighlightTotalRow(totalRowPseudoClass));
        allStock_fDqcTable.setRowFactory(tableView -> new HighlightTotalRow(totalRowPseudoClass));
        allStock_fPolTable.setRowFactory(tableView -> new HighlightTotalRow(totalRowPseudoClass));
    }

    
    /**
     * Prints a screenshot of the currently selected tab.
     */
    @FXML private void print() {
        lblPrint.setText("Please wait...");

        WritableImage image;

        if (onShelf_Tab.isSelected()) {
            image = onShelf_Grid.snapshot(new SnapshotParameters(), null);
        } else if (onCrate_Tab.isSelected()) {
            image = onCrate_Grid.snapshot(new SnapshotParameters(), null);
        } else if (allStock_Tab.isSelected()) {
            image = allStock_Grid.snapshot(new SnapshotParameters(), null);
        } else {
            image = firstUci_Grid.snapshot(new SnapshotParameters(), null);
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("_ddMMyyyy_HHmmss");
        LocalDateTime now = LocalDateTime.now();
        File file = new File(FileUtils.getUserDirectoryPath() + dtf.format(now) + ".png");

        // Send image to file
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
        } catch (Exception ex) {
            LOGGER.error("Unable to save screenshot image to {}", file.getAbsolutePath());
            Platform.runLater(() -> {
                lblPrint.setText("Unable to save screenshot.");
                ErrorHandler.ErrorMsg("File Save Error", "An error occured while saving sreenshot to " + file.getAbsolutePath(), "Please be patient while we fix the issue.");
            });
            return;
        }
        // Send image file to printer
        try {
            boolean printed = new PrintImage().sendToDefault(file.getAbsolutePath(), 1, OrientationRequested.LANDSCAPE);
            if (printed) {
                lblPrint.setText("Image sent to default printer.");
            } else {
                lblPrint.setText("Printing was cancelled.\nPlease check your default printer.");
            }
        } catch (Exception ex) {
            LOGGER.error("Unable to print image.\n{}", ExceptionUtils.getStackTrace(ex));
            Platform.runLater(() -> {
                lblPrint.setText("Unable to print image.");
                ErrorHandler.ErrorMsg("Print Error", "An error occured while sending image to printer.", "Please be patient while we fix the issue.");
            });
        }

        // Delete image file
        FileUtils.deleteQuietly(file);
        
        displayMessage();

    }

    /**
     * Displays messages for the Print and Excel buttons.
     * Messages are displayed for 3 seconds and then disappear.
     */
    private void displayMessage() {
        lblPrint.setOpacity(1);
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(3), lblPrint);
        fadeTransition.setDelay(Duration.seconds(4));
        fadeTransition.setFromValue(0.99);
        fadeTransition.setToValue(0.0);
        fadeTransition.play();
        // Move focus to read only control to remove the focus highlight from button
        lblTime.requestFocus();
    }

    
    /**
     * Saves the data as an Excel spreadsheet.
     * A datamap is created, consisting of TableNames as keys and the table data as values. The table data is the union of both the 
     * Morriston and Fforestfach data. The spreadsheet contains a single row of data, for each tab, with both sites shown separately
     * in each table. 
     */
    @FXML private void save() {
        lblPrint.setText("Please wait...");
        
        new Thread(() -> {

            // BUILD DATA MAP
            Map<TableName, List<CardData>> dataMap = new HashMap<>();
            
            // Ignores the total row plus all rows in the table that are empty
            Predicate<? super CardData> fieldsToIgnore = c -> c.cardType.equals("TOTAL") || c.cardType.isEmpty();

            // ON SHELF
            dataMap.put(TableName.ONSHELF_TACHO, ListUtils.union(ListUtils.selectRejected(onShelf_mTachoTable.getItems(), fieldsToIgnore), ListUtils.selectRejected(onShelf_fTachoTable.getItems(), fieldsToIgnore)));
            dataMap.put(TableName.ONSHELF_BRP, ListUtils.union(ListUtils.selectRejected(onShelf_mBrpTable.getItems(), fieldsToIgnore), ListUtils.selectRejected(onShelf_fBrpTable.getItems(), fieldsToIgnore)));
            dataMap.put(TableName.ONSHELF_POL, ListUtils.union(ListUtils.selectRejected(onShelf_mPolTable.getItems(), fieldsToIgnore), ListUtils.selectRejected(onShelf_fPolTable.getItems(), fieldsToIgnore)));
            dataMap.put(TableName.ONSHELF_DQC, ListUtils.union(ListUtils.selectRejected(onShelf_mDqcTable.getItems(), fieldsToIgnore), ListUtils.selectRejected(onShelf_fDqcTable.getItems(), fieldsToIgnore)));

            // IN CRATE
            dataMap.put(TableName.ONCRATE_TACHO, ListUtils.union(ListUtils.selectRejected(onCrate_mTachoTable.getItems(), fieldsToIgnore), ListUtils.selectRejected(onCrate_fTachoTable.getItems(), fieldsToIgnore)));
            dataMap.put(TableName.ONCRATE_BRP, ListUtils.union(ListUtils.selectRejected(onCrate_mBrpTable.getItems(), fieldsToIgnore), ListUtils.selectRejected(onCrate_fBrpTable.getItems(), fieldsToIgnore)));
            dataMap.put(TableName.ONCRATE_POL, ListUtils.union(ListUtils.selectRejected(onCrate_mPolTable.getItems(), fieldsToIgnore), ListUtils.selectRejected(onCrate_fPolTable.getItems(), fieldsToIgnore)));
            dataMap.put(TableName.ONCRATE_DQC, ListUtils.union(ListUtils.selectRejected(onCrate_mDqcTable.getItems(), fieldsToIgnore), ListUtils.selectRejected(onCrate_fDqcTable.getItems(), fieldsToIgnore)));

            // ALL STOCK
            dataMap.put(TableName.ALLSTOCK_TACHO, ListUtils.union(ListUtils.selectRejected(allStock_mTachoTable.getItems(), fieldsToIgnore), ListUtils.selectRejected(allStock_fTachoTable.getItems(), fieldsToIgnore)));
            dataMap.put(TableName.ALLSTOCK_BRP, ListUtils.union(ListUtils.selectRejected(allStock_mBrpTable.getItems(), fieldsToIgnore), ListUtils.selectRejected(allStock_fBrpTable.getItems(), fieldsToIgnore)));
            dataMap.put(TableName.ALLSTOCK_POL, ListUtils.union(ListUtils.selectRejected(allStock_mPolTable.getItems(), fieldsToIgnore), ListUtils.selectRejected(allStock_fPolTable.getItems(), fieldsToIgnore)));
            dataMap.put(TableName.ALLSTOCK_DQC, ListUtils.union(ListUtils.selectRejected(allStock_mDqcTable.getItems(), fieldsToIgnore), ListUtils.selectRejected(allStock_fDqcTable.getItems(), fieldsToIgnore)));

            // FIRST UCI
            dataMap.put(TableName.UCI_TACHO, ListUtils.union(ListUtils.selectRejected(uci_mTachoTable.getItems(), fieldsToIgnore), ListUtils.selectRejected(uci_fTachoTable.getItems(), fieldsToIgnore)));
            dataMap.put(TableName.UCI_BRP, ListUtils.union(ListUtils.selectRejected(uci_mBrpTable.getItems(), fieldsToIgnore), ListUtils.selectRejected(uci_fBrpTable.getItems(), fieldsToIgnore)));
            dataMap.put(TableName.UCI_POL, ListUtils.union(ListUtils.selectRejected(uci_mPolTable.getItems(), fieldsToIgnore), ListUtils.selectRejected(uci_fPolTable.getItems(), fieldsToIgnore)));
            dataMap.put(TableName.UCI_DQC, ListUtils.union(ListUtils.selectRejected(uci_mDqcTable.getItems(), fieldsToIgnore), ListUtils.selectRejected(uci_fDqcTable.getItems(), fieldsToIgnore)));

            // SAVE TO EXCEL SPREADSHEET
            Spreadsheet s = new Spreadsheet(dataMap);
            try {
                boolean saved = s.save();
                Platform.runLater(() -> {
                    if (saved) {
                    lblPrint.setText("Saved as Excel spreadsheet!");
                    } else {
                        lblPrint.setText("Save cancelled!");
                    }
                });
            } catch (IOException ex) {
                LOGGER.error("Unable to save spreadsheet. {}", ex.getMessage());
                Platform.runLater(() -> {
                    lblPrint.setText("Unable to save spreadsheet.");
                    if (ex.getMessage().contains("being used by another process")) {
                        ErrorHandler.ErrorMsg("File Save Error", "The file is currently open in Excel.", "Close the open file and try again.");
                    } else {
                        ErrorHandler.ErrorMsg("File Save Error", "Unable to save the spreadsheet.", "Check you have permission to save to this directory.");
                    }
                });
            } catch (RuntimeException | InterruptedException ex) {
                LOGGER.error("Unable to save spreadsheet. {}", ex.getMessage());
                Platform.runLater(() -> lblPrint.setText("No data to save."));
            }
            // Update label - runLater is called as we are on a background thread and need to switch to the GUI thread
            Platform.runLater(() -> displayMessage());

        }).start();;        
    }

    /**
     * User is logged out and application shut down.
     */
    public void logout() {
        // contact RPD on background thread to prevent main window from freezing
        new Thread(() -> {
            Platform.runLater(() -> LogOut.logout(NetworkConfig.getInstance()));
        }).start();
    }
}

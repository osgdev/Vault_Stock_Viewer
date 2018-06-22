package uk.gov.dvla.osg.vault.mainform;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.management.ManagementFactory;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import javax.imageio.ImageIO;
import javax.print.attribute.standard.OrientationRequested;

import org.apache.commons.collections4.ListUtils;
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
import uk.gov.dvla.osg.vault.enums.CardClass;
import uk.gov.dvla.osg.vault.enums.Site;
import uk.gov.dvla.osg.vault.enums.TableName;
import uk.gov.dvla.osg.vault.error.ErrorHandler;
import uk.gov.dvla.osg.vault.login.LogOut;
import uk.gov.dvla.osg.vault.main.NetworkConfig;
import uk.gov.dvla.osg.vault.session.Session;

public class MainFormController {

    private static final Logger LOGGER = LogManager.getLogger();
    private final boolean DEBUG_MODE = ManagementFactory.getRuntimeMXBean().getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;

    // TOP SECTION
    @FXML
    private ChoiceBox environmentChoice;
    @FXML
    private ChoiceBox siteChoice;
    @FXML
    private Label lblPrint;
    @FXML
    private Button btnPrint;
    @FXML
    private Label lblExcel;
    @FXML
    private Button btnExcel;
    @FXML
    private Label lblTime;
    @FXML
    private Button refreshBtn;
    @FXML
    private Tab tabOnShelf;
    @FXML
    private Tab tabInCrate;
    @FXML
    private Tab tabFirstUci;
    @FXML
    private GridPane gridOnShelf;
    @FXML
    private GridPane gridInCrate;
    @FXML
    private GridPane gridFirstUci;

    @FXML
    private Label lblMorriston_scs;
    @FXML
    private Label lblFforestfach_scs;
    @FXML
    private Label lblMorriston_onCrate;
    @FXML
    private Label lblFforestfach_onCrate;

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
        // Add image to button
        setPrintButtonImage();
        setRefreshButtonImage();
        setExcelButtonImage();
        assignRefreshBtnAction();
        loadChoiceBoxes();
        setCellValueFactories();
        highlightTotals();
        // Download data from vault and load into tables
        refreshBtn.fire();
    }

    private void setPrintButtonImage() {
        Image buttonImage = new Image(getClass().getResourceAsStream("/Images/print.png"));
        btnPrint.setGraphic(new ImageView(buttonImage));
    }

    private void setRefreshButtonImage() {
        Image buttonImage = new Image(getClass().getResourceAsStream("/Images/refresh.png"));
        refreshBtn.setGraphic(new ImageView(buttonImage));
    }

    private void setExcelButtonImage() {
        Image buttonImage = new Image(getClass().getResourceAsStream("/Images/excel.png"));
        btnExcel.setGraphic(new ImageView(buttonImage));
    }

    private void checkSite() {
        if (vaultStock != null) {
            if (environmentChoice.getSelectionModel().getSelectedItem().equals("TEST")) {
                dataHandler = new DataHandler(vaultStock.getStockTotals().getTest());
            } else {
                dataHandler = new DataHandler(vaultStock.getStockTotals().getProduction());
            }
        }
    }

    private void setupTableData() {
        if (siteChoice.getSelectionModel().getSelectedItem().equals("COMBINED")) {
            setupTableData_Combined();
        } else {
            setupTableData_BothSites();
        }
    }

    private VaultStock loadJsonData() {
        try {
            if (DEBUG_MODE) {
                // String file = "C:\\Users\\OSG\\Desktop\\Raw Json.txt";
                // String file = "C:\\Users\\OSG\\Desktop\\NoTest.json";
                String file = "C:\\Users\\OSG\\Desktop\\live-vault-json.json";
                return JsonUtils.loadStockFile(file);
            } else {
                VaultStockClient vsc = new VaultStockClient(NetworkConfig.getInstance());
                Optional<VaultStock> vs = vsc.getStock(Session.getInstance().getToken());
                if (vs.isPresent()) {
                    return vs.get();
                } else {
                    RpdErrorResponse error = vsc.getErrorResponse();
                    LOGGER.error(error.toString());
                    Platform.runLater(() -> {
                        ErrorHandler.ErrorMsg(error.getCode(), error.getMessage(), error.getAction());
                    });
                }
            }
        } catch (JsonIOException | JsonSyntaxException | FileNotFoundException ex) {
            LOGGER.fatal(ex.getMessage());
            // Display error msg dialog box to user
            Platform.runLater(() -> {
                ErrorHandler.ErrorMsg("An error occured while connecting to the vault.", ex.getMessage());
            });
        }
        return null;
    }

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

    private void setupTableData_BothSites() {
        lblMorriston_scs.setText("MORRISTON");
        lblFforestfach_scs.setText("FFORESTFACH");
        gridOnShelf.setRowSpan(lblMorriston_scs, 1);

        lblMorriston_onCrate.setText("MORRISTON");
        lblFforestfach_onCrate.setText("FFORESTFACH");
        gridInCrate.setRowSpan(lblMorriston_onCrate, 1);

        setDataSCS_BothSites();
        setDataOnCrate_BothSites();
        setTestDataUCI();
    }

    private void setDataSCS_BothSites() {
        scs_mTachoTable.setItems(dataHandler.getScsDataForBothSites(CardClass.TACHO, Site.M));
        gridOnShelf.setRowSpan(scs_mTachoTable, 1);

        scs_mBrpTable.setItems(dataHandler.getScsDataForBothSites(CardClass.BID, Site.M));
        gridOnShelf.setRowSpan(scs_mBrpTable, 1);

        scs_mPolTable.setItems(dataHandler.getScsDataForBothSites(CardClass.POL, Site.M));
        gridOnShelf.setRowSpan(scs_mPolTable, 1);

        scs_mDqcTable.setItems(dataHandler.getScsDataForBothSites(CardClass.DQC, Site.M));
        gridOnShelf.setRowSpan(scs_mDqcTable, 1);

        scs_fTachoTable.setItems(dataHandler.getScsDataForBothSites(CardClass.TACHO, Site.F));
        scs_fTachoTable.setVisible(true);

        scs_fBrpTable.setItems(dataHandler.getScsDataForBothSites(CardClass.BID, Site.F));
        scs_fBrpTable.setVisible(true);

        scs_fPolTable.setItems(dataHandler.getScsDataForBothSites(CardClass.POL, Site.F));
        scs_fPolTable.setVisible(true);

        scs_fDqcTable.setItems(dataHandler.getScsDataForBothSites(CardClass.DQC, Site.F));
        scs_fDqcTable.setVisible(true);
    }

    private void setDataOnCrate_BothSites() {
        onCrate_mTachoTable.setItems(dataHandler.getOnCrateDataForBothSites(CardClass.TACHO, Site.M));
        gridInCrate.setRowSpan(onCrate_mTachoTable, 1);

        onCrate_mBrpTable.setItems(dataHandler.getOnCrateDataForBothSites(CardClass.BID, Site.M));
        gridInCrate.setRowSpan(onCrate_mBrpTable, 1);

        onCrate_mPolTable.setItems(dataHandler.getOnCrateDataForBothSites(CardClass.POL, Site.M));
        gridInCrate.setRowSpan(onCrate_mPolTable, 1);

        onCrate_mDqcTable.setItems(dataHandler.getOnCrateDataForBothSites(CardClass.DQC, Site.M));
        gridInCrate.setRowSpan(onCrate_mDqcTable, 1);

        onCrate_fTachoTable.setItems(dataHandler.getOnCrateDataForBothSites(CardClass.TACHO, Site.F));
        onCrate_fTachoTable.setVisible(true);

        onCrate_fBrpTable.setItems(dataHandler.getOnCrateDataForBothSites(CardClass.BID, Site.F));
        onCrate_fBrpTable.setVisible(true);

        onCrate_fPolTable.setItems(dataHandler.getOnCrateDataForBothSites(CardClass.POL, Site.F));
        onCrate_fPolTable.setVisible(true);

        onCrate_fDqcTable.setItems(dataHandler.getOnCrateDataForBothSites(CardClass.DQC, Site.F));
        onCrate_fDqcTable.setVisible(true);
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

    private void setupTableData_Combined() {
        lblMorriston_scs.setText("MORRISTON\n&\nFFORESTFACH");
        lblFforestfach_scs.setText("");
        gridOnShelf.setRowSpan(lblMorriston_scs, 2);

        lblMorriston_onCrate.setText("MORRISTON\n&\nFFORESTFACH");
        lblFforestfach_onCrate.setText("");
        gridInCrate.setRowSpan(lblMorriston_onCrate, 2);

        setDataSCS_Combined();
        setDataOnCrate_Combined();
        setTestDataUCI();
    }

    private void setDataSCS_Combined() {
        scs_mTachoTable.setItems(dataHandler.getScsDataForCombined(CardClass.TACHO));
        gridOnShelf.setRowSpan(scs_mTachoTable, 2);

        scs_mBrpTable.setItems(dataHandler.getScsDataForCombined(CardClass.BID));
        gridOnShelf.setRowSpan(scs_mBrpTable, 2);

        scs_mPolTable.setItems(dataHandler.getScsDataForCombined(CardClass.POL));
        gridOnShelf.setRowSpan(scs_mPolTable, 2);

        scs_mDqcTable.setItems(dataHandler.getScsDataForCombined(CardClass.DQC));
        gridOnShelf.setRowSpan(scs_mDqcTable, 2);

        scs_fTachoTable.setVisible(false);
        scs_fBrpTable.setVisible(false);
        scs_fPolTable.setVisible(false);
        scs_fDqcTable.setVisible(false);
    }

    private void setDataOnCrate_Combined() {
        onCrate_mTachoTable.setItems(dataHandler.getOnCrateDataForCombined(CardClass.TACHO));
        gridInCrate.setRowSpan(onCrate_mTachoTable, 2);

        onCrate_mBrpTable.setItems(dataHandler.getOnCrateDataForCombined(CardClass.BID));
        gridInCrate.setRowSpan(onCrate_mBrpTable, 2);

        onCrate_mPolTable.setItems(dataHandler.getOnCrateDataForCombined(CardClass.POL));
        gridInCrate.setRowSpan(onCrate_mPolTable, 2);

        onCrate_mDqcTable.setItems(dataHandler.getOnCrateDataForCombined(CardClass.DQC));
        gridInCrate.setRowSpan(onCrate_mDqcTable, 2);

        onCrate_fTachoTable.setVisible(false);
        onCrate_fBrpTable.setVisible(false);
        onCrate_fPolTable.setVisible(false);
        onCrate_fDqcTable.setVisible(false);
    }

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

    public void highlightTotals() {
        final PseudoClass totalRowPseudoClass = PseudoClass.getPseudoClass("totalrow");
        // In Vault Tab
        scs_mTachoTable.setRowFactory(tableView -> new HighlightTotalRow(totalRowPseudoClass));
        scs_mBrpTable.setRowFactory(tableView -> new HighlightTotalRow(totalRowPseudoClass));
        scs_mDqcTable.setRowFactory(tableView -> new HighlightTotalRow(totalRowPseudoClass));
        scs_mPolTable.setRowFactory(tableView -> new HighlightTotalRow(totalRowPseudoClass));
        scs_fTachoTable.setRowFactory(tableView -> new HighlightTotalRow(totalRowPseudoClass));
        scs_fBrpTable.setRowFactory(tableView -> new HighlightTotalRow(totalRowPseudoClass));
        scs_fDqcTable.setRowFactory(tableView -> new HighlightTotalRow(totalRowPseudoClass));
        scs_fPolTable.setRowFactory(tableView -> new HighlightTotalRow(totalRowPseudoClass));
        // On Crate Tab
        onCrate_mTachoTable.setRowFactory(tableView -> new HighlightTotalRow(totalRowPseudoClass));
        onCrate_mBrpTable.setRowFactory(tableView -> new HighlightTotalRow(totalRowPseudoClass));
        onCrate_mDqcTable.setRowFactory(tableView -> new HighlightTotalRow(totalRowPseudoClass));
        onCrate_mPolTable.setRowFactory(tableView -> new HighlightTotalRow(totalRowPseudoClass));
        onCrate_fTachoTable.setRowFactory(tableView -> new HighlightTotalRow(totalRowPseudoClass));
        onCrate_fBrpTable.setRowFactory(tableView -> new HighlightTotalRow(totalRowPseudoClass));
        onCrate_fDqcTable.setRowFactory(tableView -> new HighlightTotalRow(totalRowPseudoClass));
        onCrate_fPolTable.setRowFactory(tableView -> new HighlightTotalRow(totalRowPseudoClass));
    }

    @FXML
    private void print() {
        WritableImage image;

        if (tabOnShelf.isSelected()) {
            image = gridOnShelf.snapshot(new SnapshotParameters(), null);
        } else if (tabInCrate.isSelected()) {
            image = gridInCrate.snapshot(new SnapshotParameters(), null);
        } else {
            image = gridFirstUci.snapshot(new SnapshotParameters(), null);
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("_ddMMyyyy_HHmmss");
        LocalDateTime now = LocalDateTime.now();
        File file = new File("C:\\temp\\snapshot" + dtf.format(now) + ".png");

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
                lblPrint.setText("Image sent to printer.");
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
        try {
            file.delete();
        } catch (Exception ex) {
            LOGGER.error("Unable to delete image file.");
            Platform.runLater(() -> {
                lblPrint.setText("Unable to print image.");
                ErrorHandler.ErrorMsg("File Delete Error", "The file " + file.getAbsolutePath() + " was not deleted.", "Please be patient while we fix the issue.");
            });
        }

        displayMessage();
    }

    private void displayMessage() {
        lblPrint.setOpacity(1);
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(3), lblPrint);
        fadeTransition.setDelay(Duration.seconds(4));
        fadeTransition.setFromValue(0.99);
        fadeTransition.setToValue(0.0);
        fadeTransition.play();
    }

    @FXML
    private void save() {
        // BUILD DATA MAP
        Map<TableName, List<CardData>> dataMap = new HashMap<>();
        // IN VAULT
        dataMap.put(TableName.INVAULT_TACHO, ListUtils.union(scs_mTachoTable.getItems(), scs_fTachoTable.getItems()));
        dataMap.put(TableName.INVAULT_BRP, ListUtils.union(scs_mBrpTable.getItems(), scs_fBrpTable.getItems()));
        dataMap.put(TableName.INVAULT_POL, ListUtils.union(scs_mPolTable.getItems(), scs_fPolTable.getItems()));
        dataMap.put(TableName.INVAULT_DQC, ListUtils.union(scs_mDqcTable.getItems(), scs_fDqcTable.getItems()));
        
        // IN CRATE
        dataMap.put(TableName.INCRATE_TACHO, ListUtils.union(onCrate_mTachoTable.getItems(), onCrate_fTachoTable.getItems()));    
        dataMap.put(TableName.INCRATE_BRP, ListUtils.union(onCrate_mBrpTable.getItems(), onCrate_fBrpTable.getItems()));
        dataMap.put(TableName.INCRATE_POL, ListUtils.union(onCrate_mPolTable.getItems(), onCrate_fPolTable.getItems()));
        dataMap.put(TableName.INCRATE_DQC, ListUtils.union(onCrate_mDqcTable.getItems(), onCrate_fDqcTable.getItems()));
        
        // FIRST UCI
        dataMap.put(TableName.UCI_TACHO, ListUtils.union(uci_mTachoTable.getItems(), uci_fTachoTable.getItems()));
        dataMap.put(TableName.UCI_BRP, ListUtils.union(uci_mBrpTable.getItems(), uci_fBrpTable.getItems()));
        dataMap.put(TableName.UCI_POL, ListUtils.union(uci_mPolTable.getItems(), uci_fPolTable.getItems()));
        dataMap.put(TableName.UCI_DQC, ListUtils.union(onCrate_mPolTable.getItems(), onCrate_fPolTable.getItems()));
        
        // SAVE TO EXCEL SPREADSHEET
        System.out.println("HERE!3");
        Spreadsheet s = new Spreadsheet(dataMap);
        System.out.println("HERE4!");
        s.save();
        System.out.println("HERE!5");
        // Update label
        lblPrint.setText("Saved as Excel spreadsheet!");
        displayMessage();
    }

    /**
     * user is logged out and application shut down.
     */
    public void logout() {
        // contact RPD on background thread to prevent main window from freezing
        new Thread(() -> {
            Platform.runLater(() -> {
                LogOut.logout(NetworkConfig.getInstance());
            });
        }).start();
    }
}

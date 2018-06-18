package uk.gov.dvla.osg.vault.mainform;

import java.lang.management.ManagementFactory;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import uk.gov.dvla.osg.rpd.client.VaultStockClient;
import uk.gov.dvla.osg.rpd.error.BadResponseModelXml;
import uk.gov.dvla.osg.rpd.json.JsonUtils;
import uk.gov.dvla.osg.vault.data.CardData;
import uk.gov.dvla.osg.vault.data.VaultStock;
import uk.gov.dvla.osg.vault.enums.CardClass;
import uk.gov.dvla.osg.vault.enums.Site;
import uk.gov.dvla.osg.vault.error.ErrorHandler;
import uk.gov.dvla.osg.vault.login.LogOut;
import uk.gov.dvla.osg.vault.main.NetworkConfig;
import uk.gov.dvla.osg.vault.session.Session;

public class MainFormController {
/*    private final class UpdateTotalRow extends TableRow<CardData> {
        private final PseudoClass totalRowPseudoClass;

        private UpdateTotalRow(PseudoClass totalRowPseudoClass) {
            this.totalRowPseudoClass = totalRowPseudoClass;
        }

        @Override
        protected void updateItem(CardData person, boolean b) {
            super.updateItem(person, b);
            boolean isTotalRow = person.getCardType().equals("TOTAL") ;
            pseudoClassStateChanged(totalRowPseudoClass, isTotalRow);
        }
    }*/

    private static final Logger LOGGER = LogManager.getLogger();
    private final boolean DEBUG_MODE = ManagementFactory.getRuntimeMXBean().getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;

    // TOP SECTION
    @FXML
    private ChoiceBox environmentChoice;
    @FXML
    private ChoiceBox siteChoice;
    @FXML
    private ChoiceBox cardChoice;
    @FXML
    private Label lblTime;
    @FXML
    private Button refreshBtn;

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
        setRefreshButtonImage();
        setupRefreshBtn();        
        loadChoiceBoxes();
        setCellValueFactories();
        // Load Json File
        //refreshJson();
        highlightTotals();
        refreshBtn.fire();
    }

    private void setRefreshButtonImage() {
        Image buttonImage = new Image(getClass().getResourceAsStream("/Images/refresh.png"));
        refreshBtn.setGraphic(new ImageView(buttonImage));
    }

    @FXML
    private void refreshJson() {
        vaultStock = loadJsonData();
        if (vaultStock != null) {
            refreshData();
        }
        updateTimeLabel();
        
    }

    private void refreshData() {
        if (vaultStock != null) {
            if (environmentChoice.getSelectionModel().getSelectedItem().equals("TEST")) {
                dataHandler = new DataHandler(vaultStock.getStockTotals().getTest());
            } else {
                dataHandler = new DataHandler(vaultStock.getStockTotals().getProduction());
            }
            setupTableData();
        }
    }

    private VaultStock loadJsonData() {
        try {
            String file = "";
            if (DEBUG_MODE) {
                // file = "C:\\Users\\OSG\\Desktop\\Raw Json.txt";
                // file = "C:\\Users\\OSG\\Desktop\\NoTest.json";
                file = "C:\\Users\\OSG\\Desktop\\live-vault-json.json";
                // file = "{\"stockTotals\":{\"test\":
                // null,\"production\":{\"cardStock\":{\"firstUCI\":\"RG9963289\",\"cardTypeName\":\"BID\",\"volumes\":[{\"content\":\"0\",\"status\":\"InVault\"},{\"content\":\"0\",\"status\":\"Quarantined\"},{\"content\":\"212\",\"status\":\"Opened\"},{\"content\":\"0\",\"status\":\"OnCrate\"},{\"content\":\"0\",\"status\":\"InTransit\"}],\"className\":\"BID\",\"location\":\"m\"}}}}";
                return JsonUtils.loadStockFile(file);
            } else {
                VaultStockClient vsc = new VaultStockClient(NetworkConfig.getInstance());
                Optional<VaultStock> vs = vsc.getStock(Session.getInstance().getToken());
                if (vs.isPresent()) {
                    return vs.get();
                } else {
                    BadResponseModelXml brm = vsc.getErrorResponse();
                    ErrorHandler.ErrorMsg(brm.getMessage().getCode(), brm.getMessage().getMessage(), brm.getMessage().getAction());
                }
            }
        } catch (Exception ex) {
            // Display error msg dialog box to user
            LOGGER.fatal(ExceptionUtils.getStackTrace(ex));
            ErrorHandler.ErrorMsg(ex.getClass().getSimpleName(), ex.getMessage());
        }
        return null;
    }

    private void updateTimeLabel() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        lblTime.setText(dtf.format(now));
    }

    private void loadChoiceBoxes() {
        ObservableList<String> environmentList = FXCollections.observableArrayList("PRODUCTION", "TEST");
        ObservableList<String> siteList = FXCollections.observableArrayList("BOTH", "COMBINED");
        ObservableList<String> cardList = FXCollections.observableArrayList("ALL", "TACHO", "BRP", "POL", "DQC");

        environmentChoice.setValue(environmentList.get(0));
        environmentChoice.setItems(environmentList);
        environmentChoice.getSelectionModel().selectedIndexProperty().addListener((ChangeListener) (observable, oldValue, newValue) -> {
            environmentChoice.getSelectionModel().select((int) newValue);
            refreshData();
        });

        siteChoice.setValue(siteList.get(0));
        siteChoice.setItems(siteList);
        siteChoice.getSelectionModel().selectedIndexProperty().addListener((ChangeListener) (observable, oldValue, newValue) -> {
            siteChoice.getSelectionModel().select((int) newValue);
        });

        cardChoice.setValue(cardList.get(0));
        cardChoice.setItems(cardList);
        cardChoice.getSelectionModel().selectedIndexProperty().addListener((ChangeListener) (observable, oldValue, newValue) -> {
            cardChoice.getSelectionModel().select((int) newValue);
        });
    }

    private void setupTableData() {
        //setupTotalRows();
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

    private void setupRefreshBtn() {
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
                        refreshData();
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

    /**
     * Files older than 7 days are deleted from the temp dir & then the user is
     * logged out and application shut down.
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

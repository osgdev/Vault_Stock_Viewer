package uk.gov.dvla.osg.vault.viewer;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import uk.gov.dvla.osg.vault.data.CardStock;
import uk.gov.dvla.osg.vault.data.Environment;
import uk.gov.dvla.osg.vault.data.StockTotals;
import uk.gov.dvla.osg.vault.data.VaultStock;

public class JsonReader {
    
    private static final Logger LOGGER = LogManager.getLogger();
    
    /**
     * Deserializes the Json read from the supplied file.
     * @param jsonFile File retrieved from the Vault WebService.
     * @return Stock information from the Vault.
     * @throws Exception if there was a syntax error in the Json.
     */
    public static VaultStock LoadStockFile(String jsonFile) throws Exception {
        try {
            return new Gson().fromJson(new FileReader(jsonFile), VaultStock.class);
        } catch (JsonSyntaxException | JsonIOException | FileNotFoundException ex) {
            LOGGER.fatal(ex.getMessage());
            throw ex;
        }
    }
    
    public static void main(String[] args) throws Exception {
       String file = "C:\\Users\\OSG\\Test Data\\live-vault-json.json";
       
       VaultStock vaultStock = LoadStockFile(file);
       
       StockTotals stockTotals = vaultStock.getStockTotals();       
       Environment production = stockTotals.getProduction();
       List<CardStock> cardStockProd = production.getCardStock();
       

       cardStockProd.stream()
           .forEachOrdered(card -> {
               LOGGER.trace("", card.getCardClass(), card.getCardTypeName(), card.getSite(), card.getFirstUCI());
               card.getVolumes().stream()
                   .filter(status -> status.getStatus().equals("InVault") || status.getStatus().equals("Opened")).forEach(volume -> {
                       LOGGER.trace("{} : {} : {} : {} : {} : {}", card.getCardClass(), card.getCardTypeName(), card.getSite(), card.getFirstUCI(), volume.getStatus(), volume.getContent());
               });
           });
    }
}

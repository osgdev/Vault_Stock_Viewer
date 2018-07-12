package uk.gov.dvla.osg.vault.mainform;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import uk.gov.dvla.osg.vault.data.*;
import uk.gov.dvla.osg.vault.enums.CardClass;
import uk.gov.dvla.osg.vault.enums.Site;
import uk.gov.dvla.osg.vault.enums.Status;

public class DataHandler {

    private final int TOTALROW = 6;
    
    private Environment model;

    /**
     * Instantiates a new data handler.
     *
     * @param model the model
     */
    public DataHandler(Environment model) {
        this.model = model;
    }

    /**
     * Gets the on shelf data for both sites. 
     * The On Shelf volume is the sum of items having a status of invault and opened.
     *
     * @param cardClass the card class
     * @param site the site
     * @return the on shelf data for the provided site
     */
    public ObservableList<CardData> getOnShelfDataForSingleSite(CardClass cardClass, Site site) {
        Predicate<? super CardStock> sitePredicate = card -> card.getSite().equals(site);
        Predicate<? super Volume> volPredicate = volume -> volume.getStatus().equals(Status.INVAULT) || volume.getStatus().equals(Status.OPENED);
        return getData(cardClass, sitePredicate, volPredicate, true);
    }

    /**
     * Gets the on crate data for both sites.
     * The On Crate volume is the sum of items having a status of OnCrate.
     *
     * @param cardClass the card class
     * @param site the site
     * @return the on crate data for the provided site
     */
    public ObservableList<CardData> getOnCrateDataForSingleSite(CardClass cardClass, Site site) {
        Predicate<? super CardStock> sitePredicate = card -> card.getSite().equals(site);
        Predicate<? super Volume> volPredicate = volume -> volume.getStatus().equals(Status.ONCRATE);
        return getData(cardClass, sitePredicate, volPredicate, true);
    }

    /**
     * Gets the all stock data for both sites.
     * The All Stock volume is the sum of items having a status of invault, opened and oncrate, ignoring other statuses.
     *
     * @param cardClass the card class
     * @param site the site
     * @return the all stock data for the provided site
     */
    public ObservableList<CardData> getAllStockDataForSingleSite(CardClass cardClass, Site site) {
        Predicate<? super CardStock> sitePredicate = card -> card.getSite().equals(site);
        Predicate<? super Volume> volPredicate = volume -> volume.getStatus().equals(Status.ONCRATE) || volume.getStatus().equals(Status.INVAULT) || volume.getStatus().equals(Status.OPENED);
        return getData(cardClass, sitePredicate, volPredicate, true);
    }
    
    /**
     * Gets the on shelf data for both sites combined.
     * The On Shelf volume is the sum of items having a status of invault and opened.
     *
     * @param cardClass the card class
     * @return the on shelf data for combined
     */
    public ObservableList<CardData> getOnShelfDataForCombined(CardClass cardClass) {
        Predicate<? super CardStock> sitePredicate = card -> true;
        Predicate<? super Volume> volPredicate = volume -> volume.getStatus().equals(Status.INVAULT) || volume.getStatus().equals(Status.OPENED);
        return getData(cardClass, sitePredicate, volPredicate, true);
    }

    /**
     * Gets the on crate data for combined.
     * The On Crate volume is the sum of items having a status of OnCrate.
     *
     * @param cardClass the card class
     * @return the on crate data for combined
     */
    public ObservableList<CardData> getOnCrateDataForCombined(CardClass cardClass) {
        Predicate<? super CardStock> sitePredicate = card -> true;
        Predicate<? super Volume> volPredicate = volume -> volume.getStatus().equals(Status.ONCRATE);
        return getData(cardClass, sitePredicate, volPredicate, true);
    }
    
    /**
     * Gets the all stock data for combined.
     * The All Stock volume is the sum of items having a status of invault, opened and oncrate, ignoring other statuses.
     *
     * @param cardClass the card class
     * @return the all stock data for combined
     */
    public ObservableList<CardData> getAllStockDataForCombined(CardClass cardClass) {
        Predicate<? super CardStock> sitePredicate = card -> true;
        Predicate<? super Volume> volPredicate = volume -> volume.getStatus().equals(Status.INVAULT) || volume.getStatus().equals(Status.OPENED) || volume.getStatus().equals(Status.ONCRATE);
        return getData(cardClass, sitePredicate, volPredicate, true);
    }
    
    /**
     * Gets the uci data for single site.
     *
     * @param cardClass the card class
     * @param site the site
     * @return the uci data for single site
     */
    public ObservableList<CardData> getUciDataForSingleSite(CardClass cardClass, Site site) {
        Predicate<? super Volume> volPredicate = volume -> true;
        Predicate<? super CardStock> sitePredicate = card -> card.getSite().equals(site);
        return getData(cardClass, sitePredicate, volPredicate, false);
    }

    private CardData data = null;
    
    private ObservableList<CardData> getData(CardClass cardClass, Predicate<? super CardStock> sitePredicate, 
            Predicate<? super Volume> filterPredicate, boolean totalRequired) {

        ObservableList<CardData> cardDataList = FXCollections.observableArrayList();
        AtomicInteger totalVolume = new AtomicInteger(0);
        Set<String> addedCards = new HashSet<>();
        
        // model may be null when there are no cards for environment
        if (model != null) {
            model.getCardStock().stream()
                .filter(card -> card.getCardClass().equals(cardClass))
                .filter(sitePredicate)
                .forEachOrdered(card -> {   
                    
                    if (addedCards.add(card.getCardType())) {
                        // Card Type has not already been added so create a new Card Data entry
                        data = new CardData(card.getCardType(), card.getFirstUCI(), card.getSite().name());
                    } else {
                        /* There is a matching Card Type in the list, so we remove it from the list and replace it with
                         * a new entry with the increased total volume. */
                        for (CardData cd : cardDataList) {
                            if (cd.getCardType().equals(card.getCardType())) {
                                data = cd;
                            }
                        }
                        cardDataList.remove(data);
                   }
                   
                    /* Increment the total volume for each status matching the predicate 
                     * This will be used as the value for the Total Row */
                   card.getVolumes().stream().filter(filterPredicate).forEach(volume -> {
                        data.increaseVolume(volume.getContent());
                        totalVolume.addAndGet(volume.getContent());
                    });
                    
                   // Add the data to the list only if the volume is greater than 0
                    if (!data.getVolume().equals("")) {
                        cardDataList.add(data);
                    }
            });           
        }
        
        // Add a Total Row if not a UCI table
        if (cardDataList.size() > 0 && totalRequired) {
            // Add empty rows
            int rowsToFill = TOTALROW - cardDataList.size();
                for (int i = 0; i < rowsToFill; i++) {
                    cardDataList.add(new CardData());
                } 
            // Add a total Row to the end of the table data
            CardData total = new CardData("TOTAL");
            total.increaseVolume(totalVolume.get());
            cardDataList.add(total);
        }
        return cardDataList;
    }

}

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

    public DataHandler(Environment model) {
        this.model = model;
    }

    public ObservableList<CardData> getOnShelfDataForBothSites(CardClass cardClass, Site site) {
        Predicate<? super CardStock> sitePredicate = card -> card.getSite().equals(site);
        Predicate<? super Volume> volPredicate = volume -> volume.getStatus().equals(Status.INVAULT) || volume.getStatus().equals(Status.OPENED);
        return getData(cardClass, sitePredicate, volPredicate, true);
    }

    public ObservableList<CardData> getOnCrateDataForBothSites(CardClass cardClass, Site site) {
        Predicate<? super CardStock> sitePredicate = card -> card.getSite().equals(site);
        Predicate<? super Volume> volPredicate = volume -> volume.getStatus().equals(Status.ONCRATE);
        return getData(cardClass, sitePredicate, volPredicate, true);
    }

    public ObservableList<CardData> getAllStockDataForBothSites(CardClass cardClass, Site site) {
        Predicate<? super CardStock> sitePredicate = card -> card.getSite().equals(site);
        Predicate<? super Volume> volPredicate = volume -> volume.getStatus().equals(Status.ONCRATE) || volume.getStatus().equals(Status.INVAULT) || volume.getStatus().equals(Status.OPENED);
        return getData(cardClass, sitePredicate, volPredicate, true);
    }
    
    public ObservableList<CardData> getOnShelfDataForCombined(CardClass cardClass) {
        Predicate<? super CardStock> sitePredicate = card -> true;
        Predicate<? super Volume> volPredicate = volume -> volume.getStatus().equals(Status.INVAULT) || volume.getStatus().equals(Status.OPENED);
        return getData(cardClass, sitePredicate, volPredicate, true);
    }

    public ObservableList<CardData> getOnCrateDataForCombined(CardClass cardClass) {
        Predicate<? super CardStock> sitePredicate = card -> true;
        Predicate<? super Volume> volPredicate = volume -> volume.getStatus().equals(Status.ONCRATE);
        return getData(cardClass, sitePredicate, volPredicate, true);
    }
    
    public ObservableList<CardData> getAllStockDataForCombined(CardClass cardClass) {
        Predicate<? super CardStock> sitePredicate = card -> true;
        Predicate<? super Volume> volPredicate = volume -> volume.getStatus().equals(Status.INVAULT) || volume.getStatus().equals(Status.OPENED) || volume.getStatus().equals(Status.ONCRATE);
        return getData(cardClass, sitePredicate, volPredicate, true);
    }
    
    public ObservableList<CardData> getUciData(CardClass cardClass, Site site) {
        Predicate<? super Volume> volPredicate = volume -> true;
        Predicate<? super CardStock> sitePredicate = card -> card.getSite().equals(site);
        return getData(cardClass, sitePredicate, volPredicate, false);
    }

    private CardData data = null;
    
    private ObservableList<CardData> getData(CardClass cardClass, Predicate<? super CardStock> sitePredicate, 
            Predicate<? super Volume> filterPredicate, boolean totalRequired) {

        ObservableList<CardData> cardDataList = FXCollections.observableArrayList();
        // model may be null when there are no cards for environment
        AtomicInteger totalVolume = new AtomicInteger(0);
        Set<String> addedCards = new HashSet<>();
        
        if (model != null) {
            model.getCardStock().stream()
                .filter(card -> card.getCardClass().equals(cardClass))
                .filter(sitePredicate)
                .forEachOrdered(card -> {   
                    
                    if (addedCards.add(card.getCardType())) {
                        data = new CardData(card.getCardType(), card.getFirstUCI(), card.getSite().name());
                    } else {
                        for (CardData cd : cardDataList) {
                            if (cd.getCardType().equals(card.getCardType())) {
                                data = cd;
                            }
                        }
                        cardDataList.remove(data);
                   }
                    
                   card.getVolumes().stream().filter(filterPredicate).forEach(volume -> {
                        data.increaseVolume(volume.getContent());
                        totalVolume.addAndGet(volume.getContent());
                    });
                    
                    if (!data.getVolume().equals("")) {
                        cardDataList.add(data);
                    }
            });           
        }
        if (cardDataList.size() > 0 && totalRequired) {
            int rowsToFill = TOTALROW - cardDataList.size();
                for (int i = 0; i < rowsToFill; i++) {
                    cardDataList.add(new CardData());
                } 
            CardData total = new CardData("TOTAL", "", "");
            total.increaseVolume(totalVolume.get());
            cardDataList.add(total);
        }
        return cardDataList;
    }

}

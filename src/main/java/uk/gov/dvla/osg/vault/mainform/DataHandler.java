package uk.gov.dvla.osg.vault.mainform;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import uk.gov.dvla.osg.vault.data.CardData;
import uk.gov.dvla.osg.vault.data.Environment;
import uk.gov.dvla.osg.vault.data.Volume;
import uk.gov.dvla.osg.vault.enums.CardClass;
import uk.gov.dvla.osg.vault.enums.Site;
import uk.gov.dvla.osg.vault.enums.Status;

public class DataHandler {

    private Environment model;

    public DataHandler(Environment model) {
        this.model = model;
    }

    public ObservableList<CardData> getScsData(CardClass cardClass, Site site) {
        return getData(cardClass, site, volume -> volume.getStatus().equals(Status.INVAULT) || volume.getStatus().equals(Status.OPENED), true);
    }

    public ObservableList<CardData> getOnCrateData(CardClass cardClass, Site site) {
        return getData(cardClass, site, volume -> volume.getStatus().equals(Status.ONCRATE), true);
    }

    public ObservableList<CardData> getUciData(CardClass cardClass, Site site) {
        // Sets filter to true, meaning that it will get the volume for all statuses.
        // This prevents the table from hiding content when the 
        return getData(cardClass, site, volume -> true, false);
    }

    private ObservableList<CardData> getData(CardClass cardClass, Site site, Predicate<? super Volume> filterPredicate, boolean volRequired) {

        ObservableList<CardData> cardDataList = FXCollections.observableArrayList();
        // model may be null when there are no cards for environment
        AtomicInteger cardCount = new AtomicInteger(0);
        AtomicInteger totalVolume = new AtomicInteger(0);
        
        if (model != null) {
            model.getCardStock().stream()
                .filter(card -> card.getCardClass().equals(cardClass))
                .filter(card -> card.getSite().equals(site))
                .forEachOrdered(card -> {
                    CardData data = new CardData(card.getCardTypeName(), card.getFirstUCI());
                    card.getVolumes().stream().filter(filterPredicate).forEach(volume -> {
                        data.increaseVolume(volume.getContent());
                        totalVolume.addAndGet(volume.getContent());
                    });
                    if (!data.getVolume().equals("")) {
                        cardDataList.add(data);
                        cardCount.incrementAndGet();
                    }
            });           
        }
        if (cardCount.get() > 0 && volRequired) {    
            for (int i = cardCount.get(); i < 6; i++) {
                cardDataList.add(new CardData("", ""));
            }
            CardData total = new CardData("TOTAL", "");
            total.increaseVolume(totalVolume.get());
            cardDataList.add(total);
        }
        return cardDataList;
    }
}

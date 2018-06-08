package uk.gov.dvla.osg.vault.mainform;

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
        return getData(cardClass, site, volume -> volume.getStatus().equals(Status.INVAULT) || volume.getStatus().equals(Status.OPENED));
    }
    
    public ObservableList<CardData> getOnCrateData(CardClass cardClass, Site site) {
        return getData(cardClass, site, volume -> volume.getStatus().equals(Status.ONCRATE));
     }
     
    public ObservableList<CardData> getUciData(CardClass cardClass, Site site) {
          return getData(cardClass, site, volume -> volume.getStatus().equals(Status.NONE));
      }
    
    private ObservableList<CardData> getData(CardClass cardClass, Site site, Predicate<? super Volume> filterPredicate) {
        
        ObservableList<CardData> cardDataList = FXCollections.observableArrayList();

         model.getCardStock().stream()
             .filter(card -> card.getCardClass().equals(cardClass))
             .filter(card -> card.getSite().equals(site))
             .forEachOrdered(card -> {
                 CardData data = new CardData(card.getCardTypeName(), card.getFirstUCI());
                 card.getVolumes().stream()
                 .filter(filterPredicate)
                 .forEach(volume -> {
                     data.increaseVolume(volume.getContent());
                 });
                 cardDataList.add(data);
             });
         return cardDataList;
     }
}

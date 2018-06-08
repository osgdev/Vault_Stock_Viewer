package uk.gov.dvla.osg.vault.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import uk.gov.dvla.osg.vault.data.CardData;
import uk.gov.dvla.osg.vault.data.Environment;

public class DataHandler {

    private Environment model;

    public DataHandler(Environment model) {
        this.model = model;
    }
    
    public ObservableList<CardData> getScsData(String cardType, String location) {
        
        ObservableList<CardData> cardDataList = FXCollections.observableArrayList();
        
        model.getCardStock().stream()
            .filter(card -> card.getClassName().equalsIgnoreCase(cardType))
            .filter(card -> card.getLocation().equalsIgnoreCase(location))
            .forEachOrdered(card -> {
                CardData data = new CardData(card.getCardTypeName());
                card.getVolumes().stream()
                    .filter(status -> status.getStatus().equalsIgnoreCase("InVault") || status.getStatus().equalsIgnoreCase("Opened"))
                    .forEach(volume -> {
                        data.increaseVolume(volume.getContent());
                });
                cardDataList.add(data);
            });
        return cardDataList;
    }
    
    public ObservableList<CardData> getOnCrateData(String cardType, String location) {
        
        ObservableList<CardData> cardDataList = FXCollections.observableArrayList();
         
         model.getCardStock().stream()
             .filter(card -> card.getClassName().equalsIgnoreCase(cardType))
             .filter(card -> card.getLocation().equalsIgnoreCase(location))
             .forEachOrdered(card -> {
                 CardData data = new CardData(card.getCardTypeName());
                 card.getVolumes().stream()
                     .filter(status -> status.getStatus().equalsIgnoreCase("OnCrate"))
                     .forEach(volume -> {
                         data.increaseVolume(volume.getContent());
                 });
                 cardDataList.add(data);
             });
         return cardDataList;
     }
     
    public ObservableList<CardData> getUciData(String cardType, String location) {
         
         ObservableList<CardData> cardDataList = FXCollections.observableArrayList();

          model.getCardStock().stream()
              .filter(card -> card.getClassName().equalsIgnoreCase(cardType))
              .filter(card -> card.getLocation().equalsIgnoreCase(location))
              .forEachOrdered(card -> {
                  CardData data = new CardData(card.getCardTypeName(), card.getFirstUCI());
                  cardDataList.add(data);
              });
          return cardDataList;
      }
}

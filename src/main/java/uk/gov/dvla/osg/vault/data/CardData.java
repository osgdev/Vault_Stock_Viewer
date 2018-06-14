package uk.gov.dvla.osg.vault.data;

public class CardData {

    public final String cardType;
    public Integer volume;
    public final String uci;
    
    public CardData() {
        this("");
    }
    
    public CardData(String cardType) {
        this(cardType, 0);
    }
    
    public CardData(String cardType, Integer volume) {
        this(cardType, volume, "");
    }

    public CardData(String cardType, String uci) {
        this(cardType, 0, uci);
    }
    
    public CardData(String cardType, Integer volume, String uci) {
        this.cardType = cardType;
        this.volume = volume;
        this.uci = uci;
    }

    public void increaseVolume(int amount) {
        volume += amount;
    }
    
    /**
     * @return the cardType
     */
    public String getCardType() {
        return cardType;
    }
    
    /**
     * @return the volume
     */
    public String getVolume() {
        return this.volume == 0 ? "" : String.format("%,d", volume);
        
    }
    
    /**
     * @return the uci
     */
    public String getUci() {
        return uci;
    }
    
    
}

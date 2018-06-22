package uk.gov.dvla.osg.vault.enums;

public enum TableName {
    // IN VAULT
    INVAULT_TACHO ("TACHO"),
    INVAULT_BRP ("BRP"),
    INVAULT_POL ("POL"),
    INVAULT_DQC ("DQC"),

    // IN CRATE
    INCRATE_TACHO ("TACHO"),
    INCRATE_BRP ("BRP"),
    INCRATE_POL ("POL"),
    INCRATE_DQC ("DQC"),

    // FIRST UCI
    UCI_TACHO ("TACHO"),
    UCI_BRP ("BRP"),
    UCI_POL ("POL"),
    UCI_DQC ("DQC");
    
    private final String columnName;

    private TableName(String columnName) {
        this.columnName = columnName;
    }
    
    public String getColumnName() {
        return this.columnName;
    }
}

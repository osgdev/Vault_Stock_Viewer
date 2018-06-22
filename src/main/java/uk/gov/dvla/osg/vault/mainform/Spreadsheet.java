package uk.gov.dvla.osg.vault.mainform;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import uk.gov.dvla.osg.vault.data.CardData;
import uk.gov.dvla.osg.vault.enums.TableName;

public class Spreadsheet {

    private final Map<TableName, List<CardData>> dataMap;
    
    Spreadsheet(Map<TableName, List<CardData>> dataMap) {
        this.dataMap = dataMap;
    }
    
    void save() {
        // Blank workbook
        XSSFWorkbook workbook = new XSSFWorkbook();
        
        // InVault_Both sheet
        XSSFSheet sheet_IVB = workbook.createSheet("In Vault - Both");
        //Create 14 rows for sheet
        ArrayList<XSSFRow> sheet_IVB_rows = createRows(sheet_IVB);
        // Add INVAULT_BOTH sheet
        addData(TableName.INVAULT_TACHO, sheet_IVB, sheet_IVB_rows, 0);
        addData(TableName.INVAULT_BRP, sheet_IVB, sheet_IVB_rows, 4);
        addData(TableName.INVAULT_POL, sheet_IVB, sheet_IVB_rows, 8);
        addData(TableName.INVAULT_DQC, sheet_IVB, sheet_IVB_rows, 12);
        addTotals(sheet_IVB, sheet_IVB_rows);
        autoSize(sheet_IVB);
        
        // InCrate_Both sheet
        XSSFSheet sheet_ICB = workbook.createSheet("In Crate - Both");
        //Create 14 rows for sheet
        ArrayList<XSSFRow> sheet_ICB_rows = createRows(sheet_ICB);
        // Add INVAULT_BOTH sheet
        addData(TableName.INCRATE_TACHO, sheet_ICB, sheet_ICB_rows, 0);
        addData(TableName.INCRATE_BRP, sheet_ICB, sheet_ICB_rows, 4);
        addData(TableName.INCRATE_POL, sheet_ICB, sheet_ICB_rows, 8);
        addData(TableName.INCRATE_DQC, sheet_ICB, sheet_ICB_rows, 12);
        addTotals(sheet_ICB, sheet_ICB_rows);
        autoSize(sheet_ICB);
        
        // First UCI sheet
        XSSFSheet sheet_UCI = workbook.createSheet("UCI's");
        //Create 14 rows for sheet
        ArrayList<XSSFRow> sheet_UCI_rows = createRows(sheet_UCI);
        // Add INVAULT_BOTH sheet
        addData(TableName.UCI_TACHO, sheet_UCI, sheet_UCI_rows, 0);
        addData(TableName.UCI_BRP, sheet_UCI, sheet_UCI_rows, 4);
        addData(TableName.UCI_POL, sheet_UCI, sheet_UCI_rows, 8);
        addData(TableName.UCI_DQC, sheet_UCI, sheet_UCI_rows, 12);
        autoSize(sheet_UCI);
        
        // Write the workbook in file system
        try {
            FileOutputStream out = new FileOutputStream(new File("cardManagement.xlsx"));
            workbook.write(out);
            out.close();
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private ArrayList<XSSFRow> createRows(XSSFSheet sheet) {
        //Create 14 rows for sheet
        ArrayList<XSSFRow> rows = new ArrayList<XSSFRow>();
        for (int i = 0; i <= 13; i++) {
            rows.add(sheet.createRow(i));
        }
        return rows;
    }
    
    private void addData(TableName tableName, XSSFSheet sheet, ArrayList<XSSFRow> rows, int colStart) {
        
        List<CardData> data = dataMap.get(tableName);
        
        int row = 0;
        
        rows.get(row).createCell(colStart).setCellValue(tableName.getColumnName());
        rows.get(row).createCell(colStart+1).setCellValue("SITE");
        rows.get(row).createCell(colStart+2).setCellValue("VOLUME");
        
        row = 1;
        
        for (CardData card : data) {
            if (card.getVolumeInt() > 0 && !card.getCardType().equals("TOTAL")) {
                rows.get(row).createCell(colStart).setCellValue(card.getCardType());
                rows.get(row).createCell(colStart+1).setCellValue(card.getSite());
                if (tableName.name().startsWith("UCI")) {
                    rows.get(row).createCell(colStart+2).setCellValue(card.getUci());  
                } else {
                    rows.get(row).createCell(colStart+2).setCellValue(card.getVolumeInt());                
                }
                row++;
            }
        }
    

    
    }
    
    private void addTotals(XSSFSheet sheet, ArrayList<XSSFRow> rows) {
        // Add total row
        XSSFRow totalRow = rows.get(rows.size() - 1);
        totalRow.createCell(0).setCellValue("TOTAL");
        totalRow.createCell(2).setCellFormula("SUM(C1:C13)");
        
        totalRow.createCell(4).setCellValue("TOTAL");
        totalRow.createCell(6).setCellFormula("SUM(G1:G13)");
        
        totalRow.createCell(8).setCellValue("TOTAL");
        totalRow.createCell(10).setCellFormula("SUM(K1:K13)");
        
        totalRow.createCell(12).setCellValue("TOTAL");
        totalRow.createCell(14).setCellFormula("SUM(O1:O13)");
    }
    
    private void autoSize(XSSFSheet sheet) {
        // Autosize based on the data just added
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(2);
        
        sheet.autoSizeColumn(4);
        sheet.autoSizeColumn(6);
        
        sheet.autoSizeColumn(8);
        sheet.autoSizeColumn(10);
        
        sheet.autoSizeColumn(12);
        sheet.autoSizeColumn(14);
    }
}

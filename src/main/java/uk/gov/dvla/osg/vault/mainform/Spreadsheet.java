package uk.gov.dvla.osg.vault.mainform;

import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.xssf.usermodel.*;

import uk.gov.dvla.osg.vault.data.CardData;
import uk.gov.dvla.osg.vault.enums.TableName;

public class Spreadsheet {

    private static final int COLUMN_WIDTH = 2500;
    
    private static final XSSFColor LIGHT_BLUE = new XSSFColor(new java.awt.Color(218, 227, 243));
    private static final XSSFColor DARK_BLUE = new XSSFColor(new java.awt.Color(68, 114, 196));
    private static final XSSFColor WHITE = new XSSFColor(new java.awt.Color(255, 255, 255));
    private static final XSSFColor BORDER_COLOR = new XSSFColor(new java.awt.Color(143, 170, 220));
    
    private final Map<TableName, List<CardData>> dataMap;

    /**
     * Constructor
     * @param dataMap The data to be written into the spreadsheet.
     */
    Spreadsheet(Map<TableName, List<CardData>> dataMap) {
        this.dataMap = dataMap;
    }

    /**
     * Creates a workbook. 
     * Adds data to each table and applies styling to each cell.
     * Saves the workbook to the desktop.
     * @throws IOException 
     */
    void save() throws IOException {
        // Blank workbook
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFCellStyle[] style = cellStyle(workbook);

        // InVault sheet
        XSSFSheet sheet_IVB = workbook.createSheet("In Vault");
        createRows(sheet_IVB);
        addData(TableName.INVAULT_TACHO, sheet_IVB, style, 0);
        addData(TableName.INVAULT_BRP, sheet_IVB, style, 4);
        addData(TableName.INVAULT_POL, sheet_IVB, style, 8);
        addData(TableName.INVAULT_DQC, sheet_IVB, style, 12);
        addTotalRow(sheet_IVB, style);
        setColumnWidths(sheet_IVB);

        // InCrate sheet
        XSSFSheet sheet_ICB = workbook.createSheet("In Crate");
        createRows(sheet_ICB);
        addData(TableName.INCRATE_TACHO, sheet_ICB, style, 0);
        addData(TableName.INCRATE_BRP, sheet_ICB, style, 4);
        addData(TableName.INCRATE_POL, sheet_ICB, style, 8);
        addData(TableName.INCRATE_DQC, sheet_ICB, style, 12);
        addTotalRow(sheet_ICB, style);
        setColumnWidths(sheet_ICB);

        // First UCI sheet
        XSSFSheet sheet_UCI = workbook.createSheet("UCI's");
        createRows(sheet_UCI);
        addData(TableName.UCI_TACHO, sheet_UCI, style, 0);
        addData(TableName.UCI_BRP, sheet_UCI, style, 4);
        addData(TableName.UCI_POL, sheet_UCI, style, 8);
        addData(TableName.UCI_DQC, sheet_UCI, style, 12);
        setColumnWidths(sheet_UCI);

        setPrintArea(workbook);

        // Write the workbook in file system
        try (FileOutputStream out = new FileOutputStream(new File("cardManagement.xlsx"))) {
            workbook.write(out);
            workbook.close();
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException ex) {
            throw ex;
        }
    }

    /**
     * Creates 14 blank rows for sheet, 12 data rows plus the header and total rows. 
     * 
     * @param sheet The sheet on which the rows will be added.
     */
    private void createRows(XSSFSheet sheet) {
        IntStream.range(0, 14).forEach(i -> sheet.createRow(i));
    }

    /**
     * Adds a single table to the supplied sheet at the location specified.
     * @param tableName The table to be added to the sheet.
     * @param sheet The sheet on which the table will be added.
     * @param style A collection of styles to be applied to cells.
     * @param colStart The starting column into which the table will be inserted.
     */
    private void addData(TableName tableName, XSSFSheet sheet, XSSFCellStyle[] style, int colStart) {

        Iterator<CardData> data = dataMap.get(tableName).iterator();

        sheet.rowIterator().forEachRemaining( r -> {

            XSSFRow row = (XSSFRow) r;
            
            XSSFCellStyle cellStyle = style[row.getRowNum() % 2];
            
            // Header Row
            if (row.getRowNum() == 0) {
                XSSFCell cell = row.createCell(colStart);
                cell.setCellValue(tableName.getColumnName());
                cell.setCellStyle(style[2]);

                cell = row.createCell(colStart + 1);
                cell.setCellValue("SITE");
                cell.setCellStyle(style[2]);

                if (tableName.name().startsWith("UCI")) {
                    cell = row.createCell(colStart + 2);
                    cell.setCellValue("UCI");
                } else {
                    cell = row.createCell(colStart + 2);
                    cell.setCellValue("VOLUME");
                }
                cell.setCellStyle(style[2]);
                // Data on all but final row (Total row)
            } else if (row.getRowNum() < sheet.getLastRowNum()) {
                if (data.hasNext()) {
                    CardData card = data.next();

                    if (card.getVolumeInt() > 0) {
                        XSSFCell cell = row.createCell(colStart);
                        cell.setCellValue(card.getCardType());
                        cell.setCellStyle(cellStyle);

                        cell = row.createCell(colStart + 1);
                        cell.setCellValue(card.getSite());
                        cell.setCellStyle(cellStyle);
        
                        if (tableName.name().startsWith("UCI")) {
                            cell = row.createCell(colStart + 2);
                            cell.setCellValue(card.getUci());
                            cell.setCellStyle(cellStyle);
                        } else {
                            cell = row.createCell(colStart + 2);
                            cell.setCellValue(card.getVolumeInt());
                            cell.setCellStyle(cellStyle);
                        }
                    }
                } else {
                    XSSFCell cell = row.createCell(colStart);
                    cell.setCellStyle(cellStyle);
                    cell = row.createCell(colStart + 1);
                    cell.setCellStyle(cellStyle);
                    cell = row.createCell(colStart + 2);
                    cell.setCellStyle(cellStyle);
                }
            }
        });

    }

    /**
     * Adds a total row to the sheet with a Sum() formula being written to 
     * the bottom of each table.
     * @param sheet The sheet to receive a Total Row
     * @param style The applied style will depend on whether the row is odd or even.
     */
    private void addTotalRow(XSSFSheet sheet, XSSFCellStyle[] style) {
        // Add total row
        int last = sheet.getLastRowNum();
        XSSFRow totalRow = sheet.getRow(last);
        XSSFCellStyle cellStyle = style[totalRow.getRowNum() % 2];

        XSSFCell cell = totalRow.createCell(0);
        cell.setCellValue("TOTAL");
        cell.setCellStyle(cellStyle);

        cell = totalRow.createCell(1);
        cell.setCellStyle(cellStyle);

        cell = totalRow.createCell(2);
        cell.setCellFormula("SUM(C1:C13)");
        cell.setCellStyle(cellStyle);

        cell = totalRow.createCell(4);
        cell.setCellValue("TOTAL");
        cell.setCellStyle(cellStyle);

        cell = totalRow.createCell(5);
        cell.setCellStyle(cellStyle);

        cell = totalRow.createCell(6);
        cell.setCellFormula("SUM(G1:G13)");
        cell.setCellStyle(cellStyle);

        cell = totalRow.createCell(8);
        cell.setCellValue("TOTAL");
        cell.setCellStyle(cellStyle);

        cell = totalRow.createCell(9);
        cell.setCellStyle(cellStyle);

        cell = totalRow.createCell(10);
        cell.setCellFormula("SUM(K1:K13)");
        cell.setCellStyle(cellStyle);

        cell = totalRow.createCell(12);
        cell.setCellValue("TOTAL");
        cell.setCellStyle(cellStyle);
        
        cell = totalRow.createCell(13);
        cell.setCellStyle(cellStyle);

        cell = totalRow.createCell(14);
        cell.setCellFormula("SUM(O1:O13)");
        cell.setCellStyle(cellStyle);
    }

    /**
     * Apply a standard width to all columns.
     * @param sheet The sheet on which column widths will be set.
     */
    private void setColumnWidths(XSSFSheet sheet) {
        IntStream.rangeClosed(0, 14).forEach(i -> sheet.setColumnWidth(i, COLUMN_WIDTH));
    }

    /**
     * Sets the print area for each sheet in the workbook and sets each sheet to 
     * print one page wide by one page tall.
     * @param workbook The workbook on which the printarea will be set.
     */
    private void setPrintArea(XSSFWorkbook workbook) {        
        workbook.sheetIterator().forEachRemaining(sheet -> {
            int index = sheet.getWorkbook().getSheetIndex(sheet.getSheetName());
            sheet.getWorkbook().setPrintArea(index, 0, 14, 0, 14);
            sheet.getPrintSetup().setFitHeight((short) 1);
            sheet.getPrintSetup().setFitWidth((short) 1);
        });
    }

    /**
     * Creates an array of styles, where the 0 index is applied to even
     * rows, the 1 index is applied to odd rows and index 3 is the 
     * style for the header row.
     * @param workbook The workbook in which the styles will be applied.
     */
    private XSSFCellStyle[] cellStyle(XSSFWorkbook workbook) {
        XSSFCellStyle[] style = new XSSFCellStyle[3];
        
        // Base common to all three styles
        XSSFCellStyle baseStyle = workbook.createCellStyle();
        
        baseStyle.setBorderBottom(BorderStyle.THIN);
        baseStyle.setBottomBorderColor(BORDER_COLOR);
        baseStyle.setBorderLeft(BorderStyle.THIN);
        baseStyle.setLeftBorderColor(BORDER_COLOR);
        baseStyle.setBorderRight(BorderStyle.THIN);
        baseStyle.setRightBorderColor(BORDER_COLOR);
        baseStyle.setBorderTop(BorderStyle.THIN);
        baseStyle.setTopBorderColor(BORDER_COLOR);
        baseStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        // EVEN ROW
        XSSFCellStyle s = workbook.createCellStyle();
        s.cloneStyleFrom(baseStyle);
        s.setFillForegroundColor(LIGHT_BLUE);
        style[0] = s;
        
        // ODD ROw
        s = workbook.createCellStyle();
        s.cloneStyleFrom(baseStyle);
        s.setFillForegroundColor(WHITE);
        style[1] = s;
        
        // HEADER ROW
        s = workbook.createCellStyle();
        s.cloneStyleFrom(baseStyle);
        XSSFFont font = workbook.createFont();
        font.setColor(WHITE);
        font.setBold(true);
        s.setFont(font);
        s.setFillForegroundColor(DARK_BLUE);
        style[2] = s;
        
        return style;
    }
}

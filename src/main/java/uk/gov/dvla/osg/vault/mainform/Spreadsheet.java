package uk.gov.dvla.osg.vault.mainform;

import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.*;

import uk.gov.dvla.osg.vault.data.CardData;
import uk.gov.dvla.osg.vault.enums.TableName;

public class Spreadsheet {

    private static final int COLUMN_WIDTH = 2500;
    
    private static final XSSFColor LIGHT_BLUE = new XSSFColor(new java.awt.Color(218, 227, 243));
    private static final XSSFColor DARK_BLUE = new XSSFColor(new java.awt.Color(68, 114, 196));
    private static final XSSFColor WHITE = new XSSFColor(new java.awt.Color(255, 255, 255));
    private static final XSSFColor BORDER_COLOR = new XSSFColor(new java.awt.Color(143, 170, 220));
    
    private XSSFCellStyle style_even;
    private XSSFCellStyle style_odd;
    private XSSFCellStyle style_header;
    private XSSFCellStyle style_even_centre;
    private XSSFCellStyle style_odd_centre;
    private XSSFCellStyle style_header_centre;
    
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
    void save() throws IOException, RuntimeException {
        // Blank workbook
        XSSFWorkbook workbook = new XSSFWorkbook();
        createWorkbookStyles(workbook);

        // InVault sheet
        XSSFSheet sheet_IVB = workbook.createSheet("In Vault");
        createRows(sheet_IVB);
        addData(TableName.INVAULT_TACHO, sheet_IVB, 0);
        addData(TableName.INVAULT_BRP, sheet_IVB, 4);
        addData(TableName.INVAULT_POL, sheet_IVB, 8);
        addData(TableName.INVAULT_DQC, sheet_IVB, 12);
        addTotalRow(sheet_IVB);
        setColumnWidths(sheet_IVB);

        // InCrate sheet
        XSSFSheet sheet_ICB = workbook.createSheet("In Crate");
        createRows(sheet_ICB);
        addData(TableName.INCRATE_TACHO, sheet_ICB, 0);
        addData(TableName.INCRATE_BRP, sheet_ICB, 4);
        addData(TableName.INCRATE_POL, sheet_ICB, 8);
        addData(TableName.INCRATE_DQC, sheet_ICB, 12);
        addTotalRow(sheet_ICB);
        setColumnWidths(sheet_ICB);

        // First UCI sheet
        XSSFSheet sheet_UCI = workbook.createSheet("UCI's");
        createRows(sheet_UCI);
        addData(TableName.UCI_TACHO, sheet_UCI, 0);
        addData(TableName.UCI_BRP, sheet_UCI, 4);
        addData(TableName.UCI_POL, sheet_UCI, 8);
        addData(TableName.UCI_DQC, sheet_UCI, 12);
        setColumnWidths(sheet_UCI);

        setPrintArea(workbook);

        // Write the workbook in file system
        saveWorkbook(workbook);
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
    private void addData(TableName tableName, XSSFSheet sheet, int colStart) throws RuntimeException {

        Iterator<CardData> data = dataMap.get(tableName).iterator();

        sheet.rowIterator().forEachRemaining( r -> {

            // Iterator returns Row, convert to XSSFRow to apply styles
            XSSFRow row = (XSSFRow) r;
            // Switch between odd and even rows
            XSSFCellStyle cellStyle = row.getRowNum() % 2 == 0 ? style_even : style_odd;
            XSSFCellStyle siteStyle = row.getRowNum() % 2 == 0 ? style_even_centre : style_odd_centre;
            
            // Header Row
            if (row.getRowNum() == 0) {
                XSSFCell cell = row.createCell(colStart);
                cell.setCellValue(tableName.getColumnName());
                cell.setCellStyle(style_header);

                cell = row.createCell(colStart + 1);
                cell.setCellValue("SITE");
                cell.setCellStyle(style_header_centre);
                
                if (tableName.name().startsWith("UCI")) {
                    cell = row.createCell(colStart + 2);
                    cell.setCellValue("UCI");
                } else {
                    cell = row.createCell(colStart + 2);
                    cell.setCellValue("VOLUME");
                }
                cell.setCellStyle(style_header);
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
                        cell.setCellStyle(siteStyle);
        
                        if (tableName.name().startsWith("UCI")) {
                            cell = row.createCell(colStart + 2);
                            cell.setCellValue(card.getUci());
                            cell.setCellStyle(cellStyle);
                        } else {
                            cell = row.createCell(colStart + 2);
                            cell.setCellValue(card.getVolumeInt());
                            cell.setCellStyle(cellStyle);
                        }
                    } else {
                        XSSFCell cell = row.createCell(colStart);
                        cell.setCellStyle(cellStyle);
                        cell = row.createCell(colStart + 1);
                        cell.setCellStyle(siteStyle);
                        cell = row.createCell(colStart + 2);
                        cell.setCellStyle(cellStyle);
                    }
                } else {
                    throw new RuntimeException("No data to process!");
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
    private void addTotalRow(XSSFSheet sheet) {
        // Add total row
        int last = sheet.getLastRowNum();
        XSSFRow totalRow = sheet.getRow(last);
        XSSFCellStyle cellStyle = totalRow.getRowNum() % 2 == 0 ? style_even : style_odd;
        
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
     * Creates different styles to be applied to cells within the workbook.
     * @param workbook The workbook in which the styles will be applied.
     */
    private void createWorkbookStyles(XSSFWorkbook workbook) {
        
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
        style_even = workbook.createCellStyle();
        style_even.cloneStyleFrom(baseStyle);
        style_even.setFillForegroundColor(LIGHT_BLUE);

        style_even_centre = workbook.createCellStyle();
        style_even_centre.cloneStyleFrom(style_even);
        style_even_centre.setAlignment(HorizontalAlignment.CENTER);
        
        // ODD ROw
        style_odd = workbook.createCellStyle();
        style_odd.cloneStyleFrom(baseStyle);
        style_odd.setFillForegroundColor(WHITE);
        
        style_odd_centre = workbook.createCellStyle();
        style_odd_centre.cloneStyleFrom(style_odd);
        style_odd_centre.setAlignment(HorizontalAlignment.CENTER);
        
        // HEADER ROW
        style_header = workbook.createCellStyle();
        style_header.cloneStyleFrom(baseStyle);
        XSSFFont font = workbook.createFont();
        font.setColor(WHITE);
        font.setBold(true);
        style_header.setFont(font);
        style_header.setFillForegroundColor(DARK_BLUE);

        // HEADER ROW
        style_header_centre = workbook.createCellStyle();
        style_header_centre.cloneStyleFrom(style_header);
        style_header_centre.setAlignment(HorizontalAlignment.CENTER);
    }
    
    /**
     * Save the workbook to the file system.
     * @param workbook The workbook to save.
     * @throws FileNotFoundException if Workbook is currently open.
     * @throws IOException if unable to save Workbook to file system.
     */
    private void saveWorkbook(XSSFWorkbook workbook) throws FileNotFoundException, IOException {
        try (FileOutputStream out = new FileOutputStream(new File("cardManagement.xlsx"))) {
            workbook.write(out);
            workbook.close();
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException ex) {
            throw ex;
        }
    }
}

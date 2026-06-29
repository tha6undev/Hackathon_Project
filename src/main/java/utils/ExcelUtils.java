package utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;


public class ExcelUtils {

    private static final Logger log = LogManager.getLogger(ExcelUtils.class);

    private static final int COLUMN_TEST_CASE        = 0;
    private static final int COLUMN_EXPECTED_MESSAGE = 1;
    private static final int HEADER_ROW_INDEX        = 0;


    public String getExpectedAlertMessage(String filePath, String testCaseName) {

        String expectedMessage = "";
        FileInputStream fileInputStream = null;
        Workbook workbook = null;

        try {
            fileInputStream = new FileInputStream(filePath);
            workbook = new XSSFWorkbook(fileInputStream);
            Sheet alertDataSheet = workbook.getSheet("AlertData");

            if (alertDataSheet == null) {
                log.error("Sheet named 'AlertData' was not found in the Excel file: " + filePath);
                return expectedMessage;
            }

            int totalRows = alertDataSheet.getLastRowNum();
            log.info("Total rows found in sheet (including header): " + (totalRows + 1));

            for (int rowIndex = HEADER_ROW_INDEX + 1; rowIndex <= totalRows; rowIndex++) {

                Row currentRow = alertDataSheet.getRow(rowIndex);
                if (currentRow == null) continue;

                Cell testCaseCell = currentRow.getCell(COLUMN_TEST_CASE);
                if (testCaseCell == null) continue;

                String cellValue = testCaseCell.getStringCellValue().trim();

                if (cellValue.equalsIgnoreCase(testCaseName.trim())) {
                    Cell messageCell = currentRow.getCell(COLUMN_EXPECTED_MESSAGE);
                    if (messageCell != null) {
                        expectedMessage = messageCell.getStringCellValue().trim();
                        log.info("Found expected message for test case '" + testCaseName + "': " + expectedMessage);
                    }
                    break;
                }
            }

            if (expectedMessage.isEmpty()) {
                log.warn("No data found in Excel for test case: " + testCaseName);
            }

        } catch (IOException e) {
            log.error("Could not open Excel file at: " + filePath);
            log.error("Error details: " + e.getMessage());

        } finally {
            try {
                if (workbook != null) workbook.close();
                if (fileInputStream != null) fileInputStream.close();
            } catch (IOException closeException) {
                log.error("Error while closing Excel file: " + closeException.getMessage());
            }
        }

        return expectedMessage;
    }
}
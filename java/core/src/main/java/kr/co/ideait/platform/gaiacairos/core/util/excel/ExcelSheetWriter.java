package kr.co.ideait.platform.gaiacairos.core.util.excel;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public interface ExcelSheetWriter {
    void writeSheet(XSSFWorkbook workbook);
    String getSheetName(); // 시트 이름도 동적으로 받아서 처리
}

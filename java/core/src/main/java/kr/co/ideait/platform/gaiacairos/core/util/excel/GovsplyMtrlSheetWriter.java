package kr.co.ideait.platform.gaiacairos.core.util.excel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
//import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;

import kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction.resource.ResourceMybatisParam.*;
import kr.co.ideait.platform.gaiacairos.core.util.excel.ExcelUtil.ExcelRow;

/**
 * 관급자재수급계획서 시트 작성
 */
public class GovsplyMtrlSheetWriter implements ExcelSheetWriter {

    private RawGovsplyMtrlItem rawItem;
    private String sheetName;
    private String contractName;

    //[UrF]	Unread field: kr.co.ideait.platform.gaiacairos.core.util.excel.GovsplyMtrlSheetWriter.subtotalCodeIndex
//    private AtomicInteger subtotalCodeIndex = new AtomicInteger(0);

    @Autowired
    public GovsplyMtrlSheetWriter() {
    }

    public GovsplyMtrlSheetWriter(RawGovsplyMtrlItem rawItem, String sheetName, String contractName) {
        this.rawItem = rawItem;
        this.sheetName = sheetName;
        this.contractName = contractName;
    }

    @Override
    public void writeSheet(XSSFWorkbook workbook) {
        // 실제 writeCostSheet 메서드로 위임
        XSSFSheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            sheet = workbook.createSheet(sheetName);
        }

        writeMtrlSheet(sheet, rawItem, contractName, workbook);
    }

    @Override
    public String getSheetName() {
        return sheetName;
    }

    public void writeMtrlSheet(Sheet sheet, RawGovsplyMtrlItem rawItem, String contractName, XSSFWorkbook workbook) {
        Row row;
        Cell cell;

        // === 3. 헤더 행 (연도 & 월) ===
        row = sheet.createRow(3);
        Row monthRow = sheet.createRow(4); // 월 표시할 행

        // "품명", "규격", "단위", "수량" 기본 컬럼
        String[] headers = { "품명", "규격", "단위", "수량" };
        int colIndex = 0;
        for (String header : headers) {
            cell = row.createCell(colIndex);
            cell.setCellValue(header);
            sheet.addMergedRegion(new CellRangeAddress(3, 4, colIndex, colIndex));
            colIndex++;
        }

        // 동적으로 연도 및 월 설정
        int startYear = Integer.parseInt(rawItem.getMinYear());
        int startMonth = Integer.parseInt(rawItem.getMinMonth());
        int endYear = Integer.parseInt(rawItem.getMaxYear());
        int endMonth = Integer.parseInt(rawItem.getMaxMonth());

        for (int year = startYear; year <= endYear; year++) {
            int monthStart = (year == startYear) ? startMonth : 1;
            int monthEnd = (year == endYear) ? endMonth : 12;

            // 연도 헤더
            cell = row.createCell(colIndex);
            cell.setCellValue(year + "년");
            sheet.addMergedRegion(new CellRangeAddress(3, 3, colIndex, colIndex + (monthEnd - monthStart)));

            // 월 헤더
            for (int month = monthStart; month <= monthEnd; month++) {
                cell = monthRow.createCell(colIndex);
                cell.setCellValue(month + "월");
                colIndex++;
            }
        }

        // "계", "비고" (3,4행 병합)
        cell = row.createCell(colIndex);
        cell.setCellValue("계");
        sheet.addMergedRegion(new CellRangeAddress(3, 4, colIndex, colIndex));
        colIndex++;

        cell = row.createCell(colIndex);
        cell.setCellValue("비고");
        sheet.addMergedRegion(new CellRangeAddress(3, 4, colIndex, colIndex));
        colIndex++;

        // === 데이터 입력 ===
        int rowIndex = 5; // 데이터 시작 행
        List<ExcelRow> excelRows = new ArrayList<>();
        Map<String, List<RawGovsplyMtrlList>> groupedByRsceNm = rawItem.getRawGovsplyMtrlList().stream()
                .collect(Collectors.groupingBy(RawGovsplyMtrlList::getRsceNm));

        List<String> sorted = new ArrayList<>(groupedByRsceNm.keySet());
        Collections.sort(sorted);

        int totalMonthCount = (endYear - startYear) * 12 + (endMonth - startMonth + 1);
        int dataStartCol = 4; // 품명~수량 다음 월별 데이터 시작 컬럼

        for (String rsceNm : sorted) {
            List<RawGovsplyMtrlList> itemList = groupedByRsceNm.get(rsceNm);

            Map<String, Row> rowMap = new LinkedHashMap<>();
            double totalQty = 0;
            String totalNm = null;
            double[] monthTotals = new double[totalMonthCount]; // 월별 총합용

            for (RawGovsplyMtrlList item : itemList) {
                String rowKey = item.getRsceCd() + "_" + item.getRsceQty() + "_" + item.getCnsttyCd();
                Row currentRow = rowMap.get(rowKey);

                if (currentRow == null) {
                    currentRow = sheet.createRow(rowIndex++);
                    rowMap.put(rowKey, currentRow);

                    currentRow.createCell(0).setCellValue(item.getRsceNm());
                    currentRow.createCell(1).setCellValue(item.getSpecNm());
                    currentRow.createCell(2).setCellValue(item.getUnit());
                    currentRow.createCell(3).setCellValue(item.getRsceQty());

                    // ("계" 열)에 수량 그대로 복사
                    int totalColIndex = dataStartCol + totalMonthCount;
                    currentRow.createCell(totalColIndex).setCellValue(item.getRsceQty());
                    currentRow.createCell(totalColIndex + 1).setCellValue("");

                    totalQty += item.getRsceQty();
                    totalNm = item.getRsceNm();
                    excelRows.add(new ExcelRow(item.getRsceNm(), "", "", currentRow.getRowNum()));
                }

                // 월별 수량 설정
                int colOffset = 0;
                for (int year = startYear; year <= endYear; year++) {
                    int monthStart = (year == startYear) ? startMonth : 1;
                    int monthEnd = (year == endYear) ? endMonth : 12;

                    for (int month = monthStart; month <= monthEnd; month++) {
                        if (Integer.parseInt(item.getYear()) == year && Integer.parseInt(item.getMonth()) == month) {
                            colIndex = dataStartCol + colOffset;
                            double val = item.getMonthlyQuantity(); // null 체크 필요 없음!
                            currentRow.createCell(colIndex).setCellValue(val);
                            monthTotals[colOffset] += val;
                        }
                        colOffset++;
                    }
                }
            }

            // === "계" 행 추가 ===
            // 종합 스타일 먼저 생성
            CellStyle totalRowStyle = workbook.createCellStyle();
            totalRowStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
            totalRowStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            totalRowStyle.setBorderTop(BorderStyle.THIN);
            totalRowStyle.setBorderBottom(BorderStyle.THIN);
            totalRowStyle.setBorderLeft(BorderStyle.THIN);
            totalRowStyle.setBorderRight(BorderStyle.THIN);

            int totalColIndex = dataStartCol + monthTotals.length;
            Row totalRow = sheet.createRow(rowIndex++);

            // 1. 스타일 먼저 전 셀에 적용
            for (int i = 0; i <= totalColIndex + 1; i++) {
                Cell totalCell = totalRow.createCell(i);
                totalCell.setCellStyle(totalRowStyle);
            }

            // 2. 값 넣기
            totalRow.getCell(0).setCellValue(totalNm);
            totalRow.getCell(1).setCellValue("계");
            totalRow.getCell(2).setCellValue("");
            totalRow.getCell(3).setCellValue(totalQty);

            for (int i = 0; i < monthTotals.length; i++) {
                totalRow.getCell(dataStartCol + i).setCellValue(monthTotals[i]);
            }

            totalRow.getCell(totalColIndex).setCellValue(totalQty); // 계
            totalRow.getCell(totalColIndex + 1).setCellValue(""); // 비고

            excelRows.add(new ExcelRow(totalNm, "", "", totalRow.getRowNum()));
        }

        applyVerticalMerge(sheet, excelRows);

        // 기본 스타일 적용
        int lastMergeColIndex = sheet.getRow(3).getLastCellNum() - 1;

        // 공통 스타일 (테두리 O, 가운데 정렬 X)
        CellStyle borderStyle = workbook.createCellStyle();
        borderStyle.setBorderTop(BorderStyle.THIN);
        borderStyle.setBorderBottom(BorderStyle.THIN);
        borderStyle.setBorderLeft(BorderStyle.THIN);
        borderStyle.setBorderRight(BorderStyle.THIN);

        // 문자열 전용 스타일 (가운데 정렬 추가)
        CellStyle centerAlignStyle = workbook.createCellStyle();
        centerAlignStyle.cloneStyleFrom(borderStyle);
        centerAlignStyle.setAlignment(HorizontalAlignment.CENTER);
        centerAlignStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        for (int rowNum = 0; rowNum <= rowIndex; rowNum++) {
            Row currentRow = sheet.getRow(rowNum);
            if (currentRow == null)
                continue;

            boolean isTotalRow = false;
            Cell cell1 = currentRow.getCell(1);
            if (cell1 != null && "계".equals(cell1.getStringCellValue())) {
                isTotalRow = true;
            }

            for (int colNum = 0; colNum <= lastMergeColIndex; colNum++) {
                Cell currentCell = currentRow.getCell(colNum);
                if (currentCell == null) {
                    currentCell = currentRow.createCell(colNum);
                }

                if (isTotalRow)
                    continue;

                if (currentCell.getCellType() == CellType.STRING) {
                    currentCell.setCellStyle(centerAlignStyle);
                } else {
                    currentCell.setCellStyle(borderStyle);
                }
            }
        }

        // 제목 스타일 생성
        CellStyle titleStyle = workbook.createCellStyle();
        titleStyle.cloneStyleFrom(borderStyle);
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 20);
        titleStyle.setFont(titleFont);

        // 소제목 스타일 생성
        CellStyle subTitleStyle = workbook.createCellStyle();
        subTitleStyle.cloneStyleFrom(borderStyle);
        subTitleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        subTitleStyle.setAlignment(HorizontalAlignment.LEFT);

        // === 1. 제목 행 (관급자재수급계획서) ===
        row = sheet.createRow(0);
        cell = row.createCell(0);
        cell.setCellStyle(titleStyle);
        cell.setCellValue("관 급 자 재 수 급 계 획 서 ( " + sheetName + " )");
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, lastMergeColIndex)); // 제목 병합

        // === 2. 소제목 행 (공사명) ===
        row = sheet.createRow(2);
        cell = row.createCell(0);
        cell.setCellStyle(subTitleStyle);
        cell.setCellValue("공사명 : " + contractName);
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, lastMergeColIndex)); // 소제목 병합

        // === 컬럼 너비 자동 조정 ===
        adjustColumnWidths(sheet, colIndex);

    }

    // 컬럼 너비 자동 조정 메서드
    private void adjustColumnWidths(Sheet sheet, int colIndex) {
        for (int i = 0; i < colIndex; i++) {
            int maxLength = 1;
            for (int rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row != null) {
                    Cell cell = row.getCell(i);
                    if (cell != null && cell.getCellType() == CellType.STRING) {
                        maxLength = cell.getStringCellValue().length();
                    }
                }
            }
            if (i < 2) {
                sheet.setColumnWidth(i, maxLength * 256 * 7);
            } else {
                sheet.setColumnWidth(i, 10 * 256);
            }
        }
    }

    // 수직 병합 처리
    private void applyVerticalMerge(Sheet sheet, List<ExcelRow> rows) {
        String prev = null;
        int start = -1;

        for (int i = 0; i < rows.size(); i++) {
            String current = rows.get(i).step1;

            if (!Objects.equals(prev, current)) {
                if (start != -1 && i - start > 1) {
                    int from = rows.get(start).rowIndex;
                    int to = rows.get(i - 1).rowIndex;
                    sheet.addMergedRegion(new CellRangeAddress(from, to, 0, 0));
                }
                start = i;
                prev = current;
            }
        }

        // 마지막 병합
        if (start != -1 && rows.size() - start > 1) {
            int from = rows.get(start).rowIndex;
            int to = rows.get(rows.size() - 1).rowIndex;
            sheet.addMergedRegion(new CellRangeAddress(from, to, 0, 0));
            sheet.autoSizeColumn(from);
        }
    }

}

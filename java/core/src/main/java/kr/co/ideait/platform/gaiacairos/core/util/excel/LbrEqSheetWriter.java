package kr.co.ideait.platform.gaiacairos.core.util.excel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
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

/**
 * 노무/장비투입설계서 작성
 */
public class LbrEqSheetWriter implements ExcelSheetWriter {

    private RawLbrEqList rawItem;
    private String sheetName;
    private String contractName;

    @Autowired
    public LbrEqSheetWriter() {
    }

    public LbrEqSheetWriter(RawLbrEqList rawItem, String sheetName, String contractName) {
        this.rawItem = rawItem;
        this.sheetName = sheetName;
        this.contractName = contractName;
    }

    @Override
    public void writeSheet(XSSFWorkbook workbook) {
        XSSFSheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            sheet = workbook.createSheet(sheetName);
        }

        writeLbrEqSheet(sheet, rawItem.getRawLbrEqDates(), rawItem.getRawLbrEqLists(), contractName, workbook);
    }

    @Override
    public String getSheetName() {
        return sheetName;
    }

    public strictfp void writeLbrEqSheet(Sheet sheet, List<RawLbrEqDate> dates, List<RawLbrEqItem> rawItems, String contractName,
            XSSFWorkbook workbook) {

        // ===== 스타일 =====

        // 타이틀
        CellStyle titleStyle = workbook.createCellStyle();
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 20);
        titleStyle.setFont(titleFont);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // 계약명
        CellStyle contractStyle = workbook.createCellStyle();
        Font contractFont = workbook.createFont();
        contractFont.setFontHeightInPoints((short) 12); // 글씨 크기 12
        contractFont.setBold(true); // Bold
        contractStyle.setFont(contractFont);
        contractStyle.setAlignment(HorizontalAlignment.LEFT);
        contractStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // 기본 셀(가운데 정렬, 테두리 스타일)
        CellStyle centerStyle = workbook.createCellStyle();
        centerStyle.setAlignment(HorizontalAlignment.CENTER);
        centerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        centerStyle.setBorderTop(BorderStyle.THIN);
        centerStyle.setBorderBottom(BorderStyle.THIN);
        centerStyle.setBorderLeft(BorderStyle.THIN);

        // 개월 셀(배경색 노란색)
        CellStyle monthTermStyle = workbook.createCellStyle();
        Font monthTermFont = workbook.createFont();
        monthTermFont.setBold(true);
        monthTermStyle.setFont(monthTermFont);
        monthTermStyle.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.getIndex());
        monthTermStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        monthTermStyle.setAlignment(HorizontalAlignment.CENTER);
        monthTermStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        monthTermStyle.setBorderTop(BorderStyle.THIN);
        monthTermStyle.setBorderBottom(BorderStyle.THIN);
        monthTermStyle.setBorderLeft(BorderStyle.THIN);
        monthTermStyle.setBorderRight(BorderStyle.THIN);

        CellStyle leftAlignStyle = workbook.createCellStyle();
        leftAlignStyle.setAlignment(HorizontalAlignment.LEFT);
        leftAlignStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        leftAlignStyle.setBorderTop(BorderStyle.THIN);
        leftAlignStyle.setBorderBottom(BorderStyle.THIN);
        leftAlignStyle.setBorderLeft(BorderStyle.THIN);
        leftAlignStyle.setBorderRight(BorderStyle.THIN);

        // ===== 1~2행: 상단 타이틀 =====
        Row titleRow1 = sheet.createRow(0);
        Cell titleCell = titleRow1.createCell(0);
        titleCell.setCellValue("노 무 / 장 비 투 입 계 획 서");
        titleCell.setCellStyle(titleStyle);

        sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 12));

        // ===== 4행: 공사명 =====
        Row contractRow = sheet.createRow(3);
        Cell contractCell = contractRow.createCell(0);
        contractCell.setCellValue("공사명 : " + contractName);
        contractCell.setCellStyle(contractStyle);

        // ===== A~C열: 구분/공종별/노무비 헤더 =====
        for (int i = 4; i <= 7; i++) {
            if (sheet.getRow(i) == null) {
                sheet.createRow(i);
            }
        }

        Row row4 = sheet.getRow(4);

        Cell categoryCell = row4.createCell(0);
        categoryCell.setCellValue("구분");
        categoryCell.setCellStyle(centerStyle);
        sheet.addMergedRegion(new CellRangeAddress(4, 7, 0, 0));

        Cell workTypeCell = row4.createCell(1);
        workTypeCell.setCellValue("공종별");
        workTypeCell.setCellStyle(centerStyle);
        sheet.addMergedRegion(new CellRangeAddress(4, 7, 1, 1));

        Cell laborCostCell = row4.createCell(2);
        laborCostCell.setCellValue("노무비");
        laborCostCell.setCellStyle(centerStyle);
        sheet.addMergedRegion(new CellRangeAddress(4, 7, 2, 2));

        sheet.setColumnWidth(0, 15 * 256); // A열 너비 15
        sheet.setColumnWidth(1, 22 * 256); // B열 너비 22
        sheet.setColumnWidth(2, 33 * 256); // C열 너비 33

        // ===== D열 ~ : 날짜 =====
        Row yearRow = sheet.getRow(4);
        Row termRow = sheet.getRow(5);
        Row monthRow = sheet.getRow(6);
        Row dayRow = sheet.getRow(7);

        // ===== 마지막 소계/주소계 =====
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        int startCol = 3;
        int currentCol = startCol;

        Map<String, Integer> yearStartColMap = new LinkedHashMap<>();
        Map<String, Integer> monthStartColMap = new LinkedHashMap<>();
        Map<String, Integer> termStartColMap = new LinkedHashMap<>();
        Map<Integer, String> colMonthKeys = new HashMap<>();
        Set<Integer> activeCols = new HashSet<>();

        for (int i = 0; i < dates.size(); i++) {
            RawLbrEqDate rawDate = dates.get(i);
            LocalDate date = LocalDate.parse(rawDate.getWeekStart(), formatter);
            String year = String.valueOf(date.getYear());
            String monthKey = year + "-" + date.getMonthValue();

            yearStartColMap.putIfAbsent(year, currentCol);
            monthStartColMap.putIfAbsent(monthKey, currentCol);
            termStartColMap.putIfAbsent(monthKey, currentCol);
            colMonthKeys.put(currentCol, monthKey);

            Cell monthCell = monthRow.createCell(currentCol);
            monthCell.setCellValue(date.getMonthValue() + "월");
            monthCell.setCellStyle(centerStyle);

            Cell dayCell = dayRow.createCell(currentCol);
            dayCell.setCellValue(date.getDayOfMonth());
            dayCell.setCellStyle(centerStyle);

            sheet.setColumnWidth(currentCol, 5 * 256);
            currentCol++;
        }
        // 연도 병합
        List<String> years = new ArrayList<>(yearStartColMap.keySet());
        for (int i = 0; i < years.size(); i++) {
            String year = years.get(i);
            int start = yearStartColMap.get(year);
            int end = (i + 1 < years.size()) ? yearStartColMap.get(years.get(i + 1)) - 1 : currentCol - 1;

            Cell cell = yearRow.getCell(start);
            if (cell == null)
                cell = yearRow.createCell(start);
            cell.setCellValue(year);
            cell.setCellStyle(centerStyle);
            sheet.addMergedRegion(new CellRangeAddress(4, 4, start, end));
        }

        // ===== 공종별/노무비 데이터 =====
        Map<String, List<RawLbrEqItem>> groupedByUnit = rawItems.stream()
                .filter(item -> item.getUnitCnstType() != null && !item.getUnitCnstType().isBlank())
                .collect(Collectors.groupingBy(item -> item.getUnitCnstType().substring(0, 1)));

        int rowIdx = 8;
        for (Map.Entry<String, List<RawLbrEqItem>> entry : groupedByUnit.entrySet()) {
            List<RawLbrEqItem> items = entry.getValue();
            int unitStartRow = rowIdx;

            for (RawLbrEqItem item : items) {
                List<String> eqLines = new ArrayList<>();
                eqLines.add("인원");
                if (item.getTotalEqRsceDetail() != null && !item.getTotalEqRsceDetail().isBlank()) {
                    eqLines.addAll(Arrays.stream(item.getTotalEqRsceDetail().split(","))
                            .map(String::trim)
                            .filter(s -> !s.isBlank())

                            .map(s -> s.contains(":") ? s.split(":")[0].trim() : s)
                            .collect(Collectors.toList()));
                }

                int eqStartRow = rowIdx;
                for (int i = 0; i < eqLines.size(); i++) {
                    Row row = sheet.getRow(rowIdx);
                    if (row == null)
                        row = sheet.createRow(rowIdx);

                    // C열: 노무비 항목
                    Cell dCell = row.createCell(2);
                    dCell.setCellValue(eqLines.get(i));
                    dCell.setCellStyle(leftAlignStyle);

                    // B열: 공종별
                    if (i == 0) {
                        Cell cCell = row.createCell(1);
                        cCell.setCellValue(item.getCnsttyNm());
                        cCell.setCellStyle(centerStyle);
                    }

                    // A열: 구분
                    Cell aCell = row.createCell(0);
                    aCell.setCellValue(contractName);
                    aCell.setCellStyle(centerStyle);

                    if (item.getPlanStart() == null || item.getPlanStart().isBlank() ||
                            item.getPlanFinish() == null || item.getPlanFinish().isBlank()) {
                        rowIdx++;
                        continue;
                    }

                    LocalDate planStart = LocalDate.parse(item.getPlanStart(), formatter);
                    LocalDate planFinish = LocalDate.parse(item.getPlanFinish(), formatter);
                    long totalDays = ChronoUnit.DAYS.between(planStart, planFinish) + 1;

                    double totalValue = 0;
                    if (eqLines.get(i).equals("인원")) {
                        totalValue = item.getTotalLbrQty() != null ? item.getTotalLbrQty() : 0;
                    } else {
                        String line = eqLines.get(i);
                        if (item.getTotalEqRsceDetail() != null && line != null) {
                            for (String part : item.getTotalEqRsceDetail().split(",")) {
                                if (part.trim().startsWith(line)) {
                                    String[] split = part.split(":");
                                    if (split.length == 2) {
                                        try {
                                            totalValue = Double.parseDouble(split[1].trim());
                                        } catch (GaiaBizException e) {
                                            totalValue = 0;
                                        }
                                    }
                                }
                            }
                        }
                    }

                    double dailyValue = totalDays > 0 ? totalValue / totalDays : 0;

                    // 날짜별 수량
                    double rowTotal = 0; // 누적 합계
                    for (int d = 0; d < dates.size(); d++) {
                        RawLbrEqDate week = dates.get(d);
                        LocalDate weekStart = LocalDate.parse(week.getWeekStart(), formatter);
                        LocalDate weekEnd = weekStart.plusDays(6);

                        LocalDate actualStart = planStart.isAfter(weekStart) ? planStart : weekStart;
                        LocalDate actualEnd = planFinish.isBefore(weekEnd) ? planFinish : weekEnd;

                        long daysInWeek = ChronoUnit.DAYS.between(actualStart, actualEnd) + 1;

                        if (daysInWeek > 0) {
                            double weekValue = Math.round(dailyValue * daysInWeek * 100.0) / 100.0;
                            rowTotal += weekValue;

                            Cell cell = row.createCell(startCol + d);
                            if (weekValue != 0) {
                                cell.setCellValue(weekValue);
                                activeCols.add(startCol + d);
                            }
                            cell.setCellStyle(centerStyle);
                        }

                    }

                    // 비고 합계 셀
                    Cell remarkCell = row.getCell(currentCol);
                    if (remarkCell == null)
                        remarkCell = row.createCell(currentCol);

                    if (rowTotal != 0) {
                        remarkCell.setCellValue(Math.round(rowTotal * 100.0) / 100.0);
                    }
                    remarkCell.setCellStyle(centerStyle);

                    rowIdx++;
                }

                if (eqLines.size() > 1) {
                    sheet.addMergedRegion(new CellRangeAddress(eqStartRow, rowIdx - 1, 1, 1));
                }
            }

            int unitEndRow = rowIdx - 1;
            Row bRow = sheet.getRow(unitStartRow);
            Cell bCell = bRow.createCell(0);
            bCell.setCellValue(items.get(0).getCmnCdNmKrn());
            bCell.setCellStyle(centerStyle);
            if (unitStartRow != unitEndRow) {
                sheet.addMergedRegion(new CellRangeAddress(unitStartRow, unitEndRow, 0, 0));
            }
        }

        // ===== 개월 병합 (공종별 수량 처리 이후) =====
        List<String> terms = new ArrayList<>(termStartColMap.keySet());
        Set<Integer> termCols = new HashSet<>();
        int termIndex = 1;
        for (int i = 0; i < terms.size(); i++) {
            String termKey = terms.get(i);
            int start = termStartColMap.get(termKey);
            int end = (i + 1 < terms.size()) ? termStartColMap.get(terms.get(i + 1)) - 1 : currentCol - 1;

            boolean hasData = false;
            for (int col = start; col <= end; col++) {
                if (activeCols.contains(col)) {
                    hasData = true;
                    break;
                }
            }

            if (hasData) {
                // 1. 셀 모두 생성
                for (int col = start; col <= end; col++) {
                    Cell cell = termRow.getCell(col);
                    if (cell == null)
                        cell = termRow.createCell(col);
                    termCols.add(col);
                }

                // 2. 병합
                sheet.addMergedRegion(new CellRangeAddress(5, 5, start, end));

                // 3. 시작 셀에만 값 입력
                Cell mergedCell = termRow.getCell(start);
                mergedCell.setCellValue(termIndex++ + "개월");
            }
        }

        // ===== 월 병합 =====
        List<String> months = new ArrayList<>(monthStartColMap.keySet());
        for (int i = 0; i < months.size(); i++) {
            String monthKey = months.get(i);
            int start = monthStartColMap.get(monthKey);
            int end = (i + 1 < months.size()) ? monthStartColMap.get(months.get(i + 1)) - 1 : currentCol - 1;

            Cell cell = monthRow.getCell(start);
            if (cell == null)
                cell = monthRow.createCell(start);
            cell.setCellValue(Integer.parseInt(monthKey.split("-")[1]) + "월");
            cell.setCellStyle(centerStyle);
            sheet.addMergedRegion(new CellRangeAddress(6, 6, start, end));
        }

        // ===== 비고 열 헤더 생성 =====
        Cell remarkHeader = row4.createCell(currentCol);
        remarkHeader.setCellValue("비고");
        remarkHeader.setCellStyle(centerStyle);
        sheet.addMergedRegion(new CellRangeAddress(4, 7, currentCol, currentCol));
        sheet.setColumnWidth(currentCol, 10 * 256);

        for (int r = 4; r < rowIdx; r++) {
            Row row = sheet.getRow(r);
            if (row == null)
                continue;

            for (int c = 0; c <= currentCol; c++) {
                Cell cell = row.getCell(c);
                if (cell == null)
                    cell = row.createCell(c);

                if (c == 2 && r >= 8) {
                    cell.setCellStyle(leftAlignStyle);
                } else {
                    cell.setCellStyle(centerStyle);
                }
            }
        }

        // ===== 소계 및 주소계 계산 =====
        Map<String, double[]> categorySums = new LinkedHashMap<>();
        double[] grandTotal = new double[dates.size()];

        for (RawLbrEqItem item : rawItems) {
            if (item.getPlanStart() == null || item.getPlanStart().isBlank()
                    || item.getPlanFinish() == null || item.getPlanFinish().isBlank()) {
                continue;
            }
            LocalDate planStart = LocalDate.parse(item.getPlanStart(), formatter);
            LocalDate planFinish = LocalDate.parse(item.getPlanFinish(), formatter);
            long totalDays = ChronoUnit.DAYS.between(planStart, planFinish) + 1;
            double dailyValue = 0;

            // 인원
            if (item.getTotalLbrQty() != null) {
                dailyValue = item.getTotalLbrQty() / (double) totalDays;
                String key = "인원";
                double[] sums = categorySums.computeIfAbsent(key, k -> new double[dates.size()]);
                for (int i = 0; i < dates.size(); i++) {
                    RawLbrEqDate date = dates.get(i);
                    LocalDate weekStart = LocalDate.parse(date.getWeekStart(), formatter);
                    LocalDate weekEnd = weekStart.plusDays(6);

                    LocalDate actualStart = planStart.isAfter(weekStart) ? planStart : weekStart;
                    LocalDate actualEnd = planFinish.isBefore(weekEnd) ? planFinish : weekEnd;

                    long days = ChronoUnit.DAYS.between(actualStart, actualEnd) + 1;
                    if (days > 0) {
                        double val = dailyValue * days;
                        sums[i] += val;
                        grandTotal[i] += val;
                    }
                }
            }

            // 장비들
            if (item.getTotalEqRsceDetail() != null && !item.getTotalEqRsceDetail().isBlank()) {
                String[] parts = item.getTotalEqRsceDetail().split(",");
                for (String part : parts) {
                    String[] split = part.trim().split(":");
                    if (split.length != 2)
                        continue;

                    String eqName = split[0].trim();
                    double eqQty = Double.parseDouble(split[1].trim());
                    dailyValue = eqQty / (double) totalDays;

                    double[] sums = categorySums.computeIfAbsent(eqName, k -> new double[dates.size()]);
                    for (int i = 0; i < dates.size(); i++) {
                        RawLbrEqDate date = dates.get(i);
                        LocalDate weekStart = LocalDate.parse(date.getWeekStart(), formatter);
                        LocalDate weekEnd = weekStart.plusDays(6);

                        LocalDate actualStart = planStart.isAfter(weekStart) ? planStart : weekStart;
                        LocalDate actualEnd = planFinish.isBefore(weekEnd) ? planFinish : weekEnd;

                        long days = ChronoUnit.DAYS.between(actualStart, actualEnd) + 1;
                        if (days > 0) {
                            double val = dailyValue * days;
                            sums[i] += val;
                            grandTotal[i] += val;
                        }
                    }
                }
            }
        }

        // ===== 소계 행 =====
        for (Map.Entry<String, double[]> entry : categorySums.entrySet()) {
            Row row = sheet.createRow(rowIdx++);

            // A~B 병합 후 "소계"
            Cell abCell = row.createCell(0);
            abCell.setCellValue("소계");
            abCell.setCellStyle(centerStyle);
            sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 1));

            // C열: 항목명
            Cell nameCell = row.createCell(2);
            nameCell.setCellValue(entry.getKey());
            nameCell.setCellStyle(leftAlignStyle);

            double total = 0;
            for (int i = 0; i < entry.getValue().length; i++) {
                Cell cell = row.createCell(startCol + i);
                double val = Math.round(entry.getValue()[i] * 100.0) / 100.0;
                if (val != 0) {
                    cell.setCellValue(val);
                }
                cell.setCellStyle(centerStyle);
                total += val;
            }

            Cell sumCell = row.createCell(currentCol);
            if (total != 0) {
                sumCell.setCellValue(Math.round(total * 100.0) / 100.0);
            }
            sumCell.setCellStyle(centerStyle);
        }

        // ===== 주소계 행 =====
        Row row = sheet.createRow(rowIdx++);
        Cell abcCell = row.createCell(0);
        abcCell.setCellValue("주 소계");
        abcCell.setCellStyle(leftAlignStyle);
        sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 2));

        double grandSum = 0;
        for (int i = 0; i < grandTotal.length; i++) {
            Cell cell = row.createCell(startCol + i);
            double val = Math.round(grandTotal[i] * 100.0) / 100.0;
            if (val != 0) {
                cell.setCellValue(val);
            }
            cell.setCellStyle(centerStyle);
            grandSum += val;
        }

        Cell sumCell = row.createCell(currentCol);
        if (grandSum != 0) {
            sumCell.setCellValue(Math.round(grandSum * 100.0) / 100.0);
        }
        sumCell.setCellStyle(centerStyle);

        // ===== 전체 셀 스타일 적용 =====
        // Bold 폰트=====
        Font boldFont = workbook.createFont();
        boldFont.setBold(true);

        CellStyle boldCenterStyle = workbook.createCellStyle();
        boldCenterStyle.cloneStyleFrom(centerStyle);
        boldCenterStyle.setFont(boldFont);

        // ===== 비고 열 스타일(오른쪽 테두리까지) =====
        CellStyle rightBorderStyle = workbook.createCellStyle();
        rightBorderStyle.cloneStyleFrom(centerStyle);
        rightBorderStyle.setBorderRight(BorderStyle.THIN);

        // ===== 셀 스타일 전체 적용 =====
        for (int r = 4; r < rowIdx; r++) {
            row = sheet.getRow(r);
            if (row == null)
                continue;

            for (int c = 0; c <= currentCol; c++) {
                Cell cell = row.getCell(c);
                if (cell == null)
                    cell = row.createCell(c);

                boolean isHeader = r >= 4 && r <= 7;
                boolean isTermCol = termCols.contains(c);
                boolean isData = r >= 8;

                if (isHeader) {
                    if (r == 5 && isTermCol) {
                        cell.setCellStyle(monthTermStyle); // '개월' 노란 셀
                    } else if (c == currentCol) {
                        CellStyle boldRightStyle = workbook.createCellStyle();
                        boldRightStyle.cloneStyleFrom(boldCenterStyle);
                        boldRightStyle.setBorderRight(BorderStyle.THIN);
                        cell.setCellStyle(boldRightStyle);
                    } else {
                        cell.setCellStyle(boldCenterStyle);
                    }
                } else if (isData) {
                    if (c == 2) {
                        cell.setCellStyle(leftAlignStyle);
                    } else if (isTermCol) {
                        cell.setCellStyle(monthTermStyle);
                    } else if (c == currentCol) {
                        cell.setCellStyle(rightBorderStyle);
                    } else {
                        cell.setCellStyle(centerStyle);
                    }
                }
            }
        }

    }
}

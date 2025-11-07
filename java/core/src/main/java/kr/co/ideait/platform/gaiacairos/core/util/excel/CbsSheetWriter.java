package kr.co.ideait.platform.gaiacairos.core.util.excel;

import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.contract.contractstatus.ContractstatusMybatisParam.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * 계약내역서 시트 작성
 */
public class CbsSheetWriter implements ExcelSheetWriter {

	private List<RawCbsItem> rawList;
    private String sheetName;
    private String contractName;

	@Autowired
	public CbsSheetWriter() {
    }
    
	public CbsSheetWriter(List<RawCbsItem> rawList, String sheetName, String contractName) {
        this.rawList = rawList;
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
        
        writeContractSheet(sheet, rawList, contractName, workbook);
	}
    
	@Override
	public String getSheetName() {
        return sheetName;
	}
    

    // 계약내역서 작성
    private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("#,###");
    private static final DecimalFormat PERCENT_FORMAT = new DecimalFormat("0.000");

    public void writeContractSheet(Sheet sheet, List<RawCbsItem> rawList, String contractName, XSSFWorkbook workbook) {
        // 스타일 준비
        CellStyle centerStyle = createCenterStyle(workbook);
        CellStyle rightAlignStyle = createRightAlignStyle(workbook);
        CellStyle leftAlignStyle = createLefttAlignStyle(workbook);

        // 계약명 입력
        Row titleRow = sheet.getRow(2);
        if (titleRow != null && titleRow.getCell(0) != null) {
            titleRow.getCell(0).setCellValue("공사명 : " + contractName);
        }

        // 트리 구조 구성
        AtomicInteger rowNum = new AtomicInteger(6);

        // 데이터 쓰기
        writeFlatRows(sheet, rawList, rowNum, centerStyle, rightAlignStyle, leftAlignStyle);

        // 열 너비 자동 조정
        for (int i = 0; i <= 13; i++) sheet.autoSizeColumn(i);

    }

    private void writeFlatRows(Sheet sheet, List<RawCbsItem> rawList, AtomicInteger rowNum,
                               CellStyle centerStyle, CellStyle rightAlignStyle, CellStyle leftAlignStyle) {

        double subTotalMtrlcstAmt = 0.0;
        double subTotalLbrcstAmt = 0.0;
        double subTotalGnrlexpnsAmt = 0.0;
        double subTotalSumAmt = 0.0;
    
        for (int i = 0; i < rawList.size(); i++) {
            RawCbsItem item = rawList.get(i);
            int level = item.getCnsttyLvlNum();
            int prevLevel = i > 0 ? rawList.get(i - 1).getCnsttyLvlNum() : 0;
    
            // Level 2 CBS 시작 전에 소계 찍기
            if (level == 2 && "CBS".equals(item.getNodeType()) && prevLevel >= level && i > 0) {
                // 1. 소계 행 출력
                Row subtotalRow = sheet.createRow(rowNum.getAndIncrement());
    
                subtotalRow.createCell(0).setCellValue("소계");
                subtotalRow.createCell(5).setCellValue(MONEY_FORMAT.format(format(subTotalMtrlcstAmt)));
                subtotalRow.createCell(7).setCellValue(MONEY_FORMAT.format(format(subTotalLbrcstAmt)));
                subtotalRow.createCell(9).setCellValue(MONEY_FORMAT.format(format(subTotalGnrlexpnsAmt)));
                subtotalRow.createCell(11).setCellValue(MONEY_FORMAT.format(format(subTotalSumAmt)));
    
                // 스타일 적용 (왼쪽 정렬, 금액 오른쪽 정렬)
                for (int j = 0; j <= 13; j++) {
                    Cell subtotalRowCell = subtotalRow.getCell(j);
                    if (subtotalRowCell == null) subtotalRow.createCell(j);
                    else{
                    if (j == 0) subtotalRowCell.setCellStyle(leftAlignStyle);
                    else subtotalRowCell.setCellStyle(rightAlignStyle);
                    }
                }
    
                // 2. 빈 줄 2개 추가해서 구분
                for (int k = 0; k < 2; k++) {
                    Row emptyRow = sheet.createRow(rowNum.getAndIncrement());
                    for (int j = 0; j <= 13; j++) {
                        emptyRow.createCell(j).setCellStyle(leftAlignStyle);
                    }
                }
            }
    
            // 새로운 Row 생성
            Row row = sheet.createRow(rowNum.getAndIncrement());
    
            if (level == 1) { // 대공종
                row.createCell(0).setCellValue(item.getCnsttyNm());
                row.getCell(0).setCellStyle(leftAlignStyle);

                for (int j = 1; j <= 13; j++) {
                    row.createCell(j).setCellValue("");
                    row.getCell(j).setCellStyle(rightAlignStyle);
                }
            }
            else if (level == 2 && "CBS".equals(item.getNodeType())) { // CBS
                row.createCell(0).setCellValue(item.getCnsttyNm());
                row.createCell(5).setCellValue(MONEY_FORMAT.format(format(item.getMtrlcstAmt())));
                row.createCell(7).setCellValue(MONEY_FORMAT.format(format(item.getLbrcstAmt())));
                row.createCell(9).setCellValue(MONEY_FORMAT.format(format(item.getGnrlexpnsAmt())));
                row.createCell(11).setCellValue(MONEY_FORMAT.format(format(item.getSumAmt())));
    
                for (int j = 0; j <= 13; j++) {
                    if (row.getCell(j) == null) {row.createCell(j);}
                    if (j == 0) {row.getCell(j).setCellStyle(leftAlignStyle);}
                    else {row.getCell(j).setCellStyle(rightAlignStyle);}
                }
    
                // 소계값 초기화
                subTotalMtrlcstAmt = item.getMtrlcstAmt();
                subTotalLbrcstAmt = item.getLbrcstAmt();
                subTotalGnrlexpnsAmt = item.getGnrlexpnsAmt();
                subTotalSumAmt = item.getSumAmt();
            }
            else { // DETAIL
                row.createCell(0).setCellValue(item.getCnsttyNm());
                row.createCell(1).setCellValue(item.getSpec());
                row.createCell(2).setCellValue(item.getUnit());
                row.createCell(3).setCellValue(format(item.getQty()));
                row.createCell(4).setCellValue(MONEY_FORMAT.format(format(item.getMtrlcstUprc())));
                row.createCell(5).setCellValue(MONEY_FORMAT.format(format(item.getMtrlcstAmt())));
                row.createCell(6).setCellValue(MONEY_FORMAT.format(format(item.getLbrcstUprc())));
                row.createCell(7).setCellValue(MONEY_FORMAT.format(format(item.getLbrcstAmt())));
                row.createCell(8).setCellValue(MONEY_FORMAT.format(format(item.getGnrlexpnsUprc())));
                row.createCell(9).setCellValue(MONEY_FORMAT.format(format(item.getGnrlexpnsAmt())));
                row.createCell(10).setCellValue(MONEY_FORMAT.format(format(item.getSumUprc())));
                row.createCell(11).setCellValue(MONEY_FORMAT.format(format(item.getSumAmt())));
                row.createCell(12).setCellValue(item.getRmrk());
                row.createCell(13).setCellValue(item.getEtc());
    
                for (int j = 0; j <= 13; j++) {
                    if (row.getCell(j) == null) row.createCell(j);
                    if (j == 0 || j == 1 || j == 12 || j == 13) row.getCell(j).setCellStyle(leftAlignStyle);
                    else if (j == 2) row.getCell(j).setCellStyle(centerStyle);
                    else row.getCell(j).setCellStyle(rightAlignStyle);
                }
            }
        }

    }


    // 헤더 스타일
    private CellStyle createHeaderStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont(); font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        setBorders(style);
        return style;
    }

    // 일반 셀 중앙 정렬 스타일
    private CellStyle createCenterStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        setBorders(style);
        return style;
    }

    // 테두리 설정
    private void setBorders(CellStyle style) {
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
    }

    // 오른쪽 정렬 스타일 (금액 전용)
    private CellStyle createRightAlignStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.RIGHT); // 오른쪽 정렬
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        setBorders(style);
        return style;
    }

    // 왼쪽 정렬 스타일 (금액 전용)
    private CellStyle createLefttAlignStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT); // 오른쪽 정렬
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        setBorders(style);
        return style;
    }

    // 금액 포맷
    private Double format(Double val) {
        return val != null ? val : 0.0;
    }

}

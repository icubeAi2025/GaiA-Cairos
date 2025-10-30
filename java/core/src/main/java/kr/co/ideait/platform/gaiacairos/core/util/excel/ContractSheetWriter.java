package kr.co.ideait.platform.gaiacairos.core.util.excel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;

import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.contract.contractstatus.ContractstatusMybatisParam.*;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.contract.contractstatus.ContractBidDto.ContractItemNode;
import kr.co.ideait.platform.gaiacairos.core.util.excel.ExcelUtil.ExcelRow;

/**
 * 계약내역서 시트 작성
 */
public class ContractSheetWriter implements ExcelSheetWriter {

	private List<RawContractItem> rawList;
    private String sheetName;
    private String contractName;

	@Autowired
	public ContractSheetWriter() {
        
    }
    
	public ContractSheetWriter(List<RawContractItem> rawList, String sheetName, String contractName) {
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

    public void writeContractSheet(Sheet sheet, List<RawContractItem> rawList, String contractName, XSSFWorkbook workbook) {
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
        ContractItemNode root = buildCostTree(rawList);
        AtomicInteger rowNum = new AtomicInteger(6);
        List<ExcelRow> excelRows = new ArrayList<>();

        // 데이터 쓰기
        writeRows(sheet, root.getChildren(), rowNum, centerStyle, rightAlignStyle, leftAlignStyle, excelRows);

        // 열 너비 자동 조정
        for (int i = 0; i <= 12; i++){
            sheet.autoSizeColumn(i);
        } 
            

    }

    // 트리 구조 생성
    private ContractItemNode buildCostTree(List<RawContractItem> rawList) {
        Map<String, ContractItemNode> map = new HashMap<>();
        ContractItemNode root = new ContractItemNode();

        for (RawContractItem item : rawList) {
            ContractItemNode node = new ContractItemNode();
            node.setData(item);
            map.put(item.getCntrctDcnsttySno(), node);
        }

        for (RawContractItem item : rawList) {
            ContractItemNode node = map.get(item.getCntrctDcnsttySno());
            String parentCd = item.getUpCntrctDcnsttySno();
            if (parentCd != null && map.containsKey(parentCd)) {
                ContractItemNode parent = map.get(parentCd);
                node.setParent(parent);
                parent.getChildren().add(node);
            } else {
                root.getChildren().add(node);
            }
        }

        insertSubtotals(root);
        return root;
    }

    // 소계 항목 자동 추가 (메뉴 레벨 3인 경우에만)
    private void insertSubtotals(ContractItemNode parent) {
        for (ContractItemNode child : parent.getChildren()) {
            insertSubtotals(child);
        }

        // 자식이 있고, 부모가 레벨 3인 경우에만 소계 생성
        if (!parent.getChildren().isEmpty() && parent.getData() != null && parent.getData().getAMenuLevel() == 2) {

            double totalMtrlcs = parent.getChildren().stream().mapToDouble(ContractItemNode::getTotalMtrlcsAmount).sum(); // 재료비 소계
            double totalLbrcst = parent.getChildren().stream().mapToDouble(ContractItemNode::getTotalLbrcstAmount).sum(); // 노무비 소계
            double totalGnrlexpns = parent.getChildren().stream().mapToDouble(ContractItemNode::getTotalGnrlexpnsAmount).sum(); // 경비 소계
            double totalSum = parent.getChildren().stream().mapToDouble(ContractItemNode::getTotalSumAmount).sum(); // 합계 소계

            RawContractItem subtotal = new RawContractItem();
            subtotal.setPrdnm("소계");
            subtotal.setAMenuLevel(3); // 2의 하위 = 3단계
            subtotal.setUpCntrctDcnsttySno(parent.getData().getCntrctDcnsttySno());
            subtotal.setMtrlcstAmt(totalMtrlcs);
            subtotal.setLbrcstAmt(totalLbrcst);
            subtotal.setGnrlexpnsAmt(totalGnrlexpns);
            subtotal.setSumAmt(totalSum);

            ContractItemNode subNode = new ContractItemNode();
            subNode.setData(subtotal);
            subNode.setParent(parent);
            parent.getChildren().add(subNode);
        }
    }

    // 재귀적으로 행 작성
    private void writeRows(Sheet sheet, List<ContractItemNode> nodes, AtomicInteger rowNum, CellStyle centerStyle, CellStyle rightAlignStyle, CellStyle leftAlignStyle, List<ExcelRow> excelRows) {
        for (ContractItemNode node : nodes) {
            RawContractItem item = node.getData();
            if (item == null) continue;
    
            int level = item.getAMenuLevel();
    
            
            int r = rowNum.getAndIncrement();
            Row row = sheet.createRow(r);
            
            // 메뉴 레벨 1은 공종명만 작성
            if (level == 1) {
                row.createCell(0).setCellValue(item.getPrdnm());
                row.createCell(1).setCellValue("");
                row.createCell(2).setCellValue("");
                row.createCell(3).setCellValue("");
                row.createCell(4).setCellValue("");
                row.createCell(5).setCellValue(MONEY_FORMAT.format(format(item.getMtrlcstAmt())));
                row.createCell(6).setCellValue("");
                row.createCell(7).setCellValue(MONEY_FORMAT.format(format(item.getLbrcstAmt())));
                row.createCell(8).setCellValue("");
                row.createCell(9).setCellValue(MONEY_FORMAT.format(format(item.getGnrlexpnsAmt())));
                row.createCell(10).setCellValue("");
                row.createCell(11).setCellValue(MONEY_FORMAT.format(format(item.getSumAmt())));
                row.createCell(12).setCellValue("");
            }
            else{
                // 셀 작성
                row.createCell(0).setCellValue(item.getPrdnm());
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
            }
    
            // 소계 항목이면 구분을 위해 빈 줄 2줄 추가
            if ("소계".equals(item.getPrdnm())) {
                // 첫 번째 빈 행
                Row emptyRow1 = sheet.createRow(rowNum.getAndIncrement());
                // 두 번째 빈 행
                Row emptyRow2 = sheet.createRow(rowNum.getAndIncrement());

                // 각 셀에 스타일 적용
                for (int i = 0; i <= 12; i++) {
                    emptyRow1.createCell(i).setCellStyle(leftAlignStyle);
                    emptyRow2.createCell(i).setCellStyle(leftAlignStyle);
                }
            }

            // 셀 스타일 설정
            for (int i = 0; i <= 12; i++) {
                if (row.getCell(i) != null) {
                    if (i == 0 || i == 1 || i == 12) {
                        row.getCell(i).setCellStyle(leftAlignStyle);
                    } else if (i == 2) {
                        row.getCell(i).setCellStyle(centerStyle);
                    } else {
                        row.getCell(i).setCellStyle(rightAlignStyle);
                    }
                }
            }
    
    
            // 자식 재귀 처리
            writeRows(sheet, node.getChildren(), rowNum, centerStyle, rightAlignStyle, leftAlignStyle, excelRows);
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

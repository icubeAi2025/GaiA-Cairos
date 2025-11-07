package kr.co.ideait.platform.gaiacairos.core.util.excel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.contract.contractstatus.ContractstatusMybatisParam;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;

import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.contract.contractstatus.ContractstatusMybatisParam.*;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.contract.contractstatus.ContractBidDto.CostItemNode;
import kr.co.ideait.platform.gaiacairos.core.util.excel.ExcelUtil.ExcelRow;

/**
 * 원가계산서 시트 작성
 */
public class CalculatorSheetWriter implements ExcelSheetWriter {

	private List<ContractstatusMybatisParam.RawCostItem> rawList;
    private String sheetName;
    private String contractName;

	private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("#,###");
    private static final DecimalFormat PERCENT_FORMAT = new DecimalFormat("0.000");

	private AtomicInteger subtotalCodeIndex = new AtomicInteger(0);

	@Autowired
	public CalculatorSheetWriter() {

	}

	public CalculatorSheetWriter(List<RawCostItem> rawList, String sheetName, String contractName) {
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

        writeCostSheet(sheet, rawList, contractName, workbook);
	}

	@Override
	public String getSheetName() {
		return sheetName;
	}

    // --------- 여기까지 공통 사용


	// 소계 구분자 자동 생성 (A, B, C, ...)
    private String getSubtotalCode() {
        return String.valueOf((char) ('A' + subtotalCodeIndex.getAndIncrement()));
    }

    public void writeCostSheet(Sheet sheet, List<RawCostItem> rawList, String contractName, XSSFWorkbook workbook) {
        // 스타일 준비
        CellStyle centerStyle = createCenterStyle(workbook);    // 셀 중앙 정렬 스타일
        CellStyle rightAlignStyle = createRightAlignStyle(workbook);    // 오른쪽 정렬 스타일

        // 계약명 입력
        Row titleRow = sheet.getRow(2); // 3번 행
        if (titleRow != null && titleRow.getCell(0) != null) {  // 3번 행 비어있을 시
            titleRow.getCell(0).setCellValue("공사명 : " + contractName);   // 3번행에 "공사명 : " + contractName(계약이름)
        }

        // 트리 구조 구성
        CostItemNode root = buildCostTree(rawList);
        AtomicInteger rowNum = new AtomicInteger(4);    // 트리는 5번 행부터
        List<ExcelRow> excelRows = new ArrayList<>();

        // 데이터 쓰기
        writeRows(sheet, root.getChildren(), rowNum, centerStyle, rightAlignStyle, excelRows);

        // 병합 처리
        for (int i = 0; i <= 2; i++) {
        	applyVerticalMerge(sheet, excelRows, i);
        }

        // 최상위 노드 출력
        for (CostItemNode node : root.getChildren()) {
            RawCostItem item = node.getData();
            if (item == null) continue;
            int r = rowNum.getAndIncrement();
            Row row = sheet.createRow(r);

            row.createCell(0).setCellValue(item.getCstCalcItNm());
            row.createCell(1).setCellValue(item.getCstCalcItNm());
            row.createCell(2).setCellValue(item.getCstCalcItNm());

            if (item.getAMenuLevel() == 1) {
                sheet.addMergedRegion(new CellRangeAddress(r, r, 0, 2));
            } else if (item.getAMenuLevel() == 2) {
                sheet.addMergedRegion(new CellRangeAddress(r, r, 0, 1));
            }

            row.createCell(3).setCellValue(item.getCstCalcItCd());
            row.createCell(4).setCellValue(MONEY_FORMAT.format(item.getCostAm()));
            row.createCell(5).setCellValue(item.getCstCalcMthdNm());
            row.createCell(6).setCellValue(PERCENT_FORMAT.format(item.getDrcnstcostCmprPt()) + "%");
            row.createCell(7).setCellValue("");

            for (int i = 0; i <= 7; i++) {
                if (row.getCell(i) != null) {
                    row.getCell(i).setCellStyle(i == 4 ? rightAlignStyle : centerStyle);
                }
            }
        }

        // 열 너비 자동 조정
        for (int i = 0; i <= 7; i++) sheet.autoSizeColumn(i);
    }

    // 재귀적으로 행 작성       시트            작성할 데이터               행 번호             셀 스타일               오른쪽 정렬         데이터가 담긴 한 행을 나타내는 객체
    private void writeRows(Sheet sheet, List<CostItemNode> nodes, AtomicInteger rowNum, CellStyle style, CellStyle rightAlignStyle, List<ExcelRow> excelRows) {
        
        for (CostItemNode node : nodes) {
            RawCostItem item = node.getData();
            if (item == null) continue;

            int level = item.getAMenuLevel();

            // 메뉴 레벨 1은 루트 출력 시에만 처리함
            if (level == 1) {
                writeRows(sheet, node.getChildren(), rowNum, style, rightAlignStyle, excelRows);
                continue;
            }

            int r = rowNum.getAndIncrement();   // 이번에 만들 행 = 이전 행 + 1
            Row row = sheet.createRow(r);

            // 단계 값 추출
            String step1 = getAncestorName(node, 1);
            String step2 = getAncestorName(node, 2);
            String step3 = level == 3 ? item.getCstCalcItNm() : "";

            // 병합 기준용 데이터 수집
            excelRows.add(new ExcelRow(step1, step2, step3, r));    // 해당 행 병합할거면 이거 추가

            // 셀 작성
            row.createCell(0).setCellValue(step1);
            row.createCell(1).setCellValue(step2);
            row.createCell(2).setCellValue(step3);
            String gubun = "소계".equals(item.getCstCalcItNm()) ? getSubtotalCode() : item.getCstCalcItCd();
            row.createCell(3).setCellValue(gubun);
            row.createCell(4).setCellValue(MONEY_FORMAT.format(item.getCostAm()));
            row.createCell(5).setCellValue(item.getCstCalcMthdNm());
            row.createCell(6).setCellValue(PERCENT_FORMAT.format(item.getDrcnstcostCmprPt()) + "%");
            row.createCell(7).setCellValue("소계".equals(item.getCstCalcItNm()) ? "합계" : "");

            for (int i = 0; i <= 7; i++) {  
                if (row.getCell(i) != null){
                    if (i == 4) {   // 4번째행만 오른쪽 정렬 스타일 적용
                        row.getCell(i).setCellStyle(rightAlignStyle); // 도급금액
                    } else {
                        row.getCell(i).setCellStyle(style);
                    }
                } 

                    
            }

            // 자식 재귀 처리
            writeRows(sheet, node.getChildren(), rowNum, style, rightAlignStyle, excelRows);
        }
    }

    // 수직 병합 처리 (step1/step2/step3)
    private void applyVerticalMerge(Sheet sheet, List<ExcelRow> rows, int colIndex) {
        String prev = null;
        int start = -1;

        for (int i = 0; i < rows.size(); i++) {
            String current = switch (colIndex) {
                case 0 -> rows.get(i).step1;
                case 1 -> rows.get(i).step2;
                case 2 -> rows.get(i).step3;
                default -> "";
            };

            if (!Objects.equals(prev, current)) {
                if (start != -1 && i - start > 1) {
                    int from = rows.get(start).rowIndex;
                    int to = rows.get(i - 1).rowIndex;
                    sheet.addMergedRegion(new CellRangeAddress(from, to, colIndex, colIndex));
                }
                start = i;
                prev = current;
            }
        }

        // 마지막 병합
        if (start != -1 && rows.size() - start > 1) {
            int from = rows.get(start).rowIndex;
            int to = rows.get(rows.size() - 1).rowIndex;
            sheet.addMergedRegion(new CellRangeAddress(from, to, colIndex, colIndex));
            sheet.autoSizeColumn(from);
        }
    }

    // 트리 구조 생성
    private CostItemNode buildCostTree(List<RawCostItem> rawList) {
        Map<String, CostItemNode> map = new HashMap<>();
        CostItemNode root = new CostItemNode();

        for (RawCostItem item : rawList) {
            CostItemNode node = new CostItemNode();
            node.setData(item);
            map.put(item.getCstCalcItCd(), node);
        }

        for (RawCostItem item : rawList) {
            CostItemNode node = map.get(item.getCstCalcItCd());
            String parentCd = item.getUpCstCalcItCd();
            if (parentCd != null && map.containsKey(parentCd)) {
                CostItemNode parent = map.get(parentCd);
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
    private void insertSubtotals(CostItemNode parent) {
        for (CostItemNode child : parent.getChildren()) {
            insertSubtotals(child);
        }

        // 자식이 있고, 부모가 레벨 2인 경우에만 소계 생성
        if (!parent.getChildren().isEmpty() && parent.getData() != null && parent.getData().getAMenuLevel() == 2) {

            long total = parent.getChildren().stream().mapToLong(CostItemNode::getTotalCostAmount).sum();

            RawCostItem subtotal = new RawCostItem();
            subtotal.setCstCalcItNm("소계");
            subtotal.setCstCalcItCd("SUB_" + UUID.randomUUID());
            subtotal.setAMenuLevel(3); // 2의 하위 = 3단계
            subtotal.setUpCstCalcItCd(parent.getData().getCstCalcItCd());
            subtotal.setCostAm(total);

            CostItemNode subNode = new CostItemNode();
            subNode.setData(subtotal);
            subNode.setParent(parent);
            parent.getChildren().add(subNode);
        }
    }

    // 특정 레벨의 조상 이름 구하기
    private String getAncestorName(CostItemNode node, int level) {
        CostItemNode current = node;
        while (current != null && current.getData() != null && current.getData().getAMenuLevel() != level) {
            current = current.getParent();
        }
        return (current != null && current.getData() != null) ? current.getData().getCstCalcItNm() : "";
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

}

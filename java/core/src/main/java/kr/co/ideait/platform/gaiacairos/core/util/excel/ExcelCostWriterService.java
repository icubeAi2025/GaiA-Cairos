package kr.co.ideait.platform.gaiacairos.core.util.excel;

import jakarta.servlet.http.HttpServletResponse;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URLEncoder;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelCostWriterService {

    //  브라우저 응답용
    public void writeMultiSheetExcel(HttpServletResponse response, List<ExcelSheetWriter> writers, String filename, String templateNm) throws Exception {
        ClassPathResource template = new ClassPathResource("public/excel/"+templateNm);
        InputStream in = template.getInputStream();
        XSSFWorkbook workbook = new XSSFWorkbook(in);
    
        for (ExcelSheetWriter writer : writers) {
            writer.writeSheet(workbook);
        }
    
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode(filename, "UTF-8") + "\"");
    
        try (OutputStream os = response.getOutputStream()) {
            workbook.write(os);
        }
        workbook.close();
    }

    // OutputStream 저장용(템플릿 사용)
    public void writeMultiSheetExcel(OutputStream outputStream, List<ExcelSheetWriter> writers, String templatePath) {
        try (InputStream in = new FileInputStream(templatePath);
             XSSFWorkbook workbook = new XSSFWorkbook(in)) {

            for (ExcelSheetWriter writer : writers) {
                writer.writeSheet(workbook);
            }

            workbook.write(outputStream);
        } catch (IOException e) {
            throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR, "엑셀 생성 중 파일을 찾을 수 없습니다.", e);
        }
    }

//    // OutputStream 저장용(템플릿 사용)
//    public void writeMultiSheetExcel(OutputStream outputStream, List<ExcelSheetWriter> writers, String templateNm) throws Exception {
//        ClassPathResource template = new ClassPathResource("public/excel/" + templateNm);
//        try (InputStream in = template.getInputStream();
//             XSSFWorkbook workbook = new XSSFWorkbook(in)) {
//
//            for (ExcelSheetWriter writer : writers) {
//                writer.writeSheet(workbook);
//            }
//
//            workbook.write(outputStream);
//        }
//    }

    // OutputStream 저장용(템플릿 사용X)
    public void writeMultiSheetExcel(OutputStream outputStream, List<ExcelSheetWriter> writers) throws Exception {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            for (ExcelSheetWriter writer : writers) {
                writer.writeSheet(workbook);
            }
            workbook.write(outputStream);
        }
    }



















    // private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("#,###");
    // private static final DecimalFormat PERCENT_FORMAT = new DecimalFormat("0.000");

    // private AtomicInteger subtotalCodeIndex = new AtomicInteger(0);

    // // 소계 구분자 자동 생성 (A, B, C, ...)
    // private String getSubtotalCode() {
    //     return String.valueOf((char) ('A' + subtotalCodeIndex.getAndIncrement()));
    // }

    // /**
    //  * 원가 계산서 엑셀 다운로드
    //  * @param response
    //  * @param rawList
    //  * @param sheetNm
    //  * @throws Exception
    //  */
    // public void writeExcel(HttpServletResponse response, List<RawCostItem> rawList, String sheetNm, String cntrctNm) throws Exception {
    //     CostItemNode root = buildCostTree(rawList); // 트리 구조 생성

    //     // 템플릿 엑셀 파일 불러오기
    //     ClassPathResource template = new ClassPathResource("public/excel/calculator_sample2.xlsx");
    //     InputStream in = template.getInputStream();
    //     XSSFWorkbook workbook = new XSSFWorkbook(in);
        
    //     CellStyle centerStyle = createCenterStyle(workbook);
    //     CellStyle rightAlignStyle = createRightAlignStyle(workbook); // 선언 추가

    //     /* 원가계산서 엑셀 생성 */
    //     XSSFSheet sheet = workbook.getSheet(sheetNm);
    //     // 계약(공사)명 작성
    //     Row titleRow = sheet.getRow(2);
    //     titleRow.getCell(0).setCellValue("공사명 : " + cntrctNm);


    //     // 실 데이터 작성
    //     AtomicInteger rowNum = new AtomicInteger(4); // 5행부터 시작
    //     List<ExcelRow> excelRows = new ArrayList<>();

    //     // 트리 구조 순회하면서 데이터 작성
    //     writeRows(sheet, root.getChildren(), rowNum, centerStyle, rightAlignStyle, excelRows);

    //     // 수직 병합 (step1~step3)
    //     applyVerticalMerge(sheet, excelRows, 0);
    //     applyVerticalMerge(sheet, excelRows, 1);
    //     applyVerticalMerge(sheet, excelRows, 2);

    //     // 레벨 1 (최상위) 노드 출력
    //     List<CostItemNode> level1Nodes = root.getChildren();
    //     for (CostItemNode node : level1Nodes) {
    //         RawCostItem item = node.getData();
    //         if (item == null) continue;

    //         int r = rowNum.getAndIncrement();
    //         Row row = sheet.createRow(r);

    //         // 레벨에 따라 병합 범위 조정
    //         row.createCell(0).setCellValue(item.getCstCalcItNm());
    //         row.createCell(1).setCellValue(item.getCstCalcItNm());
    //         row.createCell(2).setCellValue(item.getCstCalcItNm());
    //         if(item.getAMenuLevel() == 1) {
    //             sheet.addMergedRegion(new CellRangeAddress(r, r, 0, 2));
    //         } else if(item.getAMenuLevel() == 2) {
    //             sheet.addMergedRegion(new CellRangeAddress(r, r, 0, 1));
    //         }

    //         // 나머지 데이터 입력
    //         row.createCell(3).setCellValue(item.getCstCalcItCd());
    //         row.createCell(4).setCellValue(MONEY_FORMAT.format(item.getCostAm()));
    //         row.createCell(5).setCellValue(item.getCstCalcMthdNm());
    //         row.createCell(6).setCellValue(PERCENT_FORMAT.format(item.getDrcnstcostCmprPt()) + "%");
    //         row.createCell(7).setCellValue(""); // 비고

    //         for (int i = 0; i <= 7; i++) {
    //             if (row.getCell(i) != null){
    //                 if (i == 4) {
    //                     row.getCell(i).setCellStyle(rightAlignStyle); // 도급금액
    //                 } else {
    //                     row.getCell(i).setCellStyle(centerStyle);
    //                 }
    //             } 
    //         }
    //     }

    //     // 열 너비 자동조정
    //     for (int i = 0; i <= 7; i++) sheet.autoSizeColumn(i);

    //     /* 계약내역서 생성 */

    //     String fileName = cntrctNm + "_착공계_문서.xlsx";
    //     String encodedDownloadFile = URLEncoder.encode(fileName, "UTF-8").replace("+", "%20");

    //     // 파일 다운로드 처리
    //     response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    //     response.setHeader("Content-Disposition", "attachment; filename=" + encodedDownloadFile);
    //     OutputStream os = response.getOutputStream();
    //     workbook.write(os);
    //     workbook.close();
    //     os.flush();
    //     os.close();
    // }

    // // 재귀적으로 행 작성
    // private void writeRows(Sheet sheet, List<CostItemNode> nodes, AtomicInteger rowNum, CellStyle style, CellStyle rightAlignStyle, List<ExcelRow> excelRows) {
    //     for (CostItemNode node : nodes) {
    //         RawCostItem item = node.getData();
    //         if (item == null) continue;

    //         int level = item.getAMenuLevel();

    //         // 메뉴 레벨 1은 루트 출력 시에만 처리함
    //         if (level == 1) {
    //             writeRows(sheet, node.getChildren(), rowNum, style, rightAlignStyle, excelRows);
    //             continue;
    //         }

    //         int r = rowNum.getAndIncrement();
    //         Row row = sheet.createRow(r);

    //         // 단계 값 추출
    //         String step1 = getAncestorName(node, 1);
    //         String step2 = getAncestorName(node, 2);
    //         String step3 = level == 3 ? item.getCstCalcItNm() : "";

    //         // 병합 기준용 데이터 수집
    //         excelRows.add(new ExcelRow(step1, step2, step3, r));

    //         // 셀 작성
    //         row.createCell(0).setCellValue(step1);
    //         row.createCell(1).setCellValue(step2);
    //         row.createCell(2).setCellValue(step3);
    //         String gubun = "소계".equals(item.getCstCalcItNm()) ? getSubtotalCode() : item.getCstCalcItCd();
    //         row.createCell(3).setCellValue(gubun);
    //         row.createCell(4).setCellValue(MONEY_FORMAT.format(item.getCostAm()));
    //         row.createCell(5).setCellValue(item.getCstCalcMthdNm());
    //         row.createCell(6).setCellValue(PERCENT_FORMAT.format(item.getDrcnstcostCmprPt()) + "%");
    //         row.createCell(7).setCellValue("소계".equals(item.getCstCalcItNm()) ? "합계" : "");

    //         for (int i = 0; i <= 7; i++) {
    //             if (row.getCell(i) != null){
    //                 if (i == 4) {
    //                     row.getCell(i).setCellStyle(rightAlignStyle); // 도급금액
    //                 } else {
    //                     row.getCell(i).setCellStyle(style);
    //                 }
    //             } 

                    
    //         }

    //         // 자식 재귀 처리
    //         writeRows(sheet, node.getChildren(), rowNum, style, rightAlignStyle, excelRows);
    //     }
    // }

    // // 수직 병합 처리 (step1/step2/step3)
    // private void applyVerticalMerge(Sheet sheet, List<ExcelRow> rows, int colIndex) {
    //     String prev = null;
    //     int start = -1;

    //     for (int i = 0; i < rows.size(); i++) {
    //         String current = switch (colIndex) {
    //             case 0 -> rows.get(i).step1;
    //             case 1 -> rows.get(i).step2;
    //             case 2 -> rows.get(i).step3;
    //             default -> "";
    //         };

    //         if (!Objects.equals(prev, current)) {
    //             if (start != -1 && i - start > 1) {
    //                 int from = rows.get(start).rowIndex;
    //                 int to = rows.get(i - 1).rowIndex;
    //                 sheet.addMergedRegion(new CellRangeAddress(from, to, colIndex, colIndex));
    //             }
    //             start = i;
    //             prev = current;
    //         }
    //     }

    //     // 마지막 병합
    //     if (start != -1 && rows.size() - start > 1) {
    //         int from = rows.get(start).rowIndex;
    //         int to = rows.get(rows.size() - 1).rowIndex;
    //         sheet.addMergedRegion(new CellRangeAddress(from, to, colIndex, colIndex));
    //         sheet.autoSizeColumn(from);
    //     }
    // }

    // // 트리 구조 생성
    // private CostItemNode buildCostTree(List<RawCostItem> rawList) {
    //     Map<String, CostItemNode> map = new HashMap<>();
    //     CostItemNode root = new CostItemNode();

    //     for (RawCostItem item : rawList) {
    //         CostItemNode node = new CostItemNode();
    //         node.setData(item);
    //         map.put(item.getCstCalcItCd(), node);
    //     }

    //     for (RawCostItem item : rawList) {
    //         CostItemNode node = map.get(item.getCstCalcItCd());
    //         String parentCd = item.getUpCstCalcItCd();
    //         if (parentCd != null && map.containsKey(parentCd)) {
    //             CostItemNode parent = map.get(parentCd);
    //             node.setParent(parent);
    //             parent.getChildren().add(node);
    //         } else {
    //             root.getChildren().add(node);
    //         }
    //     }

    //     insertSubtotals(root);
    //     return root;
    // }

    // // 소계 항목 자동 추가 (메뉴 레벨 3인 경우에만)
    // private void insertSubtotals(CostItemNode parent) {
    //     for (CostItemNode child : parent.getChildren()) {
    //         insertSubtotals(child);
    //     }

    //     // 자식이 있고, 부모가 레벨 2인 경우에만 소계 생성
    //     if (!parent.getChildren().isEmpty() && parent.getData() != null && parent.getData().getAMenuLevel() == 2) {

    //         long total = parent.getChildren().stream().mapToLong(CostItemNode::getTotalCostAmount).sum();

    //         RawCostItem subtotal = new RawCostItem();
    //         subtotal.setCstCalcItNm("소계");
    //         subtotal.setCstCalcItCd("SUB_" + UUID.randomUUID());
    //         subtotal.setAMenuLevel(3); // 2의 하위 = 3단계
    //         subtotal.setUpCstCalcItCd(parent.getData().getCstCalcItCd());
    //         subtotal.setCostAm(total);

    //         CostItemNode subNode = new CostItemNode();
    //         subNode.setData(subtotal);
    //         subNode.setParent(parent);
    //         parent.getChildren().add(subNode);
    //     }
    // }

    // // 특정 레벨의 조상 이름 구하기
    // private String getAncestorName(CostItemNode node, int level) {
    //     CostItemNode current = node;
    //     while (current != null && current.getData() != null && current.getData().getAMenuLevel() != level) {
    //         current = current.getParent();
    //     }
    //     return (current != null && current.getData() != null) ? current.getData().getCstCalcItNm() : "";
    // }

    // // 헤더 스타일
    // private CellStyle createHeaderStyle(Workbook wb) {
    //     CellStyle style = wb.createCellStyle();
    //     Font font = wb.createFont(); font.setBold(true);
    //     style.setFont(font);
    //     style.setAlignment(HorizontalAlignment.CENTER);
    //     style.setVerticalAlignment(VerticalAlignment.CENTER);
    //     setBorders(style);
    //     return style;
    // }

    // // 일반 셀 중앙 정렬 스타일
    // private CellStyle createCenterStyle(Workbook wb) {
    //     CellStyle style = wb.createCellStyle();
    //     style.setAlignment(HorizontalAlignment.CENTER);
    //     style.setVerticalAlignment(VerticalAlignment.CENTER);
    //     setBorders(style);
    //     return style;
    // }

    // // 테두리 설정
    // private void setBorders(CellStyle style) {
    //     style.setBorderTop(BorderStyle.THIN);
    //     style.setBorderBottom(BorderStyle.THIN);
    //     style.setBorderLeft(BorderStyle.THIN);
    //     style.setBorderRight(BorderStyle.THIN);
    // }

    // // 오른쪽 정렬 스타일 (금액 전용)
    // private CellStyle createRightAlignStyle(Workbook wb) {
    //     CellStyle style = wb.createCellStyle();
    //     style.setAlignment(HorizontalAlignment.RIGHT); // 오른쪽 정렬
    //     style.setVerticalAlignment(VerticalAlignment.CENTER);
    //     setBorders(style);
    //     return style;
    // }


}
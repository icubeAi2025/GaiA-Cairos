package kr.co.ideait.platform.gaiacairos.core.util.excel;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelLbrEqWriterService {
    public void writeMultiSheetExcel(HttpServletResponse response, List<ExcelSheetWriter> writers, String filename)
            throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook();

        for (ExcelSheetWriter writer : writers) {
            writer.writeSheet(workbook);
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + URLEncoder.encode(filename, "UTF-8") + "\"");

        try (OutputStream os = response.getOutputStream()) {
            workbook.write(os);
        }
        workbook.close();
    }
}

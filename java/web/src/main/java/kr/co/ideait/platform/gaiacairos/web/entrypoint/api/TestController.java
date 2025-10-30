package kr.co.ideait.platform.gaiacairos.web.entrypoint.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.type.FileUploadType;
import kr.co.ideait.platform.gaiacairos.core.util.FileService;
import kr.co.ideait.platform.gaiacairos.core.util.PdfUtil;
import kr.co.ideait.platform.gaiacairos.core.util.restclient.DocumentServiceClient;
import kr.co.ideait.platform.gaiacairos.core.util.restclient.UbiReportClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/webApi/test")
@RequiredArgsConstructor
public class TestController extends AbstractController {


    @Autowired
    UbiReportClient ubiReportClient;

    @Autowired
    private FileService fileService;

    @Autowired
    DocumentServiceClient documentServiceClient;

    @GetMapping("/report")
    public void report(@RequestParam Map<String, String> requestParams) {
        Map<String, String> callbackInfo = new HashMap<>();
        callbackInfo.put("x-auth", "token");
        callbackInfo.put("reqKey", "req_cd");
        callbackInfo.put("reqValue", "value");
        callbackInfo.put("pdfName", "pdf_doc");
//        callbackInfo.put("callbackUrl", "http://local.idea-platform.net:8091/interface/dailyreportDoc/callback-result");
        callbackInfo.put("callbackUrl", "http://dev.idea-platform.net:8091/webApi/dailyreportDoc/callback-result");

        ubiReportClient.export(new String[]{"sample_01.jrf"}, requestParams, callbackInfo);
    }

//    @GetMapping("/pdf-merge-save")
//    public void pdfMergeSaveDownload(@RequestParam Map reqBody, HttpServletResponse response) throws IOException {
//
//        File reportFile = ubiReportClient.download(new String[]{"sample_01.jrf"}, new HashMap<>(), (String)reqBody.get("name"));
//
//        List<File> files = new ArrayList<>();
//        files.add(reportFile);
//        files.addAll(Arrays.stream( MapUtils.getString(reqBody, "files").split(",") )
//                .map(File::new)
//                .collect(Collectors.toList()));
//
//        String savedFilePath = (String)reqBody.get("path");
//        String savedFileName = (String)reqBody.get("name");
//
//        byte[] pdfBytes = PdfUtil.mergeToBytes(files, String.format("%s/%s", uploadPath, getUploadPathByWorkType(FileUploadType.TEMP)), savedFileName);
//
//
//        String encodedOriginalFileName = UriUtils.encode(savedFileName, StandardCharsets.UTF_8);
//        String contentDisposition = String.format("attachment; filename=\"%s\"", encodedOriginalFileName);
//
//        response.setContentType("application/pdf");
//        response.setContentLength(pdfBytes.length);
//        response.setHeader("Content-Disposition", contentDisposition);
//        response.setHeader("X-FILE-NAME", encodedOriginalFileName);
//        response.setHeader("Content-Transfer-Encoding","binary");
//
//        response.getOutputStream().write(pdfBytes);
//        response.getOutputStream().flush();
//        response.getOutputStream().close();
//    }

// 착공계 개발 완료 후 삭제!!!!!	
//	@Autowired
//	ChagGongGyeDocClient chagGongGyeDocClient;
//	
//	@GetMapping("/make-doc/{docType}/{docForm}/{cntrctNo}")
//    public void noReturnValue(@PathVariable("docType") String docType, @PathVariable("docForm") String docForm, @PathVariable("cntrctNo") String cntrctNo) {
//		
//		log.info("docType: {}", docType);
//		log.info("docForm: {}", docForm);
//		log.info("cntrctNo: {}", cntrctNo);
//		
//		String makeDocType = docType.split("_")[1];
//		
//		Map<String, Object> addData = new HashMap<>();
//		
//		log.info("makeDocType: {}", makeDocType);
//		
//		if("D0001".equals(makeDocType)) {
//			chagGongGyeDocClient.getDocD0001(addData, docForm, cntrctNo, "02");
//		}else if("D0002".equals(makeDocType)) {
//			chagGongGyeDocClient.getDocD0002(addData, docForm, cntrctNo, "02");
//		}
//
//    }
//	
//	
//	
//	@PostMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<Map<String, Object>> sampleApi(List<MultipartFile> paramFile, Map params, HttpServletRequest request) throws IOException {
//
//
//
//        for (MultipartFile file : paramFile) {
//            Path filePath = Path.of("D:/11", file.getOriginalFilename());
//
//            try {
//                file.transferTo(filePath);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
//
//        Map<String, Object> result = Maps.newHashMap();
//        result.put("resultCode", "success");
//
//        log.info("result: {}", result);
//
//        return ResponseEntity.ok(result);
//    }

    @PostMapping("/upload")
    public Result upload(@RequestBody List<Map<String,Object>> metas) throws JsonProcessingException {

        String targetDir = String.format("%s/%s", uploadPath,getUploadPathByWorkType(FileUploadType.ETC));
        List<FileService.FileMeta> movedFileMetas = new ArrayList<>();
        for(Map<String,Object> metaMap:metas){
            String meta = objectMapper.writeValueAsString(metaMap);
            FileService.FileMeta oldMeta = objectMapper.readValue(meta, FileService.FileMeta.class);
            FileService.FileMeta newMeta = fileService.build(meta,targetDir);

            //DB 로직 수행
            //~~Service.insert(newMeta);
            //실패시 oldMeta 이용해서 다시 원상복구


            //마지막으로 실제 파일 이동
            fileService.moveFile(oldMeta.getFilePath(), newMeta.getFilePath());
            movedFileMetas.add(newMeta);
        }

        return Result.ok().put("movedFileMetas",movedFileMetas);
    }


    /**
     * 문서 삭제 취소(테스트)
     */
    @PostMapping("/delete/rollback")
    public void rollbackRemovedDocument(@RequestBody Map<String, Object> params) {
        Result result = documentServiceClient.rollbackRemovedDocument(params);
        log.info("rollbackRemovedDocument:  {}", result);
    }

    @GetMapping("/mock")
    public Result getMockDatas(Map<String, Object> params) {
        List<Map> datas = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Map data = new HashMap();
            data.put("id", i + 1);
            data.put("name", "name_" + i + 1);
            data.put("age", i);

            datas.add(data);
        }

        return Result.ok().put("items", datas);
    }

    @GetMapping("/mock/{id}")
    public Result getMockData(@PathVariable("id") Long id, Map<String, Object> params) {
        Map data = new HashMap();
        data.put("id", id + 1);
        data.put("name", "name_" + id + 1);
        data.put("age", id);

        return Result.ok(data);
    }

    @PostMapping("/mock/{id}")
    public Result saveMockData(@PathVariable("id") Long id, @RequestBody Map<String, Object> params) {
        Map data = new HashMap(params);

        return Result.ok(data);
    }
}

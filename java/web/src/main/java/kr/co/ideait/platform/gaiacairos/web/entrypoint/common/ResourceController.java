package kr.co.ideait.platform.gaiacairos.web.entrypoint.common;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.comp.mail.MailComponent;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.MailForm;
import kr.co.ideait.platform.gaiacairos.core.type.FileUploadType;
import kr.co.ideait.platform.gaiacairos.core.util.BizServiceInvoker;
import kr.co.ideait.platform.gaiacairos.core.util.FileService;
import kr.co.ideait.platform.gaiacairos.core.util.PdfUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@Slf4j
@RestController
@RequestMapping("/resource")
public class ResourceController extends AbstractController {

    @Autowired
    private FileService fileService;

    @Autowired
    MailComponent mailComponent;

    @Autowired
    private BizServiceInvoker bizServiceInvoker;

//    @GetMapping("/download/log_")
//    @Description(name = "로그 다운로드", description = "로그 다운로드", type = Description.TYPE.MEHTOD)
//    public ResponseEntity<Resource> downloadLog_(CommonReqVo commonReqVo, HttpServletRequest request, HttpServletResponse response) {
//        UrlResource resource;
//
//        final String path = String.format("D:/logs/%s/application.log", platform);
//
//        try {
//            resource = new UrlResource(String.format("file:%s", path));
    ////            resource = new UrlResource(String.format("file:/home/ubuntu/logs/%s.log", platform));
//        } catch (MalformedURLException e) {
//            log.error("the given File path is not valid");
//            e.getStackTrace();
//            throw new RuntimeException("the given URL path is not valid");
//        }
//
//        String originalFileName = String.format("%s.log", platform);
//        String encodedOriginalFileName = UriUtils.encode(originalFileName, StandardCharsets.UTF_8);
//
//        String contentDisposition = String.format("attachment; filename=\"%s\"", encodedOriginalFileName);
//
//        return ResponseEntity
//                .ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION,contentDisposition)
//                .body(resource);
//    }

    @GetMapping("/pdf-merge")
    public void pdfMergeDownload(@RequestParam("transactionId") String transactionId, @RequestParam Map reqBody, HttpServletResponse response) throws IOException {
        Map<String, Object> result = bizServiceInvoker.invoke(transactionId, reqBody);

        if (reqBody.containsKey("files")) {
            String[] files = MapUtils.getString(reqBody, "files").split(",");
            result.put("files", Arrays.stream(files).map(File::new).collect(Collectors.toList()));
            result.put("savedFilePath", reqBody.get("path"));
            result.put("savedFileName", reqBody.get("name"));
        }

//        String savedFilePath = (String)result.get("savedFilePath");
        String savedFileName = (String)result.get("savedFileName");
//        List<String> fileList = (List<String>)result.get("files");
//        List<File> files = fileList.stream().map(File::new).collect(Collectors.toList());
        List<File> files = (List<File>)result.get("files");

        byte[] pdfBytes = PdfUtil.mergeToBytes(files, String.format("%s/%s", uploadPath, getUploadPathByWorkType(FileUploadType.TEMP)), savedFileName);

        if (pdfBytes == null) {
            pdfBytes = new byte[0];
        }

        String encodedOriginalFileName = UriUtils.encode(savedFileName, StandardCharsets.UTF_8);
        String contentDisposition = String.format("attachment; filename=\"%s\"", encodedOriginalFileName);

        response.setContentType("application/pdf");
        response.setContentLength(pdfBytes.length);
        response.setHeader("Content-Disposition", contentDisposition);
        response.setHeader("X-FILE-NAME", encodedOriginalFileName);
        response.setHeader("Content-Transfer-Encoding","binary");

        response.getOutputStream().write(pdfBytes);
        response.getOutputStream().flush();
        response.getOutputStream().close();
    }

    /**
     * 공통 파일 업로더
     * @param files
     * @param params
     * @param request
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Result upload(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam Map params,
            HttpServletRequest request
    ) throws JsonProcessingException {
        log.info("params {}", params);

        Map<String, Object> result = Maps.newHashMap();
        result.put("result", true);

        List<FileService.FileMeta> metas = Lists.newArrayList();

        if (files == null || files.isEmpty()) {
            result.put("result", false);
            return Result.ok(result);
        }

        files.forEach(file -> {
            FileService.FileMeta fileMeta = fileService.save(getUploadPathByWorkType(FileUploadType.TEMP), file);
            fileMeta.setMode("C");

            metas.add( fileMeta );
        });

        result.put("metas", metas);

        return Result.ok(result);
    }

    /**
     * 공통 temp 파일 삭제
     * @param oldFileMeta ({@link kr.co.ideait.platform.gaiacairos.core.util.FileService.FileMeta}) 지우고자 하는 temp 파일의 메타정보
     * @return
     */
    @PostMapping(value="/delete")
    public Result delete(@RequestBody FileService.FileMeta oldFileMeta) {
        if(fileService.deleteFile(oldFileMeta.getDirPath(),oldFileMeta.getFileName())){
            return Result.ok();
        }
        return Result.nok(ErrorType.INVAILD_INPUT_DATA,"temp 파일 삭제 실패");
    }

    @GetMapping(value="/download")
    public ResponseEntity<Resource> download(@RequestParam("filePath") String fullPath, @RequestParam("orgName") String orgName){
        Path filePath = Path.of(fullPath);
        if (Files.notExists(filePath)) {
            throw new GaiaBizException(ErrorType.NO_DATA, "file is not exists");
        }

        Resource resource = new FileSystemResource(filePath);

        String dwName = UriUtils.encode(orgName, StandardCharsets.UTF_8);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.builder("attachment").filename(dwName).build());

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(filePath.toFile().length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
//    public void download(@RequestBody FileService.FileMeta fileMeta, HttpServletResponse response) {
//        if(fileMeta != null) {
//            try {
//                Path filePath = Path.of(fileMeta.getDirPath(), fileMeta.getFileName());
//                if (!Files.exists(filePath)) {
//                    throw new GaiaBizException(ErrorType.NO_DATA, "file is not exist");
//                }
//                Resource resource = fileService.getFile(filePath);
//                String encodedOriginalFileName = UriUtils.encode(fileMeta.getOriginalFilename(), StandardCharsets.UTF_8);
//                if (resource != null) {
//                    String contentType = Files.probeContentType(filePath);
//                    if (contentType == null) {
//                        contentType = "application/octet-stream";
//                    }
//
//                    response.setContentType(contentType);
//                    response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedOriginalFileName + "\"");
//                    response.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(Files.size(filePath)));
//
//                    try (InputStream is = resource.getInputStream();
//                         OutputStream os = response.getOutputStream()
//                    ) {
//                        is.transferTo(os);
//                        os.flush();
//                    }
//                }
//            }catch(IOException e) {
//                throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR,"File Download Failed");
//            }
//        }
//    }

//    @Deprecated
//    @GetMapping("/stg-only/download/log")
//    @Description(name = "로그 다운로드", description = "로그 다운로드", type = Description.TYPE.MEHTOD)
//    public void downloadLog(CommonReqVo commonReqVo, HttpServletRequest request, HttpServletResponse response) {
//
//        if ( !StringUtils.equals(platform, "staging") ) {
//            return;
//        }
//
//        final String path = String.format("/home/ubuntu/logs/%s", platform);
//
//        String originalFileName = String.format("%s.log", platform);
//        String encodedOriginalFileName = UriUtils.encode(originalFileName, StandardCharsets.UTF_8);
//
//        String contentDisposition = String.format("attachment; filename=\"%s\"", encodedOriginalFileName);
//
//        try {
//            byte[] files = FileUtils.readFileToByteArray(new File(path));
//
//            response.setContentType("application/octet-stream");
//            response.setContentLength(files.length);
//            response.setHeader("Content-Disposition", contentDisposition);
//            response.setHeader("Content-Transfer-Encoding","binary");
//
//            response.getOutputStream().write(files);
//            response.getOutputStream().flush();
//            response.getOutputStream().close();
//        } catch (IOException e){
//            log.error(e.getMessage());
//            e.getStackTrace();
//        }
//    }

    /**
     * 현장개설요청 신규신청시 이메일 발송
     * 
     * @throws UnsupportedEncodingException
     */
    @PostMapping("/mail/send/pjtInstall")
    @Description(name = "현장개설요청 이메일 발송", description = "현장개설요청 신규신청시 이메일 발송", type = Description.TYPE.MEHTOD)
    public void mailSend(CommonReqVo commonReqVo, HttpServletRequest request) throws UnsupportedEncodingException {
        mailComponent.sendPJTInstall(commonReqVo.getLoginId());
    }

    /**
     * 사용요청
     */
    @PostMapping("/mail/send/request")
    @Description(name = "사용요청 이메일 발송", description = "사용요청 이메일 발송", type = Description.TYPE.MEHTOD)
    public Result sendGaiARequest(@RequestBody MailForm.useRequest useRequest, CommonReqVo commonReqVo,
            HttpServletRequest request) throws UnsupportedEncodingException {
        mailComponent.sendUseRequest(useRequest, commonReqVo.getPlatform());
        return Result.ok();
    }
}

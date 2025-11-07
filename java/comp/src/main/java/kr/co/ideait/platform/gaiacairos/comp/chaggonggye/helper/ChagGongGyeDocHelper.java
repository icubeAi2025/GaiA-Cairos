package kr.co.ideait.platform.gaiacairos.comp.chaggonggye.helper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.co.ideait.platform.gaiacairos.comp.construction.service.ResourceService;
import kr.co.ideait.platform.gaiacairos.comp.project.service.ContractstatusService;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DcStorageMain;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction.resource.ResourceMybatisParam;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.contract.contractstatus.ContractstatusMybatisParam;
import kr.co.ideait.platform.gaiacairos.core.util.excel.*;
import kr.co.ideait.platform.gaiacairos.core.util.restclient.ICubeClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.ideait.platform.gaiacairos.comp.chaggonggye.service.ChagGongGyeDocService;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.util.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChagGongGyeDocHelper {

    private final ICubeClient iCubeClient;

    @Autowired
    private ChagGongGyeDocService chagGongGyeDocService;

    @Autowired
    private FileService fileService;

    @Autowired
    ContractstatusService contractstatusService;

    @Autowired
    ExcelCostWriterService excelCostWriterService;

    @Autowired
    ResourceService resourceService;

    @Autowired
    ExcelMtrlWriterService excelMtrlWriterService;

    @Value("${link.domain.url}")
    private String apiDomain;

    /**
     * 착공신고서 문서 생성 요청
     * @param addData
     * @param naviId
     * @param docId
     * @return
     */
    public Map<String, Object> makeChagGongGyeDoc(Map<String, Object> addData, String naviId, String docId) {
        log.info("docId: {}", docId);

        Map<String, Object> result = new HashMap<>();

        String[] docParam = docId.split("_");

        //문서 템플릿 조회
        //docParam[0] : 착공계 문서 관리번호(cbgn_no)
        Map<String, Object> docTemplate = chagGongGyeDocService.selectDocTemplate(docParam[0]);

        // TODO: 추후 아래 코드로 변경.
        //Map<String, Object> docTemplate = chagGongGyeDocService.selectDocTemplateToStorageMain(docId);

        if (docTemplate == null || docTemplate.isEmpty()) {
            throw new GaiaBizException(ErrorType.NO_DATA, "문서 템플릿이 존재하지 않습니다.");
        }

        //문서 기본 데이터 조회
        //docParam[1] : 착공계 문서 종류(cbgn_doc_type)
        //docParam[2] : 착공계 문서 타입(cbgn_doc_form)
        //docParam[3] : 계약 번호 (cntrct_no)
        Map<String, Object> defaultParamsData = chagGongGyeDocService.selectDefaultData(docParam[1]+docParam[2], docParam[3]);

        //문서 추가 데이터 조회
        List<Map<String, Object>> addParamsData = chagGongGyeDocService.selectAddData(naviId, docId);

        log.info("addParamsData: {}", addParamsData);
        log.info("defaultParamsData: {}", defaultParamsData);

        log.info("docParam[0]: {}", docParam[0]);
        log.info("docParam[1]: {}", docParam[1]);
        log.info("docParam[2]: {}", docParam[2]);

        String docTemplateDiskNm = (String) docTemplate.get("doc_disk_nm");
        String originalDocDiskNm = docTemplateDiskNm.substring(0, docTemplateDiskNm.indexOf(".")) + ".hwpx";

        Resource resource = fileService.getFile((String) docTemplate.get("doc_disk_path"), originalDocDiskNm);

        // 리소스 유효성 확인
        if (resource == null || !resource.exists() || !resource.isFile()) {
            log.error("템플릿 파일 리소스를 찾을 수 없습니다. path: {}, name: {}", docTemplate.get("doc_disk_path"), originalDocDiskNm);
            result.put("resultCode", "02");
            result.put("resultMsg", "속성 데이터 수정 후, 문서 템플릿 파일을 찾을 수 없어 문서 생성에 실패하였습니다.");
            return result;
        }

        log.info("resource: {}", resource);
        log.info("resource.getFilename(): {}", resource.getFilename());
        log.info("resource.isFile(): {}", resource.isFile());

        try {
            // MultipartBodyBuilder를 사용하여 multipart/form-data 형식으로 요청 본문을 생성
            MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();

            // 문서 기본 데이터 설정
            defaultParamsData.keySet().forEach(key -> multipartBodyBuilder.part(key, defaultParamsData.get(key)));

            StringBuilder fileParamNm = new StringBuilder();
            if(addParamsData.size() > 0) {
                addParamsData.forEach(item -> {
                    // attrbt_type 값에 따라 데이터 처리
                    // "TXTA" : TextArea
                    // "ATCH" : 첨부파일
                    // "DAT" : 날짜
                    // "NUM" : 숫자
                    // "TXT" : 일반 텍스트
                    // "HTML" : JSON 문자열
                    if("TXTA".equals(item.get("attrbt_type"))) {
                        String[] textArea = ((String) item.get("attrbt_cntnts")).split("\\n");

                        log.info("textArea.length: {}", textArea.length);
                        multipartBodyBuilder.part((String) item.get("attrbt_cd"), textArea);
                    }
                    else if("ATCH".equals(item.get("attrbt_type"))) {
                        fileParamNm.append("@").append(item.get("attrbt_cd"));
                        String[] addFileParam = ((String) item.get("attrbt_cntnts")).split("_");

                        Resource addFile = fileService.getFile(addFileParam[0], addFileParam[1]);
                        if(addFile != null) {
                            try {
                                multipartBodyBuilder
                                        .part((String) item.get("attrbt_cd"), new InputStreamResource(addFile.getInputStream()))
                                        .header("Content-Disposition", String.format("form-data; name=%s; filename=%s", (String) item.get("attrbt_cd"), URLEncoder.encode(StringUtils.defaultString(addFile.getFilename(),"addFile"), StandardCharsets.UTF_8)));
                            } catch (IOException e) {
                                log.error("파일 첨부에 실패하였습니다.", e);
                                throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR, "파일 첨부에 실패하였습니다.");
                            }
                        }
                    }
                    else if("DAT".equals(item.get("attrbt_type")) || "NUM".equals(item.get("attrbt_type")) || "TXT".equals(item.get("attrbt_type"))) {
                        multipartBodyBuilder.part((String) item.get("attrbt_cd"), (String)item.get("attrbt_cntnts"));
                    }
                    else if("HTML".equals(item.get("attrbt_type"))) {
                        /* 2025.06.05 수정할 코드*/
                        // JSON 객체 단위로 분리 (쉼표로만 구분된 경우)
                        ObjectMapper objectMapper = new ObjectMapper();
                        String jsonString = (String) item.get("attrbt_cntnts");
                        String[] jsonObjects = jsonString.split("\\|");

                        if(!jsonString.isEmpty()){
                            for (String jsonObj : jsonObjects) {
                                // 2. 각각의 JSON 문자열을 Map으로 파싱
                                Map<String, String> parsedMap = null;
                                try {
                                    parsedMap = objectMapper.readValue(jsonObj, new TypeReference<Map<String, String>>() {});
                                } catch (JsonProcessingException e) {
                                    throw new RuntimeException("잘못된 JSON 형식입니다: " + jsonString, e);
                                }

                                // 3. Map에는 항상 1개의 key-value만 존재하므로 바로 꺼냄
                                for (Map.Entry<String, String> entry : parsedMap.entrySet()) {
                                    multipartBodyBuilder.part(entry.getKey(), entry.getValue());
                                }
                            }
                        }

                    }

                });
            }

            //윈도우 서버에 보내야 할 요청 파라미터 설정
            multipartBodyBuilder.part("doc_cd_list", docParam[1]+docParam[2]);
            multipartBodyBuilder.part("req_cd", naviId+"~"+docId);
            multipartBodyBuilder.part("cllbck_url", apiDomain+"/webApi/chagGongGyeDoc/callback-result");

            multipartBodyBuilder
                    .part(docParam[1]+docParam[2], new InputStreamResource(resource.getInputStream()))
                    .header("Content-Disposition", String.format("form-data; name=%s; filename=%s",docParam[1]+docParam[2], URLEncoder.encode(StringUtils.defaultString(resource.getFilename(),"paramFile"), StandardCharsets.UTF_8)));

            // 현장조직도 문서의 경우, 사용자가 업로드한 파일이 없는 경우
            if("D000201".equals(docParam[1]+docParam[2])) {
                log.info("fileParamNm: {}", fileParamNm);
                boolean checkValue = true;
                String[] reFileParamNm = fileParamNm.toString().split("@");

                for (String compareFileNm : reFileParamNm) {
                    log.info("compareFileNm: {}", compareFileNm);
                    // 사용자가 추가한 조직도 이미지가 있으면 pass
                    if("orgchrt_atch_file".equals(compareFileNm)) {
                        checkValue = false;
                        break;
                    }
                }

                log.info("checkValue: {}", checkValue);
                if (checkValue) {
                    // 사용자가 추가한 조직도 이미지가 없으면 사업 - 조직도의 이미지 파일 전송
                    if (defaultParamsData.get("file_nm") != null || !"".equals(defaultParamsData.get("file_nm")) || defaultParamsData.get("file_nm") != null) {
                        Resource img = fileService.getFile((String) defaultParamsData.get("file_disk_path"), (String) defaultParamsData.get("file_disk_nm"));

                        if(img != null) {
                            multipartBodyBuilder
                                    .part("orgchrt_atch_file", new InputStreamResource(img.getInputStream()))
                                    .header("Content-Disposition", String.format("form-data; name=%s; filename=%s", "orgchrt_atch_file", URLEncoder.encode(StringUtils.defaultString(img.getFilename(),"orgchrtAtchFile"), StandardCharsets.UTF_8)));
                        }
                    }
                }
            }

            log.info("multipartBodyBuilder: {}", multipartBodyBuilder);

            // 요청 설정
            iCubeClient.convertHwpxToPdf(multipartBodyBuilder);
        } catch (GaiaBizException | IOException e) {
            log.error("문서 생성 중 예외 발생", e);
            result.put("resultCode", "99");
            result.put("resultMsg", "문서 생성 처리 중 알 수 없는 오류가 발생하였습니다. 관리자에게 문의하세요.");
        }

        return result;
    }

    @Transactional
    public Map<String, Object> makeChagGongGyeDocToExcel(DcStorageMain saveStorageMain, String cbgnDocType, String cntrctNo, String cntrctNm) {
        Map<String, Object> result = new HashMap<>();

        // 메모리 상에 엑셀을 작성할 ByteArrayOutputStream
        ByteArrayOutputStream contractOut = new ByteArrayOutputStream();

        //문서 템플릿 조회
        //docId.split(":")[0] : 착공계 문서 관리번호(cbgn_no)
        String cbgnNo = saveStorageMain.getDocId().split("_")[0];
        Map<String, Object> docTemplate = chagGongGyeDocService.selectDocTemplate(cbgnNo);

        if (docTemplate == null || docTemplate.isEmpty()) {
            throw new GaiaBizException(ErrorType.NO_DATA, "문서 템플릿이 존재하지 않습니다.");
        }

        // 사용할 엑셀 템플릿 파일 경로 지정 (물리 경로)
        String excelTemplatePath = Path.of((String) docTemplate.get("doc_disk_path"), (String) docTemplate.get("doc_disk_nm")).toAbsolutePath().toString();
        // 엑셀 작성
        // 계약내역서(C0001)
        if("C0001".equals(cbgnDocType)) {
            List<ContractstatusMybatisParam.RawContractItem> contractList = contractstatusService.getContractBidConstructionList(cntrctNo);
            if (contractList == null || contractList.isEmpty()) {
                log.debug("계약 내역이 존재하지 않습니다.");
            }
            try {
                excelCostWriterService.writeMultiSheetExcel(
                        contractOut,
                        List.of(new ContractSheetWriter(contractList, "Sheet1", cntrctNm)),
                        excelTemplatePath);
            } catch (GaiaBizException e) {
                log.error("Exception fail", e);
                log.error("Exception error messages : {}", e.getMessage());
                result.put("resultCode", "04");
                result.put("resultMsg", "엑셀 데이터를 입력하는 중 문제가 발생했습니다.");
            }
        }
        // 금차내역서(C0002)
        else if("C0002".equals(cbgnDocType)){
            List<ContractstatusMybatisParam.RawCbsItem> cbsList = contractstatusService.getCbsConstructionList(cntrctNo);
            if (cbsList == null || cbsList.isEmpty()) {
                log.info("금차 내역이 존재하지 않습니다.");
            }
            
            try {
                excelCostWriterService.writeMultiSheetExcel(
                        contractOut,
                        List.of(new CbsSheetWriter(cbsList, "Sheet1", cntrctNm)),
                        excelTemplatePath);
            } catch (GaiaBizException e) {
                log.error("Exception fail", e);
                log.error("Exception error messages : {}", e.getMessage());
                result.put("resultCode", "04");
                result.put("resultMsg", "엑셀 데이터를 입력하는 중 문제가 발생했습니다.");
            }
        }
        // 관급자재 (C0003)
        else if("C0003".equals(cbgnDocType)) {
            List<ExcelSheetWriter> govWriters = new ArrayList<>();
            String unitCnstTypeA = "A";
            ResourceMybatisParam.RawGovsplyMtrlItem govsplyMtrlList1 = resourceService.getGovsplyMtrlList(cntrctNo, unitCnstTypeA);

            if (govsplyMtrlList1 != null && govsplyMtrlList1.getRawGovsplyMtrlList() != null
                    && !govsplyMtrlList1.getRawGovsplyMtrlList().isEmpty()) {
                govWriters.add(new GovsplyMtrlSheetWriter(govsplyMtrlList1, "건축, 토목, 조경", cntrctNm));
            }

            String unitCnstTypeB = "B";
            ResourceMybatisParam.RawGovsplyMtrlItem govsplyMtrlList2 = resourceService.getGovsplyMtrlList(cntrctNo, unitCnstTypeB);

            if (govsplyMtrlList2 != null && govsplyMtrlList2.getRawGovsplyMtrlList() != null
                    && !govsplyMtrlList2.getRawGovsplyMtrlList().isEmpty()) {
                govWriters.add(new GovsplyMtrlSheetWriter(govsplyMtrlList2, "기계", cntrctNm));
            }

            // TODO: 데이터가 없을 때 처리 필요.
            try {
                excelCostWriterService.writeMultiSheetExcel(contractOut, govWriters, excelTemplatePath);
            } catch (GaiaBizException e) {
                log.error("Exception fail", e);
                log.error("Exception error messages : {}", e.getMessage());
                result.put("resultCode", "04");
                result.put("resultMsg", "엑셀 데이터를 입력하는 중 문제가 발생했습니다.");
            }
        }
        // 노무 및 장비투입 (C0004)
        else if("C0004".equals(cbgnDocType)) {
            ResourceMybatisParam.RawLbrEqList rawLbrEqList = resourceService.getLbrEqItem(cntrctNo);

            if (rawLbrEqList != null && rawLbrEqList.getRawLbrEqLists() != null
                    && !rawLbrEqList.getRawLbrEqLists().isEmpty()) {
                List<ExcelSheetWriter> lbrEqWriters = List.of(new LbrEqSheetWriter(rawLbrEqList, "Sheet1", cntrctNm));
                try {
                    excelCostWriterService.writeMultiSheetExcel(contractOut, lbrEqWriters, excelTemplatePath);
                } catch (GaiaBizException e) {
                    log.error("Exception fail", e);
                    log.error("Exception error messages : {}", e.getMessage());
                    result.put("resultCode", "04");
                    result.put("resultMsg", "엑셀 데이터를 입력하는 중 문제가 발생했습니다.");
                }
            }
        }

        // 저장 경로 및 파일명 결합 → 최종 파일 저장 경로 생성
        String docDiskPath = saveStorageMain.getDocDiskPath();
        String docDiskNm = saveStorageMain.getDocDiskNm();
        String fullPath = Path.of(docDiskPath, docDiskNm).toAbsolutePath().toString();

        // 메모리의 엑셀 데이터를 실제 파일로 저장
        try {
            fileService.saveExcelToFile(contractOut, fullPath);
            log.debug("success!!");
            result.put("resultCode", "00");
            result.put("resultMsg", "문서 생성이 완료되었습니다.");
        } catch (GaiaBizException e) {
            log.error("Exception fail", e);
            result.put("resultCode", "05");
            result.put("resultMsg", "엑셀 문서를 저장하는 중 문제가 발생했습니다.");
        }

        return result;
    }

    /**
     *  첨부문서가 있는 경우, PDF 병합 진행
     */
    public Map<String, Object> mergeChagGongGyeDoc(List<MultipartFile> chagGonGyeDoc, List<Map<String, Object>> mergeAttachments, String naviId, String docId) {
        Map<String, Object> result = new HashMap<>();

        // MultipartBodyBuilder를 사용하여 multipart/form-data 형식으로 요청 본문을 생성
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();

        try {
            // 착공계 문서 리소스
            for (MultipartFile doc : chagGonGyeDoc) {
                if (doc != null && !doc.isEmpty()) {
                    multipartBodyBuilder
                            .part("file", new InputStreamResource(doc.getInputStream()))
                            .header("Content-Disposition", String.format("form-data; name=%s; filename=%s", "file", URLEncoder.encode(StringUtils.defaultString(doc.getOriginalFilename(),"chagGonGyeDoc"), StandardCharsets.UTF_8)));
                }
            }

            // 병합할 첨부문서 추가
            for (Map<String, Object> item : mergeAttachments) {
                Resource mergeResource = fileService.getFile((String) item.get("doc_disk_path"), (String) item.get("doc_disk_nm"));

                if (mergeResource == null || !mergeResource.exists() || !mergeResource.isFile()) {
                    log.warn("병합 대상 파일이 존재하지 않음: {}", mergeResource);
                    continue;
                }

                multipartBodyBuilder
                        .part("file", new InputStreamResource(mergeResource.getInputStream()))
                        .header("Content-Disposition", String.format("form-data; name=%s; filename=%s", "file", URLEncoder.encode(StringUtils.defaultString(mergeResource.getFilename(),"mergeAttachments"), StandardCharsets.UTF_8)));
            }

            //윈도우 서버에 보내야 할 요청 파라미터 설정
            multipartBodyBuilder.part("req_cd", naviId+"~"+docId);
            multipartBodyBuilder.part("cllbck_url", apiDomain+"/webApi/chagGongGyeDoc/merge-result");

            // 요청 설정
            iCubeClient.mergeToPdf(multipartBodyBuilder);
        } catch (IOException e) {
            log.error("Exception fail", e);
            result.put("resultCode", "05");
            result.put("resultMsg", "문서 생성 완료 후, pdf 병합에 실패하였습니다.");
        }

        return result;
    }

}

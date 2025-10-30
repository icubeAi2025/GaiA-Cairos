package kr.co.ideait.platform.gaiacairos.comp.defecttracking;

import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kr.co.ideait.platform.gaiacairos.comp.defecttracking.helper.DefectTrackingHelper;
import kr.co.ideait.platform.gaiacairos.comp.defecttracking.service.DefectTrackingService;
import kr.co.ideait.platform.gaiacairos.comp.defecttracking.service.ResponsesService;
import kr.co.ideait.platform.gaiacairos.comp.defecttracking.service.SettingService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractComponent;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DtAttachments;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DtDeficiency;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DtDeficiencyActivity;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DtDeficiencyReply;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.tool.responses.ResponsesForm.ResponsesGet;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.type.FileUploadType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResponsesComponent extends AbstractComponent {

    @Autowired
    DefectTrackingService defectTrackingService;

    @Autowired
    ResponsesService responsesService;

    @Autowired
    SettingService settingService;

    @Autowired
    DefectTrackingHelper defectTrackingHelper;

    @PersistenceContext
    private EntityManager entityManager;


    /**
     * 답변 관리 - 답변 조회
     * @param responsesGet
     * @return
     */
    public Map<String, Object> getResponses(ResponsesGet responsesGet) {
        Map<String, Object> returnMap = new HashMap<String, Object>();
        MybatisInput input = MybatisInput.of()
                .add("cntrctNo", responsesGet.getCntrctNo())
                .add("dfccyPhaseNo", responsesGet.getDfccyPhaseNo());

        DtDeficiencyReply response = responsesService.getResponses(responsesGet.getReplySeq(), responsesGet.getDfccyNo());
        List<DtAttachments> files = Collections.emptyList();
        if (response != null && response.getAtchFileNo() != null) {
            files = defectTrackingHelper.getFileList(response.getAtchFileNo());
        }

        returnMap.put("phase", settingService.selectDeficiencyPhaseList(input));
        returnMap.put("response", response != null ? response : Collections.emptyMap());
        returnMap.put("files", files);

        return returnMap;
    }


    /**
     * 결함 답변 추가
     * @param response
     * @param files
     * @param commonReqVo
     * @return
     */
    @Transactional
    public DtDeficiencyReply saveResponses(DtDeficiencyReply response, List<MultipartFile> files, CommonReqVo commonReqVo) {
        String isApiYn = commonReqVo.getApiYn();
        String pjtDiv = commonReqVo.getPjtDiv();

        // 1. 첨부파일 저장
        if (files != null && !files.isEmpty()) {
            String uploadPath = getUploadPathByWorkType(FileUploadType.DEFICIENCY, response.getCntrctNo());
            Integer fileNo = defectTrackingHelper.convertToDtAttachments(files, uploadPath, null, UserAuth.get(true).getUsrId());
            response.setAtchFileNo(fileNo);
        }

        response.generateReplySeq(entityManager);
        response.setDltYn("N");

        // 2. 답변 확인 체크
        checkRplyYn(response);

        // 3. 답변 저장
        DtDeficiencyReply savedResponse = responsesService.saveResponses(response);

        // 4. API 통신
        if ("P".equals(pjtDiv) && "Y".equals(isApiYn) && "Y".equals(response.getRplyYn())) {
            sendToApi(savedResponse);
        }
        return savedResponse;
    }


    /**
     * 결함 답변 수정
     * @param response
     * @param delFileList
     * @param files
     * @param commonReqVo
     * @return
     */
    @Transactional
    public DtDeficiencyReply updateResponses(DtDeficiencyReply response, List<DtAttachments> delFileList, List<MultipartFile> files, CommonReqVo commonReqVo) {
        String isApiYn = commonReqVo.getApiYn();
        String pjtDiv = commonReqVo.getPjtDiv();

        // 1. 답변 의견 수정
        DtDeficiencyReply findResponse = responsesService.getDeficiencyReply(response.getReplySeq(), response.getDfccyNo());
        if (findResponse == null) {
            throw new GaiaBizException(ErrorType.NOT_FOUND, "수정할 답변을 찾을 수 없습니다.");
        }

        findResponse.setRplyCd(response.getRplyCd());
        findResponse.setRplyCntnts(response.getRplyCntnts());
        findResponse.setRplyYn(response.getRplyYn());

        // 2. 기존 첨부파일 삭제
        if (delFileList != null && !delFileList.isEmpty()) {
            defectTrackingHelper.deleteAttachmentList(delFileList, UserAuth.get(true).getUsrId());
        }

        // 3. 새로운 첨부파일 저장
        Integer existingFileNo = findResponse.getAtchFileNo();
        if (files != null && !files.isEmpty()) {
            String uploadPath = getUploadPathByWorkType(FileUploadType.DEFICIENCY, findResponse.getCntrctNo());
            Integer fileNo = defectTrackingHelper.convertToDtAttachments(files, uploadPath, existingFileNo, UserAuth.get(true).getUsrId());
            findResponse.setAtchFileNo(fileNo);
        }

        // 4. 답변 확인 체크
        checkRplyYn(findResponse);

        // 5. 답변 저장
        DtDeficiencyReply savedResponse = responsesService.saveResponses(findResponse);

        // 6. pgaia & apiYn & 확인 체크 시 API 통신
        if ("P".equals(pjtDiv) && "Y".equals(isApiYn) && "Y".equals(response.getRplyYn())) {
            sendToApi(savedResponse);
        }

        return savedResponse;
    }


    /**
     * 답변 관리 - 답변 삭제
     * @param responses
     * @return
     */
    @Transactional
    public Boolean deleteResponses(List<DtDeficiencyReply> responses) {
        AtomicBoolean message = new AtomicBoolean(false);

        responses.forEach(id -> {
            DtDeficiencyReply dtDeficiencyReply = responsesService.getDeficiencyReply(id.getReplySeq(), id.getDfccyNo());
            if (dtDeficiencyReply != null && dtDeficiencyReply.getRplyYn().equals("N")) {
                if(dtDeficiencyReply.getAtchFileNo() != null) {
                    MybatisInput input = MybatisInput.of()
                                .add("fileNo", dtDeficiencyReply.getAtchFileNo())
                                .add("usrId", UserAuth.get(true).getUsrId());
                    defectTrackingHelper.deleteAttachmentList(input);
                }
                responsesService.deleteDeficiencyReply(dtDeficiencyReply);
            } else if (dtDeficiencyReply != null && dtDeficiencyReply.getRplyYn().equals("Y")) {
                message.set(true);
            }
        });
        return message.get();
    }


    /**
     * 답변 확인
     * @param responsesList
     * @param usrId
     * @param commonReqVo
     * @return
     */
    @Transactional
    public Boolean confirmResponses(List<DtDeficiencyReply> responsesList, String usrId, CommonReqVo commonReqVo) {
        String isApiYn = commonReqVo.getApiYn();
        String pjtDiv = commonReqVo.getPjtDiv();

        log.info("[confirmResponses] 시작 - responses 개수: {}, usrId: {}, isApiYn: {}", responsesList.size(), usrId, commonReqVo.getApiYn());

        AtomicBoolean message = new AtomicBoolean(false);
        Set<String> changedDfccyNos = new HashSet<>();

        // 1. 항상: 답변 상태 변경
        log.info("[confirmResponses] 1단계: 답변 상태 변경 시작");
        responsesList.forEach(id -> {
            log.info("[confirmResponses] 처리 중인 답변 - CntrctNo: {}, DfccyNo: {}, RplyYn: {}, atchFileNo: {}", id.getCntrctNo(), id.getDfccyNo(), id.getRplyYn(), id.getAtchFileNo());

            DtDeficiencyReply dtDeficiencyReply = responsesService.getDeficiencyReply(id.getReplySeq(), id.getDfccyNo());

            if (dtDeficiencyReply != null) {
                log.info("[confirmResponses] DB에서 조회된 답변 - 현재 RplyYn: {}", dtDeficiencyReply.getRplyYn());

                if (dtDeficiencyReply.getRplyYn().equals("N")) {
                    log.info("[confirmResponses] 답변 상태 변경: N → Y (DfccyNo: {})", id.getDfccyNo());
                    dtDeficiencyReply.setRplyYn("Y");
                    dtDeficiencyReply.setRplyRgstrId(usrId);
                    dtDeficiencyReply.setRplyRgstrDt(LocalDateTime.now());
                    responsesService.saveResponses(dtDeficiencyReply);
                    defectTrackingService.updateRplyYn(id.getDfccyNo());
                    changedDfccyNos.add(id.getDfccyNo());
                    log.info("[confirmResponses] 답변 상태 변경 완료 - DfccyNo: {}", id.getDfccyNo());
                } else if (dtDeficiencyReply.getRplyYn().equals("Y")) {
                    log.info("[confirmResponses] 이미 확인된 답변 - DfccyNo: {}", id.getDfccyNo());
                    message.set(true);
                }
            } else {
                log.warn("[confirmResponses] 답변을 찾을 수 없음 - ReplySeq: {}, DfccyNo: {}", id.getReplySeq(), id.getDfccyNo());
            }
        });

        log.info("[confirmResponses] 1단계 완료 - 변경된 DfccyNo 개수: {}, 변경된 DfccyNo 목록: {}", changedDfccyNos.size(), changedDfccyNos);

        // 2. 조건부: API 연동 (상태가 실제로 변경된 dfccyNo && isPGAIA && isApiYn=="Y" 인 경우만)
        log.info("[confirmResponses] 2단계: API 연동 조건 체크 시작");

        log.info("[confirmResponses] pjtDiv 여부: {}", pjtDiv);

        log.info("[confirmResponses] API 연동 조건 - changedDfccyNos.isEmpty(): {}, pjtDiv: {}, isApiYn: '{}'", changedDfccyNos.isEmpty(), pjtDiv, isApiYn);

        if ("P".equals(pjtDiv) && "Y".equals(isApiYn) && !changedDfccyNos.isEmpty()) {
            log.info("[confirmResponses] API 연동 조건 만족 - API 연동 시작");

            for (String dfccyNo : changedDfccyNos) {
                log.info("[confirmResponses] API 연동 처리 중 - DfccyNo: {}", dfccyNo);

                // 결함 정보
                DtDeficiency deficiency = defectTrackingService.getDeficiency(dfccyNo);
                if (deficiency != null) {
                    log.info("[confirmResponses] 결함 정보 조회 성공 - CntrctNo: {}, Title: {}", deficiency.getCntrctNo(), deficiency.getTitle());

                    try {
                        // 필요한 정보만 params에 담기
                        Map<String, Object> params = new HashMap<>();

                        // 결함 첨부파일 정보
                        List<DtAttachments> deficiencyFiles = deficiency.getAtchFileNo() != null ? defectTrackingHelper.getFileList(deficiency.getAtchFileNo()) : Collections.emptyList();
                        log.info("[confirmResponses] 결함 첨부파일 개수: {}", deficiencyFiles.size());

                        params.put("deficiency", deficiency);
                        params.put("deficiencyFileInfo", defectTrackingHelper.convertToFileInfo(deficiencyFiles));

                        // 답변 리스트 조회
                        List<DtDeficiencyReply> replyList = responsesService.getDeficiencyReplyList(dfccyNo, deficiency.getCntrctNo());
                        log.info("[confirmResponses] 답변 리스트 개수: {}", replyList.size());
                        params.put("reply", replyList);

                        // 답변 첨부파일 정보
                        List<Map<String, Object>> replyFileInfo = new ArrayList<>();
                        for (DtDeficiencyReply reply : replyList) {
                            if (reply.getAtchFileNo() != null) {
                                List<DtAttachments> replyFiles = defectTrackingHelper.getFileList(reply.getAtchFileNo());
                                replyFileInfo.addAll(defectTrackingHelper.convertToFileInfo(replyFiles));
                                log.info("[confirmResponses] 답변 첨부파일 개수 (ReplySeq: {}): {}", reply.getReplySeq(), replyFiles.size());
                            }
                        }
                        params.put("replyFileInfo", replyFileInfo);

                        log.info("[confirmResponses] API 호출 시작 - GACA7200");
                        Map<String, Object> __response = invokeCairos2Pgaia("GACA7200", params);
                        String resultCode = org.apache.commons.collections4.MapUtils.getString(__response, "resultCode");

                        if (__response == null || resultCode == null || !"00".equals(resultCode)) {
                            String resultMsg = org.apache.commons.collections4.MapUtils.getString(__response, "resultMsg", "API 오류");
                            log.error("[confirmResponses] API 호출 실패 - Code: {}, Message: {}", resultCode, resultMsg);
                            throw new GaiaBizException(ErrorType.INTERFACE, resultMsg);
                        }

                        log.info("[confirmResponses] API 연동 성공 - DfccyNo: {}, ResultCode: {}", dfccyNo, resultCode);

                    } catch (GaiaBizException e) {
                        log.error("[confirmResponses] API 호출 중 오류 발생 - DfccyNo: {}", dfccyNo, e);
                        throw new GaiaBizException(ErrorType.INTERFACE, "API 호출 중 오류가 발생했습니다: " + e.getMessage());
                    }
                } else {
                    log.warn("[confirmResponses] 결함 정보를 찾을 수 없음 - DfccyNo: {}", dfccyNo);
                }
            }

            log.info("[confirmResponses] 2단계 완료 - 모든 API 연동 처리 완료");
        } else {
            log.info("[confirmResponses] 2단계 건너뜀 - API 연동 조건 불만족");
        }

        log.info("[confirmResponses] 완료 - message: {}", message.get());
        return message.get();
    }


    /**
     * 답변 확인 체크
     * @param response
     */
    private void checkRplyYn(DtDeficiencyReply response) {
        if (response != null && "Y".equals(response.getRplyYn())) {
            defectTrackingService.updateRplyYn(response.getDfccyNo());
            response.setRplyRgstrId(UserAuth.get(true).getUsrId());
            response.setRplyRgstrDt(LocalDateTime.now());
        }
    }


    /**
     * api통신
     * @param savedResponse
     */
    private void sendToApi(DtDeficiencyReply savedResponse) {
        try {
            Map<String, Object> params = new HashMap<>();

            // 1. 결함 정보
            DtDeficiency dtDeficiency = defectTrackingService.getDeficiency(savedResponse.getCntrctNo(), savedResponse.getDfccyNo());
            params.put("deficiency", dtDeficiency);

            // 2. 결함 첨부파일 정보
            List<Map<String, Object>> deficiencyFileInfo = Collections.emptyList();

            if (dtDeficiency != null && dtDeficiency.getAtchFileNo() != null) {
                List<DtAttachments> dfccyFiles = defectTrackingHelper.getFileList(dtDeficiency.getAtchFileNo());
                log.info("##### Found {} deficiency attachment files", dfccyFiles.size());
                deficiencyFileInfo = defectTrackingHelper.convertToFileInfo(dfccyFiles);
            } else {
                log.info("##### No deficiency attachment fileNo found");
            }
            params.put("deficiencyFileInfo", deficiencyFileInfo);

            // 3. 결함 액티비티
            List<DtDeficiencyActivity> activity = defectTrackingService.getDeficiencyActivity(savedResponse.getDfccyNo());
            params.put("activity", activity);

            // 4. 답변 (새로 저장된 답변 포함) - 단건, dfccyNo 유니크
            List<DtDeficiencyReply> replyList = responsesService.getDeficiencyReplyList(savedResponse.getDfccyNo(), savedResponse.getCntrctNo());
            params.put("reply", replyList);

            // 5. 답변 첨부파일 정보
            List<Map<String, Object>> replyFileInfo = Collections.emptyList();
            if (savedResponse.getAtchFileNo() != null) {
                List<DtAttachments> replyAtchFiles = defectTrackingHelper.getFileList(savedResponse.getAtchFileNo());
                log.info("##### Found {} reply attachment files", replyAtchFiles.size());
                replyFileInfo = defectTrackingHelper.convertToFileInfo(replyAtchFiles);
            } else {
                log.info("##### No reply attachment fileNo found");
            }
            params.put("replyFileInfo", replyFileInfo);

            // 6. pgaia & apiYn & 확인 체크 시 API 통신
            log.info("##### Deficiency file count: {}, Reply file count: {}", deficiencyFileInfo.size(), replyFileInfo.size());

            Map<String, Object> __response = invokeCairos2Pgaia("GACA7200", params);
            String resultCode = MapUtils.getString(__response, "resultCode");

            if (__response == null) {
                log.error("API response is null");
                throw new GaiaBizException(ErrorType.INTERFACE, "API 응답이 없습니다.");
            }

            if (resultCode == null) {
                log.error("API response code is null");
                throw new GaiaBizException(ErrorType.INTERFACE, "API 응답 코드가 없습니다.");
            }

            if (!"00".equals(resultCode)) {
                String resultMsg = MapUtils.getString(__response, "resultMsg", "알 수 없는 오류가 발생했습니다.");
                log.error("API call failed - Code: {}, Message: {}", resultCode, resultMsg);
                throw new GaiaBizException(ErrorType.INTERFACE, resultMsg);
            }

            log.info("API integration completed successfully for reply: {}", savedResponse.getReplySeq());
        } catch (GaiaBizException e) {
            log.error("[결함 추적 - 답변관리] API 호출 중 오류 발생: ", e);
        }

    }


    /**
     * 답변관리 API receive
     * @param msgId
     * @param params
     * @return
     */
    @Transactional
    public Map<String, Object> receiveApiOfResponse(String msgId, Map<String, Object> params) {

        Map<String, Object> result = new HashMap<>();
        result.put("resultCode", "00");

        try {
            log.info("결함추적 > 답변관리 > receive msgId: {}, 연동 params : {}", msgId, params);

            // 답변 추가
            if ("GACA7200".equals(msgId)) {

                // 결함 정보 먼저 처리
                DtDeficiency deficiency = objectMapper.convertValue(params.get("deficiency"), DtDeficiency.class);

                if (deficiency != null) {
                    // 기존 결함이 있는지 확인
                    DtDeficiency existingDeficiency = defectTrackingService.getDeficiency(deficiency.getCntrctNo(), deficiency.getDfccyNo());

                    if (existingDeficiency == null) {
                        // 결함이 없으면 새로 저장
                        deficiency = defectTrackingService.saveDeficiency(deficiency);
                    } else {
                        deficiency = existingDeficiency;
                    }
                } else {
                    result.put("resultCode", "01");
                    result.put("resultMsg", "No deficiency data received");
                    return result;
                }

                // 답변 (List로 받아서 처리) , 실적적으로 1개
                List<DtDeficiencyReply> replyList = objectMapper.convertValue(params.get("reply"), new TypeReference<List<DtDeficiencyReply>>() {});

                DtDeficiencyReply reply = null;
                if (replyList != null && !replyList.isEmpty()) {
                    reply = responsesService.saveResponses(replyList.get(0));
                } else {
                    result.put("resultCode", "01");
                    result.put("resultMsg", "No reply data received");
                    return result;
                }

                // Activity 저장
                List<DtDeficiencyActivity> activity = objectMapper.convertValue(params.get("activity"), new TypeReference<List<DtDeficiencyActivity>>() {});
                if(activity != null && !activity.isEmpty()) {
                    defectTrackingService.createActicity(activity, deficiency.getDfccyNo(), deficiency.getCntrctNo(), deficiency.getRgstrId());
                }

                // 파일 정보에서 첨부파일들 가져오기
                List<Map<String, Object>> deficiencyFileInfo = (List<Map<String, Object>>) params.get("deficiencyFileInfo");
                if (deficiencyFileInfo != null && !deficiencyFileInfo.isEmpty()) {
                    insertFileInfoToApi(deficiencyFileInfo, deficiency.getAtchFileNo(), deficiency.getRgstrId(), "deficiency", deficiency.getDfccyNo(), deficiency.getCntrctNo());
                } else {
                    log.info("##### No deficiency attachment file info received");
                }

                // 답변 첨부파일 정보 처리
                List<Map<String, Object>> replyFileInfo = (List<Map<String, Object>>) params.get("replyFileInfo");
                if (replyFileInfo != null && !replyFileInfo.isEmpty()) {
                    insertFileInfoToApi(replyFileInfo, reply.getAtchFileNo(), reply.getRgstrId(), "reply", reply.getReplySeq(), reply.getCntrctNo());
                } else {
                    log.info("##### No reply attachment file info received");
                }
            }

        } catch (GaiaBizException e) {
            log.error("[결함 추적 - 답변관리] API receive 오류 발생: ", e);
            result.put("resultCode", "01");
            result.put("resultMsg", e.getMessage());
        }

        return result;
    }


    /**
     * 공통 파일 처리 메서드
     * @param files 처리할 파일 목록
     * @param fileNo 연결할 파일 번호
     * @param rgstrId 등록자 ID
     * @param fileType 파일 타입 ("deficiency" 또는 "reply")
     * @param targetId 대상 ID (DfccyNo 또는 ReplySeq)
     */
    @Transactional
    public void insertFileInfoToApi(List<Map<String, Object>> files, Integer fileNo, String rgstrId, String fileType, Object targetId, String cntrctNo) {
        List<DtAttachments> dtAttachmentsList = new ArrayList<>();

        if (files != null && !files.isEmpty()) {

            // 파일 저장 경로 설정
            String fullPath = Path.of(uploadPath, getUploadPathByWorkType(FileUploadType.DEFICIENCY, cntrctNo)).toString().replace("\\", "/");
            log.info("##### {} file storage path configured - FullPath: {}", fileType, fullPath);

            for(Map<String, Object> fileInfo : files) {
                String fileName = (String) fileInfo.get("fileNm"); // 실제 파일명

                // 파일 이름이 비어있거나 null인 경우 건너뛰기
                if (fileName == null || fileName.trim().isEmpty()) {
                    log.warn("##### Skipping {} file with empty name", fileType);
                    continue;
                }

                try {
                    // Base64로 인코딩된 파일 내용을 디코딩
                    String base64Content = (String) fileInfo.get("fileContent");
                    if (base64Content == null || base64Content.isEmpty()) {
                        log.warn("##### No file content found for {} file: {}", fileType, fileName);
                        continue;
                    }

                    byte[] fileContent = Base64.getDecoder().decode(base64Content);

                    // 파일을 디스크에 저장
                    String savedFileName = (String) fileInfo.get("fileDiskNm"); // 서버 저장 파일명(uuid)
                    Path savedFilePath = Paths.get(fullPath, savedFileName); // uploadpath + 업무 + 년월 + 서버 저장 파일명
                    Path parent = savedFilePath.getParent();

                    log.info("##### Saving {} file to: {}", fileType, savedFilePath);

                    if(parent != null) {
                        // 디렉토리가 없으면 생성
                        Files.createDirectories(savedFilePath.getParent());

                        // 파일 저장ß
                        Files.write(savedFilePath, fileContent);
                    }

                    DtAttachments dtAttachments = new DtAttachments();
                    dtAttachments.setFileNo(fileNo);
                    dtAttachments.setFileNm(fileName);
                    dtAttachments.setFileDiskNm(savedFileName);
                    dtAttachments.setFileDiskPath(fullPath);
                    dtAttachments.setFileSize(((Number) fileInfo.get("fileSize")).intValue());
                    dtAttachments.setDltYn("N");

                    // fileHitNum 안전한 타입 변환
                    Object fileHitNumObj = fileInfo.get("fileHitNum");
                    if (fileHitNumObj != null) {
                        if (fileHitNumObj instanceof Integer) {
                            Integer intValue = (Integer) fileHitNumObj;
                            // PostgreSQL int2 범위 체크 (-32,768 ~ 32,767)
                            if (intValue >= Short.MIN_VALUE && intValue <= Short.MAX_VALUE) {
                                dtAttachments.setFileHitNum(intValue.shortValue());
                            } else {
                                dtAttachments.setFileHitNum((short) 0);
                            }
                        } else if (fileHitNumObj instanceof Short) {
                            dtAttachments.setFileHitNum((Short) fileHitNumObj);
                        } else if (fileHitNumObj instanceof Number) {
                            Number numValue = (Number) fileHitNumObj;
                            int intValue = numValue.intValue();
                            if (intValue >= Short.MIN_VALUE && intValue <= Short.MAX_VALUE) {
                                dtAttachments.setFileHitNum((short) intValue);
                            } else {
                                dtAttachments.setFileHitNum((short) 0);
                            }
                        } else {
                            dtAttachments.setFileHitNum((short) 0);
                        }
                    } else {
                        dtAttachments.setFileHitNum((short) 0);
                    }

                    dtAttachments.setRgstrId(rgstrId);
                    dtAttachments.setChgId(rgstrId);

                    dtAttachmentsList.add(dtAttachments);
                    log.info("##### {} attachment object created for file: {} - FileNo: {}, FileSize: {}", fileType, fileName, fileNo, dtAttachments.getFileSize());
                } catch (GaiaBizException | IOException e) {
                    log.error("##### Error processing {} file {}: {}", fileType, fileName, e.getMessage(), e);
                }
            }

            if (!dtAttachmentsList.isEmpty()) {
                try {
                    defectTrackingHelper.insertAttachmentsList(dtAttachmentsList, fileNo);
                } catch (GaiaBizException e) {
                    log.error("##### Error saving {} attachments to database: {}", fileType, e.getMessage(), e);
                }
            } else {
                log.warn("##### No valid {} attachments to save", fileType);
            }
        } else {
            log.info("##### No {} files to process for {} - TargetId: {}", fileType, fileType, targetId);
        }
    }
}

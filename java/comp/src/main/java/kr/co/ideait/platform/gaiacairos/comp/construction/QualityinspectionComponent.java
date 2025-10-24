package kr.co.ideait.platform.gaiacairos.comp.construction;

import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.ideait.platform.gaiacairos.comp.construction.service.QualityinspectionService;
import kr.co.ideait.platform.gaiacairos.comp.document.service.DocumentService;
import kr.co.ideait.platform.gaiacairos.comp.eapproval.DraftComponent;
import kr.co.ideait.platform.gaiacairos.comp.eapproval.helper.EapprovalHelper;
import kr.co.ideait.platform.gaiacairos.comp.eapproval.service.ApprovalRequestService;
import kr.co.ideait.platform.gaiacairos.comp.mail.service.MailService;
import kr.co.ideait.platform.gaiacairos.comp.project.service.ContractstatusService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractComponent;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.ApDoc;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwAttachments;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwCntqltyCheckList;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwQualityActivity;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwQualityCheckList;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwQualityInspection;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwQualityPhoto;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DcStorageMain;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction.QualityinspectionMybatisParam.ActivityOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction.QualityinspectionMybatisParam.CheckListOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction.QualityinspectionMybatisParam.QualityOutPut;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction.qualityinspection.QualityinspectionForm;
import kr.co.ideait.platform.gaiacairos.core.type.FileUploadType;
import kr.co.ideait.platform.gaiacairos.core.util.FileService;
import kr.co.ideait.platform.gaiacairos.core.util.FileService.FileMeta;
import kr.co.ideait.platform.gaiacairos.core.util.restclient.DocumentServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class QualityinspectionComponent extends AbstractComponent {

    @Autowired
    QualityinspectionService qualityService;

    @Autowired
    ApprovalRequestService approvalRequestService;

    @Autowired
    QualityinspectionForm qualityinspectionForm;

    @Autowired
    ContractstatusService contractService;

    @Autowired
    FileService fileService;

    @Autowired
    MailService mailService;

    @Autowired
    DocumentServiceClient documentServiceClient;

    @Autowired
    DocumentService documentService;

    @Autowired
    DraftComponent draftComponent;

    // 첨부파일 공통 생성
    private CwAttachments createAttachment(MultipartFile file, FileMeta meta, String div) {
        CwAttachments attach = new CwAttachments();
        attach.setFileNm(file.getOriginalFilename());
        attach.setFileDiskNm(meta.getFileName());
        attach.setFileDiskPath(meta.getDirPath());
        attach.setFileSize(meta.getSize());
        attach.setFileDiv(div);
        attach.setFileHitNum(0);
        attach.setDltYn("N");
        return attach;
    }

    /**
     * 품질 검측 조회
     * - 검측, 액티비티, 체크리스트, 사진, 일반 첨부파일 분리 조회
     */
    public Map<String, Object> getQuality(String cntrctNo, String qltyIspId) {
        QualityOutPut quality = qualityService.getQualityByQuery(cntrctNo, qltyIspId);
        List<ActivityOutput> activitys = qualityService.getQualityActivityList(cntrctNo, qltyIspId);
        List<CheckListOutput> checkList = qualityService.getQualityCheckList(cntrctNo, qltyIspId);
        List<CwQualityPhoto> photoList = qualityService.getPhotoList(cntrctNo, qltyIspId);
        List<CwAttachments> attachments = qualityService.getFileList(quality.getAtchFileNo());
        List<CwAttachments> photoAttachments = qualityService.getImgFileList(quality.getAtchFileNo());

        Map<String, Object> result = new HashMap<>();
        result.put("quality", quality);
        result.put("activitys", activitys);
        result.put("checkList", checkList);
        result.put("attachments", attachments);
        result.put("photoList", photoList);
        result.put("photoAttachments", photoAttachments);

        return result;
    }

    /**
     * 품질 검측 생성
     * - 검측 정보, 첨부파일, 사진, activity, checklist 등록 처리
     */
    @Transactional
    public String createQuality(CommonReqVo commonReqVo, QualityinspectionForm.CreateQuality quality, String userId,
            List<MultipartFile> files, List<MultipartFile> photos) {

        // 1. 검측 기본 정보 세팅
        CwQualityInspection inspection = qualityinspectionForm.toEntity(quality);
        inspection.setCnstrtnId(userId);
        inspection.setDltYn("N");

        List<CwAttachments> fileList = new ArrayList<>();
        List<CwAttachments> photoList = new ArrayList<>();
        int fileNo = 0;

        // 2. 첨부파일 저장
        if (files != null && !files.isEmpty()) {
            String uploadPath = getUploadPathByWorkType(FileUploadType.QualityInspection, inspection.getCntrctNo());

            for (MultipartFile file : files) {
                FileMeta fileMeta = fileService.save(uploadPath, file);
                CwAttachments attach = createAttachment(file, fileMeta, "F");
                fileList.add(attach);
            }
            fileNo = qualityService.createCwAttachmentsList(fileList);
            inspection.setAtchFileNo(fileNo);
        }

        // 3. 검측 등록
        String qltyIspId = qualityService.createQuality(inspection).getQltyIspId();

        // 4. 사진 데이터 등록
        if (photos != null && !photos.isEmpty()) { // 사진 첨부파일 등록
            String uploadPath = getUploadPathByWorkType(FileUploadType.QualityInspection, inspection.getCntrctNo());

            for (MultipartFile file : photos) {
                FileMeta fileMeta = fileService.save(uploadPath, file);
                CwAttachments attach = createAttachment(file, fileMeta, "I");

                if (fileNo != 0) {
                    attach.setFileNo(fileNo);
                }
                photoList.add(attach);
            }

            if (fileNo == 0) {
                fileNo = qualityService.createCwAttachmentsList(photoList);
                inspection.setAtchFileNo(fileNo);
                qualityService.updateQualityFileNo(inspection.getQltyIspId(), fileNo, commonReqVo.getUserId());
            } else {
                qualityService.createCwAttachmentsList(photoList);
            }

            for (int i = 0; i < quality.getPhotos().size(); i++) { // 사진 데이터 등록
                CwQualityPhoto photo = qualityinspectionForm.toEntity(quality.getPhotos().get(i));
                CwAttachments attach = photoList.get(i);

                photo.setQltyIspId(qltyIspId);
                photo.setCntrctNo(quality.getCntrctNo());
                photo.setPhtSno(attach.getSno());
                photo.setAtchFileNo(fileNo);
                photo.setDltYn("N");
                qualityService.createPhoto(photo);
            }
        }

        // 5. Activity 저장
        if (quality.getActivity() != null) {
            for (var a : quality.getActivity()) {
                CwQualityActivity act = qualityinspectionForm.toEntity(a);
                if (act.getActivityId() == null || act.getActivityId().isBlank()) {
                    continue;
                }
                act.setQltyIspId(qltyIspId);
                act.setDltYn("N");
                qualityService.createActicity(act);
            }
        }

        // 6. CheckList 저장
        for (var c : quality.getChecklist()) {
            CwQualityCheckList check = qualityinspectionForm.toEntity(c);
            check.setQltyIspId(qltyIspId);
            check.setCntrctNo(quality.getCntrctNo());
            check.setDltYn("N");
            qualityService.createCheck(check);
        }

        return qltyIspId;
    }

    /**
     * 품질 검측 수정
     * - 기존 데이터 조회 후 activity, checklist, 파일, 사진 전부 수정 처리
     * - 최종적으로 수정된 품질 검측 데이터를 다시 저장하고 리턴
     */
    @Transactional
    public CwQualityInspection updateQuality(CommonReqVo commonReqVo, QualityinspectionForm.UpdateQuality update,
            List<MultipartFile> newFiles,
            List<Integer> removedFileNos,
            List<Integer> removedSnos,
            List<MultipartFile> photos) {
        
        CwQualityInspection oldQuality = qualityService.getQuality(update.getCntrctNo(), update.getQltyIspId());
        List<CwQualityActivity> oldActivities = qualityService.getOldActivity(update.getQltyIspId());
        List<CwQualityCheckList> oldCheckLists = qualityService.getOldCheckList(update.getCntrctNo(),
                update.getQltyIspId());

        // 공통 fileNo(첨부/사진 모두 동일하게 사용)
        Integer fileNo = oldQuality.getAtchFileNo();
        if (fileNo == null || fileNo == 0) {
            fileNo = qualityService.generateFileNo();
            oldQuality.setAtchFileNo(fileNo);
        }

        // 1. 기본 정보 및 Activity/Checklist 삭제 → 재생성
        qualityinspectionForm.updateQuality(update, oldQuality);
        qualityService.deleteActivity(oldActivities);

        if (update.getActivity() != null) {
            for (var a : update.getActivity()) {
                CwQualityActivity activity = qualityinspectionForm.toEntity(a);
                if (activity.getActivityId() == null || activity.getActivityId().isBlank()) {
                    continue;
                }
                activity.setQltyIspId(update.getQltyIspId());
                activity.setDltYn("N");
                qualityService.createActicity(activity);
            }
        }

        qualityService.deleteCheck(oldCheckLists);
        for (var c : update.getChecklist()) {
            CwQualityCheckList check = qualityinspectionForm.toEntity(c);
            check.setCntrctNo(update.getCntrctNo());
            check.setQltyIspId(update.getQltyIspId());
            check.setDltYn("N");
            qualityService.createCheck(check);
        }

        // 2. 첨부파일 삭제
        if (removedFileNos != null && removedSnos != null) {
            for (int i = 0; i < removedSnos.size(); i++) {
                qualityService.deleteAttachment(removedFileNos.get(i), removedSnos.get(i));
            }
        }

        // 3. 첨부파일 추가
        if (newFiles != null && !newFiles.isEmpty()) {
            List<CwAttachments> attachList = new ArrayList<>();
            String uploadPath = getUploadPathByWorkType(FileUploadType.QualityInspection, update.getCntrctNo());

            for (MultipartFile file : newFiles) {
                FileMeta meta = fileService.save(uploadPath, file);
                CwAttachments attach = createAttachment(file, meta, "F");
                attach.setFileNo(fileNo);
                attachList.add(attach);
            }
            qualityService.createCwAttachmentsList(attachList);
        }

        // 4. 사진 처리 (추가/삭제)
        List<CwQualityPhoto> oldPhotos = qualityService.getPhotoList(update.getCntrctNo(), update.getQltyIspId());

        List<CwAttachments> newPhotoAttach = new ArrayList<>();
        if (photos != null && !photos.isEmpty()) {
            for (MultipartFile file : photos) {
                String uploadPath = getUploadPathByWorkType(FileUploadType.QualityInspection, update.getCntrctNo());
                FileMeta meta = fileService.save(uploadPath, file);
                CwAttachments attach = createAttachment(file, meta, "I");
                attach.setFileNo(fileNo);
                newPhotoAttach.add(attach);
            }
            qualityService.createCwAttachmentsList(newPhotoAttach);
        }

        List<CwAttachments> savedPhotoAttach = qualityService.getImgFileList(fileNo);

        // 기존 사진 sno 목록
        Set<Integer> oldSnos = oldPhotos.stream()
                .map(CwQualityPhoto::getPhtSno)
                .collect(Collectors.toSet());

        // 신규 사진만 필터링 (form 기준)
        List<QualityinspectionForm.Photo> newFormPhotos = update.getPhotos().stream()
                .filter(p -> p.getPhtSno() == 0)
                .collect(Collectors.toList());

        // 신규 사진만 필터링 (attachments 기준)
        List<CwAttachments> newSavedPhotoAttach = savedPhotoAttach.stream()
                .filter(a -> !oldSnos.contains(a.getSno()))
                .collect(Collectors.toList());

        // 새로운 사진만 추가
        int loopCnt = Math.min(newFormPhotos.size(), newSavedPhotoAttach.size());
        for (int i = 0; i < loopCnt; i++) {
            CwAttachments savedAttach = newSavedPhotoAttach.get(i);
            QualityinspectionForm.Photo formPhoto = newFormPhotos.get(i);

            CwQualityPhoto newPhoto = qualityinspectionForm.toEntity(formPhoto);
            newPhoto.setCntrctNo(update.getCntrctNo());
            newPhoto.setQltyIspId(update.getQltyIspId());
            newPhoto.setAtchFileNo(fileNo);
            newPhoto.setPhtSno(savedAttach.getSno()); // 신규 첨부파일 sno 그대로
            newPhoto.setDltYn("N");
            newPhoto.setRgstrId(commonReqVo.getUserId());
            newPhoto.setRgstDt(LocalDateTime.now());

            qualityService.createPhoto(newPhoto);
        }

        // 사진 삭제
        for (Integer sno : update.getDeletePhtSno()) {
            CwQualityPhoto delPhoto = qualityService.getPhoto(update.getCntrctNo(), update.getQltyIspId(), sno);
            qualityService.deletePhoto(delPhoto);
        }

        // 사진 파일 삭제
        for (Integer sno : update.getDeleteSno()) {
            qualityService.deleteAttachment(fileNo, sno);
        }

        return qualityService.createQuality(oldQuality);
    }

    /**
     * 검측요청
     */
    @Transactional
    public void inspectionRequestList(CommonReqVo commonReqVo, List<Map<String, Object>> paramList, HttpServletRequest request) {
        for (Map<String, Object> param : paramList) {
            String cntrctNo = param.get("cntrctNo").toString();
            String qltyIspId = param.get("qltyIspId").toString();

            CwQualityInspection quality = qualityService.getQuality(cntrctNo, qltyIspId);

            // 이미 검측자가 지정되어 있으면 skip
            if (quality.getCqcId() != null && !quality.getCqcId().isBlank()) {
                log.info("이미 검측요청 보낸 항목은 건너뜀: {}", qltyIspId);
                continue;
            }

            inspectionRequest(commonReqVo, param, request);
        }
    }

    public void inspectionRequest(CommonReqVo commonReqVo, Map<String, Object> param, HttpServletRequest request) {

        // 1. 감리에게 보낼 메일 내용 구성
        String recipient = URLDecoder.decode(param.get("emailAdrs").toString()); // 감리 이메일

        String cntrctNo = param.get("cntrctNo").toString();
        String qltyIspId = param.get("qltyIspId").toString();
        String cntrctNm = contractService.getByCntrctNo(cntrctNo).getCntrctNm(); // 계약명

        CwQualityInspection quality = qualityService.getQuality(cntrctNo, qltyIspId);

        String recipientName = URLDecoder.decode(commonReqVo.getUserName()); // 검측 요청자
        String ispLct = quality.getIspLct(); // 검측 위치
        String ispPart = quality.getIspPart(); // 검측 부위
        String ispIssue = quality.getIspIssue(); // 검측사항

        String title = String.format("[%s] \"%s\" 검측 요청", cntrctNm, ispPart);

        // 메인 도메인 설정
        String protocol = request.isSecure() ? "https" : "http";
        String serverName = request.getServerName();
        int port = request.getLocalPort();

        String baseUrl = String.format("%s://%s:%d", protocol, serverName, port);

        String linkUrl = String.format(
                "%s/construction/qualityinspection/addResult?type=d&mode=create&pjtNo=%s&cntrctNo=%s&qltyIspId=%s&returnType=A",
                baseUrl, commonReqVo.getPjtNo(), cntrctNo, qltyIspId);

        StringBuilder html = new StringBuilder();
        html.append("<div>")
                .append("<p>아래 정보와 같이 검측 요청이 왔습니다.<br>")
                .append("바로가기를 통해 검측 결과를 등록해 주세요.<br>")
                .append("<br>")
                .append("-------------------------------------  아래  -------------------------------------</p>")
                .append("<ul>")
                .append("<li><strong>계약명</strong> : ").append(cntrctNm).append("</li>")
                .append("<li><strong>검측요청자</strong> : ").append(recipientName).append("</li>")
                .append("<li><strong>검측위치</strong> : ").append(ispLct).append("</li>")
                .append("<li><strong>검측부위</strong> : ").append(ispPart).append("</li>")
                .append("<li><strong>검측사항</strong> : ").append(ispIssue).append("</li>")
                .append("</ul>")
                .append("<p><a href='").append(linkUrl).append("'>[바로가기]</a></p>")
                .append("</div>");

        // 2. 요청 디비에 데이터 저장
        Map<String, Object> insertParam = new HashMap<>();

        log.info("param 전체: {}", param);

        insertParam.put("reqInsId", qltyIspId); // 요청 ID
        insertParam.put("pjtNo", commonReqVo.getPjtNo()); // 프로젝트 번호
        insertParam.put("cntrctNo", cntrctNo); // 계약 번호
        insertParam.put("reqAppDiv", "QUA"); // 업무구분: 품질검측
        insertParam.put("toUsrId", param.get("usrId")); // 감리 ID
        insertParam.put("rgstrId", commonReqVo.getUserId()); // 등록자
        insertParam.put("rgstDt", LocalDateTime.now()); // 등록일
        insertParam.put("dltYn", "N"); // 삭제여부 : default 'N'
        insertParam.put("endYn", "N"); // 업무처리구분 : default 'N'

        qualityService.insertRequestItem(insertParam);

        // 3. 품질검측 검측자 항목에 선택된 감리 세팅 후 업데이트
        quality.setCqcId(param.get("usrId").toString()); // 검측자 세팅
        quality.setCnstrtnId(commonReqVo.getUserId()); // 검측요청자 세팅
        qualityService.createQuality(quality);

        // 4. 트랜잭션 커밋 후 비동기 메일 발송
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void afterCommit() {
                CompletableFuture.runAsync(() -> {
                    try {
                        mailService.mailSend(recipient, title, html.toString());
                    } catch (GaiaBizException e) {
                        log.error("메일 발송 실패: {}", recipient, e);
                    }
                });
            }
        });
    }

    /**
     * 결재요청
     */
    @Transactional
    public void paymentRequest(CommonReqVo commonReqVo, List<CwQualityInspection> qualityList, String cmnCdNmKrn, String isApiYn,
            String pjtDiv) {
        qualityList.forEach(id -> {
            CwQualityInspection qualityInspection = qualityService.getQuality(id.getCntrctNo(), id.getQltyIspId());
            String rsltDocNo = qualityInspection.getRsltDocNo();

            // 필요한 리소스 조회
            List<CwQualityActivity> cwQualityActivities = qualityService
                    .getOldActivity(qualityInspection.getQltyIspId());
            List<CwQualityCheckList> cwQualityCheckLists = qualityService
                    .getOldCheckList(qualityInspection.getCntrctNo(), qualityInspection.getQltyIspId());
            List<CwQualityPhoto> cwQualityPhotos = qualityService.getPhotoList(qualityInspection.getCntrctNo(),
                    qualityInspection.getQltyIspId());
            List<CwCntqltyCheckList> cntqltyCheckLists = qualityService.getCntqltyCheckList();

            // 품질검측 첨부파일 (파일 객체 대신 메타데이터만 전송)
            List<Map<String, Object>> qualityFileInfo = Collections.emptyList();
            List<Map<String, Object>> photoFileInfo = Collections.emptyList();

            if (qualityInspection.getAtchFileNo() != null) { // 품질검측 첨부파일 우선 처리
                Integer qualityFileNo = qualityInspection.getAtchFileNo();

                List<CwAttachments> qualityFiles = qualityService.getFileList(qualityFileNo);
                qualityFileInfo = qualityService.convertToFileInfo(qualityFiles);

            } else if (cwQualityPhotos != null && !cwQualityPhotos.isEmpty()) { // 품질 사진 첨부파일 처리
                Integer photoFileNo = cwQualityPhotos.get(0).getAtchFileNo();

                List<CwAttachments> photoFiles = qualityService.getImgFileList(photoFileNo);
                photoFileInfo = qualityService.convertToFileInfo(photoFiles);
            }

            // 품질검측 리소스 데이터
            Map<String, Object> resourceMap = new HashMap<>();
            resourceMap.put("activity", cwQualityActivities);
            resourceMap.put("checkList", cwQualityCheckLists);
            resourceMap.put("cntqltyLists", cntqltyCheckLists);
            resourceMap.put("photo", cwQualityPhotos);
            resourceMap.put("qualityFileInfo", qualityFileInfo);
            resourceMap.put("photoFileInfo", photoFileInfo);

            // 품질검측 승인요청 시 필요 데이터
            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("pjtNo", commonReqVo.getPjtNo());
            requestMap.put("cntrctNo", qualityInspection.getCntrctNo());
            requestMap.put("isApiYn", isApiYn);
            requestMap.put("pjtDiv", pjtDiv);
            requestMap.put("usrId", commonReqVo.getUserId());

            // 결재요청 로직
            approvalRequestService.insertQualityAppDoc(qualityInspection, cmnCdNmKrn, rsltDocNo, resourceMap,
                    requestMap);
        });
    }

    /**
     * 결재취소
     * 1. 전자결재 문서 삭제 with apDocID
     * 2. 통합문서에 저장된 문서 존재 시 문서 삭제 with docId
     */
    @Transactional
    public void cancelPayment(Map<String, Object> param, String usrId, CommonReqVo commonReqVo) {
        try {
            // 기본 데이터 세팅(삭제할 품질검측 id 리스트, 계약번호)
            List<CwQualityInspection> qualityLists = (List<CwQualityInspection>) param.get("qualityList");
            List<String> qidLists = qualityLists.stream()
                    .filter(Objects::nonNull)
                    .map(CwQualityInspection::getQltyIspId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            String cntrctNo = qualityLists.get(0).getCntrctNo();

            Map<String, Object> qidsMap = new HashMap<>();
            qidsMap.put("qidLists", qidLists);
            qidsMap.put("cntrctNo", cntrctNo);

            // 1. 전자결재 문서 조회 조건 세팅
            List<String> apDocIds = qualityService.getApDocIds(qidsMap); // 삭제할 전자결재 문서 아이디 리스트
            log.info("apDocIds = {}", apDocIds);

            List<ApDoc> deleteApDocList = apDocIds.stream()
                    .filter(Objects::nonNull)
                    .map(apDocId -> {
                        ApDoc apDoc = new ApDoc();
                        apDoc.setApDocId(apDocId);
                        apDoc.setApType(EapprovalHelper.QUALITY_APP_DOC); // 품질검측(결재요청) 문서 타입 설정
                        return apDoc;
                    })
                    .collect(Collectors.toList());

            Map<String, Object> reqVoMap = new HashMap<>();
            reqVoMap.put("apiYn", (String) param.get("apiYn"));
            reqVoMap.put("pjtDiv", (String) param.get("pjtDiv"));

            // 전자결재 승인 취소(전자결재 요청자, 전자결재 요청 일자, 전자결재 문서ID, 승인상태 초기화)
            if (!deleteApDocList.isEmpty()) {
                draftComponent.setDeleteList(deleteApDocList, reqVoMap);
            }

            // 2. 통합문서 조회 조건 세팅
            List<String> docIds = qualityService.getDocIds(qidsMap); // 삭제할 통합문서 아이디 리스트

            // 통합문서에 파일 있는지 체크
            List<String> existDocIds = new ArrayList<>();
            for (String docId : docIds) {
                DcStorageMain dcStorage = documentService.getDcStorageMain(docId);
                if (dcStorage != null) {
                    existDocIds.add(docId);
                }
            }

            if (!existDocIds.isEmpty()) {
                Map<String, Object> docParam = new HashMap<>();
                docParam.put("docIds", existDocIds);
                docParam.put("usrId", usrId);

                // 통합문서 삭제
                Result docResult = documentServiceClient.removeDocument(docParam);
                if (!docResult.isOk()) {
                    throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR, "품질검측 통합문서 초기화 중 에러발생");
                }
            }
        } catch (GaiaBizException e) {
            log.error("품질검측 승인 취소 중 오류 발생 error = {}", e.getMessage(), e);
            throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR, e);
        }
    }

    /**
     * 검측 결과 등록/수정
     */
    public void addResult(QualityinspectionForm.UpdateQuality result, String usrId) {
        CwQualityInspection oldQuality = qualityService.getQuality(result.getCntrctNo(), result.getQltyIspId());
        qualityinspectionForm.updateQuality(result, oldQuality);
        qualityService.createQuality(oldQuality); // 검측결과 등록/수정

        // DB에서 기존 체크리스트 불러옴
        List<CwQualityCheckList> checkLists = qualityService.getOldCheckList(
                result.getCntrctNo(), result.getQltyIspId());

        // result checklist를 Map으로 변환 (ID → 데이터)
        Map<String, QualityinspectionForm.CheckList> resultMap = result.getChecklist()
                .stream()
                .collect(Collectors.toMap(QualityinspectionForm.CheckList::getChklstId, c -> c));

        // DB 리스트와 매칭
        for (CwQualityCheckList check : checkLists) {
            QualityinspectionForm.CheckList updated = resultMap.get(check.getChklstId());
            if (updated != null) {
                // null이 아니라면 값 반영
                if (updated.getCqcYn() != null) {
                    check.setCqcYn(updated.getCqcYn());
                }
                qualityService.createCheck(check);
            }
        }

        Map map = new HashMap();
        map.put("qltyIspId", oldQuality.getQltyIspId());
        map.put("chgId", usrId);

        qualityService.updateReqItem(map);
    }

    /**
     * 조치사항 등록/수정
     */
    public void addAction(QualityinspectionForm.UpdateQuality action) {
        List<CwQualityCheckList> checkLists = qualityService.getOldCheckList(action.getCntrctNo(),
                action.getQltyIspId());

        // Map으로 변환 (chklstId -> CwQualityCheckList)
        Map<String, CwQualityCheckList> checkListMap = checkLists.stream()
                .collect(Collectors.toMap(CwQualityCheckList::getChklstId, Function.identity()));

        // 화면에서 넘어온 checklist 데이터 순회
        for (QualityinspectionForm.CheckList inputChecklist : action.getChecklist()) {
            CwQualityCheckList target = checkListMap.get(inputChecklist.getChklstId());

            if (target != null) { // DB에 해당 ID 존재하면 업데이트
                target.setActnDscrpt(inputChecklist.getActnDscrpt());
                target.setCqcYn(inputChecklist.getCqcYn());

                qualityService.createCheck(target); // 업데이트/저장
            }
        }
    }

    /**
     * 품질 검측 삭제
     */
    @Transactional
    public void deleteQuality(CwQualityInspection quality, String usrId) {
        CwQualityInspection deleteQuality = qualityService.getQuality(quality.getCntrctNo(), quality.getQltyIspId());
        qualityService.deleteQuality(deleteQuality, usrId);

        // 전자결재문서 존재 시 문서 삭제
        if (deleteQuality.getApDocId() != null) {
            approvalRequestService.deleteApDoc(deleteQuality.getApDocId());
        }
        if (deleteQuality.getIspApDocId() != null) {
            approvalRequestService.deleteApDoc(deleteQuality.getIspApDocId());
        }
    }
}

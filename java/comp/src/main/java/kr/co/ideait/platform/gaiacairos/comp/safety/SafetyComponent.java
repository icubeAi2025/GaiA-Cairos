package kr.co.ideait.platform.gaiacairos.comp.safety;

import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.ideait.iframework.BizException;
import kr.co.ideait.platform.gaiacairos.comp.construction.service.QualityinspectionService;
import kr.co.ideait.platform.gaiacairos.comp.eapproval.service.ApprovalRequestService;
import kr.co.ideait.platform.gaiacairos.comp.mail.service.MailService;
import kr.co.ideait.platform.gaiacairos.comp.project.service.ContractstatusService;
import kr.co.ideait.platform.gaiacairos.comp.safety.service.SafetymgmtService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractComponent;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwAttachments;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwSadtag;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwSafetyInspection;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwSafetyInspectionList;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwSafetyInspectionPhoto;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwStandardInspectionList;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.safety.CheckForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.safety.SadtagForm.Sadtag;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.safety.SafetymgmtMybatisParam.SafetyOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.type.FileUploadType;
import kr.co.ideait.platform.gaiacairos.core.util.FileService;
import kr.co.ideait.platform.gaiacairos.core.util.FileService.FileMeta;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SafetyComponent extends AbstractComponent {

    @Autowired
    SafetymgmtService safetyService;

    @Autowired
    FileService fileService;

    @Autowired
    QualityinspectionService qualityService;

    @Autowired
    ApprovalRequestService approvalRequestService;

    @Autowired
    ContractstatusService contractService;

    @Autowired
    MailService mailService;

    @Autowired
    CheckForm checkForm;

    // 안전점검 추가
    @Transactional
    public void createSafety(CheckForm.Safety safety, String usrId) {
        CwSafetyInspection safetyInspection = checkForm.toEntity(safety);

        safetyInspection.setCnstrtnId(usrId);
        safetyInspection.setRsltYn("N");
        safetyInspection.setDltYn("N");

        // 1. 안전점검 저장
        String inspectionNo = safetyService.createSafety(safetyInspection);

        // 2. 안전점검 항목 저장
        List<CwSafetyInspectionList> safetyLists = new ArrayList<>();
        short ispSno = (short) safetyService.findSno(safety.getCntrctNo());

        for (int i = 0; i < safety.getSafetyLists().size(); i++) {
            CheckForm.SafetyList listForm = safety.getSafetyLists().get(i);
            CwSafetyInspectionList safetyList = checkForm.toSafeEntity(listForm);
            safetyList.setCntrctNo(safety.getCntrctNo());
            safetyList.setIspLstNo(listForm.getIspLstNo());
            safetyList.setIspSno((short) (ispSno + i + 1));
            safetyList.setInspectionNo(inspectionNo);
            safetyLists.add(safetyList);
        }
        safetyService.createSafetyList(safetyLists);

        // 3. 계약별 안전점검 리스트 저장 (신규 리스트만)
        Set<String> incomingIspLstIds = safety.getSafetyLists().stream()
                .map(CheckForm.SafetyList::getIspLstId)
                .collect(Collectors.toSet());

        Set<String> existIspLstIds = new HashSet<>(safetyService.getExistIspLstIds(incomingIspLstIds));

        for (CheckForm.SafetyList listForm : safety.getSafetyLists()) {
            String ispLstId = listForm.getIspLstId();
            if (ispLstId != null && !existIspLstIds.contains(ispLstId)) {
                CwStandardInspectionList standardList = checkForm.toStandardEntity(listForm);
                standardList.setCntrctNo(safety.getCntrctNo());
                standardList.setUpCnsttyCd(safetyService.findUpCnsttyCd(safety.getCntrctNo(), listForm.getCnsttyCd()));
                standardList.setIspLstDscrpt(listForm.getIspDscrpt());
                standardList.setCnsttyLvl((short) 4);
                standardList.setCnsttyYn("N");
                standardList.setDltYn("N");

                safetyService.createStandardList(standardList);
            }
        }
    }

    // 안전점검 수정
    @Transactional
    public void updateSafety(CheckForm.Safety safety, CommonReqVo commonReqVo) {
        CwSafetyInspection oldSafety = safetyService.getSafetyWithJpa(safety.getCntrctNo(), safety.getInspectionNo());
        checkForm.updateSafety(safety, oldSafety);
        safetyService.createSafety(oldSafety);

        // 기존의 안전점검 항목
        List<CwSafetyInspectionList> oldList = safetyService.getSafetyListByJpa(safety.getCntrctNo(),
                safety.getInspectionNo());
        Map<String, CwSafetyInspectionList> oldMap = oldList.stream()
                .collect(Collectors.toMap(CwSafetyInspectionList::getIspLstId, item -> item));

        Set<String> newIds = new HashSet<>();

        for (int i = 0; i < safety.getSafetyLists().size(); i++) {
            CheckForm.SafetyList listForm = safety.getSafetyLists().get(i);
            CwSafetyInspectionList newItem = checkForm.toSafeEntity(listForm);
            newItem.setInspectionNo(safety.getInspectionNo());
            newItem.setCntrctNo(safety.getCntrctNo());
            newItem.setIspSno((short) (safetyService.findSno(safety.getCntrctNo()) + i + 1));
            newItem.setDltYn("N");

            String ispLstId = newItem.getIspLstId();
            newIds.add(ispLstId);

            // 기존에 없던 화면에서 받은 새로운 점점항목일 시
            if (!oldMap.containsKey(ispLstId)) {
                newItem.setRgstrId(commonReqVo.getUserId());
                newItem.setRgstDt(LocalDateTime.now());
                oldList.add(newItem);
            }
        }

        for (CwSafetyInspectionList oldItem : oldList) {
            if (!newIds.contains(oldItem.getIspLstId())) {
                oldItem.setDltYn("Y");
                oldItem.setDltDt(LocalDateTime.now());
                oldItem.setDltId(commonReqVo.getUserId());
            }
        }

        safetyService.createSafetyList(oldList);

        // 새로 추가된 항목만 StandardList에 반영
        for (CheckForm.SafetyList listForm : safety.getSafetyLists()) {
            String ispLstId = listForm.getIspLstId();

            // 신규 항목: UUID가 있고, StandardList에 존재하지 않을 때만 추가
            if (ispLstId != null && !safetyService.checkStandard(ispLstId)) {
                String upCnsttyCd = safetyService.findUpCnsttyCd(safety.getCntrctNo(), listForm.getCnsttyCd());
                CwStandardInspectionList standardList = checkForm.toStandardEntity(listForm);
                standardList.setCntrctNo(safety.getCntrctNo());
                standardList.setUpCnsttyCd(upCnsttyCd);
                standardList.setIspLstDscrpt(listForm.getIspDscrpt());
                standardList.setCnsttyLvl((short) 4);
                standardList.setCnsttyYn("N");
                standardList.setDltYn("N");

                safetyService.createStandardList(standardList);
            }
        }
    }

    // 점검결과 등록
    @Transactional
    public void addResult(CheckForm.Safety safety, List<MultipartFile> photos, String usrId) {
        CwSafetyInspection safetyInspection = safetyService.getSafetyWithJpa(safety.getCntrctNo(),
                safety.getInspectionNo());

        List<CwSafetyInspectionList> inspectionLists = safetyService.getSafetyListByJpa(safety.getCntrctNo(),
                safety.getInspectionNo());

        for (CheckForm.SafetyList listForm : safety.getSafetyLists()) {
            for (CwSafetyInspectionList entity : inspectionLists) {
                if (Objects.equals(listForm.getIspLstId(), entity.getIspLstId())) {
                    entity.setGdFltyYn(listForm.getGdFltyYn());
                    entity.setImprvReq(listForm.getImprvReq());
                }
            }
        }
        safetyService.createSafetyList(inspectionLists);

        int fileNo = 0;
        if (photos != null && !photos.isEmpty()) {
            String uploadPath = getUploadPathByWorkType(
                    FileUploadType.SAFETY,
                    safety.getCntrctNo());

            List<CwAttachments> attachments = new ArrayList<>();
            for (MultipartFile file : photos) {
                FileMeta meta = fileService.save(uploadPath, file);

                CwAttachments att = new CwAttachments();
                att.setFileNm(file.getOriginalFilename());
                att.setFileDiskNm(meta.getFileName());
                att.setFileDiskPath(meta.getDirPath());
                att.setFileSize(meta.getSize());
                att.setFileDiv("I");
                att.setDltYn("N");
                att.setFileHitNum(0);

                attachments.add(att);
            }
            fileNo = qualityService.createCwAttachmentsList(attachments);

            for (int i = 0; i < safety.getPhotos().size(); i++) {
                CwSafetyInspectionPhoto photo = checkForm.toEntity(safety.getPhotos().get(i));
                CwAttachments attach = attachments.get(i);

                photo.setInspectionNo(safety.getInspectionNo());
                photo.setCntrctNo(safety.getCntrctNo());
                photo.setPhtSno(attach.getSno().shortValue());
                photo.setAtchFileNo(fileNo);
                photo.setDltYn("N");
                photo.setRgstrId(usrId);
                photo.setRgstDt(LocalDateTime.now());

                safetyService.createPhoto(photo);
            }
        }

        safetyInspection.setIspId(usrId);
        safetyInspection.setRsltYn("Y");
        safetyInspection.setAtchFileNo(fileNo);
        safetyService.createSafety(safetyInspection);
    }

    // 점검결과 수정
    @Transactional
    public void updateResult(CheckForm.Safety safety, List<MultipartFile> photos, String usrId) {
        CwSafetyInspection safetyInspection = safetyService.getSafetyWithJpa(safety.getCntrctNo(),
                safety.getInspectionNo());

        List<CwSafetyInspectionList> inspectionLists = safetyService.getSafetyListByJpa(safety.getCntrctNo(),
                safety.getInspectionNo());
        for (int i = 0; i < safety.getSafetyLists().size(); i++) {
            if (Objects.equals(safety.getSafetyLists().get(i).getIspLstId(), inspectionLists.get(i).getIspLstId())) {
                inspectionLists.get(i).setGdFltyYn(safety.getSafetyLists().get(i).getGdFltyYn());
                inspectionLists.get(i).setImprvReq(safety.getSafetyLists().get(i).getImprvReq());
            }
        }
        safetyService.createSafetyList(inspectionLists);

        int fileNo = 0;
        List<CwSafetyInspectionPhoto> oldPhotos = safetyService.getPhotoList(safety.getCntrctNo(),
                safety.getInspectionNo());
        List<CwAttachments> oldAttachments = new ArrayList<>();

        if (!oldPhotos.isEmpty()) {
            fileNo = oldPhotos.get(0).getAtchFileNo();
            oldAttachments = safetyService.getPhotoFileList(fileNo);
        }

        List<CwAttachments> newAttachments = new ArrayList<>();
        if (photos != null && !photos.isEmpty()) {
            String uploadPath = getUploadPathByWorkType(
                    FileUploadType.SAFETY,
                    safety.getCntrctNo());

            for (MultipartFile file : photos) {
                FileMeta meta = fileService.save(uploadPath, file);

                CwAttachments att = new CwAttachments();
                att.setFileNm(file.getOriginalFilename());
                att.setFileDiskNm(meta.getFileName());
                att.setFileDiskPath(meta.getDirPath());
                att.setFileSize(meta.getSize());
                att.setFileDiv("I");
                att.setFileNo(fileNo == 0 ? null : fileNo);
                att.setDltYn("N");
                att.setFileHitNum(0);

                newAttachments.add(att);
            }

            if (fileNo == 0) {
                fileNo = qualityService.createCwAttachmentsList(newAttachments);
                safetyInspection.setAtchFileNo(fileNo);
            } else {
                qualityService.createCwAttachmentsList(newAttachments);
            }
        }

        for (Integer sno : safety.getDeleteSno()) {
            safetyService.deleteAttachment(fileNo, sno);
        }

        for (Integer phtSno : safety.getDeletePhtSno()) {
            CwSafetyInspectionPhoto toDelete = safetyService.getPhoto(safety.getCntrctNo(), safety.getInspectionNo(),
                    (short) (int) phtSno);
            safetyService.deletePhoto(toDelete);
        }

        List<CheckForm.Photo> newPhotos = safety.getPhotos().stream()
                .filter(p -> p.getPhtSno() == 0)
                .toList();

        // 신규 사진 저장
        for (int i = 0; i < newPhotos.size(); i++) {
            CheckForm.Photo newPhoto = newPhotos.get(i);
            CwAttachments att = newAttachments.get(i); // 같은 index 매핑

            CwSafetyInspectionPhoto photo = checkForm.toEntity(newPhoto);
            photo.setInspectionNo(safety.getInspectionNo());
            photo.setCntrctNo(safety.getCntrctNo());

            // attachment.sno 매핑
            photo.setPhtSno(att.getSno().shortValue());

            photo.setAtchFileNo(fileNo);
            photo.setDltYn("N");
            photo.setRgstrId(usrId);
            photo.setRgstDt(LocalDateTime.now());

            safetyService.createPhoto(photo);
        }

        if (safety.getPhotos().isEmpty()) {
            for (CwSafetyInspectionPhoto p : oldPhotos)
                safetyService.deletePhoto(p);
            for (CwAttachments a : oldAttachments)
                safetyService.deleteAttachment(fileNo, a.getSno());
        }

        safetyService.createSafety(safetyInspection);
    }

    /**
     * 안전점검 점결결과 작성 요청
     */
    public void requestReportSafety(Map<String, Object> param, HttpServletRequest request, CommonReqVo commonReqVo) {
        // 1. 감리에게 보낼 메일 내용 구성
        String recipient = URLDecoder.decode(param.get("emailAdrs").toString()); // 감리 이메일

        String cntrctNo = param.get("cntrctNo").toString();
        String inspectionNo = param.get("inspectionNo").toString();
        String cntrctNm = contractService.getByCntrctNo(cntrctNo).getCntrctNm(); // 계약명

        CwSafetyInspection safety = safetyService.getSafetyWithJpa(cntrctNo, inspectionNo);

        String recipientName = URLDecoder.decode(commonReqVo.getUserName());
        String title = safety.getTitle(); // 제목

        String emailTitle = String.format("[%s] 점검결과작성 요청", cntrctNm);

        // 메인 도메인 설정
        String protocol = request.isSecure() ? "https" : "http";
        String serverName = request.getServerName();
        int port = request.getLocalPort();

        String baseUrl = String.format("%s://%s:%d", protocol, serverName, port);

        String linkUrl = String.format(
                "%s/safetymgmt/check/add/result?cntrctNo=%s&pjtNo=%s&inspectionNo=%s", baseUrl, cntrctNo,
                commonReqVo.getPjtNo(), inspectionNo);

        StringBuilder html = new StringBuilder();
        html.append("<div>")
                .append("<p>아래 정보와 같이 점검결과작성 요청이 왔습니다.<br>")
                .append("바로가기를 통해 점검 결과를 작성해 주세요.<br>")
                .append("<br>")
                .append("-------------------------------------  아래  -------------------------------------</p>")
                .append("<ul>")
                .append("<li><strong>계약명</strong> : ").append(cntrctNm).append("</li>")
                .append("<li><strong>검측요청자</strong> : ").append(recipientName).append("</li>")
                .append("<li><strong>제목</strong> : ").append(title).append("</li>")
                .append("</ul>")
                .append("<p><a href='").append(linkUrl).append("'>[바로가기]</a></p>")
                .append("</div>");

        // 2. 요청 디비에 데이터 저장
        Map<String, Object> insertParam = new HashMap<>();

        insertParam.put("reqInsId", inspectionNo); // 요청 ID
        insertParam.put("pjtNo", commonReqVo.getPjtNo()); // 프로젝트 번호
        insertParam.put("cntrctNo", cntrctNo); // 계약 번호
        insertParam.put("reqAppDiv", "SAF"); // 업무구분: 안전점검
        insertParam.put("toUsrId", param.get("usrId")); // 감리 ID
        insertParam.put("rgstrId", commonReqVo.getUserId()); // 등록자
        insertParam.put("rgstDt", LocalDateTime.now()); // 등록일
        insertParam.put("dltYn", "N"); // 삭제여부 : default 'N'
        insertParam.put("endYn", "N"); // 업무처리구분 : default 'N'

        qualityService.insertRequestItem(insertParam);

        // 3. 품질검측 결재자 항목에 선택된 감리 세팅 후 업데이트
        safety.setIspId(param.get("usrId").toString()); // 검측자 세팅
        safety.setCnstrtnId(commonReqVo.getUserId()); // 점검요청자 세팅
        safety.setIspReqDt(LocalDateTime.now()); // 점검요청일 세팅
        safetyService.createSafety(safety);

        // 4. DB 성공 후 메일 전송
        mailService.mailSend(recipient, emailTitle, html.toString());
    }

    /**
     * 안전점검 승인요청
     */
    public void requestApprovalSafety(CheckForm.Safety input, CommonReqVo commonReqVo) {
        String isApiYn = commonReqVo.getApiYn();
        String pjtDiv = commonReqVo.getPjtDiv();
        
        SafetyOutput output = safetyService.getSafetyWithQuery(input.getCntrctNo(), input.getInspectionNo());
        CwSafetyInspection safety = safetyService.getSafetyWithJpa(input.getCntrctNo(), input.getInspectionNo());

        if (safety == null) {
            throw new BizException("안전점검 정보가 없습니다.");
        }

        // 필요한 리소스 조회
        List<CwSafetyInspectionList> cwSafetyInspectionLists = safetyService.findInspectionList(safety);
        List<CwSafetyInspectionPhoto> cwSafetyPhotos = safetyService.findPhotoList(safety);
        List<CwStandardInspectionList> standardInspectionLists = safetyService.findStandardList(safety);
        List<Map<String, Object>> safetyFileInfo = Collections.emptyList();
        if (safety != null && safety.getAtchFileNo() != null) { // 안전점검 첨부파일 (파일 객체 대신 메타데이터만 전송)
            List<CwAttachments> safetyFiles = safetyService.findAttachment(safety);
            safetyFileInfo = safetyService.convertToFileInfo(safetyFiles);
        }

        // 안전점검 리소스 데이터
        Map<String, Object> resourceMap = new HashMap<>();
        resourceMap.put("safetyFileInfo", safetyFileInfo);
        resourceMap.put("inspectionlist", cwSafetyInspectionLists);
        resourceMap.put("standardLists", standardInspectionLists);
        resourceMap.put("photo", cwSafetyPhotos);

        // 안전점검 승인요청 시 필요 데이터
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("pjtNo", commonReqVo.getPjtNo());
        requestMap.put("cntrctNo", safety.getCntrctNo());
        requestMap.put("isApiYn", isApiYn);
        requestMap.put("pjtDiv", pjtDiv);
        requestMap.put("usrId", commonReqVo.getUserId());

        approvalRequestService.insertSafetyIspDoc(output, safety, requestMap, resourceMap);
    }

    /**
     * 안전점검 삭제(논리)
     */
    @Transactional
    public void deleteSafetyYn(String cntrctNo, String inspectionNo) {
        CwSafetyInspection deleteSafety = safetyService.getSafetyWithJpa(cntrctNo, inspectionNo);
        safetyService.deleteSafetyYn(deleteSafety);

        if (deleteSafety.getApDocId() != null) {
            approvalRequestService.deleteApDoc(deleteSafety.getApDocId());
        }
    }
}

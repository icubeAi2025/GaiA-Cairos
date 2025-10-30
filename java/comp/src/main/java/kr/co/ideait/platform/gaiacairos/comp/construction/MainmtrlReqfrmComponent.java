package kr.co.ideait.platform.gaiacairos.comp.construction;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.ideait.platform.gaiacairos.comp.construction.service.MainmtrlReqfrmService;
import kr.co.ideait.platform.gaiacairos.comp.document.service.DocumentService;
import kr.co.ideait.platform.gaiacairos.comp.eapproval.DraftComponent;
import kr.co.ideait.platform.gaiacairos.comp.eapproval.helper.EapprovalHelper;
import kr.co.ideait.platform.gaiacairos.comp.eapproval.service.ApprovalRequestService;
import kr.co.ideait.platform.gaiacairos.comp.mail.service.MailService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractComponent;
import kr.co.ideait.platform.gaiacairos.core.constant.CommonCodeConstants;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.*;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction.MainmtrlReqfrmMybatisParam;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction.MainmtrlReqfrmMybatisParam.MainmtrlReqfrmOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction.mainmtrlReqfrm.MainmtrlReqfrmForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction.mainmtrlReqfrm.MainmtrlReqfrmForm.Mainmtrl;
import kr.co.ideait.platform.gaiacairos.core.type.FileUploadType;
import kr.co.ideait.platform.gaiacairos.core.util.FileService;
import kr.co.ideait.platform.gaiacairos.core.util.FileService.FileMeta;
import kr.co.ideait.platform.gaiacairos.core.util.restclient.DocumentServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class MainmtrlReqfrmComponent extends AbstractComponent {

    @Autowired
    DraftComponent draftComponent;

    @Autowired
    MainmtrlReqfrmService mainmtrlReqfrmService;

    @Autowired
    FileService fileService;

    @Autowired
    ApprovalRequestService approvalRequestService;

    @Autowired
    MainmtrlReqfrmForm mainmtrlReqfrmForm;

    @Autowired
    MailService mailService;

    @Autowired
    DocumentService documentService;

    @Autowired
    DocumentServiceClient documentServiceClient;

    /*
     * 목록조회
     */
    public List<MainmtrlReqfrmMybatisParam.MainmtrlReqfrmOutput> getMainmtrlReqfrmList(
            MainmtrlReqfrmForm.MainmtrlReqfrm mainmtrlReqfrmInput) {
        MainmtrlReqfrmMybatisParam.MainmtrlReqfrmInput input = mainmtrlReqfrmForm
                .toMainmtrlReqfrmInput(mainmtrlReqfrmInput);

        input.setWorkcode(CommonCodeConstants.WORKTYPE_CODE_GROUP_CODE);
        input.setResultcode(CommonCodeConstants.RSLT_CODE_GROUP_CODE);
        input.setPaymentcode(CommonCodeConstants.APPSTATUS_CODE_GROUP_CODE);

        return mainmtrlReqfrmService.getMainmtrlReqfrmList(input);
    }

    /*
     * 상세조회
     * - 기본데이터, 자재, 첨부파일, 사진, 사진파일 분리 조회
     */
    public Map<String, Object> getMainmtrlReqfrm(String cntrctNo, String reqfrmNo) {

        // 기본데이터
        Map<String, Object> params = new HashMap<>();
        params.put("cntrctNo", cntrctNo);
        params.put("reqfrmNo", reqfrmNo);
        params.put("workcode", CommonCodeConstants.WORKTYPE_CODE_GROUP_CODE);
        params.put("paymentcode", CommonCodeConstants.PAYMENT_CODE_GROUP_CODE);
        params.put("rsltcode", CommonCodeConstants.RSLT_CODE_GROUP_CODE);
        MainmtrlReqfrmOutput mainmtrlReqfrm = mainmtrlReqfrmService.getMainmtrlReqfrm(params);

        // 사진 및 첨부파일
        List<CwMainmtrlReqfrmPhoto> photoList = mainmtrlReqfrmService.getPhotoList(cntrctNo, reqfrmNo);
        List<CwAttachments> attachments = new ArrayList<>();
        List<CwAttachments> photoAttachments = new ArrayList<>();
        if (mainmtrlReqfrm.getAtchFileNo() != null) {
            attachments = mainmtrlReqfrmService.getFileList(mainmtrlReqfrm.getAtchFileNo());
            photoAttachments = mainmtrlReqfrmService.getImgFileList(mainmtrlReqfrm.getAtchFileNo());
        }

        // 자재
        MybatisInput input = new MybatisInput().add("cntrctNo", cntrctNo).add("reqfrmNo", reqfrmNo);
        List<MainmtrlReqfrmMybatisParam.MainmtrlReqfrmOutput> mainmtrls = mainmtrlReqfrmService
                .getAddMainmtrlList(input);

        Map<String, Object> result = new HashMap<>();
        result.put("mainmtrlReqfrm", mainmtrlReqfrm);
        result.put("photoList", photoList);
        result.put("attachments", attachments);
        result.put("photoAttachments", photoAttachments);
        result.put("mainmtrls", mainmtrls);

        return result;
    }

    /*
     * 추가
     * 기본데이터
     * 첨부파일
     * 자재목록
     * 사진(데이터, 첨부파일)
     */
    @Transactional
    public void createMainmtrlReqfrm(MainmtrlReqfrmForm.MainmtrlReqfrm input, List<MultipartFile> files,
            List<MultipartFile> photos, String usrId) {

        // 기본 데이터
        CwMainmtrlReqfrm mainmtrlReqfrm = mainmtrlReqfrmForm.toEntity(input);
        mainmtrlReqfrm.setDltYn("N");

        List<CwAttachments> fileList = new ArrayList<>();
        List<CwAttachments> photoList = new ArrayList<>();
        int fileNo = 0;
        String uploadPath = getUploadPathByWorkType(FileUploadType.MAINMTRL_REQFRM, mainmtrlReqfrm.getCntrctNo());

        // 첨부파일 저장(사진 첨부파일x)
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                FileMeta fileMeta = fileService.save(uploadPath, file);
                CwAttachments attach = createAttachment(file, fileMeta, "F");
                fileList.add(attach);
            }
            fileNo = mainmtrlReqfrmService.createCwAttachmentsList(fileList);
            mainmtrlReqfrm.setAtchFileNo(fileNo);
        }

        // 주요자재 검수요청서 저장 후 PK값 리턴
        String reqfrmNo = mainmtrlReqfrmService.createMainmtrlReqfrm(mainmtrlReqfrm).getReqfrmNo();

        // 사진 데이터 등록(사진 첨부파일 포함)
        if (photos != null && !photos.isEmpty()) {
            for (MultipartFile file : photos) {
                FileMeta fileMeta = fileService.save(uploadPath, file);
                CwAttachments attach = createAttachment(file, fileMeta, "I");

                if (fileNo != 0) {
                    attach.setFileNo(fileNo);
                }
                photoList.add(attach);
            }

            if (fileNo == 0) {
                fileNo = mainmtrlReqfrmService.createCwAttachmentsList(photoList);
                mainmtrlReqfrm.setAtchFileNo(fileNo);
                mainmtrlReqfrmService.updateMainmtrlReqfrmFileNo(mainmtrlReqfrm.getReqfrmNo(), fileNo, usrId);
            } else {
                mainmtrlReqfrmService.createCwAttachmentsList(photoList);
            }

            for (int i = 0; i < input.getPhotos().size(); i++) { // 사진 데이터 등록
                CwMainmtrlReqfrmPhoto photo = mainmtrlReqfrmForm.toEntity(input.getPhotos().get(i));
                CwAttachments attach = photoList.get(i);

                photo.setReqfrmNo(reqfrmNo);
                photo.setCntrctNo(input.getCntrctNo());
                photo.setPhtSno(attach.getSno());
                photo.setAtchFileNo(fileNo);
                photo.setDltYn("N");

                // 상위 mainmtrlReqfrmService의 메소드들로 인해 JPA영속성 끊킴. JPA @PrePersist 사용불가. 직접 할당해야함
                photo.setRgstrId(usrId);
                photo.setRgstDt(LocalDateTime.now());
                mainmtrlReqfrmService.createPhoto(photo);
            }
        }

        // 자재 데이터 저장
        for (int i = 0; i < input.getMtrlList().size(); i++) {
            CwMainmtrl mainMtrl = mainmtrlReqfrmForm.toEntity(input.getMtrlList().get(i));
            mainMtrl.setCntrctNo(input.getCntrctNo());
            mainMtrl.setReqfrmNo(reqfrmNo);
            mainMtrl.setDltYn("N");
            mainmtrlReqfrmService.createMainmtrl(mainMtrl);
        }
    }

    /*
     * 수정
     */
    @Transactional
    public void updateMainmtrlReqfrm(MainmtrlReqfrmForm.MainmtrlReqfrm mainmtrlReqfrm, List<MultipartFile> newFiles,
            List<Integer> removedFileNos, List<Integer> removedSnos, List<MultipartFile> photos, String usrId) {

        CwMainmtrlReqfrm oldMainmtrlReqfrm = mainmtrlReqfrmService.getMainmtrlReqfrm(mainmtrlReqfrm.getCntrctNo(),
                mainmtrlReqfrm.getReqfrmNo());

        // 화면에서 받아온 데이터 업데이트
        mainmtrlReqfrmForm.updateMainmtrlReqfrm(mainmtrlReqfrm, oldMainmtrlReqfrm);

        // 공통 fileNo(첨부/사진 모두 동일하게 사용)
        Integer fileNo = oldMainmtrlReqfrm.getAtchFileNo();

        // 기존에 파일번호 없을 시 새로 세팅
        if (fileNo == null || fileNo == 0) {
            fileNo = mainmtrlReqfrmService.generateFileNo();
            oldMainmtrlReqfrm.setAtchFileNo(fileNo);
        }

        // 삭제된 첨부파일 처리
        if (removedFileNos != null && removedSnos != null) {
            for (int i = 0; i < removedSnos.size(); i++) {
                mainmtrlReqfrmService.deleteAttachment(removedFileNos.get(i), removedSnos.get(i));
            }
        }

        // 새로운 첨부파일 추가
        if (newFiles != null && !newFiles.isEmpty()) {
            List<CwAttachments> attachList = new ArrayList<>();
            String uploadPath = getUploadPathByWorkType(FileUploadType.MAINMTRL_REQFRM, mainmtrlReqfrm.getCntrctNo());

            for (MultipartFile file : newFiles) {
                FileMeta meta = fileService.save(uploadPath, file);
                CwAttachments attach = createAttachment(file, meta, "F");
                attach.setFileNo(fileNo);
                attachList.add(attach);
            }
            mainmtrlReqfrmService.createCwAttachmentsList(attachList);
        }

        // 사진 처리
        List<CwMainmtrlReqfrmPhoto> oldPhotos = mainmtrlReqfrmService.getPhotoList(mainmtrlReqfrm.getCntrctNo(),
                mainmtrlReqfrm.getReqfrmNo());

        // 새로운 사진 파일 추가
        List<CwAttachments> newPhotoAttach = new ArrayList<>();
        if (photos != null && !photos.isEmpty()) {
            for (MultipartFile file : photos) {
                String uploadPath = getUploadPathByWorkType(FileUploadType.MAINMTRL_REQFRM,
                        mainmtrlReqfrm.getCntrctNo());
                FileMeta meta = fileService.save(uploadPath, file);
                CwAttachments attach = createAttachment(file, meta, "I");
                attach.setFileNo(fileNo);
                newPhotoAttach.add(attach);
            }
            mainmtrlReqfrmService.createCwAttachmentsList(newPhotoAttach);
        }

        // 저장된 사진 첨부파일을 기준으로 사진관련 데이터 추출
        List<CwAttachments> savedPhotoAttach = mainmtrlReqfrmService.getImgFileList(fileNo);

        // 기존 사진 sno 목록
        Set<Integer> oldSnos = oldPhotos.stream()
                .map(CwMainmtrlReqfrmPhoto::getPhtSno)
                .collect(Collectors.toSet());

        // 신규 사진만 필터링 (form 기준)
        List<MainmtrlReqfrmForm.Photo> newFormPhotos = mainmtrlReqfrm.getPhotos().stream()
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
            MainmtrlReqfrmForm.Photo formPhoto = newFormPhotos.get(i);

            CwMainmtrlReqfrmPhoto newPhoto = mainmtrlReqfrmForm.toEntity(formPhoto);
            newPhoto.setCntrctNo(mainmtrlReqfrm.getCntrctNo());
            newPhoto.setReqfrmNo(mainmtrlReqfrm.getReqfrmNo());
            newPhoto.setAtchFileNo(fileNo);
            newPhoto.setPhtSno(savedAttach.getSno()); // 신규 첨부파일 sno 그대로
            newPhoto.setDltYn("N");
            newPhoto.setRgstrId(usrId);
            newPhoto.setRgstDt(LocalDateTime.now());

            mainmtrlReqfrmService.createPhoto(newPhoto);
        }

        // 사진 삭제
        for (Integer sno : mainmtrlReqfrm.getDeletePhtSno()) {
            CwMainmtrlReqfrmPhoto delPhoto = mainmtrlReqfrmService.getPhoto(mainmtrlReqfrm.getCntrctNo(),
                    mainmtrlReqfrm.getReqfrmNo(), sno);
            mainmtrlReqfrmService.deletePhoto(delPhoto);
        }

        // 사진 파일 삭제
        for (Integer sno : mainmtrlReqfrm.getDeleteSno()) {
            mainmtrlReqfrmService.deleteAttachment(fileNo, sno);
        }

        // 기본자재 데이터 변경시 수정
        for (Mainmtrl mtrl : mainmtrlReqfrm.getMtrlList()) {
            MybatisInput input = new MybatisInput();
            input.add("reqfrmNo", mainmtrlReqfrm.getReqfrmNo())
                    .add("cntrctNo", mainmtrlReqfrm.getCntrctNo())
                    .add("gnrlexpnsCd", mtrl.getGnrlexpnsCd())
                    .add("todayQty", mtrl.getTodayQty())
                    .add("rmrk", mtrl.getRmrk())
                    .add("updateType", "mtrlUpdate")
                    .add("mtrlChgYn",mtrl.getMtrlChgYn());

            mainmtrlReqfrmService.updateMainmtrlList(input);
        }

        // 자재 목록 수정(기존 목록에서 삭제, 새로 추가)
        // 기존의 자재목록 자원코드 리스트
        MybatisInput gnrlexpnsCdinput = new MybatisInput()
                .add("cntrctNo", mainmtrlReqfrm.getCntrctNo())
                .add("reqfrmNo", mainmtrlReqfrm.getReqfrmNo());
        List<String> gnrlexpnsCdListOutPut = mainmtrlReqfrmService.getGnrlexpnsCdList(gnrlexpnsCdinput);

        List<String> gnrlexpnsCdListInPut = mainmtrlReqfrm.getMtrlList().stream()
                .map(Mainmtrl::getGnrlexpnsCd)
                .toList();

        // 중복 제거를 위해 Set으로 변환
        Set<String> oldSet = new HashSet<>(gnrlexpnsCdListOutPut);
        Set<String> newSet = new HashSet<>(gnrlexpnsCdListInPut);

        // 삭제된 자재들 코드
        List<String> removed = oldSet.stream()
                .filter(cd -> !newSet.contains(cd))
                .toList();
        log.info("삭제된 자재 코드: {}", removed);

        // 자재들 삭제
        for (String removedCd : removed) {
            MybatisInput removedCdinput = new MybatisInput()
                    .add("cntrctNo", mainmtrlReqfrm.getCntrctNo())
                    .add("reqfrmNo", mainmtrlReqfrm.getReqfrmNo())
                    .add("chgId", usrId)
                    .add("chgDt", LocalDateTime.now())
                    .add("gnrlexpnsCd", removedCd);
            mainmtrlReqfrmService.deleteMainmtrl(removedCdinput);
        }

        // 추가된 자재들 코드
        List<String> added = newSet.stream()
                .filter(cd -> !oldSet.contains(cd))
                .toList();

        // 추가된 코드에 해당하는 CwMainmtrl 리스트
        List<CwMainmtrl> addedList = mainmtrlReqfrm.getMtrlList().stream()
                .filter(mtrl -> added.contains(mtrl.getGnrlexpnsCd()))
                .map(mtrl -> {
                    CwMainmtrl mainmtrl = mainmtrlReqfrmForm.toEntity(mtrl);
                    mainmtrl.setCntrctNo(mainmtrlReqfrm.getCntrctNo());
                    mainmtrl.setReqfrmNo(mainmtrlReqfrm.getReqfrmNo());
                    mainmtrl.setRgstrId(usrId);
                    mainmtrl.setRgstDt(LocalDateTime.now());
                    mainmtrl.setDltYn("N");
                    return mainmtrl;
                })
                .toList();

        // 새로운 자재들 추가
        for (CwMainmtrl mainmtrl : addedList) {
            mainmtrlReqfrmService.createMainmtrl(mainmtrl);
        }

        mainmtrlReqfrmService.createMainmtrlReqfrm(oldMainmtrlReqfrm);
    }

    // 삭제
    @Transactional
    public void deleteMainmtrlReqfrm(MainmtrlReqfrmForm.MainmtrlReqfrmList mainmtrlReqfrmList,
            CommonReqVo commonReqVo) {
        mainmtrlReqfrmList.getMainmtrlReqfrmList().forEach((mainmtrlReqfrm) -> {
            MybatisInput input = new MybatisInput()
                    .add("cntrctNo", mainmtrlReqfrm.getCntrctNo())
                    .add("reqfrmNo", mainmtrlReqfrm.getReqfrmNo())
                    .add("usrId", commonReqVo.getUserId());
            mainmtrlReqfrmService.deleteMainmtrlReqfrm(input);
            mainmtrlReqfrmService.updateDeleteReqItem(input);
        });
    }

    /**
     * 첨부파일 다운로드
     */
    public ResponseEntity<Resource> fileDownload(Integer fileNo, Integer sno) {
        // 1. 파일 메타정보 조회
        CwAttachments file = mainmtrlReqfrmService.getAttachments(fileNo, sno);
        if (file == null) {
            throw new GaiaBizException(ErrorType.NOT_FOUND, "첨부파일 정보가 없습니다.");
        }

        // 2. 실제 파일 경로
        Resource resource = fileService.getFile(file.getFileDiskPath(), file.getFileDiskNm());
        if (resource == null || !resource.exists()) {
            throw new GaiaBizException(ErrorType.NOT_FOUND, "파일이 존재하지 않습니다.");
        }

        // 3. 파일명 인코딩 (한글/공백/특수문자 대응)
        String fileNm = file.getFileNm();
        String encodedDownloadFile = URLEncoder.encode(fileNm, StandardCharsets.UTF_8);
        encodedDownloadFile = encodedDownloadFile.replaceAll("\\+", "%20");

        // 4. 다운로드 수 업데이트
        // MybatisInput input = MybatisInput.of().add("fileNo", fileNo).add("sno", sno);
        // updateDtAttachmentsViewCount(input);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedDownloadFile + "\"")
                .body(resource);
    }

    /**
     * 첨부파일 생성
     */
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
     * 감리목록 조회
     */
    public List<MainmtrlReqfrmMybatisParam.MainmtrlReqfrmOutput> getSupervisionList(String pjtNo, String cntrctNo,
            String platform, String searchValue) {
        MybatisInput input = new MybatisInput().add("pjtNo", pjtNo).add("cntrctNo", cntrctNo).add("pjtType", platform)
                .add("searchValue", searchValue).add("rankCd", CommonCodeConstants.RANK_CODE_GROUP_CODE);
        return mainmtrlReqfrmService.getSupervisionList(input);
    }

    /*
     * 검수요청
     */
    @Transactional
    public void inspectionRequestList(List<Map<String, Object>> paramList, HttpServletRequest request,
            CommonReqVo commonReqVo) {

        String usrId = commonReqVo.getUserId();
        String usrNm = commonReqVo.getUserName();
        String pjtNo = commonReqVo.getPjtNo();

        for (Map<String, Object> param : paramList) {
            String cntrctNo = param.get("cntrctNo").toString();
            String reqfrmNo = param.get("reqfrmNo").toString();

            CwMainmtrlReqfrm mainmtrlReqfrm = mainmtrlReqfrmService.getMainmtrlReqfrm(cntrctNo, reqfrmNo);

            // 요청 삭제 후 처리
            mainmtrlReqfrmService.deleteReqItem(reqfrmNo);

            mainmtrlReqfrm.setRsltCd(null);
            mainmtrlReqfrm.setCmDt(null);
            mainmtrlReqfrm.setRsltOpnin(null);
            mainmtrlReqfrm.setApprvlDt(null);
            mainmtrlReqfrm.setApprvlId(null);
            mainmtrlReqfrm.setApprvlStats(null);
            

            mainmtrlReqfrmService.createMainmtrlReqfrm(mainmtrlReqfrm);

            // 전자결재 존재 시 관련 전자결재 문서도 삭제
            if(mainmtrlReqfrm.getApDocId() != null && !mainmtrlReqfrm.getApDocId().isBlank()){
                approvalRequestService.deleteApDocByApDocId(mainmtrlReqfrm.getApDocId());
            }
            
            inspectionRequest(param, request, usrId, usrNm, pjtNo);
        }
    }

    /**
     * 검수요청 - 메일 전송
     */
    public void inspectionRequest(Map<String, Object> param, HttpServletRequest request, String usrId, String usrNm,
            String pjtNo) {

        String cntrctNo = param.get("cntrctNo").toString();
        String reqfrmNo = param.get("reqfrmNo").toString();
        String cntrctNm = param.get("cntrctNm").toString();
        List<Map<String, String>> supervisionList = (List<Map<String, String>>) param.get("supervisionList");

        CwMainmtrlReqfrm mainmtrlReqfrm = mainmtrlReqfrmService.getMainmtrlReqfrm(cntrctNo, reqfrmNo);

        String recipientName = URLDecoder.decode(usrNm); // 검수 요청자
        String cnsttyNm = param.get("cnsttyNm").toString(); // 공종명
        String prdnm = mainmtrlReqfrm.getPrdnm(); // 품명
        String markNm = mainmtrlReqfrm.getMakrNm(); // 제조회사명

        String title = String.format("[%s] 주요자재 \"%s\" 검수 요청", cntrctNm, prdnm);

        // 메인 도메인 설정
        String protocol = request.isSecure() ? "https" : "http";
        String serverName = request.getServerName();
        int port = request.getLocalPort();

        String baseUrl = String.format("%s://%s:%d", protocol, serverName, port);

        // 검수결과 등록 화면
        String linkUrl = String.format(
                "%s/construction/mainmtrlreqfrm/addMtrlReqfrmResult?type=d&mode=create&pjtNo=%s&cntrctNo=%s&reqfrmNo=%s&returnType=A",
                baseUrl, pjtNo, cntrctNo, reqfrmNo);

        StringBuilder html = new StringBuilder();
        html.append("<div>")
                .append("<p>아래 정보와 같이 검수 요청이 왔습니다.<br>")
                .append("바로가기를 통해 검수 결과를 등록해 주세요.<br>")
                .append("<br>")
                .append("-------------------------------------  아래  -------------------------------------</p>")
                .append("<ul>")
                .append("<li><strong>계약명</strong> : ").append(cntrctNm).append("</li>")
                .append("<li><strong>검수요청자</strong> : ").append(recipientName).append("</li>")
                .append("<li><strong>공종명</strong> : ").append(cnsttyNm).append("</li>")
                .append("<li><strong>품명</strong> : ").append(prdnm).append("</li>")
                .append("<li><strong>제조회사명</strong> : ").append(markNm).append("</li>")
                .append("</ul>")
                .append("<p><a href='").append(linkUrl).append("'>[바로가기]</a></p>")
                .append("</div>");

        // 요청 디비에 데이터 저장
        Map<String, Object> insertParam = new HashMap<>();

        log.info("param 전체: {}", param);

        insertParam.put("reqInsId", reqfrmNo); // 요청 ID
        insertParam.put("pjtNo", pjtNo); // 프로젝트 번호
        insertParam.put("cntrctNo", cntrctNo); // 계약 번호
        insertParam.put("reqAppDiv", "MMR"); // 업무구분: 품질검수
        insertParam.put("rgstrId", usrId); // 등록자
        insertParam.put("rgstDt", LocalDateTime.now()); // 등록일
        insertParam.put("dltYn", "N"); // 삭제여부 : default 'N'
        insertParam.put("endYn", "N"); // 업무처리구분 : default 'N'



        // 감리에게 메일 전송
        for (Map<String, String> supervision : supervisionList) {
            // 퀵메뉴 db저장
            insertParam.put("toUsrId", supervision.get("usrId"));
            mainmtrlReqfrmService.insertRequestItem(insertParam);

            String recipient = URLDecoder.decode(supervision.get("emailAdrs"));

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

            // 주요자재 검수자 항목에 선택된 감리 세팅 후 업데이트
            mainmtrlReqfrm.setCmId(supervision.get("usrId")); // 검수자 세팅
        }
        mainmtrlReqfrm.setReqId(usrId); // 검수요청자 세팅
        mainmtrlReqfrmService.createMainmtrlReqfrm(mainmtrlReqfrm);
    }

    /*
     * 주요자재 검수요청서 검수결과 등록/수정
     */
    @Transactional
    public void addMtrlReqfrmResult(MainmtrlReqfrmForm.MainmtrlReqfrm mainmtrlReqfrm, String usrId) {
        mainmtrlReqfrm.setCmId(usrId);
        mainmtrlReqfrm.setRsltYn("Y");

        CwMainmtrlReqfrm oldMainmtrlReqfrm = mainmtrlReqfrmService.getMainmtrlReqfrm(
                mainmtrlReqfrm.getCntrctNo(),
                mainmtrlReqfrm.getReqfrmNo());
        mainmtrlReqfrmForm.updateMainmtrlReqfrm(mainmtrlReqfrm, oldMainmtrlReqfrm);

        mainmtrlReqfrmService.createMainmtrlReqfrm(oldMainmtrlReqfrm);

        String reqfrm = mainmtrlReqfrm.getReqfrmNo();
        String cntrctNo = mainmtrlReqfrm.getCntrctNo();

        if ("02".equals(mainmtrlReqfrm.getRsltCd())) {
            // 일부불합격
            mainmtrlReqfrm.getPartialFailList().forEach((partialFail) -> {
                String gnrlexpnsCd = partialFail.getGnrlexpnsCd();
                BigDecimal passQty = partialFail.getPassQty();
                BigDecimal failQty = partialFail.getFailQty();

                MybatisInput input = new MybatisInput().add("reqfrmNo", reqfrm).add("cntrctNo", cntrctNo)
                        .add("gnrlexpnsCd", gnrlexpnsCd).add("passQty", passQty).add("failQty", failQty).add("updateType", "resultUpdate");;
                mainmtrlReqfrmService.updateMainmtrlList(input);
            });
        } else {
            // 전체합격/불합격
            String gnrlexpnsCd = mainmtrlReqfrm.getPartialFailList().getFirst().getGnrlexpnsCd();
            String passYn = mainmtrlReqfrm.getPartialFailList().getFirst().getPassYn();
            MybatisInput input = new MybatisInput().add("reqfrmNo", reqfrm).add("cntrctNo", cntrctNo)
                    .add("gnrlexpnsCd", gnrlexpnsCd).add("passYn", passYn).add("updateType", "resultUpdate");
            mainmtrlReqfrmService.updateMainmtrlList(input);
        }
    }

    /*
     * 주요자재 검수요청서 추가된 자재 목록
     */
    public List<MainmtrlReqfrmMybatisParam.MainmtrlReqfrmOutput> getAddMainmtrlList(String cntrctNo, String reqfrmNo) {

        MybatisInput input = new MybatisInput().add("cntrctNo", cntrctNo).add("reqfrmNo", reqfrmNo);

        return mainmtrlReqfrmService.getAddMainmtrlList(input);
    }

    /*
     * 주요자재 검수요청서 주요자재 목록
     */
    public List<MainmtrlReqfrmMybatisParam.MainmtrlOutput> getMainmtrlList(
            MainmtrlReqfrmForm.MainmtrlReqfrm mainmtrlReqfrmInput) {
        MainmtrlReqfrmMybatisParam.MainmtrlReqfrmInput input = mainmtrlReqfrmForm
                .toMainmtrlReqfrmInput(mainmtrlReqfrmInput);

        return mainmtrlReqfrmService.getMainmtrlList(input);
    }

    /**
     * 결재요청
     */
    @Transactional
    public void paymentRequest(List<Map<String, String>> paramList, CommonReqVo commonReqVo) {
        String isApiYn = commonReqVo.getApiYn();
        String pjtDiv = commonReqVo.getPjtDiv();

        for (int i = 0; i < paramList.size(); i++) {
            CwMainmtrlReqfrm cwMainmtrlReqfrm = mainmtrlReqfrmService
                    .getMainmtrlReqfrm(paramList.get(i).get("cntrctNo"), paramList.get(i).get("reqfrmNo"));

            if (cwMainmtrlReqfrm == null) {
                throw new GaiaBizException(ErrorType.BAD_REQUEST, "결재요청 정보가 없습니다.");
            }

            // 필요한 리소스 조회

            // 주요자재 승인요청 시 필요 데이터
            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("pjtNo", commonReqVo.getPjtNo());
            requestMap.put("cntrctNo", cwMainmtrlReqfrm.getCntrctNo());
            requestMap.put("isApiYn", isApiYn);
            requestMap.put("pjtDiv", pjtDiv);
            requestMap.put("usrId", commonReqVo.getUserId());

            // 결재요청 로직
            approvalRequestService.insertMainmtrlReqfrmAppDoc(cwMainmtrlReqfrm, requestMap,
                    paramList.get(i).get("cnsttyNm"), paramList.get(i).get("rsltNm"));
        }
    }

    /*
     * 주요자재 검수요청서 결재취소
     * 1. 전자결재 문서 삭제 with apDocID
     * 2. 통합문서에 저장된 문서 존재 시 문서 삭제 with docId
     */
    public void cancelPayment(List<Map<String, String>> paramList, CommonReqVo commonReqVo) {
        try {
            // 기본 데이터 세팅(삭제할 주요자재 id 리스트, 계약번호)
            List<String> idList = paramList.stream()
                    .map(p -> p.get("reqfrmNo"))
                    .filter(Objects::nonNull)
                    .toList();
            String cntrctNo = paramList.get(0).get("cntrctNo");

            Map<String, Object> idsMap = new HashMap<>();
            idsMap.put("idList", idList);
            idsMap.put("cntrctNo", cntrctNo);

            // 1. 주요자재 검수요청서의 자재들의 불합격여부를 초기화 
            for (int i = 0; i < idList.size(); i++) {
                List<CwMainmtrl> mainmtrl = mainmtrlReqfrmService.getMainmtrlList(cntrctNo, idList.get(i));
                for (int j = 0; j < mainmtrl.size(); j++) {
                    mainmtrl.get(j).setFailQty(null);

                    mainmtrlReqfrmService.createMainmtrl(mainmtrl.get(j));
                }
            }

            // 2. 전자결재 문서 조회 조건 세팅
            List<String> apDocIds = mainmtrlReqfrmService.getApDocIds(idsMap); // 삭제할 전자결재 문서 아이디 리스트
            log.info("apDocIds = {}", apDocIds);

            List<ApDoc> deleteApDocList = apDocIds.stream()
                    .filter(Objects::nonNull)
                    .map(apDocId -> {
                        ApDoc apDoc = new ApDoc();
                        apDoc.setApDocId(apDocId);
                        apDoc.setApType(EapprovalHelper.MAINMTRL_REQFRM_DOC);
                        return apDoc;
                    })
                    .collect(Collectors.toList());

            Map<String, Object> reqVoMap = new HashMap<>();
            reqVoMap.put("apiYn", (String) commonReqVo.getApiYn());
            reqVoMap.put("pjtDiv", (String) commonReqVo.getPjtDiv());

            // 전자결재 승인 취소(전자결재 요청자, 전자결재 요청 일자, 전자결재 문서ID, 승인상태 초기화)
            if (!deleteApDocList.isEmpty()) {
                draftComponent.setDeleteList(deleteApDocList, reqVoMap);
            }

            // 4. 통합문서 조회 조건 세팅
            List<String> docIds = mainmtrlReqfrmService.getDocIds(idsMap); // 삭제할 통합문서 아이디 리스트

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
                docParam.put("usrId", commonReqVo.getUserId());

                // 통합문서 삭제
                Result docResult = documentServiceClient.removeDocument(docParam);
                if (!docResult.isOk()) {
                    throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR, "자재검수 요청서 통합문서 초기화 중 에러발생");
                }
            }
        } catch (GaiaBizException e) {
            log.error("자재검수 요청서 결재취소 중 오류 발생 error = {}", e.getMessage(), e);
            throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR, e);
        }
    }
}

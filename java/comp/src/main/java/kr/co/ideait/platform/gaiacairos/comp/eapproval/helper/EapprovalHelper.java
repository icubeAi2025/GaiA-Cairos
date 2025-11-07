package kr.co.ideait.platform.gaiacairos.comp.eapproval.helper;

import kr.co.ideait.platform.gaiacairos.core.base.AbstractBase;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.ApAttachments;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.ApDoc;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmUserInfo;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.ApAttachmentsRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.ApDocRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.SmUserInfoRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.eapproval.approval.ApprovalMybatisParam;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.eapproval.approval.ApprovalMybatisParam.AlarmInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.eapproval.approval.ApprovalMybatisParam.SearchPjtInfo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.type.FileUploadType;
import kr.co.ideait.platform.gaiacairos.core.util.FileService;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Component
public class EapprovalHelper extends AbstractBase {

    @Autowired
    @Qualifier("sqlSessionTemplate")
    SqlSessionTemplate mybatisSession;

    @Autowired
    @Qualifier("icsSqlSessionTemplate")
    SqlSessionTemplate mybatisSessionIsc;

	@Autowired
	FileService fileService;

    @Autowired
    ApDocRepository apDocRepository;

	@Autowired
	ApAttachmentsRepository apAttachmentsRepository;

    @Autowired
    SmUserInfoRepository smUserInfoRepository;

    @Value("${link.domain.url}")
	private String domainUrl;

	// 결재문서 유형
	public static final String APPROVAL_DOC = "01";   		// 일반결재문서
	public static final String DAILY_DOC = "02";   			// 작업일보
	public static final String PAYMENT_DOC = "03";  		// 기성관리
	public static final String MONTHLY_DOC = "04";  		// 월간보고
	public static final String DEPOSIT_DOC = "05";  		// 선급, 선급금
	public static final String QUALITY_ISP_DOC = "06";  	// 품질 검측요청
	public static final String QUALITY_APP_DOC = "07";  	// 품질 결재요청
	public static final String SAFETY_DOC = "08"; 			// 안전점검 승인요청
	public static final String SADTAG_DOC = "09"; 			// 안전지적서 승인요청
	public static final String WEEKLY_DOC = "11";			// 주간보고
	public static final String INSPECTION_DOC = "12";		// 감리일지
	public static final String SAFETY_REP_DOC = "13"; 		// 안전점검 점검결과 작성 요청
	public static final String SAFETY_DIARY_DOC = "14";		// 안전일지
	public static final String MAINMTRL_REQFRM_DOC = "15";	// 주요자재 검수요청서

    /**
     * 전자결재 최초 알림 생성
     * @param apDocId
     * @param apDocStats
     */
    public void insertInitAlarm(String apDocId, String apDocStats) {
        if(!"W".equals(apDocStats)) return;

        ApDoc apDoc = apDocRepository.findByApDocId(apDocId);
        String usrIp = getUserIp();
        String targetId = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.selectFirstApproval", apDocId);
        SearchPjtInfo pjtInfo = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.selectPjtInfo", new SearchPjtInfo(apDoc.getPjtNo(), apDoc.getCntrctNo()));
        String alarmTitle = "[승인요청] 전자결재 승인요청 건이 있습니다.";
        String url = createUrl("waiting", apDocId, pjtInfo);

        // 알림 생성 targetId, Contit, Context, url, usrId, usrIp
        AlarmInput alarm = createAlarm(targetId, alarmTitle, apDoc.getApDocTitle(), url, apDoc.getApUsrId(), usrIp);

		if(!"PGAIA".equals(platform.toUpperCase())) {
        	mybatisSessionIsc.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.insertAlarm", alarm);
		}
    }



    /**
	 * 전자결재 진행 중 알림 생성
	 * @param apDocId
	 */
	public void createNextAlarms(String apDocId) {

		List<AlarmInput> alarms = new ArrayList<ApprovalMybatisParam.AlarmInput>();
		ApDoc apDoc = apDocRepository.findByApDocId(apDocId);
		String usrIp = getUserIp();
		String usrId = UserAuth.get(true).getUsrId();

		SearchPjtInfo pjtInfo = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.selectPjtInfo", new ApprovalMybatisParam.SearchPjtInfo(apDoc.getPjtNo(), apDoc.getCntrctNo()));

		// 다음 결재자 알림
		String tarid = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.selectNextApproval", new ApprovalMybatisParam.NextApprovalInput(apDocId, usrId));
		String approvalUrl = createUrl("waiting", apDocId, pjtInfo);
		alarms.add(createAlarm(tarid, "[승인요청] 전자결재 승인요청 건이 있습니다.", apDoc.getApDocTitle(), approvalUrl, apDoc.getApUsrId(), usrIp));

		// 기안자 알림
		SmUserInfo userInfo = smUserInfoRepository.findByUsrIdAndDltYn(usrId, "N");
		String drafterUrl = createUrl("request", apDocId, pjtInfo);

		alarms.add(createAlarm(
			apDoc.getApUsrId(),
			String.format("[결재완료] %s님이 결재를 완료하셨습니다.", userInfo.getUsrNm()),
			apDoc.getApDocTitle(),
			drafterUrl,
			usrId,
			usrIp
		));

		if(!"PGAIA".equals(platform.toUpperCase())) {
			mybatisSessionIsc.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.insertNextAlarms", alarms);
		}


	}


	/**
	 * 전자결재 완료 시 알림 생성
	 * @param apDocId
	 * @param apDocStats
	 */
	public void createCompleteAlarm(String apDocId, String apDocStats) {

		if (!"C".equals(apDocStats) && !"R".equals(apDocStats)) return;

		ApDoc apDoc = apDocRepository.findByApDocId(apDocId);
		String usrIp = getUserIp();
		String usrId = UserAuth.get(true).getUsrId();

		String status = "C".equals(apDocStats) ? "완료" : "반려";
		String contit = String.format("[%s] 결재가 %s되었습니다.", status, status);

		SearchPjtInfo pjtInfo = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.selectPjtInfo", new ApprovalMybatisParam.SearchPjtInfo(apDoc.getPjtNo(), apDoc.getCntrctNo()));

		List<AlarmInput> alarms = new ArrayList<ApprovalMybatisParam.AlarmInput>();

		// 기안자 알림
		String drafterUrl = createUrl("request", apDocId, pjtInfo);
		alarms.add(createAlarm(apDoc.getApUsrId(), contit,	apDoc.getApDocTitle(), drafterUrl, usrId, usrIp));

		// 결재자 알림
		String type = "C".equals(apDocStats) ? "closed" : "rejected";
		String approvalUrl = createUrl(type, apDocId, pjtInfo);
		List<String> targetApprovals =  mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.selectCompleteTarget", apDocId);
		if(!targetApprovals.isEmpty()) {
			for (String approvalId : targetApprovals) {
				alarms.add(createAlarm(approvalId, contit,	apDoc.getApDocTitle(), approvalUrl, usrId, usrIp));
			}
		}

		if(!"PGAIA".equals(platform.toUpperCase())) {
			mybatisSessionIsc.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.insertCompleteAlarms", alarms);
		}
	}


	/**
	 * AlarmInput 생성
	 * @param tarid
	 * @param contit
	 * @param apDocTitle
	 * @param url
	 * @param usrId
	 * @param usrIp
	 * @return
	 */
	private AlarmInput createAlarm(String tarid, String contit, String apDocTitle, String url, String usrId, String usrIp) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
		AlarmInput alarm = new AlarmInput();
		alarm.setTarid(tarid);
		alarm.setContit(contit);
		alarm.setContext(apDocTitle);
		alarm.setUrl(url);
		alarm.setTranstm(LocalDateTime.now().format(formatter));
		alarm.setUseyn("Y");
		alarm.setUsrId(usrId);
		alarm.setUsrIp(usrIp);
		return alarm;
	}


    /**
     * 유저 ip 조회
     * @return
     */
    private String getUserIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
             log.error("getUserIp 오류", e);
        }
		return null;
    }


    /**
     * url 생성
     * @param type
     * @param apDocId
     * @param pjtInfo
     * @return
     */
    private String createUrl(String type, String apDocId, SearchPjtInfo pjtInfo) {
        return String.format("%s/eapproval/approval/detail?type=%s&apDocId=%s&pjtNo=%s&cntrctNo=%s", domainUrl, type, apDocId, pjtInfo.getPjtNo(), pjtInfo.getCntrctNo());
    }


	/**
	 * 첨부파일 다운로드
	 * @param fileNo
	 * @param apDocId
	 * @return
	 */
	public ResponseEntity<Resource> fileDownload(Integer fileNo, String apDocId) {
		MybatisInput input = MybatisInput.of().add("fileNo", fileNo)
				.add("apDocId", apDocId);

		// 카운트 수 업데이트
		mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.draft.updateFileViewCount", input);

		// 첨부파일 다운로드 정보 조회
		Map<String, Object> downLoadFile = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.draft.downLoadFile", input);

		if (downLoadFile != null) {
			Resource resource = fileService.getFile(downLoadFile.get("file_disk_path").toString(), downLoadFile.get("file_disk_nm").toString());
			if (resource == null || !resource.exists()) {
				throw new GaiaBizException(ErrorType.NOT_FOUND, "Not found file data.");
			}
			String fileNm = downLoadFile.get("file_nm").toString();
			String encodedDownloadFile = URLEncoder.encode(fileNm, StandardCharsets.UTF_8);          // 파일명이 한글이면, 인코딩을 해야 다운로드 가능.
			encodedDownloadFile = encodedDownloadFile.replaceAll("\\+", "%20");   // 문서 이름의 빈 칸이 +로 치환되는거 방지.

			return ResponseEntity.ok()
					.contentType(MediaType.APPLICATION_OCTET_STREAM)
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedDownloadFile + "\"")
					.body(resource);
		}

		throw new GaiaBizException(ErrorType.NO_DATA);
	}

	public void createApAttachmentsList(List<MultipartFile> files, String uploadPath, int apDocNo, String apDocId, String usrId) {
		ArrayList<ApAttachments> apAttachmentslist = new ArrayList<ApAttachments>();
		for (MultipartFile file : files) {
			FileService.FileMeta fileMeta = fileService.save(uploadPath, file);
			ApAttachments apAttachments = new ApAttachments();
			apAttachments.setApDocNo(apDocNo);
			apAttachments.setApDocId(apDocId);
			apAttachments.setFileNm(file.getOriginalFilename());
			apAttachments.setFileDiskNm(fileMeta.getFileName());
			apAttachments.setFileDiskPath(fileMeta.getDirPath());
			apAttachments.setFileSize(fileMeta.getSize());
			apAttachments.setDltYn("N");
			apAttachments.setFileHitNum(Integer.parseInt("0"));
			apAttachments.setRgstrId(usrId);
			apAttachments.setChgId(usrId);

			apAttachmentslist.add(apAttachments);
		}

		apAttachmentsRepository.saveAll(apAttachmentslist);
	}

	public List<ApAttachments> getApAttachmentsByApDocId(String apDocId) {
		return apAttachmentsRepository.findByApDocIdAndDltYn(apDocId, "N");
	}


		/**
	 * API 통신을 위한 첨부파일 변환
	 * @param attachments
	 * @return
	 */
	public List<Map<String, Object>> convertToFileInfo(List<ApAttachments> attachments) {
		if (attachments == null || attachments.isEmpty()) {
            log.info("##### No attachments to convert");
            return Collections.emptyList();
        }

		List<Map<String, Object>> fileInfoList = new ArrayList<>();

		for (ApAttachments attachment : attachments) {
			if (attachment == null || attachment.getFileNm() == null) {
				log.warn("##### Invalid attachment data: {}", attachment);
                continue;
			}

			// 파일 경로가 없는 경우 건너뛰기
            if (attachment.getFileDiskPath() == null || attachment.getFileDiskNm() == null) {
                log.warn("##### Physical file path not found for attachment: {}", attachment.getFileNm());
                continue;
            }

			Path filePath = Paths.get(attachment.getFileDiskPath(), attachment.getFileDiskNm());
            if (!Files.exists(filePath)) {
                log.warn("##### File not found: {}", filePath);
                continue;
            }

			try {
                // 파일 내용을 Base64로 인코딩
                byte[] fileContent = Files.readAllBytes(filePath);
                String base64Content = Base64.getEncoder().encodeToString(fileContent);

                Map<String, Object> fileInfo = new HashMap<>();
                fileInfo.put("fileNo", attachment.getFileNo());
                fileInfo.put("apDocNo", attachment.getApDocNo());
                fileInfo.put("apDocId", attachment.getApDocId());
                fileInfo.put("fileNm", attachment.getFileNm()); // 실제 파일명
                fileInfo.put("fileDiskNm", attachment.getFileDiskNm()); // 서버 파일명
                fileInfo.put("fileDiskPath", attachment.getFileDiskPath()); // 물리적 경로
                fileInfo.put("fileSize", attachment.getFileSize());
                fileInfo.put("fileHitNum", attachment.getFileHitNum());
                fileInfo.put("dltYn", attachment.getDltYn());
                fileInfo.put("rgstrId", attachment.getRgstrId());
                fileInfo.put("chgId", attachment.getChgId());
                fileInfo.put("fileContent", base64Content); // Base64로 인코딩된 파일 내용

                fileInfoList.add(fileInfo);

            } catch (IOException e) {
                log.error("##### Error reading file {}: {}", filePath, e.getMessage());
                continue;
            }
        }
        log.info("##### Successfully converted {} attachments to file info", fileInfoList.size());
        return fileInfoList;
	}


		/**
	 * API 통신 시 첨부파일 저장
	 * @param savedDoc
	 * @param files
	 */
	public void insertApDocFileInfoToApi(ApDoc savedDoc, List<Map<String, Object>> files) {
		List<ApAttachments> apAttachmentsList = new ArrayList<>();
		if (files != null && !files.isEmpty()) {
			 String fullPath = Path.of(uploadPath, getUploadPathByWorkType(FileUploadType.EAPPROVAL, savedDoc.getCntrctNo())).toString().replace("\\", "/");
			 for(Map<String, Object> fileInfo : files) {
				 String fileName = (String) fileInfo.get("fileNm"); // 실제 파일명

                // 파일 이름이 비어있거나 null인 경우 건너뛰기
                if (fileName == null || fileName.trim().isEmpty()) {
                    continue;
                }

				try {
					// Base64로 인코딩된 파일 내용을 디코딩
					String base64Content = (String) fileInfo.get("fileContent");
                    if (base64Content == null || base64Content.isEmpty()) {
                        continue;
                    }

					byte[] fileContent = Base64.getDecoder().decode(base64Content);

					// 파일을 디스크에 저장
                    String savedFileName = (String) fileInfo.get("fileDiskNm"); // 서버 저장 파일명(uuid)
                    Path savedFilePath = Paths.get(fullPath, savedFileName); // uploadpath + 업무 + 년월 + 서버 저장 파일명
					Path parent = savedFilePath.getParent();

					if(parent != null) {
						// 디렉토리가 없으면 생성
						Files.createDirectories(parent);

						// 파일 저장
						Files.write(savedFilePath, fileContent);
					}

					ApAttachments apAttachments = new ApAttachments();
					apAttachments.setFileNo((Integer) fileInfo.get("fileNo"));
					apAttachments.setApDocNo(savedDoc.getApDocNo());
					apAttachments.setApDocId(savedDoc.getApDocId());
					apAttachments.setFileNo((Integer) fileInfo.get("fileNo"));
					apAttachments.setFileNm(fileName);
					apAttachments.setFileDiskNm(savedFileName);
					apAttachments.setFileDiskPath(fullPath);
					apAttachments.setFileSize((Integer) fileInfo.get("fileSize"));
					apAttachments.setFileHitNum((Integer) fileInfo.get("fileHitNum"));
					apAttachments.setDltYn("N");
					apAttachments.setRgstrId(savedDoc.getRgstrId());
                    apAttachments.setChgId(savedDoc.getChgId());

					apAttachmentsList.add(apAttachments);
				} catch (IOException e) {
					log.error("첨부파일 저장 오류 {}: {} ", fileName, e.getMessage(), e);
				}
			 }

			 if (!apAttachmentsList.isEmpty()) {
				 apAttachmentsRepository.saveAll(apAttachmentsList);
			 }
		}
	}

	public void saveApAttachments(List<ApAttachments> apAttachmentsList) {
		apAttachmentsRepository.saveAll(apAttachmentsList);
	}
}

package kr.co.ideait.platform.gaiacairos.comp.defecttracking.helper;

import kr.co.ideait.platform.gaiacairos.core.base.AbstractBase;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DtAttachments;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.DtAttachmentsRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.util.FileService;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Slf4j
@Component
public class DefectTrackingHelper extends AbstractBase {

    @Autowired
    @Qualifier("sqlSessionTemplate")
    SqlSessionTemplate mybatisSession;

    @Autowired
    FileService fileService;

    @Autowired
    DtAttachmentsRepository dtAttachmentsRepository;


    /**
     * MultipartFile ->  DtAttachments 변환
     * @param files
     * @param uploadPath
     * @param existingFileNo
     * @return
     */
    public Integer convertToDtAttachments(List<MultipartFile> files, String uploadPath, Integer existingFileNo, String usrId) {
        if (files == null || files.isEmpty()) return existingFileNo;

        List<DtAttachments> dtAttachmentsList = new ArrayList<>();
        for (MultipartFile file : files) {
            FileService.FileMeta fileMeta = fileService.save(uploadPath, file);
            DtAttachments dtAttachments = new DtAttachments();
            dtAttachments.setFileNm(file.getOriginalFilename());
            dtAttachments.setFileDiskNm(fileMeta.getFileName());
            dtAttachments.setFileDiskPath(fileMeta.getDirPath());
            dtAttachments.setFileSize(fileMeta.getSize());
            dtAttachments.setDltYn("N");
            dtAttachments.setFileHitNum((short) 0);
            dtAttachments.setRgstrId(usrId);
            dtAttachments.setChgId(usrId);
            dtAttachmentsList.add(dtAttachments);
        }

        return insertAttachmentsList(dtAttachmentsList, existingFileNo);
    }


    /**
     * 첨부파일 추가
     * @param dtAttachmentsList
     * @param existingFileNo
     * @return
     */
    @Transactional
    public Integer insertAttachmentsList(List<DtAttachments> dtAttachmentsList, Integer existingFileNo) {
        Integer fileNo = existingFileNo != null ? existingFileNo : dtAttachmentsRepository.findMaxFileNo()+1;
        short sno = 1;

        for (DtAttachments dtAttachments : dtAttachmentsList) {
            // 등록된 파일 없을 때 -> 파일 새로 추가
            if (dtAttachments.getFileNo() == null && existingFileNo == null) {
                dtAttachments.setFileNo(fileNo);                      // 파일들에 동일한 fileNo 설정
                dtAttachments.setSno(sno);                            // 각 파일에 대해 순차적인 sno 설정
                sno++;                                                // 다음 파일의 sno 값 증가
            } else if(dtAttachments.getFileNo() == null && existingFileNo != null) {
                // 기존 파일에 새로운 파일 추가할 때
                dtAttachments.setFileNo(fileNo); // 파일들에 동일한 fileNo 설정
                dtAttachments.setSno((short) (dtAttachmentsRepository.findMaxSnoByFileNo(existingFileNo) + 1));
            } else { // 파일 수정
                dtAttachments.setSno((short) (dtAttachmentsRepository.findMaxSnoByFileNo(dtAttachments.getFileNo()) + 1));
            }
            dtAttachmentsRepository.save(dtAttachments); // 파일 저장
        }
        return fileNo;
    }


    /**
     * 결함 첨부파일 다운로드
     * @param fileNo
     * @param sno
     * @return
     */
    public ResponseEntity<Resource> fileDownload(Integer fileNo, Short sno) {
        // 1. 파일 메타정보 조회
        DtAttachments file = dtAttachmentsRepository.findByFileNoAndSno(fileNo, sno);
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
        MybatisInput input = MybatisInput.of().add("fileNo", fileNo).add("sno", sno);
        updateDtAttachmentsViewCount(input);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedDownloadFile + "\"")
                .body(resource);
    }


    /**
     * 첨부파일 다운로드 수 업데이트
     * @param input
     */
    @Transactional
    public void updateDtAttachmentsViewCount(MybatisInput input) {
        mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.defecttracking.defectTracking.updateDtAttachmentsViewCount", input);
    }


    /**
     * 첨부파일 조회
     * @param fileNo
     * @param sno
     * @return
     */
    public DtAttachments getDtAttachmentByFileNoAndSno(Integer fileNo, Short sno) {
        return dtAttachmentsRepository.findByFileNoAndSno(fileNo, sno);
    }


    /**
     * 결함 첨부파일 조회
     * @param fileNo
     * @return
     */
    public List<DtAttachments> getFileList(Integer fileNo) {
        return dtAttachmentsRepository.findByFileNoAndDltYn(fileNo, "N");
    }


    /**
     * 결함 첨부파일 삭제
     * @param delFileList
     * @param usrId
     */
    public void deleteAttachmentList(List<DtAttachments> delFileList, String usrId) {
        delFileList.forEach(file -> {
            dtAttachmentsRepository.updateDelete(dtAttachmentsRepository.findByFileNoAndSno(file.getFileNo(), file.getSno()), usrId);
        });
    }

    public void deleteAttachmentList(MybatisInput input) {
        mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.defecttracking.defectTracking.updateDtAttachmentsDltYn", input);
    }



    /**
     * 파일 정보 변환 헬퍼 메소드 (JSON 직렬화 가능) - 파일 내용 포함
     * @param attachments
     * @return
     */
    public List<Map<String, Object>> convertToFileInfo(List<DtAttachments> attachments) {

        if (attachments == null || attachments.isEmpty()) {
            log.info("##### No attachments to convert");
            return Collections.emptyList();
        }

        List<Map<String, Object>> fileInfoList = new ArrayList<>();

        for (DtAttachments attachment : attachments) {
            if (attachment == null || attachment.getFileNm() == null) {
                log.warn("##### Invalid attachment data: {}", attachment);
                continue;
            }

            // 파일 경로가 없는 경우 건너뛰기
            if (attachment.getFileDiskPath() == null || attachment.getFileDiskNm() == null) {
                log.warn("##### Physical file path not found for attachment: {}", attachment.getFileNm());
                continue;
            }

            // file disk info : /Users/soulers/IDIT/upload/cairos/deficiency/2025/06/uuid.jpg
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
                fileInfo.put("sno", attachment.getSno());
                fileInfo.put("fileNm", attachment.getFileNm()); // 실제 파일명
                fileInfo.put("fileDiskNm", attachment.getFileDiskNm()); // 서버 파일명
                fileInfo.put("fileDiskPath", attachment.getFileDiskPath()); // 물리적 경로
                fileInfo.put("fileSize", attachment.getFileSize());
                fileInfo.put("fileHitNum", attachment.getFileHitNum());
                fileInfo.put("rgstrId", attachment.getRgstrId());
                fileInfo.put("chgId", attachment.getChgId());
                fileInfo.put("dltYn", attachment.getDltYn());
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
     * API 통신 조건 MAP 생성
     * @param commonReqVo
     * @return
     */
    public Map<String, Object> createReqVoMap(CommonReqVo commonReqVo) {
        Map<String, Object> reqVoMap = new HashMap<>();
        reqVoMap.put("apiYn", commonReqVo.getApiYn());
        reqVoMap.put("pjtDiv", commonReqVo.getPjtDiv());
        return reqVoMap;
    }
}

package kr.co.ideait.platform.gaiacairos.comp.project.service;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.fasterxml.jackson.core.type.TypeReference;
import kr.co.ideait.iframework.BizException;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.*;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.type.FileUploadType;
import kr.co.ideait.platform.gaiacairos.core.util.FileService;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.CnAttachmentsRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.CnContractOrgRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.CnContractRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.overview.organization.OrganizationMybatisParam.OrganizationInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.overview.organization.OrganizationMybatisParam.OrganizationListInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.overview.organization.OrganizationMybatisParam.OrganizationListOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.overview.organization.OrganizationMybatisParam.OrganizationOutput;
import kr.co.ideait.platform.gaiacairos.core.constant.CommonCodeConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class OrganizationService extends AbstractGaiaCairosService {

    @Autowired
    CnContractOrgRepository cnContractOrgRepository;

    @Autowired
    CnContractRepository contractRepository;

    @Autowired
    CnAttachmentsRepository cnAttachmentsRepository;

    @Autowired
    FileService fileService;

    @Value("${api.requestKey}")
    private String apiKey;

    @Value("${link.domain.url}")
    private String domainUrl;

    @Value("${platform}")
    protected String platform;

    String cmnGrpCdMajorCnstty = CommonCodeConstants.MAJOR_CNSTTY_CODE_GROUP_CODE;
    String cmnGrpCdOfcl = CommonCodeConstants.OFCL_CODE_GROUP_CODE;

    // 조직도 목록 조회
    public List<OrganizationListOutput> getOrgList(OrganizationListInput organizationListInput) {

        String cntrctNo = organizationListInput.getCntrctNo();

        // 입력값 업데이트
        organizationListInput.setCntrctNo(cntrctNo);
        organizationListInput.setCmnGrpCdMajorCnstty(cmnGrpCdMajorCnstty);
        organizationListInput.setCmnGrpCdOfcl(cmnGrpCdOfcl);
        log.debug("CntrctNo={}", organizationListInput.getCntrctNo());

        // MyBatis를 통해 데이터 조회
        List<OrganizationListOutput> orgListOutputs = mybatisSession
                .selectList(
                        "kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.organization.getOrgList",
                        organizationListInput);

        return orgListOutputs;
    }

    // 조직원 조회
    public CnContractOrg getOrg(String cntrctNo, Integer cntrctOrgId) {
        return cnContractOrgRepository.findByCntrctNoAndCntrctOrgId(cntrctNo, cntrctOrgId).orElse(null);
    }

    // 조직원 조회
    public OrganizationOutput getOrgDetail(OrganizationInput organizationInput) {
        
        // 입력값 업데이트
        organizationInput.setCmnGrpCdMajorCnstty(cmnGrpCdMajorCnstty);
        organizationInput.setCmnGrpCdOfcl(cmnGrpCdOfcl);

        // MyBatis를 통해 데이터 조회
        OrganizationOutput orgOutput = mybatisSession
                .selectOne(
                        "kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.organization.getOrg",
                        organizationInput);
        return orgOutput;
    }

    // 조직도 생성
    @Transactional
    public CnContractOrg orgCreate(CnContractOrg cnContractOrg) {
        Integer cntrctOrgId = generateCntrctOrgId(cnContractOrg.getCntrctNo());
        cnContractOrg.setCntrctOrgId(cntrctOrgId);

        if (cnContractOrg.getDltYn() == null) {
            cnContractOrg.setDltYn("N");
        }
        return cnContractOrgRepository.save(cnContractOrg);

        // TODO API 송신
    }

    /**
     * cntrctNo
     * 
     * @return 생성된 cntrctNo
     */
    private Integer generateCntrctOrgId(String cntrctNo) {
        Integer maxCntrctOrgId = cnContractOrgRepository.findMaxCntrctOrgIdByCntrctNo(cntrctNo);
        return (maxCntrctOrgId == null ? 1 : maxCntrctOrgId + 1);
    }

    public CnContractOrg updateOrg(CnContractOrg cnContractOrg) {
        return cnContractOrgRepository.save(cnContractOrg);
    }

    /* 조직 삭제 */
    @Transactional
    public void orgDelete(List<CnContractOrg> organization) {
        organization.forEach(id -> {
            CnContractOrg cnContractOrg = cnContractOrgRepository
                    .findByCntrctNoAndCntrctOrgId(id.getCntrctNo(), id.getCntrctOrgId()).orElse(null);
            if (cnContractOrg != null) {
                cnContractOrgRepository.updateDelete(cnContractOrg);

                // TODO API 송신
            }
        });
    }

    public CnContract getByCntrctNo(String cntrctNo) {
        return contractRepository.findByCntrctNo(cntrctNo);
    }

    /**
     * 조직도 이미지 저장
     * 
     * @param cnAttachment
     */
    @Transactional
    public void saveOrgImg(CnAttachments cnAttachment, String usrId) {
        if(usrId != null) {
            cnAttachment.setRgstrId(usrId);
            cnAttachment.setChgId(usrId);
        }
        cnAttachmentsRepository.save(cnAttachment);
    }

    // 조직도 이미지 수정
    @Transactional
    public void updateOrgImg(CnAttachments cnAttachment, String usrId) {
        if(usrId != null) {
            cnAttachment.setRgstrId(usrId);
            cnAttachment.setChgId(usrId);
        }
        cnAttachmentsRepository.save(cnAttachment);
    }

    // 조직도 이미지 지움
    @Transactional
    public int deleteAttachment(Integer fileNo) {
        MybatisInput input = new MybatisInput().add("fileNo", fileNo).add("usrId", UserAuth.get(true).getUsrId());
        return mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.project.updateDeleteAllCnAttachments",input);
    }

    /**
     * 첨부 파일 목록 조회
     * 
     * @param fileNo
     * @return
     */
    public CnAttachments getFile(int fileNo) {
        return cnAttachmentsRepository.findOneByFileNoAndDltYn(fileNo, "N");
    }

    /**
     * 조직도 이미지 FileNo생성
     * 
     * @return
     */
    public Integer generateFileNo() {
        Integer maxFileNo = cnAttachmentsRepository.findMaxFileNo();
        return (maxFileNo == null ? 1 : maxFileNo + 1);
    }

    /**
     * 조직도 이미지 Sno생성
     * 
     * @return
     */
    public Integer generateSno(Integer fileNo) {
        Integer maxSno = cnAttachmentsRepository.findMaxSnoByFileNo(fileNo);
        return (maxSno == null ? 1 : maxSno + 1);
    }

    // ----------------------------------------API통신--------------------------------------------

    /**
     * MID - CAGA1001
     * API 수신 메서드 (조직도 정보 입력)
     * @param msgId
     * @param params
     * @return
     */
    @Transactional
    public Map<String, Object> insertOrganizationApi (String msgId, Map<String, Object> params) {

        Map<String, Object> result = new HashMap<>();

        if (MapUtils.isEmpty(params)) {
            throw new BizException("params is empty");
        }

        if ("CAGA1001".equals(msgId)) {
            log.info("CAGA1001 - API 연동 params : {}", params);
            // DO BUSINESS LOGIC
            CnContractOrg cnContractOrg = objectMapper.convertValue(params.get("cnContractOrg"), CnContractOrg.class);
            cnContractOrgRepository.save(cnContractOrg);
        }

        result.put("resultCode", "00");
        return result;
    }

    /**
     * MID - CAGA1002
     * API 수신 메서드 (조직도 정보 수정)
     * @param msgId
     * @param params
     * @return
     */
    @Transactional
    public Map<String, Object> updateOrganizationApi (String msgId, Map<String, Object> params) {

        Map<String, Object> result = new HashMap<>();

        if (MapUtils.isEmpty(params)) {
            throw new BizException("params is empty");
        }

        if ("CAGA1002".equals(msgId)) {
            log.info("CAGA1002 - API 연동 params : {}", params);

            // DO BUSINESS LOGIC
            CnContractOrg cnContractOrg = objectMapper.convertValue(params.get("cnContractOrg"), CnContractOrg.class);
            cnContractOrgRepository.save(cnContractOrg);

        }

        result.put("resultCode", "00");
        return result;
    }

    /**
     * MID - CAGA1003
     * API 수신 메서드 (조직도 정보 삭제)
     * @param msgId
     * @param params
     * @return
     */
    @Transactional
    public Map<String, Object> deleteOrganizationApi (String msgId, Map<String, Object> params) {

        Map<String, Object> result = new HashMap<>();

        if (MapUtils.isEmpty(params)) {
            throw new BizException("params is empty");
        }

        if ("CAGA1003".equals(msgId)) {
            log.info("CAGA1003 - API 연동 params : {}", params);
            // DO BUSINESS LOGIC
            List<CnContractOrg> cnContractOrgList = objectMapper.convertValue(params.get("organizationList"), new TypeReference<List<CnContractOrg>>() {});
            cnContractOrgRepository.saveAll(cnContractOrgList);
        }

        result.put("resultCode", "00");
        return result;
    }

    /**
     * MID - CAGA1004
     * API 수신 메서드 (조직도 이미지 입력)
     * @param msgId
     * @param params
     * @return
     */
    @Transactional
    public Map<String, Object> insertOrganizationImageApi (String msgId, Map<String, Object> params) {

        Map<String, Object> result = new HashMap<>();

        if (MapUtils.isEmpty(params)) {
            throw new BizException("params is empty");
        }

        if ("CAGA1004".equals(msgId)) {
            log.info("CAGA1004 - API 연동 params : {}", params);
            // DO BUSINESS LOGIC
            String cntrctNo = (String) params.get("cntrctNo");
            String usrId = (String) params.get("usrId");
            MultipartFile file = null;
            if(params.get("file") != null) {
                file = (MultipartFile) params.get("file");
            }

            CnContract cnContract = getByCntrctNo(cntrctNo);
            String orgchrtAtchFileNo = cnContract.getOrgchrtAtchFileNo();
            CnAttachments cnAttachment = new CnAttachments();

            // 이미지가 있을때
            if (orgchrtAtchFileNo != null && !orgchrtAtchFileNo.isEmpty()) {
                // 기존 파일 삭제
                MybatisInput input = new MybatisInput().add("fileNo", Integer.parseInt(orgchrtAtchFileNo)).add("usrId", usrId);
                mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.project.updateDeleteAllCnAttachments",input);

                if (file != null && !file.isEmpty()) {
                    FileService.FileMeta fileMeta = fileService.save(fullPath, file);
                    cnAttachment.setFileNo(Integer.parseInt(orgchrtAtchFileNo));
                    cnAttachment.setSno(generateSno(Integer.parseInt(orgchrtAtchFileNo)));
                    cnAttachment.setFileNm(file.getOriginalFilename());
                    cnAttachment.setFileDiskNm(fileMeta.getFileName());
                    cnAttachment.setFileDiskPath(fileMeta.getDirPath());
                    cnAttachment.setFileSize(fileMeta.getSize());
                    cnAttachment.setDltYn("N");
                    cnAttachment.setFileHitNum(0);
                }
                saveOrgImg(cnAttachment, usrId);

            } else {
                if (file != null && !file.isEmpty()) {
                    Integer fileNo = generateFileNo();

                    FileService.FileMeta fileMeta = fileService.save(fullPath, file);
                    cnAttachment.setFileNo(fileNo);
                    cnAttachment.setSno(1);
                    cnAttachment.setFileNm(file.getOriginalFilename());
                    cnAttachment.setFileDiskNm(fileMeta.getFileName());
                    cnAttachment.setFileDiskPath(fileMeta.getDirPath());
                    cnAttachment.setFileSize(fileMeta.getSize());
                    cnAttachment.setDltYn("N");
                    cnAttachment.setFileHitNum(0);

                    saveOrgImg(cnAttachment, usrId);
                    cnContract.setOrgchrtAtchFileNo(fileNo.toString());
                }
            }
        }

        result.put("resultCode", "00");
        return result;
    }


    /**
     * MID - CAGA1005
     * API 수신 메서드 (조직도 이미지 삭제)
     * @param msgId
     * @param params
     * @return
     */
    @Transactional
    public Map<String, Object> deleteOrganizationImageApi (String msgId, Map<String, Object> params) {

        Map<String, Object> result = new HashMap<>();

        if (MapUtils.isEmpty(params)) {
            throw new BizException("params is empty");
        }

        if ("CAGA1005".equals(msgId)) {
            log.info("CAGA1005 - API 연동 params : {}", params);
            // DO BUSINESS LOGIC
           String cntrctNo = (String) params.get("cntrctNo");
           String usrId = (String) params.get("usrId");

            int fileNo = 0;
            CnContract cnContract = getByCntrctNo(cntrctNo);
            if(cnContract.getOrgchrtAtchFileNo() != null && !cnContract.getOrgchrtAtchFileNo().isEmpty()) {
                fileNo = Integer.parseInt(cnContract.getOrgchrtAtchFileNo());
            }

            MybatisInput input = new MybatisInput().add("fileNo", fileNo).add("usrId", usrId);
            mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.project.updateDeleteAllCnAttachments",input);
        }

        result.put("resultCode", "00");
        return result;
    }

    /**
     * 파일 업로드 경로
     */
    String baseDirPath = FileUploadType.PROJECT.getDirPath(); // Enum으로 기본 디렉토리 경로 생성
    String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM")); // 현재 날짜를 기반으로 하위 경로 추가
    String fullPath = Path.of(baseDirPath, datePath).toString(); // 전체 경로 생성



}

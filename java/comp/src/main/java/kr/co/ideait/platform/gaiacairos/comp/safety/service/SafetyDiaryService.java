package kr.co.ideait.platform.gaiacairos.comp.safety.service;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.ideait.iframework.BizException;
import kr.co.ideait.platform.gaiacairos.comp.system.service.CommonCodeService;
import kr.co.ideait.platform.gaiacairos.comp.system.service.DocumentManageService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.constant.CommonCodeConstants;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.ApDoc;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwAttachments;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmComCode;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.document.DocumentForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.document.CbgnPropertyDto;
import kr.co.ideait.platform.gaiacairos.core.util.restclient.UbiReportClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class SafetyDiaryService extends AbstractGaiaCairosService {

    @Autowired
    private UbiReportClient ubiReportClient;

    @Autowired
    CommonCodeService commonCodeService;

    @Autowired
    DocumentManageService documentManageService;

    private static final String DEFAULT_MAPPER_PATH = "kr.co.ideait.platform.gaiacairos.mybatis.mappers.safety.safetydiary";

    /**
     * 안전일지 추가 - 개요 - 해당 날짜의 작업일보 ID 조회
     * @param param
     * @return
     */
    public Long checkDailyReportExists(Map<String, Object> param) {
        return mybatisSession.selectOne(DEFAULT_MAPPER_PATH + ".checkDailyReportExists", param);
    }

    /**
     * 안전일지 중복검사
     * @param param
     * @return
     */
    public boolean checkDuplicateSafeDiary(Map<String, Object> param) {
        return mybatisSession.selectOne(DEFAULT_MAPPER_PATH + ".checkDuplicateSafeDiary", param);
    }

    /**
     * 안전일지 보고일자기준 교육현황 조회
     * @param param
     * @return
     */
    public List<Map<String, Object>> getEducationStatus(Map<String, Object> param) {
        return mybatisSession.selectList(DEFAULT_MAPPER_PATH + ".getEducationStatus", param);
    }

    /**
     * 안전일지 보고일자기준 재해현황 조회
     * @param param
     * @return
     */
    public Map<String, Object> getDisasterStatus(Map<String, Object> param) {
        return mybatisSession.selectOne(DEFAULT_MAPPER_PATH + ".getDisasterStatus", param);
    }

    /**
     * 안전일지 보고일자 기준전 최신 누계 값 조회
     * @param param
     * @return
     */
    public Long getPrevCusum(Map<String, Object> param) {
        return mybatisSession.selectOne(DEFAULT_MAPPER_PATH + ".getPrevCusum", param);
    }

    /**
     * 안전일지 무재해현황 데이터 조회
     * @param param
     * @return
     */
    public Map<String, Object> getZeroAccidentCampaignData(Map<String, Object> param) {
        return mybatisSession.selectOne(DEFAULT_MAPPER_PATH + ".getZeroAccidentCampaignData", param);
    }

    /**
     * 안전일지 목록 및 검색
     * @param param
     * @return
     */
    public List<Map<String, Object>> getSafetyDiaryList(Map<String, Object> param) {
        return mybatisSession.selectList(DEFAULT_MAPPER_PATH + ".getSafetyDiaryList", param);
    }

    /**
     * 안전일지 추가 - 본문
     * @param safetyDiary
     * @return
     */
    public int addSafetyDiary(Map<String, Object> safetyDiary) {
       return mybatisSession.insert(DEFAULT_MAPPER_PATH + ".addSafetyDiary", safetyDiary);
    }

    /**
     * 안전일지 추가 - 작업현황
     * @param param
     */
    public void addSafetyDiaryWorkStatus(Map<String, Object> param) {
        mybatisSession.insert(DEFAULT_MAPPER_PATH + ".addSafetyDiaryWorkStatus", param);
    }

    /**
     * 안전일지 추가 - 순회 / 점검현황
     * @param param
     */
    public void addSafetyDiaryPatrolStatus(Map<String, Object> param) {
        mybatisSession.insert(DEFAULT_MAPPER_PATH + ".addSafetyDiaryPatrolStatus", param);
    }

    /**
     * 안전일지 복사 - 본문
     * @param safetyDiary
     * @return
     */
    public int copySafetyDiary(Map<String, Object> safetyDiary) {
        return mybatisSession.insert(DEFAULT_MAPPER_PATH + ".copySafetyDiary", safetyDiary);
    }

    /**
     * 안전일지 복사 - 작업현황
     * @param param
     */
    public void copySafetyDiaryWorkStatus(Map<String, Object> param) {
        mybatisSession.insert(DEFAULT_MAPPER_PATH + ".copySafetyDiaryWorkStatus", param);
    }

    /**
     * 안전일지 복사 - 순회 / 점검현황
     * @param param
     */
    public void copySafetyDiaryPatrolStatus(Map<String, Object> param) {
        mybatisSession.insert(DEFAULT_MAPPER_PATH + ".copySafetyDiaryPatrolStatus", param);
    }

    /**
     * 안전일지 상세조회 - 본문
     * @param param
     * @return
     */
    public Map<String, Object> getSafetyDiaryDetail(Map<String, Object> param) {
        return mybatisSession.selectOne(DEFAULT_MAPPER_PATH + ".getSafetyDiaryDetail", param);
    }

    /**
     * 안전일지 상세조회 - 작업현황
     * @param param
     */
    public List<Map<String, Object>> getSafetyDiaryWorkStatusList(Map<String, Object> param) {
        return mybatisSession.selectList(DEFAULT_MAPPER_PATH + ".getSafetyDiaryWorkStatusList", param);    }

    /**
     * 안전일지 상세조회 - 순회 / 점검현황
     * @param param
     */
    public List<Map<String, Object>> getSafetyDiaryPatrolStatusList(Map<String, Object> param) {
        return mybatisSession.selectList(DEFAULT_MAPPER_PATH + ".getSafetyDiaryPatrolStatusList", param);
    }

    /**
     * 안전일지 수정 - 본문
     * @param safetyDiary
     * @return
     */
    public int updateSafetyDiary(Map<String, Object> safetyDiary) {
        return mybatisSession.update(DEFAULT_MAPPER_PATH + ".updateSafetyDiary", safetyDiary);
    }

    /**
     * 안전일지 수정 - 작업현황
     * @param param
     */
    public void updateSafetyDiaryWorkStatus(Map<String, Object> param) {
        mybatisSession.update(DEFAULT_MAPPER_PATH + ".updateSafetyDiaryWorkStatus", param);
    }

    /**
     * 안전일지 추가 - 순회 / 점검현황
     * @param param
     */
    public void updateSafetyDiaryPatrolStatus(Map<String, Object> param) {
        mybatisSession.update(DEFAULT_MAPPER_PATH + ".updateSafetyDiaryPatrolStatus", param);
    }

    /**
     * 안전일지 삭제 - 본문
     * @param params
     * @return
     */
    public int deleteSafetyDiary(Map<String, Object> params) {
        return mybatisSession.update(DEFAULT_MAPPER_PATH + ".deleteSafetyDiary", params);
    }

    /**
     * 안전일지 삭제 - 작업현황
     * @param param
     */
    public void deleteSafetyDiaryWorkStatus(Map<String, Object> param) {
        mybatisSession.update(DEFAULT_MAPPER_PATH + ".deleteSafetyDiaryWorkStatus", param);
    }

    /**
     * 안전일지 삭제 - 순회 / 점검현황
     * @param param
     */
    public void deleteSafetyDiaryPatrolStatus(Map<String, Object> param) {
        mybatisSession.update(DEFAULT_MAPPER_PATH + ".deleteSafetyDiaryPatrolStatus", param);
    }


    /**
     * 안전일지 첨부파일 최대번호 조회
     * @return
     */
    public int getSafetyDiaryAttachmentMaxFileNo() {
        return mybatisSession.selectOne(DEFAULT_MAPPER_PATH + ".getSafetyDiaryAttachmentMaxFileNo");
    }

    /**
     * 안전일지 첨부파일 최대 순번 조회 by FileNo
     * @param param
     * @return
     */
    public int getSafetyDiaryAttachmentMaxSno(Map<String, Object> param) {
        return mybatisSession.selectOne(DEFAULT_MAPPER_PATH + ".getSafetyDiaryAttachmentMaxSno", param);
    }

    /**
     * 안전일지 첨부파일 목록 조회
     */
    public List<CwAttachments> getSafetyDiaryAttachments(Map<String, Object> param) {
        return mybatisSession.selectList(DEFAULT_MAPPER_PATH + ".getSafetyDiaryAttachments", param);
    }

    /**
     * 안전일지 첨부파일 단일 조회
     */
    public CwAttachments getSafetyDiaryAttachment(Integer fileNo, Integer sno) {
        Map<String, Object> params = new HashMap<>();
        params.put("fileNo", fileNo);
        params.put("sno", sno);
        return mybatisSession.selectOne(DEFAULT_MAPPER_PATH + ".getSafetyDiaryAttachment", params);
    }


    /**
     * 안전일지 첨부파일 추가
     */
    public void addSafetyDiaryAttachment(CwAttachments attachment) {
        mybatisSession.insert(DEFAULT_MAPPER_PATH + ".addSafetyDiaryAttachment", attachment);
    }

    /**
     * 안전일지 첨부파일 수정
     */
    public void updateSafetyDiaryAttachment(CwAttachments attachment) {
        mybatisSession.update(DEFAULT_MAPPER_PATH + ".updateSafetyDiaryAttachment", attachment);
    }

    /**
     * 안전일지 첨부파일 조회수 증가
     */
    public void incrementSafetyDiaryAttachmentHitNum(Integer fileNo, Integer sno, String usrId) {
        Map<String, Object> params = new HashMap<>();
        params.put("fileNo", fileNo);
        params.put("sno", sno);
        params.put("usrId", usrId);
        mybatisSession.update(DEFAULT_MAPPER_PATH + ".incrementSafetyDiaryAttachmentHitNum", params);
    }

    /**
     * 안전일지 첨부파일 논리 삭제
     */
    public void deleteSafetyDiaryAttachment(Map<String, Object> params) {
        mybatisSession.update(DEFAULT_MAPPER_PATH + ".deleteSafetyDiaryAttachment", params);
    }

    /**
     * 안전일지 첨부파일 전체 논리 삭제 (파일번호 기준)
     */
    public void deleteSafetyDiaryAttachmentsByFileNo(Map<String, Object> params) {
        mybatisSession.update(DEFAULT_MAPPER_PATH + ".deleteSafetyDiaryAttachmentsByFileNo", params);
    }

    /**
     * 안전일지에 첨부파일 번호 연결
     */
    public void updateSafetyDiaryAttachmentFileNo(Map<String, Object> params) {
        mybatisSession.update(DEFAULT_MAPPER_PATH + ".updateSafetyDiaryAttachmentFileNo", params);
    }


}

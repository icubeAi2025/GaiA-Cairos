package kr.co.ideait.platform.gaiacairos.comp.safety.service;

import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class DisasterDiaryService extends AbstractGaiaCairosService {

    private static final String DEFAULT_MAPPER_PATH = "kr.co.ideait.platform.gaiacairos.mybatis.mappers.safety.disasterdiary";

    /**
     * 재해일지 목록 조회
     * @param input
     * @return
     */
    public List<Map<String, Object>> getDisasterDiaryList(Map<String, Object> input){
        return mybatisSession.selectList(DEFAULT_MAPPER_PATH + ".selectListDisasterDiary", input);
    }

    /**
     * 재해일지 생성
     * @param disasterDiary
     * @return
     */
    public int addDisasterDiary(Map<String, Object> disasterDiary) {
        return mybatisSession.insert(DEFAULT_MAPPER_PATH + ".addDisasterDiary", disasterDiary);
    }

    /**
     * 재해일지_인원 생성
     * @param disasterDiaryUser
     * @return
     */
    public int addDisasterDiaryPersonnel(Map<String, Object> disasterDiaryUser) {
        return mybatisSession.insert(DEFAULT_MAPPER_PATH + ".addDisasterDiaryPersonnel", disasterDiaryUser);
    }

    /**
     * 재해일지 상세 조회
     * @param input
     * @return
     */
    public Map<String, Object> getDisasterDiary(Map<String, Object> input) {
        return mybatisSession.selectOne(DEFAULT_MAPPER_PATH + ".selectOneDisasterDiary", input);
    }

    /**
     * 재해일지 - 재해인원 조회
     * @param input
     * @return
     */
    public List<Map<String, Object>> getListDisasterDiaryPersonnel(Map<String, Object> input) {
        return mybatisSession.selectList(DEFAULT_MAPPER_PATH + ".selectListDisasterDiaryPersonnel", input);
    }

    /**
     * 재해일지 수정
     * @param disasterDiary
     * @return
     */
    public int updateDisasterDiary(Map<String, Object> disasterDiary) {
        return mybatisSession.update(DEFAULT_MAPPER_PATH + ".updateDisasterDiary", disasterDiary);
    }

    /**
     * 재해일지 수정 - 재해인원
     * @param disasterDiaryUser
     * @return
     */
    public int updateDisasterDiaryPersonnel(Map<String, Object> disasterDiaryUser) {
        return mybatisSession.update(DEFAULT_MAPPER_PATH + ".updateDisasterDiaryPersonnel", disasterDiaryUser);
    }

    /**
     * 재해일지 삭제 - 재해인원
     * @param input
     * @return
     */
    public int deleteDisasterDiaryPersonnel(Map<String, Object> input) {
        return mybatisSession.update(DEFAULT_MAPPER_PATH + ".deleteDisasterDiaryPersonnel", input);
    }

    /**
     * 재해인원 삭제 by DisasId
     * @param input
     * @return
     */
    public int deleteDisasterDiaryPersonnelByDisasId(Map<String, Object> input) {
        return mybatisSession.update(DEFAULT_MAPPER_PATH + ".deleteDisasterDiaryByDisasId", input);
    }

    /**
     * 재해일지 삭제
     * @param input
     * @return
     */
    public int deleteDisasterDiary(Map<String, Object> input) {
        return mybatisSession.update(DEFAULT_MAPPER_PATH + ".deleteDisasterDiary", input);
    }

    public LocalDateTime getRecentlyDisasterDateByCntrctNo(String cntrctNo) {
        return mybatisSession.selectOne(DEFAULT_MAPPER_PATH + ".selectRecentlyDisasterDateByCntrctNo", cntrctNo);
    }
}

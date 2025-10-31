package kr.co.ideait.platform.gaiacairos.comp.safety.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwAttachments;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.safety.EducationDiaryMybatisParam.EducationDiaryListInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.safety.EducationDiaryMybatisParam.EducationDiaryListOutput;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EducationDiaryService extends AbstractGaiaCairosService {
	
	/**
	 * 교육일지 목록조회
	 *
	 * @return List<Map<String, Object>> 
	 * @throws
	 */
	public List<EducationDiaryListOutput> selectEducationDiaryList(EducationDiaryListInput input) {	

		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.safety.educationdiary.selectEducationDiaryList", input);
	}
	
	/**
	 * 교육일지 상세조회
	 *
	 * @return List<Map<String, Object>>
	 * @throws
	 */
	public List<Map<String, Object>> selectEducationDiary(MybatisInput input) {	

		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.safety.educationdiary.selectEducationDiary", input);
	}
	
	/**
	 * 안전일지에서 상세조회 팝업 시 같은 날 같은 교육구분의 일지 목록조회
	 *
	 * @return List<Map<String, Object>>
	 * @throws
	 */
	public List<Map<String, Object>> selectEduTypeList(MybatisInput input) {	

		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.safety.educationdiary.selectEduTypeList", input);
	}
	
	/**
	 * 교육일지 등록
	 * 
	 * @return int
	 * @throws
	 */
	public int insertEducationDiaryInfo(MybatisInput input) {

		return mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.safety.educationdiary.insertEducationDiary", input);
	}
	
	/**
	 * 교육일지 - 교육 참석자 등록
	 * 
	 * @return int
	 * @throws
	 */
	public int insertEducationDiaryPersonnelInfo(List<Map<String, Object>> insertEducationDiaryPersonneList) {

		return mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.safety.educationdiary.insertEducationDiaryPersonne", insertEducationDiaryPersonneList);
	}
	
	/**
	 * 교육일지 수정
	 * 
	 * @return int
	 * @throws
	 */
	public int updapeEducationDiaryInfo(MybatisInput input) {

		return mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.safety.educationdiary.updateEducationDiary", input);
	}
	
	/**
	 * 교육일지 - 교육 참석자 수정
	 * 
	 * @return int
	 * @throws
	 */
	public int updateEducationDiaryPersonnelInfo(List<Map<String, Object>> updateEducationDiaryPersonneList) {

		return mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.safety.educationdiary.updateEducationDiaryPersonne", updateEducationDiaryPersonneList);
	}
	
	/**
	 * 교육일지 - 교육 참석자 삭제
	 * 
	 * @return int
	 * @throws
	 */
	public int deleteEducationDiaryPersonnelInfo(MybatisInput deleteEducationDiaryPersonneList) {

		return mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.safety.educationdiary.deleteEducationDiaryPersonne", deleteEducationDiaryPersonneList);
	}
	
	/**
	 * 교육일지 & 교육 참석자 일괄삭제
	 * 
	 * @return int
	 * @throws
	 */
	public int deleteEducationDiaryInfo(List<Map<String, Object>> deleduDiaryList) {

		return mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.safety.educationdiary.deleteEducationDiary", deleduDiaryList);
	}

	/**
	 * 교육일지에 첨부파일 번호 연결
	 */
	public int updateEducationDiaryAttachmentFileNo(MybatisInput input) {
		return mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.safety.educationdiary.updateEducationDiaryAttachmentFileNo", input);
	}
	
	/**
     * 교육일지 첨부파일 등록 할 파일번호 조회
     * @return
     */
    public int selectEducationDiaryAttachmentMaxFileNo() {
        return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.safety.educationdiary.selectEducationDiaryAttachmentMaxFileNo");
    }
    
    /**
     * 교육일지 첨부파일 등록 할 파일순번 조회
     * @return
     */
    public int selectEducationDiaryAttachmentMaxSno(MybatisInput input) {
        return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.safety.educationdiary.selectEducationDiaryAttachmentMaxSno", input);
    }
    
    /**
     * 교육일지 첨부파일 추가
     */
    public void insertEducationDiaryAttachmentFile(CwAttachments attachment) {
        mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.safety.educationdiary.insertEducationDiaryAttachmentFile", attachment);
    }
    
    /**
     * 교육일지 첨부파일 논리 삭제
     */
    public void deleteEducationDiaryAttachmentFile(MybatisInput input) {
        mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.safety.educationdiary.deleteEducationDiaryAttachmentFile", input);
    }
}

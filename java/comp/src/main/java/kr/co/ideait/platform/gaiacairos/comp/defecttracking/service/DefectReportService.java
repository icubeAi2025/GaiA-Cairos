package kr.co.ideait.platform.gaiacairos.comp.defecttracking.service;

import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DtAttachments;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.DtAttachmentsRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.report.defectreport.DefectReportMybatisParam.DefectReportListInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.report.defectreport.DefectReportMybatisParam.DefectReportListOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DefectReportService extends AbstractGaiaCairosService {

	@Autowired
    DtAttachmentsRepository dtAttachmentsRepository;


	/**
	 * 결함보고서 목록 조회
	 * @param defectReportListInput
	 * @return
	 */
	public List<DefectReportListOutput> selectDefectReport(DefectReportListInput defectReportListInput) {
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.defectreport.selectDefectReport", defectReportListInput);
	}


	/**
	 * 결함보고서 상세 조회
	 * @param input
	 * @return
	 */
	public MybatisOutput selectDfccyReportDetail(MybatisInput input) {
		MybatisOutput output = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.defectreport.selectDfccyReportDetail", input);

		if(output != null) {
			// 결함 첨부파일
			if(output.get("atch_file_no") != null) {
				Integer dfccyFileNo = (Integer)output.get("atch_file_no");
				List<DtAttachments> dfccyFiles = dtAttachmentsRepository.findByFileNoAndDltYn(dfccyFileNo, "N");
				output.put("dfccyFiles", dfccyFiles);
			}
			// 답변 첨부파일
			if(output.get("rply_atch_file_no") != null) {
				Integer rplyAtchFileNo = (Integer)output.get("rply_atch_file_no");
				List<DtAttachments> replyFiles = dtAttachmentsRepository.findByFileNoAndDltYn(rplyAtchFileNo, "N");
				output.put("replyFiles", replyFiles);
			}

		}

		return output;
	}

	/**
	 * 결함 단계 목록 조회
	 * @param cntrctNo
	 * @return
	 */
	public List<Map<String, Object>> selectPhaseCd(String cntrctNo) {
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.defectreport.selectPhaseCd", cntrctNo);
	}


	/**
	 * 작성자 목록 조회
	 * @param cntrctNo
	 * @return
	 */
	public List<Map<String, Object>> selectRgstrNm(String cntrctNo) {
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.defectreport.selectRgstrNm", cntrctNo);
	}



}

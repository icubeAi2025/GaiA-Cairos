package kr.co.ideait.platform.gaiacairos.comp.defecttracking;

import kr.co.ideait.platform.gaiacairos.comp.defecttracking.service.DefectReportService;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.report.defectreport.DefectReportMybatisParam.DefectReportListInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.report.defectreport.DefectReportMybatisParam.DefectReportListOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class DefectReportComponent {

    @Autowired
    DefectReportService defectReportService;

    /**
    * 결함보고서 목록 조회
    * @param defectReportListInput
    * @return
    */
    public List<DefectReportListOutput> selectDefectReport(DefectReportListInput defectReportListInput) {
        defectReportListInput.setUsrId(UserAuth.get(true).getUsrId());
        return defectReportService.selectDefectReport(defectReportListInput);
    }


    /**
    * 결함보고서 상세 조회
    * @param input
    * @return
    */
    public MybatisOutput selectDfccyReportDetail(MybatisInput input) {
        return defectReportService.selectDfccyReportDetail(input);
    }


    /**
    * 결함 단계 목록 조회
    * @param cntrctNo
    * @return
    */
    public List<Map<String, Object>> selectPhaseCd(String cntrctNo) {
        return defectReportService.selectPhaseCd(cntrctNo);
    }


    /**
    * 작성자 목록 조회
    * @param cntrctNo
    * @return
    */
    public List<Map<String, Object>> selectRgstrNm(String cntrctNo) {
        return defectReportService.selectRgstrNm(cntrctNo);
    }
}

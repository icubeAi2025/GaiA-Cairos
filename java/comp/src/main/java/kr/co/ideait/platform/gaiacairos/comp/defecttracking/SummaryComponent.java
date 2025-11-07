package kr.co.ideait.platform.gaiacairos.comp.defecttracking;

import kr.co.ideait.platform.gaiacairos.comp.defecttracking.service.SummaryService;
import kr.co.ideait.platform.gaiacairos.core.constant.CommonCodeConstants;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.report.summary.SummaryMybatisParam.SummaryListOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.report.summary.SummaryMybatisParam.SummaryRgstrListOutput;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SummaryComponent {

    @Autowired
    SummaryService summaryService;

    /**
     * 결함 요약 > 공종별 리스트 조회
     * @param input
     * @return
     */
    public List<SummaryListOutput> getSummaryList(MybatisInput input) {
        input.add("dfccyCd", CommonCodeConstants.WORKTYPE_CODE_GROUP_CODE);
        return summaryService.getSummaryList(input);
    }


    /**
     * 결함 요약 > 작성자별 리스트 조회
     * @param cntrctNo
     * @return
     */
    public List<SummaryRgstrListOutput> getRgstrList(String cntrctNo) {
        return summaryService.getRgstrList(cntrctNo);
    }
}

package kr.co.ideait.platform.gaiacairos.comp.defecttracking.service;

import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.report.summary.SummaryMybatisParam.SummaryListOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.report.summary.SummaryMybatisParam.SummaryRgstrListOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class SummaryService extends AbstractGaiaCairosService {

    /**
     * 결함 요약 > 공종별 리스트 조회
     * @param input
     * @return
     */
    public List<SummaryListOutput> getSummaryList(MybatisInput input) {
        return  mybatisSession.selectList("kr.co.ideait.platform.mybatis.gaiacairos.mappers.defecttracking.summary.getSummaryList", input);
    }

    /**
     * 결함 요약 > 작성자별 리스트 조회
     * @param cntrctNo
     * @return
     */
    public List<SummaryRgstrListOutput> getRgstrList(String cntrctNo) {
        return mybatisSession.selectList("kr.co.ideait.platform.mybatis.gaiacairos.mappers.defecttracking.summary.getSummaryRgstrList", cntrctNo);
    }
}

package kr.co.ideait.platform.gaiacairos.comp.design.service;

import java.util.List;

import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import org.springframework.stereotype.Service;

import kr.co.ideait.platform.gaiacairos.core.persistence.DaoUtils;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.ReviewsummaryMybatisParam.ReviewsummaryListOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.ReviewsummaryMybatisParam.ReviewsummaryRgstrListOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ReviewsummaryService extends AbstractGaiaCairosService {

    /*
     * 검토 요약 리스트 조회
     */
    public List<ReviewsummaryListOutput> getSummaryList(MybatisInput input) {

        List<ReviewsummaryListOutput> output = mybatisSession
                .selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.reviewsummary.getReviewsummaryList", input);

        return output;
    }

    /**
     * 검토자 조회
     */
    public List<ReviewsummaryRgstrListOutput> getRgstrList(String cntrctNo) {

        List<ReviewsummaryRgstrListOutput> output = mybatisSession
                .selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.reviewsummary.getReviewsummaryRgstrList", cntrctNo);

        return output;
    }
}

package kr.co.ideait.platform.gaiacairos.comp.design.service;

import java.util.List;

import kr.co.ideait.platform.gaiacairos.comp.system.service.CommonCodeService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.ideait.platform.gaiacairos.core.persistence.DaoUtils;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DmAttachments;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmComCode;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.DmAttachmentsRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.DmBackcheckRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.DmDwgRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.DmEvaluationRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.SmComCodeRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.report.reviewcommentreport.ReviewCommentReportMybatisParam.ApprerOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.report.reviewcommentreport.ReviewCommentReportMybatisParam.BackchkOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.report.reviewcommentreport.ReviewCommentReportMybatisParam.ReviewReportDetailOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.report.reviewcommentreport.ReviewCommentReportMybatisParam.ReviewReportOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.report.reviewcommentreport.ReviewCommentReportMybatisParam.ReviewReportRgstrListOutput;
import kr.co.ideait.platform.gaiacairos.core.constant.CommonCodeConstants;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ReviewCommentReportService extends AbstractGaiaCairosService {

    @Autowired
    DmAttachmentsRepository dmAttachmentsRepository;

    @Autowired
    DmDwgRepository dmDwgRepository;

    @Autowired
    CommonCodeService commonCodeService;

    public List<ReviewReportOutput> getReviewReportList(MybatisInput input) {

        input.add("rplyStatus", CommonCodeConstants.DSGN_REPLY_CODE_GROUP_CODE)
                .add("apprerStatus", CommonCodeConstants.DSGN_APPRER_CODE_GROUP_CODE)
                .add("backchkStatus", CommonCodeConstants.DSGN_BACKCHK_CODE_GROUP_CODE)
                .add("dsgnCd", CommonCodeConstants.WORKTYPE_CODE_GROUP_CODE);

        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.reviewcommentreport.getReviewReport", input);
    }

    public ReviewReportDetailOutput getReviewReportDetail(MybatisInput input) {

        input.add("rplyStatus", CommonCodeConstants.DSGN_REPLY_CODE_GROUP_CODE)
                .add("apprerStatus", CommonCodeConstants.DSGN_APPRER_CODE_GROUP_CODE)
                .add("backchkStatus", CommonCodeConstants.DSGN_BACKCHK_CODE_GROUP_CODE)
                .add("dsgnCd", CommonCodeConstants.WORKTYPE_CODE_GROUP_CODE);

        ReviewReportDetailOutput output = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.reviewcommentreport.getReviewReportDetail", input);

        // 검토 첨부파일 조회 및 매핑
        if (output.getAtchFileNo() != null && !output.getAtchFileNo().isEmpty()) {
            List<DmAttachments> files = dmAttachmentsRepository.findByFileNoAndDltYn(output.getAtchFileNo(), "N");
            output.setFiles(files);
        }

        // 검토도서 조회 및 매핑
        if (output.getRvwDwgNo() != null && !output.getRvwDwgNo().isEmpty()) {
            DmAttachments rvwDwgFiles = dmAttachmentsRepository.findByFileNoAndSnoAndDltYn(
                    dmDwgRepository.findByDwgNoAndDltYn(output.getRvwDwgNo(), "N").getAtchFileNo(),
                    dmDwgRepository.findByDwgNoAndDltYn(output.getRvwDwgNo(), "N").getSno(), "N");
            output.setRvwDwgFile(rvwDwgFiles);
        }

        // 변경요청도서 조회 및 매핑
        if (output.getChgDwgNo() != null && !output.getChgDwgNo().isEmpty()) {
            DmAttachments chgDwgFiles = dmAttachmentsRepository.findByFileNoAndSnoAndDltYn(
                    dmDwgRepository.findByDwgNoAndDltYn(output.getChgDwgNo(), "N").getAtchFileNo(),
                    dmDwgRepository.findByDwgNoAndDltYn(output.getChgDwgNo(), "N").getSno(), "N");
            output.setChgDwgFile(chgDwgFiles);
        }

        // 답변 첨부파일 조회 및 매핑
        if (output.getRplyAtchFileNo() != null && !output.getRplyAtchFileNo().isEmpty()) {
            List<DmAttachments> files = dmAttachmentsRepository.findByFileNoAndDltYn(output.getRplyAtchFileNo(), "N");
            output.setReplyFiles(files);
        }

        // 답변 도서 조회 및 매핑
        if (output.getRplyDwgNo() != null && !output.getRplyDwgNo().isEmpty()) {
            DmAttachments rplyDwgFiles = dmAttachmentsRepository.findByFileNoAndSnoAndDltYn(
                    output.getRplyDwgAtchNo(), output.getRplyDwgSno(), "N");
            output.setRplyDwgFile(rplyDwgFiles);
        }

        // 평가 조회
        List<ApprerOutput> dmEvaluation = mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.reviewcommentreport.getReviewReportApprerDetail", input);
        for (ApprerOutput apprer : dmEvaluation) {
            if (apprer.getApprerAtchFileNo() != null && !apprer.getApprerAtchFileNo().isEmpty()) {
                apprer.setApprerFiles(dmAttachmentsRepository.findByFileNoAndDltYn(apprer.getApprerAtchFileNo(), "N"));
            }
        }
        output.setApprerList(dmEvaluation);


        // 백체크 조회
        List<BackchkOutput> dmBackcheck = mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.reviewcommentreport.getReviewReportBackchkDetail", input);
        for (BackchkOutput backchk : dmBackcheck) {
            if (backchk.getBackchkAtchFileNo() != null && !backchk.getBackchkAtchFileNo().isEmpty()) {
                backchk.setBackchkFiles(dmAttachmentsRepository.findByFileNoAndDltYn(backchk.getBackchkAtchFileNo(), "N"));
            }
        }
        output.setBackchkList(dmBackcheck);

        return output;
    }

    /**
     * 검토자 조회
     */
    public List<ReviewReportRgstrListOutput> getRgstrList(String cntrctNo) {

        List<ReviewReportRgstrListOutput> output = mybatisSession
                .selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.reviewcommentreport.getReviewReportRgstrList", cntrctNo);

        return output;
    }

    /**
     * 검토 분류 조회
     */
    public List<SmComCode> getDsgnCdList() {
        return commonCodeService.getCommonCodeListByGroupCode(CommonCodeConstants.WORKTYPE_CODE_GROUP_CODE);
    }
}

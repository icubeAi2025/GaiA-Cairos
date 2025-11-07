package kr.co.ideait.platform.gaiacairos.comp.defecttracking.service;

import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DtAttachments;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.DtAttachmentsRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.tool.defecttracking.DefectTrackingMybatisParam.DefectTrackingListOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.tool.defecttracking.DefectTrackingMybatisParam.DtConfirmOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class TerminationService extends AbstractGaiaCairosService {

    @Autowired
    DefectTrackingService defectTrackingService;

    @Autowired
    DtAttachmentsRepository dtAttachmentsRepository;


    /**
     * 종결관리 - 결함 목록 조회
     * @param input
     * @return
     */
    public List<DefectTrackingListOutput> getDfccyList(MybatisInput input) {
        List<DefectTrackingListOutput> output = mybatisSession.selectList("kr.co.ideait.platform.mybatis.gaiacairos.mappers.defecttracking.termination.selectDfccyEndList", input);

		for (DefectTrackingListOutput item : output) {
            // 결함 첨부파일 조회 및 매핑
            if (item.getAtchFileNo() != null) {
                List<DtAttachments> files = dtAttachmentsRepository.findByFileNoAndDltYn(item.getAtchFileNo(), "N");
                item.setFiles(files);
            }

            // 답변 첨부파일 조회 및 매핑
            if (item.getRplyAtchNo() != null) {
                List<DtAttachments> files = dtAttachmentsRepository.findByFileNoAndDltYn(item.getRplyAtchNo(), "N");
                item.setReplyFiles(files);
            }

			List<DtConfirmOutput> confirmList = defectTrackingService.getDeficiencyConfirmList(item.getDfccyNo());
			item.setDtConfirm(confirmList);
        }
        return output;
    }


    /**
     * 종결관리 - 결함 수 조회
     * @param input
     * @return
     */
    public Long getDfccyListCount(MybatisInput input) {
        return mybatisSession.selectOne("kr.co.ideait.platform.mybatis.gaiacairos.mappers.defecttracking.termination.getDfccyEndListCount", input);
    }
}

package kr.co.ideait.platform.gaiacairos.comp.defecttracking.service;

import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DtDeficiencyReply;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.DtDeficiencyReplyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class ResponsesService extends AbstractGaiaCairosService {

    @Autowired
    DtDeficiencyReplyRepository dtDeficiencyReplyRepository;

    /**
     * 답변 관리 - 답변 조회
     * @param replySeq
     * @param dfccyNo
     * @return
     */
    public DtDeficiencyReply getResponses(Integer replySeq, String dfccyNo) {
        return dtDeficiencyReplyRepository.findByReplySeqAndDfccyNoAndDltYn(replySeq, dfccyNo, "N").orElse(null);
    }


    /**
     * 답변 관리 - 답변 추가
     * @param response
     * @return
     */
    public DtDeficiencyReply saveResponses(DtDeficiencyReply response) {
        return dtDeficiencyReplyRepository.save(response);
    }


    /**
     * 답변 관리 - 답변 조회
     * @param replySeq
     * @param dfccyNo
     * @return
     */
    public DtDeficiencyReply getDeficiencyReply(Integer replySeq, String dfccyNo) {
        return dtDeficiencyReplyRepository.findByReplySeqAndDfccyNoAndDltYn(replySeq, dfccyNo,"N").orElse(null);
    }

    public List<DtDeficiencyReply> getDeficiencyReplyList(String dfccyNo, String cntrctNo) {
        return dtDeficiencyReplyRepository.findByDfccyNoAndCntrctNoAndDltYn(dfccyNo, cntrctNo,"N");
    }


    /**
     * 답변 관리 - 답변 삭제
     * @param responses
     */
    public void deleteDeficiencyReply(DtDeficiencyReply responses) {
        dtDeficiencyReplyRepository.updateDelete(responses);
    }
}
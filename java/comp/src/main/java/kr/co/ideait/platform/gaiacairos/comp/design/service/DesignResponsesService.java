package kr.co.ideait.platform.gaiacairos.comp.design.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import kr.co.ideait.platform.gaiacairos.comp.design.helper.DesignHelper;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.responses.DesignResponsesForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DmAttachments;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DmDwg;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DmResponse;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.DmAttachmentsRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.DmDwgRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.DmResponseRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.responses.DesignResponsesMybatisParam.DesignResponsesInput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class DesignResponsesService extends AbstractGaiaCairosService {

    @Autowired
    DmResponseRepository dmResponseRepository;

    // 답변 - 답변 조회
    public DmResponse getDesignResponses(DesignResponsesInput responsesInput) {

        return dmResponseRepository
                .findByResSeqAndDsgnNoAndDltYn(responsesInput.getResSeq(), responsesInput.getDsgnNo(), "N")
                .orElse(null);
    }

    public DmResponse getDesignResponse(String resSeq, String dsgnNo){
        DmResponse response = new DmResponse();
        response.setResSeq(resSeq);
        response.setDsgnNo(dsgnNo);
        return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.response.selectDesignResponseByDsgnNoAndResSeq",response);
    }

    // 답변 관리 - 답변 추가 수정
    public DmResponse saveResponses(DmResponse response, String saveType, String userId) {
        response.setDltYn("N");
        response.setChgId(userId);
        if ("create".equals(saveType) && StringUtils.isEmpty(response.getResSeq())) {
            response.setRgstrId(userId);
            response.setResSeq(UUID.randomUUID().toString());
            mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.response.insertDesignResponse", response);
        }
        else{
            mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.response.updateDesignResponse",response);
        }
        return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.response.selectDesignResponseByDsgnNoAndResSeq",response);

    }

    // 답변 관리 - 답변 삭제
    public void deleteResponses(List<DmResponse> responses, String userId) {

        responses.forEach(response -> {
            if ( StringUtils.isEmpty(response.getResSeq()) ) {
                return;
            }

            DmResponse dmResponse = dmResponseRepository.findByResSeqAndDsgnNo(response.getResSeq(), response.getDsgnNo()).orElse(null);

            if (dmResponse == null) {
                return;
            }

            if (userId == null) {
                dmResponseRepository.updateDelete(dmResponse);
            } else {
                dmResponseRepository.updateDelete(dmResponse, userId);
            }
        });
    }
}

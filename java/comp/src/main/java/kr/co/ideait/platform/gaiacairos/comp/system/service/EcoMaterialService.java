package kr.co.ideait.platform.gaiacairos.comp.system.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import org.apache.tika.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmEcoMaterial;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.SmEcoMaterialRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.ecomaterial.EcoMaterialMybatisParam.EcoMaterialListInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.ecomaterial.EcoMaterialMybatisParam.EcoMaterialListOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.ecomaterial.EcoMaterialForm;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EcoMaterialService extends AbstractGaiaCairosService {

    @Autowired
    SmEcoMaterialRepository smEcoMaterialRepository;

    // 친환경 자재 목록 조회
    public List<EcoMaterialListOutput> getEcoMaterialList(EcoMaterialListInput ecomaterialListInput) {

        List<EcoMaterialListOutput> ecoListOutputs = mybatisSession
                .selectList(
                        "kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.ecomaterial.getEcoMaterialList",
                        ecomaterialListInput);

        return ecoListOutputs;
    }

    // 자재 목록 조회
    public List<EcoMaterialListOutput> getMaterialList(EcoMaterialListInput ecomaterialListInput) {

        List<EcoMaterialListOutput> ecoListOutputs = mybatisSession
                .selectList(
                        "kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.ecomaterial.getMaterialList",
                        ecomaterialListInput);

        return ecoListOutputs;
    }

    // 친환경 자재 조회
    public SmEcoMaterial getEcoMaterial(String ecoId) {
        return smEcoMaterialRepository.findById(ecoId).orElse(null);
    }

    // 친환경 자재 생성
    @Transactional
    public EcoMaterialForm.CreateEcoMaterial ecoMaterialCreate(EcoMaterialForm.CreateEcoMaterial ecoMaterial) {
        return this.ecoMaterialCreate(ecoMaterial, null);
    }
    @Transactional
    public EcoMaterialForm.CreateEcoMaterial ecoMaterialCreate(EcoMaterialForm.CreateEcoMaterial ecoMaterial, String userId) {
        for (EcoMaterialForm.MaterialDto mat : ecoMaterial.getMaterialList()) {
            SmEcoMaterial sm = new SmEcoMaterial();
            sm.setEcoId(mat.getEcoId());
            sm.setCntrctNo(ecoMaterial.getCntrctNo());
            sm.setEcoTpCd(ecoMaterial.getEcoTpCd());
            sm.setPreCert(ecoMaterial.getPreCert());
            sm.setFinalCert(ecoMaterial.getFinalCert());
            sm.setMakrNm(ecoMaterial.getMakrNm());
            sm.setCertRsn(ecoMaterial.getCertRsn());
            sm.setRmrk(ecoMaterial.getRmrk());
            sm.setDltYn(ecoMaterial.getDltYn() == null ? "N" : ecoMaterial.getDltYn());

            sm.setGnrlexpnsCd(mat.getGnrlexpnsCd());
            sm.setRsceNm(mat.getRsceNm());
            sm.setSpecNm(mat.getSpecNm());
            sm.setUnit(mat.getUnit());

            if (StringUtils.isEmpty(userId)) {
                sm.setEcoId(UUID.randomUUID().toString());
                sm.setRgstrId(UserAuth.get(true).getUsrId());
                sm.setChgId(UserAuth.get(true).getUsrId());
            } else {
                sm.setRgstrId(userId);
                sm.setChgId(userId);
            }

            smEcoMaterialRepository.save(sm);

            mat.setEcoId(sm.getEcoId());
        }

        return ecoMaterial;
    }

    // 친환경 자재 수정
    @Transactional
    public EcoMaterialForm.UpdateEcoMaterial ecoMaterialUpdate(EcoMaterialForm.UpdateEcoMaterial ecoMaterial) {
        return this.ecoMaterialUpdate(ecoMaterial, null);
    }
    @Transactional
    public EcoMaterialForm.UpdateEcoMaterial ecoMaterialUpdate(EcoMaterialForm.UpdateEcoMaterial form, String userId) {
        if (form.getEcoIdList() == null || form.getEcoIdList().isEmpty()) {
            throw new IllegalArgumentException("ecoId 목록이 비어있습니다.");
        }

        for (String id : form.getEcoIdList()) {
            SmEcoMaterial sm = smEcoMaterialRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("ecoId " + id + "에 해당하는 자재를 찾을 수 없습니다."));

            sm.setEcoTpCd(form.getEcoTpCd());
            sm.setPreCert(form.getPreCert());
            sm.setFinalCert(form.getFinalCert());
            sm.setMakrNm(form.getMakrNm());
            sm.setCertRsn(form.getCertRsn());
            sm.setRmrk(form.getRmrk());

            if (StringUtils.isEmpty(userId)) {
                sm.setRgstrId(UserAuth.get(true).getUsrId());
                sm.setChgId(UserAuth.get(true).getUsrId());
            } else {
                sm.setRgstrId(userId);
                sm.setChgId(userId);
            }

            smEcoMaterialRepository.save(sm);
        }

        return form;
    }

    // 친환경 자재 삭제
    @Transactional
    public Map ecoMaterialDelete(List<SmEcoMaterial> ecoMaterialList) {
        return ecoMaterialDelete(ecoMaterialList, null);
    }
    @Transactional
    public Map ecoMaterialDelete(List<SmEcoMaterial> ecoMaterialList, String userId) {
        Map<String, Object> result = Maps.newHashMap();
        result.put("resultCode", "00");

        try {
            ecoMaterialList.forEach(id -> {
                SmEcoMaterial smEcoMaterial = smEcoMaterialRepository.findById(id.getEcoId()).orElse(null);
                if (smEcoMaterial != null) {
                    if (StringUtils.isEmpty(userId)) {
                        smEcoMaterialRepository.updateDelete(smEcoMaterial);
                    } else {
                        smEcoMaterialRepository.updateDelete(smEcoMaterial, userId);
                    }
                }
            });
        } catch (GaiaBizException e) {
            result.put("resultCode", "01");
            result.put("resultMsg", e.getMessage());
        }

        return result;
    }
}

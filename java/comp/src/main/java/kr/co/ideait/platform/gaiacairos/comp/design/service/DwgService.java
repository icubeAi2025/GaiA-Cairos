package kr.co.ideait.platform.gaiacairos.comp.design.service;

import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DmAttachments;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DmDwg;
import kr.co.ideait.platform.gaiacairos.core.util.FileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class DwgService extends AbstractGaiaCairosService {
    public DmDwg createdmDwg(String dwgCd, String dwgDscrpt, DmAttachments dmAttachments, String userId){
        DmDwg dmDwg = new DmDwg();
        dmDwg.setDwgNo(UUID.randomUUID().toString());
        dmDwg.setDwgCd(dwgCd);
        dmDwg.setDwgDscrpt(dwgDscrpt);

        dmDwg.setAtchFileNo(dmAttachments.getFileNo());
        dmDwg.setFileKey(dmAttachments.getFileKey());
        dmDwg.setSno(dmAttachments.getSno());

        dmDwg.setRgstrId(userId);
        dmDwg.setChgId(userId);

        mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.dwg.insertDwg",dmDwg);
        return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.dwg.selectDwg",dmDwg.getDwgNo());
    }

    public DmDwg insertDmDwg(DmDwg dmDwg,String rgstrId){
        dmDwg.setRgstrId(rgstrId);
        if(StringUtils.isEmpty(dmDwg.getDwgNo())){
            dmDwg.setDwgNo(UUID.randomUUID().toString());
        }

        mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.dwg.insertDwg",dmDwg);
        return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.dwg.selectDwg",dmDwg.getDwgNo());
    }

    public DmDwg updateDmDwg(DmDwg dmDwg, String chgId){
        dmDwg.setChgId(chgId);
        mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.dwg.updateDwg",dmDwg);
        return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.dwg.selectDwg",dmDwg.getDwgNo());
    }
    public DmDwg deleteDmDwg(String dwgNo, String dltId){
        DmDwg oldDmDwg = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.dwg.selectDwg",dwgNo);
        if(oldDmDwg!=null){
            oldDmDwg.setDltId(dltId);
            if(mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.dwg.deleteDwg",oldDmDwg) != 0){
                return oldDmDwg;
            }
        }
        return null;
    }

    public DmDwg selectDmDwg(String dwgNo){
        return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.dwg.selectDwg",dwgNo);
    }
}

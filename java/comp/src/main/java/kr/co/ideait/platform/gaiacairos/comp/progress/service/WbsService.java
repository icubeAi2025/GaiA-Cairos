package kr.co.ideait.platform.gaiacairos.comp.progress.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kr.co.ideait.iframework.EtcUtil;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.PrRevision;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.PrWbs;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.PrWbsRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.progress.wbs.wbs.WbsMybatisParam.WbsListInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.progress.wbs.wbs.WbsMybatisParam.WbsOutput;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WbsService extends AbstractGaiaCairosService {

    @Autowired
    PrWbsRepository prWbsRepository;

    private static final String DEFAULT_MAPPER_PATH = "kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.wbs";

    // WBS 목록 조회
    public List<WbsOutput> getWbsList(WbsListInput wbsListInput) {


        List<WbsOutput> wbsList = mybatisSession.selectList(
                DEFAULT_MAPPER_PATH + ".getWbsList", wbsListInput);

        return wbsList;
    }

    // WBS 생성
    @Transactional
    public void createWbs(PrRevision prRevision, ArrayList<HashMap<String, Object>> wbsList) {

        LocalDateTime now = LocalDateTime.now();
        ArrayList<PrWbs> prWbsList = new ArrayList<>();
        if (wbsList != null) {
            // ROOT level 의 wbs
            HashMap<String, Object> rootObjectIdObj = wbsList.getFirst();
            Object rootObjectId = (rootObjectIdObj != null) ? rootObjectIdObj.get("parentObjectId") : null;

            if (rootObjectId != null) {
                String parentObjectId = rootObjectIdObj.toString().trim();

                PrWbs rootWbs = new PrWbs();
                rootWbs.setCntrctChgId(prRevision.getCntrctChgId());
                rootWbs.setRevisionId(prRevision.getRevisionId());
                rootWbs.setWbsCd(parentObjectId);
                rootWbs.setWbsPath(prRevision.getP6ProjectId());
                rootWbs.setWbsNm(prRevision.getP6ProjectNm());
                rootWbs.setWbsLevel(1);
                if (!parentObjectId.isEmpty() && parentObjectId.matches("\\d+")) {
                    rootWbs.setP6WbsObjId(Integer.parseInt(parentObjectId));
                }
                rootWbs.setDltYn("N");
                rootWbs.setRgstrId(prRevision.getRgstrId());
                prWbsList.add(rootWbs);

                if (!wbsList.isEmpty()) {
                    for (HashMap<String, Object> wbs : wbsList) {

                        if (wbs == null) continue;

                        PrWbs prWbs = new PrWbs();
                        prWbs.setCntrctChgId(prRevision.getCntrctChgId());
                        prWbs.setRevisionId(prRevision.getRevisionId());
            //			prWbs.setWbsCd(EtcUtil.nullConvert(wbs.get("objectId")) + "_" + EtcUtil.nullConvert(wbs.get("code")));

                        String objectId = EtcUtil.nullConvert(wbs.get("objectId")).trim();
                        String parentId = EtcUtil.nullConvert(wbs.get("parentObjectId")).trim();

                        prWbs.setWbsCd(objectId);
                        prWbs.setWbsPath(prRevision.getP6ProjectId());
                        prWbs.setWbsNm(EtcUtil.nullConvert(wbs.get("name")));
                        prWbs.setUpWbsCd(parentId);
                        prWbs.setWbsLevel(2);
                        prWbs.setEarlyStart(EtcUtil.nullConvert(wbs.get("startDate")));						// [확인] early -
                        prWbs.setEarlyFinish(EtcUtil.nullConvert(wbs.get("finishDate")));					// [확인] early -
                        prWbs.setActualStart(EtcUtil.nullConvert(wbs.get("summaryActualStartDate")));		// [확인] summaryActualStartDate?
                        prWbs.setActualFinish(EtcUtil.nullConvert(wbs.get("summaryActualFinishDate")));		// [확인] summaryActualFinishDate?
            //			prWbs.setRmrk(EtcUtil.nullConvert(wbs.get("code")));
                        prWbs.setRmrk(EtcUtil.nullConvert(wbs.get("objectId")) + "_" + EtcUtil.nullConvert(wbs.get("code")));

                        if (!objectId.isEmpty() && objectId.matches("\\d+")) {
                            prWbs.setP6WbsObjId(Integer.parseInt(objectId));
                        }
                        if (!parentId.isEmpty() && parentId.matches("\\d+")) {
                            prWbs.setP6UpWbsObjId(Integer.parseInt(parentId));
                        }

                        prWbs.setDltYn("N");
                        // FIXME JPA
                        prWbs.setRgstrId(prRevision.getRgstrId());
            //			prWbs.setRgstDt(now);
                        prWbsList.add(prWbs);
                    }
                }
            }
            // insert
            prWbsRepository.saveAllAndFlush(prWbsList);
        }
    }

    // WBS 물리 삭제 - REVISION 생성시점 활용
    public void deleteWbs(HashMap<String, Object> wbs) {
        mybatisSession.delete(DEFAULT_MAPPER_PATH + ".deleteWbsList", wbs);
    }

    // WBS 논리 삭제 - REVISION 삭제시점 활용
    public void updateWbsDeleteState(HashMap<String, Object> wbs) {
        mybatisSession.delete(DEFAULT_MAPPER_PATH + ".updateWbsDeleteState", wbs);
    }

    // WBS 코드 업데이트
    public void updateWbsCd(HashMap<String, Object> wbs) {

        // 01. PR_WBS.WBS_CD를 GAIA/CMIS 사용 형태에 맞게 가공한다.
        mybatisSession.update(DEFAULT_MAPPER_PATH + ".updateWbsCd", wbs);

        // 02. PR_WBS.UP_WBS_CD UPDATE
        mybatisSession.update(DEFAULT_MAPPER_PATH + ".updateUpWbsCd", wbs);

        // 03. PR_WBS.UP_WBS_CD 빈 값 초기화
        mybatisSession.update(DEFAULT_MAPPER_PATH + ".updateRootUpWbsCd", wbs);
    }
}

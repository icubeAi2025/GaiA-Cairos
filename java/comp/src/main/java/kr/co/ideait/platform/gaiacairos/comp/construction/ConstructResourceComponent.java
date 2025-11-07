package kr.co.ideait.platform.gaiacairos.comp.construction;

import kr.co.ideait.platform.gaiacairos.comp.construction.service.ResourceService;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction.resource.ResourceMybatisParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class ConstructResourceComponent {
    @Autowired
    ResourceService resourceService;

    /**
     * 자원투입현황 (Monthly / Daily) 가져오기
     *
     * @param cntrctNo
     * @param currentMonth
     * @param currentDay
     * @return
     */
    public Map<String, Object> selectResourceList(String cntrctNo, String currentMonth, String currentDay,
                                                  String searchText) {

        return resourceService.selectResourceList(cntrctNo, currentMonth, currentDay, searchText);
    }

    /**
     * 자원투입현황 (Monthly) 상세 가져오기
     *
     * @param cntrctNo
     * @param rsceTpCd
     * @param currentMonth
     * @param searchText
     * @return
     */
    public Map<String, Object> selectMonthlyResourceDetail(String cntrctNo, String rsceTpCd, String currentMonth,
                                                           String searchText) {


        return resourceService.selectMonthlyResourceDetail(cntrctNo, rsceTpCd, currentMonth, searchText);
    }

    /**
     * 자원투입현황 (Daily) 상세 가져오기
     *
     * @param cntrctNo
     * @return
     */
    public Map<String, Object> selectDailyResourceDetail(String cntrctNo, String rsceCd, String searchText) {

        return resourceService.selectDailyResourceDetail(cntrctNo, rsceCd, searchText);
    }

    // 착공계 관급자재수급계획서 데이터
    public ResourceMybatisParam.RawGovsplyMtrlItem getGovsplyMtrlList(String cntrctNo, String unitCnstType) {

        return resourceService.getGovsplyMtrlList(cntrctNo, unitCnstType);
    }

    // 노무/장비 데이터
    public ResourceMybatisParam.RawLbrEqList getLbrEqItem(String cntrctNo) {

        return resourceService.getLbrEqItem(cntrctNo);
    }
}

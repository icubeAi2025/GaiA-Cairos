package kr.co.ideait.platform.gaiacairos.comp.progress;

import com.google.common.collect.Maps;
import kr.co.ideait.platform.gaiacairos.comp.common.CommonUtilComponent;
import kr.co.ideait.platform.gaiacairos.comp.construction.InspectionReportComponent;
import kr.co.ideait.platform.gaiacairos.comp.construction.service.ChiefInspectionReportService;
import kr.co.ideait.platform.gaiacairos.comp.construction.service.DailyreportService;
import kr.co.ideait.platform.gaiacairos.comp.construction.service.InspectionreportService;
import kr.co.ideait.platform.gaiacairos.comp.progress.service.MonthlyreportAdminService;
import kr.co.ideait.platform.gaiacairos.comp.system.service.CommonCodeService;
import kr.co.ideait.platform.gaiacairos.comp.system.service.DocumentManageService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractComponent;
import kr.co.ideait.platform.gaiacairos.core.config.security.TokenService;
import kr.co.ideait.platform.gaiacairos.core.constant.CommonCodeConstants;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.*;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction.ChiefInspectionreportMybatisParam;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction.chiefinspectionreport.ChiefInspectionreportForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.document.DocumentForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.progress.monthlyreport.MonthlyreportAdminForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.progress.monthlyreport.MonthlyreportAdminMybatisParam;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.document.CbgnPropertyDto;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.util.UtilForm;
import kr.co.ideait.platform.gaiacairos.core.util.FileService;
import kr.co.ideait.platform.gaiacairos.core.util.restclient.DocumentServiceClient;
import kr.co.ideait.platform.gaiacairos.core.util.restclient.ICubeClient;
import kr.co.ideait.platform.gaiacairos.core.util.restclient.UbiReportClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Component
@RequiredArgsConstructor
public class MonthlyreportAdminComponent extends AbstractComponent {

    @Autowired
    MonthlyreportAdminService monthlyreportAdminService;

    /**
     * 월간공정보고 관리관용 목록 조회
     */
    public List<MonthlyreportAdminMybatisParam.MonthlyreportAdminOutput> getMonthlyreportAdminList(MonthlyreportAdminMybatisParam.MonthlyreportAdminInput input) {
        return monthlyreportAdminService.getMonthlyreportAdminList(input);
    }

    /**
     * 월간공정보고 관리관용 상세조회
     */
    public MonthlyreportAdminMybatisParam.MonthlyreportAdminOutput getMonthlyreportAdminDetail(MonthlyreportAdminMybatisParam.MonthlyreportAdminInput input) {
        return monthlyreportAdminService.getMonthlyreportAdminDetail(input);
    }

    /**
     * 월간공정보고 관리관용 중복확인
     */
    public Integer existsMonthlyreportAdmin(MonthlyreportAdminMybatisParam.MonthlyreportAdminInput input) {
        return monthlyreportAdminService.existsMonthlyreportAdmin(input);
    }

    /**
     * 월간공정보고 관리관용 추가
     * 중복되면 exist, 중복안되면 insert 후  성공이면 success 실패면 fail return
     */
    @Transactional
    public String createMonthlyreportAdmin(MonthlyreportAdminMybatisParam.MonthlyreportAdminInput input) {
        if(existsMonthlyreportAdmin(input) > 0){
            return "exist";
        }else{
            return monthlyreportAdminService.createMonthlyreportAdmin(input);
        }
    }

    /**
     * 월간공정보고 관리관용 수정
     * update 후 성공이면 success 실패면 fail return
     */
    @Transactional
    public String updateMonthlyreportAdmin(MonthlyreportAdminMybatisParam.MonthlyreportAdminInput input) {
        return monthlyreportAdminService.updateMonthlyreportAdmin(input);
    }

    /**
     * 월간공정보고 관리관용 수정
     * delete 후 성공이면 success 실패면 fail return
     */
    @Transactional
    public String deleteMonthlyreportAdmin(List<PrMonthlyReportAdmin> reportAdminList,String usrId) {
        for (PrMonthlyReportAdmin report : reportAdminList) {
            String result = monthlyreportAdminService.deleteMonthlyreportAdmin(
                    report.getCntrctChgId(),
                    report.getMonthlyReportAdminId(),
                    usrId
            );
            if (!"success".equals(result)) {
                return result;
            }
        }
        return "success";
    }
}

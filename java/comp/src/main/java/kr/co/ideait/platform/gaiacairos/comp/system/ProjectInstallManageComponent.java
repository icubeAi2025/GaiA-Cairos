package kr.co.ideait.platform.gaiacairos.comp.system;

import com.google.common.collect.Maps;
import kr.co.ideait.platform.gaiacairos.comp.project.service.InformationService;
import kr.co.ideait.platform.gaiacairos.comp.project.service.ProjectService;
import kr.co.ideait.platform.gaiacairos.comp.system.service.CommonCodeService;
import kr.co.ideait.platform.gaiacairos.comp.system.service.ProjectInstallManageService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractComponent;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnAttachments;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.ProjectDto;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.pjinstall.PjInstallCriteria;
import kr.co.ideait.platform.gaiacairos.core.type.PlatformType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class ProjectInstallManageComponent extends AbstractComponent {

    @Autowired
    private ProjectInstallManageService projectInstallManageService;

    @Autowired
    ProjectService projectService;

    @Autowired
    CommonCodeService commonCodeService;

    @Autowired
    InformationService informationService;

    public HashMap<String,Object> getMainPageData() {
        HashMap<String,Object> result = new HashMap<>();
        PjInstallCriteria cri = new PjInstallCriteria();
        cri.setPlatform(platform);

        result.put("pjInstallList", projectInstallManageService.getPjInstallList(cri));
        result.put("workTypeList",commonCodeService.getCommonCodeListByGroupCode("ce229e27-98c6-8c89-7be7-cbc27b0b1fc8"));
        result.put("dminsttTypeList", projectInstallManageService.getDminsttTypeList());

        return result;
    }

    public List<ProjectDto.ProjectInstall> getPjInstallList(PjInstallCriteria cri){
        cri.setPlatform(platform);

        return projectInstallManageService.getPjInstallList(cri);
    }

    public HashMap<String,Object> getDetailPageData(String plcReqNo) {
        ProjectDto.ProjectInstall projectInstall = projectInstallManageService.getPjInstall(plcReqNo);
        List<CnAttachments> attachments = projectService.getFileList(projectInstall.getAtchFileNo());

        HashMap<String,Object> result = new HashMap<String,Object>();
        result.put("pjInstall", projectInstall);
        result.put("attachments", attachments);
        result.put("workTypeList", commonCodeService.getCommonCodeListByGroupCode("ce229e27-98c6-8c89-7be7-cbc27b0b1fc8"));
        result.put("cntrctTypeList", commonCodeService.getCommonCodeListByGroupCode("0575cf90-0d6a-4d96-a298-d252c8179c32"));

        return result;
    }

    public ProjectDto.ProjectInstall getProjectInstall(String plcReqNo) {
        return projectInstallManageService.getPjInstall(plcReqNo);
    }

    @Transactional
    public String modifyOpenPstats(String plcReqNo, HashMap<String, String> params, CommonReqVo commonReqVo) {
        ProjectDto.ProjectInstall projectInstall = projectInstallManageService.getPjInstall(plcReqNo);

        if (projectInstall == null) {
            return "fail";
        }

        String status = params.get("openPstats");

        if (projectInstallManageService.updatePjInstallOpenPstats(plcReqNo, status)) {
            if ("0704".equals(status)) {
                informationService.updateStatus(plcReqNo, status);
            }

            if ("Y".equals(commonReqVo.getApiYn())) {
                if ( PlatformType.CAIROS.getName().equals(platform) && "P".equals(projectInstall.getPltReqType()) ) {
                    Map<String, Object> invokeParams = Maps.newHashMap();
                    invokeParams.put("plcReqNo", plcReqNo);
                    invokeParams.put("openPstats", status);

                    Map response = invokeCairos2Pgaia("CAGA9000", invokeParams);

                    if (!"00".equals( MapUtils.getString(response, "resultCode") ) ) {
                        throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
                    }
                }
            }

            return "success";
        }

        return "fail";
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Map receiveInterfaceService(String transactionId, Map params) {
        Map<String, Object> result = Maps.newHashMap();
        result.put("resultCode", "00");

        if ("CAGA9000".equals(transactionId)) {
            String plcReqNo = MapUtils.getString(params, "plcReqNo");
            String openPstats = MapUtils.getString(params, "openPstats");

            ProjectDto.ProjectInstall projectInstall = projectInstallManageService.getPjInstall(plcReqNo);

            if (projectInstall == null) {
                result.put("resultCode", "01");
                result.put("resultMsg", "현장개설요청 정보가 없습니다.");
                return result;
            }

            projectInstallManageService.updatePjInstallOpenPstats(plcReqNo, openPstats);
            if ("0704".equals(openPstats)) {
                informationService.updateStatus(plcReqNo, openPstats);
            }
        }

        return result;
    }
}

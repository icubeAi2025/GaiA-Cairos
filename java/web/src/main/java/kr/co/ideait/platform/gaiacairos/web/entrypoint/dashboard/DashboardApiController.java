package kr.co.ideait.platform.gaiacairos.web.entrypoint.dashboard;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kr.co.ideait.platform.gaiacairos.comp.common.service.MainBoardService;
import kr.co.ideait.platform.gaiacairos.comp.dashboard.DashboardComponent;
import kr.co.ideait.platform.gaiacairos.comp.system.service.BoardService;
import kr.co.ideait.platform.gaiacairos.comp.system.service.CommonCodeService;
import kr.co.ideait.platform.gaiacairos.core.annotation.RequiredProjectSelect;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DcStorageMain;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.dashboard.DashboardMybatisParam.MainInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.dashboard.DashboardMybatisParam.RankProjectInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.dashboard.DashboardMybatisParam.RegionProjectInput;
import kr.co.ideait.platform.gaiacairos.comp.dashboard.service.DashboardService;
import kr.co.ideait.platform.gaiacairos.comp.document.service.DocumentService;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.board.BoardMybatisParam.BoardListInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.dashboard.DashboardForm;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.constant.CommonCodeConstants;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.GridResult;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.iframework.EtcUtil;
import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import kr.co.ideait.platform.gaiacairos.core.util.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 포탈 로그인 페이지 (테스트용/ 삭제예정)
 */
@Slf4j
@RestController
@RequestMapping("/api/dashboard")
public class DashboardApiController extends AbstractController {

    @Autowired
    DashboardService dashboardService;

    @Autowired
    DashboardForm dashboardForm;

    @Autowired
    MainBoardService boardService;

    @Autowired
    DocumentService documentService;

    @Autowired
    FileService fileService;

    @Autowired
    CommonCodeService commonCodeService;

    @Value("${spring.profiles.active}")
    protected String activeProfile;

    @Value("${spring.application.name}")
    String applicationName;

    @Autowired
    DashboardComponent dashboardComponent;


    /**
     * 최초 메인대시보드 데이터 가져오기
     */
    @PostMapping("/mainDashBoard")
    @Description(name = "메인대시보드 조회", description = "프로젝트의 종합 데이터 조회", type = Description.TYPE.MEHTOD)
    public Result getMainDashBoard(CommonReqVo commonReqVo, @RequestBody @Valid DashboardForm.DashBoardMainGet dashBoardMainGet,
           UserAuth user) throws IOException {

            String[] userParam = commonReqVo.getUserParam();

            dashBoardMainGet.setLoginId(userParam[0]);
            dashBoardMainGet.setLoginType(userParam[1]);
            dashBoardMainGet.setSystemType(userParam[2]);

            MainInput input = dashboardForm.toMainGet(dashBoardMainGet);
            Result result = Result.ok()
                    .put("today", dashboardService.getToday(input))
                    .put("resource", dashboardService.getResource(input))
                    .put("work", dashboardService.getWork(input))
                    .put("projectEsc", dashboardService.getProjectEsc(input))
                    .put("projectPhoto", dashboardService.getProjectPhoto(input))
                    .put("photoList", dashboardService.getPhotoList(input))
                    .put("audit", dashboardService.getAudit(input))
                    .put("ecoScore", dashboardService.getEcoScore(input))
                    .put("docList", documentService.getDocListToDashboard(input.getCntrctNo(), input.getLoginId(), user))
                    .put("govsplyMtrlList", dashboardService.getGovsplyMtrlList(input))
                    .put("process", dashboardService.getProcess(input));

            // 2024-12-27 추가 - 프로젝트 정보 (주소, 경위도) 조회
//            HashMap<String, Object> address = EtcUtil
//                    .changeToUpperMapKey(dashboardService.getProjectAddress(dashBoardMainGet.getPjtNo()));
//            result.put("address_info", address);

            // 조건 - 주소, x(위도), y(경도) 값이 존재할 때 수행한다.
//            String plcLctAdrs = EtcUtil.nullConvert(address.get("PLC_LCT_ADRS"));
//            String plcLctX = EtcUtil.nullConvert(address.get("PLC_LCT_X"));
//            String plcLctY = EtcUtil.nullConvert(address.get("PLC_LCT_Y"));
//            boolean existCoordinate = !"".equals(plcLctAdrs) && !"".equals(plcLctX) && !"".equals(plcLctY);
//
//            if (existCoordinate) {
//                HashMap<String, Object> weatherMap = new HashMap<>();
//                weatherMap.put("x", plcLctX);
//                weatherMap.put("y", plcLctY);
//                result.put("weather", dashboardService.getPortalWeatherList(weatherMap));
//            }
            return result;
    }

    @GetMapping("/mainDashBoard/activityList")
    @Description(name = "Activity목록 조회", description = "메인대시보드 주요작업 Activity목록 조회", type = Description.TYPE.MEHTOD)
    public GridResult getActivityList(CommonReqVo commonReqVo, HttpServletRequest request,
                                      @Valid DashboardForm.DashBoardMainGet dashBoardMainGet) {

            String[] userParam = commonReqVo.getUserParam();

            dashBoardMainGet.setLoginId(userParam[0]);
            dashBoardMainGet.setLoginType(userParam[1]);
            dashBoardMainGet.setSystemType(userParam[2]);

            return GridResult
                    .ok(dashboardService.getActivityList(dashboardForm.toMainGet(dashBoardMainGet)));

    }

    @GetMapping("/mainDashBoard/govsplyMtrlList")
    @Description(name = "관급자재 현황 조회", description = "메인대시보드 주요작업 관급자재 현황 조회", type = Description.TYPE.MEHTOD)
    public GridResult getGovsplyMtrlList(CommonReqVo commonReqVo,
                                         @Valid DashboardForm.DashBoardMainGet dashBoardMainGet) {

            return GridResult
                    .ok(dashboardService.getGovsplyMtrlList(dashboardForm.toMainGet(dashBoardMainGet)));
    }

    /**
     * PotalAPIController로 이동
     * 메인 결재 데이터 가져오기 
    @PostMapping("/mainAudit")
    @Description(name = "메인대시보드 조회", description = "프로젝트의 종합 데이터 조회", type = Description.TYPE.MEHTOD)
    public Result getMainAudit(CommonReqVo commonReqVo,
                               @RequestBody @Valid DashboardForm.DashBoardMainGet dashBoardMainGet, UserAuth user,
                               HttpServletRequest request) throws IOException {
            String[] userParam = commonReqVo.getUserParam();

            dashBoardMainGet.setLoginId(userParam[0]);
            dashBoardMainGet.setLoginType(userParam[1]);
            dashBoardMainGet.setSystemType(userParam[2]);

            MainInput input = dashboardForm.toMainGet(dashBoardMainGet);
        Result result = Result.ok().put("audit", dashboardService.getAudit(input));

        if (!"prod".equals(activeProfile)) {
            // 2024-12-27 추가 - 프로젝트 정보 (주소, 경위도) 조회
            HashMap<String, Object> address = EtcUtil
                    .changeToUpperMapKey(dashboardService.getProjectAddress(dashBoardMainGet.getPjtNo()));
            result.put("address_info", address);

            // 조건 - 주소, x(위도), y(경도) 값이 존재할 때 수행한다.
            String plcLctAdrs = EtcUtil.nullConvert(address.get("PLC_LCT_ADRS"));
            String plcLctX = EtcUtil.nullConvert(address.get("PLC_LCT_X"));
            String plcLctY = EtcUtil.nullConvert(address.get("PLC_LCT_Y"));
            boolean existCoordinate = !"".equals(plcLctAdrs) && !"".equals(plcLctX) && !"".equals(plcLctY);

//            20250630 임시주석처리
            if (existCoordinate) {
                HashMap<String, Object> weatherMap = new HashMap<>();
                weatherMap.put("x", plcLctX);
                weatherMap.put("y", plcLctY);
                result.put("weather", dashboardService.getPortalWeatherList(weatherMap));
            }
        }
            return result;
    }
     */
    
    /**
     * 최초 대쉬보드 타입01 데이터 가져오기
     */
    @PostMapping("/dashBoard_type01")
    @Description(name = "종합대시보드 조회", description = "종합대시보드 종합 데이터 조회", type = Description.TYPE.MEHTOD)
    public Result mainComprehensiveDashBoard(CommonReqVo commonReqVo, HttpServletRequest request,
                                             @CookieValue(name = "lang", required = false, defaultValue = "ko") String langInfo) {
            String[] userParam = commonReqVo.getUserParam();

            String cmnRgCd = CommonCodeConstants.RG_CODE_GROUP_CODE;
//            RegionProjectInput regionInput = new RegionProjectInput();

            BoardListInput boardInput = new BoardListInput();
            boardInput.setBoardType("1");
            boardInput.setSystemType(userParam[2]);

//            RankProjectInput rankInput = new RankProjectInput();

            String rgnCd = "";

            MybatisInput input = new MybatisInput().add("usrId",commonReqVo.getUserId()).add("loginType",userParam[1])
                    .add("rgnCd",rgnCd).add("cmnRgCd",cmnRgCd).add("pjtType",applicationName);

            return Result.ok()
                    .put("userInfo", userParam)
                    .put("noticeList", boardService.getMainBoardList(boardInput))
                    .put("projectList", dashboardService.projectListGet(input))
                    .put("region", dashboardService.regionList(input))
                    .put("rankList", dashboardService.rankProjectListGet(input))
                    .put("bigData", dashboardService.bigDataGet(input))
                    .put("bigDataPhotoList", dashboardService.bigDataPhotoListGet(input));
    }

    @GetMapping("/makeRegionList/{rgnCd}")
    @Description(name = "지역별 프로젝트 조회", description = "종합대시보드의 지역별 프로젝트 조회", type = Description.TYPE.MEHTOD)
    public Result getRegionProjectList(CommonReqVo commonReqVo, @PathVariable("rgnCd") String rgnCd) {
            String cmnRgCd = CommonCodeConstants.RG_CODE_GROUP_CODE;

        String[] userParam = commonReqVo.getUserParam();

        MybatisInput input = new MybatisInput().add("usrId",commonReqVo.getUserId()).add("loginType",userParam[1]).add("cmnRgCd",cmnRgCd).add("pjtType",applicationName);

        if (rgnCd != null && !rgnCd.trim().isEmpty()) {
            input.add("rgnCd",rgnCd);
        }

        return Result.ok().put("projectList", dashboardService.projectListGet(input));
    }

    @GetMapping("/makeRankList/{rankOrder}")
    @Description(name = "현장랭킹 조회", description = "종합대시보드의 현장랭킹 조회", type = Description.TYPE.MEHTOD)
    public Result getRankProjectList(CommonReqVo commonReqVo, @PathVariable("rankOrder") String rankOrder) {

        String[] userParam = commonReqVo.getUserParam();

        MybatisInput input = new MybatisInput().add("usrId",commonReqVo.getUserId()).add("loginType",userParam[1]).add("rankOrder",rankOrder).add("pjtType",applicationName);

        return Result.ok().put("rankList", dashboardService.rankProjectListGet(input));
    }

    @GetMapping("/makebigData/{rgnCd}")
    @Description(name = "빅데이터 조회", description = "종합대시보드의 빅데이터 및 사진 조회", type = Description.TYPE.MEHTOD)
    public Result bigDataGet(CommonReqVo commonReqVo, @PathVariable("rgnCd") String rgnCd) {

        String[] userParam = commonReqVo.getUserParam();

        MybatisInput input = new MybatisInput().add("usrId",commonReqVo.getUserId()).add("loginType",userParam[1]).add("rgnCd",rgnCd).add("pjtType",applicationName);

            return Result.ok().put("bigData", dashboardService.bigDataGet(input))
                    .put("bigDataPhotoList", dashboardService.bigDataPhotoListGet(input));
    }

    @GetMapping("/eco-friendly-list")
    public GridResult selectEcoFriendly(DashboardForm.EcoFriendlyParam param) {
        return GridResult.ok(dashboardComponent.selectEcoFriendlyList(param));
    }
}

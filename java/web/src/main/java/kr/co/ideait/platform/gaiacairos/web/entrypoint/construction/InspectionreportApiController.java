package kr.co.ideait.platform.gaiacairos.web.entrypoint.construction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.co.ideait.platform.gaiacairos.comp.system.service.AuthorityGroupService;
import kr.co.ideait.platform.gaiacairos.comp.system.service.DepartmentService;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.comp.construction.InspectionReportComponent;
import kr.co.ideait.platform.gaiacairos.comp.construction.service.ChiefInspectionReportService;
import kr.co.ideait.platform.gaiacairos.comp.construction.service.DailyreportService;
import kr.co.ideait.platform.gaiacairos.comp.construction.service.InspectionreportService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction.inspectionreport.InspectionreportForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import kr.co.ideait.platform.gaiacairos.core.util.FileService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("api/construction/inspectionreport")
public class InspectionreportApiController extends AbstractController {

        @Autowired
        InspectionreportForm inspectionReportForm;

        @Autowired
        FileService fileService;

        @Autowired
        InspectionreportService inspectionreportService;

        @Autowired
        DepartmentService departmentService;

        @Autowired
        AuthorityGroupService authorityGroupService;

        @Autowired
        InspectionReportComponent inspectionReportComponent;

        @Autowired
        DailyreportService dailyreportService;

        @Autowired
        ChiefInspectionReportService chiefInspectionReportService;

        @PostMapping("/list/main")
        @Description(name = "감리일지 목록 조회", description = "감리일지 목록 조회", type = Description.TYPE.MEHTOD)
        public Result getListMainData(CommonReqVo commonReqVo, @RequestBody InspectionreportForm.CreateReport input) {
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.FUNCTION.name());
                userLog.setExecType("감리일지 목록 메인 데이터 조회");
                systemLogComponent.addUserLog(userLog);

                //TODO 추후 아래 내용들을 컴포넌트로 옮기기!!!
                String userId = commonReqVo.getUserId();

                List<SmAuthorityGroup> myAuthorityGroupList = authorityGroupService.getAuthorityGroupListByUsrIdAndCntrctNo(userId,input.getCntrctNo());

                List<SmOrganization> myCMList = new ArrayList<>();

                for(SmAuthorityGroup authorityGroup : myAuthorityGroupList){
                        List<SmAuthorityGroupUsers> smAuthorityGroupUserList = null;
                        if("05".equals(authorityGroup.getRghtGrpRole())){
                               smAuthorityGroupUserList = authorityGroupService.getAuthorityGroupUserListByRghtGrpCd(authorityGroup.getRghtGrpCd());

                               for(SmAuthorityGroupUsers smAuthorityGroupUsers : smAuthorityGroupUserList){
                                       if("D".equals(smAuthorityGroupUsers.getRghtGrpUsrTy())){
                                               //이 때는 부서번호
                                               myCMList.add(departmentService.getOrganizationByUsrIdAndDeptNo(userId,smAuthorityGroupUsers.getAuthNo()));
                                       }
                                       else{
                                               //이 때는 조직번호
                                               myCMList.add(departmentService.getOrganizationByOrgNo(smAuthorityGroupUsers.getAuthNo()));
                                       }
                               }
                        }
                }

//                List<SmDepartment> myDepartmentList = departmentService.getDepartmentListByUserIdAndCntrctNo(userId,input.getCntrctNo());
//
//
//                for(SmDepartment smDepartment : myDepartmentList){
//                        if("05".equals(smDepartment.getSvrType())){
//                                myCMList.add(departmentService.getOrganizationByUsrIdAndDeptUuid(userId,smDepartment.getDeptUuid()));
//                        }
//                }

                return Result.ok().put("reportList", inspectionreportService.getReportList(input.getYear(),
                                input.getMonth(), input.getSearchValue(), input.getSelectValue(), input.getCntrctNo(),
                                input.getWorkType(), input.getRgstrId()))
                        .put("myCMList",myCMList);
        }
        /**
         * 감리일지 목록 데이터 조회
         */
        @PostMapping("/get/report")
        @Description(name = "감리일지 목록 조회", description = "감리일지 목록 조회", type = Description.TYPE.MEHTOD)
        public Result getReport(CommonReqVo commonReqVo, @RequestBody InspectionreportForm.CreateReport input) {

                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.FUNCTION.name());
                userLog.setExecType("감리일지 목록 조회");
                systemLogComponent.addUserLog(userLog);

                return Result.ok().put("reportList", inspectionreportService.getReportList(input.getYear(),
                                input.getMonth(), input.getSearchValue(), input.getSelectValue(), input.getCntrctNo(),
                                input.getWorkType(), input.getRgstrId()));
        }

        /**
         * 감리일지 연도 데이터 조회
         */
        @PostMapping("/get/year")
        @Description(name = "감리일지 연도 데이터 조회", description = "감리일지 목록 화면 연도 데이터 조회", type = Description.TYPE.MEHTOD)
        public Result getYear(CommonReqVo commonReqVo, @RequestBody InspectionreportForm.CreateReport input) {

                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.FUNCTION.name());
                userLog.setExecType("감리일지 년도 데이터 조회");
                systemLogComponent.addUserLog(userLog);

                List<String> years = inspectionreportService.getReportYears(input.getCntrctNo());
                return Result.ok().put("yearList", years);
        }

        /**
         * 감리일지 상세 조회
         *
         * @throws IOException
         */
        @PostMapping("/getReportData")
        @Description(name = "감리일지 상세 조회", description = "감리일지 상세 조회", type = Description.TYPE.MEHTOD)
        public Result getReportData(CommonReqVo commonReqVo, @RequestBody InspectionreportForm.CreateReport input)
                        throws IOException {

                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.FUNCTION.name());
                userLog.setExecType("감리일지 상세 조회");
                systemLogComponent.addUserLog(userLog);

                return Result.ok()
                                .put("reportData", // 감리일지 데이터
                                                inspectionreportService.getReportData(input.getCntrctNo(),
                                                                input.getDailyReportId()));

        }

        /**
         * 감리일지 복사
         *
         * @throws IOException
         */
        @PostMapping("/copy")
        @Description(name = "감리일지 복사", description = "감리일지 복사", type = Description.TYPE.MEHTOD)
        public Result copyReportData(CommonReqVo commonReqVo, @RequestBody InspectionreportForm.CreateReport input)
                        throws IOException {

                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.FUNCTION.name());
                userLog.setExecType("감리일지 복사");
                systemLogComponent.addUserLog(userLog);

                return Result.ok().put("copiedData", // 복사된 감리일지 데이터
                                inspectionReportComponent.copyReport(commonReqVo, input));

        }

        /**
         * 감리일지 추가
         */
        @PostMapping("/add/report")
        @Description(name = "감리일지 추가", description = "감리일지 추가", type = Description.TYPE.MEHTOD)
        public Result addReport(CommonReqVo commonReqVo,
                        @RequestBody InspectionreportForm.CreateReport input) {

                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.FUNCTION.name());
                userLog.setExecType("감리일지 추가");
                systemLogComponent.addUserLog(userLog);

                inspectionReportComponent.addReport(commonReqVo, input);
                return Result.ok().put("success", true);
        }

        /**
         * 감리일지 수정
         */
        @PostMapping("/update/report")
        @Description(name = "감리일지 수정", description = "감리일지 수정", type = Description.TYPE.MEHTOD)
        public Result updateReport(CommonReqVo commonReqVo,
                        @RequestBody InspectionreportForm.CreateReport input) {

                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.FUNCTION.name());
                userLog.setExecType("감리일지 수정");
                systemLogComponent.addUserLog(userLog);

                inspectionReportComponent.updateReport(commonReqVo, input);
                return Result.ok().put("success", true);
        }

        /**
         * 감리일지 삭제
         */
        @PostMapping("delete/report")
        @Description(name = "감리일지 삭제", description = "감리일지 삭제", type = Description.TYPE.MEHTOD)
        public Result deleteReport(CommonReqVo commonReqVo, @RequestBody InspectionreportForm.Delete input) {

                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.FUNCTION.name());
                userLog.setExecType("감리일지 삭제");
                systemLogComponent.addUserLog(userLog);

                for (int i = 0; i < input.getReportList().size(); i++) {
                        CwInspectionReport delete = inspectionreportService.getInspectionData(
                                        input.getReportList().get(i).getCntrctNo(),
                                        input.getReportList().get(i).getDailyReportId());
                        inspectionreportService.deleteReport(delete);
                }
                return Result.ok();
        }

        /**
         * 작성완료
         */
        @PostMapping("/approval")
        @Description(name = "감리일지 작성완료", description = "감리일지 작성완료", type = Description.TYPE.MEHTOD)
        public Result approvalSafety(CommonReqVo commonReqVo, @RequestBody List<InspectionreportForm.CreateReport> inputList) {
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.FUNCTION.name());
                userLog.setExecType("감리일지 작성완료");
                systemLogComponent.addUserLog(userLog);

                // 감리일지 상태값 업데이트
                for (InspectionreportForm.CreateReport report : inputList) {
                        inspectionReportComponent.apprvlReport(report);
                }

                // 전자결재 연동x
                // inspectionreportService.requestApprovalInspectionReport(input.getCntrctNo(),
                // input.getDailyReportId(),
                // commonReqVo.getApiYn(), commonReqVo.getPjtDiv());
                return Result.ok();
        }

        // /**
        // * 감리일지 게시물 존재 여부 확인(날짜와 업무구분 코드로 중복 체크)
        // */
        // @PostMapping("/report-exists")
        // @Description(name = "감리일지 게시물 존재 여부 확인", description = "감리일지 게시물 존재 여부 확인",
        // type = Description.TYPE.MEHTOD)
        // public Result reportExists(CommonReqVo commonReqVo, @RequestBody
        // InspectionreportForm.CreateReport input) {
        // Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        // userLog.setLogType(LogType.FUNCTION.name());
        // userLog.setExecType("감리일지 게시물 존재 여부 확인");
        // systemLogComponent.addUserLog(userLog);

        // // 보고일자 & 업무구분별 중복체크(중복아닐 시 true 반환, 중복이면 false 반환)
        // return Result.ok()
        // .put("dailyReportExists",
        // inspectionreportService.chkReportDateAndWorkAndUserId(
        // input.getCntrctNo(),
        // input.getDailyReportDate(),
        // input.getWorkCd(), commonReqVo.getUserId()));
        // }
        @PostMapping("/test")
        public Result test(CommonReqVo commonReqVo, @RequestBody HashMap<String,String> requestParams){
                String cntrctNo = requestParams.get("cntrctNo");
                String dailyReportId = requestParams.get("dailyReportId");
                String baseUrl = requestParams.get("baseUrl");
                String imgDir = requestParams.get("imgDir");

                Map<String,String> result = inspectionreportService.makeInspectionReportDoc(cntrctNo,dailyReportId,imgDir,baseUrl);

                if(result != null){
                        return Result.ok().put("result",result);
                }
                return Result.nok(ErrorType.ETC);
        }
}

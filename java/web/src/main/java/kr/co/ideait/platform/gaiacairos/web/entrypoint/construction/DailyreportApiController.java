package kr.co.ideait.platform.gaiacairos.web.entrypoint.construction;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import jakarta.validation.Valid;
import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.comp.construction.DailyreportComponent;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwAttachments;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwDailyReport;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwDailyReportPhoto;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction.dailyreport.DailyreportForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.util.UtilForm;
import kr.co.ideait.platform.gaiacairos.core.type.FileUploadType;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import kr.co.ideait.platform.gaiacairos.core.util.FileService;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/api/construction")
public class DailyreportApiController extends AbstractController {

        @Autowired
        DailyreportComponent dailyreportComponent;

        @Autowired
        DailyreportForm dailyreportForm;

        @Autowired
        FileService fileService;

        // private String pjtType = "CMIS";

        /**
         * 작업일지 목록 조회
         *
         * @param dailyreportMain
         * @return
         */
        @PostMapping("/dailyreport/dailyreport-list")
        @Description(name = "작업일지 목록 조회", description = "프로젝트의 계약별 작업일지 목록 조회", type = Description.TYPE.MEHTOD)
        public Result getDailyreportList(CommonReqVo commonReqVo, @RequestBody @Valid DailyreportForm.ConstructionMain dailyreportMain) {

                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.FUNCTION.name());
                userLog.setExecType("작업일지 목록 조회");
                systemLogComponent.addUserLog(userLog);

                return Result.ok()
                        .put("dailyreportList",
                                dailyreportComponent.selectDailyreportList(dailyreportMain.getCntrctNo(),
                                        dailyreportMain.getYear(), dailyreportMain.getMonth(),
                                        dailyreportMain.getStatus(),
                                        dailyreportMain.getSearchText()));
        }

        /**
         * 작업일지 추가
         *
         * @param dailyreportInsert
         * @return
         */
        @PostMapping("/dailyreport/add-dailyreport")
        @Description(name = "작업일지 추가", description = "프로젝트의 계약별 작업일지 추가", type = Description.TYPE.MEHTOD)
        public Result addDailyReport(CommonReqVo commonReqVo, @RequestBody @Valid DailyreportForm.DailyReportInsert dailyreportInsert) {
                log.info("addDailyReport: dailyreportInsert = {}",dailyreportInsert);
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.FUNCTION.name());
                userLog.setExecType("작업일지 추가");
                systemLogComponent.addUserLog(userLog);

                dailyreportInsert.setDltYn("N");

                Long reportId = null;

                if(!("copy").equals(dailyreportInsert.getCreateType())) {
                        // 게시물작성날짜 확인
                        if (dailyreportComponent.chkDailyReportDate(dailyreportInsert.getCntrctNo(),
                                dailyreportInsert.getDailyReportDate())) {

                                CwDailyReport cwDailyReport = dailyreportComponent
                                        .addDailyReport(dailyreportForm.DailyReportInsert(dailyreportInsert));

                                reportId = cwDailyReport.getDailyReportId();
                        } else {
                                throw new GaiaBizException(ErrorType.DUPLICATION_DATA, "DuplicateReport");
                        }
                } else {
                        // 복사인 경우 게시물번호, 게시물작성날짜 확인 안함, 수정 페이지에서 진행
                        CwDailyReport cwDailyReport = dailyreportComponent
                                .addDailyReport(dailyreportForm.DailyReportInsert(dailyreportInsert));

                        reportId = cwDailyReport.getDailyReportId();
                }
                return Result.ok().put("dailyreportId", reportId);
        }

        /**
         * 작업일지 상세조회
         *
         * @param dailyreportMain
         * @return
         */
        @PostMapping("/dailyreport/dailyreport-detail")
        @Description(name = "작업일지 상세조회", description = "프로젝트의 계약별 작업일지 상세조회", type = Description.TYPE.MEHTOD)
        public Result getSummaryDetail(CommonReqVo commonReqVo, @RequestBody @Valid DailyreportForm.ConstructionMain dailyreportMain) {

                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.FUNCTION.name());
                userLog.setExecType("작업일지 상세 조회");
                systemLogComponent.addUserLog(userLog);

               
                CwDailyReport dailyReport = dailyreportComponent.getSummary(dailyreportMain.getCntrctNo(),
                        dailyreportMain.getDailyReportId());

                /* by soulers. after 2025-07-10 delete   
                log.warn("PHOTODATA:{}",dailyreportComponent.getPhoto(dailyreportMain.getCntrctNo(),
                        dailyreportMain.getDailyReportId()));
                log.warn("ACTIVITYTODAY : "+dailyreportComponent.getActivity(dailyreportMain.getCntrctNo(),
                        dailyreportMain.getDailyReportId(),
                        dailyReport.getDailyReportDate(), "TD")); */

                return Result.ok()
                        /* by soulers. after 2025-07-10 delete   
                        .put("summaryDetail",
                                dailyreportComponent.getSummary(dailyreportMain.getCntrctNo(),
                                        dailyreportMain.getDailyReportId()))*/
                        .put("summaryDetail",dailyReport)
                        .put("activityTodayDetail",
                                dailyreportComponent.getActivity(dailyreportMain.getCntrctNo(),
                                        dailyreportMain.getDailyReportId(),
                                        dailyReport.getDailyReportDate(), "TD"))
                        .put("activityNextDetail",
                                dailyreportComponent.getActivity(dailyreportMain.getCntrctNo(),
                                        dailyreportMain.getDailyReportId(),
                                        dailyReport.getDailyReportDate(), "TM"))
                        .put("mData",
                                dailyreportComponent.getResource(dailyreportMain.getCntrctNo(),
                                        dailyreportMain.getDailyReportId(), "M"))
                        .put("lData",
                                dailyreportComponent.getResource(dailyreportMain.getCntrctNo(),
                                        dailyreportMain.getDailyReportId(), "L"))
                        .put("eData",
                                dailyreportComponent.getResource(dailyreportMain.getCntrctNo(),
                                        dailyreportMain.getDailyReportId(), "E"))
                        .put("photoData",
                                dailyreportComponent.getPhoto(dailyreportMain.getCntrctNo(),
                                        dailyreportMain.getDailyReportId()));
        }

        /**
         * 작업일지 수정
         *
         * @param dailyreportInsert
         * @return
         */
        @PostMapping("/dailyreport/update-dailyreport")
        @Description(name = "작업일지 수정", description = "프로젝트의 계약별 작업일지 수정", type = Description.TYPE.MEHTOD)
        public Result updateDailyReport(CommonReqVo commonReqVo, @RequestBody @Valid DailyreportForm.DailyReportInsert dailyreportInsert) {
                log.info("updateDailyReport: 작업일지 수정 진행 dailyreportInsert = {}",dailyreportInsert );

                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.FUNCTION.name());
                userLog.setExecType("작업일지 수정");

                Result response = Result.ok();
                try {
                        // 보고서 번호 중복 체크 (복사로 게시물 추가한 경우)
//                        if (("copy").equals(dailyreportInsert.getCreateType()) &&
//                                dailyreportComponent.chkReportNo(dailyreportInsert.getCntrctNo(), dailyreportInsert.getReportNo()) == false)
                                //throw new GaiaBizException(ErrorType.DUPLICATION_DATA, "DuplicateReportNo");

                        CwDailyReport dailyReportData = dailyreportComponent.getSummary(dailyreportInsert.getCntrctNo(),
                                dailyreportInsert.getDailyReportId());
                        if (dailyReportData != null) {
                                log.info("updateDailyReport: 작업일지 수정 대상 조회 성공 dailyReportData = {}",dailyReportData);
                                dailyreportInsert.setDltYn("N");
                                dailyreportForm.toUpdateCwDailyReport(dailyreportInsert, dailyReportData);

                                /* CwDailyReport dt 가 필요 없음. by soulers. */
                                /*CwDailyReport dt = dailyreportComponent.updateDailyReport(
                                        dailyreportInsert.getDailyReportActivity(),
                                        dailyreportInsert.getDailyReportResource(),
                                        dailyReportData); */

                                dailyreportComponent.updateDailyReport(
                                        dailyreportInsert.getDailyReportActivity(),
                                        dailyreportInsert.getDailyReportResource(),
                                        dailyReportData);

                                log.info("updateDailyReport: 작업일지 수정 성공");
                                /* updateDailyReport 에서 공정현황 업데이트 해주는 데 중복 작업 하고 있음 by soulers.*/
                                //dailyreportComponent.updateRate(dt);
                        }

                        log.info("updateDailyReport: 작업일지 수정 대상 미존재");

                        userLog.setResult("SUCCESS");
                        systemLogComponent.addUserLog(userLog);
                } catch (GaiaBizException e) {
                        log.info("updateDailyReport: 작업일지 수정 중 오류 발생, 오류 메세지 = " + e.getMessage());
                        userLog.setResult("FAIL");
                        userLog.setErrorReason(e.getMessage());
                        systemLogComponent.addUserLog(userLog);
                        response = response.put("resultMsg", e.getMessage());
                }

                return response;

        }

        @PostMapping("/dailyreport/uploadFile")
        @Description(name = "작업일지 공정사진 추가", description = "작업일지의 공정사진 추가", type = Description.TYPE.MEHTOD)
        public void uploadAjaxPost(CommonReqVo commonReqVo
                , @RequestParam("uploadData") String uploadData
                , @RequestParam("deleteData") String deleteData) throws ParseException {

                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.FUNCTION.name());
                userLog.setExecType("작업일지 공정사진 추가");
                systemLogComponent.addUserLog(userLog);

                log.warn("uploadData : "+uploadData);
                log.warn("deleteData : "+deleteData);
                JSONParser jsonParser = new JSONParser();
                JSONArray uploadJsonArray = (JSONArray) jsonParser.parse(uploadData);
                JSONArray deleteJsonArray = (JSONArray) jsonParser.parse(deleteData);

//

                for (int i = 0; i < deleteJsonArray.size(); i++) {
                        JSONObject dobj = (JSONObject) deleteJsonArray.get(i);

                        CwAttachments cwAttachments = new CwAttachments();
                        cwAttachments.setFileNo(Integer.parseInt(String.valueOf(dobj.get("fileNo"))));
                        cwAttachments.setSno(Integer.parseInt(String.valueOf(dobj.get("sno"))));
                        cwAttachments.setFileDiv("I");

                        CwDailyReportPhoto cwDailyReportPhoto = new CwDailyReportPhoto();
                        cwDailyReportPhoto.setCntrctNo(dobj.get("cntrctNo").toString());
                        cwDailyReportPhoto.setShotDate(dobj.get("shotDate").toString());
                        cwDailyReportPhoto
                                .setDailyReportId(Integer.parseInt(String.valueOf(dobj.get("dailyReportId"))));
                        cwDailyReportPhoto.setAtchFileNo(Integer.parseInt(String.valueOf(dobj.get("fileNo"))));
                        cwDailyReportPhoto.setCnsttyPhtSno(Integer.parseInt(String.valueOf(dobj.get("cnsttyPhtSno"))));

                        dailyreportComponent.deleteDailyReportPhoto(cwAttachments, cwDailyReportPhoto);
                }

                for (int i = 0; i < uploadJsonArray.size(); i++) {

                        JSONObject obj = (JSONObject) uploadJsonArray.get(i);
                        String metaJson = String.valueOf(obj.get("meta"));
                        FileService.FileMeta oldMeta = null;
                        FileService.FileMeta newMeta = null;
                        try {
                                String targetDir = String.format("%s/%s", uploadPath, getUploadPathByWorkType(FileUploadType.DailyReport));
                                oldMeta = objectMapper.readValue(metaJson, FileService.FileMeta.class);
                                newMeta = fileService.build(metaJson, targetDir);

                                //DB 로직 수행
                                Integer dId = Integer.parseInt(String.valueOf(obj.get("dailyReportId")));
                                Integer seq = Integer.parseInt(String.valueOf(obj.get("seq")));

                                CwAttachments cwAttachments = new CwAttachments();
                                cwAttachments.setFileNm(newMeta.getOriginalFilename());
                                cwAttachments.setFileDiskNm(newMeta.getFileName());
                                cwAttachments.setFileDiskPath(newMeta.getDirPath());
                                cwAttachments.setSno(1);
                                cwAttachments.setFileSize(newMeta.getSize());
                                cwAttachments.setDltYn("N");
                                cwAttachments.setFileHitNum(0);
                                cwAttachments.setFileDiv("I");

                                CwDailyReportPhoto cwDailyReportPhoto = new CwDailyReportPhoto();
                                cwDailyReportPhoto.setCntrctNo(obj.get("cntrctNo").toString());
                                cwDailyReportPhoto.setDailyReportId(dId);
                                cwDailyReportPhoto.setActivityId(obj.get("activityId") != null ? obj.get("activityId").toString() : "");
                                cwDailyReportPhoto.setTitlNm(obj.get("titlNm") != null ? obj.get("titlNm").toString() : "");
                                cwDailyReportPhoto.setShotDate(obj.get("shotDate").toString());
                                cwDailyReportPhoto.setDscrpt(obj.get("dscrpt").toString());
                                cwDailyReportPhoto.setDltYn("N");


                                dailyreportComponent.createDailyReportPhoto(cwAttachments, cwDailyReportPhoto, seq);

                                // temp폴더에서 업무폴더로 이미지 이동
                                fileService.moveFile(oldMeta.getFilePath(), newMeta.getFilePath());
                                log.info("uploadAjaxPost: 이미지파일 temp → dailyReport 이동 성공. targetDir = {} oldMeta = {}, newMeta = {}", targetDir, oldMeta, newMeta);
                        } catch (GaiaBizException e) {
                                log.error("uploadAjaxPost: 파일 이동 실패. oldMeta = {}. fallback", oldMeta, e);
                        } catch (JsonProcessingException e) {
                                log.error("JsonMappingException : {}",e.getMessage());
                        }


                }

        }

        /**
         * 금일실적 변경 및 명일실적 변경 activity 목록 조회
         *
         * @param dailyreportMain
         * @return
         */
        @PostMapping("/dailyreport/dailyreport-chg")
        @Description(name = "작업일지 실적변경 데이터 목록", description = "작업일지의 실적변경 Activity 목록 및 금일 실적 데이터, 명일 계획 Activity 조회", type = Description.TYPE.MEHTOD)
        public Result getPrActivityList(CommonReqVo commonReqVo, @RequestBody @Valid DailyreportForm.ConstructionMain dailyreportMain) {
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.FUNCTION.name());
                userLog.setExecType("작업일지 실적변경 데이터 목록 조회");
                systemLogComponent.addUserLog(userLog);

                Result response = Result.ok();
                if (dailyreportMain.getWorkDtType().toString().equals("TD")) {
                        response = response
                                .put("prActivityList",
                                        dailyreportComponent.selectPrActivityList(
                                                dailyreportMain.getCntrctNo(),
                                                dailyreportMain.getDailyReportId(),
                                                dailyreportMain.getWorkDtType(),
                                                dailyreportMain.getPlanStart(),
                                                dailyreportMain.getPlanFinish(),
                                                dailyreportMain.getSearchText()))
                                .put("todayDailyReportActivityList",
                                        dailyreportComponent.selectDailyReportActivityListforChange(
                                                dailyreportMain.getCntrctNo(),
                                                dailyreportMain.getDailyReportId(),
                                                dailyreportMain.getWorkDtType()))
                                .put("todayDailyReportQdbList",
                                        dailyreportComponent.selectTodayDailyReportQdbList(
                                                dailyreportMain.getCntrctNo(),
                                                dailyreportMain.getDailyReportId(),
                                                dailyreportMain.getWorkDtType()))
                                .put("todayDailyReportResourceList",
                                        dailyreportComponent.selectTodayDailyReportResourceList(
                                                dailyreportMain.getCntrctNo(),
                                                dailyreportMain.getDailyReportId()));
                } else {
                        response = response
                                .put("prActivityList",
                                        dailyreportComponent.selectPrActivityList(
                                                dailyreportMain.getCntrctNo(),
                                                dailyreportMain.getDailyReportId(),
                                                dailyreportMain.getWorkDtType(),
                                                dailyreportMain.getPlanStart(),
                                                dailyreportMain.getPlanFinish(),
                                                dailyreportMain.getSearchText()))
                                .put("nextDailyReportActivityList",
                                        dailyreportComponent.selectDailyReportActivityListforChange(
                                                dailyreportMain.getCntrctNo(),
                                                dailyreportMain.getDailyReportId(),
                                                dailyreportMain.getWorkDtType()));
                }
                return response;
        }

        /**
         * 작업일지 금일 명일 실적 수정
         *
         * @param dailyreportInsert
         * @return
         */
        @PostMapping("/dailyreport/update-chg-dailyreport-activity")
        @Description(name = "작업일지 - 금일 및 명일 실적 수정", description = "작업일지의 금일 및 명일 실적의 Activity 수정", type = Description.TYPE.MEHTOD)
        public Result updateDailyReportActivity(CommonReqVo commonReqVo,
                                                @RequestBody @Valid DailyreportForm.DailyReportInsert dailyreportInsert) {

                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.FUNCTION.name());
                userLog.setExecType("작업일지 - 금일 및 명일 실적 수정");
                systemLogComponent.addUserLog(userLog);

                dailyreportComponent.updateDailyReportChage(dailyreportInsert.getDailyReportActivity(),
                        dailyreportInsert.getDailyReportResource(), dailyreportInsert.getPrActivity(),
                        dailyreportInsert.getCntrctNo(), dailyreportInsert.getDailyReportId(),
                        dailyreportInsert.getDailyReportDate(), dailyreportInsert.getWorkDtType());

                return Result.ok();
        }

        /**
         * 작업일지 상태 업데이트
         *
         * @param dailyreport
         * @return
         */
        @PostMapping("/dailyreport/update-status-dailyreport")
        @Description(name = "작업일지 승인상태 수정", description = "작업일지의 승인상태 수정", type = Description.TYPE.MEHTOD)
        public Result updateStatusDailyReport(CommonReqVo commonReqVo, @RequestBody @Valid DailyreportForm.DailyReportList dailyreport) {

                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.FUNCTION.name());
                userLog.setExecType("작업일지 승인상태 수정");
                systemLogComponent.addUserLog(userLog);

                dailyreportComponent.updateDailyReportList(dailyreport.getDailyReportList(), commonReqVo.getApiYn(), commonReqVo.getPjtDiv());
                return Result.ok();
        }

        /**
         * 작업일지 삭제
         *
         * @param dailyreport
         * @return
         */
        @PostMapping("/dailyreport/del-dailyreport")
        @Description(name = "작업일지 삭제", description = "계약별 작업일지 삭제", type = Description.TYPE.MEHTOD)
        public Result delDailyReport(CommonReqVo commonReqVo, @RequestBody @Valid DailyreportForm.DailyReportList dailyreport) {

                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.FUNCTION.name());
                userLog.setExecType("작업일지 삭제");
                systemLogComponent.addUserLog(userLog);

                dailyreportComponent.delDailyReport(dailyreport.getDailyReportList());
                return Result.ok();
        }


        /**
         * 셀렉트박스 or 라디오버튼
         *
         * @return
         */
        @PostMapping("/dailyreport/make-commonBox")
        @Description(name = "TODO", description = "",type = Description.TYPE.MEHTOD)
        public Result getCommonBoxList(CommonReqVo commonReqVo, @RequestBody @Valid List<UtilForm.CommonBoxGet> commonBoxGet) {

                Map<String, Object> returnMap = new HashMap<String, Object>();
                if(commonBoxGet.size() > 0) {

                        log.debug("=====================================================================================");
                        log.debug("commonBoxGet.size() : " +commonBoxGet.size());
                        log.debug("=====================================================================================");

                        for(UtilForm.CommonBoxGet makeBox : commonBoxGet) {
                                String orderByCol = (makeBox.getOrderByCol() == null || makeBox.getOrderByCol().isBlank()) ? makeBox.getCol2() : makeBox.getOrderByCol();
                                String orderByType = (makeBox.getOrderByType() == null || makeBox.getOrderByType().isBlank()) ? "ASC" : makeBox.getOrderByType();
                                String ckeckValue = (makeBox.getCkeckedValue() == null || makeBox.getCkeckedValue().isBlank()) ? "" : makeBox.getCkeckedValue();
                                String initType = (makeBox.getInitText() == null || makeBox.getInitText().isBlank()) ? "noInit" :"init";

                                log.debug("=====================================================================================");
                                log.debug("                                   파라미터 확인                                     ");
                                log.debug("makeBox.getMakeId() 			: " + makeBox.getMakeId()                       		 );
                                log.debug("makeBox.getCol1() 			: " + makeBox.getCol1()                       			 );
                                log.debug("makeBox.getCol2()	 		: " + makeBox.getCol2()                       			 );
                                log.debug("makeBox.getTableName() 		: " + makeBox.getTableName()                   			 );
                                log.debug("makeBox.getAddSql() 			: " + makeBox.getAddSql()                      			 );
                                log.debug("makeBox.getOrderByCol() 		: " + makeBox.getOrderByCol()                 			 );
                                log.debug("makeBox.getOrderByType() 	: " + makeBox.getOrderByType()              			 );
                                log.debug("makeBox.getBoxType() 		: " + makeBox.getBoxType()                    			 );
                                log.debug("makeBox.getCkeckedValue() 	: " + makeBox.getCkeckedValue()               			 );
                                log.debug("makeBox.getInitText() 		: " + makeBox.getInitText()                    			 );
                                log.debug("makeBox.getParamNm() 		: " + makeBox.getParamNm()                     			 );
                                log.debug("=====================================================================================");

                                String [] param = makeBox.getAddSql().split(",");

                                @SuppressWarnings("unchecked")
                                List<Map<String, Object>> selectSrcList = dailyreportComponent.selectMakeDataListUsingCondition(makeBox.getCol1(), makeBox.getCol2(), makeBox.getTableName(), param, orderByCol, orderByType);

                                StringBuilder makItem = new StringBuilder();

                                if("selectBox".equals(makeBox.getBoxType())) {

                                        makItem.append("<select id='").append(makeBox.getMakeId());
                                        String funName = makeBox.getFunName();
                                        if(funName != null && !funName.isBlank()) {
                                                makItem.append("' ").append(makeBox.getFuntype()).append("='").append(makeBox.getFunName()).append("(").append(makeBox.getFunParam()).append(")");
                                        }
                                        makItem.append("'>");

                                        if("init".equals(initType)) {
                                                makItem.append("<option value='' selected>선택없음</option>");
                                        }
                                        makItem.append("<option value='' selected>선택없음</option>");
                                        for(Map<String, Object> selectSrc : selectSrcList){

                                                log.debug("selectBoxSrc : " + selectSrc);
                                                log.debug("selectBoxSrc.toString() : " + selectSrc.toString());
                                                String selectedAttribute = (selectSrc.get("item_value").equals(ckeckValue)) ? " selected" : "";

                                                makItem.append("<option value='").append(selectSrc.get("item_value")).append("'").append(selectedAttribute).append(">").append(selectSrc.get("item_text")).append("</option>");
                                        }
                                        makItem.append("</select>");
                                }else if("radioBox".equals(makeBox.getBoxType())) {

                                        for(Map<String, Object> selectSrc : selectSrcList){
                                                log.debug("selectBoxSrc : " + selectSrc);
                                                log.debug("selectBoxSrc.toString() : " + selectSrc.toString());
                                                String selectedAttribute = (selectSrc.get("item_value").equals(ckeckValue)) ? " selected" : "";
                                                makItem.append("<input type='radio' id='").append(makeBox.getMakeId()).append("' value='").append(selectSrc.get("item_value")).append("' ").append(selectedAttribute).append(">").append(selectSrc.get("item_text"));
                                        }
                                }

                                log.debug("=====================================================================================");
                                log.debug("                                  셀렉트 박스 내용                                   ");
                                log.debug(makItem.toString());
                                log.debug("=====================================================================================");
                                log.debug("변수 명 : " + makeBox.getParamNm());
                                log.debug("=====================================================================================");

                                returnMap.put(makeBox.getParamNm(), makItem.toString());
                        }
                }

                return Result.ok().put("returnMap", returnMap);
        }

        @PostMapping("/dailyreport/site-labor-list")
        @Description(name = "작업일지 인력 정보 조회", description = "작업일지 인력 정보 조회", type = Description.TYPE.MEHTOD)
        public Result getDailyReportLabor(CommonReqVo commonReqVo, @RequestBody Map<String, String> paramMap) throws IOException {

                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.FUNCTION.name());
                userLog.setExecType("작업일지 인력 정보 조회");
                systemLogComponent.addUserLog(userLog);

                String cntrctNo = paramMap.get("cntrctNo");
                String dailyReportId = paramMap.get("dailyReportId");
                String searchText = paramMap.get("searchText");
                return Result.ok()
                        .put("siteLaborList", dailyreportComponent.getDailyReportSiteLaborList(cntrctNo, dailyReportId, searchText));
        }

        // 작업일지 게시물 존재 여부 확인
        @PostMapping("/dailyreport/report-exists")
        @Description(name = "작업일지 게시물 존재 여부 확인", description = "작업일지 게시물 존재 여부 확인", type = Description.TYPE.MEHTOD)
        public Result checkDailyReportExists(CommonReqVo commonReqVo,
                                             @RequestBody Map<String, String> paramMap) throws IOException {

                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.FUNCTION.name());
                userLog.setExecType("작업일지 게시물 존재 여부 확인");
                systemLogComponent.addUserLog(userLog);

                String dailyReportDate = paramMap.get("dailyReportDate");
                String cntrctNo = paramMap.get("cntrctNo");
                log.info("checkDailyReportExists: 작업일지 게시물 존재 여부 확인. params = {}", paramMap);
                return Result.ok()
                        .put("dailyReportExists", dailyreportComponent.checkDailyReportExists(cntrctNo, dailyReportDate));
        }



        // 작업일지 현장 작업자 인력 저장
        @PostMapping("/dailyreport/save-manual-cbs-resource")
        @Description(name = "작업일지 자원 수동 추가", description = "작업일지 자원 수동 추가", type = Description.TYPE.MEHTOD)
        public Result saveManualCbsResource(CommonReqVo commonReqVo,
                                    @RequestBody @Valid DailyreportForm.SaveResourceRequest saveResourceRequest) throws IOException {

                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.FUNCTION.name());
                userLog.setExecType("작업일지 자원 수동 추가");
                systemLogComponent.addUserLog(userLog);
                List<DailyreportForm.ManualDailyReportResource> list = saveResourceRequest.getResourceList();

                log.info("checkDailyReportExists: 작업일지 현장 작업자 인력 저장. params = {}", list);
                dailyreportComponent.saveResourceSummaryManually(list);
                return Result.ok();
        }

        // 작업일지 공종자원 조회
        @PostMapping("/dailyreport/cbs-resource-summary-list")
        @Description(name = "작업일지 공종 자원 조회", description = "작업일지 공종 자원 조회", type = Description.TYPE.MEHTOD)
        public Result getCbsResourceSummary(CommonReqVo commonReqVo, @RequestBody Map<String, String> paramMap) throws IOException {

                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.FUNCTION.name());
                userLog.setExecType("작업일지 공종 자원 조회");
                systemLogComponent.addUserLog(userLog);

                String cntrctNo = paramMap.get("cntrctNo");
                String rsceTpCd = paramMap.get("rsceTpCd");
                String dailyReportId = paramMap.get("dailyReportId");
                String searchText = paramMap.get("searchText");

                log.info("getCbsResourceSummary: 작업일지 공종 자원 조회 cntrctNo = {}, rsceTpCd = {}", cntrctNo, rsceTpCd);

                return Result.ok().put("cbsResourceSummaryList", dailyreportComponent.getCbsResourceSummaryList(cntrctNo, rsceTpCd, dailyReportId, searchText));
        }

         /**
         * 작업일지 승인 취소
         *
         * @param commonReqVo
         * @return
         */
        @PostMapping("/dailyreport/cancel-approval")
        @Description(name = "작업일지 승인 취소", description = "작업일지 승인 취소 및 연계 데이터 초기화", type = Description.TYPE.MEHTOD)
        public Result cancelDailyReportApproval(CommonReqVo commonReqVo, @RequestBody @Valid DailyreportForm.DailyReportList dailyreport) {
                
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.FUNCTION.name());
                userLog.setExecType("작업일지 승인 취소");

                Result response = Result.ok();

                try {
                        Map<String, Object> paramMap = new HashMap<>();
                        paramMap.put("dailyReportList", dailyreport.getDailyReportList());
                        paramMap.put("apiYn", commonReqVo.getApiYn());
                        paramMap.put("pjtDiv", commonReqVo.getPjtDiv());
                        
                        dailyreportComponent.cancelDailyReportApproval(paramMap);
                        userLog.setResult("SUCCESS");
                } catch (GaiaBizException e) {
                        log.error("작업일지 승인 취소 중 오류 발생, 오류 메세지 = " + e.getMessage());
                        userLog.setResult("FAIL");
                        userLog.setErrorReason(e.getMessage());
                        response = response.put("result", "FailCancelApproval");
                }
                return response;
        }
}

package kr.co.ideait.platform.gaiacairos.web.entrypoint.system;

import jakarta.validation.Valid;
import kr.co.ideait.platform.gaiacairos.comp.system.CommonCodeComponent;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmComCode;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmComCodeGroup;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.commoncode.CommonCodeMybatisParam.CommonCodeListInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.commoncode.CommonCodeDto;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.commoncode.CommonCodeForm;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.GridResult;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/system/common-code")
public class CommonCodeApiController extends AbstractController {

        @Autowired
        CommonCodeComponent commonCodeComponent;

        @Autowired
        CommonCodeForm commonCodeForm;

        @Autowired
        CommonCodeDto commonCodeDto;

        // 코드그룹---------------------------------------------------------------------------------------------------

        /**
         * 코드그룹 리스트 조회
         */
        @GetMapping("/group/list")
        @Description(name = "공통코드그룹 목록 조회", description = "공통코드그룹 목록 조회", type = Description.TYPE.MEHTOD)
        public Result getCommonCodeGroupList(CommonReqVo commonReqVo) {
                // 공통로그
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.FUNCTION.name());
                userLog.setExecType("공통코드그룹 목록 조회");
                systemLogComponent.addUserLog(userLog);

                return Result.ok()
                                .put("groupCodeList",
                                                commonCodeComponent.getCommonCodeGroupList());
        }

        /**
         * 코드그룹 조회
         */
        @GetMapping("/group/{cmnGrpNo}")
        @Description(name = "공통코드그룹 상세조회", description = "공통코드그룹 상세조회", type = Description.TYPE.MEHTOD)
        public Result getCommonCodeGroup(CommonReqVo commonReqVo, @PathVariable("cmnGrpNo") int cmnGrpNo) {
                // 공통로그
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.FUNCTION.name());
                userLog.setExecType("공통코드그룹 상세조회");
                systemLogComponent.addUserLog(userLog);

                return Result.ok()
                                .put("groupCode",
                                                commonCodeDto.fromSmComCodeGroup(
                                                                commonCodeComponent.getCommonCodeGroup(cmnGrpNo)));
        }

        /**
         * 그룹코드 중복 체크
         */
        @GetMapping("/group/exist/{upCmnGrpNo}/{cmnCd}")
        @Description(name = "공통코드그룹 코드 중복 체크", description = "공통코드그룹 코드 중복 체크", type = Description.TYPE.MEHTOD)
        public Result existCommonCodeGroup(CommonReqVo commonReqVo, @PathVariable("upCmnGrpNo") Integer upCmnGrpNo, @PathVariable("cmnCd") String cmnCd) {
                // 공통로그
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.FUNCTION.name());
                userLog.setExecType("공통코드그룹 코드 중복 체크");
                systemLogComponent.addUserLog(userLog);

                return Result.ok()
                                .put("exist", commonCodeComponent.existCommonCodeGroup(upCmnGrpNo, cmnCd));
        }

        /**
         * 그룹코드 생성
         */
        @PostMapping("/group/create")
        @Description(name = "공통코드그룹 추가", description = "공통코드그룹 추가", type = Description.TYPE.MEHTOD)
        public Result createCommonCodeGroup(CommonReqVo commonReqVo, @RequestBody @Valid CommonCodeForm.CommonCodeGroup CommonCodeGroup) {
                // 공통로그
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.FUNCTION.name());
                userLog.setExecType("공통코드그룹 추가");
                systemLogComponent.addUserLog(userLog);

                SmComCodeGroup smComCodeGroup = commonCodeForm.toSmComCodeGroup(CommonCodeGroup);
                return Result.ok()
                                .put("groupCode",
                                                commonCodeComponent.createCommonCodeGroup(smComCodeGroup, commonReqVo)
                                                                .map(commonCodeDto::fromSmComCodeGroup));
        }

        /**
         * 그룹코드 수정
         */
        @PostMapping("/group/update")
        @Description(name = "공통코드그룹 수정", description = "공통코드그룹 수정", type = Description.TYPE.MEHTOD)
        public Result updateCommonCodeGroup(CommonReqVo commonReqVo, 
                        @RequestBody @Valid CommonCodeForm.CommonCodeGroupUpdate commonCodeGroupUpdate) {
                // 공통로그
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.FUNCTION.name());
                userLog.setExecType("공통코드그룹 수정");
                systemLogComponent.addUserLog(userLog);

                SmComCodeGroup smComCodeGroup = commonCodeComponent
                                .getCommonCodeGroup(commonCodeGroupUpdate.getCmnGrpNo());
                if (smComCodeGroup != null) {
                        commonCodeForm.updateSmComCodeGroup(commonCodeGroupUpdate, smComCodeGroup);
                        return Result.ok()
                                        .put("groupCode", commonCodeComponent.updateCommonCodeGroup(smComCodeGroup, commonReqVo)
                                                        .map(commonCodeDto::fromSmComCodeGroup));
                }
                throw new GaiaBizException(ErrorType.NO_DATA);
        }

        /**
         * 그룹코드 삭제
         */
        @PostMapping("/group/delete")
        @Description(name = "공통코드그룹 삭제", description = "공통코드그룹 삭제", type = Description.TYPE.MEHTOD)
        public Result deleteCommonCodeGroup(CommonReqVo commonReqVo, 
                        @RequestBody @Valid CommonCodeForm.CommonCodeGroupNoList commonCodeGroupNoList) {
                // 공통로그
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.FUNCTION.name());
                userLog.setExecType("공통코드그룹 삭제");
                systemLogComponent.addUserLog(userLog);

                commonCodeComponent.deleteCommonCodeGroup(commonCodeGroupNoList.getCmnGrpCdList(), commonReqVo);
                return Result.ok();
        }

        /**
         * 그룹코드 위로 이동
         */
        @PostMapping("/group/move-up")
        @Description(name = "공통코드그룹 위로 이동", description = "공통코드그룹 순서 한단계 위로 변경", type = Description.TYPE.MEHTOD)
        public Result groupUp(CommonReqVo commonReqVo, @RequestBody @Valid CommonCodeForm.GroupMove groupMove) {
                // 공통로그
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.FUNCTION.name());
                userLog.setExecType("공통코드그룹 위로 이동");
                systemLogComponent.addUserLog(userLog);

                SmComCodeGroup smComCodeGroup = commonCodeForm.toSmComCodeGroup(groupMove);
                boolean result = commonCodeComponent.upGroup(smComCodeGroup, commonReqVo);
                if (result) {
                        return Result.ok();
                } else {
                        return Result.nok(ErrorType.DATABSE_ERROR, "최상위 위치입니다.");
                }
        }

        /**
         * 그룹코드 아래로 이동
         */
        @PostMapping("/group/move-down")
        @Description(name = "공통코드그룹 아래로 이동", description = "공통코드그룹 순서 한단계 아래로 변경", type = Description.TYPE.MEHTOD)
        public Result groupDown(CommonReqVo commonReqVo, @RequestBody @Valid CommonCodeForm.GroupMove groupMove) {
                // 공통로그
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.FUNCTION.name());
                userLog.setExecType("공통코드그룹 아래로 이동");
                systemLogComponent.addUserLog(userLog);

                SmComCodeGroup smComCodeGroup = commonCodeForm.toSmComCodeGroup(groupMove);
                boolean result = commonCodeComponent.downGroup(smComCodeGroup, commonReqVo);
                if (result) {
                        return Result.ok();
                } else {
                        return Result.nok(ErrorType.DATABSE_ERROR, "최하위 위치입니다.");
                }
        }

        // 코드---------------------------------------------------------------------------------------------------

        /**
         * 코드리스트 조회(그리드용)
         */
        @GetMapping("/codeList")
        @Description(name = "Grid용 공통코드 목록 조회", description = "Grid용 공통코드 목록 조회", type = Description.TYPE.MEHTOD)
        public GridResult getCommonCodeList(CommonReqVo commonReqVo, @Valid CommonCodeForm.CommonCodeSearch commonCodeSearch) { // 컬럼
                // 공통로그
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.FUNCTION.name());
                userLog.setExecType("Grid용 공통코드 목록 조회");
                systemLogComponent.addUserLog(userLog);

                CommonCodeListInput commonCodeListInput = commonCodeForm.toCommonCodeListInput(commonCodeSearch);
                return GridResult.ok(
                                commonCodeComponent.getCommonCodeListGrid(commonCodeListInput));
        }

        /**
         * 코드리스트 조회(일반)
         */
        @GetMapping("/codeList/{cmnGrpNo}")
        @Description(name = "공통코드 목록 조회", description = "공통코드 목록 조회", type = Description.TYPE.MEHTOD)
        public Result getCommonCode(CommonReqVo commonReqVo, @PathVariable("cmnGrpNo") Integer cmnGrpNo,
                        @Valid CommonCodeForm.CommonCodeSearch commonCodeSearch) { // 컬럼
                // 공통로그
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.FUNCTION.name());
                userLog.setExecType("공통코드 목록 조회");
                systemLogComponent.addUserLog(userLog);

                CommonCodeListInput commonCodeListInput = commonCodeForm.toCommonCodeListInput(commonCodeSearch);
                return Result.ok()
                                .put("codeList",
                                                commonCodeComponent.getCommonCodeList(commonCodeListInput).stream()
                                                                .map(commonCodeDto::fromSmComCodeMybatis));
        }

        /**
         * 코드 개별 조회
         */
        @GetMapping("/code/{cmnCdNo}")
        @Description(name = "공통코드 상세조회", description = "공통코드 상세조회", type = Description.TYPE.MEHTOD)
        public Result getCommonCode(CommonReqVo commonReqVo, @PathVariable("cmnCdNo") String cmnCdNo) {
                // 공통로그
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.FUNCTION.name());
                userLog.setExecType("공통코드 상세조회");
                systemLogComponent.addUserLog(userLog);

                return Result.ok().put("code", commonCodeComponent.getCommonCodeLoadData(cmnCdNo));
        }

        /**
         * 코드콤보 조회
         */
        @GetMapping("/code-combo/{cmnGrpCd}")
        @Description(name = "공통코드 리스트 조회", description = "콤보박스 요소 데이터 설정을 위한 공통코드 데이터 리스트 조회", type = Description.TYPE.MEHTOD)
        public Result getCommonCode(CommonReqVo commonReqVo, @PathVariable("cmnGrpCd") String cmnGrpCd,
                        @CookieValue(name = "lang", required = false, defaultValue = "ko") String langInfo) {
                // 공통로그
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.FUNCTION.name());
                userLog.setExecType("공통코드 리스트 조회");
                systemLogComponent.addUserLog(userLog);

                return Result.ok()
                                .put("codeCombo", commonCodeComponent.getCommonCodeListByGroupCode(cmnGrpCd).stream()
                                                .map(smComCode -> {
                                                        CommonCodeDto.CommonCodeCombo codeCombo = commonCodeDto
                                                                        .fromSmComCodeToCombo(smComCode);
                                                        codeCombo.setCmnCdNm(langInfo.equals("en")
                                                                        ? smComCode.getCmnCdNmEng()
                                                                        : smComCode.getCmnCdNmKrn());
                                                        return codeCombo;
                                                }));
        }

        /**
         * 코드콤보 리스트 조회
         */
        @GetMapping("/code-combo/map")
        @Description(name = "공통코드 리스트 조회", description = "콤보박스 요소 데이터 설정을 위한 공통코드 데이터 맵 형태로 반환.", type = Description.TYPE.MEHTOD)
        public Result getCommonCodeList(CommonReqVo commonReqVo, @Valid CommonCodeForm.CommonCodeList commonCodeNoList) {
                // 공통로그
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.FUNCTION.name());
                userLog.setExecType("공통코드 리스트 조회");
                systemLogComponent.addUserLog(userLog);

                Map<String, Stream<CommonCodeDto.CommonCodeCombo>> comboMap = new HashMap<>();
                commonCodeNoList.getCmnCdList().forEach(code -> {
                        comboMap.put(code, commonCodeComponent.getCommonCodeListByGroupCode(code).stream()
                                        .map(commonCodeDto::fromSmComCodeToCombo));
                });
                return Result.ok().put("codeComboMap", comboMap);
        }

        /**
         * 코드 중복 체크
         */
        @GetMapping("/code/exist/{groupCodeCd}/{code}")
        @Description(name = "공통코드 중복 체크", description = "공통코드 중복 체크", type = Description.TYPE.MEHTOD)
        public Result existCommonCode(CommonReqVo commonReqVo, @PathVariable("groupCodeCd") String groupCodeCd, @PathVariable("code") String code) {
                // 공통로그
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.FUNCTION.name());
                userLog.setExecType("공통코드 중복 체크");
                systemLogComponent.addUserLog(userLog);

                return Result.ok()
                                .put("exist", commonCodeComponent.existCommonCode(groupCodeCd, code));
        }

        /**
         * 코드 생성
         */
        @PostMapping("/code/create")
        @Description(name = "공통코드 추가", description = "공통코드 추가", type = Description.TYPE.MEHTOD)
        public Result createCommonCode(CommonReqVo commonReqVo, @RequestBody @Valid CommonCodeForm.CommonCode commomCode) {
                // 공통로그
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.FUNCTION.name());
                userLog.setExecType("공통코드 추가");
                systemLogComponent.addUserLog(userLog);

                return Result.ok().put("code", commonCodeComponent.createCommonCode(commonCodeForm.toSmComCode(commomCode), commonReqVo));
        }

        /**
         * 코드 수정
         */
        @PostMapping("/code/update")
        @Description(name = "공통코드 수정", description = "공통코드 수정", type = Description.TYPE.MEHTOD)
        public Result updateCommonCode(CommonReqVo commonReqVo, @RequestBody @Valid CommonCodeForm.CommonCodeUpdate commomCode) {
                // 공통로그
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.FUNCTION.name());
                userLog.setExecType("공통코드 수정");
                systemLogComponent.addUserLog(userLog);

                SmComCode smComCode = commonCodeComponent.getCommonCode(commomCode.getCmnCdNo(), commomCode.getCmnCd()); // DB 코드 조회

                if (smComCode != null) { // 데이터가 있는지 확인
                        commonCodeForm.updateSmComCode(commomCode, smComCode); // 전달된 내용만 매핑
                        return Result.ok()
                                        .put("code", commonCodeComponent.updateCommonCode(smComCode, commonReqVo) // DB 업데이트
                                                        .map(commonCodeDto::fromSmComCode)); // dto 변환
                }
                throw new GaiaBizException(ErrorType.NO_DATA);

        }

        /**
         * 코드 순서 수정
         */
        @PostMapping("/code/update-order")
        @Description(name = "공통코드 순서 수정", description = "공통코드 순서 수정", type = Description.TYPE.MEHTOD)
        public Result updateCommonCodeOrder(CommonReqVo commonReqVo, @RequestBody @Valid CommonCodeForm.CommonCodeNoList commomCodeNoList) {
                // 공통로그
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.FUNCTION.name());
                userLog.setExecType("공통코드 순서 수정");
                systemLogComponent.addUserLog(userLog);

                commonCodeComponent.updateCommonCodeOrder(commomCodeNoList.getCmnCdList(), commonReqVo);
                return Result.ok();

        }

        /**
         * 코드 삭제
         */
        @PostMapping("/code/delete")
        @Description(name = "공통코드 삭제", description = "공통코드 삭제", type = Description.TYPE.MEHTOD)
        public Result deleteCommonCode(CommonReqVo commonReqVo, @RequestBody @Valid CommonCodeForm.CommonCodeNoList cmnCdList) {
                // 공통로그
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.FUNCTION.name());
                userLog.setExecType("공통코드 삭제");
                systemLogComponent.addUserLog(userLog);

                commonCodeComponent.deleteCommonCode(cmnCdList.getCmnCdList(), commonReqVo);
                return Result.ok();
        }

        /**
         * 코드콤보 여러개 조회
         */
        @GetMapping("/code-combo-list")
        @Description(name = "공통코드 리스트 다중 조회", description = "여러 개의 콤보박스 요소 데이터 설정을 위한 공통코드 데이터 리스트 다중 조회", type = Description.TYPE.MEHTOD)
        public Result getCommonCode(CommonReqVo commonReqVo, @RequestParam("cmnGrpCdList") List<String> cmnGrpCdList,
                        @CookieValue(name = "lang", required = false, defaultValue = "ko") String langInfo) {
                // 공통로그
                Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
                userLog.setLogType(LogType.FUNCTION.name());
                userLog.setExecType("공통코드 리스트 다중 조회");
                systemLogComponent.addUserLog(userLog);

                return Result.ok()
                                .put("codeComboMap",
                                                commonCodeComponent.getCommonCodeListByGroupCode(cmnGrpCdList, langInfo));
        }

}

package kr.co.ideait.platform.gaiacairos.web.entrypoint.system;

import jakarta.validation.Valid;
import kr.co.ideait.platform.gaiacairos.comp.system.MenuAuthorityGroupComponent;
import kr.co.ideait.platform.gaiacairos.comp.system.UserComponent;
import kr.co.ideait.platform.gaiacairos.comp.system.service.CommonCodeService;
import kr.co.ideait.platform.gaiacairos.comp.system.service.MenuAuthorityGroupService;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmMenuAuthorityGroup;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.MenuAuthorityGorupMybatisParam;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.commoncode.CommonCodeDto;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.menuauthoritygroup.MenuAuthorityGroupForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.menuauthoritygroup.MenuAuthotiryGroupDto;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.config.security.annotation.IsUser;
import kr.co.ideait.platform.gaiacairos.core.constant.CommonCodeConstants;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.GridResult;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@IsUser
@RestController
@RequestMapping("/api/system/menu-authority-group")
public class MenuAuthorityGroupApiController extends AbstractController {
	
	@Autowired
	MenuAuthorityGroupComponent menuAuthorityGroupComponent;

    @Autowired
    MenuAuthorityGroupService menuAuthorityGroupService;

    @Autowired
    MenuAuthorityGroupForm menuAuthorityGroupForm;

    @Autowired
    MenuAuthotiryGroupDto menuAuthorityGroupDto;

    @Autowired
    CommonCodeService commonCodeService;

    @Autowired
    CommonCodeDto commonCodeDto;

    @Value("${spring.application.name}")
    String pjtType;

    /**
    * 메뉴 권한 리스트 조회 - by jiyoung
    */
    @GetMapping("/list")
    @Description(name = "메뉴 권한 리스트 조회", description = "",type = Description.TYPE.MEHTOD)
    public Result getProjectuBillingList(CommonReqVo commonReqVo, @Valid MenuAuthorityGroupForm.MenuAuthorityGroupListGet info,
                                         @CookieValue(name = "lang", required = false) String langInfo) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("메뉴 권한 리스트 조회");
        systemLogComponent.addUserLog(userLog);

        log.debug("==============================================");
        log.debug("getProjectuBillingList : " + info.getMenuCd() + " // " + info.getCntrctNo() + " // " + info.getSystemType() + " // " + info.getSearchText());
    	log.debug("==============================================");

        MenuAuthorityGorupMybatisParam.MenuAuthorityGroupListInput input = new MenuAuthorityGorupMybatisParam.MenuAuthorityGroupListInput();

		if(langInfo == null){
            langInfo = "ko";
        }
		input.setLang(langInfo);
        input.setCntrctNo(info.getCntrctNo());
         input.setSystemType(pjtType);
        input.setMenuCd(info.getMenuCd());
        input.setSearchText(info.getSearchText());
        input.setATypeCode(CommonCodeConstants.ATYPE_CODE_GROUP_CODE);
        input.setRoleCode(CommonCodeConstants.ROLE_CODE_GROUP_CODE);
        input.setKindCode(CommonCodeConstants.AKIND_CODE_GROUP_CODE);

        return Result.ok().put("authList", menuAuthorityGroupService.getMenuAuthorityGroupList(input).stream()
		.map(menuAuthorityGroupDto::fromMybatisOutput));
    }

    /**
    * 메뉴 권한 리스트 조회(Grid 페이징)
    */
    @GetMapping("/grid-list")
    @Description(name = "메뉴 권한 리스트 조회", description = "메뉴 권한 리스트 조회 - tuiGrid 반환 구조에 맞춰 반환.", type = Description.TYPE.MEHTOD)
    public GridResult getMenuAuthList(CommonReqVo commonReqVo, @Valid MenuAuthorityGroupForm.MenuAuthorityGroupListGet info,
                                        @CookieValue(name = "lang", required = false, defaultValue = "ko") String langInfo) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("메뉴 권한 리스트 조회");
        systemLogComponent.addUserLog(userLog);

        log.debug("==============================================");
	    log.debug("getMenuAuthList : " + info.getMenuCd() + " // " + info.getCntrctNo() + " // " + info.getSystemType() + " // " + info.getSearchText());
    	log.debug("==============================================");

        MenuAuthorityGorupMybatisParam.MenuAuthorityGroupListInput input = new MenuAuthorityGorupMybatisParam.MenuAuthorityGroupListInput();

		if(langInfo != null && "ko-KR".equals(langInfo)){
            langInfo = "ko";
        }

		input.setLang(langInfo);
        input.setCntrctNo(info.getCntrctNo());
        input.setMenuCd(info.getMenuCd());
        input.setSystemType(pjtType);
        input.setSearchText(info.getSearchText());
        input.setColumnNm(info.getColumnNm());
        input.setPageable(info.getPageable());
        input.setATypeCode(CommonCodeConstants.ATYPE_CODE_GROUP_CODE);
        input.setRoleCode(CommonCodeConstants.ROLE_CODE_GROUP_CODE);
        input.setKindCode(CommonCodeConstants.KIND_CODE_GROUP_CODE);

        // menuAuthorityGroupService에서 Page 타입으로 반환
        Page<MybatisOutput> page = menuAuthorityGroupService.getMenuAuthorityGroupPage(input);

        return GridResult.ok(page.map(menuAuthorityGroupDto::fromMybatisOutput));

    }

    
    /**
     * 메뉴 권한 생성
     * @param menuAuthorityGroupList
     * @return
     */
    // @RequiredProjectSelect(superChangeable = true)
    @PostMapping("/create")
    @Description(name = "메뉴 권한그룹 추가", description = "메뉴에 권한그룹을 추가한다.", type = Description.TYPE.MEHTOD)
    public Result createMenuAuthority(CommonReqVo commonReqVo, 
        @Valid @RequestBody MenuAuthorityGroupForm.MenuAuthorityGroupCreateList menuAuthorityGroupList) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("메뉴 권한그룹 추가");
        systemLogComponent.addUserLog(userLog);

        List<SmMenuAuthorityGroup> SmMenuAuthorityGroupList = menuAuthorityGroupForm
            .toSmMenuAuthorityGroupList(menuAuthorityGroupList.getMenuAuthorityGroupList());
        
        return Result.ok().put("result",  menuAuthorityGroupComponent.insertMenuAuthority(SmMenuAuthorityGroupList));
    }

    /**
     * 메뉴 권한 수정
     * @param menuAuthorityGroupUpdate
     * @return
     */
    // @RequiredProjectSelect(superChangeable = true)
    @PostMapping("/update")
    @Description(name = "메뉴권 그룹권한 수정", description = "메뉴의 그룹권한을 수정한다.", type = Description.TYPE.MEHTOD)
    public Result updateMenuAuthority(CommonReqVo commonReqVo, 
        @Valid @RequestBody MenuAuthorityGroupForm.MenuAuthorityGroupRghtKindUpdate menuAuthorityGroupRghtKindUpdate) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("메뉴권한 그룹권한 수정");
        systemLogComponent.addUserLog(userLog);
        
        return Result.ok().put("updateCnt", menuAuthorityGroupComponent.updateMenuAuthority(menuAuthorityGroupRghtKindUpdate));
    }
        
        
    /**
     * 메뉴 권한 다중 삭제
     * @param menuAuthorityGroupDeleteList
     * @return
     */
    // @RequiredProjectSelect(superChangeable = true)
    @PostMapping("/listDelete")
    @Description(name = "메뉴 그룹권한 삭제", description = "메뉴의 그룹권한을 삭제한다.", type = Description.TYPE.MEHTOD)
    public Result deleteMenuAuthorityGroupList(CommonReqVo commonReqVo, 
            @Valid @RequestBody MenuAuthorityGroupForm.MenuAuthorityGroupDeleteList menuRghtNoList) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("메뉴 권한 삭제");
        systemLogComponent.addUserLog(userLog);

        menuAuthorityGroupComponent.deleteMenuAuthority(menuRghtNoList);
        return Result.ok();
    }
    
    /**
     * 메뉴 권한 단건 삭제
     * @param menuAuthorityGroupDeleteList
     * @return
     */
    // @RequiredProjectSelect(superChangeable = true)
    @PostMapping("/Delete")
    @Description(name = "메뉴 그룹권한 삭제", description = "메뉴의 그룹권한을 삭제한다.", type = Description.TYPE.MEHTOD)
    public Result deleteMenuAuthorityGroup(CommonReqVo commonReqVo, 
            @Valid @RequestBody MenuAuthorityGroupForm.MenuAuthorityGroupDelete menuAuthorityGroupDelete) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("메뉴 권한 삭제");
        systemLogComponent.addUserLog(userLog);

        String userId = commonReqVo.getUserId();
        
        String delKey[] = menuAuthorityGroupDelete.getMenuAuthorityGroupDelete().get(0).split(",");;

        menuAuthorityGroupService.deleteMenuAuthorityGroup(delKey[0], delKey[1], userId);
        return Result.ok();
    }

	/**
     * 메뉴권한 추가 > 권한 그룹 리스트 조회
     * @param info
     * @param langInfo
     * @return
     */
    // @RequiredProjectSelect(superChangeable = false)
    @GetMapping("/auth-group/list")
    @Description(name = "메뉴 권한 그룹 리스트 조회", description = "메뉴 권한 추가 화면에서 기 등록된 권한 그룹 리스트 조회", type = Description.TYPE.MEHTOD)
    public Result getMenuAuthForAuthorityGroupList(CommonReqVo commonReqVo, @Valid MenuAuthorityGroupForm.MenuAuthorityGroupListGet info,
                @CookieValue(name = "lang", required = false, defaultValue = "ko") String lang) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("메뉴 권한 그룹 리스트 조회");
        systemLogComponent.addUserLog(userLog);


        // lang 처리
        String langInfo = ("ko-KR".equals(lang)) ? "ko" : lang;

		MenuAuthorityGorupMybatisParam.MenuAuthorityGroupListInput input = new MenuAuthorityGorupMybatisParam.MenuAuthorityGroupListInput();
		input.setCntrctNo(info.getCntrctNo());
		input.setMenuCd(info.getMenuCd());
		input.setATypeCode(CommonCodeConstants.ATYPE_CODE_GROUP_CODE);
		input.setRoleCode(CommonCodeConstants.ROLE_CODE_GROUP_CODE);
        input.setColumnNm(info.getColumnNm());
        input.setSearchText(info.getSearchText());
        input.setLang(langInfo);

        //권한 종류 콤보박스 옵션 데이터 조회
//        Stream<Object> optionData = commonCodeService.getCommonCodeListByGroupCode(CommonCodeConstants.AKIND_CODE_GROUP_CODE).stream().map(smComCode -> {
        commonCodeService.getCommonCodeListByGroupCode(CommonCodeConstants.AKIND_CODE_GROUP_CODE).stream().map(smComCode -> {
										CommonCodeDto.CommonCodeCombo codeCombo = commonCodeDto.fromSmComCodeToCombo(smComCode);
										codeCombo.setCmnCdNm("en".equals(langInfo) ? smComCode.getCmnCdNmEng() : smComCode.getCmnCdNmKrn());
										return codeCombo;
									});
		
        return Result.ok()
                .put("authorityGroupList",
					menuAuthorityGroupService.getAuthorityGroupList(input).stream()
                                .map(menuAuthorityGroupDto::fromMybatisOutput))
                .put("authorityList", menuAuthorityGroupService.getMenuAuthorityList(info.getMenuCd()));
    }
    
    /**
     * 메뉴별 권한 목록 가져오기(등록 수정시 선택된 메뉴에 설저되어 있는 모든 권한 목록 가져오기)
     */
     @GetMapping("/selectMenu-authorityList")
     @Description(name = "메뉴별 권한 목록조회", description = "선택한 메뉴의 설정 권한 목록을 가져온다.", type = Description.TYPE.MEHTOD)
     public Result getselectMenuAuthorityList(CommonReqVo commonReqVo, @Valid MenuAuthorityGroupForm.selectMenuAuthorityForm param,
                                         @CookieValue(name = "lang", required = false, defaultValue = "ko") String langInfo) {
         // 공통로그
         Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
         userLog.setLogType(LogType.FUNCTION.name());
         userLog.setExecType("메뉴별 권한 목록조회");
         systemLogComponent.addUserLog(userLog);

         return Result.ok().put("authorityList", menuAuthorityGroupService.getMenuAuthorityList(param.getMenuCd()));

     }

}

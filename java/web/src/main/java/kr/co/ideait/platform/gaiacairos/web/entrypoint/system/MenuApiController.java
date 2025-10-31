package kr.co.ideait.platform.gaiacairos.web.entrypoint.system;

import jakarta.validation.Valid;
import kr.co.ideait.platform.gaiacairos.comp.system.MenuComponent;
import kr.co.ideait.platform.gaiacairos.comp.system.service.CommonCodeService;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmBilling;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmButtonAuthority;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmMenu;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.MenuMybatisParam;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.commoncode.CommonCodeDto;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.menu.MenuDto;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.menu.MenuForm;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.config.security.annotation.IsUser;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.GridResult;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@IsUser
@RestController
@RequestMapping("/api/system/menu")
public class MenuApiController extends AbstractController {

    @Autowired
    MenuComponent menuComponent;

    @Autowired
    CommonCodeService commonCodeService;

    @Autowired
    MenuForm menuForm;

    @Autowired
    MenuDto menuDto;

    @Autowired
    CommonCodeDto commonCodeDto;

    /**
     * 메뉴 리스트 조회
     */
    @GetMapping("/all-list")
    @Description(name = "메뉴 리스트 조회", description = "메뉴 리스트 조회", type = Description.TYPE.MEHTOD)
    public Result getMenuList(CommonReqVo commonReqVo) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("메뉴 리스트 조회");
        systemLogComponent.addUserLog(userLog);

        return Result.ok()
                .put("menuList",
                        menuComponent.getMenuList().stream()
                                .map(menuDto::fromSmMenu));
    }

    /**
     * 내 메뉴 리스트 조회
     */
    @GetMapping("/list")
    @Description(name = "내 메뉴 리스트 조회", description = "사용자가 가진 메뉴 권한에 속한 메뉴 리스트 조회", type = Description.TYPE.MEHTOD)
    public Result getMyMenuList(CommonReqVo commonReqVo, UserAuth user) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("내 메뉴 리스트 조회");
        systemLogComponent.addUserLog(userLog);

        return Result.ok()
                .put("menuList",
                        menuComponent.getMenuList(user.getCntrctNo(), user.getUsrId()).stream()
                                .map(menuDto::toMyMenu));
    }

    /**
     * 메뉴 유료기능리스트 조회
     */
    @GetMapping("/{menuCd}/billing")
    @Description(name = "메뉴 유료기능 리스트 조회", description = "해당 메뉴의 유료기능 리스트 조회", type = Description.TYPE.MEHTOD)
    public Result getMenuBillingList(CommonReqVo commonReqVo, @PathVariable("menuCd") String menuCd) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("메뉴 유료기능 리스트 조회");
        systemLogComponent.addUserLog(userLog);

        SmMenu smMenu = menuComponent.getMenu(menuCd);
        if (smMenu != null) {
            return Result.ok()
                    .put("billingList", menuComponent.getBillingList(smMenu.getMenuNo()).stream()
                            .map(menuDto::fromSmBillingMybatis));
        } else {
            throw new GaiaBizException(ErrorType.NO_DATA);
        }
    }

    /**
     * 메뉴 코드 중복 체크
     */
    @GetMapping("/exist/{menuCd}")
    @Description(name = "메뉴 코드 중복 체크", description = "메뉴 코드 중복 체크 (true / false)", type = Description.TYPE.MEHTOD)
    public Result existMenuCode(CommonReqVo commonReqVo, @PathVariable("menuCd") String menuCd) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("메뉴 코드 중복 체크");
        systemLogComponent.addUserLog(userLog);

        return Result.ok()
                .put("exist", menuComponent.existMenuCode(menuCd));
    }

    /**
     * 메뉴 등록
     */
    @PostMapping("/create")
    @Description(name = "메뉴 등록", description = "메뉴 등록", type = Description.TYPE.MEHTOD)
    public Result createMenu(CommonReqVo commonReqVo, @RequestBody @Valid MenuForm.Menu menu) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("메뉴 등록");
        systemLogComponent.addUserLog(userLog);

        SmMenu smMenu = menuForm.toSmMenu(menu);
        return Result.ok()
                .put("menu", menuComponent.createMenu(smMenu, commonReqVo)
                        .map(menuDto::fromSmMenu));
    }

    /**
     * 메뉴 수정
     */
    @PostMapping("/update")
    @Description(name = "메뉴 수정", description = "메뉴 수정", type = Description.TYPE.MEHTOD)
    public Result updateMenu(CommonReqVo commonReqVo, @RequestBody @Valid MenuForm.MenuUpdate menu) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("메뉴 수정");
        systemLogComponent.addUserLog(userLog);


        SmMenu smMenu = menuComponent.getMenu(menu.getMenuCd());
        //링크여부가 N이면 menuUrl 지우기
        if(menu.getLkYn().equals("N")){
            smMenu.setMenuUrl(null);
        }
        if (smMenu != null) {
            menuForm.updateSmMenu(menu, smMenu);
            return Result.ok()
                    .put("menu", menuComponent.updateMenu(smMenu, commonReqVo)
                            .map(menuDto::fromSmMenu));
        }
        throw new GaiaBizException(ErrorType.NO_DATA);
    }

    /**
     * 메뉴 삭제
     */
    @PostMapping("/delete")
    @Description(name = "메뉴 삭제", description = "메뉴 삭제", type = Description.TYPE.MEHTOD)
    public Result deleteMenu(CommonReqVo commonReqVo, @RequestBody @Valid MenuForm.MenuCdList menuCdList) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("메뉴 삭제");
        systemLogComponent.addUserLog(userLog);

        menuComponent.deleteMenuList(menuCdList.getMenuCdList(), commonReqVo);
        return Result.ok();

    }

    /**
     * 메뉴 위로 이동
     */
    @PostMapping("/move-up")
    @Description(name = "메뉴 위로 이동", description = "해당 메뉴 순서를 위로 이동", type = Description.TYPE.MEHTOD)
    public Result menuUp(CommonReqVo commonReqVo, @RequestBody @Valid MenuForm.MenuMove menuMove) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("메뉴 위로 이동");
        systemLogComponent.addUserLog(userLog);

        SmMenu smMenu = menuForm.toSmMenu(menuMove);
        boolean result = menuComponent.upMenu(smMenu, commonReqVo);
        if(result){
            return Result.ok();
        }else{
            return Result.nok(ErrorType.DATABSE_ERROR, "최상위 위치입니다.");
        }
    }

    /**
     * 메뉴 아래로 이동
     */
    @PostMapping("/move-down")
    @Description(name = "메뉴 아래로 이동", description = "해당 메뉴 순서를 아래로 이동", type = Description.TYPE.MEHTOD)
    public Result menuDown(CommonReqVo commonReqVo, @RequestBody @Valid MenuForm.MenuMove menuMove) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("메뉴 아래로 이동");
        systemLogComponent.addUserLog(userLog);

        SmMenu smMenu = menuForm.toSmMenu(menuMove);
        boolean result = menuComponent.downMenu(smMenu, commonReqVo);
        if(result){
            return Result.ok();
        }else{
            return Result.nok(ErrorType.DATABSE_ERROR, "최하위 위치입니다.");
        }
    }


    /**
     * 메뉴 유료기능 등록
     */
    @PostMapping("/billing/create")
    @Description(name = "메뉴 유료기능 등록", description = "메뉴 유료기능 등록", type = Description.TYPE.MEHTOD)
    public Result createBilling(CommonReqVo commonReqVo, @RequestBody @Valid MenuForm.Billing billing) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("메뉴 유료기능 등록");
        systemLogComponent.addUserLog(userLog);

        SmBilling smBilling = menuForm.toSmBilling(billing);
        boolean existBilling = menuComponent.existBilling(smBilling); //유료기능이 존재하는지 체크
        if(existBilling){
            return Result.nok(ErrorType.DATABSE_ERROR, "기능이 이미 존재합니다.");
        }
        return Result.ok()
                .put("billing",
                        menuComponent.createBilling(smBilling, commonReqVo)
                                .map(menuDto::fromSmBilling));
    }

    /**
     * 메뉴 유료기능 삭제
     */
    @PostMapping("/billing/delete")
    @Description(name = "메뉴 유료기능 삭제", description = "메뉴 유료기능 삭제", type = Description.TYPE.MEHTOD)
    public Result deleteBilling(CommonReqVo commonReqVo, @RequestBody @Valid MenuForm.MenuBillingNoList menuBillingNoList) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("메뉴 유료기능 삭제");
        systemLogComponent.addUserLog(userLog);

        menuComponent.deleteBilling(menuBillingNoList.getBilNoList(), commonReqVo);
        return Result.ok();
    }
    

    /**
     * 메뉴권한관리 추가 > 선택한 메뉴 계층리스트 조회
     * @param menuCd
     * @return
     */
    @GetMapping("/menu-auth/menu-breadcrumb")
    @Description(name = "메뉴권한관리 추가 > 선택한 메뉴 계층리스트 조회", description = "메뉴권한관리 추가 화면에서 breadcrumb 요소에 표시할 메뉴 계층 데이터 조회", type = Description.TYPE.MEHTOD)
    public Result getMenuBreadcrumb(CommonReqVo commonReqVo, @RequestParam("menuCd") String menuCd){
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("선택한 메뉴 계층리스트 조회");
        systemLogComponent.addUserLog(userLog);

        return Result.ok().put("menuBreadcrumb", menuComponent.getMenuBreadcrumb(menuCd, commonReqVo.getPlatform()).stream().map(menuDto::fromSmMenuMybatis));
    }

}

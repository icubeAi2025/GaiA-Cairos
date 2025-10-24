package kr.co.ideait.platform.gaiacairos.web.config.pebble;

import io.pebbletemplates.pebble.extension.Function;
import io.pebbletemplates.pebble.template.EvaluationContext;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import kr.co.ideait.platform.gaiacairos.comp.portal.service.PortalService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class BtnFunction implements Function {

    @Autowired
    PortalService portalService;

//    private final HttpServletRequest request;

//    public BtnFunction(HttpServletRequest request) {
//        this.request = request;
//    }

    @Override
    public List<String> getArgumentNames() {
        List<String> names = new ArrayList<>();
        names.add("userInfo");
        names.add("pjtInfo");
        names.add("menuCd");
        names.add("btnId");
        names.add("btnClass");
        names.add("btnFun");
        names.add("btnMsg");
        names.add("btnEtc");
        names.add("btnIcon");
        return names;
    }

    @Override
    public Object execute(Map<String, Object> args, PebbleTemplate self,
                          EvaluationContext context, int lineNumber) {


//        Cookie[] cookies = request.getCookies();
//        if (cookies != null) {
//            for (Cookie cookie : cookies) {
//                if (KeyConstants.PORTAL_X_AUTH_KEY.equals(cookie.getName()) ) {
//                    userInfo = cookie.getValue();
//                }
//
//                if (KeyConstants.GAIA_PROPECT_SELECTED_KEY.equals(cookie.getName()) ) {
//                    pjtInfo = cookie.getValue();
//                }
//
//            }
//        }

        boolean userInfoIsNull = StringUtils.isEmpty(String.valueOf(args.get("userInfo")));
        boolean pjtInfoIsNull = StringUtils.isEmpty(String.valueOf(args.get("pjtInfo")));
        if (userInfoIsNull || pjtInfoIsNull) { return ""; }

        String btnHtml = "";
        String menuCd = String.valueOf(args.get("menuCd"));
        String[] userParam = String.valueOf(args.get("userInfo")).split(":");
        String[] pjtParam = String.valueOf(args.get("pjtInfo")).split(":");
        String [] btnId = String.valueOf(args.get("btnId")).split(";");
        String [] btnClass = String.valueOf(args.get("btnClass")).split(";");
        String [] btnFun = String.valueOf(args.get("btnFun")).split(";");
        String [] btnMsg = String.valueOf(args.get("btnMsg")).split(";");
        String [] btnEtc = null;
        String [] btnIcon = null;
        String [] btnToolTip = null;
        String [] btnBlind = null;

        if (args.get("btnEtc") != null) btnEtc = String.valueOf(args.get("btnEtc")).split(";");
        if (args.get("btnIcon") != null) btnIcon = String.valueOf(args.get("btnIcon")).split(";");
        if (args.get("btnToolTip") != null) btnToolTip = String.valueOf(args.get("btnToolTip")).split(";");
        if (args.get("btnBlind") != null) btnBlind = String.valueOf(args.get("btnBlind")).split(";");


        if (btnEtc != null && btnIcon != null) {
            btnHtml = portalService.selectBtnAuthorityListWithIcon(userParam[1], userParam[0], userParam[2], pjtParam[0],
                    pjtParam[1], menuCd, btnId, btnClass, btnFun, btnEtc, btnIcon, btnToolTip, btnBlind);

            return btnHtml;
        }

        if (btnEtc != null) {
            btnHtml = portalService.selectBtnAuthorityList(userParam[1], userParam[0], userParam[2], pjtParam[0],
                    pjtParam[1], menuCd, btnId, btnClass, btnFun, btnMsg, btnEtc);

            return btnHtml;
        }

        btnHtml = portalService.selectBtnAuthorityList(userParam[1], userParam[0], userParam[2], pjtParam[0],
                pjtParam[1], menuCd, btnId, btnClass, btnFun, btnMsg);

        return btnHtml;
    }
}
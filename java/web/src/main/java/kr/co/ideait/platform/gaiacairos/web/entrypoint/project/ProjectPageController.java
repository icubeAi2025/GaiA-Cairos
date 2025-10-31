package kr.co.ideait.platform.gaiacairos.web.entrypoint.project;

import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.iframework.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/project")
public class ProjectPageController extends AbstractController {

    /**
     * 현장개설
     */
    @GetMapping("/project_install")
    @Description(name = "현장개설요청 화면", description = "현장개설요청 화면", type = Description.TYPE.MEHTOD)
    public String getMain(CommonReqVo commonReqVo) {
        return "page/project/project_install";
    }

    /**
     * 업체명 검색 모달창
     */
    @GetMapping("/corpNmSearch")
    @Description(name = "업체명 검색 모달창", description = "업체명 검색 모달창", type = Description.TYPE.MEHTOD)
    public String corpNmSearch(CommonReqVo commonReqVo) {
        return "page/project/search/corpNmSearch";
    }

    /**
     * 담당자명 검색 모달창
     */
    @GetMapping("/ofclNmSearch")
    @Description(name = "담당자명 검색 모달창", description = "담당자명 검색 모달창", type = Description.TYPE.MEHTOD)
    public String ofclNmSearch(CommonReqVo commonReqVo) {
        return "page/project/search/ofclNmSearch";
    }

    /**
     * 수요기관 검색 모달창
     */
    @GetMapping("/org-search")
    @Description(name = "수요기관 검색 모달창", description = "수요기관 검색 모달창", type = Description.TYPE.MEHTOD)
    public String orgSearch(CommonReqVo commonReqVo) {
        return "page/project/search/orgSearch";
    }

    /**
     * 사용요청 화면
     */
    @GetMapping("/useRequest/gaia")
    @Description(name = "사용요청 화면-GaiA", description = "사용요청 화면-GaiA", type = Description.TYPE.MEHTOD)
    public String useRequestG(CommonReqVo commonReqVo) {
        return "page/project/userequest-gaia";
    }

    @GetMapping("/useRequest/cairos")
    @Description(name = "사용요청 화면-CaiROS", description = "사용요청 화면-CaiROS", type = Description.TYPE.MEHTOD)
    public String useRequestC(CommonReqVo commonReqVo) {
        return "page/project/userequest-cairos";
    }
}

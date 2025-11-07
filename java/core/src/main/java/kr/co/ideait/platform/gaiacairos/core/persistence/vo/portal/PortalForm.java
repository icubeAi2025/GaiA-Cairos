package kr.co.ideait.platform.gaiacairos.core.persistence.vo.portal;

import jakarta.validation.constraints.NotBlank;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnProjectFavorites;
import kr.co.ideait.platform.gaiacairos.core.config.GlobalMapperConfig;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.CommonForm;
import lombok.Data;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = GlobalMapperConfig.class)
public interface PortalForm {

    CnProjectFavorites toCnProjectFavorites(SetFavoritesParam param);

    @Data
    class PortalLoginGet {
        String redirectUrl;
        String lastPage;
    }

    @Data
    class PortalLoginPost {
        @NotBlank
        String id;
        @NotBlank
        String loginType;
    }

    /**
     * 최초 메인 종합 프로젝트 화면의 조회 및 검색 파라미터
     */
    @Data
    class MainPortalTotalSearchPjtParam {
        String searchItem;
        String searchText;
        String favoritesSearch;
    }

    /**
     * 최초 메인 종합 프로젝트 화면의 즐겨찾기 셋팅 파라미터
     */
    @Data
    class SetFavoritesParam extends CommonForm {
        String pjtNo;
        String cntrctNo;
        String pjtType;
        String loginId;
        String favoritesYN;
    }

    /**
     * 최초 메인 종합 프로젝트 화면의 조회 및 검색 파라미터
     */
    @Data
    class MenuListParam {
        String pjtNo;
        String cntrctNo;
    }

    /**
     * 버튼권한리스트 가져오는 파라미터
     */
    @Data
    class BtnAuthorityParam {
        String menuCd;
        List<String> btnIdList;
        String pjtNo;
        String cntrctNo;
        String pjtType;
    }
    
    /**
     * GaiA / CaiROS 신규 사용신청 파라미터
     */
    @Data
    class NewUseReuestParam {
        String pjtNm;
        String jobNm;
        String usrId;
    }

    @Data
    class PortalMe {
        @NotBlank
        String id;
    }

    @Data
    class NavMenuInput {
        String menu_id;
    }
}

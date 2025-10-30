package kr.co.ideait.platform.gaiacairos.core.persistence.vo.portal;

import java.util.ArrayList;

import org.apache.ibatis.type.Alias;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisPageable;
import lombok.Data;

@Mapper(componentModel = ComponentModel.SPRING)
public interface PortalMybatisParam {

     /**
      * 로그인사용자정보 조회 Param
      */
     @Data
     @Alias("userLoginInput")
     public class UserLoginInput extends MybatisPageable {
          String loginId;

          String usrId;
          // 직책
          String cmnGrpCdPstn;
          // 직급
          String cmnGrpCdRank;
          // 직급
          String loginType;
          // 프로젝트 타입
          String pjtType;
     }

     /**
      * 종합메인화면 조회 Param
      */
     @Data
     @Alias("mainComprehensiveProjectInput")
     public class MainComprehensiveProjectInput extends MybatisPageable {
          String loginId;
          String usrId;
          String conPstatsCd;
          String majorCnsttyCd;
          String searchItem;
          String searchText;
          String startDate;
          String endDate;
          String favoritesSearch;
          String pjtType;
          String pjtDiv;
     }

     /**
      * 좌측메뉴 조회 Param
      */
     @Data
     @Alias("selectMenuInput")
     public class SelectMenuInput extends MybatisPageable {
          String usrId;
          String pjtType;
          String pjtNo;
          String cntrctNo;
     }
     


     /**
      * 화면버튼 권한 조회 Param
      */
     @Data
     @Alias("selectResourcesAuthorityInput")
     public class SelectResourcesAuthorityInput extends MybatisPageable {
          String usrId;
          String pjtNo;
          String cntrctNo;
          String pjtType;
          ArrayList<String> rescIdList;
     }

     /**
      * 화면버튼 권한 조회 Param =============추후=============
      */
     @Data
     @Alias("selectBtnAuthorityInput")
     public class SelectBtnAuthorityInput extends MybatisPageable {
          String loginId;
          String menuCd;
          String btnId;
          String pjtNo;
          String cntrctNo;
          String pjtType;
     }
}

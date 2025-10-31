package kr.co.ideait.platform.gaiacairos.core.persistence.vo.dashboard;

import kr.co.ideait.platform.gaiacairos.core.config.GlobalMapperConfig;
import lombok.Data;
import org.mapstruct.Mapper;


@Mapper(config = GlobalMapperConfig.class)
public interface DashboardForm {
    DashboardMybatisParam.MainInput toMainGet(DashBoardMainGet dashBoardMainGet);

    /**
     * 메인대시보드 파라미터
     */
    @Data
    class DashBoardMainGet {
        String pjtNo;
        String cntrctNo;
        String systemType;
        String loginId;
        String loginType;
    }

    /**
     * 종합대시보드01 파라미터
     */
    @Data
    class DashBoardType01Get {
        String cmnCdNmKrn;
        String cmnCdNmEng;
    }

    /**
     * 종합대시보드01의 지역별 공사정보 파라미터
     */
    @Data
    class DashBoardType01ProjectGet {
        String cmnCd;
        String rgnCd;
    }
    
    /**
     * 친환경 인증 팝업 파라미터
     */
    @Data
    class EcoFriendlyParam {
        String viewType;
        String pjtNo;
        String cntrctNo;
        String ecoTpCd;
    }
}

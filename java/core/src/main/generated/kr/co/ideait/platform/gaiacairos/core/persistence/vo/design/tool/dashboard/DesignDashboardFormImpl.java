package kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.dashboard;

import javax.annotation.processing.Generated;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.setting.DesignSettingMybatisParam;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:16+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class DesignDashboardFormImpl implements DesignDashboardForm {

    @Override
    public DesignSettingMybatisParam.DesignDashboardListInput toDesignDashboardListInput(DesignDashboardList dashboardList) {
        if ( dashboardList == null ) {
            return null;
        }

        DesignSettingMybatisParam.DesignDashboardListInput designDashboardListInput = new DesignSettingMybatisParam.DesignDashboardListInput();

        designDashboardListInput.setPage( dashboardList.getPage() );
        designDashboardListInput.setSize( dashboardList.getSize() );
        designDashboardListInput.setCntrctNo( dashboardList.getCntrctNo() );
        designDashboardListInput.setUsrId( dashboardList.getUsrId() );

        return designDashboardListInput;
    }
}

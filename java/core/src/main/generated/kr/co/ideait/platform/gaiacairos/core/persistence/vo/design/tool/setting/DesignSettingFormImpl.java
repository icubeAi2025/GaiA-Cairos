package kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.setting;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:18+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class DesignSettingFormImpl implements DesignSettingForm {

    @Override
    public DesignSettingMybatisParam.DesignPhaseDetailInput toDesignPhaseDetailInput(DesignPhaseDetail designPhaseDetail) {
        if ( designPhaseDetail == null ) {
            return null;
        }

        DesignSettingMybatisParam.DesignPhaseDetailInput designPhaseDetailInput = new DesignSettingMybatisParam.DesignPhaseDetailInput();

        designPhaseDetailInput.setCntrctNo( designPhaseDetail.getCntrctNo() );
        designPhaseDetailInput.setDsgnPhaseNo( designPhaseDetail.getDsgnPhaseNo() );

        return designPhaseDetailInput;
    }

    @Override
    public List<DesignSettingMybatisParam.DesignDisplayOrderMoveInput> toDesignDisplayOrderMoveInput(List<DesignDisplayOrderMove> displayOrderMove) {
        if ( displayOrderMove == null ) {
            return null;
        }

        List<DesignSettingMybatisParam.DesignDisplayOrderMoveInput> list = new ArrayList<DesignSettingMybatisParam.DesignDisplayOrderMoveInput>( displayOrderMove.size() );
        for ( DesignDisplayOrderMove designDisplayOrderMove : displayOrderMove ) {
            list.add( designDisplayOrderMoveToDesignDisplayOrderMoveInput( designDisplayOrderMove ) );
        }

        return list;
    }

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

    protected DesignSettingMybatisParam.DesignDisplayOrderMoveInput designDisplayOrderMoveToDesignDisplayOrderMoveInput(DesignDisplayOrderMove designDisplayOrderMove) {
        if ( designDisplayOrderMove == null ) {
            return null;
        }

        DesignSettingMybatisParam.DesignDisplayOrderMoveInput designDisplayOrderMoveInput = new DesignSettingMybatisParam.DesignDisplayOrderMoveInput();

        designDisplayOrderMoveInput.setDsgnPhaseNo( designDisplayOrderMove.getDsgnPhaseNo() );
        designDisplayOrderMoveInput.setDsplyOrdr( designDisplayOrderMove.getDsplyOrdr() );

        return designDisplayOrderMoveInput;
    }
}

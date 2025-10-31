package kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.tool.setting;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:16+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class SettingFormImpl implements SettingForm {

    @Override
    public SettingMybatisParam.DeficientyPhaseDetailInput toDeficientyPhaseDetailInput(DeficientyPhaseDetail deficientyPhaseDetail) {
        if ( deficientyPhaseDetail == null ) {
            return null;
        }

        SettingMybatisParam.DeficientyPhaseDetailInput deficientyPhaseDetailInput = new SettingMybatisParam.DeficientyPhaseDetailInput();

        deficientyPhaseDetailInput.setCntrctNo( deficientyPhaseDetail.getCntrctNo() );
        deficientyPhaseDetailInput.setDfccyPhaseNo( deficientyPhaseDetail.getDfccyPhaseNo() );

        return deficientyPhaseDetailInput;
    }

    @Override
    public List<SettingMybatisParam.DisplayOrderMoveInput> toDisplayOrderMoveInput(List<DisplayOrderMove> displayOrderMove) {
        if ( displayOrderMove == null ) {
            return null;
        }

        List<SettingMybatisParam.DisplayOrderMoveInput> list = new ArrayList<SettingMybatisParam.DisplayOrderMoveInput>( displayOrderMove.size() );
        for ( DisplayOrderMove displayOrderMove1 : displayOrderMove ) {
            list.add( displayOrderMoveToDisplayOrderMoveInput( displayOrderMove1 ) );
        }

        return list;
    }

    @Override
    public SettingMybatisParam.DashboardListInput toDashboardListInput(DashboardList dashboardList) {
        if ( dashboardList == null ) {
            return null;
        }

        SettingMybatisParam.DashboardListInput dashboardListInput = new SettingMybatisParam.DashboardListInput();

        dashboardListInput.setPage( dashboardList.getPage() );
        dashboardListInput.setSize( dashboardList.getSize() );
        dashboardListInput.setCntrctNo( dashboardList.getCntrctNo() );
        dashboardListInput.setUsrId( dashboardList.getUsrId() );

        return dashboardListInput;
    }

    protected SettingMybatisParam.DisplayOrderMoveInput displayOrderMoveToDisplayOrderMoveInput(DisplayOrderMove displayOrderMove) {
        if ( displayOrderMove == null ) {
            return null;
        }

        SettingMybatisParam.DisplayOrderMoveInput displayOrderMoveInput = new SettingMybatisParam.DisplayOrderMoveInput();

        displayOrderMoveInput.setDfccyPhaseNo( displayOrderMove.getDfccyPhaseNo() );
        displayOrderMoveInput.setDsplyOrdr( displayOrderMove.getDsplyOrdr() );

        return displayOrderMoveInput;
    }
}

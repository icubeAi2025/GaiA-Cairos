package kr.co.ideait.platform.gaiacairos.core.persistence.vo.dashboard;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:18+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class DashboardFormImpl implements DashboardForm {

    @Override
    public DashboardMybatisParam.MainInput toMainGet(DashBoardMainGet dashBoardMainGet) {
        if ( dashBoardMainGet == null ) {
            return null;
        }

        DashboardMybatisParam.MainInput mainInput = new DashboardMybatisParam.MainInput();

        mainInput.setPjtNo( dashBoardMainGet.getPjtNo() );
        mainInput.setCntrctNo( dashBoardMainGet.getCntrctNo() );
        mainInput.setSystemType( dashBoardMainGet.getSystemType() );
        mainInput.setLoginId( dashBoardMainGet.getLoginId() );
        mainInput.setLoginType( dashBoardMainGet.getLoginType() );

        return mainInput;
    }
}

package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.projectbilling;

import java.util.Map;
import javax.annotation.processing.Generated;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmProjectBilling;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:18+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class ProjectBillingDtoImpl implements ProjectBillingDto {

    @Override
    public ProjectBilling fromSmProjectBillingMybatis(Map<String, ?> map) {
        if ( map == null ) {
            return null;
        }

        ProjectBilling projectBilling = new ProjectBilling();

        for ( java.util.Map.Entry<String, ?> entry : map.entrySet() ) {
            String key = entry.getKey();
            Object value = entry.getValue();
            projectBilling.put( key, value );
        }

        return projectBilling;
    }

    @Override
    public SetProjectBilling fromSmProjectBilling(SmProjectBilling smPjtBill) {
        if ( smPjtBill == null ) {
            return null;
        }

        SetProjectBilling setProjectBilling = new SetProjectBilling();

        setProjectBilling.setPjtBilNo( smPjtBill.getPjtBilNo() );
        setProjectBilling.setBilNo( smPjtBill.getBilNo() );
        setProjectBilling.setMenuNo( smPjtBill.getMenuNo() );
        setProjectBilling.setMenuCd( smPjtBill.getMenuCd() );
        setProjectBilling.setBilCode( smPjtBill.getBilCode() );
        setProjectBilling.setPjtNo( smPjtBill.getPjtNo() );
        setProjectBilling.setCntrctNo( smPjtBill.getCntrctNo() );
        setProjectBilling.setPjtType( smPjtBill.getPjtType() );
        setProjectBilling.setDltYn( smPjtBill.getDltYn() );
        setProjectBilling.setRgstDt( smPjtBill.getRgstDt() );

        return setProjectBilling;
    }
}

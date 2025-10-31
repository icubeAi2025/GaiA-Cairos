package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.projectbilling;

import javax.annotation.processing.Generated;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmProjectBilling;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:16+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class ProjectBillingFormImpl implements ProjectBillingForm {

    @Override
    public SmProjectBilling toSmProjectBilling(ProjectBilling bill) {
        if ( bill == null ) {
            return null;
        }

        SmProjectBilling smProjectBilling = new SmProjectBilling();

        smProjectBilling.setBilNo( bill.getBilNo() );
        smProjectBilling.setMenuNo( bill.getMenuNo() );
        smProjectBilling.setMenuCd( bill.getMenuCd() );
        smProjectBilling.setBilCode( bill.getBilCode() );
        smProjectBilling.setPjtNo( bill.getPjtNo() );
        smProjectBilling.setCntrctNo( bill.getCntrctNo() );
        smProjectBilling.setPjtType( bill.getPjtType() );

        return smProjectBilling;
    }
}

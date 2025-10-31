package kr.co.ideait.platform.gaiacairos.core.persistence.vo.projectcost.deposit;

import javax.annotation.processing.Generated;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwFrontMoney;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:18+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class DepositFormImpl implements DepositForm {

    @Override
    public CwFrontMoney DepositInsert(DepositInsert depositInsert) {
        if ( depositInsert == null ) {
            return null;
        }

        CwFrontMoney cwFrontMoney = new CwFrontMoney();

        cwFrontMoney.setCntrctNo( depositInsert.getCntrctNo() );
        cwFrontMoney.setPpaymnySno( depositInsert.getPpaymnySno() );
        cwFrontMoney.setPayprceSno( depositInsert.getPayprceSno() );
        cwFrontMoney.setPayType( depositInsert.getPayType() );
        cwFrontMoney.setPpaymnyAmt( depositInsert.getPpaymnyAmt() );
        cwFrontMoney.setPpaymnyCacltAmt( depositInsert.getPpaymnyCacltAmt() );
        cwFrontMoney.setPpaymnyRemndrAmt( depositInsert.getPpaymnyRemndrAmt() );
        cwFrontMoney.setDfrcmpnstAmt( depositInsert.getDfrcmpnstAmt() );
        cwFrontMoney.setOcrnceDate( depositInsert.getOcrnceDate() );
        cwFrontMoney.setRmrk( depositInsert.getRmrk() );
        cwFrontMoney.setApprvlStats( depositInsert.getApprvlStats() );
        cwFrontMoney.setApprvlReqId( depositInsert.getApprvlReqId() );
        cwFrontMoney.setApprvlReqDt( depositInsert.getApprvlReqDt() );
        cwFrontMoney.setApprvlId( depositInsert.getApprvlId() );
        cwFrontMoney.setApprvlDt( depositInsert.getApprvlDt() );
        cwFrontMoney.setDltYn( depositInsert.getDltYn() );

        return cwFrontMoney;
    }

    @Override
    public void toUpdateCwFrontMoney(DepositInsert deposit, CwFrontMoney cwFrontMoney) {
        if ( deposit == null ) {
            return;
        }

        cwFrontMoney.setCntrctNo( deposit.getCntrctNo() );
        cwFrontMoney.setPpaymnySno( deposit.getPpaymnySno() );
        cwFrontMoney.setPayprceSno( deposit.getPayprceSno() );
        cwFrontMoney.setPayType( deposit.getPayType() );
        cwFrontMoney.setPpaymnyAmt( deposit.getPpaymnyAmt() );
        cwFrontMoney.setPpaymnyCacltAmt( deposit.getPpaymnyCacltAmt() );
        cwFrontMoney.setPpaymnyRemndrAmt( deposit.getPpaymnyRemndrAmt() );
        cwFrontMoney.setDfrcmpnstAmt( deposit.getDfrcmpnstAmt() );
        cwFrontMoney.setOcrnceDate( deposit.getOcrnceDate() );
        cwFrontMoney.setRmrk( deposit.getRmrk() );
        cwFrontMoney.setApprvlStats( deposit.getApprvlStats() );
        cwFrontMoney.setApprvlReqId( deposit.getApprvlReqId() );
        cwFrontMoney.setApprvlReqDt( deposit.getApprvlReqDt() );
        cwFrontMoney.setApprvlId( deposit.getApprvlId() );
        cwFrontMoney.setApprvlDt( deposit.getApprvlDt() );
        cwFrontMoney.setDltYn( deposit.getDltYn() );
    }
}

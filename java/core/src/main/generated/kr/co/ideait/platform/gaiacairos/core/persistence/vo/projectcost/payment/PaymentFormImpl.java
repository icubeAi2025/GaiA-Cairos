package kr.co.ideait.platform.gaiacairos.core.persistence.vo.projectcost.payment;

import javax.annotation.processing.Generated;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwPayMng;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:15+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class PaymentFormImpl implements PaymentForm {

    @Override
    public CwPayMng PaymentInsert(PaymentInsert paymentInsert) {
        if ( paymentInsert == null ) {
            return null;
        }

        CwPayMng cwPayMng = new CwPayMng();

        cwPayMng.setCntrctNo( paymentInsert.getCntrctNo() );
        cwPayMng.setPayprceSno( paymentInsert.getPayprceSno() );
        if ( paymentInsert.getPayprceTmnum() != null ) {
            cwPayMng.setPayprceTmnum( Long.parseLong( paymentInsert.getPayprceTmnum() ) );
        }
        cwPayMng.setPayprceYm( paymentInsert.getPayprceYm() );
        cwPayMng.setPrevAcmtlAmt( paymentInsert.getPrevAcmtlAmt() );
        cwPayMng.setThtmAcomAmt( paymentInsert.getThtmAcomAmt() );
        cwPayMng.setRemndrAmt( paymentInsert.getRemndrAmt() );
        cwPayMng.setPpaymnyCacltAmt( paymentInsert.getPpaymnyCacltAmt() );
        cwPayMng.setRsrvAmt( paymentInsert.getRsrvAmt() );
        cwPayMng.setThtmPaymntAmt( paymentInsert.getThtmPaymntAmt() );
        cwPayMng.setPayApprvlDate( paymentInsert.getPayApprvlDate() );
        cwPayMng.setInspctDate( paymentInsert.getInspctDate() );
        cwPayMng.setPaymntDate( paymentInsert.getPaymntDate() );
        cwPayMng.setRmrk( paymentInsert.getRmrk() );
        cwPayMng.setDltYn( paymentInsert.getDltYn() );

        return cwPayMng;
    }

    @Override
    public void toUpdateCwPayMng(PaymentInsert payment, CwPayMng cwPayMng) {
        if ( payment == null ) {
            return;
        }

        cwPayMng.setCntrctNo( payment.getCntrctNo() );
        cwPayMng.setPayprceSno( payment.getPayprceSno() );
        if ( payment.getPayprceTmnum() != null ) {
            cwPayMng.setPayprceTmnum( Long.parseLong( payment.getPayprceTmnum() ) );
        }
        else {
            cwPayMng.setPayprceTmnum( null );
        }
        cwPayMng.setPayprceYm( payment.getPayprceYm() );
        cwPayMng.setPrevAcmtlAmt( payment.getPrevAcmtlAmt() );
        cwPayMng.setThtmAcomAmt( payment.getThtmAcomAmt() );
        cwPayMng.setRemndrAmt( payment.getRemndrAmt() );
        cwPayMng.setPpaymnyCacltAmt( payment.getPpaymnyCacltAmt() );
        cwPayMng.setRsrvAmt( payment.getRsrvAmt() );
        cwPayMng.setThtmPaymntAmt( payment.getThtmPaymntAmt() );
        cwPayMng.setPayApprvlDate( payment.getPayApprvlDate() );
        cwPayMng.setInspctDate( payment.getInspctDate() );
        cwPayMng.setPaymntDate( payment.getPaymntDate() );
        cwPayMng.setRmrk( payment.getRmrk() );
        cwPayMng.setDltYn( payment.getDltYn() );
    }
}

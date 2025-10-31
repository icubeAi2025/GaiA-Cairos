package kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.contract.contractstatus;

import javax.annotation.processing.Generated;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnContractBid;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:18+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class ContractBidDtoImpl implements ContractBidDto {

    @Override
    public ContractBid toChangeList(CnContractBid contractBid) {
        if ( contractBid == null ) {
            return null;
        }

        ContractBid contractBid1 = new ContractBid();

        contractBid1.setCntrctNo( contractBid.getCntrctNo() );
        contractBid1.setCbsSno( contractBid.getCbsSno() );
        contractBid1.setExpnssYn( contractBid.getExpnssYn() );
        contractBid1.setCntrctUnitCnstwkSno( (long) contractBid.getCntrctUnitCnstwkSno() );
        contractBid1.setCntrctDcnsttySno( (long) contractBid.getCntrctDcnsttySno() );
        contractBid1.setUpCntrctDcnsttySno( (long) contractBid.getUpCntrctDcnsttySno() );
        contractBid1.setExpnssSno( (long) contractBid.getExpnssSno() );
        contractBid1.setCstBillLctCd( contractBid.getCstBillLctCd() );
        contractBid1.setExpnssKindCd( contractBid.getExpnssKindCd() );
        contractBid1.setExpnssBscrtPct( (long) contractBid.getExpnssBscrtPct() );
        contractBid1.setDrctCnstcstPct( (long) contractBid.getDrctCnstcstPct() );
        contractBid1.setExpnssCalcfrmlaCd( contractBid.getExpnssCalcfrmlaCd() );
        contractBid1.setCnsttyDtlsDivCd( contractBid.getCnsttyDtlsDivCd() );
        contractBid1.setDcnsttyLvlNum( (long) contractBid.getDcnsttyLvlNum() );
        contractBid1.setPrdnm( contractBid.getPrdnm() );
        contractBid1.setSpec( contractBid.getSpec() );
        contractBid1.setUnit( contractBid.getUnit() );
        if ( contractBid.getQty() != null ) {
            contractBid1.setQty( contractBid.getQty().longValue() );
        }
        contractBid1.setMtrlcstUprc( (long) contractBid.getMtrlcstUprc() );
        contractBid1.setLbrcstUprc( (long) contractBid.getLbrcstUprc() );
        contractBid1.setGnrlexpnsUprc( (long) contractBid.getGnrlexpnsUprc() );
        contractBid1.setSumUprc( (long) contractBid.getSumUprc() );
        contractBid1.setMtrlcstAmt( (long) contractBid.getMtrlcstAmt() );
        contractBid1.setLbrcstAmt( (long) contractBid.getLbrcstAmt() );
        contractBid1.setGnrlexpnsAmt( (long) contractBid.getGnrlexpnsAmt() );
        contractBid1.setSumAmt( (long) contractBid.getSumAmt() );
        contractBid1.setRmrk( contractBid.getRmrk() );
        contractBid1.setDcnsttyAmtTyCd( contractBid.getDcnsttyAmtTyCd() );
        contractBid1.setStdMrktUprcCd( contractBid.getStdMrktUprcCd() );
        contractBid1.setCstRsceCd( contractBid.getCstRsceCd() );
        contractBid1.setBuytaxObjYn( contractBid.getBuytaxObjYn() );
        contractBid1.setCstRsceTyCd( contractBid.getCstRsceTyCd() );
        contractBid1.setOqtyChgPermsnYn( contractBid.getOqtyChgPermsnYn() );
        contractBid1.setCstUnitCnstwkNo( contractBid.getCstUnitCnstwkNo() );
        contractBid1.setCstCnsttySno( (long) contractBid.getCstCnsttySno() );
        contractBid1.setCstDcnsttySno( (long) contractBid.getCstDcnsttySno() );
        contractBid1.setDltYn( contractBid.getDltYn() );

        return contractBid1;
    }
}

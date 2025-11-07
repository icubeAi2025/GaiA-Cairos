package kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.contract.contractstatus;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnContractBid;
import kr.co.ideait.platform.gaiacairos.core.config.GlobalMapperConfig;
import lombok.Data;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.List;

@Mapper(config = GlobalMapperConfig.class)
public interface ContractBidDto {
    ContractBid toChangeList(CnContractBid contractBid);

    @Data
    class ContractBid {
        String cntrctNo; // 계약번호
        Long cbsSno; // cbs순번

        String expnssYn; // 제경비여부(기본값 N)

        // 직접공사비

        Long cntrctUnitCnstwkSno; // 계약단위공사순번

        Long cntrctDcnsttySno; // 계약세부공종순번

        Long upCntrctDcnsttySno; // 상위세부공종순번

        Long cstCnsttySnoCal; // 원가공종순번값

        // 제경비
        Long expnssSno; // 제경비순번
        String cstBillLctCd; // 원가계산서위치코드
        String expnssKindCd; // 제경비종류코드
        Long expnssBscrtPct; // 제경비요율백분율
        Long drctCnstcstPct; // 직접공사비백분율
        String expnssCalcfrmlaCd; // 제경비산출식코드

        String cnsttyDtlsDivCd; // 공종내역구분코드
        Long dcnsttyLvlNum; // 세부공종레벨수
        String prdnm; // 품명
        String spec; // 규격
        String unit; // 단위
        Long qty; // 수량
        Long mtrlcstUprc; // 재료비단가
        Long lbrcstUprc; // 노무비단가
        Long gnrlexpnsUprc; // 경비단가
        Long sumUprc; // 합계단가
        Long mtrlcstAmt; // 재료비금액
        Long lbrcstAmt; // 노무비금액
        Long gnrlexpnsAmt; // 경비금액
        Long sumAmt; // 합계금액
        String rmrk; // 비고
        String dcnsttyAmtTyCd; // 세부공종금액유형코드(기본값 0)
        String stdMrktUprcCd; // 표준시장단가코드
        String cstRsceCd; // 원가자원코드
        String buytaxObjYn; // 매입세대상여부(기본값 N)
        String cstRsceTyCd; // 원가자원유형코드
        String oqtyChgPermsnYn; // 물량번경허용여부(기본값 N)
        String cstUnitCnstwkNo; // 원가단위공사번호
        Long cstCnsttySno; // 원가공종순번
        Long cstDcnsttySno; // 원가세부공종순번
        String dltYn; // 삭제여부
    }

    @Data
    public static class CostItemNode {  
        private ContractstatusMybatisParam.RawCostItem data;   // 행에 쓰여질 데이터( ex) 순공가원가, 재료비, 직접재료비...
        private CostItemNode parent;
        private List<CostItemNode> children = new ArrayList<>();

        public long getTotalCostAmount() {
            if (children.isEmpty()) return data != null ? data.getCostAm() : 0;
            return children.stream().mapToLong(CostItemNode::getTotalCostAmount).sum();
        }
    }

    @Data
    public static class ContractItemNode {
        private ContractstatusMybatisParam.RawContractItem data;
        private ContractItemNode parent;
        private List<ContractItemNode> children = new ArrayList<>();

        public Double getTotalMtrlcsAmount() { // 재료비 소계 계산
            if (children.isEmpty()) return data != null ? data.getMtrlcstAmt() : 0;
            return children.stream().mapToDouble(ContractItemNode::getTotalMtrlcsAmount).sum();
        }

        public Double getTotalLbrcstAmount() { // 노무비 소계 계산
            if (children.isEmpty()) return data != null ? data.getLbrcstAmt() : 0;
            return children.stream().mapToDouble(ContractItemNode::getTotalLbrcstAmount).sum();
        }

        public Double getTotalGnrlexpnsAmount() { // 경비 소계 계산
            if (children.isEmpty()) return data != null ? data.getGnrlexpnsAmt() : 0;
            return children.stream().mapToDouble(ContractItemNode::getTotalGnrlexpnsAmount).sum();
        }

        public Double getTotalSumAmount() { // 합계 소계 계산
            if (children.isEmpty()) return data != null ? data.getSumAmt() : 0;
            return children.stream().mapToDouble(ContractItemNode::getTotalSumAmount).sum();
        }
    }

    @Data
    public static class CbsItemNode {
        private ContractstatusMybatisParam.RawCbsItem data;
        private CbsItemNode parent;
        private List<CbsItemNode> children = new ArrayList<>();

    }
}

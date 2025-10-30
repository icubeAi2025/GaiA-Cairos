package kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.contract.contractstatus;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnContractChange;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.contract.contractstatus.ContractstatusMybatisParam.*;
import kr.co.ideait.platform.gaiacairos.core.config.GlobalMapperConfig;
import lombok.Data;
import org.mapstruct.Mapper;

@Mapper(config = GlobalMapperConfig.class)
public interface ContractChangeDto {
    ContractChangeList toChangeList(ContractchangeOutputList contractchangeOutput);

    ContractChange toChange(ContractchangeOutput contractchangeOutput);

    ContractChangeAdd toChangeAdd(ContractchangeAddOutput contractchangeAddOutput);

    ContractChange toContractChange(CnContractChange cnChange);

    @Data
    class ContractChangeList {
        String cntrctChgId; // 계약변경ID
        String cntrctNo; // 계약번호
        String cntrctChgNo; // 계약변경차수
        String chgApprDate; // 변경승인일자
        String cntrctChgDate; // 계약변경일자
        Long cntrctAmt; // 변경계약금액
        String cbgnDateRange; // 변경계약기간
        String cntrctChgTypeNmKrn; // 계약변경구분(한글이름)
        String cntrctChgType; // 계약변경구분
        String rmrk; // 비고
        String lastChgYn; // 최종변경여부

        Long cntrctDivCd; // 계약구분 코드
        String chgThisCbgnDate; // 변경 금차계약 기간
        Long thisCntrctAmt; // 변경 금차 계약금액
        Long chgThisConPrd; // 변경 금차공사 기간
        Long thisCntrctAmtBefore; // 이전 회차 금차 변경계약금액
    }

    @Data
    class ContractChange {
        String cntrctNo;
        String cntrctNm; // 공사계약명
        String majorCnsttyCd;
        String majorCnsttyNm; // 주공종(한글)
        String mngCntrctNo; // 관리계약번호
        String corpNm; // 업체명
        String cbgnDate; // 착공일자
        String cntrctCost; // 최초계약금액

        String cntrctPhase;  // 계약차수
        String cntrctChgNo; // 회차
        String chgApprDate; // 변경승인일자
        String cntrctChgDate; // 계약변경일자
        Long cntrctAmt; // 변경계약금액
        String chgCbgnDate; // 준공일자
        String cntrctChgType; // 계약변경구분
        String cntrctChgTypeNm; // 계약변경구분(한글)
        Long chgConPrd; // 공사기간
        String rmrk; // 비고
        Long cntrctAmtBefore; // 이전차수 변경 계약금액
        String lastChgYn; // 최종변경여부

        String cntrctDivCd; // 계약구분 코드
        String chgThisCbgnDate; // 변경 금차계약 기간(준공일자)
        Long chgThisConPrd;  // 변경 금차공사 기간
        String thisCntrctCost; //최초 금차 계약 금액
        Long thisCntrctAmt; // 변경 금차 계약금액
        Long thisCntrctAmtBefore;   // 이전 회차 금차 변경계약금액
    }

    @Data
    class ContractChangeAdd {
        String cntrctNo;
        String cntrctNm; // 공사계약명
        String majorCnsttyCd;
        String majorCnsttyNm; // 주공종(한글)
        String mngCntrctNo; // 관리계약번호
        String corpNm; // 업체명
        String cbgnDate; // 착공일자
        String cntrctCost; // 최초계약금액

        String cntrctChgNo; // 회차
        Long cntrctPhase; // 차수
        String chgApprDate; // 변경승인일자
        String cntrctChgDate; // 계약변경일자
        Long cntrctAmt; // 변경계약금액
        String chgCbgnDate; // 준공일자
        String cntrctChgType; // 계약변경구분
        Long chgConPrd; // 공사기간
        String rmrk; // 비고
        Long cntrctAmtBefore; // 이전회차 변경 계약금액
        String lastChgYn; // 최종변경여부

        String thisCntrctCost; // 최초 금차 계약 금액
        String thisCbgnDate; // 이전회차 금차 계약 일자
        String cntrctDivCd; // 계약구분코드
        Long thisCntrctAmtBefore; // 이전회차 금차 변경 계약금액
    }
}

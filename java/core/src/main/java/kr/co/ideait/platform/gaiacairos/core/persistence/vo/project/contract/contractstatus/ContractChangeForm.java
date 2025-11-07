package kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.contract.contractstatus;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnContractChange;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnContractCompany;
import lombok.Data;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = ComponentModel.SPRING)
public interface ContractChangeForm {

    ContractstatusMybatisParam.ContractchangeListInput toContractchangeListInput(ContractChangeListGet changeListGet);

    CnContractChange toContractChange(ChangeCreate change);

    void updateContractChange(ChangeUpdate change, @MappingTarget CnContractChange cnChange);

    @Data
    class ContractChangeListGet {
        String cntrctNo;
    }

    @Data
    class ContractChangeGet {
        String cntrctChgId;
        String cntrctNo;
    }

    @Data
    class ChangeCreate {
        String cntrctNo; // 계약번호
        String cntrctChgNo; // 차수
        String cntrctChgType; // 계약변경구분
        String chgApprDate; // 변경승인일자
        String cntrctChgDate; // 계약변경일자
        String chgCbgnDate; // 준공일자
        Double chgConPrd; // 공사기간
        Double cntrctAmt; // 금번차수 변경계약금액
        String rmrk; // 비고
        String lastChgYn; // 최종변경여부

        String cntrctDivCd; // 계약구분 코드
        String chgThisCbgnDate; // 변경 금차계약 기간
        Double thisCntrctAmt; // 변경 금차 계약금액
        Double chgThisConPrd; // 변경 금차공사 기간
        Double thisCntrctAmtBefore; // 이전 회차 금차 변경계약금액

        String cntrctPhase; // 계약차수
    }

    @Data
    class ChangeUpdate {
        String cntrctNo; // 계약번호
        Long cntrctPhase;   // 계약차수
        String cntrctChgNo; // 회차
        String cntrctChgType; // 계약변경구분
        String chgApprDate; // 변경승인일자
        String cntrctChgDate; // 계약변경일자
        String chgCbgnDate; // 준공일자
        Double chgConPrd; // 공사기간
        Double cntrctAmt; // 금번차수 변경계약금액
        Double dfrcmpnstRate;
        Double vatRate;
        String rmrk; // 비고
        String lastChgYn; // 최종변경여부

        String chgThisCbgnDate; // 변경 금차계약기간
        Double chgThisConPrd; // 변경 금차공사 기간
        Double thisCntrctAmt; // 변경 금차 계약 금액
    }

    @Data
    class ChangeList {
        List<CnContractChange> changeList;
    }
}

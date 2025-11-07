package kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.contract.contractstatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.type.Alias;

import lombok.Data;

public interface ContractstatusMybatisParam {

    @Data
    @Alias("contractstatusListInput")
    public class ContractstatusListInput {
        String pjtNo;
        String cntrctNo;
        String code; // 공사구분(주공종) 코드
    }

    @Data
    @Alias("contractstatusOutput")
    public class ContractstatusOutput {
        String cntrctNo; // 계약번호
        String pjtNo; // 프로젝트번호
        String cntrctNm; // 공사계약명
        String mngCntrctNo; // 계약번호(관리계약번호)
        String cntrctType; // 계약종류
        String majorCnsttyCd; // 주공종코드
        String majorCnsttyNmKrn;
        String cntrctDate; // 계약일자
        String grntyDate; // 보증일
        String cbgnDate; // 착공일자
        String ccmpltDate; // 준공일자

        Long conPrd; // 공사기간
        Long cntrctCost; // 계약금액
        Long grntyCost; // 보증금
        Long vatRate; // 부가세율 -> 소수점3자리
        Long dfrcmpnstRate; // 지체상금율 -> 소수점3자리

        String bsnsmnNo; // 사업자등록번호
        String corpNm; // 업체명(대표계약 회사명)
        String corpAdrs; // 업체주소
        String telNo; // 전화번호(사무실 번호)
        String faxNo; // 팩스번호
        String corpCeo; // 업체대표자
        String ofclNm; // 담당자명
        String ofclId; // 담당자Id
        String latestCntrctChgDate; // 계약변경일자

        String rgstrId;
        LocalDateTime rgstDt;
        String chgId;
        LocalDateTime chgDt;
        String dltId;
        LocalDateTime dltDt;
    }

    @Data
    @Alias("contractcompanyListInput")
    public class ContractcompanyListInput {
        String cntrctNo;
        String code; // 계약변경구분
    }

    @Data
    @Alias("contractcompanyOutput")
    public class ContractcompanyOutput {
        String pjtNo;   
        String cntrctNo; // 계약번호
        Long cntrctId; // 계약도급 Id
        String cntrctNm; // 공사계약명
        String cnsttyCd; // 공종코드
        String workTypeNmKrn;   // 공종명
        String bsnsmnNo; // 사업자등록번호
        String corpNm; // 업체명(도급사 이름)
        String corpNo; // 업체번호(도급사 번호)
        String telNo; // 전화번호(사무실 번호)
        String faxNo; // 팩스번호
        String corpAdrs; // 업체주소
        String corpCeo; // 업체대표자
        String ofclNm; // 담당자명
        String ofclId; // 담당자Id
        Long shreRate; // 지분율
        String rprsYn;  // 대표여부
        String cnsttyCdNmKrn;   // 공종(한글이름)
    }

    @Data
    @Alias("contractchangeOutput")
    public class ContractchangeOutput { // 일반 조회용
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

        String rgstrId;
        LocalDateTime rgstDt;
        String chgId;
        LocalDateTime chgDt;
        String dltId;
        LocalDateTime dltDt;
    }

    @Data
    @Alias("contractchangeAddOutput")
    public class ContractchangeAddOutput { // 추가 조회용
        String cntrctNo;
        String cntrctNm; // 공사계약명
        String majorCnsttyNm; // 계약 공종
        String mngCntrctNo; // 관리계약번호
        String corpNm; // 업체명
        String cbgnDate; // 착공일자
        String cntrctCost; // 최초계약금액

        Long cntrctAmtBefore; // 이전차수 변경 계약금액
        String cntrctChgNo; // 회차
        Long cntrctPhase;   // 차수

        String thisCntrctCost; //최초 금차 계약 금액
        String thisCbgnDate; //이전회차 금차 계약 기간
        String cntrctDivCd; //계약구분 코드
        Long thisCntrctAmtBefore; //이전회차 금차 변경 계약금액
    }

    @Data
    @Alias("contractchangeListInput")
    public class ContractchangeListInput {
        String cntrctNo;
        String code; // 계약변경구분
    }

    @Data
    @Alias("contractchangeOutputList")
    public class ContractchangeOutputList { // 그리드 목록 조회용
        String pjtNo;
        String cntrctChgId; // 계약변경ID
        String cntrctNo; // 계약번호
        String cntrctChgNo; // 계약변경차수
        String cntrctChgDate; // 변경계약일자
        String chgCbgnDate; // 변경준공일자
        String chgThisCbgnDate; // 금차변경준공일자
        Long cntrctAmt; // 변경계약금액
        String cbgnDateRange; // 변경계약기간
        String cntrctChgTypeNmKrn; // 계약변경구분(한글이름)
        String cntrctChgType; // 계약변경구분
        String rmrk; // 비고
        String lastChgYn; // 최종변경여부
        Long cntrctPhase;   // 계약차수
        String cntrctDivCd; // 계약 구분 코드
    }

    @Data
    @Alias("contractInput")
    public class ContractInput {
        String pInserttype;
        String pPjttype;
        String pPjtno;
        String pCntrctno;
        String pItemname;
        String pItemdesc;
        String pCorpno;
    }

    @Data
    @Alias("contractBidListOutPut")
    public class ContractBidListOutPut {
        String dcnsttyLvlNum;   // 세부공종레벨수
        Long cbsSno; // cbs순번

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
    }

    @Data
    @Alias("contractBidSearch")
    public class ContractBidSearch {
        Long cbsSno; // cbs순번

        String prdnm; // 품명
        String spec; // 규격
        double qty; // 수량
        double mtrlcstUprc; // 재료비단가
        double lbrcstUprc; // 노무비단가
        double gnrlexpnsUprc; // 경비단가
        double sumUprc; // 합계단가
        double mtrlcstAmt; // 재료비금액
        double lbrcstAmt; // 노무비금액
        double gnrlexpnsAmt; // 경비금액
        double sumAmt; // 합계금액
        String rmrk; // 비고
    }

    @Data
    @Alias("corpBidInfo")
    public class CorpBidInfo {
        List<Map> unitConstList;// 단위공사
        List<Map> detailList;// 내역서
        List<Map> detailAddList;// 내역서
        List<Map> expnssList;// 제경비
    }

    @Data
    @Alias("contractCalculator")
    public class ContractCalculator {
        String cntrctNo;
        String cstCalcItCd;
        String upCstCalcItCd;
        int dsplyOrdr;
        String cstCalcItNm;
        String cstCalcMthdNm;
        String cstCalcMthdNomfrmCntnts;
        String cstCalcbllDsplyVal;
        double ovrhdcstPt;
        double drcnstcostCmprPt;
        double costAm;
        String dltYn;
        String rgstrId;
        LocalDateTime rgstDt;
        String chgId;
        LocalDateTime chgDt;
        String dltId;
        LocalDateTime dltDt;
    }

    @Data
	@Alias("calculateCostOutput")
    public class RawCostItem {
        String cstCalcItCd;
        String cstCalcItNm;
        String upCstCalcItCd;
        String cstCalcMthdNm;
        Integer aMenuLevel;
        String path;
        int dsplyOrdr;
        double ovrhdcstPt;
        double drcnstcostCmprPt;
        long costAm;
    }

    @Data
	@Alias("contractBidOutput")
    public class RawContractItem {
        private String cntrctDcnsttySno;     // 계약 공종 항목 일련번호 (문자열 처리)
        private String prdnm;                // 품명 (공백 들여쓰기 포함됨)
        private String upCntrctDcnsttySno;   // 상위 항목 일련번호
        private int aMenuLevel;              // 메뉴 레벨 (DEPTH)
        private String path;                 // 경로
        private String cbsSno;               // CBS 일련번호
    
        private String spec;                 // 규격
        private String unit;                 // 단위
        private Double qty;                  // 수량
    
        private Double mtrlcstUprc;          // 자재 단가
        private Double mtrlcstAmt;           // 자재 금액
    
        private Double lbrcstUprc;           // 노무 단가
        private Double lbrcstAmt;            // 노무 금액
    
        private Double gnrlexpnsUprc;        // 일반경비 단가
        private Double gnrlexpnsAmt;         // 일반경비 금액
    
        private Double sumUprc;              // 총합 단가
        private Double sumAmt;               // 총합 금액
    
        private String rmrk;                 // 비고
    }

    @Data
	@Alias("cbsOutput")
    public class RawCbsItem {
        private String cntrctChgId;     // 계약 변경 id (문자열 처리)
        private String cnsttyNm;        // 공종명 (공백 들여쓰기 포함됨)
        private String cnsttySn;        // 공종 순번
        private String parentSn;        // 상위 공종 순번
        private String cnsttyCd;        // 공종 코드
        private String upCnsttyCd;      // 상위 공종 코드
        private int cnsttyLvlNum;       // 공종 레벨 (DEPTH)
        private String path;            // 경로
        private String unitCnstType;    // 단위공사 구분
        private String nodeType;    // 노드 타입
    
        private String spec;                 // 규격
        private String unit;                 // 단위
        private double qty;                  // 수량

        private double mtrlcstUprc;          // 자재 단가
        private double mtrlcstAmt;           // 자재 금액

        private double lbrcstUprc;           // 노무 단가
        private double lbrcstAmt;            // 노무 금액

        private double gnrlexpnsUprc;        // 일반경비 단가
        private double gnrlexpnsAmt;         // 일반경비 금액

        private double sumUprc;              // 총합 단가
        private double sumAmt;               // 총합 금액
    
        private String rmrk;                 // 비고
        private String etc;                  // 기타
    }

    @Data
	@Alias("dminstt")
    public class Dminstt {
        String dminsttCd;
        String dminsttNm;
        String attrbt;
    }

    @Data
    @Alias("contractDeleteInput")
    public class ContractDeleteInput {
        String pjtNo;
        String usrId;
    }

}

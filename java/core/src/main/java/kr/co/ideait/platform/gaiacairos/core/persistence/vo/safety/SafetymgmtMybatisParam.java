package kr.co.ideait.platform.gaiacairos.core.persistence.vo.safety;

import org.apache.ibatis.type.Alias;

import lombok.Data;

public interface SafetymgmtMybatisParam {

    @Data
    @Alias("safetyoutput")
    public class SafetyOutput {
        String cntrctNo; // 계약번호
        String inspectionNo; // 안전점검번호

        String title; // 제목
        String ispDocNo; // 안전점검 문서번호
        String cnstrtn_id; // 요청자 ID(시공담당자)
        String cnsttyCd;    // 공종코드
        String cnsttyCdL1; // 공종코드_1
        String cnsttyCdL2; // 공종코드_2
        String upCnsttyNm2; // 공종코드_2의 상위 공종(level3 공종)
        String cnsttyNm;  // 공종 한글명
        String cnsttyNm1; // 공종코드_1 한글명
        String cnsttyNm2; // 공종코드_2 한글명

        int atchFileNo; // 첨부파일 번호
        String rsltYn; // 결과작성여부
        String ispId; // 점검자 ID
        String ispDt; // 점검일자
        String apReqId; // 전자결재 요청자 ID
        String apReqDt; // 전자결재 요청 일자
        String apDocId; // 전자결재 문서 ID
        String apprvlId; // 전자결재 승인자 ID
        String apprvlDt; // 전자결재 승인일
        String apprvlStats; // 전자결재 승인상태
        String apOpnin; // 전자결재 의견
    }


    @Data
    @Alias("safetylistoutput")
    public class SafetyListOutPut {
        String cntrctNo;    // 계약번호
        String inspectionNo;
        String cnsttyNm;
        Integer gdFltyYn;
        String imprvReq;
        String ispDscrpt;
        String ispLstId;
        Integer ispLstNo;
        String cnsttyCd;
    }
}

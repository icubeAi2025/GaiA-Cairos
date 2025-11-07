package kr.co.ideait.platform.gaiacairos.core.config.property.eureca;

import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;

@Data
public class Api {

    /**
     * 공사-최초계약 등록
     */
    @Description(name = "공사-최초계약 등록", description = "공사-최초계약 등록 api 프로퍼티")
    String registerCnstwk;

    /**
     * 계약정보 등록
     */
    @Description(name = "계약정보 등록", description = "계약정보 등록 api 프로퍼티")
    String registerCntrct;

    /**
     * 기성내역서(작성요청)
     */
    @Description(name = "기성내역서(작성요청)", description = "기성내역서(작성요청) api 프로퍼티")
    String registerPrgpymntDtls;

    /**
     * 기성내역서(작성완료) 조회
     */
    @Description(name = "기성내역서(작성완료) 조회", description = "기성내역서(작성완료) 조회 api 프로퍼티")
    String retrievePrgpymntDtls;

    /**
     * 기성상태결과 변경
     */
    @Description(name = "기성상태결과 변경", description = "기성상태결과 변경 api 프로퍼티")
    String updatePrgpymntAprv;

    /**
     * 계약내역서(작성완료) 조회
     */
    @Description(name = "계약내역서(작성완료) 조회", description = "계약내역서(작성완료) 조회 api 프로퍼티")
    String retrieveCntrDtls;

    /**
     * 내역서 산출 상세 조회
     */
    @Description(name = "내역서 산출 상세 조회", description = "내역서 산출 상세 조회 api 프로퍼티")
    String retrieveSpcsCalcDtls;
}

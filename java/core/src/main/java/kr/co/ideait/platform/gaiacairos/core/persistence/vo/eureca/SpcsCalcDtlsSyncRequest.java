package kr.co.ideait.platform.gaiacairos.core.persistence.vo.eureca;

import com.fasterxml.jackson.annotation.JsonProperty;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;

import java.util.List;

/**
 * 내역서 산출 상세 JSON TO JAVA
 */
@Data
public class SpcsCalcDtlsSyncRequest {
    @JsonProperty("cntrctNo")
    @Description(name = "계약번호", description = "계약내역서-요청본문 Field", type = Description.TYPE.FIELD)
    String cntrctNo;

    @JsonProperty("cntrctChgId")
    @Description(name = "변경계약ID", description = "내역서산출상세-요청본문 Field", type = Description.TYPE.FIELD)
    String cntrctChgId;

    @JsonProperty("ucostCnt")
    @Description(name = "일위대가자원내역건수", description = "내역서산출상세-요청본문 Field", type = Description.TYPE.FIELD)
    Long ucostCnt;

    @JsonProperty("hmupcCnt")
    @Description(name = "중기단가자원내역건수", description = "내역서산출상세-요청본문 Field", type = Description.TYPE.FIELD)
    Long hmupcCnt;

    @JsonProperty("hmupcCalcfrmlaCntnts")
    @Description(name = "(중기단가산출자원) 경우 산출식 내용", description = "내역서산출상세-요청본문 Field", type = Description.TYPE.FIELD)
    String hmupcCalcfrmlaCntnts;

    @JsonProperty("ucostDtlList")
    @Description(name = "일위대가자원내역", description = "내역서산출상세-요청본문 Field", type = Description.TYPE.FIELD)
    List<UcostDtl> ucostDtlList;

    @JsonProperty("hmupcDtlList")
    @Description(name = "중기단가산출자원내역", description = "내역서산출상세-요청본문 Field", type = Description.TYPE.FIELD)
    List<HmupcDtl> hmupcDtlList;

    // 공통 FIELD 주입(계약번호, 계약변경ID)
    public void propagateCommonContractData() {
        applyCommonFields(this.ucostDtlList);
        applyCommonFields(this.hmupcDtlList);
    }

    // 내부 메서드 공통 FIELD 주입(계약번호, 계약변경ID)
    private void applyCommonFields(List<? extends BaseSyncModel> list) {
        if (list == null || list.isEmpty()) return;

        for (BaseSyncModel item : list) {
            item.setCntrctNo(this.cntrctNo);
            item.setCntrctChgId(this.cntrctChgId);
        }
    }
}

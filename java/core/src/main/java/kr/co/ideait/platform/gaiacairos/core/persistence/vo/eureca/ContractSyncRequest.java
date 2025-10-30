package kr.co.ideait.platform.gaiacairos.core.persistence.vo.eureca;

import com.fasterxml.jackson.annotation.JsonProperty;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 내역서 등록 JSON TO JAVA
 */
@Data
public class ContractSyncRequest {
    @JsonProperty("cntrctNo")
    @Description(name = "계약번호", description = "계약내역서-요청본문 Field", type = Description.TYPE.FIELD)
    String cntrctNo;

    @JsonProperty("cntrctChgId")
    @Description(name = "변경계약ID", description = "계약내역서-요청본문 Field", type = Description.TYPE.FIELD)
    String cntrctChgId;

    @JsonProperty("totalCnt1")
    @Description(name = "응답건수 - 계약내역서", description = "계약내역서-요청본문 Field", type = Description.TYPE.FIELD)
    private Long totalCnt1;

    @JsonProperty("totalCnt2")
    @Description(name = "응답건수 - 원가계산서", description = "계약내역서-요청본문 Field", type = Description.TYPE.FIELD)
    private Long totalCnt2;

    @JsonProperty("totalCnt3")
    @Description(name = "응답건수 - 소요자원내역", description = "계약내역서-요청본문 Field", type = Description.TYPE.FIELD)
    private Long totalCnt3;

    @JsonProperty("cntrDtlsList")
    @Description(name = "계약내역서 목록", description = "계약내역서-요청본문 Field", type = Description.TYPE.FIELD)
    private List<CntrDtl> cntrDtlsList;

    @JsonProperty("cstList")
    @Description(name = "원가내역서 목록", description = "계약내역서-요청본문 Field", type = Description.TYPE.FIELD)
    private List<Calculator> cstList;

    @JsonProperty("reqreRsceList")
    @Description(name = "소요자원내역 목록", description = "계약내역서-요청본문 Field", type = Description.TYPE.FIELD)
    private List<ReqreRsce> reqreRsceList;

    // 공통 FIELD 주입(계약번호, 계약변경ID)
    public void propagateCommonContractData() {
        if(!CollectionUtils.isEmpty(this.cntrDtlsList)) applyCommonFields(this.cntrDtlsList);
        if(!CollectionUtils.isEmpty(this.cstList)) applyCommonFields(this.cstList);
        if(!CollectionUtils.isEmpty(this.reqreRsceList)) applyCommonFields(this.reqreRsceList);
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

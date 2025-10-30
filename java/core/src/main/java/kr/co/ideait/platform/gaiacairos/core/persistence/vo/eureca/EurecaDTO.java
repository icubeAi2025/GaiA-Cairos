package kr.co.ideait.platform.gaiacairos.core.persistence.vo.eureca;

import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;

public class EurecaDTO {
    @Data
    class PaymentHistory {
        // 내역순번. 유레카에서 생성한 내역순번(내역식별번호)
        @Description(name = "내역순번", description = "PaymentHistory Field", type = Description.TYPE.FIELD)
        String dtlsSn;

        // 금회기성수량
        @Description(name = "금회기성수량", description = "PaymentHistory Field", type = Description.TYPE.FIELD)
        String thtmAcomQty;
    }
}

package kr.co.ideait.platform.gaiacairos.core.config.property.eapproval;

import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;

@Data
public class Api {

    /**
     * 기안문 작성 화면 렌더링
     */
    @Description(name = "기안문 작성 화면", description = "기안문 작성 화면 api 프로퍼티")
    String renderDraft;

    /**
     * 서식 리스트 조회
     */
    @Description(name = "서식 리스트 조회", description = "서식 리스트 조회 api 프로퍼티")
    String apFormList;
}

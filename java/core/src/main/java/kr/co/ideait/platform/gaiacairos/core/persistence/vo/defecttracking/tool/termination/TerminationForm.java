package kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.tool.termination;

import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Mapper(componentModel = ComponentModel.SPRING)
public interface TerminationForm {

	// 종결관리 데이터 조회
    @Data
    @EqualsAndHashCode(callSuper = false)
    class TerminationGet {
        @Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
		@NotBlank
        String cntrctNo;

        @Description(name = "결함단계 번호", description = "", type = Description.TYPE.FIELD)
		@NotNull
        String dfccyPhaseNo;

        @Description(name = "결함번호", description = "", type = Description.TYPE.FIELD)
		@NotBlank
        String dfccyNo;
    }

    //종결 관리 > 추가, 수정 폼
    @Data
    class CreateUpdateForm {
        @Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
        @NotBlank
        String cntrctNo;

        @Description(name = "결함번호", description = "", type = Description.TYPE.FIELD)
        @NotBlank
        String dfccyNo;

        @Description(name = "종결 코드", description = "", type = Description.TYPE.FIELD)
        @NotBlank
        String edCd;
    }

    // 일괄 종결 처리 폼
    @Data
    class TerminationAll {
        @Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
        @NotBlank
        String cntrctNo;

        @Description(name = "종결 코드", description = "", type = Description.TYPE.FIELD)
        @NotBlank
        String edCd;

        @Description(name = "결함번호 리스트", description = "", type = Description.TYPE.FIELD)
        List<String> dfccyNoList;
    }

    // 종결 처리 삭제 폼
    @Data
    class DeleteTermination {
        @Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
        @NotBlank
        String cntrctNo;

        @Description(name = "결함번호 리스트", description = "", type = Description.TYPE.FIELD)
        List<String> dfccyNoList;
    }
}

package kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.contract.subcontract;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnSubcontract;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnSubcontractChange;
import kr.co.ideait.platform.gaiacairos.core.config.GlobalMapperConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(config = GlobalMapperConfig.class)
public interface SubcontractForm {

    // 등록
    CnSubcontract toCreateSubcontract(CreateSubcontract subcontract);

    // 하도급 추가
    @Data
    public class CreateSubcontract {
        String cntrctNo;
        Long scontrctCorpId;
        @NotBlank(message = "계약번호는 필수 입력 값입니다.")
        String scontrctCntrctNo;
        String scontrctCntrctNm;
        String gcontrctCorpNo;
        String scontrctCorpNo;
        String scontrctCorpBsnsmnNo;
        String scontrctCorpNm;
        String scontrctCorpAdrs;
        String scontrctTelNo;
        String scontrctFaxNo;
        String scontrctCorpCeo;
        @NotBlank(message = "공종구분은 필수 입력 값입니다.")
        String scontrctCcnsttyCd;
        String scontrctIndstrytyCd;
        String cntrctDate;
        @NotBlank(message = "시작일자는 필수 입력 값입니다.")
        String cntrctBgnDate;
        @NotBlank(message = "종료일자는 필수 입력 값입니다.")
        String cntrctEndDate;
        Long scontrctCntrctAmt;
        String rmrk;

        @Size(max = 10)
        String bsnsmnNo;
        String corpNm;

        String scontrctCcnsttyCdKrn;
        String scontrctIndstrytyCdKrn;
        String cntrctChgTypeKrn;
        String cntrctNm;
        String cnsttyCdKrn;
        Long preCntrctAmt;
        String cntrctChgNo;
    }

    // 하도급 조회
    SubcontractMybatisParam.SubcontractListInput toSubcontractListInput(SubcontractListGet subcontractListGet);

    // 하도급 검색
    @Data
    @EqualsAndHashCode(callSuper = false)
    public class SubcontractListGet {
        String cntrctNo;
    }

    // 하도급 개별 조회
    SubcontractMybatisParam.SubcontractInput toSubcontractInput(String cntrctNo, Long scontrctCorpId);

    /* 하도급 삭제 */
    @Data
    public class SubcontractList {
        List<CnSubcontract> SubcontractList; // 수정된 부분
    }

    /* 하도급 수정 */
    void toUpdateSubcontract(UpdateSubcontract subcontract, @MappingTarget CnSubcontract cnSubcontract);

    @Data
    public class UpdateSubcontract {
        String cntrctNo;
        Long scontrctCorpId;
        String scontrctCntrctNo;
        String scontrctCntrctNm;
        String gcontrctCorpNo;
        String scontrctCorpNo;
        String scontrctCorpBsnsmnNo;
        String scontrctCorpAdrs;
        String scontrctTelNo;
        String scontrctFaxNo;
        String scontrctCorpCeo;
        String scontrctCorpNm;
        String scontrctCcnsttyCd;
        String scontrctIndstrytyCd;
        String cntrctDate;
        String cntrctBgnDate;
        String cntrctEndDate;
        Long scontrctCntrctAmt;
        String rmrk;
        String dltYn;
    }

    // ---------------------------------------------------------------------

    // 하도급 변경 조회
    SubcontractMybatisParam.SubcontractChangeListInput toSubcontractChangeListInput(SubcontractChangeListGet subcontractChangeListGet);

    // 하도급 변경 검색
    @Data
    @EqualsAndHashCode(callSuper = false)
    public class SubcontractChangeListGet {
        String cntrctNo;
        Long scontrctCorpId;
    }

    // 하도급 개별 변경 조회
    SubcontractMybatisParam.SubcontractChangeInput toSubcontractChangeInput(String cntrctNo, Long scontrctCorpId, Long cntrctChgId);

    // 변경 추가
    CnSubcontractChange toCreateSubcontractChange(CreateSubcontractChange subcontractChange);

    // 변경 추가
    @Data
    public class CreateSubcontractChange {
        String cntrctChgNo;
        Long scontrctCorpId;
        Long cntrctChgId;
        String cntrctNo;
        String cntrctChgType;
        String chgApprDate;
        String cntrctChgDate;
        String chgCbgnDate;
        Long chgConPrd;
        Long cntrctAmt;
        Long dfrcmpnstRate;
        Long vatRate;
        String rmrk;
        String dltYn;
    }

    /* 하도급 변경 삭제 */
    @Data
    public class SubcontractChangeList {
        List<CnSubcontractChange> SubcontractChangeList; // 수정된 부분
    }

    /* 하도급 변경 수정 */
    void toUpdateSubcontractChange(UpdateSubcontractChange subcontractChange,
            @MappingTarget CnSubcontractChange cnSubcontractChange);

    @Data
    public class UpdateSubcontractChange {

        String cntrctChgNo;
        Long scontrctCorpId;
        Long cntrctChgId;
        String cntrctNo;
        String cntrctChgType;
        String chgApprDate;
        String cntrctChgDate;
        String chgCbgnDate;
        Long chgConPrd;
        Long cntrctAmt;
        Long dfrcmpnstRate;
        Long vatRate;
        String rmrk;
        String dltYn;
    }
}

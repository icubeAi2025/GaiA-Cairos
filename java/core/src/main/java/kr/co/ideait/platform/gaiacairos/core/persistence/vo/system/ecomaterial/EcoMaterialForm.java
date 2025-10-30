package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.ecomaterial;

import jakarta.validation.constraints.NotBlank;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmEcoMaterial;
import kr.co.ideait.platform.gaiacairos.core.config.GlobalMapperConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = GlobalMapperConfig.class)
public interface EcoMaterialForm {

    // 조회
    EcoMaterialMybatisParam.EcoMaterialListInput toEcoMaterialListInput(EcoMaterialListGet ecoMaterialListGet);

    // 검색
    @Data
    @EqualsAndHashCode(callSuper = false)
    public class EcoMaterialListGet {
        String pjtNo;
        String cntrctNo;
        String searchTerm;
        String searchText;
    }

    // 친환경 자재 추가
    @Data
    public class CreateEcoMaterial {
        String cntrctNo;
        @NotBlank(message = "유형은 필수 입력 값입니다.")
        String ecoTpCd;
        String preCert;
        String finalCert;
        String makrNm;
        String certRsn;
        String rmrk;
        String dltYn;

        List<MaterialDto> materialList;
    }

    @Data
    public class MaterialDto {
        String ecoId;
        String gnrlexpnsCd;
        String rsceNm;
        String specNm;
        String unit;
    }

    // 친환경 자재 수정
    @Data
    public class UpdateEcoMaterial {
        String cntrctNo;
        String ecoTpCd;
        String preCert;
        String finalCert;
        String makrNm;
        String certRsn;
        String rmrk;
        String dltYn;

        List<String> ecoIdList;
    }

    // 친환경 자재 삭제
    @Data
    public class EcoMaterialList {
        List<SmEcoMaterial> ecoMaterialList;
    }

}

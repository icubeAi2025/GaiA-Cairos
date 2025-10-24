package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.commoncode;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmComCode;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmComCodeGroup;
import kr.co.ideait.platform.gaiacairos.core.config.GlobalMapperConfig;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MapDto;
import lombok.Data;
import org.apache.ibatis.type.Alias;
import org.mapstruct.Mapper;

import java.time.LocalDateTime;
import java.util.Map;

@Mapper(config = GlobalMapperConfig.class)
public interface CommonCodeDto {

    CommonCodeGroup fromSmComCodeGroup(SmComCodeGroup smComCodeGroup);

    CommonCode fromSmComCode(SmComCode smComCode);

    GetCommonCode getSmComCode(SmComCode smComCode);

    CommonCodeCombo fromSmComCodeToCombo(SmComCode smComCode);

    CommonCode fromSmComCodeMybatis(Map<String, ?> map);

    @Data
    class CommonCodeGroup {
        Integer cmnGrpNo;
        String cmnGrpCd;
        String cmnCd;
        String cmnCdNmEng;
        String cmnCdNmKrn;
        Short cmnCdDsplyOrdr;
        String cmnCdDscrpt;
        Integer upCmnGrpNo;
        String upCmnGrpCd;
        String publicYn;
        Short cmnLevel;
        String useYn;
        String dltYn;
        String rgstrId;
        LocalDateTime rgstDt;
        String chgId;
        LocalDateTime chgDt;
        String dltId;
        LocalDateTime dltDt;

    }

    @Alias("commonCode")
    class CommonCode extends MapDto {
        Integer cmnCdNo;
        Integer cmnGrpNo;
        String cmnGrpCd;
        String cmnCd;
        String cmnCdNmEng;
        String cmnCdNmKrn;
        Short cmnCdDsplyOrder;
        String cmnCdDscrpt;
        String useYn;
        String dltYn;
        String rgstrId;
        LocalDateTime rgstDt;
        String chgId;
        LocalDateTime chgDt;
        String dltId;
        LocalDateTime dltDt;
    }

    @Data
    class GetCommonCode{
        Integer cmnCdNo;
        Integer cmnGrpNo;
        String cmnGrpCd;
        String cmnCd;
        String cmnCdNmEng;
        String cmnCdNmKrn;
        Short cmnCdDsplyOrder;
        String cmnCdDscrpt;
        String useYn;
        String dltYn;
        String rgstrId;
        LocalDateTime rgstDt;
        String chgId;
        LocalDateTime chgDt;
        String dltId;
        LocalDateTime dltDt;
    }

    @Data
    class CommonCodeCombo {
        String cmnCd;
        String cmnCdNmKrn;
        String cmnCdNmEng;
        String cmnCdNm;
        String cmnCdDscrpt;
    }

}

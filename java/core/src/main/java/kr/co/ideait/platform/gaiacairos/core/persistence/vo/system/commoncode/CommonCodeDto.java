package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.commoncode;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.AbstractRudIdTime;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmComCode;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmComCodeGroup;
import kr.co.ideait.platform.gaiacairos.core.config.GlobalMapperConfig;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MapDto;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MyBatisParam;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;
import org.mapstruct.Mapper;

import java.time.LocalDateTime;
import java.util.Map;

@Mapper(config = GlobalMapperConfig.class)
public interface CommonCodeDto {

    CommonCodeGroup fromSmComCodeGroup(kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmComCodeGroup smComCodeGroup);

    CommonCode fromSmComCode(kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmComCode smComCode);

    GetCommonCode getSmComCode(kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmComCode smComCode);

    CommonCodeCombo fromSmComCodeToCombo(kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmComCode smComCode);

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

    @EqualsAndHashCode(callSuper = true)
    @Alias("smComCodeGroup")
    @Data
    class SmComCodeGroup extends MyBatisParam {
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
    }

    @EqualsAndHashCode(callSuper = true)
    @Alias("smComCode")
    @Data
    class SmComCode extends MyBatisParam {

        @Description(name = "SM_공통코드", description = "", type = Description.TYPE.FIELD)
        String cmnCdNo;
        Integer cmnGrpNo;
        String cmnGrpCd;
        String cmnCd;
        String cmnCdNmEng;
        String cmnCdNmKrn;
        Short cmnCdDsplyOrder;
        String cmnCdDscrpt;
        String attrbtCd1;
        String attrbtCd2;
        String attrbtCd3;
        String attrbtCd4;
        String attrbtCd5;
        String useYn;
    }
}

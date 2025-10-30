package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.commoncode;

import java.util.List;

import org.apache.ibatis.type.Alias;
import org.mapstruct.Mapper;

import kr.co.ideait.platform.gaiacairos.core.config.GlobalMapperConfig;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisPageable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Mapper(config = GlobalMapperConfig.class)
public interface CommonCodeMybatisParam {

    // 코드 개별 조회
    @Data
    @Alias("commonCodeGroupListOutput")
    public class CommonCodeGroupListOutput {
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
        Integer cmnLevel;
        String useYn;
        String dltYn;
        String rgstrId;
        String rgstDt;
        String chgId;
        String chgDt;
        String dltId;
        String dltDt;
    }

    @Data
    @Alias("commonCodeListInput")
    @EqualsAndHashCode(callSuper = true)
    public class CommonCodeListInput extends MybatisPageable {
        Integer cmnGrpNo;
        String searchText;

        List<Integer> cmnGrpNoList;

        String cmnCd;
        String cmnGrpCd;
        String cmnCdNmEng;
        String cmnCdNmKrn;
        String cmnCdDscrpt;

        String searchType;
        String startDt;
        String endDt;
    }

    @Data
    @Alias("commonCodeListOutput")
    @EqualsAndHashCode(callSuper = true)
    public class CommonCodeListOutput extends MybatisPageable {
        String cmnCdNo;
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
        String rgstrDt;
        String chgId;
        String chgDt;
        String dltId;
        String dltDt;

        Long totalNum;
    }

    // 그룹코드 순번 변경
    @Data
    @Alias("groupMoveInput")
    public class GroupMoveInput {
        Integer cmnGrpNo;
        Integer upCmnGrpNo;
    }

    // 코드 개별 조회
    @Data
    @Alias("codeInput")
    public class CodeInput {
        String cmnCdNo;
    }

    // 코드 개별 조회
    @Data
    @Alias("codeOutput")
    public class CodeOutput {
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
        String dltYn;

        String cmnCdGroupNmKrn;
    }

    // 공통 그룹 코드별 코드리스트 조회
    @Data
    @Alias("cmnGrpCdInput")
    public class CmnGrpCdInput {
        String cmnGrpCd;

        String lang;
    }
}

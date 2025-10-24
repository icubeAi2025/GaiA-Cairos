package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.commoncode;

import java.util.Map;
import javax.annotation.processing.Generated;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmComCode;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmComCodeGroup;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:17+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class CommonCodeDtoImpl implements CommonCodeDto {

    @Override
    public CommonCodeGroup fromSmComCodeGroup(SmComCodeGroup smComCodeGroup) {
        if ( smComCodeGroup == null ) {
            return null;
        }

        CommonCodeGroup commonCodeGroup = new CommonCodeGroup();

        commonCodeGroup.setCmnGrpNo( smComCodeGroup.getCmnGrpNo() );
        commonCodeGroup.setCmnGrpCd( smComCodeGroup.getCmnGrpCd() );
        commonCodeGroup.setCmnCd( smComCodeGroup.getCmnCd() );
        commonCodeGroup.setCmnCdNmEng( smComCodeGroup.getCmnCdNmEng() );
        commonCodeGroup.setCmnCdNmKrn( smComCodeGroup.getCmnCdNmKrn() );
        commonCodeGroup.setCmnCdDsplyOrdr( smComCodeGroup.getCmnCdDsplyOrdr() );
        commonCodeGroup.setCmnCdDscrpt( smComCodeGroup.getCmnCdDscrpt() );
        commonCodeGroup.setUpCmnGrpNo( smComCodeGroup.getUpCmnGrpNo() );
        commonCodeGroup.setUpCmnGrpCd( smComCodeGroup.getUpCmnGrpCd() );
        commonCodeGroup.setPublicYn( smComCodeGroup.getPublicYn() );
        commonCodeGroup.setCmnLevel( smComCodeGroup.getCmnLevel() );
        commonCodeGroup.setUseYn( smComCodeGroup.getUseYn() );
        commonCodeGroup.setDltYn( smComCodeGroup.getDltYn() );
        commonCodeGroup.setRgstrId( smComCodeGroup.getRgstrId() );
        commonCodeGroup.setRgstDt( smComCodeGroup.getRgstDt() );
        commonCodeGroup.setChgId( smComCodeGroup.getChgId() );
        commonCodeGroup.setChgDt( smComCodeGroup.getChgDt() );
        commonCodeGroup.setDltId( smComCodeGroup.getDltId() );
        commonCodeGroup.setDltDt( smComCodeGroup.getDltDt() );

        return commonCodeGroup;
    }

    @Override
    public CommonCode fromSmComCode(SmComCode smComCode) {
        if ( smComCode == null ) {
            return null;
        }

        CommonCode commonCode = new CommonCode();

        return commonCode;
    }

    @Override
    public GetCommonCode getSmComCode(SmComCode smComCode) {
        if ( smComCode == null ) {
            return null;
        }

        GetCommonCode getCommonCode = new GetCommonCode();

        if ( smComCode.getCmnCdNo() != null ) {
            getCommonCode.setCmnCdNo( Integer.parseInt( smComCode.getCmnCdNo() ) );
        }
        getCommonCode.setCmnGrpNo( smComCode.getCmnGrpNo() );
        getCommonCode.setCmnGrpCd( smComCode.getCmnGrpCd() );
        getCommonCode.setCmnCd( smComCode.getCmnCd() );
        getCommonCode.setCmnCdNmEng( smComCode.getCmnCdNmEng() );
        getCommonCode.setCmnCdNmKrn( smComCode.getCmnCdNmKrn() );
        getCommonCode.setCmnCdDsplyOrder( smComCode.getCmnCdDsplyOrder() );
        getCommonCode.setCmnCdDscrpt( smComCode.getCmnCdDscrpt() );
        getCommonCode.setUseYn( smComCode.getUseYn() );
        getCommonCode.setDltYn( smComCode.getDltYn() );
        getCommonCode.setRgstrId( smComCode.getRgstrId() );
        getCommonCode.setRgstDt( smComCode.getRgstDt() );
        getCommonCode.setChgId( smComCode.getChgId() );
        getCommonCode.setChgDt( smComCode.getChgDt() );
        getCommonCode.setDltId( smComCode.getDltId() );
        getCommonCode.setDltDt( smComCode.getDltDt() );

        return getCommonCode;
    }

    @Override
    public CommonCodeCombo fromSmComCodeToCombo(SmComCode smComCode) {
        if ( smComCode == null ) {
            return null;
        }

        CommonCodeCombo commonCodeCombo = new CommonCodeCombo();

        commonCodeCombo.setCmnCd( smComCode.getCmnCd() );
        commonCodeCombo.setCmnCdNmKrn( smComCode.getCmnCdNmKrn() );
        commonCodeCombo.setCmnCdNmEng( smComCode.getCmnCdNmEng() );
        commonCodeCombo.setCmnCdDscrpt( smComCode.getCmnCdDscrpt() );

        return commonCodeCombo;
    }

    @Override
    public CommonCode fromSmComCodeMybatis(Map<String, ?> map) {
        if ( map == null ) {
            return null;
        }

        CommonCode commonCode = new CommonCode();

        for ( java.util.Map.Entry<String, ?> entry : map.entrySet() ) {
            String key = entry.getKey();
            Object value = entry.getValue();
            commonCode.put( key, value );
        }

        return commonCode;
    }
}

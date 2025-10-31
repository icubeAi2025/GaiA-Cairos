package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.commoncode;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmComCode;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmComCodeGroup;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:16+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class CommonCodeFormImpl implements CommonCodeForm {

    @Override
    public SmComCode toSmComCodeTest(Test test) {
        if ( test == null ) {
            return null;
        }

        SmComCode smComCode = new SmComCode();

        return smComCode;
    }

    @Override
    public SmComCodeGroup toSmComCodeGroup(CommonCodeGroup commonCodeGroup) {
        if ( commonCodeGroup == null ) {
            return null;
        }

        SmComCodeGroup smComCodeGroup = new SmComCodeGroup();

        smComCodeGroup.setCmnCd( commonCodeGroup.getCmnCd() );
        smComCodeGroup.setCmnCdNmEng( commonCodeGroup.getCmnCdNmEng() );
        smComCodeGroup.setCmnCdNmKrn( commonCodeGroup.getCmnCdNmKrn() );
        smComCodeGroup.setCmnCdDsplyOrdr( commonCodeGroup.getCmnCdDsplyOrdr() );
        smComCodeGroup.setCmnCdDscrpt( commonCodeGroup.getCmnCdDscrpt() );
        smComCodeGroup.setUpCmnGrpNo( commonCodeGroup.getUpCmnGrpNo() );
        smComCodeGroup.setUpCmnGrpCd( commonCodeGroup.getUpCmnGrpCd() );
        smComCodeGroup.setPublicYn( commonCodeGroup.getPublicYn() );
        smComCodeGroup.setCmnLevel( commonCodeGroup.getCmnLevel() );
        smComCodeGroup.setUseYn( commonCodeGroup.getUseYn() );

        return smComCodeGroup;
    }

    @Override
    public SmComCode toSmComCode(CommonCode commonCode) {
        if ( commonCode == null ) {
            return null;
        }

        SmComCode smComCode = new SmComCode();

        smComCode.setCmnGrpNo( commonCode.getCmnGrpNo() );
        smComCode.setCmnGrpCd( commonCode.getCmnGrpCd() );
        smComCode.setCmnCd( commonCode.getCmnCd() );
        smComCode.setCmnCdNmEng( commonCode.getCmnCdNmEng() );
        smComCode.setCmnCdNmKrn( commonCode.getCmnCdNmKrn() );
        smComCode.setCmnCdDsplyOrder( commonCode.getCmnCdDsplyOrder() );
        smComCode.setCmnCdDscrpt( commonCode.getCmnCdDscrpt() );
        smComCode.setAttrbtCd1( commonCode.getAttrbtCd1() );
        smComCode.setAttrbtCd2( commonCode.getAttrbtCd2() );
        smComCode.setAttrbtCd3( commonCode.getAttrbtCd3() );
        smComCode.setAttrbtCd4( commonCode.getAttrbtCd4() );
        smComCode.setAttrbtCd5( commonCode.getAttrbtCd5() );
        smComCode.setUseYn( commonCode.getUseYn() );

        return smComCode;
    }

    @Override
    public CommonCodeMybatisParam.CommonCodeListInput toCommonCodeListInput(CommonCodeSearch commonCodeSearch) {
        if ( commonCodeSearch == null ) {
            return null;
        }

        CommonCodeMybatisParam.CommonCodeListInput commonCodeListInput = new CommonCodeMybatisParam.CommonCodeListInput();

        commonCodeListInput.setPageable( commonCodeSearch.getPageable() );
        commonCodeListInput.setCmnGrpNo( commonCodeSearch.getCmnGrpNo() );
        commonCodeListInput.setSearchText( commonCodeSearch.getSearchText() );
        commonCodeListInput.setCmnCd( commonCodeSearch.getCmnCd() );
        commonCodeListInput.setCmnGrpCd( commonCodeSearch.getCmnGrpCd() );
        commonCodeListInput.setCmnCdNmEng( commonCodeSearch.getCmnCdNmEng() );
        commonCodeListInput.setCmnCdNmKrn( commonCodeSearch.getCmnCdNmKrn() );
        commonCodeListInput.setCmnCdDscrpt( commonCodeSearch.getCmnCdDscrpt() );

        return commonCodeListInput;
    }

    @Override
    public CommonCodeMybatisParam.CommonCodeListInput toCommonCodeListInput(CommonCodeSearchMulti commonCodeSearch) {
        if ( commonCodeSearch == null ) {
            return null;
        }

        CommonCodeMybatisParam.CommonCodeListInput commonCodeListInput = new CommonCodeMybatisParam.CommonCodeListInput();

        commonCodeListInput.setPageable( commonCodeSearch.getPageable() );
        commonCodeListInput.setSearchText( commonCodeSearch.getSearchText() );
        List<Integer> list = commonCodeSearch.getCmnGrpNoList();
        if ( list != null ) {
            commonCodeListInput.setCmnGrpNoList( new ArrayList<Integer>( list ) );
        }
        commonCodeListInput.setCmnCd( commonCodeSearch.getCmnCd() );
        commonCodeListInput.setCmnCdNmEng( commonCodeSearch.getCmnCdNmEng() );
        commonCodeListInput.setCmnCdNmKrn( commonCodeSearch.getCmnCdNmKrn() );
        commonCodeListInput.setCmnCdDscrpt( commonCodeSearch.getCmnCdDscrpt() );

        return commonCodeListInput;
    }

    @Override
    public void updateSmComCodeGroup(CommonCodeGroupUpdate commonCodeGroup, SmComCodeGroup smComCodeGroup) {
        if ( commonCodeGroup == null ) {
            return;
        }

        if ( commonCodeGroup.getCmnGrpNo() != null ) {
            smComCodeGroup.setCmnGrpNo( commonCodeGroup.getCmnGrpNo() );
        }
        if ( commonCodeGroup.getCmnCdNmEng() != null ) {
            smComCodeGroup.setCmnCdNmEng( commonCodeGroup.getCmnCdNmEng() );
        }
        if ( commonCodeGroup.getCmnCdNmKrn() != null ) {
            smComCodeGroup.setCmnCdNmKrn( commonCodeGroup.getCmnCdNmKrn() );
        }
        if ( commonCodeGroup.getCmnCdDsplyOrdr() != null ) {
            smComCodeGroup.setCmnCdDsplyOrdr( commonCodeGroup.getCmnCdDsplyOrdr() );
        }
        if ( commonCodeGroup.getCmnCdDscrpt() != null ) {
            smComCodeGroup.setCmnCdDscrpt( commonCodeGroup.getCmnCdDscrpt() );
        }
        if ( commonCodeGroup.getUseYn() != null ) {
            smComCodeGroup.setUseYn( commonCodeGroup.getUseYn() );
        }
    }

    @Override
    public void updateSmComCode(CommonCodeUpdate commonCode, SmComCode smComCode) {
        if ( commonCode == null ) {
            return;
        }

        if ( commonCode.getCmnCdNo() != null ) {
            smComCode.setCmnCdNo( commonCode.getCmnCdNo() );
        }
        if ( commonCode.getCmnCd() != null ) {
            smComCode.setCmnCd( commonCode.getCmnCd() );
        }
        if ( commonCode.getCmnCdNmEng() != null ) {
            smComCode.setCmnCdNmEng( commonCode.getCmnCdNmEng() );
        }
        if ( commonCode.getCmnCdNmKrn() != null ) {
            smComCode.setCmnCdNmKrn( commonCode.getCmnCdNmKrn() );
        }
        smComCode.setCmnCdDsplyOrder( commonCode.getCmnCdDsplyOrder() );
        if ( commonCode.getCmnCdDscrpt() != null ) {
            smComCode.setCmnCdDscrpt( commonCode.getCmnCdDscrpt() );
        }
        if ( commonCode.getAttrbtCd1() != null ) {
            smComCode.setAttrbtCd1( commonCode.getAttrbtCd1() );
        }
        if ( commonCode.getAttrbtCd2() != null ) {
            smComCode.setAttrbtCd2( commonCode.getAttrbtCd2() );
        }
        if ( commonCode.getAttrbtCd3() != null ) {
            smComCode.setAttrbtCd3( commonCode.getAttrbtCd3() );
        }
        if ( commonCode.getAttrbtCd4() != null ) {
            smComCode.setAttrbtCd4( commonCode.getAttrbtCd4() );
        }
        if ( commonCode.getAttrbtCd5() != null ) {
            smComCode.setAttrbtCd5( commonCode.getAttrbtCd5() );
        }
        if ( commonCode.getUseYn() != null ) {
            smComCode.setUseYn( commonCode.getUseYn() );
        }
    }

    @Override
    public CommonCodeMybatisParam.CodeInput toCodeInput(Integer cmnGrpNo) {
        if ( cmnGrpNo == null ) {
            return null;
        }

        CommonCodeMybatisParam.CodeInput codeInput = new CommonCodeMybatisParam.CodeInput();

        return codeInput;
    }

    @Override
    public SmComCodeGroup toSmComCodeGroup(GroupMove groupMove) {
        if ( groupMove == null ) {
            return null;
        }

        SmComCodeGroup smComCodeGroup = new SmComCodeGroup();

        smComCodeGroup.setCmnGrpNo( groupMove.getCmnGrpNo() );
        smComCodeGroup.setUpCmnGrpNo( groupMove.getUpCmnGrpNo() );

        return smComCodeGroup;
    }
}

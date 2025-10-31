package kr.co.ideait.platform.gaiacairos.core.persistence.vo.safety;

import javax.annotation.processing.Generated;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwSafetyInspection;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwSafetyInspectionList;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwSafetyInspectionPhoto;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwStandardInspectionList;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:17+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class CheckFormImpl implements CheckForm {

    @Override
    public CwSafetyInspection toEntity(Safety safety) {
        if ( safety == null ) {
            return null;
        }

        CwSafetyInspection cwSafetyInspection = new CwSafetyInspection();

        cwSafetyInspection.setIspDt( CheckForm.stringToLocalDateTime( safety.getIspDt() ) );
        cwSafetyInspection.setCntrctNo( safety.getCntrctNo() );
        cwSafetyInspection.setInspectionNo( safety.getInspectionNo() );
        cwSafetyInspection.setIspDocNo( safety.getIspDocNo() );
        cwSafetyInspection.setTitle( safety.getTitle() );
        cwSafetyInspection.setCnsttyCd( safety.getCnsttyCd() );
        cwSafetyInspection.setCnsttyCdL1( safety.getCnsttyCdL1() );
        cwSafetyInspection.setCnsttyCdL2( safety.getCnsttyCdL2() );

        return cwSafetyInspection;
    }

    @Override
    public CwSafetyInspectionList toSafeEntity(SafetyList safetyList) {
        if ( safetyList == null ) {
            return null;
        }

        CwSafetyInspectionList cwSafetyInspectionList = new CwSafetyInspectionList();

        cwSafetyInspectionList.setCntrctNo( safetyList.getCntrctNo() );
        cwSafetyInspectionList.setIspLstId( safetyList.getIspLstId() );
        cwSafetyInspectionList.setIspLstNo( safetyList.getIspLstNo() );
        cwSafetyInspectionList.setIspSno( safetyList.getIspSno() );
        cwSafetyInspectionList.setCnsttyNm( safetyList.getCnsttyNm() );
        cwSafetyInspectionList.setIspDscrpt( safetyList.getIspDscrpt() );
        cwSafetyInspectionList.setGdFltyYn( safetyList.getGdFltyYn() );
        cwSafetyInspectionList.setImprvReq( safetyList.getImprvReq() );

        return cwSafetyInspectionList;
    }

    @Override
    public CwStandardInspectionList toStandardEntity(SafetyList safetyList) {
        if ( safetyList == null ) {
            return null;
        }

        CwStandardInspectionList cwStandardInspectionList = new CwStandardInspectionList();

        cwStandardInspectionList.setCntrctNo( safetyList.getCntrctNo() );
        cwStandardInspectionList.setIspLstId( safetyList.getIspLstId() );
        cwStandardInspectionList.setCnsttyYn( safetyList.getCnsttyYn() );
        cwStandardInspectionList.setCnsttyCd( safetyList.getCnsttyCd() );
        cwStandardInspectionList.setCnsttyNm( safetyList.getCnsttyNm() );
        cwStandardInspectionList.setCnsttyLvl( safetyList.getCnsttyLvl() );
        cwStandardInspectionList.setUpCnsttyCd( safetyList.getUpCnsttyCd() );
        cwStandardInspectionList.setIspLstDscrpt( safetyList.getIspLstDscrpt() );

        return cwStandardInspectionList;
    }

    @Override
    public CwSafetyInspectionPhoto toEntity(Photo photo) {
        if ( photo == null ) {
            return null;
        }

        CwSafetyInspectionPhoto cwSafetyInspectionPhoto = new CwSafetyInspectionPhoto();

        cwSafetyInspectionPhoto.setShotDate( CheckForm.stringToLocalDateTime( photo.getShotDate() ) );
        cwSafetyInspectionPhoto.setPhtSno( (short) photo.getPhtSno() );
        cwSafetyInspectionPhoto.setTitlNm( photo.getTitlNm() );
        cwSafetyInspectionPhoto.setDscrpt( photo.getDscrpt() );

        return cwSafetyInspectionPhoto;
    }

    @Override
    public void updateSafety(Safety safety, CwSafetyInspection safetyInspection) {
        if ( safety == null ) {
            return;
        }

        if ( safety.getIspDt() != null ) {
            safetyInspection.setIspDt( CheckForm.stringToLocalDateTime( safety.getIspDt() ) );
        }
        if ( safety.getCntrctNo() != null ) {
            safetyInspection.setCntrctNo( safety.getCntrctNo() );
        }
        if ( safety.getInspectionNo() != null ) {
            safetyInspection.setInspectionNo( safety.getInspectionNo() );
        }
        if ( safety.getIspDocNo() != null ) {
            safetyInspection.setIspDocNo( safety.getIspDocNo() );
        }
        if ( safety.getTitle() != null ) {
            safetyInspection.setTitle( safety.getTitle() );
        }
        if ( safety.getCnsttyCd() != null ) {
            safetyInspection.setCnsttyCd( safety.getCnsttyCd() );
        }
        if ( safety.getCnsttyCdL1() != null ) {
            safetyInspection.setCnsttyCdL1( safety.getCnsttyCdL1() );
        }
        if ( safety.getCnsttyCdL2() != null ) {
            safetyInspection.setCnsttyCdL2( safety.getCnsttyCdL2() );
        }
    }
}

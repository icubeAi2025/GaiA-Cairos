package kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.overview.information;

import javax.annotation.processing.Generated;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnProject;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:17+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class InformationFormImpl implements InformationForm {

    @Override
    public InformationMybatisParam.InformationListInput toInformationListInput(InformationListGet informationListGet) {
        if ( informationListGet == null ) {
            return null;
        }

        InformationMybatisParam.InformationListInput informationListInput = new InformationMybatisParam.InformationListInput();

        informationListInput.setSearchTerm( informationListGet.getSearchTerm() );
        informationListInput.setPjtDiv( informationListGet.getPjtDiv() );
        informationListInput.setStartDate( informationListGet.getStartDate() );
        informationListInput.setEndDate( informationListGet.getEndDate() );
        informationListInput.setSearchType( informationListGet.getSearchType() );
        informationListInput.setSearchText( informationListGet.getSearchText() );
        informationListInput.setLoginId( informationListGet.getLoginId() );

        return informationListInput;
    }

    @Override
    public CnProject toRegisterInformation(RegisterInformation information) {
        if ( information == null ) {
            return null;
        }

        CnProject cnProject = new CnProject();

        cnProject.setPjtNo( information.getPjtNo() );
        cnProject.setPjtDiv( information.getPjtDiv() );
        cnProject.setPjtNm( information.getPjtNm() );
        cnProject.setPlcReqNo( information.getPlcReqNo() );
        cnProject.setCnstwkType( information.getCnstwkType() );
        cnProject.setPlcLctAdrs( information.getPlcLctAdrs() );
        cnProject.setPlcLctX( information.getPlcLctX() );
        cnProject.setPlcLctY( information.getPlcLctY() );
        cnProject.setPjtBgnDate( information.getPjtBgnDate() );
        cnProject.setPjtEndDate( information.getPjtEndDate() );
        cnProject.setCnstwkDaynum( information.getCnstwkDaynum() );
        cnProject.setAprvlDate( information.getAprvlDate() );
        cnProject.setNtpDate( information.getNtpDate() );
        cnProject.setAcrarchlawUsgCd( information.getAcrarchlawUsgCd() );
        cnProject.setCntrctType( information.getCntrctType() );
        cnProject.setCnstwkScle( information.getCnstwkScle() );
        cnProject.setParkngPsblNum( information.getParkngPsblNum() );
        cnProject.setTotarVal( information.getTotarVal() );
        cnProject.setLndAreaVal( information.getLndAreaVal() );
        cnProject.setArchtctAreaVal( information.getArchtctAreaVal() );
        cnProject.setLandarchtAreaVal( information.getLandarchtAreaVal() );
        cnProject.setBdtlRate( information.getBdtlRate() );
        cnProject.setMeasrmtRate( information.getMeasrmtRate() );
        cnProject.setBssFloorHgVal( information.getBssFloorHgVal() );
        cnProject.setTopHgVal( information.getTopHgVal() );
        cnProject.setMainFcltyCntnts( information.getMainFcltyCntnts() );
        cnProject.setDminsttCd( information.getDminsttCd() );
        cnProject.setDminsttNm( information.getDminsttNm() );
        cnProject.setDminsttDiv( information.getDminsttDiv() );
        cnProject.setCnstwkCst( information.getCnstwkCst() );
        cnProject.setChgCntrctAmt( information.getChgCntrctAmt() );
        cnProject.setConPstats( information.getConPstats() );
        cnProject.setUseYn( information.getUseYn() );
        cnProject.setAirvwAtchFileNo( information.getAirvwAtchFileNo() );
        cnProject.setEtcCntnts( information.getEtcCntnts() );
        cnProject.setRgnCd( information.getRgnCd() );
        cnProject.setInsptrNm( information.getInsptrNm() );
        cnProject.setCmNm( information.getCmNm() );
        cnProject.setSpvsCorpNm( information.getSpvsCorpNm() );
        cnProject.setCntrctCorpNm( information.getCntrctCorpNm() );
        cnProject.setEvrfrndScr( information.getEvrfrndScr() );
        cnProject.setEnergyScr( information.getEnergyScr() );
        cnProject.setBfScr( information.getBfScr() );
        cnProject.setGreenLevel( information.getGreenLevel() );
        cnProject.setGreenLevelDocId( information.getGreenLevelDocId() );
        cnProject.setEnergyEffectLevel( information.getEnergyEffectLevel() );
        cnProject.setEnergyEffectLevelDocId( information.getEnergyEffectLevelDocId() );
        cnProject.setZeroEnergyLevel( information.getZeroEnergyLevel() );
        cnProject.setZeroEnergyLevelDocId( information.getZeroEnergyLevelDocId() );
        cnProject.setBfLevel( information.getBfLevel() );
        cnProject.setBfLevelDocId( information.getBfLevelDocId() );
        cnProject.setEvironmentMtrl( information.getEvironmentMtrl() );
        cnProject.setCo2Mtrl( information.getCo2Mtrl() );
        cnProject.setEcoMtrl( information.getEcoMtrl() );

        return cnProject;
    }

    @Override
    public void updateCnProject(InformationUpdate informationUpdate, CnProject cnProject) {
        if ( informationUpdate == null ) {
            return;
        }

        if ( informationUpdate.getPjtNo() != null ) {
            cnProject.setPjtNo( informationUpdate.getPjtNo() );
        }
        if ( informationUpdate.getPjtNm() != null ) {
            cnProject.setPjtNm( informationUpdate.getPjtNm() );
        }
        if ( informationUpdate.getCnstwkType() != null ) {
            cnProject.setCnstwkType( informationUpdate.getCnstwkType() );
        }
        if ( informationUpdate.getPlcLctAdrs() != null ) {
            cnProject.setPlcLctAdrs( informationUpdate.getPlcLctAdrs() );
        }
        cnProject.setPlcLctX( informationUpdate.getPlcLctX() );
        cnProject.setPlcLctY( informationUpdate.getPlcLctY() );
        if ( informationUpdate.getPjtBgnDate() != null ) {
            cnProject.setPjtBgnDate( informationUpdate.getPjtBgnDate() );
        }
        if ( informationUpdate.getPjtEndDate() != null ) {
            cnProject.setPjtEndDate( informationUpdate.getPjtEndDate() );
        }
        if ( informationUpdate.getCnstwkDaynum() != null ) {
            cnProject.setCnstwkDaynum( informationUpdate.getCnstwkDaynum() );
        }
        if ( informationUpdate.getAprvlDate() != null ) {
            cnProject.setAprvlDate( informationUpdate.getAprvlDate() );
        }
        if ( informationUpdate.getNtpDate() != null ) {
            cnProject.setNtpDate( informationUpdate.getNtpDate() );
        }
        if ( informationUpdate.getAcrarchlawUsgCd() != null ) {
            cnProject.setAcrarchlawUsgCd( informationUpdate.getAcrarchlawUsgCd() );
        }
        if ( informationUpdate.getCntrctType() != null ) {
            cnProject.setCntrctType( informationUpdate.getCntrctType() );
        }
        if ( informationUpdate.getCnstwkScle() != null ) {
            cnProject.setCnstwkScle( informationUpdate.getCnstwkScle() );
        }
        if ( informationUpdate.getParkngPsblNum() != null ) {
            cnProject.setParkngPsblNum( informationUpdate.getParkngPsblNum() );
        }
        cnProject.setTotarVal( informationUpdate.getTotarVal() );
        cnProject.setLndAreaVal( informationUpdate.getLndAreaVal() );
        cnProject.setArchtctAreaVal( informationUpdate.getArchtctAreaVal() );
        cnProject.setLandarchtAreaVal( informationUpdate.getLandarchtAreaVal() );
        cnProject.setBdtlRate( informationUpdate.getBdtlRate() );
        cnProject.setMeasrmtRate( informationUpdate.getMeasrmtRate() );
        cnProject.setBssFloorHgVal( informationUpdate.getBssFloorHgVal() );
        cnProject.setTopHgVal( informationUpdate.getTopHgVal() );
        if ( informationUpdate.getMainFcltyCntnts() != null ) {
            cnProject.setMainFcltyCntnts( informationUpdate.getMainFcltyCntnts() );
        }
        if ( informationUpdate.getDminsttCd() != null ) {
            cnProject.setDminsttCd( informationUpdate.getDminsttCd() );
        }
        if ( informationUpdate.getDminsttNm() != null ) {
            cnProject.setDminsttNm( informationUpdate.getDminsttNm() );
        }
        if ( informationUpdate.getDminsttDiv() != null ) {
            cnProject.setDminsttDiv( informationUpdate.getDminsttDiv() );
        }
        if ( informationUpdate.getCnstwkCst() != null ) {
            cnProject.setCnstwkCst( informationUpdate.getCnstwkCst() );
        }
        if ( informationUpdate.getChgCntrctAmt() != null ) {
            cnProject.setChgCntrctAmt( informationUpdate.getChgCntrctAmt() );
        }
        if ( informationUpdate.getConPstats() != null ) {
            cnProject.setConPstats( informationUpdate.getConPstats() );
        }
        if ( informationUpdate.getUseYn() != null ) {
            cnProject.setUseYn( informationUpdate.getUseYn() );
        }
        if ( informationUpdate.getAirvwAtchFileNo() != null ) {
            cnProject.setAirvwAtchFileNo( informationUpdate.getAirvwAtchFileNo() );
        }
        if ( informationUpdate.getEtcCntnts() != null ) {
            cnProject.setEtcCntnts( informationUpdate.getEtcCntnts() );
        }
        if ( informationUpdate.getRgnCd() != null ) {
            cnProject.setRgnCd( informationUpdate.getRgnCd() );
        }
        if ( informationUpdate.getInsptrNm() != null ) {
            cnProject.setInsptrNm( informationUpdate.getInsptrNm() );
        }
        if ( informationUpdate.getCmNm() != null ) {
            cnProject.setCmNm( informationUpdate.getCmNm() );
        }
        if ( informationUpdate.getSpvsCorpNm() != null ) {
            cnProject.setSpvsCorpNm( informationUpdate.getSpvsCorpNm() );
        }
        if ( informationUpdate.getCntrctCorpNm() != null ) {
            cnProject.setCntrctCorpNm( informationUpdate.getCntrctCorpNm() );
        }
        if ( informationUpdate.getEvrfrndScr() != null ) {
            cnProject.setEvrfrndScr( informationUpdate.getEvrfrndScr() );
        }
        if ( informationUpdate.getEnergyScr() != null ) {
            cnProject.setEnergyScr( informationUpdate.getEnergyScr() );
        }
        if ( informationUpdate.getBfScr() != null ) {
            cnProject.setBfScr( informationUpdate.getBfScr() );
        }
        if ( informationUpdate.getGreenLevel() != null ) {
            cnProject.setGreenLevel( informationUpdate.getGreenLevel() );
        }
        if ( informationUpdate.getGreenLevelDocId() != null ) {
            cnProject.setGreenLevelDocId( informationUpdate.getGreenLevelDocId() );
        }
        if ( informationUpdate.getEnergyEffectLevel() != null ) {
            cnProject.setEnergyEffectLevel( informationUpdate.getEnergyEffectLevel() );
        }
        if ( informationUpdate.getEnergyEffectLevelDocId() != null ) {
            cnProject.setEnergyEffectLevelDocId( informationUpdate.getEnergyEffectLevelDocId() );
        }
        if ( informationUpdate.getZeroEnergyLevel() != null ) {
            cnProject.setZeroEnergyLevel( informationUpdate.getZeroEnergyLevel() );
        }
        if ( informationUpdate.getZeroEnergyLevelDocId() != null ) {
            cnProject.setZeroEnergyLevelDocId( informationUpdate.getZeroEnergyLevelDocId() );
        }
        if ( informationUpdate.getBfLevel() != null ) {
            cnProject.setBfLevel( informationUpdate.getBfLevel() );
        }
        if ( informationUpdate.getBfLevelDocId() != null ) {
            cnProject.setBfLevelDocId( informationUpdate.getBfLevelDocId() );
        }
        if ( informationUpdate.getEvironmentMtrl() != null ) {
            cnProject.setEvironmentMtrl( informationUpdate.getEvironmentMtrl() );
        }
        if ( informationUpdate.getCo2Mtrl() != null ) {
            cnProject.setCo2Mtrl( informationUpdate.getCo2Mtrl() );
        }
        if ( informationUpdate.getEcoMtrl() != null ) {
            cnProject.setEcoMtrl( informationUpdate.getEcoMtrl() );
        }
    }
}

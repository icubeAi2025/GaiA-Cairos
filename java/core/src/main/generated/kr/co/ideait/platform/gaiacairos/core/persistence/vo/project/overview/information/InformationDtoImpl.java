package kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.overview.information;

import javax.annotation.processing.Generated;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnAttachments;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnProject;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:18+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class InformationDtoImpl implements InformationDto {

    @Override
    public Information fromCnProjectOutput(InformationMybatisParam.InformationOutput informationOutput) {
        if ( informationOutput == null ) {
            return null;
        }

        Information information = new Information();

        information.setPjtNo( informationOutput.getPjtNo() );
        information.setPjtNm( informationOutput.getPjtNm() );
        information.setCnstwkType( informationOutput.getCnstwkType() );
        information.setPlcLctAdrs( informationOutput.getPlcLctAdrs() );
        information.setPlcLctX( informationOutput.getPlcLctX() );
        information.setPlcLctY( informationOutput.getPlcLctY() );
        information.setPjtBgnDate( informationOutput.getPjtBgnDate() );
        information.setPjtEndDate( informationOutput.getPjtEndDate() );
        information.setCnstwkDaynum( informationOutput.getCnstwkDaynum() );
        information.setAprvlDate( informationOutput.getAprvlDate() );
        information.setNtpDate( informationOutput.getNtpDate() );
        information.setAcrarchlawUsgCd( informationOutput.getAcrarchlawUsgCd() );
        information.setCntrctType( informationOutput.getCntrctType() );
        information.setCnstwkScle( informationOutput.getCnstwkScle() );
        information.setParkngPsblNum( informationOutput.getParkngPsblNum() );
        information.setTotarVal( informationOutput.getTotarVal() );
        information.setLndAreaVal( informationOutput.getLndAreaVal() );
        information.setArchtctAreaVal( informationOutput.getArchtctAreaVal() );
        information.setLandarchtAreaVal( informationOutput.getLandarchtAreaVal() );
        information.setBdtlRate( informationOutput.getBdtlRate() );
        information.setMeasrmtRate( informationOutput.getMeasrmtRate() );
        information.setBssFloorHgVal( informationOutput.getBssFloorHgVal() );
        information.setTopHgVal( informationOutput.getTopHgVal() );
        information.setMainFcltyCntnts( informationOutput.getMainFcltyCntnts() );
        information.setDminsttCd( informationOutput.getDminsttCd() );
        information.setDminsttNm( informationOutput.getDminsttNm() );
        information.setCnstwkCst( informationOutput.getCnstwkCst() );
        information.setChgCntrctAmt( informationOutput.getChgCntrctAmt() );
        information.setConPstats( informationOutput.getConPstats() );
        information.setUseYn( informationOutput.getUseYn() );
        information.setAirvwAtchFileNo( informationOutput.getAirvwAtchFileNo() );
        information.setEtcCntnts( informationOutput.getEtcCntnts() );
        information.setConPstatsNm( informationOutput.getConPstatsNm() );
        information.setConPstatsNmEng( informationOutput.getConPstatsNmEng() );
        information.setConPstatsNmKrn( informationOutput.getConPstatsNmKrn() );
        information.setCntrctTypeNm( informationOutput.getCntrctTypeNm() );
        information.setCntrctTypeNmKrn( informationOutput.getCntrctTypeNmKrn() );
        information.setCntrctTypeNmEng( informationOutput.getCntrctTypeNmEng() );
        information.setRgnCd( informationOutput.getRgnCd() );
        information.setInsptrNm( informationOutput.getInsptrNm() );
        information.setCmNm( informationOutput.getCmNm() );
        information.setSpvsCorpNm( informationOutput.getSpvsCorpNm() );
        information.setCntrctCorpNm( informationOutput.getCntrctCorpNm() );
        information.setEvrfrndScr( informationOutput.getEvrfrndScr() );
        information.setEnergyScr( informationOutput.getEnergyScr() );
        information.setBfScr( informationOutput.getBfScr() );
        information.setGreenLevel( informationOutput.getGreenLevel() );
        information.setGreenLevelDocId( informationOutput.getGreenLevelDocId() );
        information.setEnergyEffectLevel( informationOutput.getEnergyEffectLevel() );
        information.setEnergyEffectLevelDocId( informationOutput.getEnergyEffectLevelDocId() );
        information.setZeroEnergyLevel( informationOutput.getZeroEnergyLevel() );
        information.setZeroEnergyLevelDocId( informationOutput.getZeroEnergyLevelDocId() );
        information.setBfLevel( informationOutput.getBfLevel() );
        information.setBfLevelDocId( informationOutput.getBfLevelDocId() );
        information.setEvironmentMtrl( informationOutput.getEvironmentMtrl() );
        information.setCo2Mtrl( informationOutput.getCo2Mtrl() );
        information.setEcoMtrl( informationOutput.getEcoMtrl() );

        return information;
    }

    @Override
    public InformationMybatisParam.InformationOutput toInformation(InformationMybatisParam.InformationOutput InformationOutput) {
        if ( InformationOutput == null ) {
            return null;
        }

        InformationMybatisParam.InformationOutput informationOutput = new InformationMybatisParam.InformationOutput();

        informationOutput.setPjtNo( InformationOutput.getPjtNo() );
        informationOutput.setPjtNm( InformationOutput.getPjtNm() );
        informationOutput.setCnstwkType( InformationOutput.getCnstwkType() );
        informationOutput.setPlcLctAdrs( InformationOutput.getPlcLctAdrs() );
        informationOutput.setPlcLctX( InformationOutput.getPlcLctX() );
        informationOutput.setPlcLctY( InformationOutput.getPlcLctY() );
        informationOutput.setRgnCd( InformationOutput.getRgnCd() );
        informationOutput.setPjtBgnDate( InformationOutput.getPjtBgnDate() );
        informationOutput.setPjtEndDate( InformationOutput.getPjtEndDate() );
        informationOutput.setCnstwkDaynum( InformationOutput.getCnstwkDaynum() );
        informationOutput.setAprvlDate( InformationOutput.getAprvlDate() );
        informationOutput.setNtpDate( InformationOutput.getNtpDate() );
        informationOutput.setAcrarchlawUsgCd( InformationOutput.getAcrarchlawUsgCd() );
        informationOutput.setCntrctType( InformationOutput.getCntrctType() );
        informationOutput.setCnstwkScle( InformationOutput.getCnstwkScle() );
        informationOutput.setParkngPsblNum( InformationOutput.getParkngPsblNum() );
        informationOutput.setTotarVal( InformationOutput.getTotarVal() );
        informationOutput.setLndAreaVal( InformationOutput.getLndAreaVal() );
        informationOutput.setArchtctAreaVal( InformationOutput.getArchtctAreaVal() );
        informationOutput.setLandarchtAreaVal( InformationOutput.getLandarchtAreaVal() );
        informationOutput.setBdtlRate( InformationOutput.getBdtlRate() );
        informationOutput.setMeasrmtRate( InformationOutput.getMeasrmtRate() );
        informationOutput.setBssFloorHgVal( InformationOutput.getBssFloorHgVal() );
        informationOutput.setTopHgVal( InformationOutput.getTopHgVal() );
        informationOutput.setMainFcltyCntnts( InformationOutput.getMainFcltyCntnts() );
        informationOutput.setDminsttDiv( InformationOutput.getDminsttDiv() );
        informationOutput.setDminsttNm( InformationOutput.getDminsttNm() );
        informationOutput.setDminsttCd( InformationOutput.getDminsttCd() );
        informationOutput.setCnstwkCst( InformationOutput.getCnstwkCst() );
        informationOutput.setChgCntrctAmt( InformationOutput.getChgCntrctAmt() );
        informationOutput.setConPstats( InformationOutput.getConPstats() );
        informationOutput.setUseYn( InformationOutput.getUseYn() );
        informationOutput.setAirvwAtchFileNo( InformationOutput.getAirvwAtchFileNo() );
        informationOutput.setEvrfrndScr( InformationOutput.getEvrfrndScr() );
        informationOutput.setEnergyScr( InformationOutput.getEnergyScr() );
        informationOutput.setBfScr( InformationOutput.getBfScr() );
        informationOutput.setGreenLevel( InformationOutput.getGreenLevel() );
        informationOutput.setGreenLevelDocId( InformationOutput.getGreenLevelDocId() );
        informationOutput.setEnergyEffectLevel( InformationOutput.getEnergyEffectLevel() );
        informationOutput.setEnergyEffectLevelDocId( InformationOutput.getEnergyEffectLevelDocId() );
        informationOutput.setZeroEnergyLevel( InformationOutput.getZeroEnergyLevel() );
        informationOutput.setZeroEnergyLevelDocId( InformationOutput.getZeroEnergyLevelDocId() );
        informationOutput.setBfLevel( InformationOutput.getBfLevel() );
        informationOutput.setBfLevelDocId( InformationOutput.getBfLevelDocId() );
        informationOutput.setEvironmentMtrl( InformationOutput.getEvironmentMtrl() );
        informationOutput.setCo2Mtrl( InformationOutput.getCo2Mtrl() );
        informationOutput.setEcoMtrl( InformationOutput.getEcoMtrl() );
        informationOutput.setConPstatsNmKrn( InformationOutput.getConPstatsNmKrn() );
        informationOutput.setConPstatsNmEng( InformationOutput.getConPstatsNmEng() );
        informationOutput.setCntrctTypeNmKrn( InformationOutput.getCntrctTypeNmKrn() );
        informationOutput.setCntrctTypeNmEng( InformationOutput.getCntrctTypeNmEng() );
        informationOutput.setEtcCntnts( InformationOutput.getEtcCntnts() );
        informationOutput.setPeriodDate( InformationOutput.getPeriodDate() );
        informationOutput.setRgstrId( InformationOutput.getRgstrId() );
        informationOutput.setRgstrDt( InformationOutput.getRgstrDt() );
        informationOutput.setChgId( InformationOutput.getChgId() );
        informationOutput.setChgDt( InformationOutput.getChgDt() );
        informationOutput.setDltId( InformationOutput.getDltId() );
        informationOutput.setDltDt( InformationOutput.getDltDt() );
        informationOutput.setCnstwkTypeNm( InformationOutput.getCnstwkTypeNm() );
        informationOutput.setRgnCdNm( InformationOutput.getRgnCdNm() );
        informationOutput.setAcrarchlawUsgCdNm( InformationOutput.getAcrarchlawUsgCdNm() );
        informationOutput.setCntrctTypeNm( InformationOutput.getCntrctTypeNm() );
        informationOutput.setConPstatsNm( InformationOutput.getConPstatsNm() );
        informationOutput.setInsptrNm( InformationOutput.getInsptrNm() );
        informationOutput.setCmNm( InformationOutput.getCmNm() );
        informationOutput.setSpvsCorpNm( InformationOutput.getSpvsCorpNm() );
        informationOutput.setCntrctCorpNm( InformationOutput.getCntrctCorpNm() );
        informationOutput.setCntrctNo( InformationOutput.getCntrctNo() );

        return informationOutput;
    }

    @Override
    public registerInformation toInformationRegister(CnProject cnProject) {
        if ( cnProject == null ) {
            return null;
        }

        registerInformation registerInformation = new registerInformation();

        registerInformation.setPjtNm( cnProject.getPjtNm() );
        if ( cnProject.getCnstwkDaynum() != null ) {
            registerInformation.setCnstwkDaynum( cnProject.getCnstwkDaynum() );
        }
        registerInformation.setCntrctType( cnProject.getCntrctType() );
        registerInformation.setAprvlDate( cnProject.getAprvlDate() );
        registerInformation.setMainFcltyCntnts( cnProject.getMainFcltyCntnts() );
        registerInformation.setDminsttNm( cnProject.getDminsttNm() );

        return registerInformation;
    }

    @Override
    public Information fromCnProject(CnProject cnProject) {
        if ( cnProject == null ) {
            return null;
        }

        Information information = new Information();

        information.setPjtNo( cnProject.getPjtNo() );
        information.setPjtNm( cnProject.getPjtNm() );
        information.setCnstwkType( cnProject.getCnstwkType() );
        information.setPlcLctAdrs( cnProject.getPlcLctAdrs() );
        if ( cnProject.getPlcLctX() != null ) {
            information.setPlcLctX( cnProject.getPlcLctX() );
        }
        if ( cnProject.getPlcLctY() != null ) {
            information.setPlcLctY( cnProject.getPlcLctY() );
        }
        information.setPjtBgnDate( cnProject.getPjtBgnDate() );
        information.setPjtEndDate( cnProject.getPjtEndDate() );
        information.setCnstwkDaynum( cnProject.getCnstwkDaynum() );
        information.setAprvlDate( cnProject.getAprvlDate() );
        information.setNtpDate( cnProject.getNtpDate() );
        information.setAcrarchlawUsgCd( cnProject.getAcrarchlawUsgCd() );
        information.setCntrctType( cnProject.getCntrctType() );
        information.setCnstwkScle( cnProject.getCnstwkScle() );
        information.setParkngPsblNum( cnProject.getParkngPsblNum() );
        if ( cnProject.getTotarVal() != null ) {
            information.setTotarVal( cnProject.getTotarVal() );
        }
        if ( cnProject.getLndAreaVal() != null ) {
            information.setLndAreaVal( cnProject.getLndAreaVal() );
        }
        if ( cnProject.getArchtctAreaVal() != null ) {
            information.setArchtctAreaVal( cnProject.getArchtctAreaVal() );
        }
        if ( cnProject.getLandarchtAreaVal() != null ) {
            information.setLandarchtAreaVal( cnProject.getLandarchtAreaVal() );
        }
        if ( cnProject.getBdtlRate() != null ) {
            information.setBdtlRate( cnProject.getBdtlRate() );
        }
        if ( cnProject.getMeasrmtRate() != null ) {
            information.setMeasrmtRate( cnProject.getMeasrmtRate() );
        }
        if ( cnProject.getBssFloorHgVal() != null ) {
            information.setBssFloorHgVal( cnProject.getBssFloorHgVal() );
        }
        if ( cnProject.getTopHgVal() != null ) {
            information.setTopHgVal( cnProject.getTopHgVal() );
        }
        information.setMainFcltyCntnts( cnProject.getMainFcltyCntnts() );
        information.setDminsttCd( cnProject.getDminsttCd() );
        information.setDminsttNm( cnProject.getDminsttNm() );
        information.setCnstwkCst( cnProject.getCnstwkCst() );
        information.setChgCntrctAmt( cnProject.getChgCntrctAmt() );
        information.setConPstats( cnProject.getConPstats() );
        information.setUseYn( cnProject.getUseYn() );
        information.setAirvwAtchFileNo( cnProject.getAirvwAtchFileNo() );
        information.setEtcCntnts( cnProject.getEtcCntnts() );
        information.setRgnCd( cnProject.getRgnCd() );
        information.setInsptrNm( cnProject.getInsptrNm() );
        information.setCmNm( cnProject.getCmNm() );
        information.setSpvsCorpNm( cnProject.getSpvsCorpNm() );
        information.setCntrctCorpNm( cnProject.getCntrctCorpNm() );
        information.setEvrfrndScr( cnProject.getEvrfrndScr() );
        information.setEnergyScr( cnProject.getEnergyScr() );
        information.setBfScr( cnProject.getBfScr() );
        information.setGreenLevel( cnProject.getGreenLevel() );
        information.setGreenLevelDocId( cnProject.getGreenLevelDocId() );
        information.setEnergyEffectLevel( cnProject.getEnergyEffectLevel() );
        information.setEnergyEffectLevelDocId( cnProject.getEnergyEffectLevelDocId() );
        information.setZeroEnergyLevel( cnProject.getZeroEnergyLevel() );
        information.setZeroEnergyLevelDocId( cnProject.getZeroEnergyLevelDocId() );
        information.setBfLevel( cnProject.getBfLevel() );
        information.setBfLevelDocId( cnProject.getBfLevelDocId() );
        information.setEvironmentMtrl( cnProject.getEvironmentMtrl() );
        information.setCo2Mtrl( cnProject.getCo2Mtrl() );
        information.setEcoMtrl( cnProject.getEcoMtrl() );

        return information;
    }

    @Override
    public infoAttachMent toInfoAttachments(CnAttachments cnAttachments) {
        if ( cnAttachments == null ) {
            return null;
        }

        infoAttachMent infoAttachMent = new infoAttachMent();

        if ( cnAttachments.getFileNo() != null ) {
            infoAttachMent.setFileNo( cnAttachments.getFileNo() );
        }
        if ( cnAttachments.getSno() != null ) {
            infoAttachMent.setSno( cnAttachments.getSno() );
        }
        infoAttachMent.setFileNm( cnAttachments.getFileNm() );
        infoAttachMent.setFileDiskNm( cnAttachments.getFileDiskNm() );
        infoAttachMent.setFileDiskPath( cnAttachments.getFileDiskPath() );
        if ( cnAttachments.getFileSize() != null ) {
            infoAttachMent.setFileSize( cnAttachments.getFileSize() );
        }
        if ( cnAttachments.getFileHitNum() != null ) {
            infoAttachMent.setFileHitNum( cnAttachments.getFileHitNum() );
        }
        infoAttachMent.setDltYn( cnAttachments.getDltYn() );

        return infoAttachMent;
    }
}

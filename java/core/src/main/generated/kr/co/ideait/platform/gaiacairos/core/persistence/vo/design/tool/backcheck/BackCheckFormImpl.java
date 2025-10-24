package kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.backcheck;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:17+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class BackCheckFormImpl implements BackCheckForm {

    @Override
    public BackCheckMybatisParam.BackCheckListInput toBackCheckListInput(BackCheckList backCheckList) {
        if ( backCheckList == null ) {
            return null;
        }

        BackCheckMybatisParam.BackCheckListInput backCheckListInput = new BackCheckMybatisParam.BackCheckListInput();

        backCheckListInput.setPageable( backCheckList.getPageable() );
        backCheckListInput.setDsgnPhaseNo( backCheckList.getDsgnPhaseNo() );
        backCheckListInput.setCntrctNo( backCheckList.getCntrctNo() );
        backCheckListInput.setDsgnPhaseCd( backCheckList.getDsgnPhaseCd() );
        backCheckListInput.setDsgnCd( backCheckList.getDsgnCd() );
        backCheckListInput.setBackchkStatus( backCheckList.getBackchkStatus() );
        backCheckListInput.setKeyword( backCheckList.getKeyword() );
        backCheckListInput.setRgstrNm( backCheckList.getRgstrNm() );
        backCheckListInput.setMyRplyYn( backCheckList.getMyRplyYn() );
        backCheckListInput.setStartDsgnNo( backCheckList.getStartDsgnNo() );
        backCheckListInput.setEndDsgnNo( backCheckList.getEndDsgnNo() );
        backCheckListInput.setRplyCd( backCheckList.getRplyCd() );
        backCheckListInput.setApprerCd( backCheckList.getApprerCd() );
        backCheckListInput.setBackchkCd( backCheckList.getBackchkCd() );
        backCheckListInput.setStartRgstDt( backCheckList.getStartRgstDt() );
        backCheckListInput.setEndRgstDt( backCheckList.getEndRgstDt() );
        backCheckListInput.setIsuYn( backCheckList.getIsuYn() );
        backCheckListInput.setLesnYn( backCheckList.getLesnYn() );
        backCheckListInput.setAtachYn( backCheckList.getAtachYn() );
        backCheckListInput.setUsrId( backCheckList.getUsrId() );
        backCheckListInput.setLang( backCheckList.getLang() );

        return backCheckListInput;
    }
}

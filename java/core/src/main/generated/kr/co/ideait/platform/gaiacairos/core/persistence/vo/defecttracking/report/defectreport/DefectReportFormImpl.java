package kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.report.defectreport;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:18+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class DefectReportFormImpl implements DefectReportForm {

    @Override
    public DefectReportMybatisParam.DefectReportListInput toDefectReportListInput(DefectReportList defectReportList) {
        if ( defectReportList == null ) {
            return null;
        }

        DefectReportMybatisParam.DefectReportListInput defectReportListInput = new DefectReportMybatisParam.DefectReportListInput();

        defectReportListInput.setCntrctNo( defectReportList.getCntrctNo() );
        defectReportListInput.setDfccyCd( defectReportList.getDfccyCd() );
        List<String> list = defectReportList.getDfccyPhaseNoList();
        if ( list != null ) {
            defectReportListInput.setDfccyPhaseNoList( new ArrayList<String>( list ) );
        }
        List<String> list1 = defectReportList.getRgstrIdList();
        if ( list1 != null ) {
            defectReportListInput.setRgstrIdList( new ArrayList<String>( list1 ) );
        }
        defectReportListInput.setKeyword( defectReportList.getKeyword() );
        defectReportListInput.setRgstrNm( defectReportList.getRgstrNm() );
        defectReportListInput.setMyRplyYn( defectReportList.getMyRplyYn() );
        defectReportListInput.setStartDfccyNo( defectReportList.getStartDfccyNo() );
        defectReportListInput.setEndDfccyNo( defectReportList.getEndDfccyNo() );
        defectReportListInput.setActivityNm( defectReportList.getActivityNm() );
        defectReportListInput.setRplyStatus( defectReportList.getRplyStatus() );
        defectReportListInput.setRplyCd( defectReportList.getRplyCd() );
        defectReportListInput.setQaStatus( defectReportList.getQaStatus() );
        defectReportListInput.setQaCd( defectReportList.getQaCd() );
        defectReportListInput.setSpvsStatus( defectReportList.getSpvsStatus() );
        defectReportListInput.setSpvsCd( defectReportList.getSpvsCd() );
        defectReportListInput.setEdCd( defectReportList.getEdCd() );
        defectReportListInput.setStartRgstDt( defectReportList.getStartRgstDt() );
        defectReportListInput.setEndRgstDt( defectReportList.getEndRgstDt() );
        defectReportListInput.setCrtcIsueYn( defectReportList.getCrtcIsueYn() );
        defectReportListInput.setAtachYn( defectReportList.getAtachYn() );
        defectReportListInput.setLang( defectReportList.getLang() );
        if ( defectReportList.getPriorityCheck() != null ) {
            defectReportListInput.setPriorityCheck( defectReportList.getPriorityCheck().charAt( 0 ) );
        }

        return defectReportListInput;
    }
}

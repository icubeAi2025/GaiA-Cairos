package kr.co.ideait.platform.gaiacairos.core.persistence.vo.project;

import javax.annotation.processing.Generated;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnProjectInstall;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:16+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class ProjectFormImpl implements ProjectForm {

    @Override
    public CnProjectInstall toProjectInstall(ProjectInstall projectInstall) {
        if ( projectInstall == null ) {
            return null;
        }

        CnProjectInstall cnProjectInstall = new CnProjectInstall();

        cnProjectInstall.setPlcReqNo( projectInstall.getPlcReqNo() );
        cnProjectInstall.setPlcNm( projectInstall.getPlcNm() );
        cnProjectInstall.setMajorCnsttyCd( projectInstall.getMajorCnsttyCd() );
        cnProjectInstall.setPlcLctAdrsCntnts( projectInstall.getPlcLctAdrsCntnts() );
        cnProjectInstall.setPjtBgnDate( projectInstall.getPjtBgnDate() );
        cnProjectInstall.setPjtEndDate( projectInstall.getPjtEndDate() );
        cnProjectInstall.setCnstwkDaynum( projectInstall.getCnstwkDaynum() );
        cnProjectInstall.setCntrctType( projectInstall.getCntrctType() );
        cnProjectInstall.setAprvlDate( projectInstall.getAprvlDate() );
        cnProjectInstall.setMainFcltyCntnts( projectInstall.getMainFcltyCntnts() );
        cnProjectInstall.setDminsttCd( projectInstall.getDminsttCd() );
        cnProjectInstall.setDminsttNm( projectInstall.getDminsttNm() );
        cnProjectInstall.setRmk( projectInstall.getRmk() );
        cnProjectInstall.setOfclNm( projectInstall.getOfclNm() );
        cnProjectInstall.setEmail( projectInstall.getEmail() );
        cnProjectInstall.setTelNo( projectInstall.getTelNo() );

        return cnProjectInstall;
    }

    @Override
    public void updateProject(ProjectInstall project, CnProjectInstall cnProjectInstall) {
        if ( project == null ) {
            return;
        }

        cnProjectInstall.setPlcNm( project.getPlcNm() );
        cnProjectInstall.setMajorCnsttyCd( project.getMajorCnsttyCd() );
        cnProjectInstall.setPlcLctAdrsCntnts( project.getPlcLctAdrsCntnts() );
        cnProjectInstall.setPjtBgnDate( project.getPjtBgnDate() );
        cnProjectInstall.setPjtEndDate( project.getPjtEndDate() );
        cnProjectInstall.setCnstwkDaynum( project.getCnstwkDaynum() );
        cnProjectInstall.setCntrctType( project.getCntrctType() );
        cnProjectInstall.setAprvlDate( project.getAprvlDate() );
        cnProjectInstall.setMainFcltyCntnts( project.getMainFcltyCntnts() );
        cnProjectInstall.setDminsttCd( project.getDminsttCd() );
        cnProjectInstall.setDminsttNm( project.getDminsttNm() );
        cnProjectInstall.setRmk( project.getRmk() );
        cnProjectInstall.setOfclNm( project.getOfclNm() );
        cnProjectInstall.setEmail( project.getEmail() );
        cnProjectInstall.setTelNo( project.getTelNo() );
    }
}

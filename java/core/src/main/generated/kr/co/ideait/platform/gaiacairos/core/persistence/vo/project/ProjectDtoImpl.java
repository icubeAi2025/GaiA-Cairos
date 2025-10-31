package kr.co.ideait.platform.gaiacairos.core.persistence.vo.project;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnAttachments;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnProjectInstall;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:16+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class ProjectDtoImpl implements ProjectDto {

    @Override
    public ProjectInstall toProjectInstall(CnProjectInstall cnProjectInstall) {
        if ( cnProjectInstall == null ) {
            return null;
        }

        ProjectInstall projectInstall = new ProjectInstall();

        projectInstall.setPlcReqNo( cnProjectInstall.getPlcReqNo() );
        projectInstall.setPltReqType( cnProjectInstall.getPltReqType() );
        projectInstall.setPlcNm( cnProjectInstall.getPlcNm() );
        projectInstall.setMajorCnsttyCd( cnProjectInstall.getMajorCnsttyCd() );
        projectInstall.setPlcLctAdrsCntnts( cnProjectInstall.getPlcLctAdrsCntnts() );
        projectInstall.setPjtBgnDate( cnProjectInstall.getPjtBgnDate() );
        projectInstall.setPjtEndDate( cnProjectInstall.getPjtEndDate() );
        if ( cnProjectInstall.getCnstwkDaynum() != null ) {
            projectInstall.setCnstwkDaynum( cnProjectInstall.getCnstwkDaynum() );
        }
        projectInstall.setCntrctType( cnProjectInstall.getCntrctType() );
        projectInstall.setAprvlDate( cnProjectInstall.getAprvlDate() );
        projectInstall.setMainFcltyCntnts( cnProjectInstall.getMainFcltyCntnts() );
        projectInstall.setDminsttCd( cnProjectInstall.getDminsttCd() );
        projectInstall.setDminsttNm( cnProjectInstall.getDminsttNm() );
        projectInstall.setRmk( cnProjectInstall.getRmk() );
        projectInstall.setOfclId( cnProjectInstall.getOfclId() );
        projectInstall.setOfclNm( cnProjectInstall.getOfclNm() );
        projectInstall.setEmail( cnProjectInstall.getEmail() );
        projectInstall.setTelNo( cnProjectInstall.getTelNo() );
        if ( cnProjectInstall.getAtchFileNo() != null ) {
            projectInstall.setAtchFileNo( cnProjectInstall.getAtchFileNo() );
        }
        projectInstall.setOpenPstats( cnProjectInstall.getOpenPstats() );
        projectInstall.setDltYn( cnProjectInstall.getDltYn() );
        projectInstall.setRgstrId( cnProjectInstall.getRgstrId() );
        projectInstall.setRgstDt( cnProjectInstall.getRgstDt() );
        projectInstall.setChgId( cnProjectInstall.getChgId() );
        projectInstall.setChgDt( cnProjectInstall.getChgDt() );
        projectInstall.setDltId( cnProjectInstall.getDltId() );
        projectInstall.setDltDt( cnProjectInstall.getDltDt() );

        return projectInstall;
    }

    @Override
    public List<ProjectInstall> toProjectInstalls(List<CnProjectInstall> cnProjectInstalls) {
        if ( cnProjectInstalls == null ) {
            return null;
        }

        List<ProjectInstall> list = new ArrayList<ProjectInstall>( cnProjectInstalls.size() );
        for ( CnProjectInstall cnProjectInstall : cnProjectInstalls ) {
            list.add( toProjectInstall( cnProjectInstall ) );
        }

        return list;
    }

    @Override
    public List<CnaAttachMent> toCnAttachments(List<CnAttachments> cnAttachments) {
        if ( cnAttachments == null ) {
            return null;
        }

        List<CnaAttachMent> list = new ArrayList<CnaAttachMent>( cnAttachments.size() );
        for ( CnAttachments cnAttachments1 : cnAttachments ) {
            list.add( cnAttachmentsToCnaAttachMent( cnAttachments1 ) );
        }

        return list;
    }

    protected CnaAttachMent cnAttachmentsToCnaAttachMent(CnAttachments cnAttachments) {
        if ( cnAttachments == null ) {
            return null;
        }

        CnaAttachMent cnaAttachMent = new CnaAttachMent();

        if ( cnAttachments.getFileNo() != null ) {
            cnaAttachMent.setFileNo( cnAttachments.getFileNo() );
        }
        if ( cnAttachments.getSno() != null ) {
            cnaAttachMent.setSno( cnAttachments.getSno() );
        }
        cnaAttachMent.setFileNm( cnAttachments.getFileNm() );
        cnaAttachMent.setFileDiskNm( cnAttachments.getFileDiskNm() );
        cnaAttachMent.setFileDiskPath( cnAttachments.getFileDiskPath() );
        if ( cnAttachments.getFileSize() != null ) {
            cnaAttachMent.setFileSize( cnAttachments.getFileSize() );
        }
        if ( cnAttachments.getFileHitNum() != null ) {
            cnaAttachMent.setFileHitNum( cnAttachments.getFileHitNum() );
        }
        cnaAttachMent.setDltYn( cnAttachments.getDltYn() );

        return cnaAttachMent;
    }
}

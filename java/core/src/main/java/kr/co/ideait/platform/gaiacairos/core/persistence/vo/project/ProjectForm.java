package kr.co.ideait.platform.gaiacairos.core.persistence.vo.project;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnProjectInstall;
import lombok.Data;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = ComponentModel.SPRING)
public interface ProjectForm {

    CnProjectInstall toProjectInstall(ProjectInstall projectInstall); // Dto to Entity

    @Mapping(target = "plcReqNo", ignore = true)
    void updateProject(ProjectInstall project, @MappingTarget CnProjectInstall cnProjectInstall);

    /**
     * 현장 개설
     */
    @Data
    public class ProjectInstall {
        String plcReqNo;
        String plcNm;
        String plcLctAdrsCntnts;
        String pjtBgnDate;
        String pjtEndDate;
        int cnstwkDaynum;
        String majorCnsttyCd;
        String cntrctType;
        String aprvlDate;
        String mainFcltyCntnts;
        String dminsttNm;
        String dminsttCd;
        String ofclNm;
        String email;
        String telNo;
        String rmk;
    }

    /**
     * 첨부 파일
     */
    @Data
    public class CnAttachMent {
        int fileNo;
        int sno;
        String fileNm;
        String fileDiskNm;
        String fileDiskPath;
        String fileSize;
        String fileHitNum;
        String dltYn;
        String rgstrId;
        String rgstrDt;
        String chgId;
        String chgDt;
        String dltId;
        String dltDt;

    }
}

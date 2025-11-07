package kr.co.ideait.platform.gaiacairos.core.persistence.vo.project;

import com.fasterxml.jackson.annotation.JsonFormat;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnAttachments;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnProjectInstall;
import lombok.Data;
import org.apache.ibatis.type.Alias;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = ComponentModel.SPRING)
public interface ProjectDto {

    ProjectInstall toProjectInstall(CnProjectInstall cnProjectInstall);

    List<ProjectInstall> toProjectInstalls(List<CnProjectInstall> cnProjectInstalls);

    List<CnaAttachMent> toCnAttachments(List<CnAttachments> cnAttachments);

    @Data
    @Alias("projectInstall")
    public class ProjectInstall {
        String plcReqNo;
        String pltReqType;
        String plcNm;
        String majorCnsttyCd;
        String plcLctAdrsCntnts;
        String pjtBgnDate;
        String pjtEndDate;
        int cnstwkDaynum;
        String cntrctType;
        String aprvlDate;
        String mainFcltyCntnts;
        String dminsttCd;
        String dminsttNm;
        String rmk;
        String ofclId;
        String ofclNm;
        String email;
        String telNo;
        int atchFileNo;
        String openPstats;
        String dltYn;
        String rgstrId;
        String rgstrNm;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime rgstDt;
        String chgId;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime chgDt;
        String dltId;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime dltDt;
        String pjtNo;
    }

    @Data
    public class CnaAttachMent {
        int fileNo;
        int sno;
        String fileNm;
        String fileDiskNm;
        String fileDiskPath;
        int fileSize;
        int fileHitNum;
        String dltYn;
    }
}

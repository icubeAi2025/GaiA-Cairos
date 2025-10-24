package kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.tool.defecttracking;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DtAttachments;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisPageable;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.util.List;

public interface DefectTrackingMybatisParam {

    @Data
    @Alias("defectTrackingListOutput")
    public class DefectTrackingListOutput {
        String cntrctNo;
        String dfccyNo;
        String rgstrId;
        String rgstrNm;
        String title;
        String activityIds;
        String dfccyCd;
        String dfccyCdNm;
        String rplyYn;
        String rplyCd;
        String rplyCdNm;
        String rplyStatus;
        String rplyRgstrNm;
        String rplyCntnts;
        String qaCd;
        String qaCdNm;
        String qaRgstrNm;
        String qaRgstDt;
        String qaStatus;
        String spvsCd;
        String spvsCdNm;
        String spvsRgstrNm;
        String spvsRgstDt;
        String spvsStatus;
        String edCd;
        String edCdNm;
        String edStatus;
        String edRgstrNm;
        String edRgstDt;
        String crtcIsueYn;
        String dfccyLct;
        String dfccyCntnts;
        String rgstDt;
        String rplyRgstrDt;
        String rplyOkDt;
        String rplyOkNm;
        Integer rplyAtchNo;
        Integer atchFileNo;
        Integer edYCnt;
        Integer edNCnt;
        char priorityCheck;

        // 답변관리 key
        Integer replySeq;

        // 첨부파일
        List<DtAttachments> files;

        // 답변관리 첨부파일
        List<DtAttachments> replyFiles;

        // 확인관리 상세 데이터
        List<DtConfirmOutput> dtConfirm;
    }

    @Data
    @Alias("rgstrListOutput")
    public class RgstrListOutput {
        String rgstrId;
        String rgstrNm;
    }

    @Data
    @Alias("dfccySearchInput")
    public class DfccySearchInput extends MybatisPageable {
        String cntrctNo;
        String dfccyPhaseNo;

        String rgstr;
        String dfccyCd;
        String keyword;

        // 상세 검색 조건
        String rgstrMy;
        String rgstrNm;
        Long startDfccyNo;
        Long endDfccyNo;
        String activityNm;
        String rplyStatus;
        String rplyCd;
        String qaStatus;
        String qaCd;
        String spvsStatus;
        String spvsCd;
        String edCd;
        String edStatus;
        String startRgstDt;
        String endRgstDt;
        String atachYn;
        String crtcIsueYn;
        String myRplyYn;
        String startRplyRecentDt;
        String endRplyRecentDt;
        String priorityCheck;
    }

    @Data
    @Alias("dfccyUpdateOutPut")
    public class DfccyUpdateOutPut {
        String dfccyNo;
        String cntrctNo;
        String title;
        String dfccyCd;
        String dfccyCdNm;
        String crtcIsueYn;
        String dfccyLct;
        String dfccyCntnts;
        Integer atchFileNo;
        char priorityCheck;
    }

    @Data
    @Alias("dtActivity")
    public class DtActivityOutput {
        String wbsCd;
        String activityId;
        String activityNm;
    }

    @Data
    @Alias("dtConfirmOutput")
    public class DtConfirmOutput {
        String cnfrmOpnin;
        String rgstrId;
        String cnfrmRgstrNm;
        Integer atchFileNo;
        String cnfrmDt;

        // 첨부파일
        List<DtAttachments> confirmFiles;
    }
}

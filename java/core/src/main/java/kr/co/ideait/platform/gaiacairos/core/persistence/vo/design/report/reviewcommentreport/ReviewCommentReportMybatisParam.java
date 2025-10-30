package kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.report.reviewcommentreport;

import java.util.List;

import org.apache.ibatis.type.Alias;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DmAttachments;
import lombok.Data;

public interface ReviewCommentReportMybatisParam {

    @Data
    @Alias("reviewReportRgstrListOutput")
    public class ReviewReportRgstrListOutput {
        String usrId;
        String usrNm;
    }

    @Data
    @Alias("reviewReportSearchInput")
    public class ReviewReportSearchInput {
        String cntrctNo;

        // 상세 검색 조건
        String dsgnCd;
        String rgstrNm;
        String myRplyYn;
        Long startDsgnNo;
        Long endDsgnNo;
        String startRecentDt;
        String endRecentDt;
        String isuYn;
        String lesnYn;
        String atachYn;
        String rplyStatus;
        String apprerCd;
        String backchkCd;

        // 일반 검색
        String keyword;
        List<String> dsgnPhaseNoList; // 설계단계
        List<String> rgstrIdList; // 검토자
        List<String> dsgnCdList; // 검토분류
    }

    @Data
    @Alias("reviewReportOutput")
    public class ReviewReportOutput {
        String dsgnNo;
        String rgstrNm;
        String title;
        String dsgnPhaseNm;
        String dsgnNm;
        String rplyChgDt;
        String rplyCdNm;
        String apprerRgstDt;
        String apprerCdNm;
        String backchkRgstDt;
        String backchkCdNm;
        String backchkCd;
        String rgstDt;
    }

    @Data
    @Alias("reviewReportDetailOutput")
    public class ReviewReportDetailOutput {
        String cntrctNo;
        String dsgnNo;
        String title;
        String dsgnNm;
        String rvwOpnin;
        String isuYn;
        String lesnYn;
        String rgstrNm;
        String rgstDt;
        String docNo;
        String dwgNo;
        String dwgNm;

        String apprerRgstDt;
        String apprerStatus;
        String apprerRgstrNm;

        String backchkRgstDt;
        String backchkStatus;
        String backchkRgstrNm;

        String atchFileNo;
        String rvwDwgNo;
        String chgDwgNo;

        // 첨부파일
        List<DmAttachments> files;

        // 검토도서 첨부파일
        DmAttachments rvwDwgFile;
        String rvwDwgDscrpt;

        // 변경요청도서 첨부파일
        DmAttachments chgDwgFile;
        String chgDwgDscrpt;

        // 답변
        String rplyChgDt;
        String rplyCdNm;
        String rplyCntnts;
        String rplyRgstrNm;
        String rplyAtchFileNo;
        String rplyDwgAtchNo;
        Short rplyDwgSno;
        String rplyDwgNo;

        // 답변도서 첨부파일
        DmAttachments rplyDwgFile;
        String rplyDwgDscrpt;

        // 답변관리 첨부파일
        List<DmAttachments> replyFiles;

        // 평가
        List<ApprerOutput> apprerList;

        // 백체크
        List<BackchkOutput> backchkList;

    }

    @Data
    @Alias("apprerOutput")
    public class ApprerOutput {
        // 평가
        String apprerCd;
        String apprerCdNm;
        String evlOpnin;
        String apprerRgstrId;
        String apprerRgstrNm;
        String apprerChgDt;

        String apprerAtchFileNo;
        List<DmAttachments> apprerFiles; // 첨부파일
    }

    @Data
    @Alias("backchkOutput")
    public class BackchkOutput {
        // 백체크
        String backchkCd;
        String backchkCdNm;
        String backchkRgstrId;
        String backchkRgstrNm;
        String backchkChgDt;
        String bckchkOpnin;

        String backchkAtchFileNo;
        List<DmAttachments> backchkFiles; // 첨부파일
    }

}

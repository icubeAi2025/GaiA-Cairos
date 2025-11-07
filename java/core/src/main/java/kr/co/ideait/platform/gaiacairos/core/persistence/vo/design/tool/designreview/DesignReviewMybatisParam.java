package kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.designreview;

import java.util.List;

import org.apache.ibatis.type.Alias;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DmAttachments;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisPageable;
import lombok.Data;

public interface DesignReviewMybatisParam {

    @Data
    @Alias("designReviewListInput")
    public class DesignReviewListInput extends MybatisPageable {
        int page;
        int size;
        Integer dfccyPhaseNo;
        String cntrctNo;
    }

    @Data
    @Alias("designReviewListOutput")
    public class DesignReviewListOutput {
        Integer dsgnSeq;
        String dsgnNo;
        String cntrctNo;
        String dsgnPhaseNo;
        String title;
        String dsgnCd;
        String docNo;
        String dwgNo;
        String dwgNm;
        String rvwOpnin;
        String isuYn;
        String lesnYn;
        String atchFileNo;
        String rvwDwgNo;
        String chgDwgNo;
        String apprerCd;
        String apprerCdNm;
        String apprerRgstrId;
        String apprerRgstrNm;
        String apprerRgstDt;
        String backchkCd;
        String backchkCdNm;
        String backchkRgstrId;
        String backchkRgstrNm;
        String backchkRgstDt;
        String dltYn;

        String dsgnCdNm;
        String rgstrNm;
        String rgstDt;

        // 답변관리 key
        String resSeq;
        String rplyChgDt;
        String rplyRgstDt;
        String rplyStatus;
        String rplyCntnts;
        String rplyRgstrNm;
        String rplyRgstrId;
        String rplyAtchNo;
        String rplyDwgNo;

        // 첨부파일
        List<DmAttachments> files;

        // 검토도서 첨부파일
        DmAttachments rvwDwgFile;
        String rvwDwgDscrpt;

        // 변경요청도서 첨부파일
        DmAttachments chgDwgFile;
        String chgDwgDscrpt;

        // 답변도서 첨부파일
        DmAttachments rplyDwgFile;
        String rplyDwgDscrpt;

        // 답변관리 첨부파일
        List<DmAttachments> replyFiles;
    }

    @Data
    @Alias("dsgnSearchInput")
    public class DsgnSearchInput {
        // 공통 일반 검색
        String rgstr;
        String dsgnCd;
        String keyword;

        // 상세 검색
        String rgstrNm;
        String myRplyYn;
        Long startDsgnNo;
        Long endDsgnNo;
        String rplyCd;
        String apprerCd;
        String backchkCd;
        String startRecentDt;
        String endRecentDt;
        String startRplyRecentDt;
        String endRplyRecentDt;
        String isuYn;
        String lesnYn;
        String atachYn;

        // 답변 일반 검색
        String rplyStatus;

    }

    @Data
    @Alias("dsgnUpdateOutPut")
    public class DsgnUpdateOutPut {
        String dsgnNo;
        String cntrctNo;
        String title;
        String dsgnCd;
        String dsgnCdNm;
        String isuYn;
        String lesnYn;
        String docNo;
        String dwgNo;
        String dwgNm;
        String rvwOpnin;
        String chgDwgNo;
        String rvwDwgNo;
        String atchFileNo;
        String rvwAtchFileNo;
        Integer rvwSno;
        String rvwDwgDscrpt;
        String chgAtchFileNo;
        Integer chgSno;
        String chgDwgDscrpt;
    }

    @Data
    @Alias("dsgnApprerOutput")
    public class DsgnApprerOutput {
        String apprerCd;
        String apprerCdNm;
        String evlOpnin;
        String apprerRgstrId;
        String apprerRgstrNm;
        String apprerRgstDt;

        String apprerAtchFileNo;
        List<DmAttachments> apprerFiles;   // 첨부파일
    }

    @Data
    @Alias("dsgnBackchkOutput")
    public class DsgnBackchkOutput {
        String backchkCd;
        String backchkCdNm;
        String backchkRgstrId;
        String backchkRgstrNm;
        String backchkRgstDt;
        String bckchkOpnin;

        String backchkAtchFileNo;
        List<DmAttachments> backchkFiles;   // 첨부파일
    }
}

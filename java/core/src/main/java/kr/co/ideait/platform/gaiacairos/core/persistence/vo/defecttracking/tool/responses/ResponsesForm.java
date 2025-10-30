package kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.tool.responses;

import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.core.config.GlobalMapperConfig;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DtAttachments;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DtDeficiencyReply;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mapstruct.Mapper;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(config = GlobalMapperConfig.class)
public interface ResponsesForm {

    // 추가
    DtDeficiencyReply toDtDeficiencyReply(ResponsesSave responses);


    // 답변관리 데이터 조회
    @Data
    @EqualsAndHashCode(callSuper = false)
    class ResponsesGet {
        @Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
        String cntrctNo;

        @Description(name = "결함단계번호", description = "", type = Description.TYPE.FIELD)
        String dfccyPhaseNo;

        @Description(name = "답변 시퀀스", description = "", type = Description.TYPE.FIELD)
        Integer replySeq;

        @Description(name = "결함번호", description = "", type = Description.TYPE.FIELD)
        String dfccyNo;
    }

    // 답변관리 추가/수정
    @Data
    @EqualsAndHashCode(callSuper = false)
    class ResponsesSave {
        @Description(name = "프로젝트번호", description = "", type = Description.TYPE.FIELD)
        String pjtNo;

        @Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
        String cntrctNo;

        @Description(name = "답변 시퀀스", description = "", type = Description.TYPE.FIELD)
        Integer replySeq;

        @Description(name = "결함번호", description = "", type = Description.TYPE.FIELD)
        String dfccyNo;

        @Description(name = "답변 코드", description = "", type = Description.TYPE.FIELD)
        String rplyCd;

        @Description(name = "답변 내용", description = "", type = Description.TYPE.FIELD)
        String rplyCntnts;

        @Description(name = "첨부파일 번호", description = "", type = Description.TYPE.FIELD)
        Integer atchFileNo;

        @Description(name = "답변 여부", description = "", type = Description.TYPE.FIELD)
        String rplyYn;

        @Description(name = "답변 작성자ID", description = "", type = Description.TYPE.FIELD)
        String rplyRgstrId;

        @Description(name = "답변 작성일", description = "", type = Description.TYPE.FIELD)
        LocalDateTime rplyRgstrDt;

        @Description(name = "답변 삭제여부", description = "", type = Description.TYPE.FIELD)
        String dltYn;

        @Description(name = "삭제할 첨부파일 리스트", description = "", type = Description.TYPE.FIELD)
        List<DtAttachments> delFileList;
    }

    // 삭제
    @Data
    class ResponsesList {
        @Description(name = "삭제할 답변 리스트", description = "", type = Description.TYPE.FIELD)
        List<DtDeficiencyReply> responsesList; // 수정된 부분
    }

}

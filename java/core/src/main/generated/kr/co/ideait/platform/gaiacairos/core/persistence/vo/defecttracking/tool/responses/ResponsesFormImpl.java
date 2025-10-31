package kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.tool.responses;

import javax.annotation.processing.Generated;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DtDeficiencyReply;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:18+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class ResponsesFormImpl implements ResponsesForm {

    @Override
    public ResponsesMybatisParam.ResponsesInput toResponsesInput(ResponsesGet responsesGet) {
        if ( responsesGet == null ) {
            return null;
        }

        ResponsesMybatisParam.ResponsesInput responsesInput = new ResponsesMybatisParam.ResponsesInput();

        responsesInput.setReplySeq( responsesGet.getReplySeq() );
        responsesInput.setDfccyNo( responsesGet.getDfccyNo() );

        return responsesInput;
    }

    @Override
    public DtDeficiencyReply toDtDeficiencyReply(ResponsesSave responses) {
        if ( responses == null ) {
            return null;
        }

        DtDeficiencyReply dtDeficiencyReply = new DtDeficiencyReply();

        dtDeficiencyReply.setReplySeq( responses.getReplySeq() );
        dtDeficiencyReply.setDfccyNo( responses.getDfccyNo() );
        dtDeficiencyReply.setCntrctNo( responses.getCntrctNo() );
        dtDeficiencyReply.setRplyCd( responses.getRplyCd() );
        dtDeficiencyReply.setRplyCntnts( responses.getRplyCntnts() );
        dtDeficiencyReply.setAtchFileNo( responses.getAtchFileNo() );
        dtDeficiencyReply.setRplyYn( responses.getRplyYn() );
        dtDeficiencyReply.setRplyRgstrId( responses.getRplyRgstrId() );
        dtDeficiencyReply.setRplyRgstrDt( responses.getRplyRgstrDt() );
        dtDeficiencyReply.setDltYn( responses.getDltYn() );

        return dtDeficiencyReply;
    }

    @Override
    public void toUpdateResponse(ResponsesSave responsesSave, DtDeficiencyReply dtDeficiencyReply) {
        if ( responsesSave == null ) {
            return;
        }

        if ( responsesSave.getReplySeq() != null ) {
            dtDeficiencyReply.setReplySeq( responsesSave.getReplySeq() );
        }
        if ( responsesSave.getDfccyNo() != null ) {
            dtDeficiencyReply.setDfccyNo( responsesSave.getDfccyNo() );
        }
        if ( responsesSave.getCntrctNo() != null ) {
            dtDeficiencyReply.setCntrctNo( responsesSave.getCntrctNo() );
        }
        if ( responsesSave.getRplyCd() != null ) {
            dtDeficiencyReply.setRplyCd( responsesSave.getRplyCd() );
        }
        if ( responsesSave.getRplyCntnts() != null ) {
            dtDeficiencyReply.setRplyCntnts( responsesSave.getRplyCntnts() );
        }
        if ( responsesSave.getAtchFileNo() != null ) {
            dtDeficiencyReply.setAtchFileNo( responsesSave.getAtchFileNo() );
        }
        if ( responsesSave.getRplyYn() != null ) {
            dtDeficiencyReply.setRplyYn( responsesSave.getRplyYn() );
        }
        if ( responsesSave.getRplyRgstrId() != null ) {
            dtDeficiencyReply.setRplyRgstrId( responsesSave.getRplyRgstrId() );
        }
        if ( responsesSave.getRplyRgstrDt() != null ) {
            dtDeficiencyReply.setRplyRgstrDt( responsesSave.getRplyRgstrDt() );
        }
        if ( responsesSave.getDltYn() != null ) {
            dtDeficiencyReply.setDltYn( responsesSave.getDltYn() );
        }
    }
}

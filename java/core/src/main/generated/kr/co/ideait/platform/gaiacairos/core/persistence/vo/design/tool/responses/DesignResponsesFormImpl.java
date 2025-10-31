package kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.responses;

import javax.annotation.processing.Generated;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DmDwg;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DmResponse;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:17+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class DesignResponsesFormImpl implements DesignResponsesForm {

    @Override
    public DesignResponsesMybatisParam.DesignResponsesInput toDesignResponsesInput(DesignResponsesGet responsesGet) {
        if ( responsesGet == null ) {
            return null;
        }

        DesignResponsesMybatisParam.DesignResponsesInput designResponsesInput = new DesignResponsesMybatisParam.DesignResponsesInput();

        designResponsesInput.setResSeq( responsesGet.getResSeq() );
        designResponsesInput.setDsgnNo( responsesGet.getDsgnNo() );

        return designResponsesInput;
    }

    @Override
    public DmResponse toDmResponse(DesignResponsesSave responses) {
        if ( responses == null ) {
            return null;
        }

        DmResponse dmResponse = new DmResponse();

        dmResponse.setResSeq( responses.getResSeq() );
        dmResponse.setDsgnNo( responses.getDsgnNo() );
        dmResponse.setCntrctNo( responses.getCntrctNo() );
        dmResponse.setRplyCd( responses.getRplyCd() );
        dmResponse.setRplyCntnts( responses.getRplyCntnts() );
        dmResponse.setAtchFileNo( responses.getAtchFileNo() );
        dmResponse.setDwgNo( responses.getDwgNo() );
        dmResponse.setDltYn( responses.getDltYn() );

        return dmResponse;
    }

    @Override
    public void toUpdateDesignResponse(DesignResponsesSave responsesSave, DmResponse dmResponse) {
        if ( responsesSave == null ) {
            return;
        }

        if ( responsesSave.getResSeq() != null ) {
            dmResponse.setResSeq( responsesSave.getResSeq() );
        }
        if ( responsesSave.getDsgnNo() != null ) {
            dmResponse.setDsgnNo( responsesSave.getDsgnNo() );
        }
        if ( responsesSave.getCntrctNo() != null ) {
            dmResponse.setCntrctNo( responsesSave.getCntrctNo() );
        }
        if ( responsesSave.getRplyCd() != null ) {
            dmResponse.setRplyCd( responsesSave.getRplyCd() );
        }
        if ( responsesSave.getRplyCntnts() != null ) {
            dmResponse.setRplyCntnts( responsesSave.getRplyCntnts() );
        }
        if ( responsesSave.getAtchFileNo() != null ) {
            dmResponse.setAtchFileNo( responsesSave.getAtchFileNo() );
        }
        if ( responsesSave.getDwgNo() != null ) {
            dmResponse.setDwgNo( responsesSave.getDwgNo() );
        }
        if ( responsesSave.getDltYn() != null ) {
            dmResponse.setDltYn( responsesSave.getDltYn() );
        }
    }

    @Override
    public void toUpdateDesignResponseDwg(DesignResponsesDwgSave designResponsesDwgSave, DmDwg dmDwg) {
        if ( designResponsesDwgSave == null ) {
            return;
        }

        if ( designResponsesDwgSave.getDwgNo() != null ) {
            dmDwg.setDwgNo( designResponsesDwgSave.getDwgNo() );
        }
        if ( designResponsesDwgSave.getDwgCd() != null ) {
            dmDwg.setDwgCd( designResponsesDwgSave.getDwgCd() );
        }
        if ( designResponsesDwgSave.getDwgDscrpt() != null ) {
            dmDwg.setDwgDscrpt( designResponsesDwgSave.getDwgDscrpt() );
        }
        if ( designResponsesDwgSave.getAtchFileNo() != null ) {
            dmDwg.setAtchFileNo( designResponsesDwgSave.getAtchFileNo() );
        }
        if ( designResponsesDwgSave.getSno() != null ) {
            dmDwg.setSno( designResponsesDwgSave.getSno() );
        }
        if ( designResponsesDwgSave.getDltYn() != null ) {
            dmDwg.setDltYn( designResponsesDwgSave.getDltYn() );
        }
    }
}

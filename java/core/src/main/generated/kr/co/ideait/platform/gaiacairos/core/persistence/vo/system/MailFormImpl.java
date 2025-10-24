package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system;

import javax.annotation.processing.Generated;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnUseRequest;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:16+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class MailFormImpl implements MailForm {

    @Override
    public CnUseRequest toUseRequest(useRequest uRequest) {
        if ( uRequest == null ) {
            return null;
        }

        CnUseRequest cnUseRequest = new CnUseRequest();

        cnUseRequest.setPjtNo( uRequest.getPjtNo() );
        cnUseRequest.setPjtNm( uRequest.getPjtNm() );
        cnUseRequest.setCntrctNo( uRequest.getCntrctNo() );
        cnUseRequest.setCntrctNm( uRequest.getCntrctNm() );
        cnUseRequest.setLoginId( uRequest.getLoginId() );
        cnUseRequest.setUsrNm( uRequest.getUsrNm() );
        cnUseRequest.setPosition( uRequest.getPosition() );
        cnUseRequest.setContent( uRequest.getContent() );

        return cnUseRequest;
    }
}

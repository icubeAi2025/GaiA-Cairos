package kr.co.ideait.platform.gaiacairos.core.persistence.vo.progress.wbs.wbs;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:17+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class WbsFormImpl implements WbsForm {

    @Override
    public WbsMybatisParam.WbsListInput toWbsListInput(WbsListGet wbsListGet) {
        if ( wbsListGet == null ) {
            return null;
        }

        WbsMybatisParam.WbsListInput wbsListInput = new WbsMybatisParam.WbsListInput();

        wbsListInput.setListType( wbsListGet.getListType() );
        wbsListInput.setSearchText( wbsListGet.getSearchText() );
        wbsListInput.setCntrctChgId( wbsListGet.getCntrctChgId() );
        wbsListInput.setUpWbsCd( wbsListGet.getUpWbsCd() );

        return wbsListInput;
    }
}

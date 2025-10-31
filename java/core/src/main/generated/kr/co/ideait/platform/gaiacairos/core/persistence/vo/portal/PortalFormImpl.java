package kr.co.ideait.platform.gaiacairos.core.persistence.vo.portal;

import javax.annotation.processing.Generated;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnProjectFavorites;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:17+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class PortalFormImpl implements PortalForm {

    @Override
    public CnProjectFavorites toCnProjectFavorites(SetFavoritesParam param) {
        if ( param == null ) {
            return null;
        }

        CnProjectFavorites cnProjectFavorites = new CnProjectFavorites();

        cnProjectFavorites.setPjtNo( param.getPjtNo() );
        cnProjectFavorites.setCntrctNo( param.getCntrctNo() );
        cnProjectFavorites.setLoginId( param.getLoginId() );
        cnProjectFavorites.setPjtType( param.getPjtType() );

        return cnProjectFavorites;
    }
}

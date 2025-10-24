package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.ecomaterial;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:16+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class EcoMaterialFormImpl implements EcoMaterialForm {

    @Override
    public EcoMaterialMybatisParam.EcoMaterialListInput toEcoMaterialListInput(EcoMaterialListGet ecoMaterialListGet) {
        if ( ecoMaterialListGet == null ) {
            return null;
        }

        EcoMaterialMybatisParam.EcoMaterialListInput ecoMaterialListInput = new EcoMaterialMybatisParam.EcoMaterialListInput();

        ecoMaterialListInput.setCntrctNo( ecoMaterialListGet.getCntrctNo() );
        ecoMaterialListInput.setSearchTerm( ecoMaterialListGet.getSearchTerm() );
        ecoMaterialListInput.setSearchText( ecoMaterialListGet.getSearchText() );

        return ecoMaterialListInput;
    }
}

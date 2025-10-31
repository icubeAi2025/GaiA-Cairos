package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.resources;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:17+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class ResourcesFormImpl implements ResourcesForm {

    @Override
    public ResourcesMybatisParam.ResourcesListInput toResourcesListInput(ResourcesListForm resourcesListForm) {
        if ( resourcesListForm == null ) {
            return null;
        }

        ResourcesMybatisParam.ResourcesListInput resourcesListInput = new ResourcesMybatisParam.ResourcesListInput();

        resourcesListInput.setPageable( resourcesListForm.getPageable() );
        resourcesListInput.setSearchType( resourcesListForm.getSearchType() );
        resourcesListInput.setSearchValue( resourcesListForm.getSearchValue() );

        return resourcesListInput;
    }

    @Override
    public ResourcesMybatisParam.ResourcesInsertInput toResourcesInsertInput(ResourcesInsertForm resourcesInsertForm) {
        if ( resourcesInsertForm == null ) {
            return null;
        }

        ResourcesMybatisParam.ResourcesInsertInput resourcesInsertInput = new ResourcesMybatisParam.ResourcesInsertInput();

        resourcesInsertInput.setRescId( resourcesInsertForm.getRescId() );
        resourcesInsertInput.setMenuCd( resourcesInsertForm.getMenuCd() );
        resourcesInsertInput.setRescNm( resourcesInsertForm.getRescNm() );
        resourcesInsertInput.setRghtKind( resourcesInsertForm.getRghtKind() );
        resourcesInsertInput.setRescUrl( resourcesInsertForm.getRescUrl() );
        resourcesInsertInput.setRescDscr( resourcesInsertForm.getRescDscr() );
        resourcesInsertInput.setUseYn( resourcesInsertForm.getUseYn() );

        return resourcesInsertInput;
    }

    @Override
    public ResourcesMybatisParam.ResourcesInfoInput toResourcesInfoInput(ResourcesReadForm resourcesReadForm) {
        if ( resourcesReadForm == null ) {
            return null;
        }

        ResourcesMybatisParam.ResourcesInfoInput resourcesInfoInput = new ResourcesMybatisParam.ResourcesInfoInput();

        resourcesInfoInput.setRescId( resourcesReadForm.getRescId() );

        return resourcesInfoInput;
    }
}

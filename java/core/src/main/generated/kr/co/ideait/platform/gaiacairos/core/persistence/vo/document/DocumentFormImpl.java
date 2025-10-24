package kr.co.ideait.platform.gaiacairos.core.persistence.vo.document;

import javax.annotation.processing.Generated;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DcAuthority;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DcNavigation;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DcProperty;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DcPropertyData;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DcStorageMain;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:18+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class DocumentFormImpl implements DocumentForm {

    @Override
    public DcAuthority toDcAuthority(SetAuthority setAuthority) {
        if ( setAuthority == null ) {
            return null;
        }

        DcAuthority dcAuthority = new DcAuthority();

        dcAuthority.setRghtNo( setAuthority.getRghtNo() );
        dcAuthority.setRghtGrpNo( setAuthority.getRghtGrpNo() );
        dcAuthority.setId( setAuthority.getId() );
        if ( setAuthority.getNo() != null ) {
            dcAuthority.setNo( Integer.parseInt( setAuthority.getNo() ) );
        }
        dcAuthority.setRghtGrpCd( setAuthority.getRghtGrpCd() );
        dcAuthority.setRghtTy( setAuthority.getRghtTy() );

        return dcAuthority;
    }

    @Override
    public DcNavigation toDcNavigation(NaviMove naviationMoveForm) {
        if ( naviationMoveForm == null ) {
            return null;
        }

        DcNavigation dcNavigation = new DcNavigation();

        dcNavigation.setNaviId( naviationMoveForm.getNaviId() );
        dcNavigation.setUpNaviId( naviationMoveForm.getUpNaviId() );
        dcNavigation.setNaviLevel( naviationMoveForm.getNaviLevel() );
        dcNavigation.setDsplyOrdr( naviationMoveForm.getDsplyOrdr() );

        return dcNavigation;
    }

    @Override
    public DcNavigation toDcNavigation(NavigationCreate navigation) {
        if ( navigation == null ) {
            return null;
        }

        DcNavigation dcNavigation = new DcNavigation();

        dcNavigation.setPjtNo( navigation.getPjtNo() );
        dcNavigation.setCntrctNo( navigation.getCntrctNo() );
        dcNavigation.setNaviDiv( navigation.getNaviDiv() );
        dcNavigation.setNaviPath( navigation.getNaviPath() );
        dcNavigation.setNaviNm( navigation.getNaviNm() );
        dcNavigation.setUpNaviNo( navigation.getUpNaviNo() );
        dcNavigation.setUpNaviId( navigation.getUpNaviId() );
        dcNavigation.setNaviLevel( navigation.getNaviLevel() );
        dcNavigation.setNaviType( navigation.getNaviType() );
        dcNavigation.setNaviFolderType( navigation.getNaviFolderType() );

        return dcNavigation;
    }

    @Override
    public DcNavigation toDcNavigation(NavigationUpdate navigation) {
        if ( navigation == null ) {
            return null;
        }

        DcNavigation dcNavigation = new DcNavigation();

        dcNavigation.setNaviNo( navigation.getNaviNo() );
        dcNavigation.setNaviPath( navigation.getNaviPath() );
        dcNavigation.setNaviNm( navigation.getNaviNm() );

        return dcNavigation;
    }

    @Override
    public DcAuthority toDcAuthority(AuthorityCreate authority) {
        if ( authority == null ) {
            return null;
        }

        DcAuthority dcAuthority = new DcAuthority();

        dcAuthority.setRghtGrpNo( authority.getRghtGrpNo() );
        dcAuthority.setId( authority.getId() );
        dcAuthority.setNo( authority.getNo() );
        dcAuthority.setRghtGrpCd( authority.getRghtGrpCd() );
        dcAuthority.setRghtTy( authority.getRghtTy() );

        return dcAuthority;
    }

    @Override
    public DcAuthority toDcAuthority(AuthorityUpdate authority) {
        if ( authority == null ) {
            return null;
        }

        DcAuthority dcAuthority = new DcAuthority();

        dcAuthority.setRghtNo( authority.getRghtNo() );
        dcAuthority.setRghtTy( authority.getRghtTy() );

        return dcAuthority;
    }

    @Override
    public DcProperty toDcProperty(PropertyCreate property) {
        if ( property == null ) {
            return null;
        }

        DcProperty dcProperty = new DcProperty();

        dcProperty.setNaviNo( property.getNaviNo() );
        dcProperty.setNaviId( property.getNaviId() );
        dcProperty.setAttrbtCd( property.getAttrbtCd() );
        dcProperty.setAttrbtCdType( property.getAttrbtCdType() );
        dcProperty.setAttrbtType( property.getAttrbtType() );
        dcProperty.setAttrbtTypeSel( property.getAttrbtTypeSel() );
        dcProperty.setAttrbtNmEng( property.getAttrbtNmEng() );
        dcProperty.setAttrbtNmKrn( property.getAttrbtNmKrn() );
        dcProperty.setAttrbtDsplyOrder( property.getAttrbtDsplyOrder() );
        dcProperty.setAttrbtDsplyYn( property.getAttrbtDsplyYn() );
        dcProperty.setAttrbtChgYn( property.getAttrbtChgYn() );

        return dcProperty;
    }

    @Override
    public DcProperty toDcProperty(PropertyUpdate property) {
        if ( property == null ) {
            return null;
        }

        DcProperty dcProperty = new DcProperty();

        dcProperty.setAttrbtNo( property.getAttrbtNo() );
        dcProperty.setAttrbtCdType( property.getAttrbtCdType() );
        dcProperty.setAttrbtType( property.getAttrbtType() );
        dcProperty.setAttrbtTypeSel( property.getAttrbtTypeSel() );
        dcProperty.setAttrbtNmEng( property.getAttrbtNmEng() );
        dcProperty.setAttrbtNmKrn( property.getAttrbtNmKrn() );
        dcProperty.setAttrbtDsplyOrder( property.getAttrbtDsplyOrder() );
        dcProperty.setAttrbtDsplyYn( property.getAttrbtDsplyYn() );
        dcProperty.setAttrbtChgYn( property.getAttrbtChgYn() );

        return dcProperty;
    }

    @Override
    public DcStorageMain toDcStorageMain(DocCreate document) {
        if ( document == null ) {
            return null;
        }

        DcStorageMain dcStorageMain = new DcStorageMain();

        dcStorageMain.setNaviNo( document.getNaviNo() );
        dcStorageMain.setNaviId( document.getNaviId() );
        dcStorageMain.setUpDocNo( document.getUpDocNo() );
        dcStorageMain.setUpDocId( document.getUpDocId() );
        dcStorageMain.setDocPath( document.getDocPath() );
        dcStorageMain.setDocNm( document.getDocNm() );

        return dcStorageMain;
    }

    @Override
    public DcStorageMain toDcStorageMain(DocFolderCreate documentFolder) {
        if ( documentFolder == null ) {
            return null;
        }

        DcStorageMain dcStorageMain = new DcStorageMain();

        dcStorageMain.setNaviNo( documentFolder.getNaviNo() );
        dcStorageMain.setNaviId( documentFolder.getNaviId() );
        dcStorageMain.setUpDocNo( documentFolder.getUpDocNo() );
        dcStorageMain.setUpDocId( documentFolder.getUpDocId() );
        dcStorageMain.setDocPath( documentFolder.getDocPath() );
        dcStorageMain.setDocNm( documentFolder.getDocNm() );

        return dcStorageMain;
    }

    @Override
    public DcStorageMain toDcStorageMain(DocUpdate document) {
        if ( document == null ) {
            return null;
        }

        DcStorageMain dcStorageMain = new DcStorageMain();

        dcStorageMain.setDocNo( document.getDocNo() );
        dcStorageMain.setDocId( document.getDocId() );
        dcStorageMain.setNaviNo( document.getNaviNo() );
        dcStorageMain.setNaviId( document.getNaviId() );
        dcStorageMain.setUpDocNo( document.getUpDocNo() );
        dcStorageMain.setUpDocId( document.getUpDocId() );
        dcStorageMain.setDocType( document.getDocType() );
        dcStorageMain.setDocPath( document.getDocPath() );
        dcStorageMain.setDocNm( document.getDocNm() );
        dcStorageMain.setDocDiskNm( document.getDocDiskNm() );
        dcStorageMain.setDocDiskPath( document.getDocDiskPath() );
        dcStorageMain.setDocSize( document.getDocSize() );
        dcStorageMain.setDocHitNum( document.getDocHitNum() );
        dcStorageMain.setDocTrashYn( document.getDocTrashYn() );
        dcStorageMain.setDltYn( document.getDltYn() );

        return dcStorageMain;
    }

    @Override
    public DcStorageMain toDcStorageMain(DocCopy document) {
        if ( document == null ) {
            return null;
        }

        DcStorageMain dcStorageMain = new DcStorageMain();

        dcStorageMain.setNaviNo( document.getNaviNo() );
        dcStorageMain.setNaviId( document.getNaviId() );

        return dcStorageMain;
    }

    @Override
    public DcStorageMain toDcStorageMain(DocMoveCopy document) {
        if ( document == null ) {
            return null;
        }

        DcStorageMain dcStorageMain = new DcStorageMain();

        return dcStorageMain;
    }

    @Override
    public DcPropertyData toDcPropertyData(PropertyDataCreate propertyData) {
        if ( propertyData == null ) {
            return null;
        }

        DcPropertyData dcPropertyData = new DcPropertyData();

        dcPropertyData.setDocNo( propertyData.getDocNo() );
        dcPropertyData.setDocId( propertyData.getDocId() );
        dcPropertyData.setAttrbtCd( propertyData.getAttrbtCd() );
        dcPropertyData.setAttrbtCntnts( propertyData.getAttrbtCntnts() );

        return dcPropertyData;
    }

    @Override
    public DcPropertyData toDcPropertyData(PropertyDataUpdate propertyData) {
        if ( propertyData == null ) {
            return null;
        }

        DcPropertyData dcPropertyData = new DcPropertyData();

        dcPropertyData.setDocNo( propertyData.getDocNo() );
        dcPropertyData.setDocId( propertyData.getDocId() );

        return dcPropertyData;
    }

    @Override
    public void updateDcNavigation(NavigationUpdate update, DcNavigation navigation) {
        if ( update == null ) {
            return;
        }

        if ( update.getNaviNo() != null ) {
            navigation.setNaviNo( update.getNaviNo() );
        }
        if ( update.getNaviPath() != null ) {
            navigation.setNaviPath( update.getNaviPath() );
        }
        if ( update.getNaviNm() != null ) {
            navigation.setNaviNm( update.getNaviNm() );
        }
    }

    @Override
    public void updateDcAuthority(AuthorityUpdate update, DcAuthority authority) {
        if ( update == null ) {
            return;
        }

        if ( update.getRghtNo() != null ) {
            authority.setRghtNo( update.getRghtNo() );
        }
        if ( update.getRghtTy() != null ) {
            authority.setRghtTy( update.getRghtTy() );
        }
    }

    @Override
    public void updateDcProperty(PropertyUpdate update, DcProperty property) {
        if ( update == null ) {
            return;
        }

        if ( update.getAttrbtNo() != null ) {
            property.setAttrbtNo( update.getAttrbtNo() );
        }
        if ( update.getAttrbtCdType() != null ) {
            property.setAttrbtCdType( update.getAttrbtCdType() );
        }
        if ( update.getAttrbtType() != null ) {
            property.setAttrbtType( update.getAttrbtType() );
        }
        if ( update.getAttrbtTypeSel() != null ) {
            property.setAttrbtTypeSel( update.getAttrbtTypeSel() );
        }
        if ( update.getAttrbtNmEng() != null ) {
            property.setAttrbtNmEng( update.getAttrbtNmEng() );
        }
        if ( update.getAttrbtNmKrn() != null ) {
            property.setAttrbtNmKrn( update.getAttrbtNmKrn() );
        }
        if ( update.getAttrbtDsplyOrder() != null ) {
            property.setAttrbtDsplyOrder( update.getAttrbtDsplyOrder() );
        }
        if ( update.getAttrbtDsplyYn() != null ) {
            property.setAttrbtDsplyYn( update.getAttrbtDsplyYn() );
        }
        if ( update.getAttrbtChgYn() != null ) {
            property.setAttrbtChgYn( update.getAttrbtChgYn() );
        }
    }

    @Override
    public void updateDcStorageMain(DocUpdate update, DcStorageMain DcStorageMain) {
        if ( update == null ) {
            return;
        }

        if ( update.getDocNo() != null ) {
            DcStorageMain.setDocNo( update.getDocNo() );
        }
        if ( update.getDocId() != null ) {
            DcStorageMain.setDocId( update.getDocId() );
        }
        if ( update.getNaviNo() != null ) {
            DcStorageMain.setNaviNo( update.getNaviNo() );
        }
        if ( update.getNaviId() != null ) {
            DcStorageMain.setNaviId( update.getNaviId() );
        }
        if ( update.getUpDocNo() != null ) {
            DcStorageMain.setUpDocNo( update.getUpDocNo() );
        }
        if ( update.getUpDocId() != null ) {
            DcStorageMain.setUpDocId( update.getUpDocId() );
        }
        if ( update.getDocType() != null ) {
            DcStorageMain.setDocType( update.getDocType() );
        }
        if ( update.getDocPath() != null ) {
            DcStorageMain.setDocPath( update.getDocPath() );
        }
        if ( update.getDocNm() != null ) {
            DcStorageMain.setDocNm( update.getDocNm() );
        }
        if ( update.getDocDiskNm() != null ) {
            DcStorageMain.setDocDiskNm( update.getDocDiskNm() );
        }
        if ( update.getDocDiskPath() != null ) {
            DcStorageMain.setDocDiskPath( update.getDocDiskPath() );
        }
        if ( update.getDocSize() != null ) {
            DcStorageMain.setDocSize( update.getDocSize() );
        }
        if ( update.getDocHitNum() != null ) {
            DcStorageMain.setDocHitNum( update.getDocHitNum() );
        }
        if ( update.getDocTrashYn() != null ) {
            DcStorageMain.setDocTrashYn( update.getDocTrashYn() );
        }
        if ( update.getDltYn() != null ) {
            DcStorageMain.setDltYn( update.getDltYn() );
        }
    }

    @Override
    public void updateDcStorageMain(DocFolderUpdate update, DcStorageMain DcStorageMain) {
        if ( update == null ) {
            return;
        }

        if ( update.getDocNo() != null ) {
            DcStorageMain.setDocNo( update.getDocNo() );
        }
        if ( update.getDocNm() != null ) {
            DcStorageMain.setDocNm( update.getDocNm() );
        }
    }
}

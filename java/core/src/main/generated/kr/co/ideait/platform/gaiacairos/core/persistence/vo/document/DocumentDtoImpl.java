package kr.co.ideait.platform.gaiacairos.core.persistence.vo.document;

import java.util.Map;
import javax.annotation.processing.Generated;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DcAuthority;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DcNavigation;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DcProperty;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DcPropertyData;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DcStorageMain;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:17+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class DocumentDtoImpl implements DocumentDto {

    @Override
    public SimpleNavigation toSimpleNavigation(DcNavigation navigation) {
        if ( navigation == null ) {
            return null;
        }

        SimpleNavigation simpleNavigation = new SimpleNavigation();

        simpleNavigation.setNaviNo( navigation.getNaviNo() );
        simpleNavigation.setNaviId( navigation.getNaviId() );
        simpleNavigation.setPjtNo( navigation.getPjtNo() );
        simpleNavigation.setCntrctNo( navigation.getCntrctNo() );
        simpleNavigation.setNaviDiv( navigation.getNaviDiv() );
        simpleNavigation.setNaviPath( navigation.getNaviPath() );
        simpleNavigation.setNaviNm( navigation.getNaviNm() );
        simpleNavigation.setUpNaviNo( navigation.getUpNaviNo() );
        simpleNavigation.setUpNaviId( navigation.getUpNaviId() );
        simpleNavigation.setNaviLevel( navigation.getNaviLevel() );
        simpleNavigation.setNaviType( navigation.getNaviType() );
        simpleNavigation.setNaviSharYn( navigation.getNaviSharYn() );
        simpleNavigation.setDltYn( navigation.getDltYn() );

        return simpleNavigation;
    }

    @Override
    public SimpleAuthority toSimpleAuthority(DcAuthority authority) {
        if ( authority == null ) {
            return null;
        }

        SimpleAuthority simpleAuthority = new SimpleAuthority();

        simpleAuthority.setRghtNo( authority.getRghtNo() );
        simpleAuthority.setRghtGrpNo( authority.getRghtGrpNo() );
        simpleAuthority.setId( authority.getId() );
        simpleAuthority.setNo( authority.getNo() );
        simpleAuthority.setRghtGrpCd( authority.getRghtGrpCd() );
        simpleAuthority.setRghtTy( authority.getRghtTy() );

        return simpleAuthority;
    }

    @Override
    public SimpleProperty toSimpleProperty(DcProperty property) {
        if ( property == null ) {
            return null;
        }

        SimpleProperty simpleProperty = new SimpleProperty();

        simpleProperty.setAttrbtNo( property.getAttrbtNo() );
        simpleProperty.setNaviNo( property.getNaviNo() );
        simpleProperty.setNaviId( property.getNaviId() );
        simpleProperty.setAttrbtCd( property.getAttrbtCd() );
        simpleProperty.setAttrbtCdType( property.getAttrbtCdType() );
        simpleProperty.setAttrbtType( property.getAttrbtType() );
        simpleProperty.setAttrbtTypeSel( property.getAttrbtTypeSel() );
        simpleProperty.setAttrbtNmEng( property.getAttrbtNmEng() );
        simpleProperty.setAttrbtNmKrn( property.getAttrbtNmKrn() );
        simpleProperty.setAttrbtDsplyOrder( property.getAttrbtDsplyOrder() );
        simpleProperty.setAttrbtDsplyYn( property.getAttrbtDsplyYn() );
        simpleProperty.setAttrbtChgYn( property.getAttrbtChgYn() );
        simpleProperty.setDltYn( property.getDltYn() );

        return simpleProperty;
    }

    @Override
    public SimpleCopyProperty toSimpleCopyProperty(DcProperty property) {
        if ( property == null ) {
            return null;
        }

        SimpleCopyProperty simpleCopyProperty = new SimpleCopyProperty();

        simpleCopyProperty.setNaviId( property.getNaviId() );
        simpleCopyProperty.setAttrbtCd( property.getAttrbtCd() );
        simpleCopyProperty.setAttrbtCdType( property.getAttrbtCdType() );
        simpleCopyProperty.setAttrbtType( property.getAttrbtType() );
        simpleCopyProperty.setAttrbtTypeSel( property.getAttrbtTypeSel() );
        simpleCopyProperty.setAttrbtNmEng( property.getAttrbtNmEng() );
        simpleCopyProperty.setAttrbtNmKrn( property.getAttrbtNmKrn() );
        simpleCopyProperty.setAttrbtDsplyOrder( property.getAttrbtDsplyOrder() );
        simpleCopyProperty.setAttrbtDsplyYn( property.getAttrbtDsplyYn() );
        simpleCopyProperty.setAttrbtChgYn( property.getAttrbtChgYn() );

        return simpleCopyProperty;
    }

    @Override
    public SimpleDocument toSimpleDocument(DcStorageMain documnet) {
        if ( documnet == null ) {
            return null;
        }

        SimpleDocument simpleDocument = new SimpleDocument();

        simpleDocument.setDocNo( documnet.getDocNo() );
        simpleDocument.setDocId( documnet.getDocId() );
        simpleDocument.setNaviNo( documnet.getNaviNo() );
        simpleDocument.setNaviId( documnet.getNaviId() );
        simpleDocument.setUpDocNo( documnet.getUpDocNo() );
        simpleDocument.setUpDocId( documnet.getUpDocId() );
        simpleDocument.setDocType( documnet.getDocType() );
        simpleDocument.setDocPath( documnet.getDocPath() );
        simpleDocument.setDocNm( documnet.getDocNm() );
        simpleDocument.setDocDiskNm( documnet.getDocDiskNm() );
        simpleDocument.setDocDiskPath( documnet.getDocDiskPath() );
        simpleDocument.setDocSize( documnet.getDocSize() );
        simpleDocument.setDocHitNum( documnet.getDocHitNum() );
        simpleDocument.setDocTrashYn( documnet.getDocTrashYn() );
        simpleDocument.setRgstrId( documnet.getRgstrId() );
        simpleDocument.setRgstDt( documnet.getRgstDt() );
        simpleDocument.setDltYn( documnet.getDltYn() );

        return simpleDocument;
    }

    @Override
    public SimplePropertyData toSimplePropertyData(DcPropertyData propertyData) {
        if ( propertyData == null ) {
            return null;
        }

        SimplePropertyData simplePropertyData = new SimplePropertyData();

        simplePropertyData.setAttrbtNo( propertyData.getAttrbtNo() );
        simplePropertyData.setDocNo( propertyData.getDocNo() );
        simplePropertyData.setDocId( propertyData.getDocId() );
        simplePropertyData.setAttrbtCd( propertyData.getAttrbtCd() );
        simplePropertyData.setAttrbtCntnts( propertyData.getAttrbtCntnts() );

        return simplePropertyData;
    }

    @Override
    public NavigationTree toNavigationTree(Map<String, ?> map) {
        if ( map == null ) {
            return null;
        }

        NavigationTree navigationTree = new NavigationTree();

        for ( java.util.Map.Entry<String, ?> entry : map.entrySet() ) {
            String key = entry.getKey();
            Object value = entry.getValue();
            navigationTree.put( key, value );
        }

        return navigationTree;
    }

    @Override
    public DocumentList toDocumentList(Map<String, ?> map) {
        if ( map == null ) {
            return null;
        }

        DocumentList documentList = new DocumentList();

        for ( java.util.Map.Entry<String, ?> entry : map.entrySet() ) {
            String key = entry.getKey();
            Object value = entry.getValue();
            documentList.put( key, value );
        }

        return documentList;
    }

    @Override
    public SimpleAuthorityList toSimpleAuthorityList(Map<String, ?> map) {
        if ( map == null ) {
            return null;
        }

        SimpleAuthorityList simpleAuthorityList = new SimpleAuthorityList();

        for ( java.util.Map.Entry<String, ?> entry : map.entrySet() ) {
            String key = entry.getKey();
            Object value = entry.getValue();
            simpleAuthorityList.put( key, value );
        }

        return simpleAuthorityList;
    }
}

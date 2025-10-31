package kr.co.ideait.platform.gaiacairos.core.persistence.vo.document;

import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.core.config.GlobalMapperConfig;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.*;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MapDto;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.eapproval.draft.DraftMybatisParam;
import lombok.Data;
import org.mapstruct.Mapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper(config = GlobalMapperConfig.class)
public interface DocumentDto {

    SimpleNavigation toSimpleNavigation(DcNavigation navigation);

    SimpleAuthority toSimpleAuthority(DcAuthority authority);

    SimpleProperty toSimpleProperty(DcProperty property);

    SimpleCopyProperty toSimpleCopyProperty(DcProperty property);

    SimpleDocument toSimpleDocument(DcStorageMain documnet);

    SimplePropertyData toSimplePropertyData(DcPropertyData propertyData);

    NavigationTree toNavigationTree(Map<String, ?> map);

    DocumentList toDocumentList(Map<String, ?> map);

    SimpleAuthorityList toSimpleAuthorityList(Map<String, ?> map);

    @Data
    class SimpleNavigation {
        Integer naviNo;
        String naviId;
        String pjtNo;
        String cntrctNo;
        String naviDiv;
        String naviPath;
        String naviNm;
        Integer upNaviNo;
        String upNaviId;
        Short naviLevel;
        String naviType;

        @Description(name = "폴더 종류 구분", description = "0: 기본, 1: 작업일지, 2: 감리일지, ...")
        String naviFolderType;

        @Description(name = "순번", description = "")
        Short dsplyOrdr;

        String naviSharYn;

        String dltYn;
    }

    @Data
    class NavigationTree extends MapDto {
        Integer naviNo;
        String naviId;
        String pjtNo;
        String cntrctNo;
        String naviDiv;
        String naviPath;
        String naviNm;
        Integer upNaviNo;
        String upNaviId;
        Short naviLevel;
        String naviType;
        String naviFolderType;
        String naviSharYn;
        String dltYn;
        String rghtTy;
        Short dsplyOrdr;
        String dminsttNm;
        String dminsttCd;
    }

    @Data
    class FullTree {
        String naviId;
        String upNaviId;
        String naviNm;
        String naviType;
        String naviPath;
        String naviNo;
        String upNaviNo;
        //문서의 폴더 경로일 경우, 해당 폴더가 위치한 navi 정보 필요.
        String dcNaviNo;
        String dcNaviId;
    }

    @Data
    class DocumentList extends MapDto {
        Integer docNo;
        String docId;
        Integer naviNo;
        String naviId;
        Integer upDocNo;
        String upDocId;
        String docType;
        String docPath;
        String docNm;
        // String docDiskNm;
        // String docDiskPath;
        Integer docSize;
        Short docHitNum;
        String docTrashYn;
        String dltYn;
        String rghtTy;
        String rgstrId;
        LocalDateTime rgstDt;
        String chgId;
        LocalDateTime chgDt;
    }

    @Data
    class SimpleAuthorityList extends MapDto {
    	String rghtNo;
        Integer rghtGrpNo;
    	String rghtGrpCd;
        String rghtGrpNmKrn;
        String rghtGrpNmEng;
        String rghtGrpDscrpt;
        String rghtTy;
        String checkyn;
    }

    @Data
    class SimpleAuthority {
        Integer rghtNo;
        Integer rghtGrpNo;
        String id;
        Integer no;
        String rghtGrpCd;
        String rghtTy;
    }

    @Data
    class SimpleProperty {
        Integer attrbtNo;
        Integer naviNo;
        String naviId;
        String attrbtCd;
        String attrbtCdType;
        String attrbtType;
        String attrbtTypeSel;
        String attrbtNmEng;
        String attrbtNmKrn;
        Short attrbtDsplyOrder;
        String attrbtDsplyYn;
        String attrbtChgYn;
        String dltYn;
    }

    @Data
    class SimpleCopyProperty {
        String naviId;
        String attrbtCd;
        String attrbtCdType;
        String attrbtType;
        String attrbtTypeSel;
        String attrbtNmEng;
        String attrbtNmKrn;
        Short attrbtDsplyOrder;
        String attrbtDsplyYn;
        String attrbtChgYn;
    }

    @Data
    class SimpleDocument {
        Integer docNo;
        String docId;
        Integer naviNo;
        String naviId;
        Integer upDocNo;
        String upDocId;
        String docType;
        String docPath;
        String docNm;
        String docDiskNm;
        String docDiskPath;
        Integer docSize;
        Short docHitNum;
        String docTrashYn;
        String rgstrId;
        LocalDateTime rgstDt;
        String dltYn;
    }

    @Data
    class SimplePropertyData {
        Integer attrbtNo;
        Integer docNo;
        String docId;
        String attrbtCd;
        String attrbtCntnts;
    }

    @Data
    class ApprovalRequestData{
        Map<String,Object> apForm;
        List<Map<String,Object>> files;
        Map<String,Object> data;
        String shareType;
        String rgstrId;
    }
}

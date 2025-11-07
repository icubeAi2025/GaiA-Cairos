package kr.co.ideait.platform.gaiacairos.core.persistence.vo.document;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.core.config.GlobalMapperConfig;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.*;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.CommonForm;
import lombok.Data;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Mapper(config = GlobalMapperConfig.class)
public interface DocumentForm {
    
    DcAuthority toDcAuthority(SetAuthority setAuthority);
    
    DcNavigation toDcNavigation(NaviMove naviationMoveForm);

	DcNavigation toDcNavigation(NavigationCreate navigation);

	DcNavigation toDcNavigation(DocCreateEx navigation);

    NavigationCreate toNavigationCreate(DocCreateEx navigation);

    DcNavigation toDcNavigation(NavigationUpdate navigation);

    DcAuthority toDcAuthority(AuthorityCreate authority);

    DcAuthority toDcAuthority(AuthorityUpdate authority);

    DcProperty toDcProperty(PropertyCreate property);

    List<DcProperty> toDcProperties(List<PropertyCreate> properties);

    DcProperty toDcProperty(PropertyUpdate property);

    DcStorageMain toDcStorageMain(DocCreate document);

    DcStorageMain toDcStorageMain(DocCreateEx document);

    DcStorageMain toDcStorageMain(DocFolderCreate documentFolder);

    DcStorageMain toDcStorageMain(DocUpdate document);

    DcStorageMain toDcStorageMain(DocCopy document);

    DcStorageMain toDcStorageMain(DocMoveCopy document);

    DcPropertyData toDcPropertyData(PropertyDataCreate propertyData);

    DcPropertyData toDcPropertyData(PropertyDataUpdate propertyData);

    void updateDcNavigation(NavigationUpdate update, @MappingTarget DcNavigation navigation);

    void updateDcAuthority(AuthorityUpdate update, @MappingTarget DcAuthority authority);

    void updateDcProperty(PropertyUpdate update, @MappingTarget DcProperty property);

    void updateDcStorageMain(DocUpdate update, @MappingTarget DcStorageMain DcStorageMain);

    void updateDcStorageMain(DocFolderUpdate update, @MappingTarget DcStorageMain DcStorageMain);
    
    /**
     * 메뉴 타입별 네비게이션 리스트 조회 Param
     */
    @Data
    class NavigationList {
        @NotBlank
        String cntrctNo;
    	@NotBlank
        String documentType;
    	@NotBlank
        String menuId;

    	String docId;
    	String upDocId;
    	String docNm;
    	String naviId;
    }
    
    /**
     * 문서명 중복검사 Param
     */
    @Data
    class DocExist {
        @NotBlank
        String naviId;
        @NotBlank
        String upDocId;
        @NotBlank
        String docNm;
    }
    
    /**
     * 네비게이션 경로명 중복검사 Param
     */
    @Data
    class NaviExist {
        @NotBlank
        String naviDiv;
        @NotBlank
        String upNaviId;
        @NotBlank
        String naviNm;
    }

    /**
     * 폴더 종류 중복검사 Param
     */
    @Data
    class CheckHasFolderType {
        @NotBlank
        String naviDiv;
        String upNaviId;

        @Description(name = "폴더 종류 구분", description = "0: 기본, 1: 작업일지, 2: 감리일지, ...")
        @NotBlank
        String naviFolderType;
    }

    /**
     * 네비게이션 속성코드 중복검사 Param
     */
    @Data
    class AttrbtCdExist {
        @NotBlank
        String attrbtCd;
        String naviId;
    }

    /**
     * 네비게이션 속성코드 중복검사 Param
     */
    @Data
    class propertyHtmlParam {
        @NotBlank
        String naviId;
        Integer docNo;
        @Pattern(regexp = "update", message = "{msg.doc.118}") // 옳지 않은 타입 값입니다.
        String actionTy;
    }
    
    /**
     * 네비게이션 경로명 추가 Param
     */
    @Data
    class NavigationCreate {
        @Description(name = "네비게이션 No", description = "")
        Integer naviNo;

        @Description(name = "네비게이션 ID", description = "")
        String naviId;

    	@NotBlank
        String pjtNo;
    	@NotBlank
        String cntrctNo;
    	@NotBlank
        String naviDiv;
        @NotBlank
        String naviPath;
        @NotBlank
        @Size(max = 255, message = "{msg.doc.116}") // 네비게이션 경로명은 255자 이내로 작성해야 합니다.
        String naviNm;
        @NotNull
        Integer upNaviNo;
        @NotBlank
        String upNaviId;
        @NotNull
        Short naviLevel;
        @NotBlank
        @Pattern(regexp = "FOLDR|ITEM", message = "{msg.doc.117}") // 네비게이션 타입은 'FOLDR' 또는 'ITEM'만 허용됩니다.
        String naviType;

        @Description(name = "폴더 종류 구분", description = "0: 기본, 1: 작업일지, 2: 감리일지, ...")
        String naviFolderType;

        @Description(name = "등록자ID", description = "")
        String rgstrId;

        @Description(name = "수정자ID", description = "")
        String chgId;

        @Description(name = "역할구분", description = "")
        String svrType;

        List<PropertyCreate> properties;
    }
    
    /**
     * 네비게이션 경로명 수정 Param
     */
    @Data
    class NavigationUpdate {
        @NotNull
        Integer naviNo;
        @NotBlank
        @Size(max = 255, message = "{msg.doc.116}") // 네비게이션 경로명은 255자 이내로 작성해야 합니다.
        String naviNm;
        String naviPath;
    }

    /**
     * 네비게이션 경로 이동 Param
     */
    @Data
    class NaviMove {
        @NotBlank
        String naviId;
        @NotBlank
        String upNaviId;
        Short naviLevel;
        Short dsplyOrdr;
    }

    /**
     * 문서 추가 Param
     */
    @Data
    class DocCreate {
        @NotNull
        Integer naviNo;
        @NotBlank
        String naviId;
        @Size(max = 255)
        String docNm;

        Integer upDocNo;
        String upDocId;
        String docPath;
        MultipartFile file;

        String cntrctNo;
    }

    /**
     * 문서 추가 Param - expansion
     */
    @Data
    class DocCreateEx {
        @Description(name = "네비게이션 No", description = "")
        Integer naviNo;

        @Description(name = "네비게이션 ID", description = "")
        String naviId;

        @NotBlank
        String pjtNo;

        @NotBlank
        String cntrctNo;

        @NotBlank
        String naviDiv;

        @NotBlank
        String naviPath;

        @NotBlank
        @Size(max = 255, message = "{msg.doc.116}") // 네비게이션 경로명은 255자 이내로 작성해야 합니다.
        String naviNm;

        Integer upNaviNo;

        String upNaviId;

        @NotNull
        Short naviLevel;

        @NotBlank
        @Pattern(regexp = "FOLDR|ITEM", message = "{msg.doc.117}") // 네비게이션 타입은 'FOLDR' 또는 'ITEM'만 허용됩니다.
        String naviType;

        @Description(name = "폴더 종류 구분", description = "0: 기본, 1: 작업일지, 2: 감리일지, ...")
        String naviFolderType;

        String docId;
        @Size(max = 255)
        String docNm;

        Integer upDocNo;

        String upDocId;

        String docPath;

        @Description(name = "폴더 종류 구분자", description = "'': 기본, drp: 작업일지, irp: 감리일지, wrp: 주간공정보고, mrp: 월간공정보고")
        String naviFolderKind;

        @Description(name = "등록자ID", description = "")
        String rgstrId;

        @Description(name = "수정자ID", description = "")
        String chgId;

        List<PropertyCreate> properties;
        List<PropertyData> propertyData;
    }

    @Data
    class PropertyData {
        Integer docNo;

        String docId;

        String attrbtCd;

        String attrbtCntnts;

        @Description(name = "등록자ID", description = "")
        String rgstrId;

        @Description(name = "수정자ID", description = "")
        String chgId;
    }

    /**
     * 네비게이션 권한설정 조회 Param
     */
    @Data
    class AuthorityList {
        String pjtNo;
        String cntrctNo;
        Integer naviNo;
        String naviId;
        Integer upNaviNo;
        String upNaviId;
        String naviLevel;

        Integer upDocNo;
        String upDocId;
        Integer docNo;
        String docId;
    }
    
    /**
     * 네비게이션 권한설정(입력, 수정, 삭제) 단건 Param
     */
    @Data
    class SetAuthority {
    	@NotBlank
        @Pattern(regexp = "ADD|UPDATE|DEL", message = "{msg.doc.119}") // 타입은 'ADD' 또는 'UPDATE' 또는 'DEL'만 허용됩니다.
        String actionType;
        Integer rghtNo;
        @NotBlank
        Integer rghtGrpNo;
        @NotBlank
        String id;
        @NotBlank
        String no;
        @NotBlank
        String rghtGrpCd;
        @NotBlank
        String rghtTy;
    }
    
    /**
     * 네비게이션 권한설정(입력, 수정, 삭제) List Param
     */
    @Data
    class SetAuthorityList {
        List<SetAuthority> setAuthorityList;

        String allYn; // 설정한 권한을 하위 문서에 일괄 적용할지 여부
    }
    
    /**
     * 권한설정 권한 사용자 List Param
     */
    @Data
    class SelectNaviAuthorityGroupUserList {
    	@NotBlank
        String rghtGrpNo;
        @NotBlank
        String rghtGrpCd;
    }

    /**
     * 네비게이션 속성 추가 Param
     */
    @Data
    class PropertyCreate {
        // Integer attrbtNo;
        @NotNull
        Integer naviNo;
        @NotBlank
        String naviId;
        String attrbtCd;
        @NotBlank
        String attrbtCdType;
        @NotBlank
        String attrbtType;
        String attrbtTypeSel;
        String attrbtNmEng;
        @NotBlank
        String attrbtNmKrn;
        @NotNull
        Short attrbtDsplyOrder;
        @NotBlank
        String attrbtDsplyYn;
        @NotBlank
        String attrbtChgYn;
        // String dltYn;
    }

    /**
     * 속성 데이터 Param
     */
    @Data
    class AttrbtData {
        @NotNull
        Integer attrbtNo;
        @NotBlank
        String attrbtCd;
        @NotNull
        String attrbtCntnts;
    }

    /**
     * 속성 데이터리스트 추가 Param
     */
    @Data
    class ItemAttbtDataCreate {
        @Valid
        List<AttrbtData> attrbtDataList;
    }

    /**
     * 권한, 속성 복사 Param
     */
    @Data
    class CopyParam {
        @NotNull
        Integer targetNo;
        @NotBlank
        String targetId;
    }

    /**
     * 속성 붙이기 Param
     */
    @Data
    class PropertyPaste {
        @NotNull
        Integer targetNo;
        @NotBlank
        String targetId;
        @NotNull
        Integer sourceNo;
        @NotBlank
        String sourceId;
    }

    /**
     * 권한 붙이기 Param
     */
    @Data
    class AuthorityPaste {
        @NotNull
        Integer targetNo;
        @NotBlank
        String targetId;
        @NotNull
        Integer sourceNo;
        @NotBlank
        String sourceId;
        
    }

    /**
     * 문서 리스트 조회 Param
     */
    @Data
    class DocListGet extends CommonForm{
        @NotBlank
        String naviId;
        String upDocId;

        String searchText;
        String columnNm;

        // 통합 상세 검색 파라미터
        String cntrctNo;
        String naviFolderType;
        String naviDiv;
    }

    /**
     * 속성 정의 수정 Param
     */
    @Data
    class PropertyDataUpdate {
        Integer docNo;
        String docId;
        String naviId;
        String documentType; // 문서 구분
        @Valid
        List<AttrbtData> attrbtDataList;

        String cntrctNo;
    }

    /**
     * 문서 권한설정 조회 Param
     */
    @Data
    class DocAuthorityList {
        Integer naviNo;
        String naviId;
        String pjtNo;
        String cntrctNo;

        Integer upDocNo;
        String upDocId;
        Integer docNo;
        String docId;
    }

    /**
     * 문서 다운로드 Param
     */
    @Data
    class DocDownload {
        @NotNull
        List<String> docIdList;
    }

    /**
     * 아이템형 문서 다운로드 Param
     */
    @Data
    class ItemDownload {
        @NotBlank
        String itemDocId;
    }

    /**
     * 네비, 문서 풀 경로 Param
     */
    @Data
    class FullTreeList {
        @NotBlank
        String topNaviId;

        List<String> docFolderIdList;
    }

    /**
     * 문서 이동, 복사 Param
     */
    @Data
    class DocMoveCopy {
        String sourceItemId;
        Integer sourceItemNo;
        String sourceItemKind;
        String sourceItemPath;

        List<String> targetDocIdList;
    }

    /**
     * 휴지통 문서 Param
     */
    @Data
    class TrashDocParam {
        @NotNull
        List<String> trashDocIdList;
    }

    /**
     * 휴지통 문서 리스트 조회 Param
     */
    @Data
    class TrashDocGet {
        @NotBlank(message = "{msg.doc.120}") // 계약 정보가 없습니다.
        String cntrctNo;
        @NotBlank(message = "{msg.doc.127}") // 문서 타입 정보가 없습니다.
        String documentType;

        String columnNm;
        String searchText;
    }

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    



    @Data
    class AuthorityCreate {
        // Integer rghtNo;
        Integer rghtGrpNo;
        String id;
        Integer no;
        String rghtGrpCd;
        String rghtTy;
        // String dltYn;
    }

    @Data
    class AuthorityCreateList {
        List<AuthorityCreate> authorityCreateList;
    }

    @Data
    class AuthorityUpdate {
        Integer rghtNo;
        // Integer rghtGrpNo;
        // String id;
        // Integer no;
        // String rghtGrpCd;
        String rghtTy;
        // String dltYn;
    }

    // @Data
    // class AuthorityCopy {
    //     String sourceId;
    //     Integer targetNo;
    //     String targetId;
    // }

    @Data
    class AuthorityDelete {
        List<Integer> rghtNoList;
    }


    @Data
    class PropertyUpdate {
        Integer attrbtNo;
        // Integer naviNo;
        // String naviId;
        // String attrbtCd;
        String attrbtCdType;
        String attrbtType;
        String attrbtTypeSel;
        String attrbtNmEng;
        String attrbtNmKrn;
        Short attrbtDsplyOrder;
        String attrbtDsplyYn;
        String attrbtChgYn;
        // String dltYn;
    }

    @Data
    class PropertyDelete {
        List<Integer> attrbtNoList;
    }


    @Data
    class DocFolderCreate {
        Integer naviNo;
        String naviId;
        Integer upDocNo;
        String upDocId;
        String docPath;
        String docNm;
    }

    @Data
    class DocFolderUpdate {
        Integer docNo;
        String docNm;
    }

    @Data
    class DocUpdate {
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
        String dltYn;
    }

    @Data
    class DocCopy {
        // Integer fromDocNo;
        // // Integer docNo;
        // String docId;
        // //여기서의 navi 값은 (doc or navi)
        // Integer naviNo;
        // String naviId;
        // Integer upDocNo;
        // String upDocId;
        // String docType;
        // String docPath;
        // String docNm;
        // String docDiskNm;
        // String docDiskPath;
        // Integer docSize;
        // Short docHitNum;
        // String docTrashYn;
        // String dltYn;

        Integer naviNo;
        String naviId;
        String naviPath;
        String upNaviId;
        Integer upNaviNo;
        //해당 폴더(문서)가 위치한 navi 정보
        Integer dcNaviNo;
        String dcNaviId;
        List<Integer> docNoList;

    }

    @Data
    class DocDeleteList {
        @NotNull
        List<String> docIdList;
    }

    @Data
    class DocDelete {
        @NotNull
        String docId;
    }

    @Data
    class PropertyDataCreate {
        // Integer attrbtNo;
        Integer docNo;
        String docId;
        String attrbtCd;
        String attrbtCntnts;
    }


    @Data
    class DocListOptions extends CommonForm{
        String naviType;
        String docId;
        String upDocId = "#";
    }

}

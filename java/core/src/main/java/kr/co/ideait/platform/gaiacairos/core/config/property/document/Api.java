package kr.co.ideait.platform.gaiacairos.core.config.property.document;

import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;

@Data
public class Api {
    //문서 업로드
    @Description(name = "문서 업로드", description = "문서 업로드 api 프로퍼티")
    String createFile;


    //########################################[네비게이션 관련]########################################
    //문서 네비게이션 생성(다중)
    @Description(name = "문서 네비게이션 생성(다중)", description = "문서 네비게이션 생성(다중) api 프로퍼티")
    String createNavigationList;

    //메인 화면의 트리 전시를 위한 네비게이션 목록 등 데이터
    String selectDocumentMainData;

    String checkHasNavigationType;


    //폴더유형 네비 최신 문서 정보 조회
    @Description(name = "폴더유형 네비 최신 문서 정보 조회", description = "폴더유형 네비 최신 문서 정보 조회 api 프로퍼티")
    String lastestDcStorageMainByFolderType;

    //########################################[공유이력 관련]########################################
    //문서 공유 이력 정보 삽입
    @Description(name = "문서 공유 이력 정보 삽입", description = "문서 공유 이력 정보 삽입 api 프로퍼티")
    String insertDocSharedHistory;
    //문서 공유 이력 정보 수정
    @Description(name = "문서 공유 이력 정보 수정", description = "문서 공유 이력 정보 수정 api 프로퍼티")
    String updateDocSharedHistory;



    //########################################[폴더 관련]########################################
    String checkFolderExist;

    //########################################[문서 관련]########################################
    @Description(name = "문서 삭제", description = "문서 삭제 api 프로퍼티")
    String removeDocument;

    @Description(name = "문서 삭제 취소", description = "문서 삭제 취소 api 프로퍼티")
    String rollbackRemovedDocument;

    //########################################[결재 문서 관련]########################################
    @Description(name = "결재 문서 생성", description = "결재 승인된 결재 문서 통합문서관리에 생성")
    String createApprovalDocument;
}

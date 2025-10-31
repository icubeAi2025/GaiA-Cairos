package kr.co.ideait.platform.gaiacairos.comp.defecttracking;

import kr.co.ideait.platform.gaiacairos.comp.defecttracking.helper.DefectTrackingHelper;
import kr.co.ideait.platform.gaiacairos.comp.defecttracking.service.DefectTrackingService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractComponent;
import kr.co.ideait.platform.gaiacairos.core.constant.CommonCodeConstants;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DtAttachments;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DtDeficiency;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DtDeficiencyActivity;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.tool.defecttracking.DefectTrackingMybatisParam.*;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.type.FileUploadType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class DefectTrackingComponent extends AbstractComponent {

    @Autowired
    DefectTrackingService defectTrackingService;

    @Autowired
    DefectTrackingHelper defectTrackingHelper;


    /**
     * 결함단계 리스트 조회
     * @param cntrctNo
     * @param dfccyPhaseCd
     * @return
     */
    public List<Map<String, ?>> getDfccyPhaseList(String cntrctNo, String dfccyPhaseCd) {
        MybatisInput input = MybatisInput.of().add("cntrctNo", cntrctNo)
                .add("dfccyPhaseCd", dfccyPhaseCd);

        return defectTrackingService.getDfccyPhaseList(input);
    }


    /**
     * 결함추적관리 - 결함 목록 조회
     * @param dfccySearchInput
     * @param langInfo
     * @return
     */
    public Page<DefectTrackingListOutput> getDfccyListToGrid(DfccySearchInput dfccySearchInput, String langInfo) {
        MybatisInput input = createMybatisInput(dfccySearchInput, langInfo);
        List<DefectTrackingListOutput> output = defectTrackingService.getDfccyList(input);
        Long totalCount = defectTrackingService.getDfccyListCount(input);
        return new PageImpl<>(output, input.getPageable(), totalCount);
    }


    /**
     * 결함추적관리 - 결함 상세 조회
     * @param dfccySearchInput
     * @param langInfo
     * @return
     */
    public List<DefectTrackingListOutput> getDfccyList(DfccySearchInput dfccySearchInput, String langInfo) {
        MybatisInput input = createMybatisInput(dfccySearchInput, langInfo);
        return defectTrackingService.getDfccyList(input);
    }


    /**
     * 결함 목록/상세 조회를 위한 MybatisInput 생성
     * @param dfccySearchInput
     * @param langInfo
     * @return
     */
    private MybatisInput createMybatisInput(DfccySearchInput dfccySearchInput, String langInfo) {
        if ("my".equals(dfccySearchInput.getRgstr())) {
            dfccySearchInput.setRgstr(UserAuth.get(true).getUsrId());
        }

        String confirm = "완료";
        String ing = "진행중";
        String end = "종료";

        MybatisInput input = MybatisInput.of()
                .add("cntrctNo", dfccySearchInput.getCntrctNo())
                .add("dfccyPhaseNo", dfccySearchInput.getDfccyPhaseNo())
                .add("lang", langInfo)
                .add("pageable", dfccySearchInput.getPageable())
                .add("searchInput", dfccySearchInput)
                .add("usrId", UserAuth.get(true).getUsrId())
                .add("confirm", confirm)
                .add("ing", ing)
                .add("end", end)
                .add("rplyStatus", CommonCodeConstants.REPLY_CODE_GROUP_CODE)
                .add("qaStatus", CommonCodeConstants.QA_CODE_GROUP_CODE)
                .add("spvsStatus", CommonCodeConstants.SPVS_CODE_GROUP_CODE)
                .add("edCd", CommonCodeConstants.ED_CODE_GROUP_CODE)
                .add("dfccyCd", CommonCodeConstants.WORKTYPE_CODE_GROUP_CODE);

        input.setPageable(dfccySearchInput.getPageable());
        return input;
    }


    /**
     * 결함 추적관리 - 작성자 목록 조회
     * @param cntrctNo
     * @param dfccyPhaseNo
     * @return
     */
    public List<RgstrListOutput> getRgstrList(String cntrctNo, String dfccyPhaseNo) {
        MybatisInput input = MybatisInput.of().add("cntrctNo", cntrctNo)
                .add("dfccyPhaseNo", dfccyPhaseNo);
        return defectTrackingService.getRgstrList(input);
    }


    /**
     * Activity 선택 - 목록
     * @param params
     * @return
     */
    public List<Map<String, ?>> getActivityList(Map<String, Object> params) {
        return defectTrackingService.getActivityList(params);
    }


    /**
     * Activity 선택 - 검색
     * @param params
     * @return
     */
    public List<Map<String, ?>> getActivityListSearch(Map<String, Object> params) {
        return defectTrackingService.getActivityListSearch(params);
    }


    /**
     * 결함 추적 관리 > 결함 조회
     * @param cntrctNo
     * @param dfccyPhaseNo
     * @param dfccyNo
     * @param langInfo
     * @return
     */
    public Map<String, Object> getDfccy(String cntrctNo, String dfccyPhaseNo, String dfccyNo, String langInfo) {
        DfccyUpdateOutPut dfccy = defectTrackingService.getDeficiency(cntrctNo, dfccyPhaseNo, dfccyNo, langInfo);   // 결함 정보 데이터
        List<DtActivityOutput> activitys = defectTrackingService.getDeficiencyActivityList(cntrctNo, dfccyNo);      // 결함 액티비티
        List<DtAttachments> attachments = defectTrackingHelper.getFileList(dfccy.getAtchFileNo());

        Map<String, Object> returnMap = new HashMap<String, Object>();
        returnMap.put("dfccy", dfccy);
        returnMap.put("activitys", activitys);

        if (attachments != null && !attachments.isEmpty()) {
            returnMap.put("attachments", attachments);
        }

        return returnMap;
    }

    public DtDeficiency getDfccy(String cntrctNo, String dfccyNo) {
        return defectTrackingService.getDeficiency(cntrctNo, dfccyNo);
    }


    /**
     * 결함 추적 관리 > 결함 추가
     * @param deficiency
     * @param activityList
     * @param files
     */
    @Transactional
    public void createDeficiency(DtDeficiency deficiency, List<DtDeficiencyActivity> activityList, List<MultipartFile> files) {
         // 1. 결함번호 생성
        String dfccyNo = defectTrackingService.createDfccyNo();

        // 2. 파일저장
        if (files != null && !files.isEmpty()) {
            String uploadPath = getUploadPathByWorkType(FileUploadType.DEFICIENCY, deficiency.getCntrctNo());
            Integer fileNo = defectTrackingHelper.convertToDtAttachments(files, uploadPath, null, UserAuth.get(true).getUsrId());
            deficiency.setAtchFileNo(fileNo);
        }

        // 3. 결함 저장
        deficiency.setDltYn("N");
        deficiency.setDfccyNo(dfccyNo);

        defectTrackingService.saveDeficiency(deficiency);

        // 4. Activity 저장
        if(activityList != null && !activityList.isEmpty()){
            defectTrackingService.createActicity(activityList, dfccyNo, deficiency.getCntrctNo(), UserAuth.get(true).getUsrId());
        }

    }


    /**
     * 결함 추적 관리 > 결함 수정
     * @param deficiency
     * @param activityList
     * @param newFiles
     * @param delFileList
     */
    @Transactional
    public void updateDeficiency(DtDeficiency deficiency,
                                List<DtDeficiencyActivity> activityList,
                                List<MultipartFile> newFiles,
                                List<DtAttachments> delFileList) {

        // 1. Activity 데이터 수정(삭제 후 새로운 데이터 저장)
        List<DtDeficiencyActivity> oldActivities = defectTrackingService.getDeficiencyActivity(deficiency.getDfccyNo());

        if(!oldActivities.isEmpty()){
            defectTrackingService.deleteActivity(oldActivities);
        }
        if(activityList != null && !activityList.isEmpty()){
            defectTrackingService.createActicity(activityList, deficiency.getDfccyNo(), deficiency.getCntrctNo(), UserAuth.get(true).getUsrId());
        }

        // 2. 파일 삭제 처리
        if (delFileList != null && !delFileList.isEmpty()) {
            defectTrackingHelper.deleteAttachmentList(delFileList, UserAuth.get(true).getUsrId());
        }

        // 3. 새 파일 추가 처리
        Integer existingFileNo = deficiency.getAtchFileNo();
        if (newFiles != null && !newFiles.isEmpty()) {
            String uploadPath = getUploadPathByWorkType(FileUploadType.DEFICIENCY, deficiency.getCntrctNo());
            Integer fileNo = defectTrackingHelper.convertToDtAttachments(newFiles, uploadPath, existingFileNo, UserAuth.get(true).getUsrId());
            deficiency.setAtchFileNo(fileNo);
        }
        
         // 4. 결함 저장
        defectTrackingService.saveDeficiency(deficiency);
    }


    /**
     * 결함 삭제
     * @param dfccyNoList
     */
    @Transactional
    public void deleteDeficiencyList(List<String> dfccyNoList) {
        dfccyNoList.forEach(dfccy -> {
            DtDeficiency deficiency = defectTrackingService.getDeficiency(dfccy);
            if(deficiency != null){
                if(deficiency.getAtchFileNo() != null){
                    MybatisInput input = MybatisInput.of()
                                .add("fileNo", deficiency.getAtchFileNo())
                                .add("usrId", UserAuth.get(true).getUsrId());
                    defectTrackingHelper.deleteAttachmentList(input);
                }
                defectTrackingService.deleteDeficiency(deficiency);
            }
        });
    }


    /**
     * 결함 확인 리스트 조회
     * @param dfccyNo
     * @return
     */
    public List<DtConfirmOutput> getDeficiencyConfirmList(String dfccyNo) {
        return defectTrackingService.getDeficiencyConfirmList(dfccyNo);
    }


    /**
     * 결함추적 메뉴 첨부파일 다운로드(공통)
     * @param fileNo
     * @param sno
     * @return
     */
    public ResponseEntity<Resource> fileDownload(Integer fileNo, Short sno) {
        return defectTrackingHelper.fileDownload(fileNo, sno);
    }
}

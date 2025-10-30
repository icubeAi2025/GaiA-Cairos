package kr.co.ideait.platform.gaiacairos.core.persistence.vo.safety;

import java.util.List;
import java.util.Map;

import kr.co.ideait.platform.gaiacairos.core.util.FileService;
import org.mapstruct.Mapper;

import kr.co.ideait.platform.gaiacairos.core.config.GlobalMapperConfig;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.CommonForm;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Mapper(config = GlobalMapperConfig.class)
public interface EducationDiaryForm {
	
	EducationDiaryMybatisParam.EducationDiaryListInput toEducationDiaryListInput(EduDiaryListForm eduDiaryListForm);
	
	/**
     * 교육일지 목록조회 파라미터
     */
    @Data
    @EqualsAndHashCode(callSuper=false)
    class EduDiaryListForm extends CommonForm {
        String cntrctNo;
        String searchYear;
        String searchMonth;
        String searchEduType;
    }
    
    /**
     * 교육일지 상세조회 파라미터
     */
    @Data
    class EduDiaryForm {
        String eduId;
        String openType;
    }
	
	/**
     * 교육일자의 교육일지 존재여부 확인 파라미터
     */
    @Data
    class EduDateExistForm {
        String eduDiaryDate;
        String cntrctNo;
    }
    
    /**
     * 교육일지 저장 파라미터
     */
    @Data
    class EduDiaryCrateForm {
        String eduId;
        String cntrctNo;
        String eduDt;
        String eduType;
        String eduRank;
        String eduNm;
        String eduSurvM;
        String eduSurvF;
        String eduSurvNote;
        String eduActiM;
        String eduActiF;
        String eduActiNote;
        String eduNoActiM;
        String eduNoActiF;
        String eduNoActiNote;
        String outline;
        String subject;
        String method;
        String time;
        String textbook;
        String location;
        String note;
        String personnelIsEmpty;
        Integer atchFileNo;
        List<EduDiaryPersonnelCrateForm> eduDiaryPersonnelList;
        
        List<FileService.FileMeta> fileList;
    }
    
    /**
     * 교육일지의 교육참석자 저장 파라미터
     */
    @Data
    class EduDiaryPersonnelCrateForm {
        String actionType;
        String eduVicSeq;
        String eduVicOccu;
        String eduVicNm;
    }
    
    /**
     * 교육일지 아이디 List
     */
    @Data
    class eduIdListForm {
        List<Map<String, Object>> eduIdList;
    }
}

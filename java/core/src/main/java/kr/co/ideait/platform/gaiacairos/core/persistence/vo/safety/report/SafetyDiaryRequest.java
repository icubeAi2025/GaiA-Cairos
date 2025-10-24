package kr.co.ideait.platform.gaiacairos.core.persistence.vo.safety.report;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwAttachments;
import kr.co.ideait.platform.gaiacairos.core.util.FileService;
import lombok.Data;

import java.util.List;

/**
 * 안전관리 > 안전 일지 요청 사용
 */
@Data
public class SafetyDiaryRequest {
    private SafetyDiary safetyDiary;                // 본문
    private List<WorkDTO> workList;                 // 작업정보
    private List<SafetyDTO> safetyList;             // 안전현황
    private List<WorkDTO> deletedWorkList;          // 작업정보
    private List<SafetyDTO> deletedSafetyList;      // 안전현황
    private List<FileService.FileMeta> fileList;           // 파일현황
}

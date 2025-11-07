package kr.co.ideait.platform.gaiacairos.core.persistence.vo.safety.report;

import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 재해일지_인원
 */
@Data
public class DisasterDiaryPersonnel {
    @Description(name = "재해 일련번호", description = "재해일지_인원 Field", type = Description.TYPE.FIELD)
    String disasId;
    @Description(name = "인원번호", description = "재해일지_인원 Field", type = Description.TYPE.FIELD)
    Integer disasVicSeq;
    @Description(name = "직종", description = "재해일지_인원 Field", type = Description.TYPE.FIELD)
    String diasaVicOccu;
    @Description(name = "성명", description = "재해일지_인원 Field", type = Description.TYPE.FIELD)
    String diasaVicNm;
    @Description(name = "삭제여부", description = "재해일지_인원 Field", type = Description.TYPE.FIELD)
    String dltYn;
    @Description(name = "등록자ID", description = "재해일지_인원 Field", type = Description.TYPE.FIELD)
    String rgstrId;
    @Description(name = "등록일", description = "재해일지_인원 Field", type = Description.TYPE.FIELD)
    LocalDateTime rgstDt;
    @Description(name = "수정자ID", description = "재해일지_인원 Field", type = Description.TYPE.FIELD)
    String chgId;
    @Description(name = "수정일", description = "재해일지_인원 Field", type = Description.TYPE.FIELD)
    LocalDateTime chgDt;
    @Description(name = "삭제자ID", description = "재해일지_인원 Field", type = Description.TYPE.FIELD)
    String dltId;
    @Description(name = "삭제일", description = "재해일지_인원 Field", type = Description.TYPE.FIELD)
    LocalDateTime dltDt;
}

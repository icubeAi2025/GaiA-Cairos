package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.document;

import com.fasterxml.jackson.annotation.JsonFormat;
import kr.co.ideait.platform.gaiacairos.core.config.GlobalMapperConfig;
import lombok.Data;
import org.apache.ibatis.type.Alias;
import org.mapstruct.Mapper;

import java.time.LocalDateTime;

@Mapper(config = GlobalMapperConfig.class)
@Alias("cbgnHtmlFormDto")
@Data
public class CbgnHtmlFormDto {
	Integer formNo;
	String formNm;
	String formType;
	String dcForm;
	String dltYn;
	String rgstrId;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	LocalDateTime rgstDt;
	String chgId;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	LocalDateTime chgDt;
	String dltId;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	LocalDateTime dltDt;
}

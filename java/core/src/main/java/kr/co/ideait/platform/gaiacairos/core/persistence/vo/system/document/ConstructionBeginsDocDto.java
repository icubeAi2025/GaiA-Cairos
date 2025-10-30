package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.document;

import com.fasterxml.jackson.annotation.JsonFormat;
import kr.co.ideait.platform.gaiacairos.core.config.GlobalMapperConfig;
import lombok.Data;
import org.apache.ibatis.type.Alias;
import org.mapstruct.Mapper;

import java.time.LocalDateTime;

@Mapper(config = GlobalMapperConfig.class)
@Alias("constructionBeginsDocDto")
@Data
public class ConstructionBeginsDocDto {
	Integer cbgnNo;
	String cbgnNm;
	String cbgnPath;
	Integer upCbgnNo;
	String naviType;
	Integer cbgnLevel;
	String docYn;
	String cbgnDocType;
	String cbgnDocForm;
	int dsplyOrdr;
	String orgnlDocNm;
	String orgnlDocDiskNm;
	String orgnlDocDiskPath;
	String pdfDocNm;
	String pdfDocDiskNm;
	String pdfDocDiskPath;
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

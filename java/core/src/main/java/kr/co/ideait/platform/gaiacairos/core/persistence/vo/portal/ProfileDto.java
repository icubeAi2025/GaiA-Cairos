package kr.co.ideait.platform.gaiacairos.core.persistence.vo.portal;

import com.fasterxml.jackson.annotation.JsonFormat;
import kr.co.ideait.platform.gaiacairos.core.config.GlobalMapperConfig;
import lombok.Data;
import org.apache.ibatis.type.Alias;
import org.mapstruct.Mapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Mapper(config = GlobalMapperConfig.class)
public interface ProfileDto {
	
	@Data
	@Alias("profile")
	class Profile{
	    String usrId;
	    Integer fileNo;
	    String fileOrgNm;
	    String fileDiskNm;
	    String fileDiskPath;
	    BigDecimal fileSize;
	    String stampYn;
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
}
